import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, TextField, VerticalLayout } from '@vaadin/react-components';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

export const config: ViewConfig = {
  route: 'login',
  menu: {
    exclude: true,
  },
};

export default function LoginView() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [code, setCode] = useState(null);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleLogin = () => {
      const formData = new FormData();
      formData.append('name', username);
      formData.append('code', password);
      fetch('http://localhost:8080/login', {
        method: 'POST',
        body: formData,
      })
      .then((res) => res.json())
      .then((data) => {
          setCode(data.code); // 更新状态
          setMessage(data.message);
      }).catch((err) => {
        console.error('失败：', err);
      });
  };

  // 监听 code 的变化
  useEffect(() => {
    if (code === 200) {
      navigate('/JavaCodeEditor', { state: { isFirst: true, name: username } } ); // 登录成功后跳转到主页面
    } else if (code !== null) {
      alert(message); // 显示错误提示
    }
  }, [code, message, navigate]);

  return (
    <VerticalLayout
      style={{
        alignItems: 'center',
        justifyContent: 'center',
        height: '100vh',
        backgroundColor: '#f5f5f5',
      }}
    >
      <h1>登录</h1>
      <TextField
        label="用户名"
        value={username}
        onValueChanged={(e) => setUsername(e.detail.value)}
        style={{ width: '300px', marginBottom: '10px' }}
      />
      <TextField
        label="邀请码"
        type="password"
        value={password}
        onValueChanged={(e) => setPassword(e.detail.value)}
        style={{ width: '300px', marginBottom: '20px' }}
      />
      <Button onClick={handleLogin} theme="primary">
        登录
      </Button>
    </VerticalLayout>
  );
}