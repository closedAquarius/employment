import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const withAuth = (WrappedComponent) => {

  return (props) => {
    const [isAuthenticated, setIsAuthenticated] = useState(null);
    const navigate = useNavigate();  // 替换 useHistory 为 useNavigate

    useEffect(() => {
      try {
        const token = localStorage.getItem('token');
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
              localStorage.setItem('username', data.data);
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
    }, [history]);

    if (isAuthenticated === null) {
      return <div>Loading...</div>;
    }

    return <WrappedComponent {...props} />;
  };
};

export default withAuth;