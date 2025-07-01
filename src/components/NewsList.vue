<template>
  <div class="news-list-page">
    <h2 v-if="tag">“{{ tag }}”相关新闻</h2>
    <h2 v-else>全部新闻</h2>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-if="newsList.length === 0" class="empty">暂无新闻</div>
      <div class="news-list-vertical">
        <div class="news-list-item" v-for="item in newsList" :key="item.id" @click="viewDetail(item.id)">
          <img v-if="item.img" :src="item.img" :alt="item.title" class="news-img" />
          <div class="news-info">
            <div class="news-title">{{ item.title }}</div>
            <div class="news-meta">
              <span class="news-date">{{ item.date }}</span>
              <span class="news-tag">{{ item.tags }}</span>
              <span class="news-views">👁️ {{ item.views }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <FooterLinks />
</template>

<script setup>
import FooterLinks from './FooterLinks.vue'
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/utils/axios'

const router = useRouter()
const route = useRoute()
const newsList = ref([])
const loading = ref(false)
const tag = ref(route.query.tags || '')

async function fetchNewsList() {
  loading.value = true
  try {
    const res = await axios.get('/news/list', {
      params: {
        page: 1,
        size: 20,
        tags: tag.value || undefined
      }
    })
    const news = res.data || []
    newsList.value = news.map(item => ({
      id: item.newsId,
      title: item.title,
      img: item.imageUrls
        ? (import.meta.env.VITE_BASE_URL || 'http://localhost:8080/') + item.imageUrls.replace(/^\//, '')
        : '',
      date: item.publishTime ? item.publishTime.split('T')[0] : '',
      tags: item.tags || '',
      views: item.readCount || 0
    }))
  } finally {
    loading.value = false
  }
}

function viewDetail(id) {
  router.push(`/news/${id}`)
}

// 监听路由参数变化，自动刷新
watch(() => route.query.tags, (val) => {
  tag.value = val || ''
  fetchNewsList()
})

// 首次加载
onMounted(fetchNewsList)
</script>

<style scoped>
.news-list-page {
  max-width: 1600px; /* 更宽 */
  margin: 40px auto 0;
  padding: 0 40px;   /* 两侧间距适当加大 */
}
.news-list-page h2 {
  font-size: 22px;
  margin-bottom: 24px;
  color: #2a4d9b;
}
.news-list-vertical {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.news-list-item {
  display: flex;
  background: #f7f7f7;
  border-radius: 12px;
  overflow: hidden;
  min-height: 110px;
  min-width: 1000px;
  box-shadow: 0 2px 8px #0001;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.news-list-item:hover {
  box-shadow: 0 4px 16px #0002;
}
.news-img {
  width: 260px;      /* 图片更宽 */
  height: 180px;     /* 固定高度更大气 */
  object-fit: cover;
  background: #e6eaf3;
  flex-shrink: 0;
  border-radius: 16px 0 0 16px;
}
.news-info {
  flex: 1;
  padding: 32px 48px 24px 48px; /* 内容区更宽松 */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.news-title {
  font-size: 28px;
  font-weight: 700;
  color: #223;
  margin-bottom: 18px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.news-meta {
  font-size: 15px;
  color: #888;
  display: flex;
  gap: 24px;
  align-items: center;
}
.news-tag {
  background: #e0eaff;
  color: #3b82f6;
  border-radius: 8px;
  padding: 2px 10px;
  font-size: 13px;
}
.loading, .empty {
  text-align: center;
  color: #888;
  margin: 40px 0;
}
</style>