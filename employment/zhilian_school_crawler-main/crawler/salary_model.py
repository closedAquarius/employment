import pymysql
import pandas as pd
import numpy as np
import lightgbm as lgb
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split, GridSearchCV
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score
import pickle
import os
import warnings
import re

from collections import Counter
import time
import traceback

warnings.filterwarnings('ignore')

# 配置matplotlib中文字体支持
plt.rcParams['font.sans-serif'] = ['SimHei', 'Arial Unicode MS', 'Microsoft YaHei', 'SimSun']  # 中文字体设置
plt.rcParams['axes.unicode_minus'] = False  # 解决保存图像负号'-'显示为方块的问题


# 数据库连接类
class MySql(object):
    # 建立数据库链接
    def __init__(self):
        try:
            self.connect = pymysql.connect(host="127.0.0.1",
                                           port=3306,
                                           user="root",
                                           password="sh20051015",
                                           database="spiderdatabase",
                                           charset="utf8")
            self.cursor = self.connect.cursor(cursor=pymysql.cursors.DictCursor)
        except Exception as e:
            print(f"数据库连接错误: {e}")
            raise

    # 关闭链接
    def __del__(self):
        if hasattr(self, 'connect') and self.connect:
            try:
                self.connect.close()
            except:
                pass
        if hasattr(self, 'cursor') and self.cursor:
            try:
                self.cursor.close()
            except:
                pass

    # 手动关闭数据库链接
    def close(self):
        if hasattr(self, 'connect') and self.connect:
            try:
                self.connect.close()
            except:
                pass
        if hasattr(self, 'cursor') and self.cursor:
            try:
                self.cursor.close()
            except:
                pass

    # 获取数据
    def get_salary_data(self, limit=None):
        """从数据库中获取薪资数据"""
        sql = """
              SELECT name, \
                     province, \
                     city, \
                     area, \
                     detail, \
                     company, \
                     scale, \
                     min_salary, \
                     max_salary, \
                     avg_salary, \
                     education, \
                     experience, \
                     label, \
                     skill, \
                     welfare
              FROM c_data
              WHERE min_salary > 0 \
                AND max_salary > 0 \
                AND avg_salary > 0 \
              """

        # 如果指定了数据量限制，添加LIMIT子句
        if limit:
            sql += f" LIMIT {limit}"

        self.cursor.execute(sql)
        results = self.cursor.fetchall()
        return pd.DataFrame(results)


# 数据预处理和特征工程
class SalaryDataProcessor:
    def __init__(self):
        self.label_encoders = {}
        self.feature_importances = None
        self.top_skills = []  # 初始化为空列表而不是None
        self.skill_pattern = None

    def _encode_categorical(self, df, column):
        """对类别型数据进行编码"""
        if column not in self.label_encoders:
            self.label_encoders[column] = LabelEncoder()
            df[column] = self.label_encoders[column].fit_transform(df[column].fillna('未知'))
        else:
            # 处理测试集或预测时可能出现的新类别
            df[column] = df[column].fillna('未知')
            unique_values = set(df[column].unique())
            known_values = set(self.label_encoders[column].classes_)
            new_values = unique_values - known_values

            if new_values:
                for val in new_values:
                    df[column] = df[column].replace(val, '未知')

            df[column] = self.label_encoders[column].transform(df[column])

        return df

    def _extract_experience_years(self, exp_str):
        """提取工作经验年限"""
        if pd.isna(exp_str) or exp_str == '经验不限' or exp_str == '不限':
            return 0
        elif exp_str == '在校/应届':
            return 0
        elif exp_str == '1年以内':
            return 0.5
        elif '-' in exp_str:
            years = exp_str.split('-')
            try:
                min_year = int(years[0])
                max_year = int(re.sub(r'[^\d]', '', years[1]))
                return (min_year + max_year) / 2
            except:
                return 2  # 默认值
        elif '年以上' in exp_str:
            try:
                return int(re.sub(r'[^\d]', '', exp_str))
            except:
                return 5  # 默认值
        else:
            return 1  # 默认值

    def _extract_scale_value(self, scale_str):
        """将公司规模转换为数值"""
        if pd.isna(scale_str):
            return 0
        elif scale_str == '0-20人':
            return 10
        elif scale_str == '20-99人':
            return 50
        elif scale_str == '100-499人':
            return 300
        elif scale_str == '500-999人':
            return 750
        elif scale_str == '1000-9999人':
            return 5000
        elif scale_str == '10000人以上':
            return 10000
        else:
            return 0

    def _extract_top_skills(self, df, top_n=50):
        """提取出现频率最高的技能"""
        all_skills = []
        for skills in df['skill'].dropna():
            all_skills.extend([skill.strip() for skill in skills.split() if skill.strip()])

        skill_counter = Counter(all_skills)
        self.top_skills = [skill for skill, _ in skill_counter.most_common(top_n)]
        self.skill_pattern = '|'.join(self.top_skills)

        return self.top_skills

    def _create_skill_features(self, df):
        """为每个常见技能创建二进制特征"""
        if not self.top_skills:
            self.top_skills = []  # 确保top_skills至少是一个空列表而不是None
            return df

        for skill in self.top_skills:
            df[f'skill_{skill}'] = df['skill'].fillna('').apply(
                lambda x: 1 if skill in x else 0
            )

        return df

    def preprocess(self, df, is_training=True):
        """数据预处理主函数"""
        # 1. 删除无用特征
        if 'num' in df.columns:
            df = df.drop(['num'], axis=1)

        # 2. 检查并处理缺失值
        df = df.fillna({
            'province': '未知',
            'city': '未知',
            'area': '未知',
            'detail': '未知',
            'company': '未知',
            'scale': '未知',
            'education': '未知',
            'experience': '经验不限',
            'label': '未知',
            'skill': '',
            'welfare': ''
        })

        # 3. 处理异常值
        # 薪资合理性检查
        if is_training and 'min_salary' in df.columns and 'max_salary' in df.columns:
            df = df[(df['min_salary'] >= 1000) & (df['max_salary'] <= 100000)]

        # 4. 特征工程
        # 将工作经验转换为数值
        df['exp_years'] = df['experience'].apply(self._extract_experience_years)

        # 将公司规模转换为数值
        df['scale_value'] = df['scale'].apply(self._extract_scale_value)

        # 5. 类别特征编码
        categorical_features = ['province', 'city', 'education', 'scale']
        for col in categorical_features:
            df = self._encode_categorical(df, col)

        # 6. 处理技能特征
        if is_training:
            self._extract_top_skills(df)

        df = self._create_skill_features(df)

        # 7. 选择最终特征
        features = [
            'province', 'city', 'education', 'scale', 'exp_years', 'scale_value'
        ]

        # 确保top_skills不为None
        if self.top_skills:
            features += [f'skill_{skill}' for skill in self.top_skills if f'skill_{skill}' in df.columns]

        return df[features], df['avg_salary'] if 'avg_salary' in df.columns else None

    def save_encoders(self, path='model'):
        """保存标签编码器和技能列表"""
        if not os.path.exists(path):
            os.makedirs(path)

        with open(os.path.join(path, 'label_encoders.pkl'), 'wb') as f:
            pickle.dump(self.label_encoders, f)

        with open(os.path.join(path, 'top_skills.pkl'), 'wb') as f:
            pickle.dump(self.top_skills, f)

    def load_encoders(self, path='model'):
        """加载标签编码器和技能列表"""
        try:
            with open(os.path.join(path, 'label_encoders.pkl'), 'rb') as f:
                self.label_encoders = pickle.load(f)

            with open(os.path.join(path, 'top_skills.pkl'), 'rb') as f:
                self.top_skills = pickle.load(f)
                if self.top_skills is None:
                    self.top_skills = []  # 如果加载的top_skills为None，设置为空列表

            if self.top_skills:
                self.skill_pattern = '|'.join(self.top_skills)

            return True
        except Exception as e:
            print(f"加载编码器失败: {str(e)}")
            self.top_skills = []  # 确保在加载失败时top_skills为空列表
            return False


# 薪资预测模型
class SalaryPredictor:
    def __init__(self):
        self.model = None
        self.processor = SalaryDataProcessor()

    def train(self, df):
        """训练模型"""
        print("开始数据预处理...")
        X, y = self.processor.preprocess(df)

        print(f"预处理后特征数量: {X.shape[1]}")
        print(f"训练样本数量: {X.shape[0]}")

        # 划分训练集和测试集
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42
        )

        print("开始训练模型...")
        # 创建LightGBM数据集
        train_data = lgb.Dataset(X_train, label=y_train)
        test_data = lgb.Dataset(X_test, label=y_test, reference=train_data)

        # 设置参数
        params = {
            'boosting_type': 'gbdt',
            'objective': 'regression',
            'metric': 'rmse',
            'learning_rate': 0.05,
            'num_leaves': 31,
            'min_data_in_leaf': 20,
            'feature_fraction': 0.8,
            'bagging_fraction': 0.8,
            'bagging_freq': 5,
            'verbose': -1
        }

        # 尽可能兼容不同版本的LightGBM API
        try:
            print("尝试使用新版API进行训练...")
            if hasattr(lgb, 'early_stopping') and callable(lgb.early_stopping):
                # 新版本LightGBM API
                callbacks = [lgb.early_stopping(stopping_rounds=50)]
                self.model = lgb.train(
                    params,
                    train_data,
                    num_boost_round=1000,
                    valid_sets=[test_data],
                    callbacks=callbacks
                )
            else:
                # 中间版本API
                print("尝试使用中间版本API...")
                try:
                    self.model = lgb.train(
                        params,
                        train_data,
                        num_boost_round=1000,
                        valid_sets=[test_data],
                        early_stopping_rounds=50
                    )
                except TypeError as e:
                    if "unexpected keyword argument" in str(e) and "early_stopping_rounds" in str(e):
                        print("API不支持early_stopping_rounds，使用基本版本...")
                        self.model = lgb.train(
                            params,
                            train_data,
                            num_boost_round=1000,
                            valid_sets=[test_data]
                        )
                    else:
                        raise e
        except Exception as e:
            print(f"标准训练方式失败，尝试基本训练方法: {str(e)}")
            # 最基本的训练方式，几乎所有版本都支持
            try:
                # 移除可能导致问题的参数
                basic_params = params.copy()
                if 'verbose' in basic_params:
                    basic_params['verbose'] = 0

                self.model = lgb.train(
                    basic_params,
                    train_data,
                    num_boost_round=500,
                    valid_sets=[test_data]
                )
                print("基本训练方法完成")
            except Exception as final_e:
                print(f"所有训练方法均失败: {str(final_e)}")
                print("尝试最简单的训练方式...")
                self.model = lgb.train(
                    {'objective': 'regression'},
                    train_data,
                    num_boost_round=100
                )

        print("模型训练完成，开始评估")

        # 评估模型性能
        y_pred = self.model.predict(X_test)
        rmse = np.sqrt(mean_squared_error(y_test, y_pred))
        mae = mean_absolute_error(y_test, y_pred)
        r2 = r2_score(y_test, y_pred)

        print(f"模型评估结果:")
        print(f"RMSE: {rmse:.2f}")
        print(f"MAE: {mae:.2f}")
        print(f"R²: {r2:.4f}")

        # 保存特征重要性
        self.processor.feature_importances = pd.DataFrame({
            'feature': X.columns,
            'importance': self.model.feature_importance()
        }).sort_values('importance', ascending=False)

        print("特征重要性排名:")
        print(self.processor.feature_importances.head(10))

        return {'rmse': rmse, 'mae': mae, 'r2': r2}

    def save_model(self, path='model'):
        """保存模型"""
        if not os.path.exists(path):
            os.makedirs(path)

        # 保存模型
        model_path = os.path.join(path, 'salary_model.txt')
        self.model.save_model(model_path)

        # 保存编码器和处理器
        self.processor.save_encoders(path)

        print(f"模型已保存至: {path}")

    def load_model(self, path='model'):
        """加载模型"""
        try:
            # 加载编码器和处理器
            self.processor.load_encoders(path)

            # 加载模型
            model_path = os.path.join(path, 'salary_model.txt')
            self.model = lgb.Booster(model_file=model_path)

            print("模型加载成功")
            return True
        except Exception as e:
            print(f"模型加载失败: {str(e)}")
            return False

    def predict_salary(self, input_data):
        """
        预测薪资

        input_data: 包含用户输入特征的字典
        """
        try:
            # 转换为DataFrame
            input_df = pd.DataFrame([input_data])

            # 预处理
            X, _ = self.processor.preprocess(input_df, is_training=False)

            # 预测
            avg_salary = self.model.predict(X)[0]

            # 计算最低和最高薪资 (±15%)
            min_salary = avg_salary * 0.85
            max_salary = avg_salary * 1.15

            # 计算个人匹配度（简单逻辑）
            # 根据特征重要性计算置信度
            confidence = 0.85  # 默认值

            # 生成预测可视化图表
            chart_path = self.visualize_prediction(input_data, min_salary, avg_salary, max_salary)

            return {
                'min_salary': int(min_salary),
                'max_salary': int(max_salary),
                'avg_salary': int(avg_salary),
                'confidence': confidence,
                'chart_path': chart_path,
                'factors': [
                    {'name': '地区', 'impact': 30},
                    {'name': '学历', 'impact': 25},
                    {'name': '经验', 'impact': 20},
                    {'name': '技能匹配度', 'impact': 15},
                    {'name': '公司规模', 'impact': 10}
                ]
            }
        except Exception as e:
            print(f"薪资预测失败: {str(e)}")
            # 返回默认值
            return {
                'min_salary': 5000,
                'max_salary': 8000,
                'avg_salary': 6500,
                'confidence': 0.5,
                'chart_path': '',
                'factors': [
                    {'name': '数据不足', 'impact': 100}
                ],
                'error': str(e)
            }

    def visualize_prediction(self, input_data, min_salary, avg_salary, max_salary):
        """可视化预测结果并保存图表"""
        try:
            # 创建目录（如果不存在）
            upload_dir = "../../../employment/backend/uploads/predictions"
            os.makedirs(upload_dir, exist_ok=True)

            # 生成唯一文件名
            filename = f"salary_prediction_{int(time.time())}.png"
            filepath = os.path.join(upload_dir, filename)

            # 使用非交互式后端，避免NSWindow错误
            import matplotlib
            matplotlib.use('Agg')  # 确保使用非交互式后端

            # 创建图表
            plt.figure(figsize=(10, 6))

            # 绘制薪资范围
            x = ['最低薪资', '平均薪资', '最高薪资']
            y = [min_salary, avg_salary, max_salary]

            # 使用简单的条形图而不是seaborn，减少依赖
            plt.bar(x, y, color=['#5470c6', '#91cc75', '#fac858'])

            # 设置标题和标签
            plt.title(f"{input_data.get('name', '职位')}薪资预测 - {input_data.get('city', '未知')}地区")
            plt.ylabel('薪资 (元/月)')

            # 在柱状图上显示具体数值
            for i, v in enumerate(y):
                plt.text(i, v + 500, f"{int(v)}", ha='center')

            plt.tight_layout()

            # 保存图表
            plt.savefig(filepath, dpi=300, bbox_inches='tight')
            plt.close('all')  # 确保关闭所有图形

            print(f"图表已保存: {filepath}")
            # 返回相对路径，供前端访问
            return f"predictions/{filename}"
        except Exception as e:
            print(f"图表生成失败: {str(e)}")
            traceback.print_exc()
            return ""  # 出错时返回空字符串


# 数据可视化
def visualize_data(df, save_dir='../../../employment/backend/uploads/plots'):
    """生成数据可视化图表"""
    print("生成数据可视化...")

    if not os.path.exists(save_dir):
        os.makedirs(save_dir)

    # 1. 薪资分布直方图
    plt.figure(figsize=(10, 6))
    sns.histplot(df['avg_salary'], bins=30, kde=True)
    plt.title('平均薪资分布')
    plt.xlabel('薪资 (元/月)')
    plt.ylabel('频数')
    plt.savefig(os.path.join(save_dir, 'salary_distribution.png'), dpi=300, bbox_inches='tight')
    plt.close()

    # 2. 不同学历的薪资箱线图
    plt.figure(figsize=(12, 6))
    sns.boxplot(x='education', y='avg_salary', data=df)
    plt.title('不同学历的薪资分布')
    plt.xlabel('学历')
    plt.ylabel('平均薪资 (元/月)')
    plt.xticks(rotation=45)
    plt.tight_layout()  # 自动调整布局
    plt.savefig(os.path.join(save_dir, 'salary_by_education.png'), dpi=300, bbox_inches='tight')
    plt.close()

    # 3. 不同工作经验的薪资箱线图
    plt.figure(figsize=(12, 6))
    sns.boxplot(x='experience', y='avg_salary', data=df)
    plt.title('不同工作经验的薪资分布')
    plt.xlabel('工作经验')
    plt.ylabel('平均薪资 (元/月)')
    plt.xticks(rotation=45)
    plt.tight_layout()  # 自动调整布局
    plt.savefig(os.path.join(save_dir, 'salary_by_experience.png'), dpi=300, bbox_inches='tight')
    plt.close()

    # 4. 不同公司规模的薪资箱线图
    plt.figure(figsize=(12, 6))
    sns.boxplot(x='scale', y='avg_salary', data=df)
    plt.title('不同公司规模的薪资分布')
    plt.xlabel('公司规模')
    plt.ylabel('平均薪资 (元/月)')
    plt.xticks(rotation=45)
    plt.tight_layout()  # 自动调整布局
    plt.savefig(os.path.join(save_dir, 'salary_by_scale.png'), dpi=300, bbox_inches='tight')
    plt.close()

    # 5. 不同城市的薪资条形图 (Top 10)
    city_salary = df.groupby('city')['avg_salary'].mean().sort_values(ascending=False).head(10)
    plt.figure(figsize=(12, 6))
    sns.barplot(x=city_salary.index, y=city_salary.values)
    plt.title('不同城市的平均薪资 (Top 10)')
    plt.xlabel('城市')
    plt.ylabel('平均薪资 (元/月)')
    plt.xticks(rotation=45)
    plt.tight_layout()  # 自动调整布局
    plt.savefig(os.path.join(save_dir, 'salary_by_city.png'), dpi=300, bbox_inches='tight')
    plt.close()

    print(f"可视化图表已保存至 {save_dir} 目录")


def main():
    """主函数"""
    print("开始薪资预测模型训练流程")

    # 1. 连接数据库并获取数据
    print("从数据库获取数据...")
    db = MySql()
    data_limit = None  # 设置要获取的数据量，如果需要所有数据，可以设置为None
    df = db.get_salary_data(limit=data_limit)
    db.close()

    print(f"获取到 {len(df)} 条数据记录")

    # 2. 数据基本信息
    print("\n数据基本信息:")
    print(f"数据规模: {df.shape[0]} 行, {df.shape[1]} 列")
    print("数据类型:")
    print(df.dtypes)
    print("缺失值统计:")
    print(df.isnull().sum())

    # 3. 数据可视化
    visualize_data(df)

    # 4. 创建模型并训练
    predictor = SalaryPredictor()
    metrics = predictor.train(df)

    # 5. 保存模型
    predictor.save_model()

    print("训练完成!")

    # 6. 测试模型预测
    test_input = {
        'province': '北京',
        'city': '北京',
        'area': '朝阳区',
        'scale': '1000-9999人',
        'name': 'Python开发工程师',
        'education': '本科',
        'experience': '3-5年',
        'skill': 'Python Django Flask 数据分析 爬虫'
    }

    # 重新加载模型以验证保存和加载功能
    predictor = SalaryPredictor()
    predictor.load_model()

    prediction = predictor.predict_salary(test_input)
    print("\n测试预测结果:")
    print(f"预估薪资区间: {prediction['min_salary']} ~ {prediction['max_salary']} 元/月")
    print(f"平均薪资: {prediction['avg_salary']} 元/月")
    print(f"置信度: {prediction['confidence']}")


if __name__ == "__main__":
    main()