<!-- filepath: d:\programstudy\test\employment-frontend\src\components\FooterLinks.vue -->
<template>
  <footer class="footer-links">
    <div class="footer-inner">
      <ul>
        <li><a href="https://dj.ncss.cn/" target="_blank">全国高校毕业生毕业去向登记系统</a></li>
        <li><a href="https://cy.ncss.cn/" target="_blank">全国大学生创业服务网</a></li>
        <li><a href="https://yz.chsi.com.cn/" target="_blank">中国研究生招生信息网</a></li>
        <li><a href="https://gj.ncss.cn/" target="_blank">国际组织实习任职信息平台</a></li>
        <li><a href="https://xj.ncss.cn/" target="_blank">新疆籍毕业生就业创业信息平台</a></li>
        <li><a href="https://zgs.chsi.com.cn/" target="_blank">全国大学生职业规划大赛</a></li>
        <li><a href="https://js.ncss.cn/" target="_blank">就业指导教师网</a></li>
        <li><a href="https://xz.chsi.com.cn/home.action" target="_blank">学职平台</a></li>
      </ul>
    </div>
  </footer>
</template>

<style scoped>
.footer-links {
  width: 100vw;
  min-width: 100vw;
  background: #f7f9fb;
  border-top: 1px solid #e3e7ed;
  margin-top: 48px;
  padding: 0;
  position: relative;
  left: 50%;
  right: 50%;
  margin-left: -50vw;
  margin-right: -50vw;
}

.footer-inner {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 16px;
  display: flex;
  justify-content: flex-end;
}

.footer-links ul {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(5, auto);
  gap: 12px 48px;
  list-style: none;
  padding: 0;
  margin: 0;
  width: 600px;
}

.footer-links li {
  font-size: 15px;
  color: #2a4d9b;
  text-align: left;
}

.footer-links a {
  color: #2a4d9b;
  text-decoration: none;
  transition: color 0.2s;
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
}

.footer-links a:hover {
  color: #fff;
  background: #3b82f6;
  text-decoration: underline;
}
</style>