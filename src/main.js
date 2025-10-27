import './assets/main.css'
import router from '@/router/index.js';

import Toast, { useToast } from "vue-toastification";
import "vue-toastification/dist/index.css";

import { createApp } from 'vue'
import { createPinia } from "pinia";
import App from './App.vue'

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);
app.use(Toast).mount('#app');
