<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {useRouter} from 'vue-router'
import api from '@/services/api'

type ClassTemplate = {
  id: number
  name: string
  description: string
  difficultyLevel: string
  status: string
}

const classtemplates = ref<ClassTemplate[]>([])
const search = ref('')
const difficultyFilter = ref('')
const statusFilter = ref('')
const router = useRouter();

onMounted(async () => {
  try {
    const res = await api.get("/classtemplate")
    classtemplates.value = res.data
  } catch (err) {
    console.error("Error fetching class templates:", err)
  }
})


const filteredTemplates = computed(() => {
  return classtemplates.value.filter(t => {
    const matchesName = t.name?.toLowerCase().includes(search.value.toLowerCase())
    const matchesDifficulty = difficultyFilter.value ? t.difficultyLevel === difficultyFilter.value : true
    const matchesStatus = statusFilter.value ? t.status === statusFilter.value : true
    return matchesName && matchesDifficulty && matchesStatus
  })
})


const handleAdd = () => {
  alert('Open Add Template Modal')
}
const handleEdit = (id: number) => {
  alert(`Open Edit Template Modal for ID: ${id}`)
}
const handleDelete = (id: number) => {
  if (confirm(`Are you sure you want to delete template ID: ${id}?`)) {
    alert(`Deleting template ID: ${id}`)
  }
}

const handleSelect = (classtemplate : ClassTemplate) => {
  const templateJSON = JSON.stringify(classtemplate);
  sessionStorage.setItem('selectedTemplate', templateJSON);
  router.push({name: "classschedule"});
}

</script>

<template>
  <div class="space-y-4 p-4 md:p-6">
    <div class="flex flex-col md:flex-row justify-between items-center gap-4">
      <h1 class="text-2xl font-semibold text-gray-800">Class Templates</h1>
      <div class="flex flex-wrap items-center gap-2">
        <input
          v-model="search"
          type="text"
          placeholder="Search by template name"
          class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
        />
        <select v-model="difficultyFilter" class="px-3 py-2 border border-gray-300 rounded-lg">
          <option value="">All Difficulties</option>
          <option value="Beginner">Beginner</option>
          <option value="Intermediate">Intermediate</option>
          <option value="Advanced">Advanced</option>
        </select>
        <select v-model="statusFilter" class="px-3 py-2 border border-gray-300 rounded-lg">
          <option value="">All Statuses</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>
        <button
          @click="handleAdd"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          Add Template
        </button>
      </div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mt-6">
      <div
        v-for="classtemplates in filteredTemplates"
        :key="classtemplates.id"
        class="bg-white border border-gray-200 rounded-xl shadow-sm hover:shadow-lg transition-shadow duration-300 p-5 flex flex-col justify-between"
      >
        <div>
          <div class="flex justify-between items-start mb-3">
            <h2 class="text-lg font-bold text-gray-900">{{ classtemplates.name }}</h2>
            <div class="flex gap-2">
              <span
                class="px-2 py-1 text-xs font-medium rounded-full capitalize"
                :class="{
                  'bg-green-100 text-green-800': classtemplates.status === 'Active',
                  'bg-gray-100 text-gray-800': classtemplates.status === 'Inactive',
                }"
              >
                {{ classtemplates.status }}
              </span>
            </div>
          </div>

          <div class="mb-4">
            <span
              class="px-2 py-1 text-xs font-semibold rounded-full capitalize"
              :class="{
                'bg-blue-100 text-blue-800': classtemplates.difficultyLevel === 'Beginner',
                'bg-purple-100 text-purple-800': classtemplates.difficultyLevel === 'Intermediate',
                'bg-red-100 text-red-800': classtemplates.difficultyLevel === 'Advanced',
              }"
            >
              {{ classtemplates.difficultyLevel }}
            </span>
          </div>

          <p class="text-sm text-gray-600 line-clamp-3">
            {{ classtemplates.description || 'No description available.' }}
          </p>
        </div>

        <div class="mt-5 flex justify-end gap-2">
          <button
            @click="handleSelect(classtemplates)"
            class="px-3 py-1 rounded-md bg-red-600 text-white text-sm hover:bg-red-700"
          >
            Select
          </button>
          <button
            @click="handleEdit(classtemplates.id)"
            class="px-3 py-1 rounded-md bg-red-600 text-white text-sm hover:bg-red-700"
          >
            Edit
          </button>
          <button
            @click="handleDelete(classtemplates.id)"
            class="px-3 py-1 rounded-md bg-red-600 text-white text-sm hover:bg-red-700"
          >
            Delete
          </button>
        </div>
      </div>

      <div
        v-if="filteredTemplates.length === 0"
        class="col-span-full text-center text-gray-500 py-10 border-2 border-dashed border-gray-300 rounded-lg"
      >
        <p class="font-semibold">No class templates found</p>
        <p class="text-sm">Try adjusting your search or filters.</p>
      </div>
    </div>
  </div>
</template>