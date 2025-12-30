<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { Pin, PinOff, Edit, Trash2, Plus } from 'lucide-vue-next'
import axios from 'axios'

const router = useRouter()
const toast = useToast()

const blogs = ref([])
const loading = ref(false)

// Lấy danh sách blog
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

// Toggle ghim
const togglePin = async (id) => {
  try {
    await axios.patch(`http://localhost:8080/api/blogs/${id}/pin`)
    toast.success('Cập nhật trạng thái ghim thành công')
    fetchBlogs()
  } catch (error) {
    toast.error('Lỗi khi cập nhật trạng thái ghim')
    console.error(error)
  }
}

// Xóa blog
const deleteBlog = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa blog này?')) return
  
  try {
    await axios.delete(`http://localhost:8080/api/blogs/${id}`)
    toast.success('Xóa blog thành công')
    fetchBlogs()
  } catch (error) {
    toast.error('Lỗi khi xóa blog')
    console.error(error)
  }
}

// Chuyển đến trang tạo blog
const goToCreate = () => {
  router.push('/manager/blog/create')
}

// Chuyển đến trang chỉnh sửa
const goToEdit = (id) => {
  router.push(`/manager/blog/edit/${id}`)
}

// Format ngày
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchBlogs()
})
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-gray-900">Quản Lý Blog</h1>
        <button
          @click="goToCreate"
          class="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus :size="20" />
          Tạo Blog Mới
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p class="mt-4 text-gray-600">Đang tải...</p>
      </div>

      <!-- Blog List -->
      <div v-else-if="blogs.length > 0" class="bg-white rounded-lg shadow overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Ảnh Bìa
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tiêu Đề
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Người Đăng
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Ngày Đăng
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Trạng Thái
              </th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Thao Tác
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="blog in blogs" :key="blog.id" class="hover:bg-gray-50">
              <!-- Ảnh bìa -->
              <td class="px-6 py-4 whitespace-nowrap">
                <img
                  v-if="blog.coverImagePath"
                  :src="`http://localhost:8080${blog.coverImagePath}`"
                  alt="Cover"
                  class="h-16 w-24 object-cover rounded"
                />
                <div v-else class="h-16 w-24 bg-gray-200 rounded flex items-center justify-center">
                  <span class="text-gray-400 text-xs">Không có ảnh</span>
                </div>
              </td>

              <!-- Tiêu đề -->
              <td class="px-6 py-4">
                <div class="text-sm font-medium text-gray-900">{{ blog.title }}</div>
              </td>

              <!-- Người đăng -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ blog.authorName }}</div>
              </td>

              <!-- Ngày đăng -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ formatDate(blog.publishDate) }}</div>
              </td>

              <!-- Trạng thái ghim -->
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  v-if="blog.isPinned"
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800"
                >
                  Đã Ghim
                </span>
                <span
                  v-else
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800"
                >
                  Bình Thường
                </span>
              </td>

              <!-- Thao tác -->
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex justify-end gap-2">
                  <!-- Toggle ghim -->
                  <button
                    @click="togglePin(blog.id)"
                    :title="blog.isPinned ? 'Bỏ ghim' : 'Ghim'"
                    class="text-yellow-600 hover:text-yellow-900 transition-colors"
                  >
                    <Pin v-if="!blog.isPinned" :size="18" />
                    <PinOff v-else :size="18" />
                  </button>

                  <!-- Chỉnh sửa -->
                  <button
                    @click="goToEdit(blog.id)"
                    title="Chỉnh sửa"
                    class="text-blue-600 hover:text-blue-900 transition-colors"
                  >
                    <Edit :size="18" />
                  </button>

                  <!-- Xóa -->
                  <button
                    @click="deleteBlog(blog.id)"
                    title="Xóa"
                    class="text-red-600 hover:text-red-900 transition-colors"
                  >
                    <Trash2 :size="18" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Empty state -->
      <div v-else class="bg-white rounded-lg shadow p-12 text-center">
        <p class="text-gray-500 text-lg mb-4">Chưa có blog nào</p>
        <button
          @click="goToCreate"
          class="inline-flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus :size="20" />
          Tạo Blog Đầu Tiên
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Custom styles if needed */
</style>