# 百度千问API密钥配置说明

## 问题说明

目前系统在使用百度千问API时遇到了认证错误：
```
AK(ALTAKF***) is not correct, please check! AK(`ALTAKF***`) 错误, 请检查！
```

这表明当前配置的API密钥无效。要解决此问题，需要获取有效的百度千问API密钥并正确配置。

## 获取API密钥步骤

1. 访问百度智能云千帆平台：[https://console.bce.baidu.com/qianfan/ais/console/applicationConsole/application](https://console.bce.baidu.com/qianfan/ais/console/applicationConsole/application)

2. 登录百度账号（如果没有，需要先注册）

3. 创建应用（如已有应用可跳过此步骤）：
   - 点击"创建应用"
   - 填写应用名称、应用描述
   - 选择需要使用的模型（如ERNIE-Bot-turbo-0922）
   - 完成创建

4. 获取API密钥：
   - 在应用列表中找到您创建的应用
   - 点击"查看密钥"
   - 复制API Key（AK）和Secret Key（SK）

## 配置API密钥

1. 打开项目中的`.env`文件：
```
/AI-Interview/face-service/AI-Interview-py/talk_to_gemini/.env
```

2. 修改文件内容如下（替换为您的实际密钥）：
```
# 百度千问API密钥配置
QIANFAN_AK=您的API Key
QIANFAN_SK=您的Secret Key
```

3. 保存文件，然后重启Python服务：
```bash
cd /AI-Interview/face-service/AI-Interview-py/talk_to_gemini
python app_qianwen.py
```

## 验证配置

配置完成后，访问 [http://localhost:8084/talkToAzure](http://localhost:8084/talkToAzure)，选择角色类型并点击"开始"按钮，测试语音对话功能是否正常工作。

如果一切正常，您应该能够通过麦克风与AI进行对话。

## 常见问题

1. **遇到"API Key无效"错误**：确保复制的密钥是完整的，没有多余的空格或换行符。

2. **没有听到AI响应**：检查浏览器是否允许了麦克风权限，以及音频输出设备是否正常工作。

3. **连接被拒绝**：确保Python服务正在运行，并且端口没有被其他应用占用。 