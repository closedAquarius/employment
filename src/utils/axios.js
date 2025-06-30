// 新建 src/utils/axios.js
import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://localhost:8080', // 这里写你的后端基地址
  timeout: 5000
})

export default instance