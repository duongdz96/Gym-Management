<script setup>
import ManagerHeader from '@/components/ManagerHeader.vue';
import Footer from '@/components/Footer.vue';
</script>

<template>
    <div class=" flex flex-col min-h-screen bg-gray-50">
        <div class="">
            <ManagerHeader></ManagerHeader>
        </div>

        <div class="min-h-screen">
            <RouterView />
        </div>

        <div>
            <Footer></Footer>
        </div>
    </div>
</template>