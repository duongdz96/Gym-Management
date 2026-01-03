<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-emerald-100 via-teal-50 to-cyan-100 p-8 relative overflow-hidden">
    <!-- Decorative circles -->
    <div class="absolute top-20 left-20 w-72 h-72 bg-gradient-to-br from-pink-300 to-purple-400 rounded-full opacity-20 blur-3xl"></div>
    <div class="absolute bottom-20 right-20 w-96 h-96 bg-gradient-to-br from-yellow-300 to-orange-400 rounded-full opacity-20 blur-3xl"></div>
    <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-64 h-64 bg-gradient-to-br from-cyan-300 to-blue-400 rounded-full opacity-10 blur-3xl"></div>
    
    <div class="w-full max-w-4xl grid md:grid-cols-2 rounded-2xl overflow-hidden shadow-2xl relative z-10">
      <!-- Left Panel -->
      <div class="hidden md:flex flex-col justify-center gap-6 bg-gradient-to-br from-emerald-600 via-teal-600 to-cyan-600 text-white p-10 relative overflow-hidden">
        <!-- Decorative elements -->
        <div class="absolute top-0 right-0 w-40 h-40 bg-yellow-400 rounded-full opacity-10 blur-2xl"></div>
        <div class="absolute bottom-0 left-0 w-32 h-32 bg-pink-400 rounded-full opacity-10 blur-2xl"></div>
        
        <div class="flex items-center gap-3 relative z-10">
          <div
            class="w-14 h-14 rounded-lg bg-gradient-to-br from-yellow-400 via-orange-500 to-pink-500 flex items-center justify-center font-bold text-white text-lg shadow-lg">
            GY
          </div>
          <div>
            <div class="text-xl font-bold tracking-wide">IRON HAVEN</div>
            <div class="text-sm text-emerald-100">Strength • Cardio • Community</div>
          </div>
        </div>

        <h2 class="text-3xl font-bold">Chào mừng quay lại</h2>
        <p class="text-emerald-50 max-w-sm">Đăng nhập để bắt đầu buổi tập của bạn. Cùng chinh phục giới hạn!</p>

        <ul class="space-y-3 mt-4 text-sm text-emerald-50 relative z-10">
          <li class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-gradient-to-r from-yellow-400 to-orange-500 shadow-md"></span>
            Ghi nhận check-in tự động
          </li>
          <li class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-gradient-to-r from-pink-400 to-purple-500 shadow-md"></span>
            Theo dõi lịch tập và tiến độ
          </li>
          <li class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-gradient-to-r from-cyan-400 to-blue-500 shadow-md"></span>
            Ưu đãi và sự kiện đặc biệt
          </li>
        </ul>
      </div>

      <!-- Right Panel -->
      <div class="bg-white p-10 flex flex-col justify-center gap-6">
        <div>
          <h2 class="text-2xl font-bold text-black">Đăng nhập tài khoản</h2>
          <p class="text-sm text-gray-500">Nhập email và mật khẩu để tiếp tục</p>
        </div>

        <form class="flex flex-col gap-5" @submit.prevent="loginUser">
          <div>
            <label for="email" class="block text-sm text-gray-500 mb-1">Email</label>
            <input
              id="email"
              type="email"
              v-model="email"
              placeholder="ex: your.email@example.com"
              required
              class="w-full px-4 py-3 rounded-lg border border-gray-200 focus:ring-2 focus:ring-cyan-400 focus:border-cyan-400 focus:outline-none transition-all"
            />
          </div>

          <div>
            <label for="password" class="block text-sm text-gray-500 mb-1">Mật khẩu</label>
            <input
              id="password"
              type="password"
              v-model="password"
              placeholder="Nhập mật khẩu"
              required
              class="w-full px-4 py-3 rounded-lg border border-gray-200 focus:ring-2 focus:ring-purple-400 focus:border-purple-400 focus:outline-none transition-all"
            />
          </div>

          <div class="flex items-center justify-between text-sm">
            <label class="flex items-center gap-2 text-gray-500">
              <input type="checkbox" class="rounded border-gray-300" /> Ghi nhớ tôi
            </label>
            <a href="#" class="text-cyan-600 font-medium hover:text-cyan-700 hover:underline transition-colors">Quên mật khẩu?</a>
          </div>

          <button
            type="submit"
            class="w-full py-3 rounded-lg bg-gradient-to-r from-cyan-500 via-teal-500 to-emerald-500 text-white font-bold shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-300">
            Đăng nhập
          </button>

          <div class="text-center text-sm text-gray-500 mt-3">
            Chưa có tài khoản?
            <a href="#" class="text-purple-600 font-semibold hover:text-purple-700 hover:underline transition-colors">Đăng ký ngay</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/useAuthStore";

const email = ref("");
const password = ref("");
const router = useRouter();
const toast = useToast();
const authStore = useAuthStore();

const loginUser = async () => {
  try {
    await authStore.login(email.value, password.value);
    toast.success(`Chào mừng ${authStore.user.fullName}!`);
    const role = authStore.user.role?.toLowerCase();

    switch (role) {
      case "user":
      case "member":
        router.push("/customer");
        break;
      case "manager":
        router.push("/manager");
        break;
      case "receptionist":
        router.push("/reception");
        break;
      case "pt":
        router.push("/pt");
        break;
      case "teacher":
        router.push("/teacher");
        break;
      default:
        router.push("/"); // fallback nếu không có role
        break;
    }
  } catch (err) {
    const msg =
      "Sai thông tin đăng nhập hoặc lỗi máy chủ!";
    toast.error(msg);
  }
};
</script>