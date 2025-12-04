<script setup lang="ts">
import { ref, onMounted } from "vue";
import api from '@/services/api'; 
import axios from 'axios';

// =================================================================
// 1. TYPESCRIPT DEFINITIONS
// =================================================================
type Staff = {
  id: number;
};

type ClassTemplate = {
  id: number;
  name: string;
  description: string;
  difficultyLevel: string;
  status: string;
};

type ClassTemplateWithRegistrationStatus = ClassTemplate & {
  isRegistered: boolean;
  registrationId: number | null;
};


// =================================================================
// 2. STATE MANAGEMENT
// =================================================================
const classes = ref<ClassTemplateWithRegistrationStatus[]>([]);
const isLoading = ref(true);
const error = ref<string | null>(null);

const currentStaffId = 2; 


// =================================================================
// 3. API LOGIC
// =================================================================
const fetchData = async () => {
  try {
    isLoading.value = true;

    const [classTemplatesRes, registrationsRes] = await Promise.all([
      api.get<ClassTemplate[]>("/fitness_class"),
      api.get<any[]>(`/class-registrations/teacher/${currentStaffId}`) 
    ]);

    const allClassTemplates = classTemplatesRes.data;
    const registeredItems = registrationsRes.data;

    const registrationMap = new Map<number, number>();
    registeredItems.forEach(item => {
        registrationMap.set(item.classTemplate.id, item.id);
    });

    classes.value = allClassTemplates.map(cls => ({
      ...cls,
      isRegistered: registrationMap.has(cls.id),
      registrationId: registrationMap.get(cls.id) || null,
    }));

  } catch (err) {
    console.error("Error fetching data:", err);
    error.value = "Could not load data. Please try again later.";
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchData);

const handleRegistrationToggle = async (targetClass: ClassTemplateWithRegistrationStatus) => {
  if (targetClass.status !== 'ACTIVE') return;

  try {
    if (targetClass.isRegistered) {
      await api.delete(`/class-registrations/${targetClass.registrationId}`);
      
      targetClass.isRegistered = false;
      targetClass.registrationId = null;

    } else {
      const payload = {
        description: `Staff ID ${currentStaffId} registered for class ${targetClass.name}`, 
        classTemplate: { id: targetClass.id },
        staff: { id: currentStaffId }
      };
      const response = await api.post<any>("/class-registrations", payload);
      
      targetClass.isRegistered = true;
      targetClass.registrationId = response.data.id; 
    }
  } catch (err) {
    console.error(`Error while ${targetClass.isRegistered ? 'unregistering' : 'registering'}:`, err);

    if (axios.isAxiosError(err) && err.response) {
      const errorMessage = err.response.data.message || "An error occurred on the server.";
      alert(errorMessage);
    } else {
      alert("Operation failed. Please check your network connection and try again.");
    }
  }
};

</script>

<template>
  <div class="p-6 space-y-8">
    <h1 class="text-2xl font-bold text-stone-800">Register to Teach a Class</h1>

    <div v-if="isLoading" class="text-center text-gray-500 py-8">Loading data...</div>
    <div v-else-if="error" class="text-center text-red-600 bg-red-100 p-4 rounded-md">{{ error }}</div>

    <div v-else>
      <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="cls in classes"
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
            @click="handleRegistrationToggle(cls)"
            :disabled="cls.status !== 'ACTIVE'"
            class="mt-4 w-full px-4 py-2 rounded-lg font-medium text-white transition"
            :class="{
              'bg-gray-500 hover:bg-gray-600': cls.isRegistered,
              'bg-blue-600 hover:bg-blue-700': !cls.isRegistered,
              'bg-gray-300 cursor-not-allowed text-gray-500': cls.status !== 'ACTIVE'
            }"
          >
            <span v-if="cls.status !== 'ACTIVE'">Class Closed</span>
            <span v-else>{{ cls.isRegistered ? 'Unregister' : 'Register to Teach' }}</span>
          </button>
        </div>
      </div>

      <div v-if="!isLoading && classes.length === 0" class="text-center text-gray-500 py-8">
        No classes available.
      </div>
    </div>
  </div>
</template>