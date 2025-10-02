import { createRouter,createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";

import ManagerLayout from "@/layout/ManagerLayout.vue";
import Coupon from "@/views/Manager/Coupon/Coupon.vue";
import AddCoupon from "@/views/Manager/Coupon/AddCoupon.vue";

import CustomerLayout from "@/layout/CustomerLayout.vue";
import CustomerHome from "@/views/Customer/CustomerHome.vue";

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
    },
    {
        path: "/customer",
        component: CustomerLayout,
        children: [
            {
                path: '/customer/',
                name: 'customer.home',
                component: CustomerHome
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router;