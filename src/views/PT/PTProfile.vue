<script setup>
import { ref, onMounted } from "vue";

const isEditing = ref(false);
const profile = ref({
  name: "Nguyễn Văn PT",
  email: "pt@gym.com",
  phone: "0123-456-789",
  specialization: "Yoga & Fitness",
  experience: "5 năm",
  certifications: ["Yoga Instructor", "Personal Trainer"],
  birthDate: "15/05/1990",
  address: "Phường 1, Thành phố Hồ Chí Minh",
  avatar: "https://via.placeholder.com/150", // Placeholder avatar
});

const editedProfile = ref({ ...profile.value });

const startEditing = () => {
  editedProfile.value = { ...profile.value };
  isEditing.value = true;
};

const saveChanges = () => {
  profile.value = { ...editedProfile.value };
  isEditing.value = false;
  // TODO: Call API to save changes
  console.log("Profile updated:", profile.value);
};

const cancelEditing = () => {
  isEditing.value = false;
};

onMounted(() => {
  console.log("PT Profile loaded");
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Title -->
    <h1 class="text-3xl font-bold text-stone-800 mb-6">Personal Information</h1>

    <!-- Thông tin cá nhân -->
    <section class="bg-white rounded-xl shadow-lg p-8">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-semibold text-gray-800">PT Profile</h2>
        <div class="flex gap-3">
          <button
            v-if="!isEditing"
            @click="startEditing"
            class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition-colors font-medium"
          >
            <svg
              class="w-4 h-4 inline mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
              ></path>
            </svg>
            Edit
          </button>
          <button
            v-if="isEditing"
            @click="saveChanges"
            class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg transition-colors font-medium"
          >
            <svg
              class="w-4 h-4 inline mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M5 13l4 4L19 7"
              ></path>
            </svg>
            Save
          </button>
          <button
            v-if="isEditing"
            @click="cancelEditing"
            class="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg transition-colors font-medium"
          >
            <svg
              class="w-4 h-4 inline mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              ></path>
            </svg>
            Cancel
          </button>
        </div>
      </div>
      <div
        class="flex flex-col md:flex-row items-start space-y-6 md:space-y-0 md:space-x-8"
      >
        <!-- Avatar bên trái -->
        <div class="flex-shrink-0">
          <div
            class="w-32 h-40 rounded-lg border-4 border-gray-200 shadow-md"
            :style="{
              backgroundImage: `url(${profile.avatar})`,
              backgroundSize: 'cover',
              backgroundPosition: 'center',
              backgroundRepeat: 'no-repeat',
            }"
          ></div>
        </div>
        <!-- Thông tin bên phải -->
        <div class="flex-1 grid md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Full Name</label
            >
            <p class="mt-1 text-lg text-gray-900 font-medium">
              {{ profile.name }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Email</label>
            <input
              v-if="isEditing"
              v-model="editedProfile.email"
              type="email"
              class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
            <p v-else class="mt-1 text-lg text-gray-900">{{ profile.email }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Phone Number</label
            >
            <input
              v-if="isEditing"
              v-model="editedProfile.phone"
              type="tel"
              class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
            <p v-else class="mt-1 text-lg text-gray-900">{{ profile.phone }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Date of Birth</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.birthDate }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Address</label
            >
            <textarea
              v-if="isEditing"
              v-model="editedProfile.address"
              rows="3"
              class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            ></textarea>
            <p v-else class="mt-1 text-lg text-gray-900">
              {{ profile.address }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Specialization</label
            >
            <p class="mt-1 text-lg text-gray-900">
              {{ profile.specialization }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Experience</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.experience }}</p>
          </div>
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700"
              >Certifications</label
            >
            <ul class="mt-1 text-lg text-gray-900 list-disc list-inside">
              <li
                v-for="cert in profile.certifications"
                :key="cert"
                class="mb-1"
              >
                {{ cert }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
