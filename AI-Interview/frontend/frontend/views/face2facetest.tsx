import React, { useState, useRef, useEffect } from 'react';
import { nanoid } from "nanoid";
import { ClipLoader } from 'react-spinners';
import { motion } from 'framer-motion';
import { FaMicrophone, FaStop, FaPaperPlane } from 'react-icons/fa';
import FaceVerificationDialog from './FaceVerificationDialog';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Notification } from '@vaadin/react-components/Notification.js';

export const config: ViewConfig = { menu: { order: 1, icon: 'vaadin:users' }, title: '智联面试' };

const AudioRecorder = () => {
  const [showStartButton, setShowStartButton] = useState(true); // 是否显示"开始面试"按钮
  const [showAnswerButton, setShowAnswerButton] = useState(false); // 是否显示"回答"按钮
  const [isRecording, setIsRecording] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [audioBlob, setAudioBlob] = useState<Blob | null>(null);
  const [audioUrl, setAudioUrl] = useState('');
  const [mediaRecorder, setMediaRecorder] = useState<MediaRecorder | null>(null);
  const [chatId, setChatId] = useState(nanoid());
  const lottieRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);
  const [isFaceVerified, setIsFaceVerified] = useState(false); // 人脸识别状态
  const [userName, setUserName] = useState('');
  const [userId, setUserId] = useState('');
  const [token, setToken] = useState('');

  useEffect(() => {
    // 确保获取到最新的用户信息
    const storedUserName = localStorage.getItem('username');
    const storedUserId = localStorage.getItem('userId');
    const storedToken = localStorage.getItem('token');
    
    if (storedUserName) {
      setUserName(storedUserName);
      console.log("从本地存储获取用户名:", storedUserName);
    } else {
      setUserName('张三'); // 使用默认用户名
      console.log("未找到用户名，使用默认值: 张三");
    }
    
    if (storedUserId) {
      setUserId(storedUserId);
      console.log("从本地存储获取用户ID:", storedUserId);
    } else {
      setUserId('1'); // 使用默认用户ID
      console.log("未找到用户ID，使用默认值: 1");
    }
    
    if (storedToken) {
      setToken(storedToken);
      console.log("从本地存储获取认证令牌");
    } else {
      console.warn("未找到认证令牌");
    }
  }, []);

  const handleVerificationSuccess = () => {
    setIsFaceVerified(true); // 人脸识别成功后启用开始按钮
  };

  // 显示通知
  const showNotification = (message: string, theme = 'error') => {
    Notification.show(message, {
      position: 'middle',
      duration: 3000,
      theme
    });
  };

  // 播放欢迎语音
  const playWelcomeAudio = async () => {
    if (!isFaceVerified) {
      showNotification("请先完成人脸验证");
      return;
    }
    
    if (!userName) {
      showNotification("未获取到用户名，请重新登录");
      return;
    }

    setShowStartButton(false); // 隐藏"开始面试"按钮
    setIsRecording(false);
    setIsProcessing(true);

    const formData = new FormData();
    formData.append('chatId', chatId);
    formData.append('userName', userName);
    if (userId) {
      formData.append('userId', userId);
    }

    try {
      console.log(`Starting interview with userName: ${userName}, userId: ${userId || 'undefined'}`);
      const response = await fetch(`/interview/face2faceChat`, {
        method: 'POST',
        headers: token ? {
          'Authorization': `Bearer ${token}`
        } : {},
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const responseBlob = await response.blob();
      const audioUrl = URL.createObjectURL(responseBlob);

      // 获取状态信息
      const status = response.headers.get('X-Chat-Status');
      const completed = status === 'completed';
      setIsCompleted(completed);

      setAudioUrl(audioUrl);
      setIsProcessing(false);
      playAudio(audioUrl);
      
      // 显示回答按钮
      setShowAnswerButton(true);
    } catch (error: any) {
      console.error('Error sending audio to backend:', error);
      showNotification(`开始面试失败: ${error.message}`);
      setIsProcessing(false);
      setShowStartButton(true); // 重新显示开始按钮
    }
  };

  // 开始录音
  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const recorder = new MediaRecorder(stream);
      setMediaRecorder(recorder);

      const audioChunks: Blob[] = [];
      recorder.ondataavailable = (event) => {
        audioChunks.push(event.data);
      };

      recorder.onstop = () => {
        const blob = new Blob(audioChunks, { type: 'audio/wav' });
        setAudioBlob(blob);
        sendAudioToBackend(blob);
      };

      recorder.start();
      setIsRecording(true);
    } catch (error: any) {
      console.error('Error accessing microphone:', error);
      showNotification(`无法访问麦克风: ${error.message}`);
    }
  };

  // 停止录音并发送到后端
  const stopRecording = () => {
    if (mediaRecorder) {
      mediaRecorder.stop();
      setIsRecording(false);
      setIsProcessing(true);
    }
  };

  // 发送音频到后端
  const sendAudioToBackend = async (blob: Blob) => {
    const formData = new FormData();
    formData.append('chatId', chatId);
    formData.append('userName', userName);
    if (userId) {
      formData.append('userId', userId);
    }
    formData.append('audio', blob, 'recording.webm');

    try {
      console.log(`Sending audio with userName: ${userName}, userId: ${userId || 'undefined'}`);
      const response = await fetch(`/interview/face2faceChat`, {
        method: 'POST',
        headers: token ? {
          'Authorization': `Bearer ${token}`
        } : {},
        body: formData,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const responseBlob = await response.blob();
      const audioUrl = URL.createObjectURL(responseBlob);

      // 获取状态信息
      const status = response.headers.get('X-Chat-Status');
      const completed = status === 'completed';
      setIsCompleted(completed);

      setAudioUrl(audioUrl);
      setIsProcessing(false);
      playAudio(audioUrl);
    } catch (error: any) {
      console.error('Error sending audio to backend:', error);
      showNotification(`发送音频失败: ${error.message}`);
      setIsProcessing(false);
      setShowAnswerButton(true); // 重新显示回答按钮
    }
  };

  // 播放音频
  const playAudio = (url) => {
    const audio = new Audio(url);
    audio.play().catch(error => {
      console.error('Error playing audio:', error);
      showNotification(`播放音频失败: ${error.message}`);
    });
    setIsPlaying(true);

    audio.onended = () => {
      setIsPlaying(false);
      setIsRecording(false);
      setShowAnswerButton(true); // 显示"回答"按钮
      if (!isCompleted) {
        // 不要自动开始录音，等待用户点击回答按钮
        // startRecording();
      }
    };
  };

  return (
  <div>
   {/* 人脸识别对话框 */}
   {!isFaceVerified && <FaceVerificationDialog onVerificationSuccess={handleVerificationSuccess} />}
   <div style={{ textAlign: 'center',
                    position: 'fixed',
                    top: '20%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)' }}>
        {/* 开始面试按钮 */}
      {showStartButton && (
        <motion.button
          onClick={playWelcomeAudio}
          disabled={!isFaceVerified}
          style={{
            padding: '15px 30px',
            fontSize: '18px',
            borderRadius: '10px',
            border: 'none',
            backgroundColor: isFaceVerified ? '#4CAF50' : '#cccccc',
            color: 'white',
            cursor: isFaceVerified ? 'pointer' : 'not-allowed',
          }}
          whileHover={{ scale: isFaceVerified ? 1.1 : 1 }}
          whileTap={{ scale: isFaceVerified ? 0.9 : 1 }}
        >
          开始面试
        </motion.button>
      )}

      {/* 回答按钮 */}
        {showAnswerButton && !isProcessing && !isCompleted && !isPlaying &&(
        <motion.button
          onClick={isRecording ? stopRecording : startRecording}
          style={{
            padding: '15px 30px',
            fontSize: '18px',
            borderRadius: '10px',
            border: 'none',
            backgroundColor: isRecording ? '#ff4d4d' : '#4CAF50',
            color: 'white',
            cursor: 'pointer',
            position: 'relative',
            zIndex: 20, // 按钮在上层
          }}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
        >
          {isRecording ? '回答完毕' : '回答'}
        </motion.button>
        )}
    </div>

    <div style={{ textAlign: 'center',
                  position: 'fixed',
                  top: '50%',
                  left: '50%',
                  transform: 'translate(-50%, -50%)' }}>

        {/* 加载动画 */}
        {isRecording && !isCompleted && (
          <div
            style={{
              position: 'fixed',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              pointerEvents: 'none', // 禁止点击
              zIndex: 10, // 动画在底层
            }}
          >
            <div style={{ fontSize: '24px', color: '#4CAF50' }}>
              正在录音...
            </div>
          </div>
        )}

        {/* 处理中动画 */}
        {isProcessing && (
          <div
            style={{
              position: 'fixed',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: 10,
            }}
          >
            <ClipLoader color="#4CAF50" size={50} />
            <div style={{ marginTop: '20px', fontSize: '18px', color: '#4CAF50' }}>
              处理中...
            </div>
          </div>
        )}

        {/* 播放中动画 */}
        {isPlaying && (
          <div
            style={{
              position: 'fixed',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: 10,
            }}
          >
            <div style={{ fontSize: '24px', color: '#4CAF50' }}>
              面试官正在说话...
            </div>
          </div>
        )}

        {/* 完成提示 */}
        {isCompleted && (
          <div
            style={{
              position: 'fixed',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: 10,
            }}
          >
            <div style={{ fontSize: '24px', color: '#4CAF50' }}>
              面试已结束，谢谢参与！
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AudioRecorder;