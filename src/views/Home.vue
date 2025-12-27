<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

// ================== Trial Form ==================
const trialName = ref('')
const trialPhone = ref('')
const trialEmail = ref('')
const trialPreferredDate = ref('')

function handleTrialSubmit() {
  if (!trialName.value || !trialPhone.value) return
  localStorage.setItem('custom_name', trialName.value)
  localStorage.setItem('custom_phone', trialPhone.value)
  if (trialEmail.value) localStorage.setItem('custom_email', trialEmail.value)
  if (trialPreferredDate.value) localStorage.setItem('custom_preferred_date', trialPreferredDate.value)
  alert('Đã gửi đăng ký. Chúng tôi sẽ liên hệ sớm!')
  // Reset form
  trialName.value = ''
  trialPhone.value = ''
  trialEmail.value = ''
  trialPreferredDate.value = ''
}

// ================== Feedback Pagination ==================
const feedbacks = ref([])
const currentPage = ref(0)
const itemsPerPage = 3

const totalPages = computed(() => Math.ceil(feedbacks.value.length / itemsPerPage))

const paginatedFeedbacks = computed(() => {
  const start = currentPage.value * itemsPerPage
  const end = start + itemsPerPage
  return feedbacks.value.slice(start, end)
})

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
  }
}

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/feedbacks')
    feedbacks.value = response.data
      .sort((a, b) => b.id - a.id)
  } catch (error) {
    console.error('Lỗi khi tải feedback:', error)
  }
})

// ================== Banner Carousel ==================
const banners = ref<{ id: number; value: string; public: boolean }[]>([])
const bannerIndex = ref(0)

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/config/banner/public')
    banners.value = res.data
  } catch (err) {
    console.error('Lỗi tải banner:', err)
  }
})

onMounted(() => {
  setInterval(() => {
    if (banners.value.length > 1) {
      bannerIndex.value = (bannerIndex.value + 1) % banners.value.length
    }
  }, 4000)
})

// ================== Smooth Scroll ==================
function scrollToTrial() {
  const trialSection = document.getElementById('trial')
  if (trialSection) {
    trialSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}
</script>

<template>
    <div class="bg-gray-50">
        <!-- Banner Section -->
        <section id="banner" class="relative overflow-hidden rounded-xl">
            <div class="aspect-[19/8] w-full bg-black rounded-xl overflow-hidden relative">
                <img
                v-for="(banner, index) in banners"
                :key="banner.id"
                :src="`http://localhost:8080${banner.value}`"
                :alt="`Banner ${index + 1}`"
                class="w-full h-full object-cover absolute top-0 left-0 transition-opacity duration-700"
                :class="bannerIndex === index ? 'opacity-100 z-10' : 'opacity-0 z-0'"
                />
            </div>

            <!-- CTA button -->
            <div class="absolute inset-0 flex items-end p-6">
                <button
                @click="scrollToTrial"
                class="btn-submit-banner inline-flex items-center rounded-md bg-red-600 px-5 py-3 text-white text-sm font-semibold hover:bg-red-500 transition-all hover:scale-105"
                >
                Giữ Chỗ Ưu Đãi!
                </button>
            </div>

            <!-- Navigation -->
            <div class="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex gap-2">
                <button
                v-for="(banner, index) in banners"
                :key="index"
                @click="bannerIndex = index"
                :class="bannerIndex === index ? 'bg-white' : 'bg-gray-400'"
                class="w-3 h-3 rounded-full transition-all"
                ></button>
            </div>
        </section>

        <!-- Membership Pricing Section -->
        <section id="pricing" class="py-16 px-4 bg-gradient-to-br from-gray-50 to-gray-100">
            <div class="max-w-7xl mx-auto">
                <div class="text-center mb-12">
                    <h2 class="text-4xl font-bold text-gray-900 mb-4">GÓI HỘI VIÊN</h2>
                    <p class="text-lg text-gray-600">Chọn gói phù hợp với mục tiêu của bạn</p>
                </div>

                <div class="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
                    <!-- Basic Package -->
                    <div class="bg-white rounded-2xl shadow-lg p-8 border-2 border-gray-200 hover:border-blue-400 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
                        <div class="text-center mb-6">
                            <h3 class="text-2xl font-bold text-gray-900 mb-2">BASIC</h3>
                            <div class="text-4xl font-bold text-blue-600 mb-2">
                                <span class="text-lg">từ</span> 500K
                            </div>
                            <p class="text-gray-500 text-sm">/ tháng</p>
                        </div>
                        <ul class="space-y-4 mb-8">
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-blue-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Truy cập phòng tập cơ bản</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-blue-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Tập luyện giờ hành chính</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-blue-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Tủ đồ miễn phí</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-blue-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Hỗ trợ từ nhân viên</span>
                            </li>
                        </ul>
                        <button class="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors">
                            Đăng Ký Ngay
                        </button>
                    </div>

                    <!-- Standard Package -->
                    <div class="bg-white rounded-2xl shadow-2xl p-8 border-2 border-purple-500 relative hover:shadow-3xl transition-all duration-300 hover:-translate-y-2 transform scale-105">
                        <div class="absolute -top-4 left-1/2 transform -translate-x-1/2 bg-purple-500 text-white px-4 py-1 rounded-full text-sm font-semibold">
                            PHỔ BIẾN NHẤT
                        </div>
                        <div class="text-center mb-6 mt-2">
                            <h3 class="text-2xl font-bold text-gray-900 mb-2">STANDARD</h3>
                            <div class="text-4xl font-bold text-purple-600 mb-2">
                                <span class="text-lg">từ</span> 800K
                            </div>
                            <p class="text-gray-500 text-sm">/ tháng</p>
                        </div>
                        <ul class="space-y-4 mb-8">
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-purple-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700 font-medium">Tất cả quyền lợi Basic</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-purple-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Tập luyện không giới hạn giờ</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-purple-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">2 buổi PT miễn phí/tháng</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-purple-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Tham gia lớp nhóm</span>
                            </li>
                        </ul>
                        <button 
                        @click="scrollToTrial"
                        class="w-full bg-gradient-to-r from-purple-600 to-purple-700 text-white py-3 rounded-lg font-semibold hover:from-purple-700 hover:to-purple-800 transition-all mb-3 shadow-lg hover:shadow-xl">
                            Đăng Ký Tập Thử
                        </button>
                        <button class="w-full bg-purple-600 text-white py-3 rounded-lg font-semibold hover:bg-purple-700 transition-colors">
                            Đăng Ký Ngay
                        </button>
                    </div>

                    <!-- VIP Package -->
                    <div class="bg-gradient-to-br from-amber-50 to-yellow-50 rounded-2xl shadow-lg p-8 border-2 border-amber-400 relative hover:border-amber-500 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
                        <div class="absolute -top-4 left-1/2 transform -translate-x-1/2 bg-gradient-to-r from-amber-500 to-yellow-500 text-white px-4 py-1 rounded-full text-sm font-semibold">
                            CAO CẤP
                        </div>
                        <div class="text-center mb-6 mt-2">
                            <h3 class="text-2xl font-bold text-gray-900 mb-2">VIP</h3>
                            <div class="text-4xl font-bold bg-gradient-to-r from-amber-600 to-yellow-600 bg-clip-text text-transparent mb-2">
                                <span class="text-lg">từ</span> 1.5M
                            </div>
                            <p class="text-gray-500 text-sm">/ tháng</p>
                        </div>
                        <ul class="space-y-4 mb-8">
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-amber-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700 font-medium">Tất cả quyền lợi Standard</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-amber-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700 font-semibold">Đăng ký lớp học sớm (ưu tiên)</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-amber-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">PT không giới hạn</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-amber-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Xông hơi & Jacuzzi</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-amber-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Đồ uống miễn phí</span>
                            </li>
                            <li class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-amber-600 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Ưu đãi sản phẩm 20%</span>
                            </li>
                        </ul>
                        <button class="w-full bg-gradient-to-r from-amber-500 to-yellow-500 text-white py-3 rounded-lg font-semibold hover:from-amber-600 hover:to-yellow-600 transition-all shadow-lg">
                            Đăng Ký Ngay
                        </button>
                    </div>
                </div>
            </div>
        </section>

        <!-- Why Choose Us Section -->
        <section id="why-choose-us" class="py-16 px-4 bg-white">
            <div class="max-w-7xl mx-auto">
                <div class="text-center mb-12">
                    <h2 class="text-4xl font-bold text-gray-900 mb-4">TẠI SAO CHỌN CHÚNG TÔI?</h2>
                    <p class="text-lg text-gray-600">Những lý do khiến hàng nghìn hội viên tin tưởng</p>
                </div>

                <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
                    <!-- Reason 1 -->
                    <div class="text-center group">
                        <div class="bg-gradient-to-br from-blue-500 to-blue-600 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform duration-300 shadow-lg">
                            <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path>
                            </svg>
                        </div>
                        <h3 class="text-xl font-bold text-gray-900 mb-2">Trang Thiết Bị Hiện Đại</h3>
                        <p class="text-gray-600">Máy móc nhập khẩu cao cấp, cập nhật công nghệ mới nhất</p>
                    </div>

                    <!-- Reason 2 -->
                    <div class="text-center group">
                        <div class="bg-gradient-to-br from-purple-500 to-purple-600 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform duration-300 shadow-lg">
                            <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path>
                            </svg>
                        </div>
                        <h3 class="text-xl font-bold text-gray-900 mb-2">Huấn Luyện Viên Chuyên Nghiệp</h3>
                        <p class="text-gray-600">Đội ngũ PT có chứng chỉ quốc tế, tận tâm hỗ trợ</p>
                    </div>

                    <!-- Reason 3 -->
                    <div class="text-center group">
                        <div class="bg-gradient-to-br from-green-500 to-green-600 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform duration-300 shadow-lg">
                            <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                            </svg>
                        </div>
                        <h3 class="text-xl font-bold text-gray-900 mb-2">Lịch Tập Linh Hoạt</h3>
                        <p class="text-gray-600">Mở cửa 24/7, phù hợp mọi lịch trình bận rộn</p>
                    </div>

                    <!-- Reason 4 -->
                    <div class="text-center group">
                        <div class="bg-gradient-to-br from-red-500 to-red-600 w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-4 group-hover:scale-110 transition-transform duration-300 shadow-lg">
                            <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
                            </svg>
                        </div>
                        <h3 class="text-xl font-bold text-gray-900 mb-2">Cộng Đồng Năng Động</h3>
                        <p class="text-gray-600">Kết nối với hội viên nhiệt huyết, cùng nhau tiến bộ</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- Classes (service tiles) -->
        <section id="classes" class="service-contain bg-black py-8 px-4">
            <div class="max-w-7xl mx-auto">
                <div class="text-center mb-8">
                    <h2 class="text-4xl font-bold text-white mb-4">CÁC CHƯƠNG TRÌNH TẬP LUYỆN</h2>
                    <p class="text-lg text-gray-300">Đa dạng lớp học phù hợp mọi nhu cầu</p>
                </div>
                <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
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
            </div>
        </section>

        <!-- Find club -->
        <section id="clubs" class="clb-nearly bg-black py-12 px-4">
            <div class="max-w-7xl mx-auto">
                <div class="grid md:grid-cols-2 gap-8 items-center">
                    <div>
                        <h2 class="text-4xl font-bold text-white mb-4">TIÊU CHUẨN CỦA SỰ SANG TRỌNG</h2>
                        <p class="text-lg text-gray-300 mb-6">Tập luyện tại hơn 37+ Câu lạc bộ khắp cả nước và tận hưởng môi trường tập luyện đẳng cấp với xông hơi, hồ bơi, jacuzzi.</p>
                        <a href="https://cali.vn/phong-tap-gym-the-hinh-yoga-toan-quoc" target="_blank" class="inline-flex items-center gap-2 text-white hover:text-red-500 font-semibold text-lg transition-colors">
                            Tìm Câu Lạc Bộ Gần Bạn →
                        </a>
                    </div>
                    <div>
                        <img class="w-full rounded-xl shadow-2xl" src="https://cali.vn/storage/app/media/2021/Membership/Big-workout_area.jpg" alt="Club luxury">
                    </div>
                </div>
            </div>
        </section>

        <!-- What Customers Say (Feedback) -->
        <section id="feedbacks" class="py-16 px-4 bg-gradient-to-br from-gray-50 to-gray-100">
            <div class="max-w-7xl mx-auto">
                <div class="text-center mb-12">
                    <h2 class="text-4xl font-bold text-gray-900 mb-4">KHÁCH HÀNG NÓI GÌ VỀ CHÚNG TÔI</h2>
                    <p class="text-lg text-gray-600">Những phản hồi chân thực từ hội viên</p>
                </div>

                <div v-if="feedbacks.length === 0" class="text-center text-gray-500 text-lg">
                    Đang tải feedback...
                </div>

                <div v-else>
                    <!-- Feedback Grid -->
                    <div class="grid md:grid-cols-3 gap-6 mb-8">
                        <div
                            v-for="feedback in paginatedFeedbacks"
                            :key="feedback.id"
                            class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300"
                        >
                            <div class="flex items-center justify-center mb-4">
                                <div class="flex gap-1">
                                    <svg v-for="star in 5" :key="star" class="w-5 h-5" :class="star <= feedback.rating ? 'text-yellow-400' : 'text-gray-300'" fill="currentColor" viewBox="0 0 20 20">
                                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                                    </svg>
                                </div>
                                <span class="ml-2 text-gray-600 font-semibold">{{ feedback.rating }}/5</span>
                            </div>
                            
                            <p class="text-gray-700 italic mb-4 text-center min-h-[80px]">
                                "{{ feedback.comment }}"
                            </p>

                            <div class="border-t pt-4">
                                <p class="font-bold text-gray-900 text-center">
                                    {{ feedback.member.fullName }}
                                </p>
                            </div>

                            <div
                                v-if="feedback.attachments?.length"
                                class="flex justify-center gap-2 mt-4"
                            >
                                <img
                                    v-for="img in feedback.attachments"
                                    :key="img.id"
                                    :src="`http://localhost:8080/${img.filePath}`"
                                    alt="feedback image"
                                    class="w-20 h-20 object-cover rounded-lg border-2 border-gray-200"
                                />
                            </div>
                        </div>
                    </div>

                    <!-- Pagination Controls -->
                    <div class="flex items-center justify-center gap-4">
                        <button
                            @click="prevPage"
                            :disabled="currentPage === 0"
                            :class="currentPage === 0 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-800'"
                            class="px-6 py-3 bg-gray-700 text-white rounded-lg font-semibold transition-colors disabled:hover:bg-gray-700"
                        >
                            ← Trang Trước
                        </button>
                        
                        <span class="text-gray-700 font-semibold">
                            Trang {{ currentPage + 1 }} / {{ totalPages }}
                        </span>
                        
                        <button
                            @click="nextPage"
                            :disabled="currentPage >= totalPages - 1"
                            :class="currentPage >= totalPages - 1 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-800'"
                            class="px-6 py-3 bg-gray-700 text-white rounded-lg font-semibold transition-colors disabled:hover:bg-gray-700"
                        >
                            Trang Sau →
                        </button>
                    </div>
                </div>
            </div>
        </section>

        <!-- Trial form -->
        <section id="trial" class="py-16 px-4 bg-white">
            <div class="max-w-5xl mx-auto">
                <div class="bg-gradient-to-br from-red-50 to-orange-50 rounded-2xl shadow-xl p-8 md:p-12 border-2 border-red-200">
                    <div class="grid md:grid-cols-2 gap-8 items-start">
                        <div>
                            <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-4">TRẢI NGHIỆM MIỄN PHÍ NGAY!</h2>
                            <p class="text-lg text-gray-700 mb-6">Dùng thử 7 ngày cho khách mới. Để lại thông tin, chúng tôi sẽ liên hệ tư vấn và hỗ trợ bạn.</p>
                            <div class="flex items-start gap-3 mb-3">
                                <svg class="w-6 h-6 text-red-600 flex-shrink-0 mt-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Trải nghiệm đầy đủ tiện ích</span>
                            </div>
                            <div class="flex items-start gap-3 mb-3">
                                <svg class="w-6 h-6 text-red-600 flex-shrink-0 mt-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Tư vấn lộ trình tập luyện</span>
                            </div>
                            <div class="flex items-start gap-3">
                                <svg class="w-6 h-6 text-red-600 flex-shrink-0 mt-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                                </svg>
                                <span class="text-gray-700">Không ràng buộc, không phí ẩn</span>
                            </div>
                        </div>
                        <form @submit.prevent="handleTrialSubmit" class="space-y-4 bg-white p-6 rounded-xl shadow-lg" aria-label="Form đăng ký trải nghiệm">
                            <div>
                                <label class="block text-sm font-semibold text-gray-700 mb-2" for="trial-name">Họ và Tên *</label>
                                <input 
                                    v-model="trialName" 
                                    id="trial-name" 
                                    type="text" 
                                    placeholder="Nhập họ và tên của bạn" 
                                    class="w-full rounded-lg border-2 border-gray-300 focus:border-red-500 focus:ring-2 focus:ring-red-200 bg-white px-4 py-3 text-sm transition-colors outline-none" 
                                    required
                                >
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-gray-700 mb-2" for="trial-phone">Số điện thoại *</label>
                                <input 
                                    v-model="trialPhone" 
                                    id="trial-phone" 
                                    type="tel" 
                                    placeholder="Nhập số điện thoại" 
                                    class="w-full rounded-lg border-2 border-gray-300 focus:border-red-500 focus:ring-2 focus:ring-red-200 bg-white px-4 py-3 text-sm transition-colors outline-none" 
                                    required
                                >
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-gray-700 mb-2" for="trial-email">Email</label>
                                <input 
                                    v-model="trialEmail" 
                                    id="trial-email" 
                                    type="email" 
                                    placeholder="Nhập email (không bắt buộc)" 
                                    class="w-full rounded-lg border-2 border-gray-300 focus:border-red-500 focus:ring-2 focus:ring-red-200 bg-white px-4 py-3 text-sm transition-colors outline-none"
                                >
                            </div>
                            <div>
                                <label class="block text-sm font-semibold text-gray-700 mb-2" for="trial-preferred-date">Ngày muốn tập thử</label>
                                <input 
                                    v-model="trialPreferredDate" 
                                    id="trial-preferred-date" 
                                    type="date" 
                                    class="w-full rounded-lg border-2 border-gray-300 focus:border-red-500 focus:ring-2 focus:ring-red-200 bg-white px-4 py-3 text-sm transition-colors outline-none"
                                >
                            </div>
                            <button 
                                type="submit" 
                                class="w-full inline-flex items-center justify-center rounded-lg bg-gradient-to-r from-red-600 to-red-700 px-6 py-3.5 text-white text-base font-bold hover:from-red-700 hover:to-red-800 transition-all shadow-lg hover:shadow-xl hover:scale-105"
                            >
                                ĐĂNG KÝ NGAY
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </section>

        <!-- Blog -->
        <section id="blog" class="grey-contain px-4 py-12 bg-gray-200">
            <div class="max-w-7xl mx-auto">
                <div class="flex items-center justify-between mb-8">
                    <h3 class="text-3xl font-bold text-gray-900">ĐỪNG BỎ LỠ BÀI VIẾT HỮU ÍCH</h3>
                    <a href="https://cali.vn/blog" target="_blank" class="inline-flex items-center gap-2 text-red-600 font-semibold hover:text-red-700 transition-colors">
                        Xem tất cả →
                    </a>
                </div>
                <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
                    <a href="https://cali.vn/blog/cong-nghe-exbody-nen-tang-cho-tap-luyen-hieu-chinh-giup-giam-dau-va-cai-thien-van-dong" target="_blank" class="block rounded-xl overflow-hidden border-2 border-gray-300 hover:border-red-500 transition-all hover:shadow-lg bg-white">
                        <img class="w-full h-48 object-cover" src="https://cali.vn/storage/app/media/2025/Blog/Corrective%20Exercise/CE-4.webp" alt="ExBody">
                        <div class="p-4 text-sm font-medium text-gray-800">Công nghệ ExBody - Nền Tảng Cho Tập Luyện Hiệu Chỉnh...</div>
                    </a>
                    <a href="https://cali.vn/blog/hoa-hau-huong-giang-saabirose-xuat-hien-bung-no-tai-hnoise-2025" target="_blank" class="block rounded-xl overflow-hidden border-2 border-gray-300 hover:border-red-500 transition-all hover:shadow-lg bg-white">
                        <img class="w-full h-48 object-cover" src="https://cali.vn/storage/app/media/2025/Blog/HUONG%20GIANG.webp" alt="HNOISE">
                        <div class="p-4 text-sm font-medium text-gray-800">Hoa Hậu Hương Giang & Saabirose Xuất Hiện Bùng Nổ...</div>
                    </a>
                    <a href="https://cali.vn/blog/ngoi-nhieu-cung-nguy-hiem-nhu-hut-thuoc-canh-bao-cho-dan-van-phong" target="_blank" class="block rounded-xl overflow-hidden border-2 border-gray-300 hover:border-red-500 transition-all hover:shadow-lg bg-white">
                        <img class="w-full h-48 object-cover" src="https://cali.vn/storage/app/media/2025/Blog/Corrective%20Exercise/hut-thuoc.webp" alt="Sitting hazard">
                        <div class="p-4 text-sm font-medium text-gray-800">Ngồi Nhiều Cũng Nguy Hiểm Như Hút Thuốc...</div>
                    </a>
                    <a href="https://cali.vn/blog/so-tai-luc-tai-hnoise-2025-cung-california" target="_blank" class="block rounded-xl overflow-hidden border-2 border-gray-300 hover:border-red-500 transition-all hover:shadow-lg bg-white">
                        <img class="w-full h-48 object-cover" src="https://cali.vn/storage/app/media/2025/Blog/HNOISE-blog.webp" alt="So tai luc">
                        <div class="p-4 text-sm font-medium text-gray-800">So Tài Thể Lực Tại THE HNOISE 2025...</div>
                    </a>
                </div>
            </div>
        </section>
    </div>
</template>

<style scoped>
/* Smooth scrolling */
html {
  scroll-behavior: smooth;
}
</style>