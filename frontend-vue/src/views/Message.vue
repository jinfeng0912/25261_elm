<template>
  <div class="wrapper">
    <!-- header -->
    <header>
      <i class="fa fa-arrow-left" @click="goBack"></i>
      <p>消息中心</p>
      <div class="header-right">
        <i class="fa fa-shield-alt"></i>
        <i class="fa fa-eye"></i>
      </div>
    </header>

    <div class="content">
      <!-- 平台消息 -->
      <div class="section">
        <div class="section-header">
          <h3>平台消息</h3>
          <span class="view-all">查看全部 ></span>
        </div>
        
        <!-- 毕业季消息 -->
        <div class="message-item graduation-message">
          <div class="message-icon graduation-icon">
            <i class="fa fa-graduation-cap"></i>
          </div>
          <div class="message-content">
            <div class="message-header">
              <span class="badge">毕业季</span>
              <span class="title">2025毕业季来了</span>
              <span class="tag">毕业账单</span>
              <i class="fa fa-times close-icon"></i>
            </div>
            <p class="message-text">你的大学外卖账单出炉啦</p>
          </div>
        </div>

        <!-- 食无忧保障消息 -->
        <div class="message-item">
          <div class="message-icon food-icon">
            <i class="fa fa-cutlery"></i>
          </div>
          <div class="message-content">
            <div class="message-text">食无忧保障已生效</div>
            <div class="message-subtitle">点击查看保障详情>></div>
          </div>
          <div class="message-time">星期四</div>
        </div>

        <div class="message-item">
          <div class="message-icon food-icon">
            <i class="fa fa-cutlery"></i>
          </div>
          <div class="message-content">
            <div class="message-text">食无忧保障已生效</div>
            <div class="message-subtitle">点击查看保障详情>></div>
          </div>
          <div class="message-time">星期三</div>
        </div>
      </div>

      <!-- 聊天动态 -->
      <div class="section">
        <h3 class="section-title">聊天动态</h3>
        
        <div class="chat-item" v-for="item in chatList" :key="item.id" @click="openChat(item)">
          <div class="chat-avatar">
            <img :src="item.avatar" :alt="item.name">
          </div>
          <div class="chat-content">
            <div class="chat-header">
              <h4>{{ item.name }}</h4>
              <span class="chat-time">{{ item.time }}</span>
            </div>
            <div class="chat-message">
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

    <!-- 底部菜单 -->
    <Footer></Footer>
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
</script>

<style scoped>
.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* header */
.wrapper header {
  width: 100%;
  height: 12vw;
  background-color: #fff;
  color: #333;
  font-size: 4.8vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4vw;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.wrapper header .fa-arrow-left {
  font-size: 5vw;
  cursor: pointer;
  color: #666;
}

.wrapper header p {
  font-weight: bold;
  color: #333;
}

.header-right {
  display: flex;
  gap: 4vw;
}

.header-right i {
  font-size: 5vw;
  cursor: pointer;
  color: #666;
}

/* 内容区域 */
.content {
  margin-top: 12vw;
  margin-bottom: 14vw;
  padding: 0;
}

/* 区域样式 */
.section {
  background-color: #fff;
  margin-bottom: 2vw;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4vw;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h3 {
  font-size: 4vw;
  color: #333;
  margin: 0;
  font-weight: bold;
}

.view-all {
  color: #999;
  font-size: 3.2vw;
  cursor: pointer;
}

.section-title {
  font-size: 4vw;
  color: #333;
  padding: 4vw;
  margin: 0;
  border-bottom: 1px solid #f0f0f0;
  font-weight: bold;
}

/* 消息项目 */
.message-item {
  display: flex;
  padding: 4vw;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.message-item:hover {
  background-color: #f9f9f9;
}

.message-item:last-child {
  border-bottom: none;
}

.graduation-message {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-radius: 2vw;
  margin: 2vw 4vw;
  padding: 4vw;
  border-bottom: none;
}

.message-icon {
  width: 12vw;
  height: 12vw;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 3vw;
  color: white;
  font-size: 6vw;
  flex-shrink: 0;
}

.graduation-icon {
  background: linear-gradient(135deg, #4fc3f7, #29b6f6);
}

.food-icon {
  background-color: #ff9800;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 2vw;
  margin-bottom: 2vw;
  flex-wrap: wrap;
}

.badge {
  background-color: #2196F3;
  color: white;
  padding: 1vw 2vw;
  border-radius: 2vw;
  font-size: 2.8vw;
  font-weight: bold;
}

.title {
  font-size: 3.8vw;
  color: #333;
  font-weight: bold;
}

.tag {
  background-color: rgba(255, 255, 255, 0.9);
  color: #666;
  padding: 1vw 2vw;
  border-radius: 2vw;
  font-size: 2.6vw;
  border: 1px solid #ddd;
}

.close-icon {
  margin-left: auto;
  color: #999;
  cursor: pointer;
  font-size: 4vw;
}

.message-text {
  font-size: 3.4vw;
  color: #333;
  margin-bottom: 1vw;
  line-height: 1.4;
}

.message-subtitle {
  font-size: 3vw;
  color: #666;
  cursor: pointer;
}

.message-subtitle:hover {
  color: #0097FF;
}

.message-time {
  position: absolute;
  right: 4vw;
  top: 4vw;
  font-size: 2.8vw;
  color: #999;
}

/* 聊天项目 */
.chat-item {
  display: flex;
  padding: 3vw 4vw;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.chat-item:hover {
  background-color: #f9f9f9;
}

.chat-item:last-child {
  border-bottom: none;
}

.chat-avatar {
  position: relative;
  margin-right: 3vw;
  flex-shrink: 0;
}

.chat-avatar img {
  width: 12vw;
  height: 12vw;
  border-radius: 2vw;
  object-fit: cover;
}

.chat-content {
  flex: 1;
  min-width: 0; /* 防止文本溢出 */
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1vw;
}

.chat-header h4 {
  font-size: 3.6vw;
  color: #333;
  margin: 0;
  font-weight: normal;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.chat-time {
  font-size: 2.8vw;
  color: #999;
  flex-shrink: 0;
  margin-left: 2vw;
}

.chat-message {
  font-size: 3.2vw;
  color: #666;
  line-height: 1.5;
  margin-bottom: 2vw;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.merchant-tag {
  color: #0097FF;
  font-weight: bold;
}

.chat-tags {
  display: flex;
  gap: 2vw;
  flex-wrap: wrap;
}

.chat-tags .tag {
  background-color: #f0f0f0;
  color: #666;
  font-size: 2.6vw;
  padding: 1vw 2vw;
  border-radius: 2vw;
  border: none;
}

/* 响应式调整 */
@media (max-width: 480px) {
  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1vw;
  }
  
  .close-icon {
    position: absolute;
    top: 0;
    right: 0;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item, .chat-item {
  animation: fadeIn 0.3s ease-out;
}
</style>