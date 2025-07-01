<!-- filepath: d:\programstudy\test\employment-frontend\src\components\PolicyTabs.vue -->
<template>
  <div class="info-part">
    <div class="info-title"><span></span> 政策法规</div>
    <div class="common-tab">
      <div
        v-for="tab in tabList"
        :key="tab"
        class="common-tab-item zc-tab-item"
        :class="{ 'curr-tab': zcActive === tab }"
        @click="handleTabClick(tab)"
      >
        {{ tab }}
      </div>
    </div>
    <!-- 只渲染当前激活tab的内容 -->
    <div v-if="policyMap[zcActive]">
      <div v-for="item in policyMap[zcActive]" :key="item.title">
        <a :href="baseUrl + item.link" target="_blank">
          <div class="info-list-title">{{ item.title }}</div>
        </a>
      </div>
      <a class="view-more" :href="baseUrl + moreMap[zcActive]" target="_blank">查看更多 &gt;</a>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const baseUrl = 'https://www.newjobs.com.cn'

const tabList = [
  '就业创业政策',
  '档案服务政策',
  '人才政策',
  '政策解读'
]

const zcActive = ref(tabList[0])

function handleTabClick(tab) {
  console.log('点击tab:', tab)
  zcActive.value = tab
  console.log('当前zcActive:', zcActive.value)
  console.log(policyMap[zcActive.value])
  console.log(zcActive.value)
}

const policyMap = {
  '就业创业政策': [
    { title: '【全国】人力资源社会保障部办公厅 财政部办公厅关于做好2025年高校毕业生“三支一扶”计划实施工作的通知（人社厅发〔2025〕21号）', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1929702676191293441.html' },
    { title: '【全国】人力资源社会保障部等5部门关于进一步健全就业公共服务体系的意见（人社部发〔2025〕21号）', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1927172248309903362.html' },
    { title: '【全国】人力资源社会保障部 教育部 财政部关于做好2025年高校毕业生等青年就业工作的通知 （人社部发〔2025〕20号）', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1916314907586981890.html' },
    { title: '【全国】国务院就业促进和劳动保护工作领导小组关于印发《加力重点领域、重点行业、城乡基层和中小微企业岗位挖潜扩容 支持重点群体就业创业实施方案》的通知（国就劳发〔2025〕2号）', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1905500171823816706.html' },
    { title: '【全国】人力资源社会保障部办公厅关于进一步加大金融助企稳岗扩岗力度的通知（人社厅函〔2025〕25号）', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1904333476946059266.html' },
    { title: '【全国】人力资源社会保障部等7部门关于健全创业支持体系提升创业质量的意见（人社部发〔2025〕5号）', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1894582403750461442.html' },
    { title: '【全国】人力资源社会保障部关于开展 2025年全国公共就业服务专项活动的通知（人社部函〔2025〕2号）', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1879071356042100738.html' },
    { title: '【全国】人力资源社会保障部 财政部 民政部 中国残联关于进一步做好就业援助工作的通知', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1869944197579030530.html' },
    { title: '【全国】人力资源社会保障部 国家发展改革委 工业和信息化部 商务部 全国工商联关于加强人力资源服务助力制造业高质量发展的意见（人社部发〔2024〕86号）', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1869944824904302593.html' },
    { title: '【全国】人力资源社会保障部办公厅关于开展2024年全国人力资源市场高校毕业生就业服务专项行动的通知', link: '/html/web/zhengcexinxi/jiuyechuangyezhengce/1861572281784881154.html' }
  ],
  '档案服务政策': [
    { title: '【全国】人力资源社会保障部办公厅 教育部办公厅 关于积极稳妥做好高校毕业生档案转递接收工作的通知（人社厅发〔2023〕20号）', link: '/html/web/zhengcexinxi/danganfuwuzhengce/1670592857931616258.html' },
    { title: '【全国】中共中央组织部 人力资源社会保障部等五部门关于印发《流动人员人事档案管理服务规定》的通知 （人社部发〔2021〕112号）', link: '/html/web/zhengcexinxi/danganfuwuzhengce/1574278131972358146.html' },
    { title: '【全国】中共中央组织部 人力资源社会保障部等五部门关于进一步加强流动人员人事档案管理服务工作的通知（人社部发〔2014〕90号）', link: '/html/web/zhengcexinxi/danganfuwuzhengce/1574277495969071106.html' },
    { title: '【全国】人力资源社会保障部办公厅关于简化优化流动人员人事档案管理服务的通知(人社厅发(〔2016〕75号)', link: '/html/web/zhengcexinxi/danganfuwuzhengce/1574277090837053442.html' },
    { title: '【全国】中华人民共和国档案法', link: '/html/web/zhengcexinxi/danganfuwuzhengce/1574278896459759618.html' },
    { title: '【全国】人力资源市场暂行条例（国务院令第700号）', link: '/html/web/zhengcexinxi/danganfuwuzhengce/1574278581752741889.html' }
  ],
  '人才政策': [
    { title: '【全国】人力资源社会保障部办公厅关于开展专业技术人才管理服务改革试点工作的通知（人社厅函〔2025〕76号）', link: '/html/web/zhengcexinxi/rencaizhengce/1934446759707176961.html' },
    { title: '【全国】人力资源社会保障部 财政部 国务院国资委关于加大国有企业技能人才薪酬分配激励的通知（人社部发〔2025〕28号）', link: '/html/web/zhengcexinxi/rencaizhengce/1929701426938818562.html' },
    { title: '【全国】人力资源社会保障部 财政部关于实施“技能照亮前程”培训行动的通知（人社部发〔2025〕10号）', link: '/html/web/zhengcexinxi/rencaizhengce/1905082997686738946.html' },
    { title: '【全国】人力资源社会保障部等8部门关于推动技能强企工作的指导意见（人社部发〔2025〕3号）', link: '/html/web/zhengcexinxi/rencaizhengce/1879688414467473410.html' },
    { title: '【全国】人力资源社会保障部办公厅关于进一步加强专家人才服务基层工作的通知（人社厅发〔2025〕2号）', link: '/html/web/zhengcexinxi/rencaizhengce/1879071698083397634.html' },
    { title: '【山西】关于加强新时代高技能人才队伍建设的行动计划', link: '/html/web/zhengcexinxi/rencaizhengce/1846007396724404226.html' },
    { title: '【湖南】关于做好高技能人才与专业技术人才职业发展贯通工作的通知（湘人社办发〔2024〕23号）', link: '/html/web/zhengcexinxi/rencaizhengce/1812726316101824513.html' },
    { title: '【全国】人力资源社会保障部 中共中央组织部 中央网信办 国家发展改革委 教育部 科技部 工业和信息化部 财政部 国家数据局关于印发《加快数字人才培育支撑数字经济发展行动方案（2024-2026年）》的通知 （人社部发〔2024〕37号）', link: '/html/web/zhengcexinxi/rencaizhengce/1780762134176698369.html' },
    { title: '【全国】人力资源社会保障部关于进一步规范职业技能培训管理工作的通知（人社部发〔2024〕5号）', link: '/html/web/zhengcexinxi/rencaizhengce/1765614506959015938.html' },
    { title: '【天津】天津市人才发展促进条例', link: '/html/web/zhengcexinxi/rencaizhengce/1764820451169767426.html' }
  ],
  '政策解读': [
    { title: '【全国】人力资源社会保障部职业能力建设司负责人就《关于实施“技能照亮前程” 培训行动的通知》答记者问', link: '/html/web/zhengcexinxi/zhengcejiedu/1905082333359316994.html' },
    { title: '【全国】人力资源社会保障部职业能力建设司负责人就《关于推动技能强企工作的指导意见》答记者问', link: '/html/web/zhengcexinxi/zhengcejiedu/1879687384610652161.html' },
    { title: '【全国】做好新时代流动党员管理工作的指导性文件——中央组织部负责人就《关于进一步加强和改进流动党员管理工作的意见》答记者问', link: '/html/web/zhengcexinxi/zhengcejiedu/1856146909035515906.html' },
    { title: '【北京】解读 问答 流程《关于做好北京地区毕业生 一次性求职补贴发放工作的通知》', link: '/html/web/zhengcexinxi/zhengcejiedu/1812671318894604289.html' },
    { title: '【贵州】《贵州省促进2024年高校毕业生等青年就业创业工作方案》政策解读', link: '/html/web/zhengcexinxi/zhengcejiedu/1774963178326822914.html' },
    { title: '【全国】人力资源社会保障部有关司局负责同志解读新就业形态劳动者权益保障指引指南', link: '/html/web/zhengcexinxi/zhengcejiedu/1761923570143956993.html' },
    { title: '【安徽】《安徽省创业培训管理办法》政策解读', link: '/html/web/zhengcexinxi/zhengcejiedu/1760128131425996802.html' },
    { title: '【全国】人社部调解仲裁管理司负责人解读《关于加强新就业形态劳动纠纷一站式调解工作的通知》', link: '/html/web/zhengcexinxi/zhengcejiedu/1752860163700789250.html' },
    { title: '【江苏】江苏省专精特新企业就业创业扬帆计划启动实施', link: '/html/web/zhengcexinxi/zhengcejiedu/1734378480777400321.html' },
    { title: '【北京】解读 问答《关于实施创业启航培训有关工作的通知》', link: '/html/web/zhengcexinxi/zhengcejiedu/1734378075469221890.html' }
  ]
}

const moreMap = {
  '就业创业政策': '/html/web/zhengcexinxi/jiuyechuangyezhengce/index.html',
  '档案服务政策': '/html/web/zhengcexinxi/danganfuwuzhengce/index.html',
  '人才政策': '/html/web/zhengcexinxi/rencaizhengce/index.html',
  '政策解读': '/html/web/zhengcexinxi/zhengcejiedu/index.html'
}

// 页面渲染时打印当前tab
console.log('初始zcActive:', zcActive.value)
</script>

<style scoped>
.info-part {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px #0001;
  padding: 32px 32px 24px 32px;
  margin: 32px auto;
  max-width: 1600px;
}
.info-title {
  font-size: 24px;
  font-weight: 600;
  color: #222;
  margin-bottom: 18px;
  display: flex;
  align-items: center;
}
.info-title span {
  display: inline-block;
  width: 8px;
  height: 24px;
  background: #0096c7;
  border-radius: 4px;
  margin-right: 12px;
}
.common-tab {
  display: flex;
  gap: 18px;
  margin-bottom: 18px;
}
.common-tab-item {
  padding: 10px 32px;
  font-size: 18px;
  background: #f5f7fa;
  border-radius: 8px;
  color: #222;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  font-weight: 500;
  border: none;
  outline: none;
}
.common-tab-item.curr-tab {
  background: #0096c7;
  color: #fff;
}
.info-list-title {
  font-size: 17px;
  color: #222;
  padding: 12px 0 12px 0;
  border-bottom: 1px solid #e3e7ed;
  transition: color 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.info-list-title:hover {
  color: #0096c7;
  text-decoration: underline;
}
.view-more {
  display: inline-block;
  margin-top: 18px;
  color: #0096c7;
  font-size: 16px;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}
.view-more:hover {
  color: #005f7f;
  text-decoration: underline;
}
</style>