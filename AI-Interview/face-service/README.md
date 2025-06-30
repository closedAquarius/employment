# 人脸识别服务 (Face Recognition Service)

这是AI-Interview系统的人脸识别服务组件，提供人脸注册和验证功能。

## 功能

- 人脸注册：将用户的人脸图像保存到系统中
- 人脸验证：验证用户的人脸与已注册的人脸是否匹配

## 安装

1. 创建虚拟环境：
```bash
python3 -m venv venv
source venv/bin/activate  # 在Linux/Mac上
```

2. 安装依赖：
```bash
pip install -r requirements.txt
```

3. 安装CMake（如果尚未安装）：
```bash
# 在Mac上
brew install cmake

# 在Ubuntu上
sudo apt-get install cmake

# 在Windows上
# 从cmake.org下载并安装
```

## 运行服务

```bash
python app.py
```

服务将在 http://localhost:5001 上运行。

## API接口

### 健康检查

- **URL**: `/health`
- **方法**: `GET`
- **返回示例**:
```json
{
  "status": "UP",
  "message": "Face recognition service is running"
}
```

### 注册人脸

- **URL**: `/register-face`
- **方法**: `POST`
- **请求体**:
```json
{
  "userId": "用户ID",
  "image": "Base64编码的图像数据"
}
```
- **返回示例**:
```json
{
  "status": "0",
  "message": "Face saved for user 用户ID"
}
```

### 验证人脸

- **URL**: `/verify-face`
- **方法**: `POST`
- **请求体**:
```json
{
  "userId": "用户ID",
  "image": "Base64编码的图像数据"
}
```
- **返回示例（成功）**:
```json
{
  "status": "0",
  "message": "Verification successful: The user is 用户ID",
  "userid": "用户ID"
}
```
- **返回示例（失败）**:
```json
{
  "status": "1",
  "message": "Verification failed: The user is not recognized"
}
```

## 与AI-Interview系统集成

1. 确保人脸识别服务在端口5001上运行
2. 在AI-Interview前端配置中设置`flaskBaseUrl=http://localhost:5001`
3. 重启AI-Interview前端服务

## 故障排除

如果遇到端口占用问题，可以在`app.py`中修改端口号，并相应地更新前端配置。 