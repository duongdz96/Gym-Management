<script setup>
import { ChevronDown, ChevronUp } from "lucide-vue-next"
</script>

<template>
    <header class="w-full bg-stone-900">
        <div class="mx-auto max-w-7xl px-6 py-3 flex items-center justify-between">
            <div class="flex items-center gap-3">
                <RouterLink to="/" class="inline-flex items-center gap-2">
                    <span class="h-8 w-8 rounded-full bg-red-600 inline-block"></span>
                    <span class="font-bold text-white tracking-wider uppercase">Gym Management</span>
                </RouterLink>
            </div>
            <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
                <div class="relative group">
                    <a href="#classes"
                        class="flex items-center justify-between w-40 text-white uppercase tracking-wider border-b-2 border-transparent hover:border-red-500 transition-colors duration-300">
                        <span>Dịch vụ</span>
                        <!-- Icon -->
                        <ChevronDown
                        class="w-4 h-4 transform transition-transform duration-300 group-hover:rotate-180"
                        />
                    </a>

                    <!-- Dropdown -->
                    <div class="z-10 absolute left-0 top-full hidden w-40 bg-stone-900 shadow-lg group-hover:block">
                        <a href="#service1" class="block px-4 py-2 text-white hover:bg-red-500">Lớp Học</a>
                        <a href="#service2" class="block px-4 py-2 text-white hover:bg-red-500">Huấn Luyện</a>
                    </div>
                </div>
            </nav>
            <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
                <a href="#clubs" class=" text-white uppercase tracking-wider hover:text-red-600">Câu lạc bộ</a>
            </nav>
            <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
                <a href="#schedule" class=" text-white uppercase tracking-wider hover:text-red-600">Lịch tập</a>
            </nav>
            <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
                <a href="#pricing" class=" text-white uppercase tracking-wider hover:text-red-600">Bảng giá</a>
            </nav>
            <nav class="hidden md:flex items-center gap-6 text-sm font-medium">
                <a href="#gifts" class=" text-white uppercase tracking-wider hover:text-red-600">Quà tặng</a>
            </nav>
            <div class="hidden md:flex">
                <RouterLink :to="{name: 'login'}" class="inline-flex items-center rounded-md bg-red-600 px-4 py-2 text-white text-sm font-semibold hover:bg-black">Đăng Nhập</RouterLink>
            </div>
        </div>
    </header>
</template>