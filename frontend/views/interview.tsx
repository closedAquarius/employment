import React, { useState, useRef, useEffect } from 'react';
import Lottie from 'lottie-react';
import {AssistantService, ClientService} from "Frontend/generated/endpoints";
import {nanoid} from "nanoid";
import { ClipLoader } from 'react-spinners';
import { motion } from 'framer-motion';
import talkingAnimation from 'Frontend/assets/animations/talking-animation.json'; // 从 LottieFiles 下载的动画文件
import thinkingAnimation from 'Frontend/assets/animations/think-animation.json'; // 从 LottieFiles 下载的动画文件
import { FaMicrophone, FaStop, FaPaperPlane } from 'react-icons/fa';

export const config: ViewConfig = { menu: { order: 1, icon: 'line-awesome/svg/file.svg' }, title: '光哥面试' };

const AudioRecorder = () => {
  const [isWelcomePlaying, setIsWelcomePlaying] = useState(false); // 是否正在播放欢迎语音
  const [showStartButton, setShowStartButton] = useState(true); // 是否显示“开始面试”按钮
  const [showAnswerButton, setShowAnswerButton] = useState(false); // 是否显示“回答”按钮
  const [isRecording, setIsRecording] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [audioBlob, setAudioBlob] = useState(null);
  const [audioUrl, setAudioUrl] = useState('');
  const [mediaRecorder, setMediaRecorder] = useState(null);
  const [chatId, setChatId] = useState(nanoid());
  const lottieRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(false);

  // 播放欢迎语音
  const playWelcomeAudio = async () => {
    setShowStartButton(false); // 隐藏“开始面试”按钮
    setIsWelcomePlaying(true); // 标记正在播放欢迎语音
    try {
      const response = await fetch('http://localhost:8080/interview/welcomemp3', {
        method: 'Get'
      });

      const responseBlob = await response.blob();
      const audioUrl = URL.createObjectURL(responseBlob);
      setAudioUrl(audioUrl);
      setIsProcessing(false);
      playAudio(audioUrl);
    } catch (error) {
      console.error('Error sending audio to backend:', error);
      setIsProcessing(false);
    }
  };

  // 开始录音
  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mediaRecorder = new MediaRecorder(stream);
      setMediaRecorder(mediaRecorder);

      const audioChunks = [];
      mediaRecorder.ondataavailable = (event) => {
        audioChunks.push(event.data);
      };

      mediaRecorder.onstop = () => {
        const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
        setAudioBlob(audioBlob);
        sendAudioToBackend(audioBlob);
      };

      mediaRecorder.start();
      setIsRecording(true);
    } catch (error) {
      console.error('Error accessing microphone:', error);
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
  const sendAudioToBackend = async (audioBlob) => {
    const formData = new FormData();
    formData.append('chatId', chatId);
    formData.append('audio', audioBlob, 'recording.webm');

    try {
      const response = await fetch('http://localhost:8080/interview/face2faceChat', {
        method: 'POST',
        body: formData,
      });

      const responseBlob = await response.blob();
      const audioUrl = URL.createObjectURL(responseBlob);
      setAudioUrl(audioUrl);
      setIsProcessing(false);
      playAudio(audioUrl);
    } catch (error) {
      console.error('Error sending audio to backend:', error);
      setIsProcessing(false);
    }
  };

  // 播放音频
  const playAudio = (audioUrl) => {
    const audio = new Audio(audioUrl);
    audio.play();
    setIsPlaying(true);

    // 同步动画
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const analyser = audioContext.createAnalyser();
    const source = audioContext.createMediaElementSource(audio);
    source.connect(analyser);
    analyser.connect(audioContext.destination);

    const dataArray = new Uint8Array(analyser.frequencyBinCount);

    const animate = () => {
      if (!isPlaying) return;

      analyser.getByteFrequencyData(dataArray);
      const average = dataArray.reduce((sum, value) => sum + value, 0) / dataArray.length;

      // 根据音频波形调整动画速度
      if (lottieRef.current) {
        lottieRef.current.setSpeed(average / 50 + 1);
      }

      requestAnimationFrame(animate);
    };

    animate();

    audio.onended = () => {
      setIsPlaying(false);
      setIsRecording(false);
      setIsWelcomePlaying(false);
      setShowAnswerButton(true); // 显示“回答”按钮
    };
  };

  return (
    <div style={{ textAlign: 'center',
                  position: 'fixed',
                  top: '50%',
                  left: '50%',
                  transform: 'translate(-50%, -50%)' }}>
          {/* 开始面试按钮 */}
          {showStartButton && (
            <motion.button
              onClick={playWelcomeAudio}
              style={{
                padding: '15px 30px',
                fontSize: '18px',
                borderRadius: '10px',
                border: 'none',
                backgroundColor: '#4CAF50',
                color: 'white',
                cursor: 'pointer',
              }}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
            >
              开始面试
            </motion.button>
          )}

          {/* 按钮 */}
          {showAnswerButton && !isProcessing && (
          <motion.button
            onClick={isRecording ? stopRecording : startRecording}
            disabled={isProcessing || isPlaying}
            style={{
              padding: '15px 30px',
              fontSize: '18px',
              borderRadius: '10px',
              border: 'none',
              backgroundColor: isRecording ? '#ff4d4d' : '#4CAF50',
              color: 'white',
              cursor: isProcessing || isPlaying ? 'not-allowed' : 'pointer',
              position: 'relative',
              zIndex: 1, // 按钮在底层
            }}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
          >
            {isProcessing ? '思考中...' : isRecording ? '回答完毕' : '回答'}
          </motion.button>
          )}

          {/* 加载动画 */}
          {isProcessing && (
            <div
              style={{
                position: 'fixed',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                zIndex: 2, // 动画小人在上层
                pointerEvents: 'none', // 禁止点击
              }}
            >
              <Lottie
                lottieRef={lottieRef}
                animationData={thinkingAnimation}
                loop={true}
                style={{ width: '600px', height: '400px' }} // 调整动画大小
              />
               <div
               style={{
                 marginTop: '10px',
                 fontSize: '20px',
                 fontWeight: 'bold',
                 color: '#333',
               }}
             >
               正在思考您的回答...
             </div>
            </div>
          )}

          {/* 动画小人 */}
          {isPlaying && (
            <div
              style={{
                position: 'fixed',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                zIndex: 2, // 动画小人在上层
                pointerEvents: 'none', // 禁止点击
              }}
            >
              <Lottie
                lottieRef={lottieRef}
                animationData={talkingAnimation}
                loop={true}
                style={{ width: '600px', height: '400px' }} // 调整动画大小
              />
               <div
               style={{
                 marginTop: '10px',
                 fontSize: '20px',
                 fontWeight: 'bold',
                 color: '#333',
               }}
             >
               面试官光哥正在回答...
             </div>
            </div>
          )}

          {/* 播放完成提示 */}
          {audioUrl && !isPlaying && (
            <motion.div
              style={{ marginTop: '20px' }}
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}
            >
              <p style={{ fontSize: '18px' }}>回答完毕！</p>
            </motion.div>
          )}
        </div>
  );
};

export default AudioRecorder;