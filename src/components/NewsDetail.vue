<template>
  <div>
    <div class="news-detail-page">
      <!-- 左侧新闻详情 -->
      <div class="news-detail-card" v-if="news">
        <h1>{{ news.title }}</h1>
        <div class="meta">
          <span>发布时间：{{ news.publishTime ? news.publishTime.replace('T', ' ').slice(0, 19) : '-' }}</span>
          <span>更新时间：{{ news.updateTime ? news.updateTime.replace('T', ' ').slice(0, 19) : '-' }}</span>
          <span>阅读量：{{ news.readCount ?? 0 }}</span>
          <span v-if="news.tags">标签：{{ news.tags }}</span>
        </div>
        <div class="img-wrap" v-if="news.imageUrls">
          <img
            :src="news.imageUrls.startsWith('http') ? news.imageUrls : 'http://localhost:8080/' + news.imageUrls"
            alt="新闻图片"
          />
        </div>
        <div class="content" v-html="news.content"></div>
        <div class="author">作者：{{ news.authorName ?? '未知' }}</div>
      </div>
      <div class="news-detail-card" v-else>
        新闻不存在或已被删除
      </div>

      <!-- 右侧评论区 -->
      <div class="comment-card">
        <h2>评论区</h2>
        <div v-if="comments.length === 0" class="no-comment">暂无评论</div>
        <ul v-else class="comment-list">
          <li v-for="c in comments" :key="c.commentId">
            <div class="comment-user">{{ c.userName ?? '匿名' }}</div>
            <div class="comment-content">{{ c.content }}</div>
            <div class="comment-time">{{ c.createTime ? c.createTime.replace('T', ' ').slice(0, 19) : '' }}</div>
          </li>
        </ul>
        <!-- 固定底部输入框 -->
        <div class="comment-input-bar">
          <input
            v-model="commentContent"
            type="text"
            placeholder="说点什么吧…"
            :disabled="!isLogin"
            @keyup.enter="handlePostComment"
          />
          <button @click="handlePostComment" :disabled="!isLogin || !commentContent.trim()">发表评论</button>
        </div>
        <div v-if="!isLogin" class="login-tip">请先登录后再发表评论</div>
      </div>
    </div>

    <!-- 相关新闻模块，放在最外层div的下方 -->
    <div class="related-news-section" v-if="relatedNews.length">
      <h2>相关新闻</h2>
      <ul class="related-news-list">
        <li v-for="item in relatedNews" :key="item.newsId" @click="goToDetail(item.newsId)">
          <div class="related-img-wrap" v-if="item.imageUrls">
            <img
              :src="item.imageUrls.startsWith('http') ? item.imageUrls : 'http://localhost:8080/' + item.imageUrls"
              alt="相关图片"
            />
          </div>
          <div class="related-info">
            <div class="related-title">{{ item.title }}</div>
            <div class="related-meta">
              <span>{{ item.publishTime ? item.publishTime.replace('T', ' ').slice(0, 19) : '-' }}</span>
              <span v-if="item.tags">标签：{{ item.tags }}</span>
              <span>阅读量：{{ item.readCount ?? 0 }}</span>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
  <FooterLinks />
</template>

<script setup>
import FooterLinks from './FooterLinks.vue'
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/utils/axios'

const route = useRoute()
const router = useRouter()
const news = ref(null)
const comments = ref([])
const commentContent = ref('')
const relatedNews = ref([])

const userStr = localStorage.getItem('user') || ''
const user = userStr ? JSON.parse(userStr) : null
const isLogin = !!user
const userId = user?.id || null
function goToDetail(id) {
  router.push(`/news/${id}`)
}

// 获取相关新闻
async function fetchRelatedNews() {
  // 取标题第一个字为关键词（你也可以自定义逻辑）
  const keyword = news.value?.title?.charAt(0) || ''
  if (!keyword) return
  try {
    const res = await axios.get('/news/search', {
      params: { keyword, page: 1, size: 5 }
    })
    // 过滤掉当前新闻自身
    relatedNews.value = (res.data || []).filter(item => item.newsId !== news.value.newsId)
  } catch (e) {
    relatedNews.value = []
  }
}

// 监听路由参数变化，自动刷新内容
watch(
  () => route.params.id,
  async (newId, oldId) => {
    if (newId !== oldId) {
      await loadAll()
    }
  }
)

// 封装加载逻辑
async function loadAll() {
  const id = route.params.id
  try {
    const res = await axios.get(`/news/${id}`)
    news.value = res.data
  } catch (e) {
    news.value = null
  }
  await fetchComments()
  await fetchRelatedNews()
}

// 初始加载
onMounted(loadAll)

async function fetchComments() {
  const id = route.params.id
  try {
    const cres = await axios.get(`/news/${id}/comments`)
    comments.value = cres.data || []
  } catch (e) {
    comments.value = []
  }
}

async function handlePostComment() {
  if (!isLogin || !userId) {
    alert('请先登录后再发表评论')
    return
  }
  if (!commentContent.value.trim()) {
    return
  }
  try {
    await axios.post(`/news/${route.params.id}/comment`, null, {
      params: {
        userId,
        content: commentContent.value.trim()
      }
    })
    commentContent.value = ''
    await fetchComments()
  } catch (e) {
    console.error('评论失败', e)
    alert('评论失败，请稍后再试')
  }
}
</script>

<style scoped>
.news-detail-page {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 40px;
  padding: 40px 0;
  background: #f7f8fa;
  min-height: 100vh;
}
.news-detail-card {
  flex: 2 1 0;
  max-width: 700px;
  min-width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 32px 32px 56px 32px;
  box-shadow: 0 2px 16px #0001;
  position: relative;
  margin-right: 0;
}
.news-detail-card h1 {
  font-size: 2rem;
  margin-bottom: 16px;
  font-weight: bold;
}
.meta {
  color: #888;
  font-size: 14px;
  margin-bottom: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
}
.img-wrap {
  text-align: center;
  margin-bottom: 24px;
}
.img-wrap img {
  width: 320px;
  height: 180px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 1px 6px #0002;
  display: block;
  margin: 0 auto;
}
.content {
  font-size: 1.1rem;
  line-height: 1.8;
  margin-bottom: 40px;
  min-height: 120px;
}
.author {
  position: absolute;
  right: 32px;
  bottom: 18px;
  color: #666;
  font-size: 15px;
  text-align: right;
}
.related-news-section {
  margin-top: 40px;
}
.related-news-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.related-news-list li {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  cursor: pointer;
  transition: background 0.2s;
  align-items: center;
}
.related-news-list li:hover {
  background: #f1f1f1;
}
.related-img-wrap {
  flex-shrink: 0;
}
.related-img-wrap img {
  width: 100px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  box-shadow: 0 1px 4px #0002;
}
.related-title {
  font-size: 1rem;
  font-weight: 500;
  margin-bottom: 4px;
  text-align: left; /* 标题靠左 */
  color: #222;
  cursor: pointer;
}
.related-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.related-meta {
  color: #888;
  font-size: 12px;
  display: flex;
  gap: 12px;
}
.comment-card {
  flex: 1 1 0;
  min-width: 320px;
  max-width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 24px 24px 72px 24px; /* 留出底部输入框空间 */
  box-shadow: 0 2px 16px #0001;
  margin-left: 0;
  position: relative;
}
.comment-card h2 {
  font-size: 1.2rem;
  margin-bottom: 18px;
  font-weight: bold;
}
.no-comment {
  color: #aaa;
  text-align: center;
  margin-top: 40px;
}
.comment-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.comment-list li {
  border-bottom: 1px solid #eee;
  padding: 12px 0;
}
.comment-user {
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}
.comment-content {
  margin-bottom: 4px;
  color: #444;
}
.comment-time {
  font-size: 12px;
  color: #aaa;
  text-align: right;
}
.comment-input-bar {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  display: flex;
  gap: 8px;
  padding: 12px 24px;
  background: #fafbfc;
  border-top: 1px solid #eee;
  box-sizing: border-box;
}
.comment-input-bar input {
  flex: 1;
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 15px;
}
.comment-input-bar button {
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  background: #3b82f6;
  color: #fff;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.2s;
}
.comment-input-bar button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.login-tip {
  color: #f56c6c;
  font-size: 13px;
  margin-top: 8px;
  text-align: center;
}
</style>
