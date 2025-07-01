import React, { useRef, useState } from 'react';
import Webcam from 'react-webcam';
import { Dialog } from '@vaadin/react-components/Dialog';
import { ClipLoader } from 'react-spinners';

const FaceRegisterDialog = ({ onRegisterSuccess }) => {
  const webcamRef = useRef(null);
  const [image, setImage] = useState(null);
  const [isDialogOpen, setIsDialogOpen] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  // 注册人脸
  const handleRegister = () => {
    const imageSrc = webcamRef.current.getScreenshot();
    setImage(imageSrc);
    setIsLoading(true);
    fetch('/api/register-face', {
      method: 'POST',
      body: JSON.stringify({ image: imageSrc, userId: localStorage.getItem('userId') }),
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then(response => response.json())
      .then(data => {
        if (data === 'success' || data.status === '0') {
          alert('人脸注册成功！');
          setIsDialogOpen(false);
          if (onRegisterSuccess) onRegisterSuccess();
        } else {
          alert('人脸注册失败，请重试。');
        }
      })
      .catch(() => {
        alert('注册请求失败，请检查网络或稍后再试。');
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleDialogClose = (e) => {
    if (e.detail.source === 'outside') {
      e.preventDefault();
    } else {
      setIsDialogOpen(false);
    }
  };

  return (
    <Dialog
      opened={isDialogOpen}
      onOpenedChanged={handleDialogClose}
      noCloseOnOutsideClick
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
          人脸注册
        </h2>
        <p style={{ marginBottom: '15px', color: '#666', fontSize: '14px' }}>
          请确保您的脸部清晰可见，并点击“注册人脸”按钮。
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
        <button
          onClick={handleRegister}
          style={{
            padding: '8px 16px',
            fontSize: '14px',
            borderRadius: '5px',
            border: 'none',
            backgroundColor: '#2196F3',
            color: 'white',
            cursor: 'pointer',
            transition: 'background-color 0.3s ease',
          }}
          disabled={isLoading}
        >
          {isLoading ? '注册中...' : '注册人脸'}
        </button>
        {isLoading && (
          <div style={{ marginTop: '15px' }}>
            <ClipLoader color="#2196F3" size={25} />
            <p style={{ marginTop: '8px', color: '#666', fontSize: '12px' }}>
              正在注册，请稍候...
            </p>
          </div>
        )}
      </div>
    </Dialog>
  );
};

export default FaceRegisterDialog;