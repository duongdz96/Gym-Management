import { createRouter,createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import ManagerLayout from "@/layout/ManagerLayout.vue";

import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";
import Coupon from "@/views/Coupon.vue";
import AddCoupon from "@/views/Coupon/AddCoupon.vue";

const routes = [
    {
        path: "/",
        component: MainLayout,
        children: [
            {
                path: '',
                name: 'home',
                component: Home
            },
            {
                path: '/login',
                name: 'login',
                component: Login
            }
        ]
    },
    {
        path: "/admin",
        component: ManagerLayout,
        children: [
            {
                path: '/admin/coupon',
                name: 'coupon',
                component: Coupon
            },
            {
                path: '/admin/coupon/add',
                name: 'coupon.add',
                component: AddCoupon
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router;