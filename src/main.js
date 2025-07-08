import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import Vant from 'vant'
import 'vant/lib/index.css'
import { createPinia } from 'pinia'


const pinia = createPinia()

const app = createApp(App)
app.use(pinia)   // ⚠️ 这行非常重要，要先注册 pinia
app.use(router)
app.use(Vant)
app.mount('#app')
