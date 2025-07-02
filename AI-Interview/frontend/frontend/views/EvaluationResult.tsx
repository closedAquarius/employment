import { useRef, useEffect, useState } from 'react';
import { useNavigate, useLocation } from "react-router-dom";
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import withAuth from '../components/withAuth';

export const config: ViewConfig = {
  route: 'EvaluationResult',
  menu: {
    exclude: true,
  },
};

function EvaluationResult() {
  const navigate = useNavigate();
  const location = useLocation();
  const { isFirst, question, input, output, code } = location.state;
  const [data, setData] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const name = localStorage.getItem('username');

  useEffect(() => {
    // 重置数据
    setData("");
    setIsLoading(true);
    
    const params = {
        question: question,
        input: input,
        output: output,
        code: code
    };

    // 获取token
    const token = localStorage.getItem('token');
    
    console.log("准备发送评估请求...");
    
    // 使用fetch请求，兼容性更好
    fetch(`/interview/checkProgram`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(params)
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      
      const reader = response.body?.getReader();
      if (!reader) {
        throw new Error('无法获取响应体的读取器');
      }
      
      const decoder = new TextDecoder();
      let buffer = '';
      let fullText = ''; // 用于收集完整的评估文本
      
      function readChunk(): Promise<void> {
        if (!reader) return Promise.resolve();
        
        return reader.read().then(({value, done}) => {
          if (done) {
            setIsLoading(false);
            return;
          }
          
          const chunk = decoder.decode(value);
          buffer += chunk;
          
          // 处理SSE格式的响应
          const lines = buffer.split('\n');
          // 保留最后一行（可能不完整）
          buffer = lines.pop() || '';
          
          lines.forEach(line => {
            if (line.startsWith('data:')) {
              const eventData = line.substring(5).trim();
              if (eventData) {
               
                
                // 将每条消息添加到完整文本中，不添加换行符
                fullText += eventData;
                // 设置完整文本作为数据
                setData(fullText);
                setIsLoading(false); // 一旦有数据就关闭加载状态
              }
            }
          });
          
          return readChunk();
        }).catch(error => {
          console.error('读取流错误:', error);
          setIsLoading(false);
        });
      }
      
      readChunk();
    })
    .catch(error => {
      console.error('请求失败:', error);
      setIsLoading(false);
    });

    // 清理函数
    return () => {
      // 清理工作
      console.log('组件卸载，清理资源');
    };
  }, [question, input, output, code]);

  // 跳转到下一道题目
  const handleNextQuestion = () => {
    navigate('/JavaCodeEditor', { state: { isFirst: false } } );
  };

  // 跳转到笔试页面
  const handleStartTest = () => {
    navigate("/writetest");
  };

  return (
    <div>
      <h1>评估结果</h1>
      {isLoading && data === "" ? (
        <div>加载中...</div>
      ) : (
        <div 
          style={{ 
            width: '100%', 
            height: '300px', 
            minHeight: '200px',
            padding: '1em',
            border: '1px solid #ccc',
            borderRadius: '4px',
            backgroundColor: '#f9f9f9',
            overflow: 'auto',
            whiteSpace: 'pre-wrap',
            wordBreak: 'break-word'
          }}
        >
          {data || "暂无评估结果"}
        </div>
      )}
      <div style={{ marginTop: '20px' }}>
        {isFirst ? (
          <button onClick={handleNextQuestion}>下一道</button>
        ) : (
          <button onClick={handleStartTest}>开始笔试</button>
        )}
      </div>
    </div>
  );
}

export default withAuth(EvaluationResult);