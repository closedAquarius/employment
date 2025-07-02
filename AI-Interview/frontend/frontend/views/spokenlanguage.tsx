import React, { useEffect, useState, useRef } from 'react';
import withAuth from '../components/withAuth';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Notification } from '@vaadin/react-components/Notification.js';

// 扩展Window接口，添加silenceCounter属性
declare global {
  interface Window {
    silenceCounter?: number;
  }
}

export const config: ViewConfig = { menu: { order: 5, icon: 'vaadin:chat' }, title: '口语练习' };

const GeminiVoice = () => {
  const [isRecording, setIsRecording] = useState(false);
  const [isConnected, setIsConnected] = useState(false);
  const [isConnecting, setIsConnecting] = useState(false);
  const peerConnectionRef = useRef<RTCPeerConnection | null>(null);
  const boxContainerRef = useRef<HTMLDivElement | null>(null);
  const audioOutputRef = useRef<HTMLAudioElement | null>(null);
  const dataChannelRef = useRef<RTCDataChannel | null>(null);
  const audioContextRef = useRef<AudioContext | null>(null);
  const analyserRef = useRef<AnalyserNode | null>(null);
  const dataArrayRef = useRef<Uint8Array | null>(null);
  const animationIdRef = useRef<number | null>(null);
  const webrtcIdRef = useRef(Math.random().toString(36).substring(7));
  const [language, setLanguage] = useState('英语');
  const [type, setType] = useState('1'); // 1=成人, 2=儿童
  const [voice, setVoice] = useState('Puck');

  // 在组件挂载时创建音频可视化
  useEffect(() => {
    // 创建音频可视化的盒子
    if (boxContainerRef.current) {
      const numBars = 32;
      boxContainerRef.current.innerHTML = '';
      for (let i = 0; i < numBars; i++) {
        const box = document.createElement('div');
        box.className = 'box';
        boxContainerRef.current.appendChild(box);
      }
    }

    return () => {
      // 清理资源
      if (peerConnectionRef.current) {
        peerConnectionRef.current.close();
      }
      if (audioContextRef.current) {
        audioContextRef.current.close();
      }
      if (animationIdRef.current) {
        cancelAnimationFrame(animationIdRef.current);
      }
    };
  }, []);

  // 显示通知
  const showNotification = (message: string, theme = 'error') => {
    Notification.show(message, {
      position: 'middle',
      duration: 3000,
      theme
    });
  };

  // 更新按钮状态
  const updateButtonState = () => {
    if (peerConnectionRef.current && 
      (peerConnectionRef.current.connectionState === 'connecting' || 
       peerConnectionRef.current.connectionState === 'new')) {
      setIsConnecting(true);
      setIsConnected(false);
    } else if (peerConnectionRef.current && peerConnectionRef.current.connectionState === 'connected') {
      setIsConnecting(false);
      setIsConnected(true);
      showNotification("连接成功，现在可以开始对话", "success");
    } else {
      setIsConnecting(false);
      setIsConnected(false);
    }
  };

  // 发送输入钩子
  const sendInputHook = async () => {
    try {
      console.log(`Sending input hook with parameters: language=${language}, type=${type}, voice=${voice}`);
      
      const payload = {
        webrtc_id: webrtcIdRef.current,
        language: language,
        select_type: type,
        voice_name: voice
      };
      
      console.log("Full payload:", payload);
      
      const response = await fetch('/api/webrtc/input_hook', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        console.error('Failed to send input hook:', response.status, errorText);
        showNotification(`配置失败 (${response.status}): ${errorText}`);
        return false;
      }
      
      const responseData = await response.json();
      console.log("Input hook response:", responseData);
      
      if (responseData.status === "error") {
        showNotification(`配置错误: ${responseData.message}`);
        return false;
      }
      
      return true;
    } catch (error: any) {
      console.error('Error sending input hook:', error);
      showNotification(`发送配置时出错: ${error.message}`);
      return false;
    }
  };

  // 设置WebRTC连接
  const setupWebRTC = async () => {
    try {
      // 显示正在连接的通知
      showNotification("正在连接到语音服务...", "primary");
      
      const config: RTCConfiguration = {}; // 使用空对象而不是null
      peerConnectionRef.current = new RTCPeerConnection(config);
      
      // 设置超时警告
      const timeoutId = setTimeout(() => {
        showNotification("连接耗时较长，您是否使用了VPN？", "warning");
      }, 10000);

      // 获取音频流
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      stream.getTracks().forEach(track => 
        peerConnectionRef.current!.addTrack(track, stream)
      );

      // 设置音频可视化
      audioContextRef.current = new AudioContext();
      const analyserInput = audioContextRef.current.createAnalyser();
      const source = audioContextRef.current.createMediaStreamSource(stream);
      source.connect(analyserInput);
      analyserInput.fftSize = 64;
      const dataArrayInput = new Uint8Array(analyserInput.frequencyBinCount);

      // 更新音频电平函数
      const updateAudioLevel = () => {
        analyserInput.getByteFrequencyData(dataArrayInput);
        const average = Array.from(dataArrayInput).reduce((a, b) => a + b, 0) / dataArrayInput.length;
        const audioLevel = average / 255;

        const pulseCircle = document.querySelector('.pulse-circle');
        if (pulseCircle) {
          (pulseCircle as HTMLElement).style.setProperty('--audio-level', (1 + audioLevel).toString());
        }

        animationIdRef.current = requestAnimationFrame(updateAudioLevel);
      };
      updateAudioLevel();

      // 添加连接状态变化监听器
      peerConnectionRef.current.addEventListener('connectionstatechange', () => {
        console.log('connectionstatechange', peerConnectionRef.current!.connectionState);
        if (peerConnectionRef.current!.connectionState === 'connected') {
          clearTimeout(timeoutId);
        }
        updateButtonState();
      });

      // 处理传入的音频
      peerConnectionRef.current.addEventListener('track', (evt) => {
        if (audioOutputRef.current && audioOutputRef.current.srcObject !== evt.streams[0]) {
          audioOutputRef.current.srcObject = evt.streams[0];
          audioOutputRef.current.play();

          // 设置输出音频的可视化
          audioContextRef.current = new AudioContext();
          analyserRef.current = audioContextRef.current.createAnalyser();
          const source = audioContextRef.current.createMediaStreamSource(evt.streams[0]);
          source.connect(analyserRef.current);
          analyserRef.current.fftSize = 2048;
          dataArrayRef.current = new Uint8Array(analyserRef.current.frequencyBinCount);
          
          // 添加静音检测逻辑
          const silenceDetectionInterval = setInterval(() => {
            if (!analyserRef.current || !dataArrayRef.current) return;
            
            analyserRef.current.getByteFrequencyData(dataArrayRef.current);
            const average = Array.from(dataArrayRef.current).reduce((a, b) => a + b, 0) / dataArrayRef.current.length;
            
            // 如果平均值非常低，可能是静音帧
            if (average < 1.0) {
              // 静音计数，连续5次检测到静音才显示提示
              if (!window.silenceCounter) window.silenceCounter = 0;
              window.silenceCounter++;
              
              if (window.silenceCounter > 5) {
                clearInterval(silenceDetectionInterval);
                showNotification("连接到语音服务失败，请检查网络后重试", "warning");
                setTimeout(() => {
                  stopWebRTC();
                }, 2000);
              }
            } else {
              // 重置静音计数器
              window.silenceCounter = 0;
            }
          }, 500);
          
          updateVisualization();
        }
      });

      // 创建数据通道用于消息传递
      dataChannelRef.current = peerConnectionRef.current.createDataChannel('text');
      dataChannelRef.current.onmessage = (event) => {
        try {
          const eventJson = JSON.parse(event.data);
          if (eventJson.type === "error") {
            showNotification(eventJson.message);
          } else if (eventJson.type === "send_input") {
            sendInputHook();
          }
        } catch (error) {
          console.error("Error parsing data channel message:", error);
        }
      };

      // 创建并发送offer
      const offer = await peerConnectionRef.current.createOffer();
      await peerConnectionRef.current.setLocalDescription(offer);

      // 等待ICE收集完成
      await new Promise<void>((resolve) => {
        if (peerConnectionRef.current!.iceGatheringState === "complete") {
          resolve();
        } else {
          const checkState = () => {
            if (peerConnectionRef.current!.iceGatheringState === "complete") {
              peerConnectionRef.current!.removeEventListener("icegatheringstatechange", checkState);
              resolve();
            }
          };
          peerConnectionRef.current!.addEventListener("icegatheringstatechange", checkState);
        }
      });

      console.log("Sending WebRTC offer");
      
      // 发送offer到后端
      const response = await fetch('/api/webrtc/offer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sdp: peerConnectionRef.current.localDescription!.sdp,
          type: peerConnectionRef.current.localDescription!.type,
          webrtc_id: webrtcIdRef.current,
        })
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`HTTP error! Status: ${response.status}, Response: ${errorText}`);
      }

      let serverResponse;
      try {
        const serverResponseText = await response.text();
        console.log("Raw server response:", serverResponseText);
        serverResponse = JSON.parse(serverResponseText);
      } catch (parseError: any) {
        console.error("Failed to parse response:", parseError);
        throw new Error(`无法解析响应: ${parseError.message}`);
      }

      if (serverResponse.status === 'failed') {
        showNotification(serverResponse.meta.error === 'concurrency_limit_reached'
          ? `连接过多，最大限制为${serverResponse.meta.limit}`
          : serverResponse.meta.error);
        stopWebRTC();
        return;
      }

      await peerConnectionRef.current.setRemoteDescription(serverResponse);
      
      // 成功连接后发送配置
      const hookSuccess = await sendInputHook();
      if (!hookSuccess) {
        throw new Error("无法发送配置参数");
      }
      
      setIsRecording(true);
    } catch (err: any) {
      console.error('Error setting up WebRTC:', err);
      showNotification(`建立连接失败: ${err.message}`);
      stopWebRTC();
    }
  };

  // 更新可视化
  const updateVisualization = () => {
    if (!analyserRef.current) return;

    analyserRef.current.getByteFrequencyData(dataArrayRef.current);
    const bars = document.querySelectorAll('.box');

    for (let i = 0; i < bars.length; i++) {
      const barHeight = (dataArrayRef.current[i] / 255) * 2;
      bars[i].style.transform = `scaleY(${Math.max(0.1, barHeight)})`;
    }

    animationIdRef.current = requestAnimationFrame(updateVisualization);
  };

  // 停止WebRTC连接
  const stopWebRTC = () => {
    if (peerConnectionRef.current) {
      peerConnectionRef.current.close();
      peerConnectionRef.current = null;
    }
    if (animationIdRef.current) {
      cancelAnimationFrame(animationIdRef.current);
    }
    if (audioContextRef.current) {
      audioContextRef.current.close();
    }
    setIsRecording(false);
    updateButtonState();
  };

  return (
    <div style={{ 
      display: 'flex', 
      flexDirection: 'column', 
      alignItems: 'center', 
      justifyContent: 'center',
      height: '100vh',
      backgroundColor: '#ffffff'
    }}>
      <style>
        {`
          :root {
            --color-accent: #6366f1;
            --color-background: #ffffff;
            --color-surface: #1e293b;
            --color-text: #e2e8f0;
            --boxSize: 8px;
            --gutter: 4px;
          }
          
          .container {
            width: 90%;
            max-width: 800px;
            background-color: var(--color-surface);
            padding: 2rem;
            border-radius: 1rem;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
          }
          
          .wave-container {
            position: relative;
            display: flex;
            min-height: 100px;
            max-height: 128px;
            justify-content: center;
            align-items: center;
            margin: 2rem 0;
          }
          
          .box-container {
            display: flex;
            justify-content: space-between;
            height: 64px;
            width: 100%;
          }
          
          .box {
            height: 100%;
            width: var(--boxSize);
            background: var(--color-accent);
            border-radius: 8px;
            transition: transform 0.05s ease;
          }
          
          .controls {
            display: grid;
            gap: 1rem;
            margin-bottom: 2rem;
          }
          
          .input-group {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
          }
          
          label {
            font-size: 0.875rem;
            font-weight: 500;
            color: var(--color-text);
          }
          
          select {
            padding: 0.75rem;
            border-radius: 0.5rem;
            border: 1px solid rgba(255, 255, 255, 0.1);
            background-color: #0f172a;
            color: var(--color-text);
            font-size: 1rem;
          }
          
          button {
            padding: 1rem 2rem;
            border-radius: 0.5rem;
            border: none;
            background-color: var(--color-accent);
            color: white;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s ease;
          }
          
          button:hover {
            opacity: 0.9;
            transform: translateY(-1px);
          }
          
          .icon-with-spinner {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 12px;
            min-width: 180px;
          }
          
          .spinner {
            width: 20px;
            height: 20px;
            border: 2px solid white;
            border-top-color: transparent;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            flex-shrink: 0;
          }
          
          @keyframes spin {
            to {
              transform: rotate(360deg);
            }
          }
          
          .pulse-container {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 12px;
            min-width: 180px;
          }
          
          .pulse-circle {
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background-color: white;
            opacity: 0.2;
            flex-shrink: 0;
            transform: translateX(-0%) scale(var(--audio-level, 1));
            transition: transform 0.1s ease;
          }
        `}
      </style>
      
      <div style={{ textAlign: 'center', color: '#374151' }}>
        <h1 style={{ fontWeight: 600, marginBottom: '0.5rem' }}>口语练习</h1>
        <p style={{ marginTop: 0, opacity: 0.8 }}>口语实时对话</p>
      </div>
      
      <div className="container">
        <div className="controls">
          <div className="input-group">
            <label htmlFor="language">语言</label>
            <select 
              id="language" 
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
            >
              <option value="英语">英语</option>
              <option value="日语">日语</option>
            </select>
          </div>
          
          <div className="input-group">
            <label htmlFor="type">类型</label>
            <select 
              id="type"
              value={type}
              onChange={(e) => setType(e.target.value)}
            >
              <option value="1">成人</option>
              <option value="2">儿童</option>
            </select>
          </div>
          
          <div className="input-group">
            <label htmlFor="voice">声音</label>
            <select 
              id="voice"
              value={voice}
              onChange={(e) => setVoice(e.target.value)}
            >
              <option value="Puck">Puck</option>
              <option value="Charon">Charon</option>
              <option value="Kore">Kore</option>
              <option value="Fenrir">Fenrir</option>
              <option value="Aoede">Aoede</option>
            </select>
          </div>
        </div>

        <div className="wave-container">
          <div className="box-container" ref={boxContainerRef}>
            {/* 动态添加的音频可视化盒子 */}
          </div>
        </div>

        <button 
          onClick={isConnected ? stopWebRTC : setupWebRTC}
          disabled={isConnecting}
        >
          {isConnecting ? (
            <div className="icon-with-spinner">
              <div className="spinner"></div>
              <span>连接中...</span>
            </div>
          ) : isConnected ? (
            <div className="pulse-container">
              <div className="pulse-circle"></div>
              <span>结束会话</span>
            </div>
          ) : (
            '开始会话'
          )}
        </button>
      </div>

      <audio ref={audioOutputRef} style={{ display: 'none' }}></audio>
    </div>
  );
};

export default withAuth(GeminiVoice);