<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/useAuthStore'
import { ArrowLeft, Save, Image as ImageIcon } from 'lucide-vue-next'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from 'axios'

const router = useRouter()
const toast = useToast()
const authStore = useAuthStore()

const title = ref('')
const content = ref('')
const isPinned = ref(false)
const coverImage = ref(null)
const coverImagePreview = ref(null)
const loading = ref(false)

// Quill editor options
const editorOptions = {
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote', 'code-block'],
      [{ 'header': 1 }, { 'header': 2 }],
      [{ 'list': 'ordered'}, { 'list': 'bullet' }],
      [{ 'script': 'sub'}, { 'script': 'super' }],
      [{ 'indent': '-1'}, { 'indent': '+1' }],
      [{ 'direction': 'rtl' }],
      [{ 'size': ['small', false, 'large', 'huge'] }],
      [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
      [{ 'color': [] }, { 'background': [] }],
      [{ 'font': [] }],
      [{ 'align': [] }],
      ['link', 'image', 'video'],
      ['clean']
    ]
  },
  placeholder: 'Nhập nội dung blog...',
  theme: 'snow'
}

// Xử lý chọn ảnh
const handleImageSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (!file.type.startsWith('image/')) {
      toast.error('Vui lòng chọn file ảnh')
      return
    }
    
    coverImage.value = file
    
    // Tạo preview
    const reader = new FileReader()
    reader.onload = (e) => {
      coverImagePreview.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// Xóa ảnh đã chọn
const removeImage = () => {
  coverImage.value = null
  coverImagePreview.value = null
}

// Tạo blog
const createBlog = async () => {
  // Validation
  if (!title.value.trim()) {
    toast.error('Vui lòng nhập tiêu đề')
    return
  }
  
  if (!content.value.trim()) {
    toast.error('Vui lòng nhập nội dung')
    return
  }

  if (!authStore.user || !authStore.user.id) {
    toast.error('Không tìm thấy thông tin người dùng')
    return
  }

  loading.value = true

  try {
    const formData = new FormData()
    formData.append('title', title.value)
    formData.append('content', content.value)
    formData.append('authorId', authStore.user.id)
    formData.append('isPinned', isPinned.value)
    
    if (coverImage.value) {
      formData.append('coverImage', coverImage.value)
    }

    await axios.post('http://localhost:8080/api/blogs', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    toast.success('Tạo blog thành công')
    router.push('/manager/blog')
  } catch (error) {
    toast.error('Lỗi khi tạo blog')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// Quay lại
const goBack = () => {
  router.push('/manager/blog')
}
</script>

<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <div class="max-w-5xl mx-auto">
      <!-- Header -->
      <div class="flex items-center gap-4 mb-6">
        <button
          @click="goBack"
          class="flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors"
        >
          <ArrowLeft :size="20" />
          Quay Lại
        </button>
        <h1 class="text-3xl font-bold text-gray-900">Tạo Blog Mới</h1>
      </div>

      <!-- Form -->
      <div class="bg-white rounded-lg shadow p-6">
        <!-- Tiêu đề -->
        <div class="mb-6">
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Tiêu Đề <span class="text-red-500">*</span>
          </label>
          <input
            v-model="title"
            type="text"
            placeholder="Nhập tiêu đề blog"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors"
          />
        </div>

        <!-- Ảnh bìa -->
        <div class="mb-6">
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Ảnh Bìa
          </label>
          
          <div v-if="!coverImagePreview" class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center">
            <input
              type="file"
              accept="image/*"
              @change="handleImageSelect"
              class="hidden"
              id="coverImageInput"
            />
            <label
              for="coverImageInput"
              class="cursor-pointer flex flex-col items-center gap-2"
            >
              <ImageIcon :size="48" class="text-gray-400" />
              <span class="text-sm text-gray-600">Nhấn để chọn ảnh bìa</span>
              <span class="text-xs text-gray-500">PNG, JPG, GIF (tối đa 5MB)</span>
            </label>
          </div>

          <div v-else class="relative">
            <img
              :src="coverImagePreview"
              alt="Preview"
              class="w-full h-64 object-cover rounded-lg"
            />
            <button
              @click="removeImage"
              class="absolute top-2 right-2 bg-red-600 text-white px-3 py-1 rounded-lg hover:bg-red-700 transition-colors"
            >
              Xóa
            </button>
          </div>
        </div>

        <!-- Nội dung -->
        <div class="mb-6">
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Nội Dung <span class="text-red-500">*</span>
          </label>
          <QuillEditor
            v-model:content="content"
            :options="editorOptions"
            content-type="html"
            style="min-height: 400px"
          />
        </div>

        <!-- Ghim -->
        <div class="mb-6">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              v-model="isPinned"
              type="checkbox"
              class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span class="text-sm font-medium text-gray-700">Ghim bài viết này</span>
          </label>
        </div>

        <!-- Buttons -->
        <div class="flex justify-end gap-3">
          <button
            @click="goBack"
            class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Hủy
          </button>
          <button
            @click="createBlog"
            :disabled="loading"
            class="flex items-center gap-2 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Save :size="18" />
            {{ loading ? 'Đang lưu...' : 'Tạo Blog' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Quill editor custom styles */
:deep(.ql-container) {
  min-height: 400px;
  font-size: 16px;
}

:deep(.ql-editor) {
  min-height: 400px;
}
</style>