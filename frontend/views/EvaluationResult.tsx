import { useRef, useEffect, useState } from 'react';
import { useNavigate, useLocation } from "react-router-dom";
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { TextArea } from '@vaadin/react-components/TextArea.js';
import withAuth from 'Frontend/components/withAuth';

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
  const [data, setData] = useState([]);
  const name = localStorage.getItem('username');

  useEffect(() => {
    const fetchData = async () => {
        const formData = new FormData();
        formData.append('question', question);
        formData.append('input', input);
        formData.append('output', output);
        formData.append('code', code);

        // 定义并初始化 AbortController
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 60000); // 60秒超时

        try {
           const response = await fetch('http://localhost:8080/interview/checkProgram', {
                method: 'POST',
                body: formData,
                signal: controller.signal,
              });

           if (!response.ok) {
             throw new Error(`HTTP error! status: ${response.status}`);
           }

           const reader = response.body.getReader();
           const decoder = new TextDecoder();
           let buffer = ''; // 用于存储未处理完的流数据

           while (true) {
             const { done, value } = await reader.read();
             if (done) break; // 流结束

             const chunk = decoder.decode(value, { stream: true });
             buffer += chunk; // 将新数据追加到缓冲区
             // 按行分割并处理
             const lines = buffer.split('\n');
             buffer = lines.pop() || ''; // 最后一行可能不完整，保留在缓冲区中

             const extractedData = lines
                         .filter(line => line.trim() !== '') // 过滤空行
                         .map(line => {
                             const match = line.match(/data:\s*(.*)/); // 匹配 data: 后面的内容
                             return match ? match[1] : null; // 返回匹配到的值
                         })
                         .filter(value => value !== null); // 过滤掉未匹配的行

             // 将提取的数据拼接到状态中
             if (extractedData.length > 0) {
                 setData((prevData) => [...prevData, ...extractedData]);
             }
           }

        } catch (error) {
          if (error.name === 'AbortError') {
            alert('请求超时，请稍后重试');
          } else {
            alert(error.message);
          }
        } finally {
          clearTimeout(timeoutId); // 清除超时定时器
        }
    };
    fetchData();
  }, [ question, input, output, code]);

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
      <TextArea label="Description" value={data} style={{ width: '100%' }} />
      {isFirst ? (
        <button onClick={handleNextQuestion}>下一道</button>
      ) : (
        <button onClick={handleStartTest}>开始笔试</button>
      )}
    </div>
  );
}

export default withAuth(EvaluationResult);