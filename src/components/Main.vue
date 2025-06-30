<template>
  <!-- 顶部导航栏 -->
  <div class="top-navbar">
    <div class="navbar-left">
      <img src="https://cdn.jsdelivr.net/gh/edent/SuperTinyIcons/images/svg/briefcase.svg" class="logo-icon" alt="logo" />
      <span class="project-title">职引未来</span>
    </div>
    <div class="navbar-right">
      <span v-if="!isLogin" class="login-btn" @click="goLogin">登录</span>
      <span v-else class="username">{{ username }}</span>
    </div>
  </div>

  <!-- 最新速递 -->
  <div>
    <div class="row-a1">
      <div class="wp">
        <div class="g-tit1">
          <h3>最新速递</h3>
        </div>
        <div class="box flex-box">
          <!-- 轮播图 -->
          <div class="slider" v-if="sliderItems.length">
            <a @click.prevent="goToDetail(sliderItems[currentIndex].id)" class="con" style="cursor:pointer">
              <div class="pic">
                <img :src="sliderItems[currentIndex].img" :alt="sliderItems[currentIndex].title" />
                <div class="slider-bar">
                  <div class="slider-title">{{ sliderItems[currentIndex].title }}</div>
                  <div class="slider-controls">
                    <button
                      v-for="(item, idx) in sliderItems"
                      :key="idx"
                      @click.stop="resetSlider(idx)"
                      :class="{ active: idx === currentIndex }"
                    ></button>
                  </div>
                </div>
              </div>
            </a>
            <div class="slider-date">{{ sliderItems[currentIndex].date }}</div>
          </div>
          <!-- 新闻列表 -->
          <ul class="p-list1 grid-list">
            <li v-for="news in newsList" :key="news.id">
              <a
                @click.prevent="goToDetail(news.id)"
                class="con"
                :title="news.title"
                style="cursor:pointer"
              >
                <h3>{{ news.title }}</h3>
                <span class="date">{{ news.date }}</span>
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>

  <!-- 就业信息模块 -->
  <div class="row-a1">
    <div class="wp">
      <div class="g-tit1 flex-between">
        <h3>就业信息</h3>
        <span class="more-link" @click="goToNewsList('就业')">更多 &gt;</span>
      </div>
      <div class="employment-grid">
        <div
          class="employment-item"
          v-for="item in employmentList"
          :key="item.id"
          @click="goToDetail(item.id)"
        >
          <div class="employment-img">
            <img :src="item.img" :alt="item.title" />
          </div>
          <div class="employment-info">
            <div class="employment-title">{{ item.title }}</div>
            <div class="employment-meta">
              <span class="employment-date">{{ item.date }}</span>
              <span class="employment-tag">{{ item.tags }}</span>
              <span class="employment-views">👁️ {{ item.views }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 校园活动模块 -->
  <div class="row-a1">
    <div class="wp">
      <div class="g-tit1 flex-between">
        <h3>校园活动</h3>
        <span class="more-link" @click="goToNewsList('校园')">更多 &gt;</span>
      </div>
      <div class="activity-grid">
        <div
          class="activity-item"
          v-for="item in activityList"
          :key="item.id"
          @click="goToDetail(item.id)"
        >
          <div class="activity-img">
            <img :src="item.img" :alt="item.title" />
          </div>
          <div class="activity-info">
            <div class="activity-title">{{ item.title }}</div>
            <div class="activity-meta">
              <span class="activity-date">{{ item.date }}</span>
              <span class="activity-tag">{{ item.tags }}</span>
              <span class="activity-views">👁️ {{ item.views }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 大家都在看模块 -->
  <div class="row-a1">
    <div class="wp">
      <div class="g-tit1">
        <h3>大家都在看</h3>
      </div>
      <div class="hot-grid">
        <div
          class="hot-item"
          v-for="item in hotList"
          :key="item.id"
          @click="goToDetail(item.id)"
        >
          <div class="hot-img">
            <img :src="item.img" :alt="item.title" />
          </div>
          <div class="hot-info">
            <div class="hot-title">{{ item.title }}</div>
            <div class="hot-meta">
              <span class="hot-date">{{ item.date }}</span>
              <span class="hot-tag">{{ item.tags }}</span>
              <span class="hot-views">👁️ {{ item.views }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utils/axios'

const router = useRouter()

// 登录状态模拟
const username = localStorage.getItem('username') || ''
const isLogin = !!username

function goLogin() {
  router.push('/login')
}

function goToNews() {
  router.push('/news')
}
function goToDetail(id) {
  router.push(`/news/${id}`)
}
function goToNewsList(tag) {
  router.push({ path: '/news', query: { tags: tag } })
}

const currentIndex = ref(0)
let timer = null
const sliderItems = ref([])
const newsList = ref([])
const employmentList = ref([])
const activityList = ref([]) // 新增活动列表
const hotList = ref([]) // 新增热门新闻列表

function startSlider() {
  timer = setInterval(() => {
    if (sliderItems.value.length > 0) {
      currentIndex.value = (currentIndex.value + 1) % sliderItems.value.length
    }
  }, 3000)
}
function resetSlider(idx) {
  currentIndex.value = idx
  clearInterval(timer)
  startSlider()
}

const BASE_URL = 'http://localhost:8080/' // 你的后端基地址

async function fetchNews() {
  try {
    const res = await axios.get('/news/latest', { params: { limit: 12 } })
    const news = res.data || []
    // 处理数据结构
    const mapped = news.map(item => ({
      id: item.newsId,
      title: item.title,
      img: item.imageUrls ? BASE_URL.replace(/\/$/, '') + '/' + item.imageUrls.replace(/^\//, '') : '',
      date: item.publishTime ? item.publishTime.split('T')[0] : '',
      link: `/news/${item.newsId}` // 你可以根据实际路由调整
    }))
    sliderItems.value = mapped.slice(0, 4)
    newsList.value = mapped.slice(4, 12)
  } catch (e) {
    console.error('获取新闻失败', e)
  }
}

async function fetchEmploymentNews() {
  try {
    const res = await axios.get('/news/list', {
      params: { page: 1, size: 4, tags: '就业' }
    })
    const news = res.data || []
    employmentList.value = news.map(item => ({
      id: item.newsId,
      title: item.title,
      img: item.imageUrls
        ? BASE_URL.replace(/\/$/, '') + '/' + item.imageUrls.replace(/^\//, '')
        : '',
      date: item.publishTime ? item.publishTime.split('T')[0] : '',
      tags: item.tags || '就业',
      views: item.readCount || 0
    }))
  } catch (e) {
    console.error('获取就业新闻失败', e)
  }
}

async function fetchActivityNews() {
  try {
    const res = await axios.get('/news/list', {
      params: { page: 1, size: 4, tags: '校园' }
    })
    const news = res.data || []
    activityList.value = news.map(item => ({
      id: item.newsId,
      title: item.title,
      img: item.imageUrls
        ? BASE_URL.replace(/\/$/, '') + '/' + item.imageUrls.replace(/^\//, '')
        : '',
      date: item.publishTime ? item.publishTime.split('T')[0] : '',
      tags: item.tags || '校园',
      views: item.readCount || 0
    }))
  } catch (e) {
    console.error('获取校园新闻失败', e)
  }
}

async function fetchHotNews() {
  try {
    const res = await axios.get('/news/hot', {
      params: { limit: 4 }
    })
    const news = res.data || []
    hotList.value = news.map(item => ({
      id: item.newsId,
      title: item.title,
      img: item.imageUrls
        ? BASE_URL.replace(/\/$/, '') + '/' + item.imageUrls.replace(/^\//, '')
        : '',
      date: item.publishTime ? item.publishTime.split('T')[0] : '',
      tags: item.tags || '',
      views: item.readCount || 0
    }))
  } catch (e) {
    console.error('获取热门新闻失败', e)
  }
}

onMounted(() => {
  fetchNews().then(() => startSlider())
  fetchEmploymentNews()
  fetchActivityNews()
  fetchHotNews() // 新增
})
onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
/* 顶部导航栏样式 */
.top-navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 56px;
  background: #fff;
  box-shadow: 0 2px 12px #0001;
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
  padding: 0 24px; /* 调小内边距 */
  box-sizing: border-box;
}

.navbar-left,
.navbar-right {
  min-width: 0;
}

.logo-icon {
  width: 32px;
  height: 32px;
  vertical-align: middle;
  object-fit: contain;
  background: #f5f5f5;
}

.project-title {
  font-size: 22px;
  font-weight: bold;
  color: #2a4d9b;
  letter-spacing: 2px;
}

.navbar-right {
  font-size: 17px;
  color: #2a4d9b;
  display: flex;
  align-items: center;
  gap: 16px;
}

.login-btn {
  cursor: pointer;
  color: #3b82f6;
  font-weight: 500;
  padding: 6px 18px;
  border-radius: 18px;
  border: 1px solid #3b82f6;
  transition: background 0.2s, color 0.2s;
}
.login-btn:hover {
  background: #3b82f6;
  color: #fff;
}

.username {
  font-weight: 600;
  color: #222;
  background: #f0f4fa;
  padding: 6px 18px;
  border-radius: 18px;
}

.row-a1 {
  width: 100%; /* 不要用100vw */
  margin-left: 0;
  box-sizing: border-box;
  background: none;
  padding: 0;
  margin-top: 72px; /* 给内容加顶部间距，避免被导航栏遮住 */
}

.wp {
  width: 100%;
  max-width: 1400px; /* 可根据需要调整 */
  margin: 0 auto;
  box-sizing: border-box;
  padding: 0 24px;
}

.box.flex-box {
  display: flex;
  gap: 24px;
  align-items: stretch;
  width: 100%;
  box-sizing: border-box;
}

.slider {
  flex: 1 1 0;
  min-width: 320px;
  max-width: 600px;
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.slider .pic {
  position: relative;
  width: 100%;
  height: 100%;
  flex: 1 1 0;
  display: block;
  /* 不能有margin-bottom等撑开高度的属性 */
}

.slider .pic img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 16px;
  display: block;
}

.slider-bar {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 56px;
  background: rgba(34, 60, 120, 0.85);
  border-radius: 0 0 16px 16px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-sizing: border-box;
  z-index: 2;
}

.slider-title {
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.slider-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 24px;
}

.slider-controls button {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: none;
  background: #fff;
  opacity: 0.5;
  cursor: pointer;
  outline: none;
  transition: opacity 0.2s;
}
.slider-controls button.active {
  opacity: 1;
  background: #ffd700;
}

.slider-date {
  text-align: left;
  color: #668;
  font-size: 16px;
  margin: 12px 0 0 0; /* 只留上间距 */
  padding-left: 8px;
}

.grid-list {
  flex: 1 1 0;
  min-width: 400px;   /* 原320px，调宽 */
  max-width: 700px;   /* 原600px，调宽 */
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(4, 1fr);
  gap: 12px;
  align-items: stretch;
  padding: 0;
  margin: 0;
  list-style: none;
}
.grid-list li {
  height: 120px; /* 原80px，调大 */
  background: #f7f7f7;
  border-radius: 10px;
  padding: 18px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.grid-list li a {
  color: #222;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: center;
}
.grid-list li h3 {
  font-size: 18px;    /* 原15px，调大 */
  margin: 0 0 8px 0;
  font-weight: 600;
  line-height: 1.3;
}
.grid-list li .date {
  font-size: 15px;    /* 原12px，调大 */
  color: #666;
}

/* 就业信息模块样式 */
.employment-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(2, 1fr);
  gap: 20px;
  width: 100%;
  margin-top: 12px;
}

.employment-item {
  display: flex;
  background: #f7f7f7;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 8px #0001;
  min-height: 110px;
}

.employment-item:hover {
  box-shadow: 0 4px 16px #0002;
}

.employment-img {
  width: 110px;
  height: 100%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e6eaf3;
}

.employment-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.employment-info {
  flex: 1;
  padding: 16px 18px 12px 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.employment-title {
  font-size: 17px;
  font-weight: 600;
  color: #223;
  margin-bottom: 10px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.employment-meta {
  font-size: 14px;
  color: #888;
  display: flex;
  gap: 18px;
  align-items: center;
}

.employment-tag {
  background: #e0eaff;
  color: #3b82f6;
  border-radius: 8px;
  padding: 2px 10px;
  font-size: 13px;
}

/* 校园活动模块样式 */
.activity-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(2, 1fr);
  gap: 20px;
  width: 100%;
  margin-top: 12px;
}

.activity-item {
  display: flex;
  background: #f7f7f7;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 8px #0001;
  min-height: 110px;
}

.activity-item:hover {
  box-shadow: 0 4px 16px #0002;
}

.activity-img {
  width: 110px;
  height: 100%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e6eaf3;
}

.activity-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.activity-info {
  flex: 1;
  padding: 16px 18px 12px 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.activity-title {
  font-size: 17px;
  font-weight: 600;
  color: #223;
  margin-bottom: 10px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-meta {
  font-size: 14px;
  color: #888;
  display: flex;
  gap: 18px;
  align-items: center;
}

.activity-tag {
  background: #ffe0e0;
  color: #f66;
  border-radius: 8px;
  padding: 2px 10px;
  font-size: 13px;
}

/* 热门新闻模块样式 */
.hot-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(2, 1fr);
  gap: 20px;
  width: 100%;
  margin-top: 12px;
}

.hot-item {
  display: flex;
  background: #f7f7f7;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
  box-shadow: 0 2px 8px #0001;
  min-height: 110px;
}

.hot-item:hover {
  box-shadow: 0 4px 16px #0002;
}

.hot-img {
  width: 110px;
  height: 100%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e6eaf3;
}

.hot-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.hot-info {
  flex: 1;
  padding: 16px 18px 12px 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.hot-title {
  font-size: 17px;
  font-weight: 600;
  color: #223;
  margin-bottom: 10px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-meta {
  font-size: 14px;
  color: #888;
  display: flex;
  gap: 18px;
  align-items: center;
}

.hot-tag {
  background: #fff7e0;
  color: #e6a23c;
  border-radius: 8px;
  padding: 2px 10px;
  font-size: 13px;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.more-link {
  color: #3b82f6;
  font-size: 15px;
  cursor: pointer;
  padding: 2px 10px;
  border-radius: 6px;
  transition: background 0.2s, color 0.2s;
  user-select: none;
}
.more-link:hover {
  background: #e0eaff;
  color: #174ea6;
}
</style>