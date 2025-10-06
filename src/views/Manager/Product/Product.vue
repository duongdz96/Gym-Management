<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { RouterLink } from 'vue-router'
import api from '@/services/api'

type Product = {
  id: number
  name: string
  type: string
  price: number
  brand?: string
  quantity?: number
}

const products = ref<Product[]>([])

const search = ref('')
const productType = ref('')

onMounted(async () => {
  try {
    const res = await api.get("/product");
    products.value = res.data
  } catch (error) {
    console.error('Failed to load products:', error)
  }
})


const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchesName = p.name.toLowerCase().includes(search.value.toLowerCase())
    const matchesType = productType.value ? p.type === productType.value : true
    return matchesName && matchesType
  })
})
</script>

<template>
  <div class="space-y-4 p-4">
    <div class="flex justify-between items-center">
      <h1 class="text-xl font-semibold">Product</h1>
      <div>
        <input
          v-model="search"
          type="text"
          placeholder="Search by product name"
          class="px-3 py-2 border rounded-lg mr-2"
        />
        <select v-model="productType" class="px-3 py-2 border rounded-lg mr-2">
          <option value="">All types</option>
          <option value="clothes">Clothes</option>
          <option value="powder">Powder</option>
          <option value="drinks">Drinks</option>
        </select>
        <RouterLink
          :to="{ name: 'product.add' }"
          class="px-3 py-2 bg-blue-600 text-white rounded-lg hover:opacity-90 mr-2"
        >
          Add
        </RouterLink>
        <RouterLink
          :to="{ name: 'product.importproduct' }"
          class="px-3 py-2 bg-blue-600 text-white rounded-lg hover:opacity-90"
        >
          Import From Provider
        </RouterLink>
      </div>
    </div>

    <div class="bg-white border border-gray-200 rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Brand</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Quantity</th>
            <th class="px-4 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 bg-white">
          <tr v-if="products.length === 0">
            <td colspan="7" class="px-4 py-3 text-center text-sm text-gray-500">No products found</td>
          </tr>
          <tr v-for="p in filteredProducts" :key="p.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.id }}</td>
            <td class="px-4 py-3 text-sm text-blue-600 hover:underline hover:cursor-pointer">{{ p.name }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.type }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.price.toFixed(0) }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.brand || '-' }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ p.quantity ?? '-' }}</td>
            <td class="px-4 py-3 text-sm text-center">
              <button class="px-2 py-1 rounded bg-green-600 text-white mr-2 hover:bg-green-700 hover:cursor-pointer">Edit</button>
              <button class="px-2 py-1 rounded bg-red-600 text-white hover:bg-red-700 hover:cursor-pointer">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
