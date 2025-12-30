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

// Xem chi tiết blog (có thể mở modal hoặc navigate)
const viewBlog = (blog) => {
  // Tạm thời hiển thị nội dung trong modal hoặc có thể tạo trang detail riêng
  // Ở đây tôi sẽ tạo một modal đơn giản
  selectedBlog.value = blog
  showModal.value = true
}

const selectedBlog = ref(null)
const showModal = ref(false)

const closeModal = () => {
  showModal.value = false
  selectedBlog.value = null
}

onMounted(() => {
  fetchBlogs()
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
          @click="viewBlog(blog)"
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
    </div>

    <!-- Modal xem chi tiết -->
    <div
      v-if="showModal && selectedBlog"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click.self="closeModal"
    >
      <div class="bg-white rounded-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
        <!-- Header -->
        <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
          <h2 class="text-2xl font-bold text-gray-900">{{ selectedBlog.title }}</h2>
          <button
            @click="closeModal"
            class="text-gray-500 hover:text-gray-700 transition-colors"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- Ảnh bìa -->
        <div v-if="selectedBlog.coverImagePath" class="w-full">
          <img
            :src="`http://localhost:8080${selectedBlog.coverImagePath}`"
            :alt="selectedBlog.title"
            class="w-full h-96 object-cover"
          />
        </div>

        <!-- Metadata -->
        <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
          <div class="flex items-center gap-6 text-sm text-gray-600">
            <div class="flex items-center gap-2">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
              <span class="font-medium">{{ selectedBlog.authorName }}</span>
            </div>
            <div class="flex items-center gap-2">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
              </svg>
              <span>{{ formatDate(selectedBlog.publishDate) }}</span>
            </div>
            <div v-if="selectedBlog.isPinned" class="flex items-center gap-2 text-yellow-600">
              <span class="font-semibold">📌 Bài viết được ghim</span>
            </div>
          </div>
        </div>

        <!-- Nội dung -->
        <div class="px-6 py-8">
          <div class="prose prose-lg max-w-none" v-html="selectedBlog.content"></div>
        </div>

        <!-- Footer -->
        <div class="sticky bottom-0 bg-gray-50 border-t border-gray-200 px-6 py-4">
          <button
            @click="closeModal"
            class="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors"
          >
            Đóng
          </button>
        </div>
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

/* Prose styles for blog content */
:deep(.prose) {
  color: #374151;
}

:deep(.prose h1) {
  font-size: 2em;
  font-weight: 700;
  margin-top: 1em;
  margin-bottom: 0.5em;
}

:deep(.prose h2) {
  font-size: 1.5em;
  font-weight: 600;
  margin-top: 1em;
  margin-bottom: 0.5em;
}

:deep(.prose p) {
  margin-top: 1em;
  margin-bottom: 1em;
  line-height: 1.75;
}

:deep(.prose img) {
  max-width: 100%;
  height: auto;
  border-radius: 0.5rem;
  margin: 1.5em 0;
}

:deep(.prose a) {
  color: #2563eb;
  text-decoration: underline;
}

:deep(.prose ul),
:deep(.prose ol) {
  margin-top: 1em;
  margin-bottom: 1em;
  padding-left: 1.5em;
}

:deep(.prose li) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}

:deep(.prose blockquote) {
  border-left: 4px solid #e5e7eb;
  padding-left: 1em;
  font-style: italic;
  color: #6b7280;
  margin: 1.5em 0;
}

:deep(.prose code) {
  background-color: #f3f4f6;
  padding: 0.2em 0.4em;
  border-radius: 0.25rem;
  font-size: 0.875em;
}

:deep(.prose pre) {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1em;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1.5em 0;
}
</style>
