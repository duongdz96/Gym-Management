<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from 'vue-toastification'
import { ArrowLeft, Calendar, User, Copy } from 'lucide-vue-next'
import axios from 'axios'

const route = useRoute()
const toast = useToast()

const blog = ref(null)
const loading = ref(false)
const blogId = ref(route.params.id)

// Lấy chi tiết blog
const fetchBlog = async () => {
  loading.value = true
  try {
    const response = await axios.get(`http://localhost:8080/api/blogs/${blogId.value}`)
    blog.value = response.data
    
    // Update page title
    if (blog.value) {
      document.title = `${blog.value.title} - Gym Management`
    }
  } catch (error) {
    toast.error('Không tìm thấy bài viết')
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
    month: 'long',
    day: 'numeric'
  })
}

// Share functionality
const shareUrl = ref('')
const copyLink = () => {
  navigator.clipboard.writeText(shareUrl.value)
  toast.success('Đã copy link bài viết!')
}

const shareOnFacebook = () => {
  // TODO: Implement Facebook share functionality
  toast.info('Tính năng chia sẻ Facebook sẽ được cập nhật sau')
}

onMounted(() => {
  fetchBlog()
  shareUrl.value = window.location.href
})
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <div class="inline-block animate-spin rounded-full h-16 w-16 border-b-4 border-blue-600"></div>
        <p class="mt-4 text-gray-600 text-lg">Đang tải bài viết...</p>
      </div>
    </div>

    <!-- Blog Content -->
    <div v-else-if="blog" class="max-w-4xl mx-auto px-4 py-8">
      <!-- Back Button -->
      <router-link
        to="/"
        class="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-6 transition-colors"
      >
        <ArrowLeft :size="20" />
        Quay lại trang chủ
      </router-link>

      <!-- Blog Header -->
      <article class="bg-white rounded-2xl shadow-xl overflow-hidden">
        <!-- Cover Image -->
        <div v-if="blog.coverImagePath" class="w-full h-96 overflow-hidden">
          <img
            :src="`http://localhost:8080${blog.coverImagePath}`"
            :alt="blog.title"
            class="w-full h-full object-cover"
          />
        </div>

        <!-- Content -->
        <div class="p-8 md:p-12">
          <!-- Title -->
          <h1 class="text-4xl md:text-5xl font-bold text-gray-900 mb-6 leading-tight">
            {{ blog.title }}
          </h1>

          <!-- Metadata -->
          <div class="flex flex-wrap items-center gap-6 mb-8 pb-8 border-b border-gray-200">
            <div class="flex items-center gap-2 text-gray-600">
              <User :size="20" />
              <span class="font-medium">{{ blog.authorName }}</span>
            </div>
            <div class="flex items-center gap-2 text-gray-600">
              <Calendar :size="20" />
              <span>{{ formatDate(blog.publishDate) }}</span>
            </div>
            <div v-if="blog.isPinned" class="flex items-center gap-2">
              <span class="bg-yellow-100 text-yellow-800 px-3 py-1 rounded-full text-sm font-semibold">
                📌 Bài viết nổi bật
              </span>
            </div>
          </div>

          <!-- Blog Content -->
          <div class="prose prose-lg max-w-none mb-8" v-html="blog.content"></div>

          <!-- Share Section -->
          <div class="mt-12 pt-8 border-t border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Chia sẻ bài viết</h3>
            <div class="flex items-center gap-3">
              <button
                @click="shareOnFacebook"
                class="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-semibold"
              >
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                </svg>
                Chia sẻ trên Facebook
              </button>
              <button
                @click="copyLink"
                class="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition-colors font-semibold"
              >
                <Copy :size="20" />
                Copy Link
              </button>
            </div>
          </div>
        </div>
      </article>

      <!-- Related or Back to List -->
      <div class="mt-8 text-center">
        <router-link
          to="/blogs"
          class="inline-flex items-center gap-2 text-blue-600 hover:text-blue-700 font-semibold text-lg transition-colors"
        >
          Xem tất cả bài viết →
        </router-link>
      </div>
    </div>

    <!-- Error State -->
    <div v-else class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <svg class="w-24 h-24 mx-auto text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
        </svg>
        <p class="text-gray-500 text-xl mb-4">Không tìm thấy bài viết</p>
        <router-link
          to="/"
          class="inline-flex items-center gap-2 text-blue-600 hover:text-blue-700 font-semibold"
        >
          <ArrowLeft :size="20" />
          Quay lại trang chủ
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Prose styles for blog content */
:deep(.prose) {
  color: #374151;
}

:deep(.prose h1) {
  font-size: 2em;
  font-weight: 700;
  margin-top: 1em;
  margin-bottom: 0.5em;
  color: #111827;
}

:deep(.prose h2) {
  font-size: 1.5em;
  font-weight: 600;
  margin-top: 1.5em;
  margin-bottom: 0.75em;
  color: #1f2937;
}

:deep(.prose h3) {
  font-size: 1.25em;
  font-weight: 600;
  margin-top: 1.25em;
  margin-bottom: 0.5em;
  color: #374151;
}

:deep(.prose p) {
  margin-top: 1.25em;
  margin-bottom: 1.25em;
  line-height: 1.75;
}

:deep(.prose img) {
  max-width: 100%;
  height: auto;
  border-radius: 0.75rem;
  margin: 2em 0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

:deep(.prose a) {
  color: #2563eb;
  text-decoration: underline;
  font-weight: 500;
}

:deep(.prose a:hover) {
  color: #1d4ed8;
}

:deep(.prose ul),
:deep(.prose ol) {
  margin-top: 1.25em;
  margin-bottom: 1.25em;
  padding-left: 1.75em;
}

:deep(.prose li) {
  margin-top: 0.5em;
  margin-bottom: 0.5em;
}

:deep(.prose blockquote) {
  border-left: 4px solid #3b82f6;
  padding-left: 1.5em;
  font-style: italic;
  color: #4b5563;
  margin: 2em 0;
  background-color: #f9fafb;
  padding: 1em 1.5em;
  border-radius: 0.5rem;
}

:deep(.prose code) {
  background-color: #f3f4f6;
  padding: 0.25em 0.5em;
  border-radius: 0.375rem;
  font-size: 0.875em;
  color: #dc2626;
  font-weight: 500;
}

:deep(.prose pre) {
  background-color: #1f2937;
  color: #f9fafb;
  padding: 1.5em;
  border-radius: 0.75rem;
  overflow-x: auto;
  margin: 2em 0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

:deep(.prose pre code) {
  background-color: transparent;
  padding: 0;
  color: inherit;
  font-weight: normal;
}

:deep(.prose strong) {
  font-weight: 700;
  color: #111827;
}

:deep(.prose em) {
  font-style: italic;
}

:deep(.prose table) {
  width: 100%;
  margin: 2em 0;
  border-collapse: collapse;
}

:deep(.prose th) {
  background-color: #f3f4f6;
  padding: 0.75em;
  text-align: left;
  font-weight: 600;
  border: 1px solid #e5e7eb;
}

:deep(.prose td) {
  padding: 0.75em;
  border: 1px solid #e5e7eb;
}
</style>
