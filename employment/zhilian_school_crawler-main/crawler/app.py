import logging
import json
import time
from flask import Flask, jsonify, request, send_from_directory
from threading import Thread
import sys
import traceback
import os

from salary_model import SalaryPredictor
from match import Match  # 引入你的 Match 类
import crawler  # 引入爬虫脚本的主函数

# from crawler.salary_model import SalaryPredictor
# from match import Match  # 引入你的 Match 类
from crawler import run_spider  # 引入爬虫脚本的主函数

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger("salary-api")

app = Flask(
    __name__
)
logger = logging.getLogger("flask")
predictor = SalaryPredictor()


# 添加一个简单的健康检查端点
@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "ok", "model_loaded": predictor.model is not None}), 200

# 添加根路径的健康检查
@app.route('/', methods=['GET'])
def root():
    return jsonify({"status": "ok", "message": "Salary prediction API is running"}), 200

# 添加静态文件服务
@app.route('/uploads/<path:filename>')
def serve_uploads(filename):
    """提供上传文件的访问"""
    uploads_dir = os.path.abspath("../../../employment/backend/uploads")
    print(f"尝试访问: {uploads_dir}/{filename}")
    return send_from_directory(uploads_dir, filename)

@app.route('/predictions/<path:filename>')
def serve_predictions(filename):
    """直接提供预测图表的访问"""
    predictions_dir = os.path.abspath("../../../employment/backend/uploads/predictions")
    print(f"尝试访问预测图表: {predictions_dir}/{filename}")
    try:
        response = send_from_directory(predictions_dir, filename)
        # 手动添加CORS头部
        response.headers['Access-Control-Allow-Origin'] = '*'
        response.headers['Access-Control-Allow-Methods'] = 'GET, OPTIONS'
        return response
    except Exception as e:
        print(f"访问预测图表出错: {str(e)}")
        return jsonify({"error": str(e)}), 404


# 确保在应用启动时加载模型
print("正在加载薪资预测模型...")
try:
    model_loaded = predictor.load_model()
    if model_loaded:
        print("薪资预测模型加载成功!")
    else:
        print("警告: 薪资预测模型加载失败，将使用默认值")
except Exception as e:
    print(f"错误: 薪资预测模型加载异常: {str(e)}")
    traceback.print_exc()


@app.route('/predict_salary', methods=['POST'])
def predict_salary():
    """
    API endpoint to predict salary based on the input data.
    Expected input: JSON with features like 'province', 'city', 'area', 'education', 'experience', 'skills', etc.
    """
    try:
        data = request.json
        print(f"接收到薪资预测请求: {data}")

        # Ensure required fields are provided
        required_fields = ['province', 'city', 'area', 'education', 'experience', 'skills']
        for field in required_fields:
            if field not in data:
                return jsonify({"error": f"Missing field: {field}"}), 400

        # Extract features from the input data
        input_data = {
            'province': data.get('province'),
            'city': data.get('city'),
            'area': data.get('area', ''),  # 确保area字段不为None
            'education': data.get('education'),
            'experience': data.get('experience'),
            'skill': ' '.join(data.get('skills', [])),  # Join list of skills into a string
            'name': data.get('job_title', '未知'),
            'scale': data.get('company_scale', '未知')
        }

        # 检查模型是否已加载
        if predictor.model is None:
            print("模型未加载，尝试重新加载...")
            if not predictor.load_model():
                raise Exception("无法加载模型")

        # Predict the salary using the SalaryPredictor
        prediction = predictor.predict_salary(input_data)

        # 检查是否有错误
        if 'error' in prediction:
            print(f"薪资预测出错: {prediction['error']}")
            # 仍然返回结果，但包含错误信息

        return jsonify(prediction), 200
    except Exception as e:
        print(f"薪资预测API出错: {str(e)}")
        traceback.print_exc()
        return jsonify({
            'error': f"薪资预测失败: {str(e)}",
            'min_salary': 5000,
            'max_salary': 8000,
            'avg_salary': 6500,
            'confidence': 0.5,
            'factors': [{'name': '服务器错误', 'impact': 100}]
        }), 500

@app.route('/job_match', methods=['POST'])
def job_match():
    try:
        # 从请求中获取参数
        data = request.json
        lan = data.get('lan')
        city = data.get('city')
        area = data.get('area', '')  # 确保area字段不为None
        salary = data.get('salary')
        education = data.get('education')
        experience = data.get('experience')
        skills = data.get('skills', [])
        priority = data.get('priority')

        print(f"接收到职位匹配请求: {data}")

        # 创建 Match 对象并调用 jobMach 方法
        match = Match()
        result = match.jobMach(lan, city, area, salary, education, experience, skills, priority)

        if result:
            return app.response_class(
                response=json.dumps(result, ensure_ascii=False),
                status=200,
                mimetype='application/json'
            )
            #return jsonify(result), 200
        else:
            return jsonify({"error": "No match found"}), 404
    except Exception as e:
        print(f"职位匹配出错: {str(e)}")
        traceback.print_exc()
        return jsonify({"error": f"职位匹配失败: {str(e)}"}), 500

# 修改 start_crawler 路由
@app.route('/start_crawler', methods=['POST'])
def start_crawler():
    def run_crawler_in_thread(auto):
        # 将 auto 包装成字典传递给 run_spider
        run_spider({"auto": auto})

    # 从请求中获取参数（auto模式）
    auto = request.json.get('auto', False)
    print(auto)

    # 在新线程中启动爬虫，避免阻塞 Flask 进程
    crawler_thread = Thread(target=run_crawler_in_thread, args=(auto,))
    crawler_thread.start()

    return jsonify({"status": "success", "message": "Crawler started in the background."}), 200
# 启动 Flask 服务
if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5000)
