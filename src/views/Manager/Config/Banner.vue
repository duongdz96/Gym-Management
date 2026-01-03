<template>
  <div class="p-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Banner List</h1>

      <select v-model="statusFilter" class="px-3 py-2 border border-gray-200 rounded-lg bg-white">
        <option value="">All Status</option>
        <option value="1">Public</option>
        <option value="0">Private</option>
      </select>

      <!-- Nút mở modal -->
      <button
        @click="showModal = true"
        class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow flex items-center"
      >
        <Plus class="w-5 h-5 mr-1" />
        Add Banner
      </button>
    </div>

    <!-- Giới hạn banner -->
    <div
      v-if="isLimitReached"
      class="bg-yellow-300 text-white border border-yellow-300 rounded-lg px-4 py-2 mb-3 flex items-center"
    >
      <TriangleAlert class="w-4 h-4 mr-1" />
      <span>
        You should from {{ MAX_PUBLIC }} to {{ MAX_PUBLIC + 1 }} public banners ({{ publicCount }} currently).
      </span>
    </div>

    <!-- Thông báo khi chưa có banner -->
    <div v-if="banners.length === 0" class="text-center text-gray-500 my-6">
      No banners available. Please add some banners.
    </div>

    <!-- Grid hiển thị banner -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      <div
        v-for="banner in filteredBanner"
        :key="banner.id"
        class="bg-white rounded-xl shadow-md overflow-hidden border border-gray-200 flex flex-col relative"
      >
        <span
          class="absolute top-2 right-2 px-2 py-1 text-xs font-semibold rounded-full"
          :class="banner.isPublic ? 'bg-green-100 text-green-700' : 'bg-gray-200 text-gray-600'"
        >
          {{ banner.isPublic ? 'Public' : 'Private' }}
        </span>

        <img :src="banner.value" alt="Banner" class="w-full h-40 object-cover" />

        <div class="p-4 flex justify-between items-center">
          <button
            @click="togglePublic(banner)"
            class="px-3 py-1 rounded-lg text-sm font-medium transition"
            :class="banner.isPublic
              ? 'bg-yellow-100 text-yellow-700 hover:bg-yellow-200'
              : 'bg-green-100 text-green-700 hover:bg-green-200'"
          >
            {{ banner.isPublic ? 'Make Private' : 'Make Public' }}
          </button>

          <button
            @click="deleteBanner(banner.id)"
            class="bg-red-100 text-red-700 hover:bg-red-200 px-3 py-1 rounded-lg text-sm font-medium transition"
          >
            Delete
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Upload Banner -->
    <div
      v-if="showModal"
      class="fixed inset-0 bg-black/50 bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white p-6 rounded-xl shadow-lg w-96 relative">
        <h2 class="text-xl font-bold mb-4">Upload New Banner</h2>

        <!-- Nhập tên banner -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Banner Name (optional)
          </label>
          <input
            type="text"
            v-model="bannerName"
            placeholder="Enter a name for this banner"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring focus:ring-blue-200"
          />
        </div>

        <!-- Chọn file -->
        <div class="flex flex-col items-center">
          <input
            type="file"
            accept="image/*"
            ref="fileInput"
            @change="handleFileChange"
            class="hidden"
          />

          <button
            @click="fileInput?.click()"
            class="bg-gray-100 hover:bg-gray-200 text-gray-700 px-4 py-2 rounded-lg border border-gray-300"
          >
            Choose Image
          </button>

          <!-- Preview -->
          <div v-if="previewUrl" class="mt-4">
            <img
              :src="previewUrl"
              alt="Preview"
              class="w-full h-40 object-cover rounded-lg border"
            />
          </div>

          <p v-else class="text-gray-500 text-sm mt-4">Choose file to preview</p>
        </div>

        <!-- Actions -->
        <div class="mt-6 flex justify-end gap-3">
          <button
            @click="closeModal"
            class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-100"
          >
            Cancel
          </button>
          <button
            @click="uploadBanner"
            class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow"
          >
            Add
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useToast } from "vue-toastification";
import { TriangleAlert, Plus } from "lucide-vue-next";
import api from "@/services/api";

const toast = useToast();

type Banner = {
  id: number;
  key: string;
  value: string;
  isPublic: boolean;
};

const MAX_PUBLIC = 3;
const showModal = ref(false);
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string | null>(null);
const fileInput = ref<HTMLInputElement | null>(null);
const statusFilter = ref<number | ''>('');
const banners = ref<Banner[]>([]);
const bannerName = ref<string>('');
const API_BASE = "http://localhost:8080";

// computed
const publicCount = computed(() => banners.value.filter(b => b.isPublic).length);
const isLimitReached = computed(() => publicCount.value >= MAX_PUBLIC);
const filteredBanner = computed(() => {
  if (statusFilter.value === '') return banners.value;
  const wanted = Number(statusFilter.value);
  return banners.value.filter(b => (b.isPublic ? 1 : 0) === wanted);
});

// handle file input
const handleFileChange = (e: Event) => {
  const input = e.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    selectedFile.value = input.files[0];
    previewUrl.value = URL.createObjectURL(input.files[0]);
  }
};

// upload banner
const uploadBanner = async () => {
  if (!selectedFile.value) {
    toast.warning("Vui lòng chọn ảnh!");
    return;
  }

  try {
    const formData = new FormData();
    formData.append("file", selectedFile.value);
    if (bannerName.value.trim() !== '') {
      formData.append("name", bannerName.value.trim());
    }

    const res = await api.post("/config/banner", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    // map đúng field public → isPublic
    const newBanner: Banner = {
      id: res.data.id,
      key: res.data.key,
      value: API_BASE + res.data.value,
      isPublic: res.data.public
    };

    banners.value.push(newBanner);
    toast.success("Banner uploaded successfully!");
    closeModal();
  } catch (error: any) {
    toast.error(error.response?.data?.message || "Error occurred!");
  }
};

// toggle public/private
const togglePublic = async (banner: Banner) => {
  if (!banner.isPublic && publicCount.value >= MAX_PUBLIC) {
    toast.error(`You can only have up to ${MAX_PUBLIC} public banners.`);
    return;
  }

  try {
    const res = await api.patch(`/config/banner/${banner.id}/toggle`);
    banner.isPublic = res.data.public; // map lại
    toast.success("Updated banner visibility!");
  } catch (error: any) {
    toast.error(error.response?.data?.message || "Failed to update banner!");
  }
};

// delete banner
const deleteBanner = async (id: number) => {
  try {
    await api.delete(`/config/banner/${id}`);
    banners.value = banners.value.filter(b => b.id !== id);
    toast.success("Banner deleted!");
  } catch (error: any) {
    toast.error(error.response?.data?.message || "Failed to delete banner!");
  }
};

// fetch all banners
const fetchBanners = async () => {
  try {
    const res = await api.get("/config/banner/all");
    banners.value = res.data.map((b: any) => ({
      id: b.id,
      key: b.key,
      value: API_BASE + b.value,
      isPublic: b.public // map đúng field
    }));
  } catch (error: any) {
    toast.error("Failed to fetch banners!");
  }
};

// close modal
const closeModal = () => {
  showModal.value = false;
  selectedFile.value = null;
  previewUrl.value = null;
  bannerName.value = '';
};

onMounted(() => {
  fetchBanners();
});
</script>

