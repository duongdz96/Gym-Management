<script setup>
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
</script>

<template>
    <div class=" flex flex-col min-h-screen bg-gray-50">
        <div class="">
            <Header></Header>
        </div>

        <div class="">
            <RouterView />
        </div>

        <div>
            <Footer></Footer>
        </div>
    </div>
</template>