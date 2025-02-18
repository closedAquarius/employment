// hooks/useVoiceRecorder.ts
import { useEffect, useRef, useState } from 'react';

type AudioChunk = {
  data: Int16Array;
  timestamp: number;
  vadScore: number;
};

export default function useVoiceRecorder() {
  const [isRecording, setIsRecording] = useState(false);
  const audioContextRef = useRef<AudioContext>();
  const processorRef = useRef<ScriptProcessorNode>();
  const audioBufferRef = useRef<number[]>([]);
  const wsRef = useRef<WebSocket>();
  const silenceTimerRef = useRef<NodeJS.Timeout>();
  const noiseLevelRef = useRef(0.02);

  // 核心录音逻辑
  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: {
        sampleRate: 16000,
        channelCount: 1,
        echoCancellation: true
      }});

      const audioContext = new AudioContext({ sampleRate: 16000 });
      const source = audioContext.createMediaStreamSource(stream);
      const processor = audioContext.createScriptProcessor(2048, 1, 1);

      processor.onaudioprocess = (e) => {
        const inputData = e.inputBuffer.getChannelData(0);
        const { isVoice, score } = vadDetection(inputData);

        handleAudioProcess(inputData, isVoice, score);
      };

      source.connect(processor);
      processor.connect(audioContext.destination);

      audioContextRef.current = audioContext;
      processorRef.current = processor;
      setIsRecording(true);

      // 环境噪声校准
      //calibrateNoise(stream);
    } catch (error) {
      console.error('录音启动失败:', error);
    }
  };


    const calculateEnergy = (buffer: Float32Array): number => {
      // 方法1：计算平均绝对值能量（更高效）
      let sum = 0;
      for (let i = 0; i < buffer.length; i++) {
        sum += Math.abs(buffer[i]);
      }
      return sum / buffer.length;

      // 方法2：计算RMS能量（更精确但稍耗性能）
      // let sumSquares = 0;
      // for (let i = 0; i < buffer.length; i++) {
      //   sumSquares += buffer[i] * buffer[i];
      // }
      // return Math.sqrt(sumSquares / buffer.length);
    };

    const calculateZeroCross = (buffer: Float32Array): number => {
      let count = 0;
      for (let i = 1; i < buffer.length; i++) {
        // 检测符号变化：当前样本与前一个样本相乘为负数
        if (buffer[i] * buffer[i-1] < 0) {
          count++;
        }
      }
      return count;
    };

    // 在VAD检测中使用
    const vadDetection = (buffer: Float32Array) => {
      const energy = calculateEnergy(buffer);      // 能量值 0~1
      const zeroCross = calculateZeroCross(buffer); // 过零次数

      // 动态阈值计算（示例值需实际校准）
      const energyThreshold = noiseLevelRef.current * 1.8;
      const zcrThreshold = 60;

      return {
        isVoice: energy > energyThreshold && zeroCross < zcrThreshold,
        score: energy
      };
    };

  // 音频处理逻辑
  const handleAudioProcess = (
    inputData: Float32Array,
    isVoice: boolean,
    score: number
  ) => {
    if (isVoice) {
      clearTimeout(silenceTimerRef.current);
      audioBufferRef.current.push(...Array.from(inputData));

      // 实时发送（可选）
      sendChunk(inputData, score);
    } else if (audioBufferRef.current.length > 0) {
      silenceTimerRef.current = setTimeout(() => {

        audioBufferRef.current = [];
      }, 800); // 动态调整静音间隔
    }
  };

  // 发送完整语句
  const sendFullAudio = () => {
    const intBuffer = convertToInt16(audioBufferRef.current);
    wsRef.current?.send(JSON.stringify({
      type: 'full',
      size: intBuffer.length
    }));
    wsRef.current?.send(intBuffer.buffer);
  };

  // 实时分片发送
  const sendChunk = (chunk: Float32Array, score: number) => {
    const intBuffer = convertToInt16(Array.from(chunk));
    wsRef.current?.send(JSON.stringify({
      type: 'chunk',
      size: intBuffer.length,
      score
    }));
    wsRef.current?.send(intBuffer.buffer);
  };

  // 清理资源
  const stopRecording = () => {
    audioContextRef.current?.close();
    processorRef.current?.disconnect();
    setIsRecording(false);
  };
  const convertToInt16 = (buffer: Float32Array): Int16Array => {
    const int16Buffer = new Int16Array(buffer.length);

    // 使用TypedArray的forEach优化性能
    buffer.forEach((sample, i) => {
      int16Buffer[i] = sample < -1 ? -32768 :
                       sample > 1 ? 32767 :
                       Math.round(sample * 32767);
    });

    return int16Buffer;
  };
  return { isRecording, startRecording, stopRecording, wsRef };
}