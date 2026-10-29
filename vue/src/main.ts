import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './assets/theme.css'
import './assets/erp.css'
import App from './App.vue'
import { router } from './router'
import { vReveal } from './composables/useReveal'

createApp(App).use(createPinia()).use(router).directive('reveal', vReveal).mount('#app')
