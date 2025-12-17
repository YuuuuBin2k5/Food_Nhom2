import { api } from "./api";

export const userService = {
  GetAllUsers: async () => {
    try {
      //Get users
      const reponse = await api.get("/users");

      if (reponse.data.status === "success") {
        return reponse.data.data;
      } else {
        console.error("Lỗi", reponse.data.error);
        return [];
      }
    } catch (error) {
      console.error("Lỗi kết nối database", error);
      return [];
    }
  },
  AddUsers: async () => { },
};
