<template>
  <div class="min-h-screen flex flex-col bg-gradient-to-br from-blue-500 to-blue-600">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg sticky top-0 z-50">
      <div class="px-8 py-5 flex justify-between items-center">
        <div class="flex items-center gap-3">
          <Dumbbell class="w-8 h-8 text-blue-600" />
          <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-600 to-blue-700 bg-clip-text text-transparent">
            Gym Class Management
          </h1>
        </div>
        
        <div class="flex gap-4">
          <button 
            @click="currentView = 'manager'" 
            :class="currentView === 'manager' 
              ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white border-transparent' 
              : 'bg-white text-gray-700 border-gray-300 hover:border-blue-600 hover:bg-gray-50'"
            class="px-6 py-3 border-2 rounded-xl font-semibold transition-all duration-300 hover:-translate-y-0.5 flex items-center gap-2"
          >
            <Briefcase class="w-5 h-5" />
            Manager
          </button>
          <button 
            @click="currentView = 'teacher'" 
            :class="currentView === 'teacher' 
              ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white border-transparent' 
              : 'bg-white text-gray-700 border-gray-300 hover:border-blue-600 hover:bg-gray-50'"
            class="px-6 py-3 border-2 rounded-xl font-semibold transition-all duration-300 hover:-translate-y-0.5 flex items-center gap-2"
          >
            <GraduationCap class="w-5 h-5" />
            Teacher
          </button>
          <button 
            @click="currentView = 'student'" 
            :class="currentView === 'student' 
              ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white border-transparent' 
              : 'bg-white text-gray-700 border-gray-300 hover:border-blue-600 hover:bg-gray-50'"
            class="px-6 py-3 border-2 rounded-xl font-semibold transition-all duration-300 hover:-translate-y-0.5 flex items-center gap-2"
          >
            <Dumbbell class="w-5 h-5" />
            Student
          </button>
          <button 
            @click="currentView = 'receptionist'" 
            :class="currentView === 'receptionist' 
              ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white border-transparent' 
              : 'bg-white text-gray-700 border-gray-300 hover:border-blue-600 hover:bg-gray-50'"
            class="px-6 py-3 border-2 rounded-xl font-semibold transition-all duration-300 hover:-translate-y-0.5 flex items-center gap-2"
          >
            <UserCheck class="w-5 h-5" />
            Receptionist
          </button>
        </div>
      </div>
    </nav>

    <!-- Content -->
    <div class="flex-1 p-8 bg-gray-50">
      <!-- Manager View -->
      <div v-if="currentView === 'manager'" class="max-w-7xl mx-auto bg-white rounded-2xl shadow-xl overflow-hidden">
        <div class="flex gap-2 px-5 pt-5 border-b-2 border-gray-200">
          <button 
            @click="managerSubView = 'classes'" 
            :class="managerSubView === 'classes' 
              ? 'text-blue-600 border-blue-600' 
              : 'text-gray-500 border-transparent hover:text-blue-600'"
            class="px-5 py-3 font-semibold border-b-3 transition-all duration-300 -mb-0.5 flex items-center gap-2"
          >
            <BookOpen class="w-5 h-5" />
            Quản lý lớp học
          </button>
          <button 
            @click="managerSubView = 'approvals'" 
            :class="managerSubView === 'approvals' 
              ? 'text-blue-600 border-blue-600' 
              : 'text-gray-500 border-transparent hover:text-blue-600'"
            class="px-5 py-3 font-semibold border-b-3 transition-all duration-300 -mb-0.5 flex items-center gap-2"
          >
            <UserCheck class="w-5 h-5" />
            Duyệt giáo viên
          </button>
          <button 
            @click="managerSubView = 'leave'" 
            :class="managerSubView === 'leave' 
              ? 'text-blue-600 border-blue-600' 
              : 'text-gray-500 border-transparent hover:text-blue-600'"
            class="px-5 py-3 font-semibold border-b-3 transition-all duration-300 -mb-0.5 flex items-center gap-2"
          >
            <CalendarX class="w-5 h-5" />
            Duyệt đơn nghỉ
          </button>
        </div>
        
        <manager-class-list v-if="managerSubView === 'classes'" />
        <teacher-approval-list v-if="managerSubView === 'approvals'" />
        <manager-leave-approval v-if="managerSubView === 'leave'" />
      </div>

      <!-- Teacher View -->
      <div v-if="currentView === 'teacher'" class="max-w-7xl mx-auto bg-white rounded-2xl shadow-xl overflow-hidden">
        <div class="flex gap-2 px-5 pt-5 border-b-2 border-gray-200">
          <button 
            @click="teacherSubView = 'browse'" 
            :class="teacherSubView === 'browse' 
              ? 'text-blue-600 border-blue-600' 
              : 'text-gray-500 border-transparent hover:text-blue-600'"
            class="px-5 py-3 font-semibold border-b-3 transition-all duration-300 -mb-0.5 flex items-center gap-2"
          >
            <BookOpen class="w-5 h-5" />
            Đăng ký dạy
          </button>
          <button 
            @click="teacherSubView = 'sessions'" 
            :class="teacherSubView === 'sessions' 
              ? 'text-blue-600 border-blue-600' 
              : 'text-gray-500 border-transparent hover:text-blue-600'"
            class="px-5 py-3 font-semibold border-b-3 transition-all duration-300 -mb-0.5 flex items-center gap-2"
          >
            <CalendarCheck class="w-5 h-5" />
            Quản lý buổi dạy
          </button>
        </div>
        
        <teacher-class-browse v-if="teacherSubView === 'browse'" />
        <teacher-session-management v-if="teacherSubView === 'sessions'" />
      </div>

      <!-- Student View -->
      <div v-if="currentView === 'student'" class="max-w-7xl mx-auto bg-white rounded-2xl shadow-xl overflow-hidden">
        <student-class-browse />
      </div>

      <!-- Receptionist View -->
      <div v-if="currentView === 'receptionist'" class="max-w-7xl mx-auto bg-white rounded-2xl shadow-xl overflow-hidden">
        <receptionist-early-registration />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import ManagerClassList from './ManagerClassList.vue';
import TeacherApprovalList from './TeacherApprovalList.vue';
import ManagerLeaveApproval from './ManagerLeaveApproval.vue';
import TeacherClassBrowse from './TeacherClassBrowse.vue';
import TeacherSessionManagement from './TeacherSessionManagement.vue';
import StudentClassBrowse from './StudentClassBrowse.vue';
import ReceptionistEarlyRegistration from './ReceptionistEarlyRegistration.vue';
import { 
  Dumbbell, 
  Briefcase, 
  GraduationCap, 
  BookOpen, 
  UserCheck,
  CalendarCheck,
  CalendarX
} from 'lucide-vue-next';

// State
const currentView = ref('manager');
const managerSubView = ref('classes');
const teacherSubView = ref('browse');
</script>
