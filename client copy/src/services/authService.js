import { api } from "./api";

export const loginAPI = (email, password) => {
  return api.post("/login", { email, password });
};

export const registerAPI = (data) => {
  // data bao gồm: fullName, email, password, phone, role, shopName...
  return api.post("/register", data);
};
