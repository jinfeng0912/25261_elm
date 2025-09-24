<template>
  <div class="wrapper">
    <!-- header -->
    <header>
      <i class="fa fa-arrow-left" @click="goBack"></i>
      <div class="header-info">
        <h3>{{ chatData.name }}</h3>
        <p>粉丝群{{ chatData.fans }}人</p>
      </div>
      <div class="header-actions">
        <i class="fa fa-comments"></i>
        <i class="fa fa-ellipsis-h"></i>
      </div>
    </header>

    <div class="content">
      <!-- 聊天消息区域 -->
      <div class="chat-messages">
        <div class="message-item" v-for="message in chatData.messages" :key="message.id">
          <div class="message-avatar">
            <img :src="chatData.avatar" :alt="chatData.name">
          </div>
          <div class="message-content">
            <div class="message-header">
              <span class="sender-name">{{ chatData.name }}</span>
              <span class="message-time">{{ message.time }}</span>
            </div>
            <div class="message-text">{{ message.content }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="chat-input">
      <div class="input-actions">
        <i class="fa fa-smile-o"></i>
      </div>
      <input type="text" placeholder="请输入内容..." v-model="inputMessage" @keyup.enter="sendMessage">
      <div class="input-actions">
        <i class="fa fa-paper-plane" @click="sendMessage" :class="{ active: inputMessage.trim() }"></i>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

// 导入商家图片
import sj01 from '@/assets/sj01.png'
import sj02 from '@/assets/sj02.png'
import sj03 from '@/assets/sj03.png'
import sj04 from '@/assets/sj04.png'
import sj05 from '@/assets/sj05.png'
import sj06 from '@/assets/sj06.png'
import sj07 from '@/assets/sj07.png'

const router = useRouter()
const route = useRoute()

const inputMessage = ref('')
const chatData = ref({})

// 商家聊天数据 - 简化版，只包含消息
const businessChatData = {
  1: {
    id: 1,
    name: '万家饺子（软件园E18店）',
    fans: 479,
    avatar: sj01,
    messages: [
      {
        id: 1,
        content: '亲爱的顾客，我们新推出的纯肉鲜饺和玉米鲜肉饺子现在有优惠！🥟',
        time: '昨天 19:00'
      },
      {
        id: 2,
        content: '热腾腾的饺子配上我们的特色炒菜，绝对让您满意！',
        time: '昨天 19:01'
      },
      {
        id: 3,
        content: '欢迎随时咨询我们的菜品信息哦～',
        time: '昨天 19:02'
      }
    ]
  },
  2: {
    id: 2,
    name: '茶兮鲜果饮品（国际软件园店）',
    fans: 328,
    avatar: sj07,
    messages: [
      {
        id: 1,
        content: '欢迎光临茶兮鲜果饮品！🍹',
        time: '昨天 18:30'
      },
      {
        id: 2,
        content: '新鲜芒果汁和经典珍珠奶茶等您来品尝～',
        time: '昨天 18:31'
      },
      {
        id: 3,
        content: '这个季节来一杯暖暖的饮品最棒了！我们所有饮品都是现做现卖，保证新鲜美味！',
        time: '昨天 18:32'
      }
    ]
  },
  3: {
    id: 3,
    name: '小锅饭豆腐馆（全运店）',
    fans: 256,
    avatar: sj02,
    messages: [
      {
        id: 1,
        content: '今日特推：蛋黄焗豆花和麻婆豆腐！🍛',
        time: '昨天 17:45'
      },
      {
        id: 2,
        content: '我们的豆腐都是当天现做，口感嫩滑，营养丰富。',
        time: '昨天 17:46'
      },
      {
        id: 3,
        content: '蛋黄焗豆花是我们的招牌，一定要试试！',
        time: '昨天 17:47'
      }
    ]
  },
  4: {
    id: 4,
    name: '麦当劳麦乐送（全运路店）',
    fans: 1205,
    avatar: sj03,
    messages: [
      {
        id: 1,
        content: '巨无霸套餐限时优惠！🍔',
        time: '昨天 16:20'
      },
      {
        id: 2,
        content: '薯条可乐一应俱全，金黄香脆的薯条配上经典巨无霸，再来一杯冰爽可乐，完美组合！',
        time: '昨天 16:21'
      },
      {
        id: 3,
        content: '快来下单吧！我们的汉堡新鲜制作，绝对美味！',
        time: '昨天 16:22'
      }
    ]
  },
  5: {
    id: 5,
    name: '申记串道（中海康城店）',
    fans: 367,
    avatar: sj05,
    messages: [
      {
        id: 1,
        content: '烤串炸串大促销！🔥',
        time: '星期四 20:15'
      },
      {
        id: 2,
        content: '新疆风味烤羊肉串、香嫩烤鸡翅，还有蒜蓉烤茄子，闭眼下单不踩雷！',
        time: '星期四 20:16'
      },
      {
        id: 3,
        content: '我们的烤串都是现烤现卖，香味扑鼻！欢迎品尝～',
        time: '星期四 20:17'
      }
    ]
  },
  6: {
    id: 6,
    name: '半亩良田排骨米饭',
    fans: 298,
    avatar: sj06,
    messages: [
      {
        id: 1,
        content: '糖醋排骨饭和红烧排骨饭今日特价！🍖',
        time: '星期三 19:30'
      },
      {
        id: 2,
        content: '酸甜可口营养丰富的糖醋排骨，浓郁香味的红烧排骨！',
        time: '星期三 19:31'
      },
      {
        id: 3,
        content: '配上软糯香甜的米饭，绝对满足您的味蕾！',
        time: '星期三 19:32'
      }
    ]
  },
  7: {
    id: 7,
    name: '米村拌饭（浑南店）',
    fans: 189,
    avatar: sj04,
    messages: [
      {
        id: 1,
        content: '韩式拌饭和石锅拌饭新鲜上线！🍚',
        time: '星期二 18:00'
      },
      {
        id: 2,
        content: '热乎乎的石锅拌饭等您来尝～正宗韩式口味！',
        time: '星期二 18:01'
      },
      {
        id: 3,
        content: '各种蔬菜搭配，营养均衡又美味！欢迎下单品尝！',
        time: '星期二 18:02'
      }
    ]
  }
}

onMounted(() => {
  const chatId = parseInt(route.query.chatId)
  if (chatId && businessChatData[chatId]) {
    chatData.value = businessChatData[chatId]
  } else {
    // 默认显示第一个商家
    chatData.value = businessChatData[1]
  }
})

const goBack = () => {
  router.go(-1)
}

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    // 添加用户消息到聊天记录
    const userMessage = {
      id: chatData.value.messages.length + 1,
      content: inputMessage.value.trim(),
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      isUser: true
    }
    chatData.value.messages.push(userMessage)
    
    console.log('发送消息:', inputMessage.value)
    inputMessage.value = ''
    
    // 模拟商家自动回复
    setTimeout(() => {
      const autoReply = {
        id: chatData.value.messages.length + 1,
        content: '感谢您的咨询！我们会尽快为您处理～',
        time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        isUser: false
      }
      chatData.value.messages.push(autoReply)
    }, 1000)
  }
}
</script>

<style scoped>
.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
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

.header-info {
  flex: 1;
  text-align: center;
}

.header-info h3 {
  font-size: 4vw;
  color: #333;
  margin: 0;
  font-weight: bold;
}

.header-info p {
  font-size: 2.8vw;
  color: #999;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 4vw;
}

.header-actions i {
  font-size: 5vw;
  cursor: pointer;
  color: #666;
}

/* 内容区域 */
.content {
  margin-top: 12vw;
  margin-bottom: 14vw;
  flex: 1;
  overflow-y: auto;
}

/* 聊天消息区域 */
.chat-messages {
  background-color: #fff;
  padding: 4vw;
  min-height: calc(100vh - 26vw);
}

.message-item {
  display: flex;
  margin-bottom: 4vw;
  animation: fadeInUp 0.3s ease-out;
}

/* 用户消息靠右显示 */
.message-item.user-message {
  flex-direction: row-reverse;
}

.message-item.user-message .message-content {
  background-color: #0097FF;
  color: white;
  border-radius: 4vw 1vw 4vw 4vw;
  margin-left: 3vw;
  margin-right: 0;
}

.message-item.user-message .message-avatar {
  display: none;
}

.message-avatar img {
  width: 10vw;
  height: 10vw;
  border-radius: 2vw;
  object-fit: cover;
  margin-right: 3vw;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  background-color: #f0f0f0;
  padding: 3vw;
  border-radius: 1vw 4vw 4vw 4vw;
  max-width: 70%;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1vw;
}

.sender-name {
  font-size: 3.2vw;
  color: #666;
  font-weight: bold;
}

.message-time {
  font-size: 2.8vw;
  color: #999;
}

.message-text {
  font-size: 3.4vw;
  color: #333;
  line-height: 1.5;
  word-wrap: break-word;
}

/* 用户消息的文字颜色 */
.message-item.user-message .message-text {
  color: white;
}

.message-item.user-message .sender-name,
.message-item.user-message .message-time {
  color: rgba(255, 255, 255, 0.8);
}

/* 底部输入区域 */
.chat-input {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #fff;
  padding: 3vw 4vw;
  display: flex;
  align-items: center;
  gap: 3vw;
  border-top: 1px solid #eee;
  box-sizing: border-box;
}

.input-actions i {
  font-size: 5vw;
  color: #666;
  cursor: pointer;
  transition: color 0.2s ease;
}

.input-actions i:hover {
  color: #0097FF;
}

.input-actions i.active {
  color: #0097FF;
}

.chat-input input {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 5vw;
  padding: 2.5vw 4vw;
  font-size: 3.4vw;
  outline: none;
  background-color: #f9f9f9;
}

.chat-input input:focus {
  border-color: #0097FF;
  background-color: #fff;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条样式 */
.content::-webkit-scrollbar {
  width: 1vw;
}

.content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.content::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 1vw;
}

.content::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}
</style>