<script setup>
import { Facebook,Youtube,FileMusic, Instagram } from 'lucide-vue-next';
</script>

<template>
    <footer class="mt-8 border-t border-gray-200 dark:border-gray-800 bg-stone-900 dark:bg-gray-950">
        <div class="mx-auto max-w-7xl px-6 py-10 grid gap-8 md:grid-cols-4">
            <div>
                <div class="text-sm font-semibold text-white dark:text-white">DỊCH VỤ</div>
                <ul class="mt-3 space-y-1 text-sm text-zinc-500 dark:text-gray-300">
                    <li><a href="https://cali.vn/coaching/dance" target="_blank" class="hover:text-white">Dance</a></li>
                    <li><a href="https://cali.vn/coaching/group-x" target="_blank" class="hover:text-white">Group X</a></li>
                    <li><a href="https://cali.vn/coaching/yoga" target="_blank" class="hover:text-white">Yoga</a></li>
                    <li><a href="https://cali.vn/training/hypoxi" target="_blank" class="hover:text-white">Công nghệ giảm mỡ Hypoxi</a></li>
                    <li><a href="https://cali.vn/training/huan-luyen-vien-suc-khoe" target="_blank" class="hover:text-white">Huấn Luyện Viên Sức Khỏe</a></li>
                    <li><a href="https://cali.vn/training/kickfitmma" target="_blank" class="hover:text-white">Kickfit & MMA</a></li>
                </ul>
            </div>
            <div>
                <div class="text-sm font-semibold text-white dark:text-white">CÔNG TY</div>
                <ul class="mt-3 space-y-1 text-sm text-zinc-500 dark:text-gray-300">
                    <li><a href="https://cali.vn/ve-chung-toi" target="_blank" class="hover:text-white">Về chúng tôi</a></li>
                    <li><a href="https://cali.vn/dieu-khoan-su-dung" target="_blank" class="hover:text-white">Điều kiện sử dụng</a></li>
                    <li><a href="https://cali.vn/chinh-sach-bao-mat" target="_blank" class="hover:text-white">Chính sách bảo mật</a></li>
                    <li><a href="https://cali.vn/chinh-sach-thanh-toan" target="_blank" class="hover:text-white">Chính sách thanh toán</a></li>
                    <li><a href="https://cali.vn/chinh-sach-giai-quyet-khieu-nai" target="_blank" class="hover:text-white">Chính sách giải quyết khiếu nại</a></li>
                    <li><a href="https://cali.vn/chinh-sach-giai-quyet-khieu-nai" target="_blank" class="hover:text-white">Chính sách bảo vệ dữ liệu cá nhân</a></li>
                    <li><a href="https://careerflg.vn/" target="_blank" class="hover:text-white">Tuyển dụng</a></li>
                    <li><a href="https://cali.vn/lien-he" target="_blank" class="hover:text-white">Liên hệ</a></li>
                </ul>
            </div>
            <div>
                <div class="text-sm font-semibold text-white dark:text-white">SUPPORT</div>
                <p class="mt-2 text-4xl font-bold text-red-500 dark:text-white"><a href="tel:18006995">1800 6995</a></p>
                <div class="mt-4 flex items-center gap-3">
                    <a href="https://www.facebook.com/cfycvn" target="_blank" aria-label="Facebook" class="text-gray-600 dark:text-gray-300 hover:text-red-600">
                        <Facebook />
                    </a>
                    <a href="https://www.youtube.com/user/CaliFitnessYogaVN" target="_blank" aria-label="YouTube" class="text-gray-600 dark:text-gray-300 hover:text-red-600">
                        <Youtube />
                    </a>
                    <a href="https://www.instagram.com/cfycvn" target="_blank" aria-label="Instagram" class="text-gray-600 dark:text-gray-300 hover:text-red-600">
                        <Instagram />
                    </a>
                    <a href="https://open.spotify.com/user/aytjp94wdcsqff7tkbwx5p8ek" target="_blank" aria-label="Spotify" class="text-gray-600 dark:text-gray-300 hover:text-red-600">
                        <FileMusic />
                    </a>
                    <a href="https://www.tiktok.com/@cali.vn" target="_blank" aria-label="Tiktok" class="text-gray-600 dark:text-gray-300 hover:text-red-600">
                        <FileMusic />
                    </a>
                </div>
            </div>
            <div>
                <div class="text-sm font-semibold text-white dark:text-white">THÔNG TIN</div>
                <div class="mt-6 text-sm text-gray-600 dark:text-gray-300">
                    <p>Công ty TNHH Trung Tâm Thể Dục Thể Hình & Yoga California</p>
                    <p>Số 126 Hùng Vương, P.12, Q.5, Tp. Hồ Chí Minh</p>
                    <p>MST: 0305060028</p>
                </div>
            </div>
        </div>
        <div class=" text-center py-4 text-sm text-white dark:text-gray-300">© California Fitness & Yoga Center 2025</div>
    </footer>
</template>