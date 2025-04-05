import React, { useState, useEffect, useRef } from 'react';
import './WebRtcChat.css';
import { Notification } from '@vaadin/react-components/Notification.js';
import withAuth from 'Frontend/components/withAuth';

export const config: ViewConfig = { menu: { order: 6, icon: 'vaadin:bold' }, title: '老板面试' };

function talkToAzure() {
  const [personality, setPersonality] = useState(null); // 1-董明珠 2-雷军
  const [messages, setMessages] = useState([]);
  const [connectionState, setConnectionState] = useState('disconnected');
  const [audioLevel, setAudioLevel] = useState(0);
  const [bars, setBars] = useState([]);

  const peerConnectionRef = useRef(null);
  const audioOutputRef = useRef(null);
  const eventSourceRef = useRef(null);
  const boxContainerRef = useRef(null);
  const audioContextRef = useRef(null);
  const analyserRef = useRef(null);
  const animationIdRef = useRef(null);
  const dataArrayRef = useRef(new Uint8Array(32));

  // 人格图片配置
  const PERSONALITIES = [
    {
      id: 1,
      name: '董明珠',
      image: '/images/dongmingzhu.jpg',
      style: { borderColor: '#e74c3c' },
      description: '格力式高压面试，注重细节与执行力'
    },
    {
      id: 2,
      name: '雷军',
      image: '/images/leijun.jpg',
      style: { borderColor: '#3498db' },
      description: '小米方法论，极致性价比思维'
    },
    {
      id: 3,
      name: '埃隆·马斯克',
      image: '/images/elonmusk.jpg',
      style: { borderColor: '#00ff00' },
      description: '第一性原理拷问，火星殖民思维'
    },
    {
      id: 4,
      name: '唐纳德·特朗普',
      image: '/images/trump.jpg',
      style: {
        borderColor: '#ff0000',
        boxShadow: '0 0 10px gold'
      },
      description: '《学徒》式真人秀，商业谈判高手'
    }
  ];

  // Initialize audio bars
  useEffect(() => {
    const numBars = 32;
    const newBars = Array(numBars).fill(0).map((_, i) => (
      <div
        key={i}
        className="box"
        style={{
          '--boxSize': `${100 / numBars}%`,
          transform: 'scaleY(0.1)'
        }}
      />
    ));
    setBars(newBars);

    return () => {
      if (animationIdRef.current) {
        cancelAnimationFrame(animationIdRef.current);
      }
    };
  }, []);

  // Audio visualization update
  const updateVisualization = () => {
    if (!analyserRef.current || !boxContainerRef.current) return;

    analyserRef.current.getByteFrequencyData(dataArrayRef.current);
    const boxes = boxContainerRef.current.querySelectorAll('.box');

    boxes.forEach((box, i) => {
      const barHeight = (dataArrayRef.current[i] / 255) * 2;
      box.style.transform = `scaleY(${Math.max(0.1, barHeight)})`;
    });

    // Calculate overall audio level for the pulse indicator
    const average = dataArrayRef.current.reduce((a, b) => a + b, 0) / dataArrayRef.current.length;
    setAudioLevel(average / 255);

    animationIdRef.current = requestAnimationFrame(updateVisualization);
  };

  // Initialize audio context and analyzer
  const initAudioContext = (stream) => {
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const analyser = audioContext.createAnalyser();
    const audioSource = audioContext.createMediaStreamSource(stream);

    analyser.fftSize = 64;
    audioSource.connect(analyser);
    dataArrayRef.current = new Uint8Array(analyser.frequencyBinCount);

    audioContextRef.current = audioContext;
    analyserRef.current = analyser;

    // Start visualization
    animationIdRef.current = requestAnimationFrame(updateVisualization);
  };

  // Show error message
  const showError = (message) => {
    console.error('Error:', message);
    // You could implement a toast notification here
  };

  // Establish WebRTC connection
  const startConnection = async () => {
    if (!personality) {
        Notification.show('请先选择面试官人格', { theme: "error" });
        return;
    }
    const webrtc_id = Math.random().toString(36).substring(7);
    try {
      setConnectionState('connecting');

      // 1. Get user media stream
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      initAudioContext(stream);

      // 2. Create PeerConnection
      const peerConnection = new RTCPeerConnection({
        iceServers: [
          { urls: 'stun:stun.l.google.com:19302' }
          // Add TURN servers if needed
        ]
      });
      peerConnectionRef.current = peerConnection;

      // 3. Add audio tracks
      stream.getTracks().forEach(track => peerConnection.addTrack(track, stream));

      // 4. Handle remote stream
      peerConnection.ontrack = (event) => {
        if (audioOutputRef.current.srcObject !== event.streams[0]) {
          audioOutputRef.current.srcObject = event.streams[0];
          audioOutputRef.current.play().catch(e => console.error('Audio play failed:', e));
        }
      };

      // 5. Create data channel for error messages
      const dataChannel = peerConnection.createDataChannel('text');
      dataChannel.onmessage = (event) => {
        const eventJson = JSON.parse(event.data);
        if (eventJson.type === "error") {
          showError(eventJson.message);
        }else if (eventJson.type === "send_input") {
           fetch('/api/webrtc/personality', {
                 method: 'POST',
                 headers: { 'Content-Type': 'application/json' },
                 body: JSON.stringify({
                     webrtc_id: webrtc_id,
                     personality: personality.toString()
                 })
            });
         }
      };

      // 6. Create and send offer
      const offer = await peerConnection.createOffer();
      await peerConnection.setLocalDescription(offer);

      const response = await fetch('/api/webrtc/offer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sdp: offer.sdp,
          type: offer.type,
          webrtc_id
        })
      });

      // 7. Set remote description
      const answer = await response.json();
      await peerConnection.setRemoteDescription(answer);

      // 8. Establish SSE connection
      eventSourceRef.current = new EventSource(`/api/webrtc/outputs?webrtc_id=${webrtc_id}`);
      eventSourceRef.current.addEventListener('output', (event) => {
        const data = JSON.parse(event.data);
        setMessages(prev => [...prev, { role: 'assistant', content: data.content }]);
      });

      setConnectionState('connected');
    } catch (error) {
      console.error('Connection failed:', error);
      setConnectionState('error');
      stopConnection();
    }
  };

  // Close connection
  const stopConnection = () => {
    if (peerConnectionRef.current) {
      peerConnectionRef.current.close();
      peerConnectionRef.current = null;
    }
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }
    if (animationIdRef.current) {
      cancelAnimationFrame(animationIdRef.current);
      animationIdRef.current = null;
    }
    if (audioContextRef.current) {
      audioContextRef.current.close();
      audioContextRef.current = null;
    }
    setConnectionState('disconnected');
  };

  const handlePersonalitySelect = async (selectedId) => {
    setPersonality(selectedId);

    const selectedPerson = PERSONALITIES.find(p => p.id === selectedId);
    if (selectedId === 1) {
      Notification.show(`❄ 进入${selectedPerson.name}模式: 格力式压力面试准备就绪，请证明你的价值！`);
    } else if (selectedId === 2) {
       Notification.show(`🚗 进入${selectedPerson.name}模式: 性价比评估模式启动，请准备数据化回答！`);
    } else if (selectedId === 3) {
      Notification.show(`🚀 进入${selectedPerson.name}模式: 准备接受物理学拷问!`);
    } else if (selectedId === 4) {
      Notification.show(`👑 进入${selectedPerson.name}模式激活: 这将是史上最棒的面试!`);
    }
  }

  return (
    <div className="container">
      <div className="personality-selector">
        <h3>选择面试官风格：</h3>
        <div className="personality-options">
          {PERSONALITIES.map((p) => (
            <div
              key={p.id}
              className={`personality-card ${personality === p.id ? 'active' : ''}`}
              style={p.style}
              onClick={() => handlePersonalitySelect(p.id)}
              title={p.description}
            >
              <img src={p.image} alt={p.name} />
              <span>{p.name}</span>
              {personality === p.id && (
                <div className="selection-badge">✓</div>
              )}
            </div>
          ))}
        </div>
      </div>

      <div className="wave-container">
        <div className="box-container" ref={boxContainerRef}>
          {bars}
        </div>
      </div>

      <button onClick={connectionState === 'connected' ? stopConnection : startConnection}>
        {connectionState === 'connecting' ? (
          <div className="spinner">连接...</div>
        ) : connectionState === 'connected' ? (
          <div className="pulse-indicator" style={{ '--audio-level': 1 + audioLevel }}>
            停止
          </div>
        ) : (
          personality === 4 ? '开始 (相信我!)' : '开始'
        )}
      </button>

      <audio ref={audioOutputRef} hidden />
    </div>
  );
}

export default withAuth(talkToAzure);