<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-r from-black to-white p-8">
    <div class="w-full max-w-5xl grid md:grid-cols-2 rounded-2xl overflow-hidden shadow-2xl">
      <!-- Left Panel -->
      <div class="hidden md:flex flex-col justify-center gap-6 bg-black text-white p-10">
        <div class="flex items-center gap-3">
          <div class="w-14 h-14 rounded-lg bg-gradient-to-br from-red-600 to-red-400 flex items-center justify-center font-bold text-white text-lg shadow-lg">GY</div>
          <div>
            <div class="text-xl font-bold tracking-wide">IRON HAVEN</div>
            <div class="text-sm text-gray-400">Strength • Cardio • Community</div>
          </div>
        </div>

        <h2 class="text-3xl font-bold">Chào mừng quay lại</h2>
        <p class="text-gray-300 max-w-sm">Vào nhanh và bắt đầu buổi tập. Quét mã QR hoặc đăng nhập để ghi nhận giờ vào phòng.</p>

        <div class="space-y-3 mt-4">
          <div class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-red-600 shadow-md"></span>
            <p class="font-semibold">Ghi nhận check-in tự động</p>
          </div>
          <div class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-red-600 shadow-md"></span>
            <p class="font-semibold">Lịch lớp và huấn luyện viên</p>
          </div>
          <div class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-red-600 shadow-md"></span>
            <p class="font-semibold">Ưu đãi thành viên</p>
          </div>
        </div>
      </div>

      <!-- Right Panel -->
      <div class="bg-white p-10 flex flex-col gap-5">
        <div>
          <div class="text-2xl font-bold text-black">Đăng nhập để vào phòng</div>
          <div class="text-sm text-gray-500">Nhập email hoặc mã thành viên để bắt đầu</div>
        </div>

        <form class="flex flex-col gap-4">
          <div>
            <label for="identity" class="block text-sm text-gray-500 mb-1">Email hoặc Mã thành viên</label>
            <input id="identity" type="text" placeholder="vd: long.ngoc@example.com hoặc GYM12345" required
              class="w-full px-4 py-3 rounded-lg border border-gray-200 focus:ring-2 focus:ring-red-500 focus:outline-none" />
          </div>

          <div>
            <label for="password" class="block text-sm text-gray-500 mb-1">Mật khẩu (nếu cần)</label>
            <input id="password" type="password" placeholder="Mật khẩu"
              class="w-full px-4 py-3 rounded-lg border border-gray-200 focus:ring-2 focus:ring-red-500 focus:outline-none" />
          </div>

          <div class="flex items-center justify-between text-sm">
            <label class="flex items-center gap-2 text-gray-500">
              <input type="checkbox" class="rounded border-gray-300" /> Ghi nhớ tôi
            </label>
            <a href="#" class="text-red-600 font-medium">Quên mật khẩu?</a>
          </div>

          <div class="flex gap-3 mt-2">
            <button type="submit" class="flex-1 py-3 rounded-lg bg-gradient-to-r from-red-600 to-red-400 text-white font-bold shadow-lg">Đăng nhập</button>
            <button type="button" class="flex-1 py-3 rounded-lg border border-gray-200 text-black font-semibold">Quét QR</button>
          </div>

          <div class="flex items-center gap-3 my-2">
            <div class="flex-1 h-px bg-gray-200"></div>
            <span class="text-xs text-gray-400">hoặc</span>
            <div class="flex-1 h-px bg-gray-200"></div>
          </div>

          <div class="flex gap-3 justify-center">
            <button type="button" class="px-4 py-2 rounded-lg border border-gray-200 text-black font-semibold">Đăng nhập bằng SMS</button>
            <button type="button" class="px-4 py-2 rounded-lg border border-gray-200 text-black font-semibold">Đăng ký mới</button>
          </div>

          <div class="text-center text-xs text-gray-400 mt-3">
            Bằng cách tiếp tục, bạn đồng ý với <a href="#" class="text-red-600">Điều khoản dịch vụ</a> của chúng tôi.
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
