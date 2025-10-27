<script setup>
import Footer from '@/components/Footer.vue';
import ReceptionHeader from '@/components/ReceptionHeader.vue';
</script>

<template>
    <div class=" flex flex-col min-h-screen bg-gray-50">
        <div class="">
            <ReceptionHeader></ReceptionHeader>
        </div>

        <div class="min-h-screen">
            <RouterView />
        </div>

        <div>
            <Footer></Footer>
        </div>
    </div>
</template>