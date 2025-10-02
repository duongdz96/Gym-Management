<script setup>
import { ref } from "vue";
import { RouterLink } from "vue-router";
import { Camera, X } from "lucide-vue-next"; // import icon

const rating = ref(0);
const hoverRating = ref(0);
const comment = ref("");
const images = ref([]);

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

const submitFeedback = () => {
  console.log("Rating:", rating.value);
  console.log("Comment:", comment.value);
  console.log("Images:", images.value);
  alert("Feedback submitted!");
};
</script>

<template>
  <div class="mx-auto max-w-7xl px-6 py-8">
    <h1 class="text-2xl font-bold text-gray-800 mb-6">
      Welcome back, Customer 👋
    </h1>

    <!-- Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <!-- Membership -->
      <div class="bg-white p-6 rounded-xl shadow hover:shadow-lg transition">
        <h2 class="font-semibold text-gray-700 mb-2">My Membership</h2>
        <p class="text-gray-500 text-sm">
          Active until: <span class="font-medium">30/12/2025</span>
        </p>
        <RouterLink to="/membership"
          class="mt-3 inline-block text-red-600 text-sm font-semibold hover:underline">
          View details →
        </RouterLink>
      </div>

      <!-- Classes -->
      <div class="bg-white p-6 rounded-xl shadow hover:shadow-lg transition">
        <h2 class="font-semibold text-gray-700 mb-2">My Classes</h2>
        <p class="text-gray-500 text-sm">You have 2 classes today.</p>
        <RouterLink to="/classes"
          class="mt-3 inline-block text-red-600 text-sm font-semibold hover:underline">
          View schedule →
        </RouterLink>
      </div>

      <!-- Training Plan -->
      <div class="bg-white p-6 rounded-xl shadow hover:shadow-lg transition">
        <h2 class="font-semibold text-gray-700 mb-2">Training Plan</h2>
        <p class="text-gray-500 text-sm">Plan with PT updated last week.</p>
        <RouterLink to="/plan"
          class="mt-3 inline-block text-red-600 text-sm font-semibold hover:underline">
          View plan →
        </RouterLink>
      </div>

      <!-- Coupons -->
      <div class="bg-white p-6 rounded-xl shadow hover:shadow-lg transition">
        <h2 class="font-semibold text-gray-700 mb-2">Coupons</h2>
        <p class="text-gray-500 text-sm">You have 3 active coupons.</p>
        <RouterLink to="/coupons"
          class="mt-3 inline-block text-red-600 text-sm font-semibold hover:underline">
          View coupons →
        </RouterLink>
      </div>

      <!-- Notifications -->
      <div class="bg-white p-6 rounded-xl shadow hover:shadow-lg transition">
        <h2 class="font-semibold text-gray-700 mb-2">Notifications</h2>
        <ul class="list-disc pl-4 text-sm text-gray-500">
          <li>Your class "Yoga Morning" starts at 8:00</li>
          <li>New coupon available!</li>
        </ul>
        <RouterLink to="/notifications"
          class="mt-3 inline-block text-red-600 text-sm font-semibold hover:underline">
          View all →
        </RouterLink>
      </div>

      <!-- History -->
      <div class="bg-white p-6 rounded-xl shadow hover:shadow-lg transition">
        <h2 class="font-semibold text-gray-700 mb-2">History</h2>
        <p class="text-gray-500 text-sm">You checked in 15 times this month.</p>
        <RouterLink to="/history"
          class="mt-3 inline-block text-red-600 text-sm font-semibold hover:underline">
          View history →
        </RouterLink>
      </div>
    </div>

    <!-- Feedback -->
    <div class="mt-12 bg-white p-6 rounded-xl shadow">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">Leave your Feedback</h2>

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
        class="bg-red-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-red-700"
      >
        Submit Feedback
      </button>
    </div>
  </div>
</template>
