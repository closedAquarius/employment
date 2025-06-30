import React, { useRef, useState, useEffect } from 'react';
import Webcam from 'react-webcam';
import { Dialog } from '@vaadin/react-components/Dialog';
import { ClipLoader } from 'react-spinners';

export const config: ViewConfig = {
  menu: {
    exclude: true,
  },
};

const FaceVerificationDialog = ({ onVerificationSuccess }) => {
  const webcamRef = useRef(null);
  const [image, setImage] = useState(null);
  const [isDialogOpen, setIsDialogOpen] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [isRegistering, setIsRegistering] = useState(false);
  const [attempts, setAttempts] = useState(0);

  useEffect(() => {
    // 清除消息
    const timer = setTimeout(() => {
      if (message) {
        setMessage('');
      }
    }, 3000);
    return () => clearTimeout(timer);
  }, [message]);

  const registerFace = async (imageSrc) => {
    setIsRegistering(true);
    setMessage('正在注册人脸...');
    try {
      const response = await fetch(`/api/register-face`, {
        method: 'POST',
        body: JSON.stringify({ image: imageSrc, userId: localStorage.getItem('userId') }),
        headers: {
          'Content-Type': 'application/json',
        },
      });
      const data = await response.json();
      if (data.status === '0') {
        setMessage('人脸注册成功，请再次拍照进行验证');
        return true;
      } else {
        setMessage(`人脸注册失败: ${data.message}`);
        return false;
      }
    } catch (error) {
      setMessage(`人脸注册出错: ${error.message}`);
      return false;
    } finally {
      setIsRegistering(false);
    }
  };

  const capture = async () => {
    const imageSrc = webcamRef.current.getScreenshot();
    setImage(imageSrc);
    setIsLoading(true);
    setMessage('正在验证...');
    
    try {
      console.info("userid:", localStorage.getItem('userId'));
      const response = await fetch(`/api/verify-face`, {
      method: 'POST',
      body: JSON.stringify({ image: imageSrc, userId: localStorage.getItem('userId') }),
      headers: {
        'Content-Type': 'application/json',
      },
      });
      
      const data = await response.json();
      console.log("验证结果:", data);
      
      if (data.status === '0') {
        setMessage('验证成功');
          onVerificationSuccess();
          setIsDialogOpen(false);
      } else if (data.message && data.message.includes('No registered face found')) {
        // 如果没有注册过人脸，自动注册
        setMessage('未找到注册的人脸，正在自动注册...');
        const registered = await registerFace(imageSrc);
        if (registered) {
          setAttempts(attempts + 1);
        }
      } else if (data.message && data.message.includes('No face detected')) {
        setMessage('未检测到人脸，请确保您的脸部在摄像头范围内');
      } else {
        setMessage(`人脸验证失败: ${data.message || '未知错误'}`);
      }
    } catch (error) {
      setMessage(`验证出错: ${error.message}`);
      console.error('Face verification error:', error);
    } finally {
        setIsLoading(false);
    }
  };

  const handleDialogClose = (e) => {
    // 判断是否是点击外部区域触发的关闭
    if (e.detail.source === 'outside') {
      e.preventDefault(); // 阻止关闭
    } else {
      setIsDialogOpen(false); // 其他情况允许关闭
    }
  };

  return (
    <Dialog
      opened={isDialogOpen}
      onOpenedChanged={handleDialogClose} // 监听关闭事件
      noCloseOnOutsideClick // 禁用点击外部关闭
      style={{
        padding: '20px',
        borderRadius: '15px',
        maxWidth: '400px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        backgroundColor: '#f9f9f9',
      }}
    >
      <div style={{ textAlign: 'center' }}>
        <h2 style={{ marginBottom: '10px', color: '#333', fontSize: '20px', fontWeight: 'bold' }}>
          人脸识别认证
        </h2>
        <p style={{ marginBottom: '15px', color: '#666', fontSize: '14px' }}>
          请确保您的脸部清晰可见，并点击"拍照"按钮进行认证。
        </p>
        <Webcam
          audio={false}
          ref={webcamRef}
          screenshotFormat="image/jpeg"
          style={{
            width: '100%',
            height: '300px',
            borderRadius: '10px',
            marginBottom: '15px',
            border: '2px solid #ddd',
          }}
        />
        
        {message && (
          <div style={{ 
            marginBottom: '15px', 
            padding: '8px', 
            backgroundColor: message.includes('成功') ? '#d4edda' : '#f8d7da',
            color: message.includes('成功') ? '#155724' : '#721c24',
            borderRadius: '5px',
            fontSize: '14px'
          }}>
            {message}
          </div>
        )}
        
        <button
          onClick={capture}
          style={{
            padding: '8px 16px',
            fontSize: '14px',
            borderRadius: '5px',
            border: 'none',
            backgroundColor: '#4CAF50',
            color: 'white',
            cursor: 'pointer',
            transition: 'background-color 0.3s ease',
          }}
          disabled={isLoading || isRegistering}
        >
          {isLoading ? '验证中...' : isRegistering ? '注册中...' : '拍照'}
        </button>
        
        {(isLoading || isRegistering) && (
          <div style={{ marginTop: '15px' }}>
            <ClipLoader color="#4CAF50" size={25} />
            <p style={{ marginTop: '8px', color: '#666', fontSize: '12px' }}>
              {isRegistering ? '正在注册人脸，请稍候...' : '正在验证，请稍候...'}
            </p>
          </div>
        )}
      </div>
    </Dialog>
  );
};

export default FaceVerificationDialog;