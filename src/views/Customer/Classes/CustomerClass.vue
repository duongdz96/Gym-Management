<script setup lang="ts">
import { ref, computed, onMounted } from "vue"
import api from '@/services/api'; 
import axios from 'axios';

// =================================================================
// 1. TYPESCRIPT DEFINITIONS
// =================================================================
type Member = {
  id: number;
};

type ClassTemplate = {
  id: number;
  name: string;
  description: string;
  difficultyLevel: string;
  status: string;
};

type ClassTemplateWithFollowStatus = ClassTemplate & {
  isFollowed: boolean;
  followId: number | null;
};

// =================================================================
// 2. STATE MANAGEMENT
// =================================================================
const classes = ref<ClassTemplateWithFollowStatus[]>([]);
const isLoading = ref(true);
const error = ref<string | null>(null);

const currentMemberId = 1; // Assuming the current member's ID is 1

const selectedDifficulty = ref("All");
const selectedStatus = ref("All");

// =================================================================
// 3. API LOGIC
// =================================================================
const fetchData = async () => {
  try {
    isLoading.value = true;
    const [classTemplatesRes, followedClassesRes] = await Promise.all([
      api.get<ClassTemplate[]>("/classtemplate"),
      api.get<any[]>(`/follow-classes/member/${currentMemberId}`)
    ]);

    const allClassTemplates = classTemplatesRes.data;
    const followedItems = followedClassesRes.data;

    const followedClassMap = new Map<number, number>();
    followedItems.forEach(item => {
        followedClassMap.set(item.classTemplate.id, item.id);
    });

    classes.value = allClassTemplates.map(cls => ({
      ...cls,
      isFollowed: followedClassMap.has(cls.id),
      followId: followedClassMap.get(cls.id) || null,
    }));

  } catch (err) {
    console.error("Error fetching data:", err);
    error.value = "Could not load data. Please try again later.";
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchData);

const handleFollowToggle = async (targetClass: ClassTemplateWithFollowStatus) => {
  try {
    if (targetClass.isFollowed) {
      await api.delete(`/follow-classes/${targetClass.followId}`);
      targetClass.isFollowed = false;
      targetClass.followId = null;
    } else {
      const payload = {
        followDate: new Date().toISOString(),
        classTemplate: { id: targetClass.id },
        member: { id: currentMemberId }
      };
      const response = await api.post<any>("/follow-classes", payload);
      targetClass.isFollowed = true;
      targetClass.followId = response.data.id;
    }
  } catch (err) {
    console.error(`Error while ${targetClass.isFollowed ? 'unfollowing' : 'following'}:`, err);
    
    if (axios.isAxiosError(err) && err.response) {
      const errorMessage = err.response.data.message || "An error occurred on the server.";
      alert(errorMessage);
    } else {
      alert("Operation failed. Please check your network connection and try again.");
    }
  }
};

const filteredClasses = computed(() => {
  return classes.value.filter(c => {
    const matchDifficulty = selectedDifficulty.value === "All" || c.difficultyLevel === selectedDifficulty.value
    const matchStatus = selectedStatus.value === "All" || c.status === selectedStatus.value
    return matchDifficulty && matchStatus
  })
});

</script>

<template>
  <div class="p-6 space-y-8">
    <h1 class="text-2xl font-bold text-stone-800">Available Classes</h1>

    <div v-if="isLoading" class="text-center text-gray-500 py-8">Loading data...</div>
    <div v-else-if="error" class="text-center text-red-600 bg-red-100 p-4 rounded-md">{{ error }}</div>

    <div v-else>
      <div class="flex flex-wrap gap-4 items-center mb-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Difficulty</label>
            <select v-model="selectedDifficulty" class="border rounded-md px-3 py-2 text-gray-700 focus:ring-red-500 focus:border-red-500">
              <option>All</option>
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select v-model="selectedStatus" class="border rounded-md px-3 py-2 text-gray-700 focus:ring-red-500 focus:border-red-500">
              <option>All</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </select>
          </div>
      </div>

      <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="cls in filteredClasses"
          :key="cls.id"
          class="bg-white rounded-xl shadow p-5 border hover:shadow-lg transition"
        >
          <h2 class="text-lg font-semibold text-stone-800 mb-2">{{ cls.name }}</h2>
          <p class="text-gray-600"><strong>Difficulty:</strong> {{ cls.difficultyLevel }}</p>
          <p class="text-gray-600 italic truncate" :title="cls.description">{{ cls.description }}</p>

          <p class="mt-2">
            <strong>Status:</strong>
            <span :class="cls.status === 'ACTIVE' ? 'text-green-600 font-medium' : 'text-orange-600 font-medium'">
              {{ cls.status === 'ACTIVE' ? 'Active' : 'Inactive' }}
            </span>
          </p>

          <button
            @click="handleFollowToggle(cls)"
            :disabled="cls.status !== 'ACTIVE'"
            class="mt-4 w-full px-4 py-2 rounded-lg font-medium text-white transition"
            :class="{
              'bg-gray-500 hover:bg-gray-600': cls.isFollowed,
              'bg-red-600 hover:bg-red-700': !cls.isFollowed,
              'bg-gray-300 cursor-not-allowed': cls.status !== 'ACTIVE'
            }"
          >
            {{ cls.isFollowed ? 'Unfollow' : 'Follow Class' }}
          </button>
        </div>
      </div>

      <div v-if="!isLoading && filteredClasses.length === 0" class="text-center text-gray-500 py-8">
        No classes match your filter criteria.
      </div>
    </div>
  </div>
</template>