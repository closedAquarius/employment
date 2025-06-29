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
    // 将 FormData 数据编码为 URL 查询参数
    /*const params = new URLSearchParams();
    params.append('question', question);
    params.append('input', input);
    params.append('output', output);
    params.append('code', code);*/

    const params = {
        question: question,
        input: input,
        output: output,
        code: code
    };

    // 将参数对象转换为 JSON 字符串
    const jsonParams = JSON.stringify(params);

    // 使用 encodeURIComponent 编码 JSON 字符串
    const encodedJson = encodeURIComponent(jsonParams);

    // 将编码后的字符串转换为 Base64
    const encodedParams = btoa(encodedJson);

    // 初始化 EventSource
    const eventSource = new EventSource(
      `/interview/checkProgram?data=${encodeURIComponent(encodedParams)}`
    );

    // 超时处理
    const timeoutId = setTimeout(() => {
      eventSource.close(); // 关闭 EventSource
      alert('请求超时，请稍后重试');
    }, 60000); // 60秒超时

    // 监听消息事件
    eventSource.onmessage = (event) => {
      const data = event.data; // 获取数据（已经是去掉 "data:" 前缀的）

      // 将新数据拼接到状态中
      setData((prevData) => [...prevData, data]);
    };

    // 监听错误事件
    eventSource.onerror = (event) => {
      console.error('EventSource failed:', event);
      eventSource.close(); // 关闭连接
      clearTimeout(timeoutId); // 清除超时定时器

      if (event.target.readyState === EventSource.CLOSED) {
        console.info('连接已关闭');
      } else {
        console.error('连接错误，请稍后重试');
      }
    };

    // 清理函数
    return () => {
      eventSource.close(); // 组件卸载时关闭连接
      clearTimeout(timeoutId); // 清除超时定时器
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