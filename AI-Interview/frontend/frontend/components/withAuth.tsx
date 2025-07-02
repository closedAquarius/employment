import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

export const config: ViewConfig = { title: '用户认证' };

// 简单的身份验证高阶组件
const withAuth = (Component) => {
  return function WithAuthComponent(props) {
    const navigate = useNavigate();
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
      // 检查本地存储中是否有令牌
      try {
        const token = localStorage.getItem('token');
        
        if (!token) {
          // 如果没有令牌，重定向到登录页面
          navigate('/login');
          return;
        }
        
        // 如果有令牌，设置为已认证
        setIsAuthenticated(true);
      } catch (error) {
        console.error('Authentication error:', error);
        navigate('/login');
      } finally {
        setLoading(false);
      }
    }, [navigate]);

    if (loading) {
      return <div>Loading...</div>;
    }

    if (!isAuthenticated) {
      return null;
    }

    return <Component {...props} />;
  };
};

export default withAuth;