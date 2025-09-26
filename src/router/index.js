import { createRouter,createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import BareLayout from "@/layout/BareLayout.vue";

import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";

const routes = [
    {
        path: "/",
        component: MainLayout,
        children: [
            {
                path: '',
                name: 'home',
                component: Home
            }
        ]
    },
    {
        path: "/",
        component: BareLayout,
        children: [
            {
                path: '/login',
                name: 'login',
                component: Login
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router;