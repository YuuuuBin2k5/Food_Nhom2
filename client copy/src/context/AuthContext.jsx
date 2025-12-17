import React, { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    const storedToken = localStorage.getItem("token");

    if (storedUser && storedToken) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  // ========================= LOGIN REQUEST =========================
  const loginRequest = async (email, password) => {
    try {
      const res = await fetch("http://localhost:8080/server/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await res.json();
      return data;
    } catch (err) {
      return { success: false, message: "Không kết nối được server" };
    }
  };

  const login = (userData, token) => {
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);
  };

  // ========================= REGISTER REQUEST =========================
  const registerRequest = async (payload) => {
    try {
      const res = await fetch("http://localhost:8080/server/api/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      return await res.json(); // backend trả {success,message,...}
    } catch (err) {
      return { success: false, message: "Không thể kết nối server" };
    }
  };

  // Bọc lại nếu cần xử lý thêm
  const register = async (payload) => {
    return await registerRequest(payload);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    window.location.href = "/login";
  };

  return (
    <AuthContext.Provider
      value={{ user, loginRequest, login, register, logout, loading }}
    >
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
export default AuthContext;
