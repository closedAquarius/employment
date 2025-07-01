# Zhilian Spider

本项目是基于 Python 的智联校园招聘数据爬取，用于爬取特定条件下的不同专业的招聘信息。

## 使用说明
主要代码在crawler文件夹下
文件结果如下：

- crawler
  - data 爬取结果数据
  - persist 持久化数据备份
  - crawler.py 爬虫主程序
  - constansts.py 常量定义
  - preprocess_major_map.py 获取网站的所有专业与专业代码的映射关系，便于爬取
  - procecss_data.py 对于爬取中遇到的错误信息进行修正，比如跳过了某个专业的招聘信息等，需要二次爬取
- README.md 说明文档
- requirements.txt 依赖包
- job_class_map.json 职位类别映射关系
- run_api.py API服务启动脚本

## 与后端集成
本爬虫项目已与employment/backend后端项目集成，通过以下组件实现数据交互：

1. SpiderConfigurer.java - 爬虫配置类
2. JobImportController.java - 职位导入控制器
3. JobPosting.java - 职位发布模型
4. JobImportService.java - 职位导入服务
5. SpiderService.java - 爬虫服务接口

后端可以调用爬虫程序获取最新的招聘信息，并将数据存储到数据库中。

## 运行环境
- Python 3.7+
- 依赖包见requirements.txt

## 虚拟环境使用
项目使用Python虚拟环境隔离依赖：

```bash
# 激活虚拟环境
source venv/bin/activate

# 安装依赖
pip install -r requirements.txt

# 启动API服务
python run_api.py

# 退出虚拟环境
deactivate
```

## API服务
爬虫提供了REST API服务，默认运行在8000端口：

- POST /crawl - 启动爬虫
  - 请求体: `{"auto": true}` (auto模式) 或 `{"auto": false}` (手动模式)
  - 返回: 爬虫启动状态信息

## 许可证

MIT License
