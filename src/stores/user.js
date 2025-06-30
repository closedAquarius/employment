// stores/user.js
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    username: '',
    token: '',
  }),
  actions: {
    setUserInfo(user) {
      this.userId = user.id
      this.username = user.name
      this.token = user.token
    },
    clearUserInfo() {
      this.userId = null
      this.username = ''
      this.token = ''
    }
  }
})