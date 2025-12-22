import { createRouter, createWebHistory } from "vue-router";

import { setupRouterGuard } from "./guard";
import MainLayout from "@/layout/MainLayout.vue";
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";
import Forbidden from "@/views/Forbidden.vue";
import ChangePassword from "@/views/ChangePassword.vue";
import Profile from "@/views/Profile.vue";

import ManagerLayout from "@/layout/ManagerLayout.vue";
import Coupon from "@/views/Manager/Coupon/Coupon.vue";
import AddCoupon from "@/views/Manager/Coupon/AddCoupon.vue";
import CouponDetail from "@/views/Manager/Coupon/CouponDetail.vue";
import EditCoupon from "@/views/Manager/Coupon/EditCoupon.vue";
import ProductList from "@/views/Manager/Product/ProductList.vue";
import SupplierList from "@/views/Manager/Product/SupplierList.vue";
import ImportGoodsManager from "@/views/Manager/Product/ImportGoods.vue";
import ImportReceiptList from "@/views/Manager/Product/ImportReceiptList.vue";
import ImportReceiptDetail from "@/views/Manager/Product/ImportReceiptDetail.vue";
import Staff from "@/views/Manager/Staff/Staff.vue";
import Customer from "@/views/Manager/Customer/Customer.vue";
import Attendance from "@/views/Manager/Attendance/Attendance.vue";
import ManagerClassList from "@/views/Manager/Classes/ManagerClassList.vue";
import Banner from "@/views/Manager/Config/Banner.vue";
import ManageMembership from "@/views/Manager/Membership/ManageMembership.vue";
import ManageAddMembership from "@/views/Manager/Membership/ManageAddMembership.vue";
import ManageAddTier from "@/views/Manager/Membership/ManageAddTier.vue";
import ManagerDashboard from "@/views/Manager/ManagerDashboard.vue";

import CustomerLayout from "@/layout/CustomerLayout.vue";
import CustomerHome from "@/views/Customer/CustomerHome.vue";
import CustomerDashboard from "@/views/Customer/CustomerDashboard.vue";
import CustomerMembership from "@/views/Customer/CustomerMembership.vue";
import CustomerClass from "@/views/Customer/Classes/CustomerClass.vue";
import CustomerPersonalCalendar from "@/views/Customer/Classes/CustomerPersonalCalendar.vue";
import CustomerCoupon from "@/views/Customer/CustomerCoupon.vue";
import CustomerPlan from "@/views/Customer/CustomerPlan.vue";
import CustomerProfile from "@/views/Customer/CustomerProfile.vue";
import CustomerSetting from "@/views/Customer/CustomerSetting.vue";

import ReceptionLayout from "@/layout/ReceptionLayout.vue";
import ReceptionHome from "@/views/Reception/ReceptionHome.vue";
import ReceptionAddMembership from "@/views/Reception/Membership/ReceptionAddMembership.vue";
import ReceptionMemberships from "@/views/Reception/Membership/ReceptionMemberships.vue";
import SalesSelect from "@/views/Reception/Sale/SalesSelect.vue";
import SalesCheckout from "@/views/Reception/Sale/SalesCheckout.vue";
import ReceptionClassRegistration from "@/views/Reception/Classes/ReceptionClassRegistration.vue";

import PTLayout from "@/layout/PTLayout.vue";
import PTHome from "@/views/PT/PTHome.vue";
import PTSchedule from "@/views/PT/PTSchedule.vue";
import PTProfile from "@/views/PT/PTProfile.vue";
import PTMembers from "@/views/PT/PTMembers.vue";

import TestIndex from "@/views/Test/index.vue";
import Test from "@/views/Test.vue";
import TeacherRegister from "@/views/Teacher/TeacherRegisterClass.vue";
import AddStaff from "@/views/Manager/Staff/AddStaff.vue";
import CustomerBillHistory from "@/views/Customer/History/CustomerBillHistory.vue";
import TeacherLayout from "@/layout/TeacherLayout.vue";
import TeacherPersonalCalendar from "@/views/Teacher/TeacherPersonalCalendar.vue";
import RoomManagement from "@/views/Manager/Classes/RoomManagement.vue";
import TeacherApprovalList from "@/views/Manager/Classes/TeacherApprovalList.vue";
import ManagerLeaveApproval from "@/views/Manager/Classes/ManagerLeaveApproval.vue";
import TeacherAttendance from "@/views/Teacher/Classes/TeacherAttendance.vue";

const routes = [
    {
        path: "/test",
        component: TestIndex
    },
    {
        path: "/",
        component: MainLayout,
        children: [
            { path: "", name: "home", component: Home },
            { path: "login", name: "login", component: Login },
            { path: "forbidden", name: "forbidden", component: Forbidden },
            { path: "test", component: Test },
            { path: "/profile", name: "profile", component: Profile },
            { path: "change-password", component: ChangePassword }
        ],
    },

    {
        path: "/manager",
        component: ManagerLayout,
        meta: { requiresAuth: true, roles: ["MANAGER"] },
        children: [
            { path: "", name: "manager.dashboard", component: ManagerDashboard },
            { path: "dashboard", name: "manager.dashboard.full", component: ManagerDashboard },
            { path: "staff", name: "staff", component: Staff },
            { path: "staff/add", name: "staff.add", component: AddStaff },
            { path: "customer", name: "customer", component: Customer },
            { path: "attendance", name: "attendance", component: Attendance },
            { path: "coupon", name: "coupon", component: Coupon },
            { path: "coupon/:id", name: "CouponDetail", component: CouponDetail },
            { path: "coupon/add", name: "coupon.add", component: AddCoupon },
            { path: "coupon/:id", name: "coupon.edit", component: EditCoupon },
            { path: "product", redirect: "/manager/product/list" },
            { path: "product/list", name: "product.list", component: ProductList },
            { path: "product/supplier", name: "product.supplier", component: SupplierList },
            { path: "product/import", name: "product.import", component: ImportGoodsManager },
            { path: "product/history", name: "product.history", component: ImportReceiptList },
            { path: "product/history/:id", name: "product.history.detail", component: ImportReceiptDetail },
            { path: "class", name: "manager.class", component: ManagerClassList },
            { path: "banner", name: "banner", component: Banner },
            { path: "membership", name: "membership", component: ManageMembership },
            { path: "membership/add", name: "membership.add", component: ManageAddMembership },
            { path: "membership/add-tier", name: "membership.add-tier", component: ManageAddTier },
            { path: "room", name: "room", component: RoomManagement },
            { path: "teacher-approval", name: "manager.teacher-approval", component: TeacherApprovalList },
            { path: "leave-approval", name: "manager.leave-approval", component: ManagerLeaveApproval },
            { path: "banner", name: "banner", component: Banner },

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
            { path: "calendar", name: "customer.calendar", component: CustomerPersonalCalendar },
            { path: "coupon", name: "customer.coupon", component: CustomerCoupon },
            { path: "plan", name: "customer.plan", component: CustomerPlan },
            { path: "profile", name: "customer.profile", component: CustomerProfile },
            { path: "setting", name: "customer.setting", component: CustomerSetting },
            { path: "billhistory", name: "customer.billhistory", component: CustomerBillHistory },

            // Workout Management Routes
            {
                path: "workout/exercises",
                name: "customer.workout.exercises",
                component: () => import("@/views/Shared/Workout/Exercise/ExerciseList.vue")
            },
            {
                path: "workout/exercise/create",
                name: "customer.workout.exercise.create",
                component: () => import("@/views/Shared/Workout/Exercise/ExerciseCreate.vue")
            },
            {
                path: "workout/exercise/:id",
                name: "customer.workout.exercise.detail",
                component: () => import("@/views/Shared/Workout/Exercise/ExerciseDetail.vue")
            },
            {
                path: "workout/exercise/edit/:id",
                name: "customer.workout.exercise.edit",
                component: () => import("@/views/Shared/Workout/Exercise/ExerciseCreate.vue")
            },
            {
                path: "workout/routines",
                name: "customer.workout.routines",
                component: () => import("@/views/Shared/Workout/WorkoutRoutine/WorkoutRoutineList.vue")
            },
            {
                path: "workout/routine/create",
                name: "customer.workout.routine.create",
                component: () => import("@/views/Shared/Workout/WorkoutRoutine/WorkoutRoutineCreate.vue")
            },
            {
                path: "workout/routine/edit/:id",
                name: "customer.workout.routine.edit",
                component: () => import("@/views/Shared/Workout/WorkoutRoutine/WorkoutRoutineCreate.vue")
            },
            {
                path: "workout/training-plans",
                name: "customer.workout.training-plans",
                component: () => import("@/views/Customer/Workout/CustomerTrainingPlanList.vue")
            },
            {
                path: "workout/training-plan/start/:id",
                name: "customer.workout.training-plan.start",
                component: () => import("@/views/Customer/Workout/CustomerTrainingPlanStart.vue")
            },
        ],
    },

    {
        path: "/reception",
        component: ReceptionLayout,
        meta: { requiresAuth: true, roles: ["RECEPTIONIST"] },
        children: [
            { path: "", name: "reception.home", component: ReceptionHome },
            { path: "memberships", name: "reception.memberships", component: ReceptionMemberships },
            { path: "add-membership", name: "add-membership", component: ReceptionAddMembership },
            { path: "class-registration", name: "reception.class-registration", component: ReceptionClassRegistration },
            { path: "salesselect", name: "salesselect", component: SalesSelect },
            { path: "salescheckout", name: "salesCheckout", component: SalesCheckout },
        ],
    },
    {
        path: "/pt",
        component: PTLayout,
        meta: { requiresAuth: true, roles: ["PT"] },
        children: [
            {
                path: '/pt/',
                name: 'pt.home',
                component: PTHome
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

            // Workout Management Routes
            {
                path: '/pt/workout/exercises',
                name: 'pt.workout.exercises',
                component: () => import("@/views/Shared/Workout/Exercise/ExerciseList.vue")
            },
            {
                path: '/pt/workout/exercise/create',
                name: 'pt.workout.exercise.create',
                component: () => import("@/views/Shared/Workout/Exercise/ExerciseCreate.vue")
            },
            {
                path: '/pt/workout/exercise/:id',
                name: 'pt.workout.exercise.detail',
                component: () => import("@/views/Shared/Workout/Exercise/ExerciseDetail.vue")
            },
            {
                path: '/pt/workout/exercise/edit/:id',
                name: 'pt.workout.exercise.edit',
                component: () => import("@/views/Shared/Workout/Exercise/ExerciseCreate.vue")
            },
            {
                path: '/pt/workout/routines',
                name: 'pt.workout.routines',
                component: () => import("@/views/Shared/Workout/WorkoutRoutine/WorkoutRoutineList.vue")
            },
            {
                path: '/pt/workout/routine/create',
                name: 'pt.workout.routine.create',
                component: () => import("@/views/Shared/Workout/WorkoutRoutine/WorkoutRoutineCreate.vue")
            },
            {
                path: '/pt/workout/routine/edit/:id',
                name: 'pt.workout.routine.edit',
                component: () => import("@/views/Shared/Workout/WorkoutRoutine/WorkoutRoutineCreate.vue")
            },
        ]
    },
    {
        path: "/teacher",
        component: TeacherLayout,
        meta: { requiresAuth: true, roles: ["TEACHER"] },
        children: [
            { path: "/teacher/registerclass", name: "teacher.register", component: TeacherRegister },
            { path: "/teacher/calendar", name: "teacher.calendar", component: TeacherPersonalCalendar },
            { path: "/teacher/attendance", name: "teacher.attendance", component: TeacherAttendance },

        ],
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
});

setupRouterGuard(router);

export default router;
