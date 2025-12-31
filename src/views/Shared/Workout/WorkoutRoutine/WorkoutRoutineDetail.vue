<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from "@/services/api"; // Import theo yêu cầu của bạn

const route = useRoute();
const router = useRouter();
const routineId = route.params.id; 

const routine = ref(null);
const isLoading = ref(true); // Thêm trạng thái loading để UI mượt hơn
const error = ref(null);

// Hàm gọi API lấy chi tiết
const fetchRoutineDetail = async () => {
    isLoading.value = true;
    error.value = null;
    try {
        // Gọi API: GET /api/workout-routines/{id}
        // Lưu ý: Nếu baseURL của bạn chưa có chữ '/api', hãy sửa thành: api.get(`/api/workout-routines/${routineId}`)
        const response = await api.get(`/workout-routines/${routineId}`);
        
        routine.value = response.data;
    } catch (err) {
        console.error("Lỗi khi tải lịch tập:", err);
        error.value = "Không thể tải thông tin lịch tập. Vui lòng thử lại sau.";
    } finally {
        isLoading.value = false;
    }
};

onMounted(() => {
    fetchRoutineDetail();
});

const goBack = () => {
    router.back();
}
</script>

<template>
    <div class="p-4 sm:p-6 bg-gray-50 min-h-screen">
        <button @click="goBack" class="mb-3 sm:mb-4 flex items-center text-sm sm:text-base text-gray-500 hover:text-gray-800 transition">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clip-rule="evenodd" />
            </svg>
            Quay lại danh sách
        </button>

        <div v-if="isLoading" class="flex flex-col items-center justify-center py-20 text-gray-500">
            <svg class="animate-spin h-8 w-8 text-blue-600 mb-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <p>Đang tải dữ liệu...</p>
        </div>

        <div v-else-if="error" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-lg text-center">
            {{ error }}
        </div>

        <div v-else-if="routine">
            <div class="bg-white rounded-xl shadow-sm p-4 sm:p-6 mb-4 sm:mb-6 border border-gray-100">
                <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-3 sm:mb-4 gap-3">
                    <div class="w-full md:w-auto">
                        <span class="bg-emerald-100 text-emerald-600 px-2 sm:px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide">
                            {{ routine.muscleGroupFocus }}
                        </span>
                        <h1 class="text-2xl sm:text-3xl font-bold text-gray-900 mt-2 break-words">{{ routine.name }}</h1>
                        <p class="text-sm sm:text-base text-gray-500 mt-1" v-if="routine.creator">
                            Được tạo bởi: <span class="font-medium text-gray-700">{{ routine.creator.fullName }}</span>
                        </p>
                    </div>
                </div>
                
                <hr class="border-gray-100 my-3 sm:my-4">
                
                <div>
                    <h3 class="text-xs sm:text-sm font-semibold text-gray-400 uppercase mb-1">Mô tả</h3>
                    <p class="text-sm sm:text-base text-gray-700 italic break-words">"{{ routine.description }}"</p>
                </div>
            </div>

            <div class="flex items-center justify-between mb-3 sm:mb-4">
                <h2 class="text-lg sm:text-xl font-bold text-gray-800">Danh sách bài tập</h2>
                <span class="bg-gray-200 text-gray-600 text-xs sm:text-sm py-0.5 px-2 sm:px-3 rounded-full font-bold">
                    {{ routine.routineDetails?.length || 0 }} bài
                </span>
            </div>

            <div class="space-y-3 sm:space-y-4">
                <div 
                    v-for="(detail, index) in routine.routineDetails" 
                    :key="detail.id"
                    class="bg-white rounded-lg p-3 sm:p-4 shadow-sm border border-gray-100 flex flex-col md:flex-row items-start md:items-center justify-between hover:shadow-md transition-shadow gap-3"
                >
                    <div class="flex items-start gap-3 sm:gap-4 w-full md:w-auto">
                        <div class="flex-shrink-0 w-7 h-7 sm:w-8 sm:h-8 bg-gray-100 text-gray-500 rounded-full flex items-center justify-center font-bold text-sm">
                            {{ index + 1 }}
                        </div>
                        
                        <div>
                            <h3 class="text-lg font-bold text-gray-900">{{ detail.exercise?.name }}</h3>
                            <p class="text-sm text-gray-500">{{ detail.exercise?.description }}</p>
                            <span class="text-xs text-gray-400 mt-1 inline-block bg-gray-50 px-2 py-0.5 rounded">
                                Nhóm cơ: {{ detail.exercise?.muscleGroup }}
                            </span>
                        </div>
                    </div>

                    <div class="flex items-center gap-6 bg-blue-50 px-5 py-3 rounded-lg border border-blue-100">
                        <div class="text-center">
                            <p class="text-xs text-blue-400 uppercase font-bold mb-1">Sets</p>
                            <p class="text-xl font-bold text-blue-700">{{ detail.defaultSetCount }}</p>
                        </div>
                        <div class="w-px h-8 bg-blue-200"></div>
                        <div class="text-center">
                            <p class="text-xs text-blue-400 uppercase font-bold mb-1">Reps</p>
                            <p class="text-xl font-bold text-blue-700">{{ detail.defaultRepCount }}</p>
                        </div>
                    </div>
                </div>
            </div>
            
            <div v-if="!routine.routineDetails || routine.routineDetails.length === 0" class="text-center py-10 text-gray-400 bg-white rounded-xl border border-dashed border-gray-300">
                Lịch tập này chưa có bài tập nào.
            </div>
        </div>
    </div>
</template>