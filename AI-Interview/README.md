# 🤖 AI Interviewer - 下一代智能面试官系统

[English](https://github.com/xgwangdl/AI-Interview/blob/master/docs/README.md) | [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://makeapullrequest.com)
[![GitHub Stars](https://img.shields.io/github/stars/yourname/ai-interviewer?style=social)](https://github.com/yourname/ai-interviewer)

**首个支持全流程技术面试的开源AI系统** | **RESTful API设计** | **代码实操评估** | **多模态行为分析**

<p align="center">
  <img src="docs/demo.gif" alt="Demo" width="800">
</p>

## 🌟 为什么选择AI Interviewer？

### 开发者痛点
- 😰 技术面试缺乏真实场景练习
- 📚 传统刷题无法培养沟通表达能力
- ⏳ 人工模拟面试成本高昂

### 我们的优势
✅ **轻量级架构** - 基于REST API，易于集成  
✅ **深度技术评估** - AST解析+LLM代码评审双引擎  
✅ **智能进化系统** - 每周自动更新面试题库  
✅ **多模态分析** - 语音/代码/表情多维度评估

## 🚀 核心功能速览

| 功能模块         | 技术亮点                          | 应用场景                   |
|------------------|-----------------------------------|---------------------------|
| 智能问答引擎     | GPT-4 + 本地知识库混合推理        | 技术概念考察               |
| 代码实操评估     | JavaParser + 自定义规则引擎       | 算法题/系统设计题实战      |
| 语音交互系统     | REST API + 异步任务队列           | 模拟技术沟通场景           |
| 行为分析仪表盘   | OpenCV情绪识别 + 代码热力图       | 面试表现多维可视化         |

## 🛠️ 核心技术栈
```bash
智能引擎: Spring Boot + QWen-Max

语音识别: Vosk + FFmpeg

语音合成: Sambert

代码分析: JavaParser + ANTLR

Rag处理: Postgre Vector

数据库: Postgresql

Python 的实时通信库: FastRTC
```

##🗣️ 项目迭代视频
<tr>
<td width="50%">
<h5>第五次迭代<h5>
<p>多风格面试官人格矩阵</p>
<p align="center">
  <video src="https://github.com/user-attachments/assets/c81d6f3f-51e4-4d88-bfab-11beb17ae662" width="450" controls></video>
</p>
</td>
<td width="50%">
<h5>第四次迭代<h5>
<p>新增自动生成简历和外语口语会话练习</p>
<p align="center">
  <video src="https://github.com/user-attachments/assets/52a52a13-e21c-4b89-98b4-da8c0ef91234" width="450" controls></video>
</p>
</td>
<td width="50%">
<h5>第三次迭代</h5>
<p>新增程序题和人脸识别</p>
<p align="center">
  <video src="https://github.com/user-attachments/assets/16e440a9-9c97-4cb1-a36e-639f7ad22bc4" width="450" controls></video>
</p>
</td>
</tr>
<tr>
<td width="50%">
<h5>第二次迭代</h5>
<p>增加口语面试</p>
[观看视频](https://github.com/xgwangdl/AI-Interview/blob/master/docs/step2.mp4)
</td>
<td width="50%">
<h5>第一次迭代</h5>
<p>项目搭建</p>
[观看视频](https://github.com/xgwangdl/AI-Interview/blob/master/docs/step1.mp4)
</td>
</tr>

## 🚩 项目展示
1.登录界面
![image](https://github.com/user-attachments/assets/84f996dc-e97c-43a0-a39d-7f21f87d9824)
2.程序和笔试面试界面
![image](https://github.com/user-attachments/assets/0dc0f4af-2ac7-48f9-9f4a-40d071b26de3)
![image](https://github.com/user-attachments/assets/292710b6-ee46-440b-a006-0a393d5522ce)

3.面试界面
人脸识别
![image](https://github.com/user-attachments/assets/d5ba9d39-c33e-498f-912a-1e38809eb4f8)
面试官提问
![image](https://github.com/user-attachments/assets/f77a2319-4274-498e-9478-b567bbf26fda)
面试者回答
![image](https://github.com/user-attachments/assets/75cdd8a2-f7ba-4e9a-aa9c-8a298ec20133)
4.简历生成界面
![image](https://github.com/user-attachments/assets/e8e046ae-2661-42d2-bd04-4b608df61467)
5.口语练习界面
![image](https://github.com/user-attachments/assets/758037e4-5038-4516-ae1d-cd2187c9b936)
6.老板面试界面
![image](https://github.com/user-attachments/assets/012af5fb-3a13-40ce-b7dd-5878f759e44c)


## ⚡ 快速开始

5分钟开启你的第一次AI面试：

```bash
# 1. 克隆仓库
java部分
git clone https://github.com/xgwangdl/AI-Interview.git
python部分
git clone https://github.com/xgwangdl/AI-Interview-py.git

# 2. 启动服务
docker暂时没做，需要本地运行。
1.JDK17+
2.使用的数据库postgreSql，但是需要注意使用带有支持向量库的postgreSql。数据库的DDL和数据已经上传
3.申请阿里千问大模型，有免费额度可以使用
3.另外那个Python项目（AI-Interview-py）也需要启动，里面包括人脸识别，Gemini和Azure Openai的服务启动
4.口语练习需要谷歌的Gemini大模型申请虽然有免费额度但需要科学上网
5.老板面试需要去微软Azure申请Openai当然也可以免费试用，最主要不需要科学上网也能使用

# 3. 访问API文档
open http://localhost:8080/swagger-ui.html

# 项目名称
作者: 大连光哥  
技术栈: JAVA、AI 、React 
邮箱: xgwangdl@163.com
```

📜 开源协议
本项目采用 Apache License 2.0，您可自由地：

修改并私有化部署 ✅

用于商业产品 ✅

保留原始版权声明 ⚠️

🙌 致谢
特别感谢这些优秀开源项目：

Spring Boot - REST API核心框架

QWen-max - 阿里千问大模型

Spring-Ai-Alibaba - 快速开发生成式 AI 应用

⭐ 如果这个项目对您有帮助，请点击右上角Star支持我们的开发！
📢 关注更新：点击Watch按钮获取最新功能通知

## 🌱 成长轨迹  
[![Star History Chart](https://api.star-history.com/svg?repos=xgwangdl/AI-Interview)](https://star-history.com/#xgwangdl/AI-Interview)  
*感谢每一位Star支持者！*
