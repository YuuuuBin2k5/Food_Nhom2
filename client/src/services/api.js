import axios from "axios";

//BASE_URL
const BASE_URL = "http://localhost:8080/server/api";

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    console.log('=== [API Interceptor] Token:', token ? 'EXISTS' : 'NULL');
    console.log('=== [API Interceptor] Request URL:', config.url);

    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
      console.log('=== [API Interceptor] Authorization header added');
    } else {
      console.warn('=== [API Interceptor] No token found in localStorage!');
    }
    return config;
  },
  (error) => {
    console.error('=== [API Interceptor] Request error:', error);
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Only redirect on 401 if we're NOT on the login page
    if (error.response && error.response.status === 401 && !window.location.pathname.includes('/login')) {
      console.warn("Phiên đăng nhập hết hạn, đang đăng xuất...");

      localStorage.removeItem("token");
      localStorage.removeItem("user");

      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;
