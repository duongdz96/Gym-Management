<script setup lang="ts">
import { ref,onMounted } from 'vue'
import axios from 'axios'

const trialName = ref('')
const trialPhone = ref('')
const trialEmail = ref('')

function handleTrialSubmit() {
    if (!trialName.value || !trialPhone.value) return
    localStorage.setItem('custom_name', trialName.value)
    localStorage.setItem('custom_phone', trialPhone.value)
    if (trialEmail.value) localStorage.setItem('custom_email', trialEmail.value)
    alert('Đã gửi đăng ký. Chúng tôi sẽ liên hệ sớm!')
}

const feedbacks = ref([])
const currentIndex = ref(0)

// gọi API lấy feedback
onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/feedbacks')
    // Giả sử feedback có id hoặc createdAt tăng dần
    feedbacks.value = response.data
      .sort((a, b) => b.id - a.id) // sắp xếp giảm dần theo id
      .slice(0, 3) // lấy 3 cái đầu tiên
  } catch (error) {
    console.error('Lỗi khi tải feedback:', error)
  }
})

// chuyển feedback trái/phải
const prevFeedback = () => {
  currentIndex.value =
    (currentIndex.value - 1 + feedbacks.value.length) % feedbacks.value.length
}

const nextFeedback = () => {
  currentIndex.value = (currentIndex.value + 1) % feedbacks.value.length
}

// tự động chuyển feedback sau 4 giây
onMounted(() => {
  setInterval(() => {
    if (feedbacks.value.length > 1) {
      nextFeedback()
    }
  }, 4000)
})

</script>

<template>
    <div class="">
        <!-- Banner (carousel simplified) -->
        <section id="banner" class="relative overflow-hidden rounded-xl">
            <div class="aspect-[19/8] w-full bg-black rounded-xl overflow-hidden">
                <img class="w-full h-full object-cover" src="https://cali.vn/storage/app/media/2025/Homepage/TTC%20Presale/TTC-Presale-Desktop.webp" alt="California banner">
            </div>
            <div class="absolute inset-0 flex items-end p-6">
                <RouterLink to="#trial" class="btn-submit-banner inline-flex items-center rounded-md bg-red-600 px-5 py-3 text-white text-sm font-semibold hover:bg-red-500">Giữ Chỗ Ưu Đãi!</RouterLink>
            </div>
        </section>

        <!-- Feedback Carousel -->
        <section id="feedbacks" class="relative bg-white py-10">
            <div class="max-w-4xl mx-auto px-4 text-center">
                <h2 class="text-3xl font-bold mb-6 text-gray-900">CẢM NHẬN TỪ HỘI VIÊN</h2>

                <div v-if="feedbacks.length === 0" class="text-gray-500">Đang tải feedback...</div>

                <div v-else class="relative overflow-hidden">
                    <!-- Feedback hiển thị -->
                    <transition name="fade" mode="out-in">
                        <div
                            :key="feedbacks[currentIndex].id"
                            class="bg-gray-50 border rounded-xl shadow-md p-6"
                        >
                            <div class="text-yellow-500 text-lg mb-2">
                                ⭐ {{ feedbacks[currentIndex].rating }}/5
                            </div>
                            <p class="text-gray-700 italic mb-4">
                                "{{ feedbacks[currentIndex].comment }}"
                            </p>

                            <div class="mb-4">
                                <p class="font-semibold text-gray-900">
                                    {{ feedbacks[currentIndex].member.fullName }}
                                </p>
                            </div>

                            <div
                                v-if="feedbacks[currentIndex].attachments?.length"
                                class="flex justify-center gap-2 mt-2"
                            >
                                <img
                                    v-for="img in feedbacks[currentIndex].attachments"
                                    :key="img.id"
                                    :src="`http://localhost:8080/${img.filePath}`"
                                    alt="feedback image"
                                    class="w-24 h-24 object-cover rounded-md border"
                                />
                            </div>
                        </div>
                    </transition>

                    <!-- Nút điều hướng -->
                    <button
                        @click="prevFeedback"
                        class="absolute left-0 top-1/2 -translate-y-1/2 bg-black/40 hover:bg-black/70 text-white rounded-full w-8 h-8 flex items-center justify-center"
                    >
                        ‹
                    </button>
                    <button
                        @click="nextFeedback"
                        class="absolute right-0 top-1/2 -translate-y-1/2 bg-black/40 hover:bg-black/70 text-white rounded-full w-8 h-8 flex items-center justify-center"
                    >
                        ›
                    </button>

                    <!-- Dấu chấm trạng thái -->
                    <div class="flex justify-center mt-3 space-x-2">
                        <span
                            v-for="(f, i) in feedbacks"
                            :key="f.id"
                            class="w-2 h-2 rounded-full transition-all"
                            :class="i === currentIndex ? 'bg-red-600 w-4' : 'bg-gray-300'"
                        ></span>
                    </div>
                </div>
            </div>
        </section>

        <!-- Classes (service tiles) -->
        <section id="classes" class="service-contain bg-black py-4">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 px-2  text-white">
                <div class="grid gap-4">
                    <a class="block group rounded-xl overflow-hidden relative" href="https://cali.vn/coaching/dance" target="_blank">
                        <img class="w-full h-[360px] object-cover transition-transform duration-300 group-hover:scale-105" src="https://cali.vn/storage/app/media/2023/Home%20page/cali-community_900x1200px-new-min.jpg" alt="Dance">
                        <div class="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent"></div>
                        <div class="absolute bottom-4 left-4 text-white">
                            <h3 class="text-xl font-semibold">Chương Trình Nhảy Độc Đáo</h3>
                            <span class="text-sm opacity-90">Tìm hiểu thêm →</span>
                        </div>
                    </a>
                    <a class="block group rounded-xl overflow-hidden relative" href="https://cali.vn/training/chuong-trinh-yogii" target="_blank">
                        <img class="w-full h-[360px] object-cover transition-transform duration-300 group-hover:scale-105" src="https://cali.vn/storage/app/media/2025/Homepage/Community-VN-shirt.webp" alt="Community">
                        <div class="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent"></div>
                        <div class="absolute bottom-4 left-4 text-white">
                            <h3 class="text-xl font-semibold">Cộng Đồng Truyền Cảm Hứng</h3>
                            <span class="text-sm opacity-90">Tìm hiểu thêm →</span>
                        </div>
                    </a>
                    <a class="block group rounded-xl overflow-hidden relative" href="https://cali.vn/coaching/yoga" target="_blank">
                        <img class="w-full h-[360px] object-cover transition-transform duration-300 group-hover:scale-105" src="https://cali.vn/storage/app/media/2024/Homepage/ori-yoga.webp" alt="Yoga">
                        <div class="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent"></div>
                        <div class="absolute bottom-4 left-4 text-white">
                            <h3 class="text-xl font-semibold">Tinh Hoa Yoga Ấn Độ</h3>
                            <span class="text-sm opacity-90">Tìm hiểu thêm →</span>
                        </div>
                    </a>
                </div>
                <div class="grid gap-4">
                    <a class="block group rounded-xl overflow-hidden relative" href="https://cali.vn/training/huan-luyen-vien-suc-khoe" target="_blank">
                        <img class="w-full h-[550px] object-cover transition-transform duration-300 group-hover:scale-105" src="https://cali.vn/storage/app/media/2025/Homepage/Cali-PT.webp" alt="PT">
                        <div class="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent"></div>
                        <div class="absolute bottom-4 left-4 text-white">
                            <h3 class="text-xl font-semibold">Huấn Luyện Viên Sức Khỏe</h3>
                            <span class="text-sm opacity-90">Tìm hiểu thêm →</span>
                        </div>
                    </a>
                    <a class="block group rounded-xl overflow-hidden relative" href="https://cali.vn/coaching/group-x" target="_blank">
                        <img class="w-full h-[550px] object-cover transition-transform duration-300 group-hover:scale-105" src="https://cali.vn/storage/app/media/2024/Homepage/multi-choices.png" alt="Group X">
                        <div class="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent"></div>
                        <div class="absolute bottom-4 left-4 text-white">
                            <h3 class="text-xl font-semibold">Không Giới Hạn Lớp Tập Nhóm</h3>
                            <span class="text-sm opacity-90">Tìm hiểu thêm →</span>
                        </div>
                    </a>
                </div>
            </div>
        </section>

        <!-- Find club -->
        <section id="clubs" class="clb-nearly">
            <div class="grid md:grid-cols-2 gap-6 items-center px-2 bg-black pb-4">
                <div>
                    <h2 class="text-4xl font-bold text-white">TIÊU CHUẨN CỦA SỰ SANG TRỌNG</h2>
                    <p class="mt-2 text-white dark:text-gray-300">Tập luyện tại hơn 37+ Câu lạc bộ khắp cả nước và tận hưởng môi trường tập luyện đẳng cấp với xông hơi, hồ bơi, jacuzzi.</p>
                    <a href="https://cali.vn/phong-tap-gym-the-hinh-yoga-toan-quoc" target="_blank" class="inline-flex items-center gap-2 mt-3 text-white hover:text-red-600 font-semibold">Tìm Câu Lạc Bộ Gần Bạn →</a>
                </div>
                <div>
                    <img class="w-full rounded-xl" src="https://cali.vn/storage/app/media/2021/Membership/Big-workout_area.jpg" alt="Club luxury">
                </div>
            </div>
        </section>

        <!-- Trial form -->
        <section id="trial" class="rounded-xl bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 p-6">
            <div class="grid md:grid-cols-2 gap-6 items-start">
                <div>
                    <h2 class="text-2xl font-bold">TRẢI NGHIỆM MIỄN PHÍ NGAY!</h2>
                    <p class="mt-2 text-gray-600 dark:text-gray-300">Dùng thử 7 ngày cho khách mới. Để lại thông tin, chúng tôi sẽ liên hệ.</p>
                </div>
                <form @submit.prevent="handleTrialSubmit" class="space-y-4" aria-label="Form đăng ký trải nghiệm">
                    <div>
                        <label class="sr-only" for="trial-name">Họ và Tên</label>
                        <input v-model="trialName" id="trial-name" type="text" placeholder="Họ và Tên *" class="w-full rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 px-3 py-2 text-sm" required>
                    </div>
                    <div>
                        <label class="sr-only" for="trial-phone">Số điện thoại</label>
                        <input v-model="trialPhone" id="trial-phone" type="tel" placeholder="Số điện thoại *" class="w-full rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 px-3 py-2 text-sm" required>
                    </div>
                    <div>
                        <label class="sr-only" for="trial-email">Email</label>
                        <input v-model="trialEmail" id="trial-email" type="email" placeholder="Email" class="w-full rounded-md border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 px-3 py-2 text-sm">
                    </div>
                    <button type="submit" class="inline-flex items-center rounded-md bg-red-600 px-5 py-2.5 text-white text-sm font-semibold hover:bg-red-500">ĐĂNG KÝ</button>
                </form>
            </div>
        </section>

        <!-- Blog -->
        <section id="blog" class="grey-contain px-2 py-4 bg-gray-300">
            <div class="flex items-center justify-between gap-2">
                <h3 class="text-xl font-bold">ĐỪNG BỎ LỠ BÀI VIẾT HỮU ÍCH</h3>
                <a href="https://cali.vn/blog" target="_blank" class="inline-flex items-center gap-2 text-red-600 font-semibold">Xem tất cả →</a>
            </div>
            <div class="mt-4 grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <a href="https://cali.vn/blog/cong-nghe-exbody-nen-tang-cho-tap-luyen-hieu-chinh-giup-giam-dau-va-cai-thien-van-dong" target="_blank" class="block rounded-lg overflow-hidden border border-gray-200 dark:border-gray-800">
                    <img class="w-full h-300px object-cover" src="https://cali.vn/storage/app/media/2025/Blog/Corrective%20Exercise/CE-4.webp" alt="ExBody">
                    <div class="p-3 text-sm font-medium">Công nghệ ExBody - Nền Tảng Cho Tập Luyện Hiệu Chỉnh...</div>
                </a>
                <a href="https://cali.vn/blog/hoa-hau-huong-giang-saabirose-xuat-hien-bung-no-tai-hnoise-2025" target="_blank" class="block rounded-lg overflow-hidden border border-gray-200 dark:border-gray-800">
                    <img class="w-full h-300px object-cover" src="https://cali.vn/storage/app/media/2025/Blog/HUONG%20GIANG.webp" alt="HNOISE">
                    <div class="p-3 text-sm font-medium">Hoa Hậu Hương Giang & Saabirose Xuất Hiện Bùng Nổ...</div>
                </a>
                <a href="https://cali.vn/blog/ngoi-nhieu-cung-nguy-hiem-nhu-hut-thuoc-canh-bao-cho-dan-van-phong" target="_blank" class="block rounded-lg overflow-hidden border border-gray-200 dark:border-gray-800">
                    <img class="w-full h-300px object-cover" src="https://cali.vn/storage/app/media/2025/Blog/Corrective%20Exercise/hut-thuoc.webp" alt="Sitting hazard">
                    <div class="p-3 text-sm font-medium">Ngồi Nhiều Cũng Nguy Hiểm Như Hút Thuốc...</div>
                </a>
                <a href="https://cali.vn/blog/so-tai-luc-tai-hnoise-2025-cung-california" target="_blank" class="block rounded-lg overflow-hidden border border-gray-200 dark:border-gray-800">
                    <img class="w-full h-300px object-cover" src="https://cali.vn/storage/app/media/2025/Blog/HNOISE-blog.webp" alt="So tai luc">
                    <div class="p-3 text-sm font-medium">So Tài Thể Lực Tại THE HNOISE 2025...</div>
                </a>
            </div>
        </section>
    </div>
</template>
<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.6s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
