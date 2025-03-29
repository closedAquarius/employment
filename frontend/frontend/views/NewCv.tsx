// NewCv.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { Button, VerticalLayout, HorizontalLayout } from '@vaadin/react-components';
import ResumeForm from './ResumeForm';
import { Notification } from '@vaadin/react-components/Notification.js';
import withAuth from 'Frontend/components/withAuth';

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
      link.download = 'resume.pdf';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  return (
    <VerticalLayout>
      <ResumeForm onGenerate={handleGenerateResume} />

      {/* 简历操作按钮 */}
      {resumeUrl && (
        <HorizontalLayout>
          <Button
            theme="primary"
            onClick={() => window.open(resumeUrl, '_blank')}
            disabled={isLoading}
          >
            浏览简历
          </Button>
          <Button
            theme="primary"
            onClick={downloadResume}
            disabled={isLoading}
          >
            下载简历
          </Button>
        </HorizontalLayout>
      )}
    </VerticalLayout>
  );
}

export default withAuth(NewCv);