<template>
  <div class="wrapper">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <i class="fas fa-arrow-left back-icon" @click="goBack"></i>
      <h1 class="header-title">消息中心</h1>
      <div class="header-actions">
        <i class="fas fa-shield-alt security-icon"></i>
        <i class="fas fa-eye view-icon"></i>
      </div>
    </header>

    <div class="content">
      <!-- 平台消息部分 -->
      <div class="message-section">
        <div class="section-header">
          <h2 class="section-title">平台消息</h2>
          <span class="view-all"> <i class="fas fa-angle-right"></i></span>
        </div>
        
        <!-- 红包消息卡片 -->
        <div class="message-card redpacket-card" @click="openRedPacket">
          <div class="card-icon redpacket-icon">
            <i class="fas fa-gift"></i>
          </div>
          <div class="card-content">
            <div class="card-text">亲~有一份惊喜在等待您</div>
            <div class="card-subtext">21元红包已经到账，快来打开看看吧，感谢您的支持！</div>
          </div>
          <div class="card-time">21:45</div>
          <div class="card-arrow">
            <i class="fas fa-chevron-right"></i>
          </div>
        </div>

        <!-- AI营养专家卡片 -->
        <router-link to="/myLLM" class="message-card ai-nutrition-card">
          <div class="card-icon nutrition-icon">
            <i class="fas fa-apple-alt"></i>
          </div>
          <div class="card-content">
            <div class="card-text">AI营养专家</div>
            <div class="card-subtext">点击查看详情 <i class="fas fa-angle-double-right"></i></div>
          </div>
        </router-link>

        <!-- 健康资讯卡片 -->
        <a href="https://mp.weixin.qq.com/s/AJjFJ0CEOGulejft4fQvOg" target="_blank" class="message-card health-info-card">
          <div class="card-icon health-icon">
            <i class="fas fa-user-md"></i>
          </div>
          <div class="card-content">
            <div class="card-text">国家卫健委喊你科学减肥！</div>
            <div class="card-subtext">点击查看两会健康指南 <i class="fas fa-external-link-alt"></i></div>
          </div>
          <div class="card-tag">最新政策</div>
        </a>

        <!-- 生活科普卡片 -->
        <a href="https://mp.weixin.qq.com/s/5IxsPEg-x0gz_JSUVcifyQ" target="_blank" class="message-card life-science-card">
          <div class="card-icon science-icon">
            <i class="fas fa-flask"></i>
          </div>
          <div class="card-content">
            <div class="card-text">隔夜水果是吃还是扔</div>
            <div class="card-subtext">不要让"节俭"损害身体！ <i class="fas fa-external-link-alt"></i></div>
          </div>
          <div class="card-tag">科学原理</div>
        </a>

        <!-- 健康常识卡片 -->
        <a href="https://mp.weixin.qq.com/s/QGBt70iUl9LuJ-UGw5AoZg" target="_blank" class="message-card health-tip-card">
          <div class="card-icon tip-icon">
            <i class="fas fa-heartbeat"></i>
          </div>
          <div class="card-content">
            <div class="card-text">久坐腰疼的原因是什么？</div>
            <div class="card-subtext">腰痛是因为肌肉紧张吗？ <i class="fas fa-external-link-alt"></i></div>
          </div>
          <div class="card-tag">生活常识</div>
        </a>
      </div>

      <!-- 聊天动态部分 -->
      <div class="chat-section">
        <h2 class="section-title">聊天动态</h2>
        
        <div class="chat-list">
          <div class="chat-item" v-for="item in chatList" :key="item.id" @click="openChat(item)">
            <div class="chat-avatar">
              <img :src="item.avatar" :alt="item.name" class="avatar-img">
              <span class="unread-badge" v-if="item.unread">{{ item.unread }}</span>
            </div>
            <div class="chat-details">
              <div class="chat-header">
                <h3 class="chat-name">{{ item.name }}</h3>
                <span class="chat-time">{{ item.time }}</span>
              </div>
              <div class="chat-preview">
                <span class="merchant-tag" v-if="item.isMerchant">商家：</span>
                {{ item.message }}
              </div>
              <div class="chat-tags" v-if="item.tags && item.tags.length > 0">
                <span class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <Footer class="app-footer"></Footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '../components/Footer.vue'

// 导入商家图片
import sj01 from '@/assets/sj01.png'
import sj02 from '@/assets/sj02.png'
import sj03 from '@/assets/sj03.png'
import sj04 from '@/assets/sj04.png'
import sj05 from '@/assets/sj05.png'
import sj06 from '@/assets/sj06.png'
import sj07 from '@/assets/sj07.png'

const router = useRouter()

// 聊天列表数据 - 使用项目中真实的商家
const chatList = ref([
  {
    id: 1,
    name: '万家饺子（软件园E18店）',
    message: '亲爱的顾客，我们新推出的纯肉鲜饺和玉米鲜肉饺子现在有优惠！🥟 热腾腾的饺子配上我们的特色炒菜，绝对让您满意！',
    time: '昨天',
    avatar: sj01,
    isMerchant: true,
    tags: []
  },
  {
    id: 2,
    name: '茶兮鲜果饮品（国际软件园店）',
    message: '商家：欢迎光临茶兮！新鲜芒果汁和珍珠奶茶等您来品尝～这个季节来一杯暖暖的饮品最棒了！',
    time: '昨天',
    avatar: sj07,
    isMerchant: true,
    tags: []
  },
  {
    id: 3,
    name: '小锅饭豆腐馆（全运店）',
    message: '今日特推：蛋黄焗豆花和麻婆豆腐！',
    time: '昨天',
    avatar: sj02,
    isMerchant: true,
    tags: ['商品精选推荐', '省心选购']
  },
  {
    id: 4,
    name: '麦当劳麦乐送（全运路店）',
    message: '巨无霸套餐限时优惠！薯条可乐一应俱全，快来下单吧！',
    time: '昨天',
    avatar: sj03,
    isMerchant: true,
    tags: ['商品精选推荐', '省心选购']
  },
  {
    id: 5,
    name: '申记串道（中海康城店）',
    message: '商家：烤串炸串大促销！新疆风味烤羊肉串、香嫩烤鸡翅，闭眼下单不踩雷！',
    time: '星期四',
    avatar: sj05,
    isMerchant: true,
    tags: []
  },
  {
    id: 6,
    name: '半亩良田排骨米饭',
    message: '糖醋排骨饭和红烧排骨饭今日特价，酸甜可口营养丰富！',
    time: '星期三',
    avatar: sj06,
    isMerchant: true,
    tags: []
  },
  {
    id: 7,
    name: '米村拌饭（浑南店）',
    message: '韩式拌饭和石锅拌饭新鲜上线！热乎乎的石锅拌饭等您来尝～',
    time: '星期二',
    avatar: sj04,
    isMerchant: true,
    tags: ['新品推荐']
  }
])

const goBack = () => {
  router.push('/index')
}

const openChat = (chatItem) => {
  console.log('打开聊天:', chatItem.name)
  // 跳转到聊天详情页面，传递聊天ID
  router.push({
    path: '/chatDetail',
    query: { chatId: chatItem.id }
  })
}

// 红包点击事件
const openRedPacket = () => {
  console.log('打开红包卡券页面')
  // 跳转到红包卡券页面
  router.push('/redpacket')
}
</script>

<style scoped>
/* 基础样式 */
.wrapper {
  font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
  min-height: 100vh;
  color: #333;
}

/* 顶部导航栏样式 */
.app-header {
  position: sticky;
  top: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background-color:#0097FF;/* 纯蓝色背景 */
  color: white;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  /* 可选：添加平滑过渡效果 */
}


.back-icon {
  font-size: 18px;
  cursor: pointer;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 15px;
}

.security-icon, .view-icon {
  font-size: 18px;
  cursor: pointer;
}

/* 内容区域样式 */
.content {
  padding: 15px;
  padding-bottom: 70px;
}

/* 消息卡片通用样式 */
.message-card {
  display: flex;
  align-items: center;
  background-color: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.message-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 18px;
  color: white;
}

.card-content {
  flex: 1;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 8px 0 0;
}

.card-text {
  font-size: 15px;
  font-weight: 500;
  margin: 0;
}

.card-subtext {
  font-size: 13px;
  color: #888;
  margin-top: 3px;
}

.card-time {
  font-size: 12px;
  color: #999;
  margin-left: 10px;
}

.card-arrow {
  margin-left: 10px;
  color: #ccc;
}

.card-tag {
  background-color: #f0f0f0;
  color: #666;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-left: 10px;
}

/* 特定卡片样式 */
.graduation-card {
  border-left: 4px solid #ff9800;
}

.graduation-icon {
  background-color: #ff9800;
}

.graduation-badge {
  background-color: #ffeb3b;
  color: #ff9800;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: bold;
}

.bill-tag {
  background-color: #e3f2fd;
  color: #2196f3;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.redpacket-card {
  border-left: 4px solid #e91e63;
}

.redpacket-icon {
  background-color: #e91e63;
}

.ai-nutrition-card {
  border-left: 4px solid #4caf50;
}

.nutrition-icon {
  background-color: #4caf50;
}

.health-info-card {
  border-left: 4px solid #3f51b5;
}

.health-icon {
  background-color: #3f51b5;
}

.life-science-card {
  border-left: 4px solid #009688;
}

.science-icon {
  background-color: #009688;
}

.health-tip-card {
  border-left: 4px solid #ff5722;
}

.tip-icon {
  background-color: #ff5722;
}

/* 聊天动态样式 */
.chat-section {
  margin-top: 25px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #555;
}

.chat-list {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chat-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.chat-item:last-child {
  border-bottom: none;
}

.chat-item:hover {
  background-color: #f9f9f9;
}

.chat-avatar {
  position: relative;
  margin-right: 15px;
}

.avatar-img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff5722;
  color: white;
  font-size: 12px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-details {
  flex: 1;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.chat-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.chat-time {
  font-size: 12px;
  color: #999;
}

.chat-preview {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.merchant-tag {
  color: #ff5722;
  font-weight: bold;
}

.chat-tags {
  margin-top: 5px;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.tag {
  background-color: #f0f0f0;
  color: #666;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
}

.view-all {
  font-size: 14px;
  color: #888;
  display: flex;
  align-items: center;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.close-btn {
  margin-left: auto;
  color: #ccc;
  cursor: pointer;
}

.close-btn:hover {
  color: #999;
}

/* 底部导航栏样式 */
.app-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;
}
</style>