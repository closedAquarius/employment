import React, { useEffect, useState, ComponentType } from 'react';
import { useNavigate } from 'react-router-dom';

const withAuth = <P extends object>(WrappedComponent: ComponentType<P>) => {

  return (props: P) => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          navigate('/');
          return;
        }
        
        // 调用统一认证服务验证令牌
        fetch('/login/verify-token', {
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
              // 从响应中获取用户信息
              if (data.userInfo) {
                localStorage.setItem('username', data.userInfo.username);
                localStorage.setItem('userId', data.userInfo.id);
              }
              setIsAuthenticated(true);
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
    }, [navigate]);

    if (isAuthenticated === null) {
      return <div>Loading...</div>;
    }

    return <WrappedComponent {...props} />;
  };
};

export default withAuth;