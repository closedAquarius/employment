<template>
  <router-view />
</template>

<script setup>
import { useUserStore } from '@/stores/user'
// 监听登录页发来的消息
window.addEventListener("message", event => {
  // 安全校验，确保消息来自可信的登录页
  if (event.origin !== "http://localhost:3000") return;

  const { user } = event.data;
  if (user) {
    const userStore = useUserStore()
    console.log("收到登录用户信息:", user);

    // 保存 user 信息，比如 localStorage 或 pinia/vuex 状态管理
    localStorage.setItem("user", JSON.stringify(user));
    localStorage.setItem("token", user.accessToken);
    userStore.setUserInfo({
      id: user.id,
      username: user.nickname,  
      token: user.accessToken
    })

    // TODO: 可以触发登录状态更新，比如刷新用户信息，更新UI等
  }
});
</script>

<style>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
