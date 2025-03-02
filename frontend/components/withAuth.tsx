import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const withAuth = (WrappedComponent) => {

  return (props) => {
    const [isAuthenticated, setIsAuthenticated] = useState(null);
    const navigate = useNavigate();  // 替换 useHistory 为 useNavigate

    useEffect(() => {
      try {
        const response = fetch('http://localhost:8080/auth/verify-token', {
                method: 'POST',
                credentials: 'include',
        })
        .then((response) => response.json())
        .then((data) => {
            if (data.code == 401) {
              navigate('/');
            } else {
              setIsAuthenticated(true);
            }
        })
        .catch((err) => {
           navigate('/');
        });
      } catch {
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