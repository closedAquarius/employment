import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { useRef, useEffect, useState } from 'react';
import { SplitLayout } from '@vaadin/react-components/SplitLayout';
import { useNavigate, useLocation } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import * as monaco from 'monaco-editor';
import { Details } from '@vaadin/react-components/Details.js';
import { VerticalLayout } from '@vaadin/react-components/VerticalLayout.js';
import { ClipLoader } from 'react-spinners';
import thinkingAnimation from 'Frontend/assets/animations/think-animation.json'; // 从 LottieFiles 下载的动画文件
import Lottie from 'lottie-react';
import withAuth from 'Frontend/components/withAuth';
import { ProgressBar } from '@vaadin/react-components/ProgressBar.js';
import { motion } from 'framer-motion';

export const config: ViewConfig = { menu: { order: 0, icon: 'line-awesome/svg/file.svg' }, title: '光哥笔试' };

const JavaCodeEditor  = () => {

  const [code, setCode] = useState('');
  const [input, setInput] = useState('');
  const [output, setOutput] = useState('');
  const [question, setQuestion] = useState('');
  const editorRef = useRef(null);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const lottieRef = useRef(null);
  const [showStartButton, setShowStartButton] = useState(true); // 是否显示“开始面试”按钮
  const location = useLocation();
  const { isFirst } = location.state || { isFirst: true }; // 获取状态

  const name = localStorage.getItem('username');

  useEffect(() => {
    setShowStartButton(isFirst);
    if (!isFirst) {
        startTest();
    }
  }, [isFirst]);
  // 从后端获取代码
  function startTest() {
    const apiUrl = isFirst
      ? `http://localhost:8080/interview/makeProgram?first=true&name=${name}`
      : `http://localhost:8080/interview/makeProgram?first=false&name=${name}`;
    setShowStartButton(false); // 隐藏“开始面试”按钮
    setLoading(true);
    fetch(apiUrl)
      .then((res) => res.json())
      .then((data) => {
        setCode(data.code);
        setInput(data.example.input); // 设置示例
        setOutput(data.example.output); // 设置示例
        setQuestion(data.question); // 设置问题描述
        setLoading(false);
      })
      .catch((err) => {
        console.error('获取代码失败：', err);
        setLoading(false);
      });
  }

  // 初始化编辑器
  function handleEditorDidMount(editor, monacoInstance) {
    editorRef.current = editor;

    // 注册 Java 代码补全
    monacoInstance.languages.registerCompletionItemProvider('java', {
      provideCompletionItems: (model, position) => {
        const suggestions = [
          {
            label: 'println',
            kind: monacoInstance.languages.CompletionItemKind.Function,
            insertText: 'System.out.println("${1:message}");',
            documentation: '打印消息到控制台',
          },
          {
            label: 'main',
            kind: monacoInstance.languages.CompletionItemKind.Method,
            insertText: 'public static void main(String[] args) {\n\t${1}\n}',
            documentation: 'Java 主方法',
          },
        ];
        return { suggestions };
      },
    });
  }

  // 提交代码
  const submitCode = async () => {
     const code = editorRef.current.getValue();
     navigate('/EvaluationResult', { state: { isFirst, question, input, output, code } });
  };

 if (loading) {
    return (
    <div>
      <div>
        <ProgressBar indeterminate />
      </div>
      <div style={{ textAlign: 'center',
                              position: 'fixed',
                              top: '20%',
                              left: '50%',
                              transform: 'translate(-50%, -50%)' }}>
          <p>问题生成中...</p>
      </div>
    </div>
    );
  }

 if (showStartButton) {
    return (
    <div style={{ textAlign: 'center',
                        position: 'fixed',
                        top: '20%',
                        left: '50%',
                        transform: 'translate(-50%, -50%)' }}>
    <motion.button
      onClick={startTest}
      style={{
        padding: '15px 30px',
        fontSize: '18px',
        borderRadius: '10px',
        border: 'none',
        backgroundColor: '#4CAF50',
        color: 'white',
        cursor: 'pointer',
      }}
      whileHover={{ scale: 1.1 }}
      whileTap={{ scale: 0.9 }}
    >
      开始笔试
    </motion.button>
    </div>
    );
 }
  return (
    <div style={{ width: '90%', height: '100%' ,padding: '20px',fontFamily: 'Arial, sans-serif',}}>

      <Details summary="Java编程" opened theme="filled" style={{fontSize: '15px',fontWeight: 'bold',}}>
        <VerticalLayout style={{fontSize: '14px',fontWeight: 'bold',}}>
          <span><p style={{fontWeight: 'bold',}}>题目描述：</p><p style={{fontSize: '13px',color: '#d7ba7d'}}>{question}</p></span>
          <span><p style={{fontWeight: 'bold',}}>输入：</p><p style={{fontSize: '13px',color: '#d7ba7d'}}>{input}</p></span>
          <span><p style={{fontWeight: 'bold',}}>输出：</p><p style={{fontSize: '13px',color: '#d7ba7d'}}>{output}</p></span>
        </VerticalLayout>
      </Details>

      <div style={{ height: '500px', border: '1px solid #ccc', marginBottom: '20px' }}>
        <Editor
          height="100%"
          width="100%"
          theme="vs-dark"
          defaultLanguage="java"
          value={code}
          onMount={handleEditorDidMount}
        />
      </div>
      <button onClick={submitCode} style={{ marginTop: '10px' }}>
        提交代码
      </button>
    </div>

  );
}

export default withAuth(JavaCodeEditor);