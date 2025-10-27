import { defineStore } from "pinia";
import { ref,computed } from "vue";
import api from "@/services/api";

export const useAuthStore = defineStore("auth", () => {
  const user = ref(JSON.parse(localStorage.getItem("user")) || null);
  const accessToken = ref(localStorage.getItem("accessToken") || null);
  const refreshToken = ref(localStorage.getItem("refreshToken") || null);

  const login = async (email, password) => {
    const res = await api.post("/auth/login", { email, password });
    const { accessToken: token, refreshToken: rToken, user: userData } = res.data;

    user.value = userData;
    accessToken.value = token;
    refreshToken.value = rToken;

    localStorage.setItem("user", JSON.stringify(userData));
    localStorage.setItem("accessToken", token);
    localStorage.setItem("refreshToken", rToken);
  };

  const logout = () => {
    user.value = null;
    accessToken.value = null;
    refreshToken.value = null;
    localStorage.removeItem("user");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  };

  const isLoggedIn = computed(() => !!accessToken.value);

  return { user, accessToken, refreshToken, login, logout, isLoggedIn };
});
