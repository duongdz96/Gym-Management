import { createRouter,createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";

import ManagerLayout from "@/layout/ManagerLayout.vue";
import Coupon from "@/views/Manager/Coupon/Coupon.vue";
import AddCoupon from "@/views/Manager/Coupon/AddCoupon.vue";
import Product from "@/views/Manager/Product/Product.vue";
import AddProduct from "@/views/Manager/Product/AddProduct.vue";

import CustomerLayout from "@/layout/CustomerLayout.vue";
import CustomerHome from "@/views/Customer/CustomerHome.vue";

import ReceptionLayout from "@/layout/ReceptionLayout.vue";
import SalesSelect from "@/views/Reception/Sale/SalesSelect.vue";
import SalesCheckout from "@/views/Reception/Sale/SalesCheckout.vue";
import ReceptionHome from "@/views/Reception/ReceptionHome.vue";
import ImportProduct from "@/views/Manager/Product/ImportProduct.vue";
import ImportCheckout from "@/views/Manager/Product/ImportCheckout.vue";
import SalesBooking from "@/views/Reception/Sale/SalesBooking.vue";



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
            },
            {
                path: '/admin/product',
                name: 'product',
                component: Product
            },
            {
                path: '/admin/product/add',
                name: 'product.add',
                component: AddProduct
            },
            {
                path: '/admin/product/importproduct',
                name: 'product.importproduct',
                component: ImportProduct
            },
            {
                path: '/admin/product/importcheckout',
                name: 'product.importcheckout',
                component: ImportCheckout
            },
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
    },
    {
        path: "/reception",
        component: ReceptionLayout,
        children: [
            {
                path: '/reception/salesselect',
                name: 'salesselect',
                component: SalesSelect
            },
            {
                path: '/reception/',
                name: 'reception.home',
                component: ReceptionHome
            },
            {
                path: '/reception/salesCheckout',
                name: 'salesCheckout',
                component: SalesCheckout
            },
            {
                path: '/reception/salesBooking',
                name: 'salesBooking',
                component: SalesBooking
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router;