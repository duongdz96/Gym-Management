<script setup>
import Footer from "@/components/Footer.vue";
import PTHeader from "@/components/PTHeader.vue";
</script>

<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <div class="">
      <PTHeader></PTHeader>
    </div>

    <div class="min-h-screen">
      <RouterView />
    </div>

    <div>
      <Footer></Footer>
    </div>
  </div>
</template>
