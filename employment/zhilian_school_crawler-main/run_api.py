#!/usr/bin/env python3
"""
智联爬虫API服务启动脚本
"""
import os
import sys

# 添加当前目录到Python路径
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

# 修改导入方式
from crawler.crawler import app

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8000))
    app.run(host='0.0.0.0', port=port, debug=True) 