# employment-frontend

## 项目简介

本项目为“职引未来”前端系统，基于 Vue3 + Vite + Vant 实现，包含首页轮播图、政策法规Tab切换、新闻搜索等模块，界面简洁美观，适配PC端。

## 主要功能

- 首页顶部导航栏与Logo
- 大幅轮播图（支持自动与手动切换）
- 搜索新闻功能
- 政策法规Tab切换（四大类政策，内容切换展示）
- 最新速递、底部链接等模块

## 技术栈

- [Vue 3](https://vuejs.org/)
- [Vite](https://vitejs.dev/)
- [Vant](https://vant-ui.github.io/vant/#/zh-CN/)
- [JavaScript](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript)
- [CSS3](https://developer.mozilla.org/zh-CN/docs/Web/CSS)

## 项目结构

```
employment-frontend/
├── src/
│   ├── components/
│   │   ├── HomeSwiper.vue      # 首页轮播图组件
│   │   ├── PolicyTabs.vue      # 政策法规Tab切换组件
│   │   └── ...                 # 其他组件
│   ├── App.vue
│   └── main.js
├── public/
├── package.json
└── README.md
```

## 本地开发

1. 安装依赖

   ```bash
   npm install
   ```

2. 启动开发服务器

   ```bash
   npm run dev
   ```

3. 访问 [http://localhost:5173](http://localhost:5173) 查看效果

## 构建发布

```bash
npm run build
```

## 其他说明

- 轮播图和政策Tab内容均可在 `src/components/HomeSwiper.vue` 和 `PolicyTabs.vue` 中自定义维护。
- 如需对接后端API，请在对应组件内调整数据获取逻辑。

---

如有问题请联系前端开发者或提交 issue。
