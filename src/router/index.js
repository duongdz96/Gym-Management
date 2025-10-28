import { createRouter, createWebHistory } from "vue-router";

import { setupRouterGuard } from "./guard";
import MainLayout from "@/layout/MainLayout.vue";
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";
import Forbidden from "@/views/Forbidden.vue";

import ManagerLayout from "@/layout/ManagerLayout.vue";
import Coupon from "@/views/Manager/Coupon/Coupon.vue";
import AddCoupon from "@/views/Manager/Coupon/AddCoupon.vue";
import Product from "@/views/Manager/Product/Product.vue";
import AddProduct from "@/views/Manager/Product/AddProduct.vue";
import ImportProduct from "@/views/Manager/Product/ImportProduct.vue";
import ImportCheckout from "@/views/Manager/Product/ImportCheckout.vue";
import Staff from "@/views/Manager/Staff/Staff.vue";
import Customer from "@/views/Manager/Customer/Customer.vue";
import Attendance from "@/views/Manager/Attendance/Attendance.vue";
import ClassTemplate from "@/views/Manager/Classes/ClassTemplate.vue";
import AddClassTemplate from "@/views/Manager/Classes/AddClassTemplate.vue";
import ClassSchedule from "@/views/Manager/Classes/ClassSchedule.vue";
import Banner from "@/views/Manager/Config/Banner.vue";
import ManageMembership from "@/views/Manager/Membership/ManageMembership.vue";
import ManageAddMembership from "@/views/Manager/Membership/ManageAddMembership.vue";
import ManageAddTier from "@/views/Manager/Membership/ManageAddTier.vue";

import CustomerLayout from "@/layout/CustomerLayout.vue";
import CustomerHome from "@/views/Customer/CustomerHome.vue";
import CustomerDashboard from "@/views/Customer/CustomerDashboard.vue";
import CustomerMembership from "@/views/Customer/CustomerMembership.vue";
import CustomerClass from "@/views/Customer/CustomerClass.vue";
import CustomerCoupon from "@/views/Customer/CustomerCoupon.vue";
import CustomerPlan from "@/views/Customer/CustomerPlan.vue";
import CustomerProfile from "@/views/Customer/CustomerProfile.vue";
import CustomerSetting from "@/views/Customer/CustomerSetting.vue";

import ReceptionLayout from "@/layout/ReceptionLayout.vue";
import ReceptionHome from "@/views/Reception/ReceptionHome.vue";
import AddMembership from "@/views/Reception/AddMembership.vue";
import SalesSelect from "@/views/Reception/Sale/SalesSelect.vue";
import SalesCheckout from "@/views/Reception/Sale/SalesCheckout.vue";
import SalesBooking from "@/views/Reception/Sale/SalesBooking.vue";

import PTLayout from "@/layout/PTLayout.vue";
import PTSchedule from "@/views/PT/PTSchedule.vue";
import PTProfile from "@/views/PT/PTProfile.vue";
import PTMembers from "@/views/PT/PTMembers.vue";

import Test from "@/views/Test.vue";
import SchedulePattern from "@/views/Manager/Classes/SchedulePattern.vue";

const routes = [
    {
        path: "/test",
        component: Test
    },
    {
        path: "/",
        component: MainLayout,
        children: [
            { path: "", name: "home", component: Home },
            { path: "login", name: "login", component: Login },
            { path: "forbidden", name: "forbidden", component: Forbidden },
            { path: "test", component: Test },
        ],
    },

    {
        path: "/manager",
        component: ManagerLayout,
        // meta: { requiresAuth: true, roles: ["MANAGER"] },
        children: [
            { path: "staff", name: "staff", component: Staff },
            { path: "customer", name: "customer", component: Customer },
            { path: "attendance", name: "attendance", component: Attendance },
            { path: "coupon", name: "coupon", component: Coupon },
            { path: "coupon/add", name: "coupon.add", component: AddCoupon },
            { path: "product", name: "product", component: Product },
            { path: "product/add", name: "product.add", component: AddProduct },
            { path: "product/importproduct", name: "product.importproduct", component: ImportProduct },
            { path: "product/importcheckout", name: "product.importcheckout", component: ImportCheckout },
            { path: "classtemplate", name: "classtemplate", component: ClassTemplate },
            { path: "classtemplate/add", name: "classtemplate.add", component: AddClassTemplate },
            { path: "classschedule", name: "classschedule", component: ClassSchedule },
            { path: "banner", name: "banner", component: Banner },
            { path: "membership", name: "membership", component: ManageMembership },
            { path: "membership/add", name: "membership.add", component: ManageAddMembership },
            { path: "membership/add-tier", name: "membership.add-tier", component: ManageAddTier }
        ],
    },

    {
        path: "/customer",
        component: CustomerLayout,
        meta: { requiresAuth: true, roles: ["MEMBER"] },
        children: [
            { path: "", name: "customer.home", component: CustomerHome },
            { path: "dashboard", name: "customer.dashboard", component: CustomerDashboard },
            { path: "membership", name: "customer.membership", component: CustomerMembership },
            { path: "class", name: "customer.class", component: CustomerClass },
            { path: "coupon", name: "customer.coupon", component: CustomerCoupon },
            { path: "plan", name: "customer.plan", component: CustomerPlan },
            { path: "profile", name: "customer.profile", component: CustomerProfile },
            { path: "setting", name: "customer.setting", component: CustomerSetting },
        ],
    },

    {
        path: "/reception",
        component: ReceptionLayout,
        meta: { requiresAuth: true, roles: ["RECEPTIONIST"] },
        children: [
            { path: "", name: "reception.home", component: ReceptionHome },
            { path: "add-membership", name: "add-membership", component: AddMembership },
            { path: "salesselect", name: "salesselect", component: SalesSelect },
            { path: "salescheckout", name: "salesCheckout", component: SalesCheckout },
            { path: "salesbooking", name: "salesBooking", component: SalesBooking },
        ],
    },
    {
        path: "/pt",
        component: PTLayout,
        children: [
            {
                path: '/pt/',
                name: 'pt.home',
                component: PTSchedule
            },
            {
                path: '/pt/schedule',
                name: 'pt.schedule',
                component: PTSchedule
            },
            {
                path: '/pt/profile',
                name: 'pt.profile',
                component: PTProfile
            },
            {
                path: '/pt/members',
                name: 'pt.members',
                component: PTMembers
            },
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

setupRouterGuard(router);

export default router;
