import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, TextField, VerticalLayout } from '@vaadin/react-components';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { LoginForm } from '@vaadin/react-components/LoginForm.js';

export const config: ViewConfig = {
  title: "登录",
  route: 'login',
  skipLayouts: true,
  menu: {
    exclude: true,
  },
};

export default function LoginView() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [code, setCode] = useState(null);
  const navigate = useNavigate();
  const [disabled, setDisabled] = useState(false);
  const [error, setError] = useState(false); // 用于显示错误信息
  const [logincount, setLogincount] = useState(0);

  const handleLogin = (event) => {
      setDisabled(true); // 禁用按钮，防止重复提交
      setError(false);   // 先清除错误状态
      const { username, password } = event.detail;
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
          setLogincount(logincount + 1);
          localStorage.setItem('token', data.data);
          console.info('token1:' + data.data);
      }).catch((err) => {
        console.error('失败：', err);
      });
      setError(true);  // 显示错误信息
      setDisabled(false); // 重新启用按钮
  };

  // 监听 code 的变化
  useEffect(() => {
    const name = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    console.info('token2:' + token);
    if (name != '') {
      try {
          const response = fetch('http://localhost:8080/auth/verify-token', {
                  method: 'POST',
                  headers: {
                    'token': token
                  },
          })
          .then((response) => response.json())
          .then((data) => {
              if (data.code == 401) {
                localStorage.removeItem('username');
                localStorage.removeItem('token');
                navigate('/');
              } else {
                navigate('/JavaCodeEditor', { state: { isFirst: true, name: name } } ); // 登录成功后跳转到主页面
              }
          })
          .catch((err) => {
             localStorage.removeItem('username');
             localStorage.removeItem('token');
             navigate('/');
          });
        } catch {
          localStorage.removeItem('username');
          localStorage.removeItem('token');
          navigate('/');
        }
    }
    if (code === 200) {
      navigate('/JavaCodeEditor', { state: { isFirst: true, name: username } } ); // 登录成功后跳转到主页面
    } else if (code !== null) {
      setError(true);  // 显示错误信息
      setDisabled(false); // 重新启用按钮
    }
  }, [code, navigate,logincount]);

   const i18n = {
    header: {
    },
    form: {
      username: '用户名',
      password: '邀请码',
      submit: '登录',
      forgotPassword: '忘记邀请码',
    },
    errorMessage: {
        message:  '用户名或邀请码输入错误',
        username: '输入用户名',
        password: '输入邀请码',
      },
    additionalInformation: '光哥面试系统',
  };
  return (
    <div style={{
            backgroundImage: 'url("/images/earth.jpg")',
            backgroundPosition: 'center',
            backgroundSize: 'cover',
            backgroundRepeat: 'no-repeat',
            height: '100vh',  // 让它铺满整个屏幕
            width: '100vw',    // 让它宽度也铺满屏幕
            display: 'flex',
            justifyContent: 'center', // 居中 LoginForm
            alignItems: 'center'
        }}>
      <LoginForm
                      theme="dark"
                      i18n={i18n}
                      disabled={disabled} // 控制按钮是否可点击
                      error={error} // 显示错误提示
                      onLogin={handleLogin}
                      no-autofocus
                  />
    </div>
  );
}