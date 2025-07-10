<!-- filepath: d:\programstudy\test\employment-frontend\src\components\SearchNews.vue -->
<template>
  <div class="search-news">
    <input
      v-model="keyword"
      @input="onInput"
      @focus="onFocus"
      @blur="onBlur"
      type="text"
      placeholder="搜索新闻标题、正文、作者"
      class="search-input"
      autocomplete="off"
    />
    <ul v-if="showDropdown && results.length" class="dropdown">
      <li
        v-for="item in results"
        :key="item.newsId"
        @mousedown.prevent="goDetail(item.newsId)"
      >
        {{ item.title }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utils/axios'

const keyword = ref('')
const results = ref([])
const showDropdown = ref(false)
const router = useRouter()
let timer = null

const onInput = () => {
  clearTimeout(timer)
  if (!keyword.value.trim()) {
    results.value = []
    showDropdown.value = false
    return
  }
  timer = setTimeout(async () => {
    const res = await axios.get('/news/search', {
      params: { keyword: keyword.value, page: 1, size: 8 }
    })
    results.value = res.data || []
    showDropdown.value = true
  }, 300)
}

const onFocus = () => {
  if (results.value.length) showDropdown.value = true
}
const onBlur = () => {
  setTimeout(() => { showDropdown.value = false }, 150)
}

const goDetail = (id) => {
  router.push(`/news/${id}`)
  showDropdown.value = false
}
</script>

<style scoped>
.search-news {
  margin: 50px auto 50px auto !important;
  position: relative;
  width: 70vw;
  margin: 0 auto;
}

.search-input {
  width: 100%;
  padding: 15px 25px;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  outline: none;
  transition: border 0.2s;
  background: #F2F2F2;
}

.search-input:focus {
  border-color: #174ea6;
}

.dropdown {
  position: absolute;
  width: 73vw !important;
  left: 0;
  right: 0;
  top: 50px;
  background: #fff;
  border: 1px solid #e3e7ed;
  border-radius: 15px;
  box-shadow: 0 4px 16px #0001;
  z-index: 10;
  max-height: 320px;
  overflow-y: auto;
  padding: 0;
  margin: 0;
  list-style: none;
}

.dropdown::-webkit-scrollbar {
  width: 8px;
}

.dropdown::-webkit-scrollbar-track {
  background: white;
  border-radius: 2px;
}

.dropdown::-webkit-scrollbar-thumb {
  background:  rgb(239, 239, 239);
  border-radius: 10px;
}

.dropdown::-webkit-scrollbar-thumb:hover {
  background:  rgb(179, 179, 179);
}

.dropdown li {
  padding: 12px 18px;
  cursor: pointer;
  font-size: 15px;
  color: #223;
  transition: background 0.2s, color 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown li:hover {
  background: #e0eaff;
  color: #174ea6;
}
</style>