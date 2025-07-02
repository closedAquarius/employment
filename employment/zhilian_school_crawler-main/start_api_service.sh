#!/bin/bash

# 智联爬虫API服务启动脚本

# 切换到脚本所在目录
cd "$(dirname "$0")"

# 激活虚拟环境
source venv/bin/activate

# 启动API服务
echo "Starting Zhilian Spider API service on port 8000..."
python run_api.py

# 如果服务意外退出，自动退出虚拟环境
deactivate 