<script setup>
import Footer from '@/components/Footer.vue';
import CustomerHeader from '@/components/CustomerHeader.vue';
</script>

<template>
    <div class=" flex flex-col min-h-screen bg-gray-50">
        <div class="">
            <CustomerHeader></CustomerHeader>
        </div>

        <div class="min-h-screen">
            <RouterView />
        </div>

        <div>
            <Footer></Footer>
        </div>
    </div>
</template>