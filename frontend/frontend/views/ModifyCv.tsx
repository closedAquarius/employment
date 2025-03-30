import React, { useState } from 'react';
import axios from 'axios';
import { saveAs } from 'file-saver';
import { Document, Page, pdfjs } from 'react-pdf';
import withAuth from 'Frontend/components/withAuth';
import Lottie from 'lottie-react';
import rewriter from 'Frontend/assets/animations/resumeRewriter.json';
import { TextArea } from '@vaadin/react-components/TextArea.js';
import { Upload } from '@vaadin/react-components/Upload.js';
import { Notification } from '@vaadin/react-components/Notification.js';
import { Dialog } from '@vaadin/react-components/Dialog.js';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/4.8.69/pdf.worker.mjs`;


export const config: ViewConfig = {
  menu: {
    exclude: true,
  },
};

function ModifyCv() {
  const [jd, setJd] = useState('');
  const [pdfFile, setPdfFile] = useState(null);
  const [pdfUrl, setPdfUrl] = useState('');
  const [numPages, setNumPages] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [currentPdf, setCurrentPdf] = useState(null);

  const i18n = {
    dropFiles: {
      one: '拖转简历到这',
      many: '拖转简历到这',
    },
    addFiles: {
      one: '上传简历',
      many: '上传简历',
    },
    error: {
      tooManyFiles: '文件太多了',
      fileIsTooBig: '文件太大了',
      incorrectFileType: '文件类型不对',
    },
    uploading: {
      status: {
        connecting: 'connecting',
        stalled: 'stalled',
        processing: 'processing',
        held: 'held',
      },
      remainingTime: {
        prefix: '前缀: ',
        unknown: '未知',
      },
      error: {
        serverUnavailable: 'serverUnavailable',
        unexpectedServerError: 'unexpectedServerError',
        forbidden: 'forbidden',
      },
    },
    units: {
      size: ['t', 'kt', 'Mt', 'Gt', 'Tt', 'Pt', 'Et', 'ZB', 'YB'],
      sizeBase: 1000,
    },
  };

  // 处理JD输入
  const handleJdChange = (e) => {
    setJd(e.target.value);
  };

  // 处理PDF上传
  const handlePdfUpload = (e) => {
    const file = e.detail.file; // 获取上传的文件
    if (file && file.type === 'application/pdf') {
      setPdfFile(file); // 设置PDF文件
    } else {
      alert('请上传PDF文件');
    }
  };

  // 处理修改简历按钮点击
  const handleModifyResume = async () => {
    if (!pdfFile || !jd) {
      alert('请上传PDF文件并输入招聘JD信息');
      return;
    }

    setIsLoading(true);

    const formData = new FormData();
    formData.append('resume', pdfFile);
    formData.append('jd', jd);

    try {
      const response = await axios.post(`/resume/upload`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      if (response.data.code === 200) {
        setPdfUrl(response.data.data);
      } else {
        alert('修改简历失败：' + response.data.message);
      }
    } catch (error) {
      console.error('修改简历时出错：', error);
      alert('修改简历时出错，请检查网络或后端服务');
    } finally {
      setIsLoading(false);
    }
  };

  // 处理下载简历按钮点击
  const handleDownloadResume = async () => {
    if (!pdfUrl) {
      alert('没有可下载的简历');
      return;
    }

    try {
      const fullUrl = `/${pdfUrl}`;
      const response = await axios.get(fullUrl, {
        responseType: 'blob',
      });

      const fileName = pdfUrl.split('/').pop();
      saveAs(response.data, fileName);
    } catch (error) {
      console.error('下载简历时出错：', error);
      alert('下载简历时出错，请检查网络或后端服务');
    }
  };

  // 处理PDF加载成功
  const onDocumentLoadSuccess = ({ numPages }) => {
    setNumPages(numPages);
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      {/* 招聘JD信息输入框 */}
      <div style={{ marginBottom: '20px' }}>
        <label htmlFor="jd" style={{ display: 'block', marginBottom: '10px' }}>
          修改简历
        </label>
        <TextArea
          label="职位描述(JD)"
          value={jd}
          onChange={handleJdChange}
          style={{ width: '80%', minHeight: '80px', maxHeight: '100px' }}
          placeholder="请输入职位描述(JD)信息"
        />
      </div>

      {/* 上传PDF文件 */}
      <div style={{ width: '80%', marginBottom: '20px' }}>
        <label htmlFor="pdfUpload" style={{ display: 'block', marginBottom: '10px' }}>
          上传PDF简历：
        </label>
        <Upload
          i18n={i18n}
          id="pdfUpload"
          maxFiles="1"
          accept="application/pdf"
          onUploadBefore={(e) => {
            handlePdfUpload(e); // 直接调用 handlePdfUpload
          }}
          style={{ display: 'block' }}
          onFileReject={(event) => {
            Notification.show('只能上传一个简历');
          }}
        />
      </div>

      {/* 修改简历按钮 */}
      <div style={{ marginBottom: '20px' }}>
        <button
          onClick={handleModifyResume}
          disabled={isLoading}
          style={{
            padding: '10px 20px',
            fontSize: '16px',
            backgroundColor: '#007bff',
            color: '#fff',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer',
          }}
        >
          修改简历
        </button>
        <hr style={{ width: '80%', margin: '20px 0', border: '1px solid #ccc' }} /> {/* 添加横线 */}
      </div>

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
            <p style={{ marginTop: '10px', fontSize: '18px', color: '#333' }}>正在修改简历，请稍候...</p>
          </div>
        </>
      )}

      {/* 修改简历后展示浏览简历按钮 */}
      {pdfUrl && (
        <div style={{ marginBottom: '20px' }}>
          <button
            onClick={() => {
              setCurrentPdf(pdfUrl);
              setIsDialogOpen(true);
            }}
            style={{
              padding: '10px 20px',
              fontSize: '16px',
              backgroundColor: '#28a745',
              color: '#fff',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer',
            }}
          >
            浏览修改后的简历
          </button>
          <hr style={{ width: '80%', margin: '20px 0', border: '1px solid #ccc' }} /> {/* 添加横线 */}
        </div>
      )}

      {/* 下载简历按钮 */}
      {pdfUrl && (
        <div style={{ marginBottom: '20px' }}>
          <button
            onClick={handleDownloadResume}
            style={{
              padding: '10px 20px',
              fontSize: '16px',
              backgroundColor: '#28a745',
              color: '#fff',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer',
            }}
          >
            下载简历
          </button>
          <hr style={{ width: '80%', margin: '20px 0', border: '1px solid #ccc' }} /> {/* 添加横线 */}
        </div>
      )}

      {/* 对话框 */}
      <Dialog
        opened={isDialogOpen}
        onOpenedChanged={(e) => setIsDialogOpen(e.detail.value)}
        header="简历内容"
        footer={
          <button
            onClick={() => setIsDialogOpen(false)}
            style={{
              padding: '10px 20px',
              fontSize: '16px',
              backgroundColor: '#007bff',
              color: '#fff',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer',
            }}
          >
            关闭
          </button>
        }
      >
        <div style={{ height: '600px', overflow: 'auto' }}>
          <Document file={currentPdf} onLoadSuccess={onDocumentLoadSuccess}>
            {Array.from(new Array(numPages), (el, index) => (
              <Page
                key={`page_${index + 1}`}
                pageNumber={index + 1}
                height={600}
                scale={1.5} // 调整缩放比例
                renderAnnotationLayer={false}
                renderTextLayer={false}
                style={{ marginBottom: '10px' }}
              />
            ))}
          </Document>
        </div>
      </Dialog>
    </div>
  );
};

export default withAuth(ModifyCv);