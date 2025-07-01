<!-- filepath: d:\programstudy\test\employment-frontend\src\components\HomeSwiper.vue -->
<template>
  <div class="home-swiper-cover">
    <div class="arrow left" @click="handlePrev"><span>&lt;</span></div>
    <van-swipe
      ref="swipeRef"
      class="home-swiper1"
      :autoplay="autoPlayInterval"
      indicator-color="#3b82f6"
      lazy-render
      :show-indicators="false"
      @change="onChange"
      v-model="current"
    >
      <van-swipe-item v-for="(item, idx) in banners" :key="idx">
        <a :href="item.link" target="_blank">
          <img :src="item.img" :alt="'banner'+(idx+1)" />
        </a>
      </van-swipe-item>
    </van-swipe>
    <div class="arrow right" @click="handleNext"><span>&gt;</span></div>
    <!-- 自定义指示器 -->
    <div class="custom-indicators">
      <span
        v-for="(item, idx) in banners"
        :key="idx"
        :class="['indicator-dot', { active: idx === current }]"
      ></span>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const swipeRef = ref(null)
const banners = [
  {
    img: 'https://www.newjobs.com.cn/upload/1/cms/content/1749084380481.jpg',
    link: 'http://dwqds.newjobs.com.cn/'
  },
  {
    img: 'https://www.newjobs.com.cn/upload/1/cms/content/1749804274223.png',
    link: 'http://job.mohrss.gov.cn/25brqw/index.jhtml'
  },
  {
    img: 'https://www.newjobs.com.cn/upload/1/cms/content/1728873068736.jpg',
    link: 'https://www.ciiczhaopin.com/14thsdzp'
  },
  {
    img: 'https://www.newjobs.com.cn/upload/1/cms/content/1741229639433.jpg',
    link: 'http://dzcs.newjobs.com.cn/'
  }
]

const autoPlayInterval = ref(3000)
const current = ref(0)
let timer = null

function resetAutoplay() {
  autoPlayInterval.value = 0
  clearTimeout(timer)
  timer = setTimeout(() => {
    autoPlayInterval.value = 3000
  }, 100)
}

function handlePrev() {
  swipeRef.value?.prev()
  resetAutoplay()
}
function handleNext() {
  swipeRef.value?.next()
  resetAutoplay()
}
function onChange(idx) {
  current.value = idx
}
</script>

<style scoped>
.home-swiper-cover {
  width: 100vw;
  max-width: none;
  margin: 0 auto 36px auto;
  box-sizing: border-box;
  padding: 0 0;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.home-swiper1 {
  width: 100%;
  height: 440px;
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 4px 24px #0001;
}
.home-swiper1 img {
  width: 100%;
  height: 440px;
  object-fit: cover;
  display: block;
  border-radius: 32px;
  background: #f5f7fa;
}
.arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  width: 48px;
  height: 48px;
  background: rgba(59,130,246,0.15);
  color: #3b82f6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  user-select: none;
}
.arrow.left {
  left: 12px;
}
.arrow.right {
  right: 12px;
}
.arrow:hover {
  background: #3b82f6;
  color: #fff;
}

/* 自定义指示器 */
.custom-indicators {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 11;
  pointer-events: none;
}
.indicator-dot {
  display: inline-block;
  width: 18px;
  height: 12px;
  border-radius: 8px;
  background: #e0e0e0;
  margin: 0 8px;
  transition: all 0.3s;
  pointer-events: auto;
}
.indicator-dot.active {
  background: #00b4e9;
  width: 36px;
  height: 12px;
  border-radius: 8px;
}
@media (max-width: 900px) {
  .home-swiper1,
  .home-swiper1 img {
    height: 200px;
    border-radius: 12px;
  }
  .home-swiper-cover {
    max-width: 100vw;
    padding: 0 4px;
  }
  .arrow {
    width: 32px;
    height: 32px;
    font-size: 20px;
  }
  .custom-indicators {
    bottom: 8px;
  }
}
</style>