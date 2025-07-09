import os
import json
import asyncio
import base64
from typing import List, Literal, AsyncGenerator, Optional
from pathlib import Path
import numpy as np
from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse, HTMLResponse, JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import uvicorn
import quart
from werkzeug.utils import secure_filename
import tempfile
import random
import wave
import struct
import soundfile as sf
import io
import time
import requests
from dotenv import load_dotenv

# 尝试导入OpenAI SDK
try:
    import openai
    OPENAI_SDK_AVAILABLE = True
except ImportError:
    OPENAI_SDK_AVAILABLE = False
    print("警告: OpenAI SDK未安装，请运行 pip install openai")

# 添加文本转语音库
try:
    from gtts import gTTS
    GTTS_AVAILABLE = True
except ImportError:
    GTTS_AVAILABLE = False
    print("警告: gTTS库未安装，备用TTS将不可用")

try:
    import pyttsx3
    PYTTSX3_AVAILABLE = True
except ImportError:
    PYTTSX3_AVAILABLE = False
    print("警告: pyttsx3库未安装，本地TTS将不可用")

# 尝试导入百度语音合成SDK
try:
    from aip import AipSpeech
    BAIDU_SDK_AVAILABLE = True
except ImportError:
    BAIDU_SDK_AVAILABLE = False
    print("警告: 百度语音合成SDK未安装，将使用备用TTS")

# 检查环境变量中的API密钥
def check_api_keys():
    """检查环境变量中是否设置了必要的API密钥"""
    load_dotenv()
    
    # 检查千帆API密钥
    qianfan_ak = os.getenv("QIANFAN_AK") or os.getenv("QIANFAN_ACCESS_KEY")
    qianfan_sk = os.getenv("QIANFAN_SK") or os.getenv("QIANFAN_SECRET_KEY")
    # 检查百度语音合成API密钥
    baidu_app_id = os.getenv("BAIDU_APP_ID")
    baidu_api_key = os.getenv("BAIDU_API_KEY")
    baidu_secret_key = os.getenv("BAIDU_SECRET_KEY")
    
    if not (baidu_app_id and baidu_api_key and baidu_secret_key):
        print("警告: 百度语音合成API密钥未设置，将使用备用TTS")
    
    return {
        "qianfan_ak": qianfan_ak,
        "qianfan_sk": qianfan_sk,
        "baidu_app_id": baidu_app_id,
        "baidu_api_key": baidu_api_key,
        "baidu_secret_key": baidu_secret_key
    }

# 加载.env文件中的环境变量
try:
    # 尝试加载.env文件
    env_path = Path(__file__).parent / '.env'
    if env_path.exists():
        print(f"加载环境变量文件: {env_path}")
        load_dotenv(dotenv_path=env_path)
    else:
        print("警告: .env文件不存在")
    
    # 检查API密钥
    check_api_keys()
except ImportError:
    print("警告: python-dotenv 库未安装，无法从.env文件加载环境变量")

# 尝试导入语音识别库
try:
    import speech_recognition as sr
except ImportError:
    print("警告: speech_recognition 库未安装，语音识别功能将不可用")
    sr = None

# 尝试导入语音活动检测库
try:
    import webrtcvad
except ImportError:
    print("警告: webrtcvad 库未安装，语音活动检测功能将不可用")
    webrtcvad = None

# 自定义编码函数
def encode_audio(audio_data: np.ndarray) -> bytes:
    """将音频数据编码为字节流"""
    if audio_data.dtype != np.int16:
        # 确保数据类型正确
        scaled = np.round(audio_data * 32767).astype(np.int16)
    else:
        scaled = audio_data
    return bytes(scaled.tobytes())

# 初始化语音识别器
recognizer = sr.Recognizer() if sr else None
vad = webrtcvad.Vad(3) if webrtcvad else None  # 最高级别的语音活动检测

# 设置音频处理参数
frame_duration_ms = 30  # 30 毫秒帧长度

# 创建一个临时目录，用于存储音频文件
temp_audio_dir = os.path.join(tempfile.gettempdir(), "ai_interview_audio")
os.makedirs(temp_audio_dir, exist_ok=True)
print(f"临时音频目录: {temp_audio_dir}")

async def process_audio_data(audio_data, sample_rate=16000):
    """处理音频数据，识别语音内容"""
    if not sr or not recognizer:
        print("语音识别库未安装，返回默认文本")
        return "我是用户的发言内容，语音识别未启用"
    
    try:
        # 保存音频数据到临时WAV文件
        temp_file = os.path.join(temp_audio_dir, f"temp_audio_{random.randint(1000, 9999)}.wav")
        
        # 规范化音频数据
        if audio_data.dtype != np.int16:
            audio_data = np.int16(audio_data / np.max(np.abs(audio_data)) * 32767)
        
        # 写入WAV文件
        with wave.open(temp_file, 'wb') as wf:
            wf.setnchannels(1)  # 单声道
            wf.setsampwidth(2)  # 16位采样
            wf.setframerate(sample_rate)
            wf.writeframes(audio_data.tobytes())
        
        print(f"音频已保存到 {temp_file}")
        
        # 使用不同引擎尝试识别语音
        text = ""
        
        try:
            # 使用Google Speech Recognition
            with sr.AudioFile(temp_file) as source:
                audio_data = recognizer.record(source)
                text = recognizer.recognize_google(audio_data, language="zh-CN")
                print(f"Google识别结果: {text}")
                if text:
                    return text
        except Exception as e:
            print(f"Google识别失败: {str(e)}")
        
        try:
            # 使用Sphinx (离线引擎)
            with sr.AudioFile(temp_file) as source:
                audio_data = recognizer.record(source)
                text = recognizer.recognize_sphinx(audio_data)
                print(f"Sphinx识别结果: {text}")
                if text:
                    return text
        except Exception as e:
            print(f"Sphinx识别失败: {str(e)}")
        
        # 如果所有尝试都失败，返回默认消息
        if not text:
            return "无法识别您的语音，请再试一次"
        
        return text
    except Exception as e:
        print(f"音频处理错误: {e}")
        return "处理音频时发生错误，请重试"
    finally:
        # 清理临时文件
        try:
            if os.path.exists(temp_file):
                os.remove(temp_file)
        except:
            pass

# 当前目录
current_dir = Path(__file__).parent.absolute()
handlers = {}

app = FastAPI()

# 添加CORS支持
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 简化的AsyncStreamHandler
class AsyncStreamHandler:
    def __init__(self, output_sample_rate=24000, output_frame_size=480, input_sample_rate=16000):
        self.output_sample_rate = output_sample_rate
        self.output_frame_size = output_frame_size
        self.input_sample_rate = input_sample_rate
        self.latest_args = None
        self.input_queue = asyncio.Queue()
        self.output_queue = asyncio.Queue()
        self.quit = asyncio.Event()

    async def set_args(self, args: List):
        print(f"设置参数: {args}")
        self.latest_args = args

    async def wait_for_args(self):
        print("等待参数...")
        while self.latest_args is None:
            await asyncio.sleep(0.1)


# 千问处理器
class QianwenHandler(AsyncStreamHandler):
    def __init__(self):
        """初始化千问处理器"""
        super().__init__()
        
        # 加载环境变量
        load_dotenv()
        
        # 获取API密钥
        self.api_keys = check_api_keys()
        
        # 设置固定的APP ID和ACCESS KEY
        self.app_id = "app-ilFpj9cb"
        self.api_key = "bce-v3/ALTAK-1GDTAywL2nQBH1RsSmOUk/d712140f4a0a9fbcd17f4094444a4090e8f7d256"
        self.secret_key = None  # 使用OpenAI兼容方式时不需要secret_key
        
        # 初始化百度语音合成相关属性
        self.baidu_app_id = self.api_keys["baidu_app_id"]
        self.baidu_api_key = self.api_keys["baidu_api_key"]
        self.baidu_secret_key = self.api_keys["baidu_secret_key"]
        
        # 初始化会话历史
        self.conversation_history = []
        
        # 初始化客户输入参数存储
        self.input_params = {}
        
        # 初始化输出队列字典
        self.output_queues = {}
        
        # 配置OpenAI SDK使用百度千帆API
        if OPENAI_SDK_AVAILABLE:
            try:
                # 根据文档配置正确的base_url
                base_url = "https://qianfan.baidubce.com/v2"
                
            
                api_key_without_prefix = self.api_key
                # 创建OpenAI客户端实例
                print("尝试创建OpenAI客户端...")
                self.openai_client = openai.OpenAI(
                    api_key=api_key_without_prefix,  # 使用处理后的API密钥
                    base_url=base_url
                )
                
                # 设置自定义请求头，包含appid
                try:
                    # 使用默认请求头
                    default_headers = {"X-Appid": self.app_id}
                    # 使用正确的方法添加自定义头
                    self.openai_client._client.session.headers.update(default_headers)
                except Exception as header_e:
                    print(f"设置自定义请求头失败: {header_e}")
                    # 继续执行，不要因为这个错误中断
                
                # 测试客户端是否可用
                try:
                    # 获取模型列表，验证客户端是否正常工作
                    models = self.openai_client.models.list()
                except Exception as test_e:
                    print(f"客户端测试失败: {test_e}")
                    # 继续执行，不要因为这个错误中断
                
            except Exception as e:
                import traceback
                print(f"OpenAI客户端初始化失败: {e}")
                print(traceback.format_exc())
                self.openai_client = None
        else:
            print("OpenAI SDK未安装，无法使用OpenAI兼容模式")
            self.openai_client = None
        
        # 保留原始前缀
        print(f"千问API配置: AK={self.api_key[:5]}{'*' * (len(self.api_key) - 10)}{self.api_key[-5:] if self.api_key and len(self.api_key) > 10 else ''}")
        
        # 检查网络连接
        try:
            requests.get("https://www.baidu.com", timeout=5)
            self.network_ok = True
        except Exception as e:
            self.network_ok = False
            
        # 存储输入参数
        self.input_params = {}
        
        # 存储对话历史
        self.conversation_history = []
        
    def validate_api_keys(self):
        """验证并修复API密钥格式"""
        if not self.api_key or not self.secret_key:
            print("警告: API密钥未设置")
            return
            
        # 检查并记录原始密钥格式
        original_ak = self.api_key
        
        # 检查API密钥是否已包含Bearer前缀
        if self.api_key and self.api_key.startswith("Bearer "):
            print("API密钥已包含Bearer前缀")
            # 不做改变，使用原始密钥
            return
        
        # 定义需要移除的前缀列表
        ak_prefixes = ["bce-v3/", "ALTAK-", "ak-", "AK-"]
        
        # 移除可能存在的其他前缀
        for prefix in ak_prefixes:
            if self.api_key.startswith(prefix):
                print(f"检测到access key包含前缀'{prefix}'，移除此前缀")
                self.api_key = self.api_key[len(prefix):]
                break
        
        # 注意：我们不自动添加Bearer前缀，而是在发送请求时添加
        # 这样可以避免在存储密钥时重复添加前缀
        
        if original_ak != self.api_key:
            print("API密钥格式已更新")
        else:
            print("API密钥格式未发生改变")
            
        # 不再处理secret_key，因为我们使用Bearer Token认证
        
        print(f"API密钥验证完成，当前使用: Bearer认证")
        
        # 确保在初始化Qianfan客户端前调用此函数
    
    async def process_audio(self, audio_data):
        """处理音频数据，实现语音转文字功能"""
        print(f"收到音频数据: {len(audio_data)} 字节, 形状={audio_data.shape if isinstance(audio_data, np.ndarray) else 'unknown'}")
        
        # 使用语音识别处理音频
        text = await process_audio_data(audio_data, self.input_sample_rate)
        print(f"语音识别结果: {text}")
        return text
    
    async def start_up(self):
        """启动处理器"""
        print("启动千问处理器...")
        try:
            # 等待参数
            await self.wait_for_args()
            
            # 输出参数日志
            print(f"接收到的参数: {self.latest_args}")
        
            # 从参数列表中提取值
            if not self.latest_args or len(self.latest_args) < 1:
                language, select_type, voice_name, page_type, personality = "中文", "1", "zh-CN-XiaomoNeural", "oral", None
            else:
                args = self.latest_args[0] if isinstance(self.latest_args[0], list) else self.latest_args
                language = args[0] if len(args) > 0 else "中文"
                select_type = args[1] if len(args) > 1 else "1"
                voice_name = args[2] if len(args) > 2 else "zh-CN-XiaomoNeural"
                page_type = args[3] if len(args) > 3 else "oral"
                personality = args[4] if len(args) > 4 else None
        
            print(f"使用参数: language={language}, select_type={select_type}, voice_name={voice_name}, page_type={page_type}, personality={personality}")
            
            # 选择合适的系统提示和首条消息
            system_prompt = ""
            first_message = ""
            
            if page_type == "interview":
                # 面试模式 - 根据人格选择不同的提示
                personality_id = str(personality) if personality is not None else "default"
                print(f"面试模式激活, 人格ID: {personality_id}")
                
                if personality_id == "1":
                    # 董明珠风格
                    system_prompt = """你是董明珠，格力电器的董事长。在面试中，你应该:
                                    1. 强调自律和执行力
                                    2. 关注细节和品质
                                    3. 询问应聘者对创新的看法
                                    4. 考察责任感和抗压能力
                                    5. 提出与实际业务相关的问题
                                    请模拟董明珠的语气和风格，以"我"的第一人称进行对话。"""
                    first_message = "我是董明珠，格力电器的董事长。我们格力有一条铁律：真才实干、说到做到。今天，我想了解你是否具备这样的品质。请先简单介绍一下你自己。"
                
                elif personality_id == "2":
                    # 雷军风格
                    system_prompt = """你是雷军，小米科技的创始人兼CEO。在面试中，你应该:
                                    1. 关注性价比思维和效率
                                    2. 询问关于用户体验和产品细节的问题
                                    3. 考察应聘者的互联网思维
                                    4. 探讨创新与技术的结合
                                    5. 评估团队合作精神和用户导向思维
                                    请模拟雷军的语气和风格，以"我"的第一人称进行对话，偶尔使用"are you OK?"等标志性表达。"""
                    first_message = "你好，我是雷军。在小米，我们追求的是'专注、极致、口碑、快'的理念。我们的每一款产品都致力于让每个人都能享受科技的乐趣。告诉我，你对这个理念有什么看法？Are you OK?"
                
                elif personality_id == "3":
                    # 埃隆·马斯克风格
                    system_prompt = """你是埃隆·马斯克，特斯拉和SpaceX的CEO。在面试中，你应该:
                                    1. 使用第一性原理思考方式
                                    2. 提出具有挑战性的技术问题
                                    3. 关注创新和突破性思维
                                    4. 考察工程和科学背景
                                    5. 探讨应聘者对未来的看法
                                    请模拟埃隆·马斯克的直接、不拘一格的语气和风格，以"我"的第一人称进行对话。"""
                    first_message = "我是埃隆·马斯克。我相信第一性原理思考是解决复杂问题的最佳方式。告诉我，如果你有无限资源，你会解决地球上的哪个问题，为什么？具体怎么做？"
                
                elif personality_id == "4":
                    # 特朗普风格
                    system_prompt = """你是唐纳德·特朗普，前美国总统和商业大亨。在面试中，你应该:
                                    1. 使用夸张和肯定的语气
                                    2. 强调"交易"和商业头脑
                                    3. 询问应聘者的自信和谈判能力
                                    4. 评估领导力和决策能力
                                    5. 偶尔使用"tremendous"、"huge"等标志性词汇
                                    请模拟特朗普的语气和风格，以"我"的第一人称进行对话。"""
                    first_message = "我是唐纳德·特朗普，我创建了世界上最伟大的企业之一。今天我要找到最优秀的人才。告诉我，你为什么认为自己是最好的候选人？"
                else:
                    # 默认面试官
                    print(f"使用默认面试官模式，未识别的人格ID: {personality_id}")
                    system_prompt = """你是一位经验丰富的面试官，正在面试一位应聘者。请按照以下要求进行面试：
                                    1. 开始时简短介绍自己
                                    2. 询问应聘者的基本情况和工作经历
                                    3. 提出1-2个专业问题
                                    4. 根据回答进行追问
                                    5. 3分钟后结束面试并给出简短评价"""
                    first_message = "你好，我是今天的面试官。请简单介绍一下你自己和你的工作经历。"
                
                print(f"选择的系统提示: {system_prompt[:100]}...")
                print(f"选择的首条消息: {first_message}")
            else:
                # 口语练习模式
                if select_type == "1":
                    system_prompt = f"""你是一位专业的{language}考官，正在进行3分钟的口语测试对话。请保持对话自然流畅，按照以下要求进行：
                                    1. 开始时简单问候
                                    2. 询问1-2个日常问题（如天气、爱好）
                                    3. 提出1个情景问题（如旅行计划、工作场景）
                                    4. 根据回答适当追问
                                    5. 3分钟后自然结束对话"""
                    first_message = "您好！我是今天的口语考官。请放松，让我们开始对话吧。请问您今天感觉如何？"
                else:
                    system_prompt = f"""你是一位资深的小学{language}口语外教，正在进行3分钟的口语测试教学。请保持对话自然流畅，按照以下要求进行：
                                    1. 开始时简单问候
                                    2. 教小朋友简单会话
                                    3. 适当用简单并且慢的语言问小朋友问题"""
                    first_message = "嗨，小朋友！我是你的外语老师。今天我们来练习简单的对话，好吗？你能告诉我你的名字吗？"
            
            # 初始化会话历史 - 按照百度千帆API要求：
            # 1. messages数组长度必须为奇数
            # 2. 索引为偶数的role必须是user或function
            # 3. 索引为奇数的role必须是assistant
            self.conversation_history = [
                {"role": "user", "content": f"{system_prompt}\n\n请按照上述要求扮演角色。"}
            ]
            
            # 创建千问客户端
            try:
                print("发送初始消息...")
                
                # 验证和处理API密钥格式
                self.validate_api_keys()
                
                # 获取处理后的密钥
                api_key = self.api_key
                secret_key = self.secret_key
                
                # 检查密钥是否可用
                if not api_key or not secret_key:
                    print("错误: API密钥未设置或无效，使用备用回复")
                    assistant_response = first_message
                    print(f"使用默认回复: {assistant_response}")
                    
                    # 更新会话历史
                    self.conversation_history.append({"role": "assistant", "content": assistant_response})
                    
                    # 将消息发送到输出队列
                    message = {"role": "assistant", "content": assistant_response}
                    await self.output_queue.put(message)
                    return
                
                # 导入并初始化ChatCompletion
                from qianfan import ChatCompletion
                chat_comp = ChatCompletion(
                    ak=api_key,  # 直接使用处理后的密钥
                    sk=secret_key  # 直接使用处理后的密钥
                )
                
                # 打印完整请求内容用于调试
                print(f"模型请求参数: model=ERNIE-Bot-4, messages={self.conversation_history}")
                
                # 发送请求
                try:
                    response = chat_comp.do(
                        model="ERNIE-Bot-4",
                        messages=self.conversation_history
                    )
                    print(f"API响应: {response}")
                except Exception as e:
                    print(f"API请求失败: {e}")
                    if "SignatureDoesNotMatch" in str(e):
                        print("签名验证失败，请检查API密钥格式是否正确，确保包含任何必要的前缀如 'bce-v3/'")
                    raise e
                
                assistant_response = ""
                print("收到千问响应，处理中...")
                
                if "result" in response:
                    assistant_response = response["result"]
                    print(f"千问回复: {assistant_response}")
                else:
                    assistant_response = first_message
                    print(f"使用默认回复: {assistant_response}")
                
                # 更新会话历史
                self.conversation_history.append({"role": "assistant", "content": assistant_response})
                
                # 将消息发送到输出队列，确保只发送可JSON序列化的对象
                message = {"role": "assistant", "content": assistant_response}
                await self.output_queue.put(message)
                
                # 等待用户输入
                print("等待用户输入...")
                
            except Exception as e:
                print(f"千问API调用失败: {type(e).__name__}: {e}")
                await self._generate_fallback_audio(language, select_type)
                
        except Exception as e:
            print(f"未知异常: {type(e).__name__}: {e}")
            await self._generate_fallback_audio(language, select_type)
    
    async def receive(self, frame: tuple[int, np.ndarray]) -> None:
        """接收音频帧并进行处理"""
        timestamp, audio = frame
        
        # 添加详细日志以跟踪音频帧接收
        print(f"接收到音频帧: timestamp={timestamp}, 形状={audio.shape if isinstance(audio, np.ndarray) else 'unknown'}, 类型={audio.dtype if isinstance(audio, np.ndarray) else 'unknown'}")
        
        # 检查音频数据是否有效
        if audio is None or (isinstance(audio, np.ndarray) and (len(audio) == 0 or np.all(audio == 0))):
            print("警告: 收到空音频帧或静音帧，跳过处理")
            return
        
        # 将音频帧放入输入队列，等待处理
        audio_copy = audio.copy()
        await self.input_queue.put((timestamp, audio_copy))
        
        # 处理音频，获取文字结果
        text = await self.process_audio(audio_copy)
        
        if not text or text == "无法识别您的语音，请再试一次":
            print("音频识别失败或没有识别到语音内容，跳过此帧")
            return
        
        print(f"识别到用户语音内容: {text}")
        
        # 确保会话历史符合百度千帆API的要求
        # 1. 如果最后一条消息是assistant角色，可以直接添加用户消息
        # 2. 如果最后一条消息是user角色，需要先添加一个空的assistant回复
        if len(self.conversation_history) > 0 and self.conversation_history[-1]["role"] == "user":
            # 添加一个空的assistant回复，确保角色交替
            self.conversation_history.append({"role": "assistant", "content": "我正在思考您的问题..."})
        
        # 添加用户消息到对话历史
        self.conversation_history.append({"role": "user", "content": text})
        
        try:
            # 验证和处理API密钥
            self.validate_api_keys()
            
            # 获取处理后的密钥
            api_key = self.api_key
            secret_key = self.secret_key
            
            # 检查是否有有效的API密钥
            if not api_key or not secret_key:
                print("错误: 缺少API密钥，使用备用回复")
                response = "很抱歉，由于API密钥未配置，无法使用AI对话功能。请联系管理员配置百度千问API密钥。"
            elif not self.network_ok:
                print("错误: 网络连接异常，使用备用回复")
                response = "很抱歉，由于网络连接异常，无法连接到AI服务。请检查网络连接后重试。"
            else:
                print("发送用户消息到千问API...")
                try:
                    # 初始化Completion客户端
                    from qianfan import Completion
                    
                    # 使用处理后的密钥初始化客户端
                    print(f"使用处理后的密钥初始化Completion: AK={api_key[:5]}...{api_key[-5:] if len(api_key) > 10 else ''}")
                    completion = Completion(
                        ak=api_key,  # 直接使用处理后的密钥
                        sk=secret_key  # 直接使用处理后的密钥
                    )
                    
                    # 使用千帆API进行对话
                    resp = completion.do(
                        prompt=text,
                        model="ERNIE-Bot-turbo-0922",
                        temperature=0.7,
                        top_p=0.9,
                    )
                    
                    if "result" in resp:
                        response = resp["result"]
                        print(f"千问API响应: {response[:100]}...")
                    else:
                        print(f"千问API响应格式异常: {resp}")
                        response = "AI响应格式异常，请重试。"
                except Exception as e:
                    print(f"千问API调用失败: {e}")
                    response = f"千问API调用失败，请确保API密钥正确: {str(e)}"
            
            # 添加助手回复到对话历史
            self.conversation_history.append({"role": "assistant", "content": response})
            
            # 将结果放入输出队列，确保只发送可JSON序列化的对象
            message = {"role": "assistant", "content": response}
            await self.output_queue.put(message)
            print(f"已发送助手回复到输出队列: {response[:50]}...")
            
        except Exception as e:
            print(f"处理响应时发生错误: {e}")
            await self._generate_fallback_audio(self.latest_args[0] if self.latest_args else "英语", 
                                              self.latest_args[1] if self.latest_args and len(self.latest_args) > 1 else "1")

    async def _generate_fallback_audio(self, language, select_type):
        """生成备用响应，用于API调用失败时"""
        print(f"生成备用响应: {language}, 类型 {select_type}")
        
        # 根据语言和类型选择不同的备用消息
        message = "你好，我是AI教学助手。由于API连接问题，无法提供完整服务。"
        
        # 添加到对话历史
        if len(self.conversation_history) > 0 and self.conversation_history[-1].get("role") != "assistant":
            self.conversation_history.append({"role": "assistant", "content": message})
        
        print(f"备用消息: {message}")
        
        # 将消息放入输出队列，确保是可JSON序列化的
        await self.output_queue.put({"role": "assistant", "content": message})

    async def emit(self):
        """发送音频帧"""
        try:
            return await self.output_queue.get()
        except asyncio.CancelledError:
            return None

    async def process_qianfan(self, webrtc_id, user_message=None):
        """使用百度千帆API处理WebRTC语音或用户文字消息"""
        print(f"尝试连接千问API... {'处理用户消息：' + user_message if user_message else '处理音频输入'}")
        
        # 获取页面类型和其他参数
        language, select_type, voice_name, page_type = self.input_params.get(
            webrtc_id, ("英语", "1", "Puck", "oral")
        )
        if len(self.input_params.get(webrtc_id, [])) < 4:
            page_type = "oral"  # 默认为口语练习
        
        print(f"语言: {language}, 类型: {select_type}, 声音: {voice_name}, 页面类型: {page_type}")
        
        # 检查是否已有会话历史，如果没有则初始化
        if not hasattr(self, 'conversation_history') or not self.conversation_history:
            self.conversation_history = []
            
            # 根据页面类型选择不同的系统提示
            if page_type == "interview":
                # 面试模式，根据人格ID选择不同的系统提示
                personality_id = self.input_params.get(webrtc_id, ["", "1"])[1]
                print(f"面试模式激活, 人格ID: {personality_id}")
                
                # 添加系统消息
                system_prompt = self.get_interview_system_prompt(personality_id)
                if system_prompt:
                    print(f"选择的系统提示: {system_prompt[:100]}...")
                    self.conversation_history.append({
                        "role": "system",
                        "content": system_prompt
                    })
                
                # 添加初始消息
                initial_message = self.get_interview_initial_message(personality_id)
                if initial_message:
                    print(f"选择的首条消息: {initial_message[:100]}...")
                    print("发送初始消息...")
                    self.conversation_history.append({
                        "role": "assistant",
                        "content": initial_message
                    })
                    
                    # 将初始消息发送到输出队列
                    await self.send_to_output(webrtc_id, {
                        "role": "assistant", 
                        "content": initial_message
                    })
            else:
                # 口语练习模式，使用默认系统提示
                self.conversation_history.append({
                    "role": "system",
                    "content": "你是一个友好的AI面试官，帮助用户练习面试情景对话。请用简短、自然的方式回应，就像真人对话一样。"
                })

        try:
            # 检查OpenAI客户端是否可用
            if not OPENAI_SDK_AVAILABLE or not self.openai_client:
                print("错误: OpenAI SDK未安装或客户端初始化失败，使用备用回复")
                await self._generate_fallback_audio(language, select_type)
                return
            
            # 如果提供了用户消息，将其添加到对话历史中
            if user_message:
                self.conversation_history.append({
                    "role": "user",
                    "content": user_message
                })
            
            # 转换对话历史为OpenAI格式
            openai_messages = []
            for msg in self.conversation_history:
                openai_messages.append({
                    "role": msg["role"],
                    "content": msg["content"]
                })
            
            print(f"发送请求到千帆API...")
            print(f"消息数量: {len(openai_messages)}")
            print(f"最新消息: {openai_messages[-1] if openai_messages else 'None'}")
            
            # 确保消息不为空
            if not openai_messages:
                print("警告: 消息列表为空，添加默认系统消息")
                openai_messages.append({
                    "role": "system",
                    "content": "你是一个友好的AI面试官，请用专业的方式回答用户的问题。"
                })
                
                # 如果用户消息也为空，添加一个默认的用户消息
                if not user_message:
                    print("警告: 用户消息为空，添加默认用户消息")
                    openai_messages.append({
                        "role": "user",
                        "content": "你好，请开始面试。"
                    })
            
            try:
                # 使用OpenAI SDK调用千帆API
                # 使用正确的模型名称
                response = self.openai_client.chat.completions.create(
                    model="ernie-4.0-turbo-8k",  # 使用ernie-4.0-turbo-8k
                    messages=openai_messages,
                    temperature=0.7,
                    stream=False
                )
                
                print(f"API响应: {response}")
                
                # 从响应中提取助手回复
                if response.choices and len(response.choices) > 0:
                    assistant_response = response.choices[0].message.content
                    
                    # 将助手回复加入对话历史
                    self.conversation_history.append({
                        "role": "assistant",
                        "content": assistant_response
                    })
                    
                    # 将消息发送到输出队列
                    await self.send_to_output(webrtc_id, {
                        "role": "assistant", 
                        "content": assistant_response
                    })
                    return
                else:
                    print(f"警告: API响应中没有回复内容")
                    raise ValueError("API响应格式异常，没有回复内容")
                
            except Exception as e:
                import traceback
                print(f"OpenAI SDK调用失败: {e}")
                print(traceback.format_exc())
                
                # 尝试使用直接HTTP请求作为备用方案
                print("SDK调用失败，尝试直接HTTP请求...")
                
                # 构造HTTP请求
                import requests
                
                # 准备请求URL和请求头
                api_url = "https://qianfan.baidubce.com/v2/chat/completions"
                
                headers = {
                    "Content-Type": "application/json",
                    "Authorization": f"Bearer {self.api_key}",
                    "X-Appid": self.app_id
                }
                
                # 准备请求体
                payload = {
                    "model": "ernie-4.0-turbo-8k",  
                    "messages": openai_messages,
                    "temperature": 0.7,
                    "stream": False
                }
                
                # 发送请求
                http_response = requests.post(api_url, headers=headers, json=payload, timeout=10)
                
                if http_response.status_code == 200:
                    response_json = http_response.json()
                    if "choices" in response_json and len(response_json["choices"]) > 0:
                        assistant_response = response_json["choices"][0]["message"]["content"]
                        print(f"HTTP请求成功，千问回复: {assistant_response}")
                        
                        # 将助手回复加入对话历史
                        self.conversation_history.append({
                            "role": "assistant",
                            "content": assistant_response
                        })
                        
                        # 将消息发送到输出队列
                        await self.send_to_output(webrtc_id, {
                            "role": "assistant", 
                            "content": assistant_response
                        })
                        return
                    else:
                        print(f"警告: HTTP响应中没有回复内容: {response_json}")
                else:
                    print(f"HTTP请求失败: {http_response.status_code} {http_response.text}")
                    raise ValueError(f"HTTP请求失败: {http_response.status_code}")
                
        except Exception as e:
            import traceback
            print(f"千问API调用失败: {e}")
            print(traceback.format_exc())
        
        # 如果上面的流程失败，发送备用消息
        print(f"生成备用响应: {language}, 类型 {select_type}, 页面类型 {page_type}")
        await self._generate_fallback_audio(language, select_type)

    async def send_to_output(self, webrtc_id, message):
        """将消息发送到输出队列，并尝试进行语音合成"""
        if not message or not isinstance(message, dict) or "content" not in message:
            print("错误: 无效的消息格式")
            return
        
        # 获取消息内容
        content = message["content"]
        if not content:
            print("错误: 消息内容为空")
            return
        
        # 获取语音设置
        language, select_type, voice_name, page_type = self.input_params.get(
            webrtc_id, ("英语", "1", "Puck", "oral")
        )
        
        # 尝试生成语音
        try:
            # 初始化语音合成客户端（如果需要）
            if not hasattr(self, 'speech_client') or not self.speech_client:
                self.initialize_speech_client()
            
            # 使用语音合成客户端生成语音
            audio_data = await self._synthesize_speech(content, language, voice_name)
            
            # 将语音数据添加到消息中
            if audio_data:
                message["audio"] = audio_data
            
        except Exception as e:
            print(f"语音合成出错: {e}")
            print("尝试使用Google TTS进行语音合成...")
            
            try:
                # 使用Google TTS作为备用
                audio_data = await self._synthesize_speech_google(content, language)
                if audio_data:
                    message["audio"] = audio_data
                    print("Google TTS生成成功")
                else:
                    print("Google TTS生成失败")
            except Exception as e2:
                print(f"Google TTS也失败了: {e2}")
        
        # 将消息发送到输出队列
        output_queue = self.output_queues.get(webrtc_id)
        if output_queue:
            await output_queue.put(message)
            print(f"发送消息到前端: {content[:100]}...")
        else:
            print(f"错误: 找不到输出队列: {webrtc_id}")
    
    def initialize_speech_client(self):
        """初始化语音合成客户端"""
        # 这里可以初始化百度或其他TTS客户端
        # 由于我们已经有了Google TTS作为备用，这里可以留空
        pass
    
    async def _synthesize_speech(self, text, language, voice_name):
        """使用语音合成API生成语音"""
        # 这个方法可以调用百度或其他TTS API
        # 由于我们已经有了Google TTS作为备用，这里直接使用Google TTS
        return await self._synthesize_speech_google(text, language)
    
    async def _synthesize_speech_google(self, text, language):
        """使用Google TTS生成语音"""
        try:
            from gtts import gTTS
            import io
            import base64
            
            # 根据语言选择Google TTS语言代码
            lang_code = "en"
            if language == "中文":
                lang_code = "zh-cn"
            elif language == "日语":
                lang_code = "ja"
            
            # 创建内存文件对象
            mp3_fp = io.BytesIO()
            
            # 生成语音
            tts = gTTS(text=text, lang=lang_code, slow=False)
            tts.write_to_fp(mp3_fp)
            mp3_fp.seek(0)
            
            # 将MP3数据转换为base64编码
            audio_data = base64.b64encode(mp3_fp.read()).decode('utf-8')
            
            # 返回base64编码的音频数据
            return f"data:audio/mp3;base64,{audio_data}"
        
        except Exception as e:
            print(f"Google TTS错误: {e}")
            return None

    def get_interview_system_prompt(self, personality_id):
        """根据人格ID获取面试系统提示"""
        prompts = {
            "1": """你是董明珠，格力电器的董事长。在面试中，你应该:
                1. 强调自律和执行力
                2. 关注细节和品质
                3. 询问应聘者对创新的看法
                4. 考察责任感和抗压能力
                5. 提出与实际业务相关的问题
                请模拟董明珠的语气和风格，以"我"的第一人称进行对话。""",
            
            "2": """你是雷军，小米科技的创始人兼CEO。在面试中，你应该:
                1. 关注性价比思维和效率
                2. 询问关于用户体验和产品细节的问题
                3. 考察应聘者的互联网思维
                4. 探讨创新与技术的结合
                5. 评估团队合作精神和用户导向思维
                请模拟雷军的语气和风格，以"我"的第一人称进行对话，偶尔使用"are you OK?"等标志性表达。""",
            
            "3": """你是埃隆·马斯克，特斯拉和SpaceX的CEO。在面试中，你应该:
                1. 使用第一性原理思考方式
                2. 提出具有挑战性的技术问题
                3. 关注创新和突破性思维
                4. 考察工程和科学背景
                5. 探讨应聘者对未来的看法
                请模拟埃隆·马斯克的直接、不拘一格的语气和风格，以"我"的第一人称进行对话。""",
            
            "4": """你是唐纳德·特朗普，前美国总统和商业大亨。在面试中，你应该:
                1. 使用夸张和肯定的语气
                2. 强调"交易"和商业头脑
                3. 询问应聘者的自信和谈判能力
                4. 评估领导力和决策能力
                5. 偶尔使用"tremendous"、"huge"等标志性词汇
                请模拟特朗普的语气和风格，以"我"的第一人称进行对话。""",
            
            "5": """你是马云，阿里巴巴的创始人。在面试中，你应该:
                1. 关注候选人的潜力和学习能力
                2. 寻找有梦想、有激情的人才
                3. 强调团队合作和价值观的重要性
                4. 提出开放性问题，考察候选人的思维方式
                5. 分享你对互联网、电子商务和企业家精神的见解
                请模拟马云的语气和风格，以"我"的第一人称进行对话。"""
        }
        
        return prompts.get(personality_id, "你是一位经验丰富的面试官，正在对候选人进行面试。请提出专业、有深度的问题，评估候选人的能力和潜力。")

    def get_interview_initial_message(self, personality_id):
        """根据人格ID获取面试初始消息"""
        messages = {
            "1": "我是董明珠，格力电器的董事长。我们格力有一条铁律：真才实干、说到做到。今天，我想了解你是否具备这样的品质。请先简单介绍一下你自己。",
            
            "2": "你好，我是雷军。在小米，我们追求的是'专注、极致、口碑、快'的理念。我们的每一款产品都致力于让每个人都能享受科技的乐趣。告诉我，你对这个理念有什么看法？Are you OK?",
            
            "3": "我是埃隆·马斯克。我相信第一性原理思考是解决复杂问题的最佳方式。告诉我，如果你有无限资源，你会解决地球上的哪个问题，为什么？具体怎么做？",
            
            "4": "我是唐纳德·特朗普，我创建了世界上最伟大的企业之一。今天我要找到最优秀的人才。告诉我，你为什么认为自己是最好的候选人？",
            
            "5": "你好，我是马云。很高兴今天能和你见面。阿里巴巴寻找的不仅是有能力的人，更是有梦想、有激情的人。请告诉我，你为什么选择应聘这个职位？你的梦想是什么？",
        }
        
        return messages.get(personality_id, "你好，我是面试官。很高兴认识你，请简单介绍一下你自己和你的工作经历。")

    async def text_to_speech(self, text, language="中文", voice_name=None):
        """将文本转换为语音"""
        try:
            # 直接使用Google TTS作为语音合成方法
            return await self._synthesize_speech_google(text, language)
        except Exception as e:
            print(f"语音合成失败: {e}")
            return None


# 简化的Stream类
class Stream:
    handlers_dict = {}
    
    @staticmethod
    def set_handlers_dict(handlers_dict):
        Stream.handlers_dict = handlers_dict
    
    def set_input(self, webrtc_id, *args):
        print(f"设置输入: webrtc_id={webrtc_id}, args={args}")
        handler = Stream.handlers_dict.get(webrtc_id)
        if handler:
            # 确保参数中包含page_type
            if len(args) >= 4:
                # 已经包含page_type
                asyncio.create_task(handler.set_args(args))
            else:
                # 添加默认的page_type="oral"
                new_args = list(args)
                new_args.append("oral")  # 默认为口语练习
                asyncio.create_task(handler.set_args(new_args))
            return True
        return False


# 创建全局stream实例
stream = Stream()


# 输入数据模型
class InputData(BaseModel):
    webrtc_id: str
    language: str = "中文"  # 默认语言
    select_type: str = "1"  # 默认类型
    voice_name: str = "zh-CN-XiaomoNeural"  # 默认语音
    page_type: str = "oral"  # 默认为口语练习页面
    personality: Optional[str] = None  # 添加人格选择参数
    message: Optional[str] = None  # 添加聊天消息字段


@app.post("/input_hook")
async def input_hook(request: Request):
    """处理输入事件"""
    try:
        # 直接从请求体获取JSON数据
        data = await request.json()
        print(f"收到输入: {data}")
        
        # 获取WebRTC ID和其他参数
        webrtc_id = data.get('webrtc_id')
        if not webrtc_id:
            return JSONResponse({"error": "Missing webrtc_id parameter"}, status_code=400)
        
        # 处理聊天消息 - 修改条件检查，更加宽松地接受消息格式
        if 'message' in data:
            message = data.get('message', '')
            print(f"收到聊天消息: {message}")
            # 将消息发送到处理程序
            handler = handlers.get(webrtc_id)
            if handler and message:
                # 创建一个音频帧格式的消息，让处理程序理解
                # 这里我们只是传递文本消息，不提供实际音频数据
                dummy_audio = np.zeros(100, dtype=np.float32)
                await handler.input_queue.put((0, dummy_audio))
                # 这里我们直接将消息添加到会话历史中
                if hasattr(handler, 'conversation_history'):
                    handler.conversation_history.append({"role": "user", "content": message})
                    # 触发处理逻辑
                    asyncio.create_task(handler.process_qianfan(webrtc_id))
                
                # 立即返回确认，不等待AI响应
                return JSONResponse({
                    "success": True,
                    "message": "消息已接收，正在处理中"
                })
            return JSONResponse({"error": "No active handler or empty message"}, status_code=400)
            
        # 处理人格选择
        elif 'personality' in data:
            personality = data.get('personality')
            print(f"收到人格选择: {personality}")
            
            # 构造输入参数
            language = data.get('language', '中文')
            select_type = data.get('select_type', personality)  # 使用personality作为备用值
            voice_name = data.get('voice_name', 'zh-CN-XiaomoNeural')
            page_type = data.get('page_type', 'interview')
            
            # 获取处理器
            handler = handlers.get(webrtc_id)
            if not handler:
                # 如果处理器不存在，创建一个新的
                handler = QianwenHandler()
                handlers[webrtc_id] = handler
                print(f"为webrtc_id={webrtc_id}创建了新的处理器")
                
                # 为该WebRTC ID创建输出队列
                handler.output_queues[webrtc_id] = asyncio.Queue()
                
                # 启动处理器的异步任务，确保音频处理流程初始化
                asyncio.create_task(handler.start_up())
            
            # 重置会话历史
            if hasattr(handler, 'conversation_history'):
                handler.conversation_history = []
            
                # 存储参数
                handler.input_params[webrtc_id] = (language, select_type, voice_name, page_type)
            
                # 设置参数
                await handler.set_args([language, select_type, voice_name, page_type, personality])
                print(f"设置参数: {language}, {select_type}, {voice_name}, {page_type}, {personality}")
            
                # 触发处理逻辑
                asyncio.create_task(handler.process_qianfan(webrtc_id))
            
            # 立即返回确认，不等待AI响应
            return JSONResponse({
                "success": True,
                "message": f"人格选择已接收: {personality}，正在初始化对话"
            })
        
        # 处理其他输入参数
        elif all(k in data for k in ['language', 'select_type', 'voice_name']):
            language = data.get('language')
            select_type = data.get('select_type')
            voice_name = data.get('voice_name')
            page_type = data.get('page_type', 'oral')  # 默认为口语练习
            
            print(f"收到输入: webrtc_id='{webrtc_id}' language='{language}' select_type='{select_type}' voice_name='{voice_name}' page_type='{page_type}'")
            
            # 获取处理器
            handler = handlers.get(webrtc_id)
            if not handler:
                # 如果处理器不存在，创建一个新的
                handler = QianwenHandler()
                handlers[webrtc_id] = handler
                print(f"为webrtc_id={webrtc_id}创建了新的处理器")
            
                # 存储参数
                handler.input_params[webrtc_id] = (language, select_type, voice_name, page_type)
                # 设置参数
                await handler.set_args([language, select_type, voice_name, page_type])
                print(f"设置参数: ({language}, {select_type}, {voice_name}, {page_type})")
            
            # 立即返回确认，不等待AI响应
            return JSONResponse({
                "success": True,
                "message": "参数已接收，正在初始化对话"
            })
        
        else:
            print(f"无法处理的输入格式: {data}")
            return JSONResponse({"error": "Invalid input parameters", "received": data}, status_code=400)
        
    except Exception as e:
        import traceback
        print(f"输入钩子错误: {e}")
        print(traceback.format_exc())
        return JSONResponse({"error": str(e)}, status_code=500)


@app.get("/outputs")
async def outputs(webrtc_id: str):
    """处理SSE输出流，发送消息到前端"""
    print(f"收到输出请求: webrtc_id={webrtc_id}")
    
    async def event_stream():
        """事件流处理函数"""
        # 发送初始消息，表示连接已建立
        yield f"event: connected\ndata: {json.dumps({'status': 'connected'})}\n\n"
        
        # 发送系统就绪消息
        yield f"event: ready\ndata: {json.dumps({'content': '系统已准备就绪，请开始说话...'})}\n\n"
        
        # 初始化计数器和标志
        counter = 0
        handler = handlers.get(webrtc_id)
        
        if not handler:
            yield f"event: error\ndata: {json.dumps({'error': '找不到该WebRTC ID的处理器'})}\n\n"
            return
        
        # 检查API密钥配置
        if not handler.api_key:
            yield f"event: output\ndata: {json.dumps({'content': '请配置有效的千问API密钥以启用AI对话功能。请在.env文件中添加QIANFAN_AK和QIANFAN_SK。'})}\n\n"
        
        # 主动发送初始AI消息
        try:
            # 获取页面类型和其他参数
            language, select_type, voice_name, page_type = handler.input_params.get(
                webrtc_id, ("英语", "1", "Puck", "oral")
            )
            if len(handler.input_params.get(webrtc_id, [])) < 4:
                page_type = "oral"  # 默认为口语练习
            
            # 根据页面类型选择不同的初始消息
            if page_type == "interview":
                if select_type == "1":  # 董明珠
                    initial_message = "我是董明珠，格力电器的董事长。我们格力有一条铁律：真才实干、说到做到。今天，我想了解你是否具备这样的品质。请先简单介绍一下你自己。"
                elif select_type == "2":  # 雷军
                    initial_message = "你好，我是雷军。在小米，我们追求的是'专注、极致、口碑、快'的理念。我们的每一款产品都致力于让每个人都能享受科技的乐趣。告诉我，你对这个理念有什么看法？Are you OK?"
                elif select_type == "3":  # 埃隆·马斯克
                    initial_message = "我是埃隆·马斯克。我相信第一性原理思考是解决复杂问题的最佳方式。告诉我，如果你有无限资源，你会解决地球上的哪个问题，为什么？具体怎么做？"
                elif select_type == "4":  # 唐纳德·特朗普
                    initial_message = "我是唐纳德·特朗普，我创建了世界上最伟大的企业之一。今天我要找到最优秀的人才。告诉我，你为什么认为自己是最好的候选人？"
                elif select_type == "5":  # 马云
                    initial_message = "你好，我是马云。很高兴今天能和你见面。阿里巴巴寻找的不仅是有能力的人，更是有梦想、有激情的人。请告诉我，你为什么选择应聘这个职位？你的梦想是什么？"
                else:
                    initial_message = "你好，我是今天的面试官。请简单介绍一下你自己和你的工作经历。"
            else:
                initial_message = f"你好，我是AI面试助手。我们今天将进行{language}面试。请用{language}简单介绍一下你自己。"
            
            # 生成消息对象
            import uuid
            initial_response = {
                "role": "assistant", 
                "content": initial_message,
                "type": "assistant",
                "timestamp": int(time.time() * 1000),
                "id": str(uuid.uuid4())
            }
            
            # 生成语音数据
            audio_data = await handler.text_to_speech(initial_message)
            if audio_data:
                # 将音频数据转换为Base64编码
                audio_base64 = base64.b64encode(audio_data).decode('utf-8')
                initial_response["audio"] = audio_base64
            
            # 将初始消息放入输出队列
            await handler.output_queue.put(initial_response)
            
            # 直接发送初始消息
            yield f"event: output\ndata: {json.dumps(initial_response)}\n\n"
            
            # 立即触发处理逻辑
            asyncio.create_task(handler.process_qianfan(webrtc_id))
            
        except Exception as e:
            print(f"发送初始AI消息失败: {e}")
            # 发送一个简单的默认消息
            import uuid
            yield f"event: output\ndata: {json.dumps({
                'role': 'assistant', 
                'content': '你好，我是AI助手。请开始我们的对话。',
                'type': 'assistant',
                'timestamp': int(time.time() * 1000),
                'id': str(uuid.uuid4())
            })}\n\n"
        
        # 初始化计数器
        counter = 0
        
        # 持续从处理器获取输出并发送到前端
        while True:
            try:
                if not handler.output_queue.empty():
                    message = await handler.output_queue.get()
                    if message:
                        # 确保消息有正确的格式
                        if isinstance(message, dict) and 'content' in message:
                            if 'type' not in message:
                                message['type'] = 'assistant'
                            if 'timestamp' not in message:
                                message['timestamp'] = int(time.time() * 1000)
                            if 'id' not in message:
                                message['id'] = str(uuid.uuid4())
                                
                            print(f"发送消息到前端: {message['content'][:50]}...")
                        yield f"event: output\ndata: {json.dumps(message)}\n\n"
                
                # 发送心跳以保持连接
                counter += 1
                if counter >= 3:  # 每3次循环发送一次心跳（更频繁）
                    yield f"event: ping\ndata: {json.dumps({'time': time.time()})}\n\n"
                    counter = 0
                
                # 减少等待时间，提高响应速度
                await asyncio.sleep(0.2)
            except Exception as e:
                print(f"流处理错误: {e}")
                yield f"event: error\ndata: {json.dumps({'error': str(e)})}\n\n"
                # 不中断循环，继续尝试发送消息
                await asyncio.sleep(1)
    
    return StreamingResponse(event_stream(), media_type="text/event-stream")


@app.get("/health")
async def health():
    logger.info("Health check requested")
    return {"status": "UP", "message": "Qianwen service is running"}

@app.get("/test-qianfan-auth")
async def test_qianfan_auth():
    """测试千帆API认证是否正常工作的端点"""
    try:
        # 创建一个临时的QianwenHandler实例以利用其验证方法
        temp_handler = QianwenHandler()
        
        # 检查OpenAI客户端是否可用
        if not OPENAI_SDK_AVAILABLE:
            return {"status": "ERROR", "message": "OpenAI SDK未安装，请运行 pip install openai"}
        
        if not temp_handler.openai_client:
            return {"status": "ERROR", "message": "OpenAI客户端初始化失败，请检查API密钥配置"}
        
        # 使用OpenAI SDK调用千帆API
        try:
            # 准备测试消息
            messages = [{"role": "user", "content": "你好，请做个自我介绍"}]
            
            # 发送请求
            response = temp_handler.openai_client.chat.completions.create(
                model="ernie-4.0-turbo-8k",  
                messages=messages,
                temperature=0.7,
                stream=False
            )
            
            # 从响应中提取助手回复
            if response.choices and len(response.choices) > 0:
                assistant_response = response.choices[0].message.content
                
                return {
                    "status": "SUCCESS",
                    "message": "千帆API认证成功",
                    "auth_method": "OpenAI SDK兼容模式",
                    "response_sample": assistant_response[:100] + "...",
                    "app_id": temp_handler.app_id,
                    "base_url": "https://qianfan.baidubce.com/v2"
                }
            else:
                return {
                    "status": "WARNING",
                    "message": "API响应状态正常，但响应格式异常，没有回复内容",
                    "auth_method": "OpenAI SDK兼容模式",
                    "response": str(response)
                }
                
        except Exception as e:
            import traceback
            trace = traceback.format_exc()
            
            # 尝试使用直接HTTP请求作为备用方案
            try:
                import requests
                
                # 准备请求URL和请求头
                api_url = "https://qianfan.baidubce.com/v2/chat/completions"
                
                # 处理API密钥格式
                api_key = temp_handler.api_key
                
                headers = {
                    "Content-Type": "application/json",
                    "Authorization": f"Bearer {api_key}",
                    "X-Appid": temp_handler.app_id
                }
                
                # 准备请求体
                payload = {
                    "model": "ernie-4.0-turbo-8k",  # 使用小写模型名称
                    "messages": [{"role": "user", "content": "你好，请做个自我介绍"}],
                    "temperature": 0.7,
                    "stream": False
                }
                
                # 打印请求信息
                print(f"HTTP请求URL: {api_url}")
                print(f"HTTP请求头: {headers}")
                print(f"HTTP请求体: {payload}")
                
                # 发送请求
                http_response = requests.post(api_url, headers=headers, json=payload, timeout=10)
                
                print(f"HTTP响应状态码: {http_response.status_code}")
                print(f"HTTP响应内容: {http_response.text[:200]}...")
                
                if http_response.status_code == 200:
                    response_json = http_response.json()
                    if "choices" in response_json and len(response_json["choices"]) > 0:
                        assistant_response = response_json["choices"][0]["message"]["content"]
                        return {
                            "status": "SUCCESS",
                            "message": "千帆API认证成功（通过HTTP请求）",
                            "auth_method": "直接HTTP请求",
                            "response_sample": assistant_response[:100] + "...",
                            "app_id": temp_handler.app_id,
                            "base_url": api_url
                        }
                    else:
                        return {
                            "status": "WARNING",
                            "message": f"HTTP请求成功，但响应格式异常: {response_json}",
                            "auth_method": "直接HTTP请求"
                        }
                else:
                    error_msg = f"HTTP请求失败: {http_response.status_code} "
                    try:
                        error_detail = http_response.json()
                        if isinstance(error_detail, dict):
                            error_msg += f" - {error_detail.get('error', {}).get('message', '')}"
                    except:
                        error_msg += http_response.text[:200]
                    
                    return {
                        "status": "ERROR",
                        "message": f"千帆API认证失败: {error_msg}",
                        "error_type": "HTTPError",
                        "auth_method": "直接HTTP请求",
                        "sdk_error": str(e),
                        "sdk_trace": trace
                    }
            
            except Exception as http_e:
                http_trace = traceback.format_exc()
                return {
                    "status": "ERROR", 
                    "message": f"千帆API认证失败: SDK错误: {str(e)}，HTTP错误: {str(http_e)}", 
                    "error_type": f"{type(e).__name__}, {type(http_e).__name__}",
                    "sdk_trace": trace,
                    "http_trace": http_trace
                }
            
    except Exception as e:
        import traceback
        trace = traceback.format_exc()
        return {"status": "ERROR", "message": f"测试过程中发生错误: {str(e)}", "error_type": type(e).__name__, "trace": trace}

@app.post("/webrtc/offer")
async def webrtc_offer(request: Request):
    """处理WebRTC offer"""
    try:
        data = await request.json()
        webrtc_id = data.get("webrtc_id", "")
        sdp = data.get("sdp", "")
        print(f"收到WebRTC offer，webrtc_id: {webrtc_id}")
        
        # 创建一个新的处理器并存储到handlers字典中
        handler = QianwenHandler()
        handlers[webrtc_id] = handler
        
        # 为该WebRTC ID创建输出队列
        handler.output_queues[webrtc_id] = asyncio.Queue()
        
        # 启动处理器的异步任务，确保音频处理流程初始化
        asyncio.create_task(handler.start_up())
        print(f"已启动WebRTC处理器，等待音频输入")
        
        # 更详细地解析输入SDP以获取媒体类型和顺序
        m_lines = []
        for line in sdp.split("\r\n"):
            if line.startswith("m="):
                m_type = line.split(" ")[0].replace("m=", "")
                m_lines.append(m_type)
        
        print(f"检测到的媒体类型: {m_lines}")
        
        # 构建与offer匹配的answer SDP
        answer_sdp = "v=0\r\no=- 3960346771 3960346771 IN IP4 0.0.0.0\r\ns=-\r\nt=0 0\r\n"
        
        # 捆绑所有媒体行
        bundle_line = "a=group:BUNDLE"
        for i in range(len(m_lines)):
            bundle_line += f" {i}"
        answer_sdp += bundle_line + "\r\n"
        
        answer_sdp += "a=msid-semantic:WMS *\r\n"
        
        # 添加对应客户端offer的媒体行，保持相同的顺序和类型
        for i, media_type in enumerate(m_lines):
            if media_type == "audio":
                answer_sdp += f"m=audio 9 UDP/TLS/RTP/SAVPF 111\r\n"
                answer_sdp += "c=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\n"
                answer_sdp += "a=ice-ufrag:rnoH\r\na=ice-pwd:cJVbCPn8XAsHgrbtvbhxrT\r\n"
                answer_sdp += "a=fingerprint:sha-256 2D:40:2B:3F:40:4D:5A:A8:0C:68:81:E8:5A:22:3B:43:E5:90:2D:A8:11:44:6A:0E:2E:C3:E8:51:10:E9:30:54\r\n"
                answer_sdp += f"a=setup:active\r\na=mid:{i}\r\na=sendrecv\r\na=rtcp-mux\r\na=rtpmap:111 opus/48000/2\r\n"
            
            elif media_type == "video":
                answer_sdp += f"m=video 9 UDP/TLS/RTP/SAVPF 96\r\n"
                answer_sdp += "c=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\n"
                answer_sdp += "a=ice-ufrag:rnoH\r\na=ice-pwd:cJVbCPn8XAsHgrbtvbhxrT\r\n"
                answer_sdp += "a=fingerprint:sha-256 2D:40:2B:3F:40:4D:5A:A8:0C:68:81:E8:5A:22:3B:43:E5:90:2D:A8:11:44:6A:0E:2E:C3:E8:51:10:E9:30:54\r\n"
                answer_sdp += f"a=setup:active\r\na=mid:{i}\r\na=sendrecv\r\na=rtcp-mux\r\n"
                answer_sdp += "a=rtpmap:96 VP8/90000\r\n"
            
            else:
                # 应用数据通道或其他媒体类型
                answer_sdp += f"m=application 9 UDP/DTLS/SCTP webrtc-datachannel\r\n"
                answer_sdp += "c=IN IP4 0.0.0.0\r\n"
                answer_sdp += "a=ice-ufrag:rnoH\r\na=ice-pwd:cJVbCPn8XAsHgrbtvbhxrT\r\n"
                answer_sdp += "a=fingerprint:sha-256 2D:40:2B:3F:40:4D:5A:A8:0C:68:81:E8:5A:22:3B:43:E5:90:2D:A8:11:44:6A:0E:2E:C3:E8:51:10:E9:30:54\r\n"
                answer_sdp += f"a=setup:active\r\na=mid:{i}\r\na=sctp-port:5000\r\n"
        
        print(f"生成的answer SDP: {answer_sdp[:100]}...")
        
        # 返回一个有效的SDP应答
        response = JSONResponse({
            "sdp": answer_sdp,
            "type": "answer"
        })
        
        # 设置CORS响应头
        response.headers["Access-Control-Allow-Origin"] = "*"
        response.headers["Access-Control-Allow-Methods"] = "POST, OPTIONS"
        response.headers["Access-Control-Allow-Headers"] = "Content-Type"
        
        return response
    except Exception as e:
        print(f"处理WebRTC offer错误: {e}")
        return JSONResponse({"error": str(e)}, status_code=500)


# 暴露处理程序
Stream.set_handlers_dict(handlers)

@app.post("/chat/{webrtc_id}")
async def chat_endpoint(webrtc_id: str, request: Request):
    """处理聊天消息，转发到input_hook函数"""
    try:
        # 获取请求体
        data = await request.json()
        
        # 创建新的请求数据，包含webrtc_id和message
        new_data = {
            "webrtc_id": webrtc_id,
            "message": data.get("message", "")
        }
        
        # 打印请求信息以便调试
        print(f"收到/chat/{webrtc_id}请求，消息内容: {new_data}")
        
        # 获取handler实例
        handler = handlers.get(webrtc_id)
        if not handler:
            print(f"未找到webrtc_id={webrtc_id}的handler，创建新的handler")
            handler = QianwenHandler()
            handlers[webrtc_id] = handler
            
            # 为该WebRTC ID创建输出队列
            handler.output_queues[webrtc_id] = asyncio.Queue()
            
            # 启动处理器
            asyncio.create_task(handler.start_up())
        
        # 设置消息
        if new_data["message"]:
            # 直接处理消息
            try:
                response = await handler.process_qianfan(webrtc_id, new_data["message"])
                return JSONResponse({"success": True, "message": "消息已处理", "response": response})
            except Exception as process_error:
                print(f"处理消息时出错: {process_error}")
                return JSONResponse({"error": f"处理消息失败: {str(process_error)}"}, status_code=500)
        else:
            return JSONResponse({"error": "消息为空"}, status_code=400)
    except Exception as e:
        print(f"处理聊天消息错误: {e}")
        return JSONResponse({"error": str(e)}, status_code=500)

@app.get("/stream/{webrtc_id}")
async def stream_endpoint(webrtc_id: str):
    """处理流式事件，转发到outputs函数"""
    print(f"收到/stream/{webrtc_id}请求，转发到/outputs")
    return await outputs(webrtc_id)

def convert_to_html(content):
    # 使用三引号字符串而非f-string来避免语法冲突
    return """
    <!DOCTYPE html>
    <html>
    <head>
        <title>千问AI会话</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f9f9f9;
                display: flex;
                flex-direction: column;
                height: 100vh;
            }
            .container {
                flex-grow: 1;
                display: flex;
                flex-direction: column;
                max-width: 800px;
                margin: 0 auto;
                padding: 20px;
                background-color: white;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }
            .chat-container {
                flex-grow: 1;
                overflow-y: auto;
                margin-bottom: 20px;
                padding: 10px;
                border: 1px solid #e0e0e0;
                border-radius: 8px;
            }
            .message {
                padding: 8px 15px;
                margin-bottom: 10px;
                border-radius: 18px;
                max-width: 80%;
                word-wrap: break-word;
            }
            .ai {
                background-color: #e6f7ff;
                margin-right: auto;
            }
            .user {
                background-color: #dcf8c6;
                margin-left: auto;
            }
            .input-container {
                display: flex;
                gap: 10px;
            }
            #userInput {
                flex-grow: 1;
                padding: 10px;
                border: 1px solid #e0e0e0;
                border-radius: 4px;
            }
            button {
                padding: 10px 15px;
                background-color: #1a73e8;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }
            button:hover {
                background-color: #155db1;
            }
            #recordButton {
                background-color: #4caf50;
                width: 60px;
            }
            #recordButton.recording {
                background-color: #f44336;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>千问AI对话</h1>
            <div class="chat-container" id="chatContainer">
                <div class="message ai">
                    <p>你好！我是基于百度千问大模型的AI助手。请开始我们的对话吧！</p>
                </div>
            </div>
            <div class="input-container">
                <input type="text" id="userInput" placeholder="输入你的问题..." />
                <button id="recordButton">录音</button>
                <button id="sendButton">发送</button>
            </div>
        </div>

        <script>
            let pc;
            let stream;
            let webrtcId;
            
            // 简单的消息显示函数
            function addMessage(text, isAi) {
                const messageDiv = document.createElement('div');
                messageDiv.className = `message ${isAi ? 'ai' : 'user'}`;
                messageDiv.innerHTML = `<p>${text}</p>`;
                document.getElementById('chatContainer').appendChild(messageDiv);
                messageDiv.scrollIntoView();
            }
            
            // 发送按钮处理
            document.getElementById('sendButton').addEventListener('click', () => {
                const userInput = document.getElementById('userInput');
                const text = userInput.value.trim();
                if (text) {
                    addMessage(text, false);
                    // 这里应该有AI回复的逻辑
                    userInput.value = '';
                    
                    // 简单模拟AI回复
                    setTimeout(() => {
                        addMessage("我已经收到你的消息: " + text, true);
                    }, 1000);
                }
            });
            
            // 录音按钮处理
            document.getElementById('recordButton').addEventListener('click', async function() {
                const button = this;
                
                if (!button.classList.contains('recording')) {
                    // 开始录音
                    button.classList.add('recording');
                    button.textContent = '停止';
                    
                    try {
                        // 创建WebRTC连接
                        await initWebRTC();
                    } catch (err) {
                        console.error('WebRTC初始化失败:', err);
                        button.classList.remove('recording');
                        button.textContent = '录音';
                        addMessage("录音初始化失败: " + err.message, true);
                    }
                } else {
                    // 停止录音
                    button.classList.remove('recording');
                    button.textContent = '录音';
                    
                    if (stream) {
                        stream.getTracks().forEach(track => track.stop());
                    }
                    
                    if (pc) {
                        pc.close();
                        pc = null;
                    }
                }
            });
            
            // 初始化WebRTC
            async function initWebRTC() {
                try {
                    // 获取麦克风
                    stream = await navigator.mediaDevices.getUserMedia({ audio: true });
                    
                    // 创建PeerConnection
                    pc = new RTCPeerConnection({
                        iceServers: [
                            { urls: 'stun:stun.l.google.com:19302' }
                        ]
                    });
                    
                    // 添加音频轨道
                    stream.getAudioTracks().forEach(track => {
                        pc.addTrack(track, stream);
                    });
                    
                    // 创建offer
                    const offer = await pc.createOffer();
                    await pc.setLocalDescription(offer);
                    
                    // 发送offer到服务器
                    const response = await fetch('/api/webrtc/offer', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            sdp: pc.localDescription.sdp,
                            type: pc.localDescription.type
                        })
                    });
                    
                    const answerData = await response.json();
                    webrtcId = answerData.webrtc_id;
                    
                    // 设置远程描述
                    await pc.setRemoteDescription({
                        type: 'answer',
                        sdp: answerData.sdp
                    });
                    
                    // 监听ICE候选
                    pc.onicecandidate = event => {
                        if (event.candidate) {
                            console.log('ICE candidate:', event.candidate);
                        }
                    };
                    
                    // 启动SSE监听
                    startEventSource();
                    
                    // 设置语言和类型
                    await setPersonality();
                    
                    addMessage("录音已开始，请对着麦克风说话...", true);
                    
                } catch (err) {
                    console.error('WebRTC设置失败:', err);
                    throw err;
                }
            }
            
            // 开始SSE监听
            function startEventSource() {
                if (!webrtcId) return;
                
                const eventSource = new EventSource(`/api/webrtc/outputs?webrtc_id=${webrtcId}`);
                
                eventSource.onmessage = function(event) {
                    try {
                        const data = JSON.parse(event.data);
                        console.log('收到SSE消息:', data);
                        
                        if (data.text) {
                            addMessage(data.text, true);
                        }
                    } catch (e) {
                        console.error('解析SSE消息失败:', e);
                    }
                };
                
                eventSource.onerror = function(err) {
                    console.error('SSE错误:', err);
                    eventSource.close();
                };
            }
            
            // 设置语言和类型
            async function setPersonality() {
                if (!webrtcId) return;
                
                try {
                    const response = await fetch('/api/webrtc/personality', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            webrtc_id: webrtcId,
                            personality: "1" // 1表示面试官，2表示儿童教师
                        })
                    });
                    
                    const data = await response.json();
                    console.log('设置个性结果:', data);
                } catch (err) {
                    console.error('设置个性失败:', err);
                }
            }
        </script>
    </body>
    </html>
    """

@app.route("/qianwen-voice")
async def qianwen_voice_page():
    return quart.Response(convert_to_html(""), mimetype="text/html")

@app.route("/images/<path:filename>")
async def serve_image(filename):
    try:
        # 处理相对路径的情况
        parent_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        images_dir = os.path.join(parent_dir, "static", "images")
        
        # 确保路径安全
        filename = secure_filename(filename)
        file_path = os.path.join(images_dir, filename)
        
        # 检查文件是否存在
        if not os.path.exists(file_path):
            # 尝试查找其他位置
            alternative_path = os.path.join(parent_dir, "..", "..", "frontend", "src", "main", "resources", "static", "images", filename)
            if os.path.exists(alternative_path):
                file_path = alternative_path
            else:
                return "图片不存在", 404
        
        # 确定MIME类型
        mime_type = "image/jpeg"
        if filename.endswith(".png"):
            mime_type = "image/png"
        elif filename.endswith(".gif"):
            mime_type = "image/gif"
        
        # 读取并返回文件
        with open(file_path, "rb") as f:
            content = f.read()
        
        return quart.Response(content, mimetype=mime_type)
    except Exception as e:
        return "图片获取失败", 500

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=7860) 