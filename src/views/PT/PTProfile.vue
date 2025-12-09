<script setup>
import { ref, onMounted } from "vue";
import api from "@/services/api";
import { useAuthStore } from "@/stores/useAuthStore";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const toast = useToast();

const profile = ref(null);

onMounted(async () => {
  try {
    const res = await api.get("/pts");
    const staffs = Array.isArray(res.data) ? res.data : [];
    const currentStaff = staffs.find(staff => staff.id === authStore.user.id);
    if (currentStaff) {
      profile.value = {
        id: currentStaff.id,
        fullName: currentStaff.fullName,
        email: currentStaff.email,
        phone: currentStaff.phone,
        dob: new Date(currentStaff.dob).toLocaleDateString(),
        gender: currentStaff.gender,
        position: currentStaff.position,
        specialize: currentStaff.specialize,
        hirePrice: currentStaff.hirePrice,
      };
    }
    console.log("PT Profile loaded:", profile.value);
  } catch (error) {
    console.error("Failed to load PT profile:", error);
    toast.error("Failed to load profile. Please try again.");
  }
});
</script>

<template>
  <div class="p-6 space-y-8">
    <!-- Title -->
    <h1 class="text-3xl font-bold text-stone-800 mb-6">Personal Information</h1>

    <!-- Thông tin cá nhân -->
    <section v-if="profile" class="bg-white rounded-xl shadow-lg p-8">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-semibold text-gray-800">PT Profile</h2>
      </div>
      <div
        class="flex flex-col md:flex-row items-start space-y-6 md:space-y-0 md:space-x-8"
      >
        <!-- Thông tin bên phải -->
        <div class="flex-1 grid md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Full Name</label
            >
            <p class="mt-1 text-lg text-gray-900 font-medium">
              {{ profile.fullName }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Email</label>
            <p class="mt-1 text-lg text-gray-900">{{ profile.email }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Phone Number</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.phone }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Date of Birth</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.dob }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Gender</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.gender }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Position</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.position }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Specialization</label
            >
            <p class="mt-1 text-lg text-gray-900">
              {{ profile.specialize }}
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700"
              >Hire Price</label
            >
            <p class="mt-1 text-lg text-gray-900">{{ profile.hirePrice }} VND</p>
          </div>
        </div>
      </div>
    </section>
    <div v-else class="text-center py-8 text-gray-500">
      Loading profile...
    </div>
  </div>
</template>
