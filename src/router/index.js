import { createRouter, createWebHistory } from 'vue-router'
import Main from '@/components/Main.vue'
import NewsList from '@/components/NewsList.vue'
import NewsDetail from '@/components/NewsDetail.vue'

const routes = [
  {
    path: '/',
    name: 'Main',
    component: Main
  },
  {
    path: '/news',
    name: 'NewsList',
    component: NewsList
  },
  {
    path: '/news/:id',
    name: 'NewsDetail',
    component: NewsDetail,
    props: true // 将路由参数作为 props 传递
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router