import axios from "axios";

// Địa chỉ của Backend Java
const API_URL = "http://localhost:8080/api/auth";

export const authService = {
  login: async (email, password) => {
    try {
      // Gửi yêu cầu POST sang Java
      const response = await axios.post(`${API_URL}/login`, {
        email: email,
        password: password,
      });

      // Nếu Java trả về OK (200), lấy dữ liệu user
      if (response.data) {
        return {
          success: true,
          token: response.data.token, // Token JWT từ Java gửi về
          user: response.data.user,
        };
      }
    } catch (error) {
      console.error("Lỗi đăng nhập:", error);
      return {
        success: false,
        message: error.response?.data || "Lỗi kết nối Server",
      };
    }
  },

  register: async (userData) => {
    return axios.post(`${API_URL}/register`, userData);
  },
};
