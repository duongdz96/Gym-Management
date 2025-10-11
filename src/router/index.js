import { createRouter,createWebHistory } from "vue-router";
import MainLayout from "@/layout/MainLayout.vue";
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";

import ManagerLayout from "@/layout/ManagerLayout.vue";
import Coupon from "@/views/Manager/Coupon/Coupon.vue";
import AddCoupon from "@/views/Manager/Coupon/AddCoupon.vue";
import Product from "@/views/Manager/Product/Product.vue";
import AddProduct from "@/views/Manager/Product/AddProduct.vue";
import Staff from "@/views/Manager/Staff/Staff.vue";
import Customer from "@/views/Manager/Customer/Customer.vue";
import Attendance from "@/views/Manager/Attendance/Attendance.vue";

import CustomerLayout from "@/layout/CustomerLayout.vue";
import CustomerHome from "@/views/Customer/CustomerHome.vue";
import CustomerPlan from "@/views/Customer/CustomerPlan.vue";
import CustomerClass from "@/views/Customer/CustomerClass.vue";
import CustomerDashboard from "@/views/Customer/CustomerDashboard.vue";
import CustomerCoupon from "@/views/Customer/CustomerCoupon.vue";
import CustomerMembership from "@/views/Customer/CustomerMembership.vue";
import CustomerProfile from "@/views/Customer/CustomerProfile.vue";
import CustomerSetting from "@/views/Customer/CustomerSetting.vue";

import ReceptionLayout from "@/layout/ReceptionLayout.vue";
import SalesSelect from "@/views/Reception/Sale/SalesSelect.vue";
import SalesCheckout from "@/views/Reception/Sale/SalesCheckout.vue";
import ReceptionHome from "@/views/Reception/ReceptionHome.vue";
import ImportProduct from "@/views/Manager/Product/ImportProduct.vue";
import ImportCheckout from "@/views/Manager/Product/ImportCheckout.vue";
import SalesBooking from "@/views/Reception/Sale/SalesBooking.vue";

import Test from "@/views/Test.vue";

const routes = [
    {
        path: "/test",
        component: Test
    },
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
        path: "/manager",
        component: ManagerLayout,
        children: [
            {
                path: '/manager/staff',
                name: 'staff',
                component: Staff
            },
            {
                path: '/manager/customer',
                name: 'customer',
                component: Customer
            },
            {
                path: '/manager/attendance',
                name: 'attendance',
                component: Attendance
            },
            {
                path: '/manager/coupon',
                name: 'coupon',
                component: Coupon
            },
            {
                path: '/manager/coupon/add',
                name: 'coupon.add',
                component: AddCoupon
            },
            {
                path: '/manager/product',
                name: 'product',
                component: Product
            },
            {
                path: '/manager/product/add',
                name: 'product.add',
                component: AddProduct
            },
            {
                path: '/manager/product/importproduct',
                name: 'product.importproduct',
                component: ImportProduct
            },
            {
                path: '/manager/product/importcheckout',
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
            {
                path: '/customer/dashboard',
                name: 'customer.dashboard',
                component: CustomerDashboard
            },
            {
                path: '/customer/membership',
                name: 'customer.membership',
                component: CustomerMembership
            },
            {
                path: '/customer/class',
                name: 'customer.class',
                component: CustomerClass
            },
            {
                path: '/customer/coupon',
                name: 'customer.coupon',
                component: CustomerCoupon
            },
            {
                path: '/customer/plan',
                name: 'customer.plan',
                component: CustomerPlan
            },
            {
                path: '/customer/profile',
                name: 'customer.profile',
                component: CustomerProfile
            },
            {
                path: '/customer/setting',
                name: 'customer.setting',
                component: CustomerSetting
            }
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