<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { Eye } from 'lucide-vue-next'
import axios from 'axios'

const router = useRouter()
const toast = useToast()

const blogs = ref([])
const loading = ref(false)

// Lấy tất cả blog
const fetchBlogs = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/blogs')
    blogs.value = response.data
  } catch (error) {
    toast.error('Lỗi khi tải danh sách blog')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// Format ngày
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// Xem chi tiết blog
const viewBlog = (blogId) => {
  router.push(`/blog/${blogId}`)
}

onMounted(() => {
  fetchBlogs()
  document.title = 'Bài Viết - Gym Management'
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-12 px-4">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">Bài Viết Hữu Ích</h1>
        <p class="text-lg text-gray-600">Khám phá các bài viết về sức khỏe và tập luyện</p>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Đang tải...</p>
      </div>

      <!-- Blog Grid -->
      <div v-else-if="blogs.length > 0" class="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
        <div
          v-for="blog in blogs"
          :key="blog.id"
          class="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-2xl transition-all duration-300 hover:-translate-y-1 cursor-pointer"
          @click="viewBlog(blog.id)"
        >
          <!-- Ảnh bìa -->
          <div class="relative h-56 overflow-hidden">
            <img
              v-if="blog.coverImagePath"
              :src="`http://localhost:8080${blog.coverImagePath}`"
              :alt="blog.title"
              class="w-full h-full object-cover transition-transform duration-300 hover:scale-110"
            />
            <div v-else class="w-full h-full bg-gradient-to-br from-gray-300 to-gray-400 flex items-center justify-center">
              <span class="text-gray-600 text-lg">Không có ảnh</span>
            </div>
            
            <!-- Badge ghim -->
            <div v-if="blog.isPinned" class="absolute top-3 right-3">
              <span class="bg-yellow-500 text-white px-3 py-1 rounded-full text-xs font-semibold shadow-lg">
                📌 Ghim
              </span>
            </div>
          </div>

          <!-- Nội dung -->
          <div class="p-6">
            <h3 class="text-xl font-bold text-gray-900 mb-3 line-clamp-2 hover:text-blue-600 transition-colors">
              {{ blog.title }}
            </h3>
            
            <div class="flex items-center gap-4 text-sm text-gray-500 mb-4">
              <div class="flex items-center gap-1">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                </svg>
                <span>{{ blog.authorName }}</span>
              </div>
              <div class="flex items-center gap-1">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                </svg>
                <span>{{ formatDate(blog.publishDate) }}</span>
              </div>
            </div>

            <button
              class="flex items-center gap-2 text-blue-600 hover:text-blue-700 font-semibold transition-colors"
            >
              <Eye :size="18" />
              Xem chi tiết
            </button>
          </div>
        </div>
      </div>

      <!-- Empty state -->
      <div v-else class="text-center py-20">
        <svg class="w-24 h-24 mx-auto text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
        </svg>
        <p class="text-gray-500 text-xl">Chưa có bài viết nào</p>
      </div>

      <!-- Back to Home -->
      <div class="mt-12 text-center">
        <router-link
          to="/"
          class="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 font-semibold transition-colors"
        >
          ← Quay lại trang chủ
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Line clamp utility */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
