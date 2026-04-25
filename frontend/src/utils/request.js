import axios from 'axios';

const request = axios.create({
  baseURL: 'http://localhost:9090/api',
  timeout: 5000,
});

request.interceptors.request.use(
  config => {
      config.headers['Content-Type'] = 'application/json;charset=utf-8';
      // 仅对非登录请求添加 token
      if (!config.url.includes('/login')) {
          const user = localStorage.getItem("user");
          if (user) {
              config.headers['token'] = JSON.parse(user).token;
          }
      }
      return config;
  },
  error => {
      console.error('Request error:', error);
      return Promise.reject(error);
  }
);

request.interceptors.response.use(
  response => {
    console.log('Interceptor response:', response.data);
    return response.data;
  },
  error => {
    console.error('Interceptor error:', error);
    return Promise.reject(error);
  }
);

export default request;