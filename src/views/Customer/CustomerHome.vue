<script setup>
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { Camera, X } from "lucide-vue-next"; // import icon
import axios from "axios";

const rating = ref(0);
const hoverRating = ref(0);
const comment = ref("");
const images = ref([]);
const memberId = 1;

const setRating = (star) => {
  rating.value = star;
};

const previewImages = (event) => {
  const files = Array.from(event.target.files);
  images.value.push(
    ...files.map((file) => ({
      file,
      url: URL.createObjectURL(file),
    }))
  );
};

const removeImage = (index) => {
  images.value.splice(index, 1);
};

const submitFeedback = async () => {
  try {
    const formData = new FormData();
    formData.append("rating", rating.value);
    formData.append("comment", comment.value);
    formData.append("memberId", memberId);

    // Thêm từng ảnh vào formData
    images.value.forEach((img) => {
      formData.append("images", img.file);
    });

    const response = await axios.post("http://localhost:8080/api/feedbacks", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    alert("Feedback submitted successfully!");
    console.log("Response:", response.data);

    // Reset form
    rating.value = 0;
    comment.value = "";
    images.value = [];
  } catch (error) {
    console.error("Error submitting feedback:", error);
    alert("Failed to submit feedback!");
  }
};
</script>

<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">Welcome back, Member 👋</h1>
        <p class="text-xl text-gray-600">Your fitness journey starts here</p>
      </div>

      <!-- Quick Actions Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <!-- My Membership -->
        <RouterLink
          to="/customer/membership"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4 group-hover:bg-blue-200 transition-colors">
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">My Membership</h3>
          <p class="text-gray-600">Active until: <span class="font-medium">30/12/2025</span></p>
        </RouterLink>

        <!-- My Classes -->
        <RouterLink
          to="/customer/class"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4 group-hover:bg-green-200 transition-colors">
            <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">My Classes</h3>
          <p class="text-gray-600">You have 2 classes scheduled today</p>
        </RouterLink>

        <!-- Training Plan -->
        <RouterLink
          to="/customer/plan"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-purple-100 rounded-full mb-4 group-hover:bg-purple-200 transition-colors">
            <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Training Plan</h3>
          <p class="text-gray-600">Personalized plan updated last week</p>
        </RouterLink>

        <!-- Coupons -->
        <RouterLink
          to="/customer/coupon"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-orange-100 rounded-full mb-4 group-hover:bg-orange-200 transition-colors">
            <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Coupons</h3>
          <p class="text-gray-600">You have 3 active coupons available</p>
        </RouterLink>

        <!-- Profile Settings -->
        <RouterLink
          to="/customer/profile"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-cyan-100 rounded-full mb-4 group-hover:bg-cyan-200 transition-colors">
            <svg class="w-8 h-8 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">My Profile</h3>
          <p class="text-gray-600">Update your personal information</p>
        </RouterLink>

        <!-- Settings -->
        <RouterLink
          to="/customer/setting"
          class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300 group"
        >
          <div class="flex items-center justify-center w-16 h-16 bg-indigo-100 rounded-full mb-4 group-hover:bg-indigo-200 transition-colors">
            <svg class="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Settings</h3>
          <p class="text-gray-600">Manage your account preferences</p>
        </RouterLink>

        <!-- Today's Activity -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-center w-16 h-16 bg-teal-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">Today's Activity</h3>
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Check-ins:</span>
              <span class="font-semibold text-teal-600">2</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Classes attended:</span>
              <span class="font-semibold text-teal-600">1</span>
            </div>
            <div class="text-sm text-gray-500 mt-2">Next: Yoga Morning at 8:00 AM</div>
          </div>
        </div>

        <!-- Quick Stats -->
        <div class="bg-white rounded-xl shadow-lg p-6">
          <div class="flex items-center justify-center w-16 h-16 bg-pink-100 rounded-full mb-4">
            <svg class="w-8 h-8 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">This Month</h3>
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Total visits:</span>
              <span class="font-semibold text-pink-600">15</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Classes attended:</span>
              <span class="font-semibold text-pink-600">8</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">Active streak:</span>
              <span class="font-semibold text-pink-600">5 days</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Feedback Section -->
      <div class="mt-12 bg-white rounded-xl shadow-lg p-6">
        <h2 class="text-2xl font-semibold text-gray-900 mb-6">Leave your Feedback</h2>

      <!-- Star Rating -->
      <div class="flex items-center mb-4">
        <template v-for="star in 5" :key="star">
          <svg
            @click="setRating(star)"
            @mouseover="hoverRating = star"
            @mouseleave="hoverRating = 0"
            class="w-6 h-6 cursor-pointer"
            :class="[
              (hoverRating >= star || rating >= star)
                ? 'text-yellow-400'
                : 'text-gray-300'
            ]"
            xmlns="http://www.w3.org/2000/svg"
            fill="currentColor"
            viewBox="0 0 22 20"
          >
            <path
              d="M20.924 7.625a1.523 1.523 0 0 0-1.238-1.044l-5.051-.734-2.259-4.577a1.534 1.534 0 0 0-2.752 0L7.365 
                 5.847l-5.051.734A1.535 1.535 0 0 0 1.463 9.2l3.656 3.563-.863 
                 5.031a1.532 1.532 0 0 0 2.226 1.616L11 17.033l4.518 
                 2.375a1.534 1.534 0 0 0 2.226-1.617l-.863-5.03L20.537 
                 9.2a1.523 1.523 0 0 0 .387-1.575Z"
            />
          </svg>
        </template>
      </div>

      <!-- Comment Box -->
      <textarea
        v-model="comment"
        rows="4"
        placeholder="Share details of your own experience at our place"
        class="w-full border border-gray-300 rounded-lg p-3 mb-4 focus:ring-2 focus:ring-red-500 focus:outline-none"
      ></textarea>

      <!-- Upload Images -->
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">Upload images (optional)</label>

        <div class="flex flex-wrap gap-3">
          <!-- Nút add ảnh -->
          <label
            class="w-20 h-20 border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center cursor-pointer hover:border-red-400"
          >
            <Camera class="w-6 h-6 text-gray-400" />
            <input type="file" class="hidden" multiple 
            accept="image/png, image/jpeg, image/jpg, image/gif, image/webp"
            @change="previewImages" />
          </label>

          <!-- Preview ảnh -->
          <div
            v-for="(img, index) in images"
            :key="index"
            class="relative w-20 h-20 rounded-lg overflow-hidden border"
          >
            <img :src="img.url" class="w-full h-full object-cover" />
            <button
              @click="removeImage(index)"
              class="absolute top-1 right-1 bg-black bg-opacity-50 text-white rounded-full p-0.5 hover:bg-opacity-70"
            >
              <X class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

        <!-- Submit -->
        <button
          @click="submitFeedback"
          class="bg-red-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-red-700 transition-colors"
        >
          Submit Feedback
        </button>
      </div>

      <!-- Footer -->
      <div class="mt-12 text-center">
        <p class="text-gray-500">Keep up the great work on your fitness journey!</p>
      </div>
    </div>
  </div>
</template>
