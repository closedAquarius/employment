import logging
import json
import time
from flask import Flask, jsonify, request
from threading import Thread

from crawler.salary_model import SalaryPredictor
from match import Match  # 引入你的 Match 类
from crawler import run_spider  # 引入爬虫脚本的主函数
app = Flask(
    __name__
)
logger = logging.getLogger("flask")
predictor = SalaryPredictor()


@app.route('/predict_salary', methods=['POST'])
def predict_salary():
    """
    API endpoint to predict salary based on the input data.
    Expected input: JSON with features like 'province', 'city', 'area', 'education', 'experience', 'skills', etc.
    """
    data = request.json

    # Ensure required fields are provided
    required_fields = ['province', 'city', 'area', 'education', 'experience', 'skills']
    for field in required_fields:
        if field not in data:
            return jsonify({"error": f"Missing field: {field}"}), 400

    # Extract features from the input data
    input_data = {
        'province': data.get('province'),
        'city': data.get('city'),
        'area': data.get('area'),
        'education': data.get('education'),
        'experience': data.get('experience'),
        'skill': ' '.join(data.get('skills', [])),  # Join list of skills into a string
        'name': data.get('job_title', '未知'),
        'scale': data.get('company_scale', '未知')
    }

    # Predict the salary using the SalaryPredictor
    prediction = predictor.predict_salary(input_data)

    return jsonify(prediction), 200

@app.route('/job_match', methods=['POST'])
def job_match():
    # 从请求中获取参数
    data = request.json
    lan = data.get('lan')
    city = data.get('city')
    area = data.get('area')
    salary = data.get('salary')
    education = data.get('education')
    experience = data.get('experience')
    skills = data.get('skills', [])
    priority = data.get('priority')

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

@app.route('/start_crawler', methods=['POST'])
def start_crawler():
    def run_crawler_in_thread():
        # 从请求中获取参数（auto模式）
        auto = request.json.get('auto', False)
        # 运行爬虫
        run_spider(auto)

    # 在新线程中启动爬虫，避免阻塞 Flask 进程
    crawler_thread = Thread(target=run_crawler_in_thread)
    crawler_thread.start()

    return jsonify({"status": "success", "message": "Crawler started in the background."}), 200

# 启动 Flask 服务
if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=5000)
