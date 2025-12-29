<template>
  <div 
    v-if="show" 
    class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
    @click.self="closeModal"
  >
    <div class="bg-white rounded-2xl w-full max-w-2xl shadow-2xl animate-fadeIn">
      <!-- Modal Header -->
      <div class="p-6 bg-gradient-to-r from-blue-600 to-blue-700 text-white flex justify-between items-center rounded-t-2xl">
        <h2 class="text-2xl font-bold flex items-center gap-2">
          <Edit class="w-6 h-6" />
          Chỉnh Sửa Lớp Học
        </h2>
        <button 
          @click="closeModal" 
          class="w-10 h-10 bg-white/20 rounded-full hover:bg-white/30 transition-all flex items-center justify-center"
        >
          <X class="w-6 h-6" />
        </button>
      </div>
      
      <!-- Modal Body -->
      <div class="p-8 space-y-6">
        <div class="p-4 bg-yellow-50 border border-yellow-200 rounded-xl flex items-start gap-3">
          <AlertTriangle class="w-5 h-5 text-yellow-600 shrink-0 mt-0.5" />
          <p class="text-sm text-yellow-800">
            <strong>Lưu ý:</strong> Chỉ có thể sửa thông tin cơ bản. Để thay đổi lịch học, vui lòng sửa từng buổi học riêng lẻ.
          </p>
        </div>

        <!-- Class Name -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Tên lớp học *
          </label>
          <input 
            v-model="formData.name" 
            type="text" 
            placeholder="VD: Yoga Buổi Sáng"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
          />
        </div>

        <!-- Description -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Mô tả *
          </label>
          <textarea 
            v-model="formData.description" 
            rows="4" 
            placeholder="Mô tả về lớp học..."
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
          ></textarea>
        </div>

        <!-- Difficulty Level -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Độ khó *
          </label>
          <select 
            v-model="formData.difficultyLevel"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
          >
            <option value="Beginner">Beginner</option>
            <option value="Intermediate">Intermediate</option>
            <option value="Advanced">Advanced</option>
          </select>
        </div>

        <!-- Status -->
        <!-- <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            Trạng thái *
          </label>
          <select 
            v-model="formData.status"
            class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-600 focus:ring-2 focus:ring-blue-100 outline-none transition-all"
          >
            <option value="ACTIVE">Đang hoạt động</option>
            <option value="INACTIVE">Không hoạt động</option>
          </select>
        </div> -->
      </div>
      
      <!-- Modal Footer -->
      <div class="p-6 bg-gray-50 border-t flex justify-end gap-3 rounded-b-2xl">
        <button 
          @click="closeModal" 
          class="px-6 py-3 bg-gray-200 text-gray-700 rounded-xl font-semibold hover:bg-gray-300 transition-all"
        >
          Hủy
        </button>
        <button 
          @click="saveChanges" 
          :disabled="!isValid || saving"
          class="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
        >
          <Save class="w-5 h-5" />
          {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { Edit, X, Save, AlertTriangle } from 'lucide-vue-next';
import api from '@/services/api';
import { useToast } from 'vue-toastification';

const toast = useToast();

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  fitnessClass: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['close', 'updated']);

const formData = ref({
  name: '',
  description: '',
  difficultyLevel: 'Beginner',
  status: 'ACTIVE'
});

const saving = ref(false);

const isValid = computed(() => {
  return formData.value.name.trim() !== '' && 
         formData.value.description.trim() !== '' &&
         formData.value.difficultyLevel !== '' &&
         formData.value.status !== '';
});

// Watch for prop changes to populate form
watch(() => props.fitnessClass, (newClass) => {
  if (newClass) {
    formData.value = {
      name: newClass.name || '',
      description: newClass.description || '',
      difficultyLevel: newClass.difficulty || newClass.difficultyLevel || 'Beginner',
      status: newClass.status || 'ACTIVE'
    };
  }
}, { immediate: true });

const closeModal = () => {
  emit('close');
};

const saveChanges = async () => {
  if (!isValid.value || !props.fitnessClass) return;
  
  saving.value = true;
  try {
    const updateData = {
      name: formData.value.name,
      description: formData.value.description,
      difficultyLevel: formData.value.difficultyLevel,
      status: formData.value.status
    };
    
    await api.put(`/fitness_class/${props.fitnessClass.id}`, updateData);
    
    emit('updated', {
      ...props.fitnessClass,
      ...updateData
    });
    
    closeModal();
  } catch (error) {
    toast.error('Lỗi khi cập nhật lớp học');
  } finally {
    saving.value = false;
  }
};
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.3s ease-out;
}
</style>

