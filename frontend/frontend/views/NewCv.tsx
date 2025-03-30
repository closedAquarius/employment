// NewCv.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { Button, VerticalLayout, HorizontalLayout } from '@vaadin/react-components';
import ResumeForm from './ResumeForm';
import { Notification } from '@vaadin/react-components/Notification.js';
import withAuth from 'Frontend/components/withAuth';
import Lottie from 'lottie-react';
import rewriter from 'Frontend/assets/animations/resumeRewriter.json';

export const config: ViewConfig = {
  menu: {
    exclude: true,
  },
};

function NewCv() {
  const [resumeUrl, setResumeUrl] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // 处理简历生成
  const handleGenerateResume = async (resumeData) => {
    setIsLoading(true);
    try {
      const response = await axios.post(`/resume/create`, resumeData, {
        headers: {
          'Content-Type': 'application/json',
        },
      });
     if (response.data.code === 200) {
        setResumeUrl(response.data.data);
        Notification.show(`简历已生成`, { theme: "success" });
      } else {
        Notification.show('简历生成失败：' + response.data.message, { theme: "error" });
      }
    } catch (error) {
      Notification.show('生成简历失败', { theme: "error" });
      console.error('Error:', error);
      // 这里可以添加错误提示
    } finally {
      setIsLoading(false);
    }
  };

  // 下载简历
  const downloadResume = () => {
    if (resumeUrl) {
      const link = document.createElement('a');
      link.href = resumeUrl;
      const fileName = resumeUrl.split('/').pop();
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  return (
    <VerticalLayout>
      <ResumeForm onGenerate={handleGenerateResume} />

      {/* 加载动画 */}
      {isLoading && (
        <>
          <div
            style={{
              position: 'fixed',
              top: 0,
              left: 0,
              width: '100%',
              height: '100%',
              backgroundColor: 'rgba(255, 255, 255, 0.8)',
              zIndex: 999,
            }}
          ></div>
          <div
            style={{
              position: 'fixed',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: 1000,
              textAlign: 'center',
            }}
          >
            <Lottie
              animationData={rewriter}
              loop={true}
              style={{ width: '300px', height: '300px' }}
            />
            <p style={{ marginTop: '10px', fontSize: '18px', color: '#333' }}>正在生成简历，请稍候...</p>
          </div>
        </>
      )}

      {/* 简历操作按钮 */}
      {resumeUrl && (
        <HorizontalLayout style={{ gap: '10px' }}>
          <Button
            theme="primary success"
            onClick={() => window.open(resumeUrl, '_blank')}
            disabled={isLoading}
            style={{
                  padding: '10px 20px',
                  fontSize: '16px',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: 'pointer',
                }}
          >
            浏览简历
          </Button>
          <Button
            theme="primary success"
            onClick={downloadResume}
            disabled={isLoading}
            style={{
                  padding: '10px 20px',
                  fontSize: '16px',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: 'pointer',
                }}
          >
            下载简历
          </Button>
        </HorizontalLayout>
      )}
    </VerticalLayout>
  );
}

export default withAuth(NewCv);