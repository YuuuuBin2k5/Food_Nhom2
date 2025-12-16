import api from "./api";

export const loginAPI = (email, password) => {
  return api.post("/login", { email, password });
};

export const registerAPI = (data) => {
  return api.post("/register", data);
};

export const forgotPasswordAPI = (email) => {
  return api.post("/forgot-password", { email });
};

export const resetPasswordAPI = (token, password) => {
  return api.post("/reset-password", { token, password });
};
