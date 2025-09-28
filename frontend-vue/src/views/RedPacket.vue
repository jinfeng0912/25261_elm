<template>
  <div class="wrapper">
    <!-- header -->
    <header>
      <i class="fa fa-arrow-left" @click="goBack"></i>
      <p>红包卡券</p>
      <div class="header-right">
        <i class="fa fa-shield-alt"></i>
        <i class="fa fa-eye"></i>
      </div>
    </header>

    <div class="content">
      <!-- 标签页 -->
      <div class="tabs">
        <div class="tab active" @click="switchTab('redpacket')">
          <span>红包</span>
          <span class="count">13</span>
        </div>
        <div class="tab" @click="switchTab('coupon')">
          <span>已购券</span>
          <span class="count">3</span>
        </div>
      </div>


      <!-- 红包列表 -->
      <div class="redpacket-list" v-if="currentTab === 'redpacket'">
        <div class="redpacket-item" v-for="item in redpacketList" :key="item.id">
          <div class="redpacket-left">
            <div class="redpacket-icon">
              <span class="icon-text">爆</span>
            </div>
            <div class="redpacket-info">
              <h4>{{ item.title }}</h4>
              <p class="expire-time">{{ item.expireTime }}</p>
            </div>
          </div>
          <div class="redpacket-right">
            <div class="expire-tag" v-if="item.isExpiring">将到期</div>
            <div class="amount">¥{{ item.amount }}</div>
            <div class="condition">满{{ item.condition }}可用</div>
            <div class="detail-rule" @click="toggleDetail(item.id)">
              <span>{{ item.detailRule }}</span>
              <i class="fa fa-chevron-down" :class="{ 'rotated': item.showDetail }"></i>
            </div>
            <button class="use-btn" @click="useRedPacket(item)">去使用</button>
          </div>
        </div>
      </div>

      <!-- 优惠券列表 -->
      <div class="coupon-list" v-if="currentTab === 'coupon'">
        <div class="coupon-item" v-for="item in couponList" :key="item.id">
          <div class="coupon-left">
            <div class="coupon-icon">
              <span class="icon-text">囍</span>
            </div>
            <div class="coupon-info">
              <h4>{{ item.title }}</h4>
              <p class="expire-time">{{ item.expireTime }}</p>
            </div>
          </div>
          <div class="coupon-right">
            <div class="amount">¥{{ item.amount }}</div>
            <div class="condition">满{{ item.condition }}可用</div>
            <button class="use-btn" @click="useCoupon(item)">去使用</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部菜单 -->
    <div class="bottom-menu">
      <div class="menu-item">
        <span>历史红包</span>
      </div>
      <div class="menu-divider">|</div>
      <div class="menu-item">
        <span>兑换码</span>
      </div>
      <div class="menu-divider">|</div>
      <div class="menu-item">
        <span>天天爆红包</span>
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

const router = useRouter()

// 当前标签页
const currentTab = ref('redpacket')

// 红包列表数据
const redpacketList = ref([
  {
    id: 1,
    title: '惊喜爆红包',
    amount: 21,
    condition: 60,
    expireTime: '今日 23:59 到期',
    isExpiring: true,
    detailRule: '满60元可用 (使用门槛=商品现价+打包费-所...)',
    showDetail: false
  },
  {
    id: 2,
    title: '惊喜爆红包',
    amount: 17,
    condition: 48,
    expireTime: '今日 23:59 到期',
    isExpiring: true,
    detailRule: '满48元可用 (使用门槛=商品现价+打包费-所...)',
    showDetail: false
  },
  {
    id: 3,
    title: '惊喜爆红包',
    amount: 19,
    condition: 55,
    expireTime: '今日 23:59 到期',
    isExpiring: true,
    detailRule: '满55元可用 (使用门槛=商品现价+打包费-所...)',
    showDetail: false
  },
  {
    id: 4,
    title: '爆红包',
    amount: 6,
    condition: 20,
    expireTime: '今日 23:59 到期',
    isExpiring: false,
    detailRule: '满20元可用 (使用门槛=商品现价+打包费-所...)',
    showDetail: false
  },
  {
    id: 5,
    title: '爆红包',
    amount: 7,
    condition: 25,
    expireTime: '今日 23:59 到期',
    isExpiring: false,
    detailRule: '满25元可用 (使用门槛=商品现价+打包费-所...)',
    showDetail: false
  }
])

// 优惠券列表数据
const couponList = ref([
  {
    id: 1,
    title: '满减券',
    amount: 10,
    condition: 50,
    expireTime: '3天后到期'
  },
  {
    id: 2,
    title: '新用户券',
    amount: 15,
    condition: 30,
    expireTime: '7天后到期'
  },
  {
    id: 3,
    title: '会员专享券',
    amount: 20,
    condition: 80,
    expireTime: '15天后到期'
  }
])

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 切换标签页
const switchTab = (tab) => {
  currentTab.value = tab
}

// 切换详情显示
const toggleDetail = (id) => {
  const item = redpacketList.value.find(item => item.id === id)
  if (item) {
    item.showDetail = !item.showDetail
  }
}

// 使用红包
const useRedPacket = (item) => {
  console.log('使用红包:', item)
  
  // 将红包信息保存到localStorage，供其他页面使用
  const redpacketInfo = {
    id: item.id,
    title: item.title,
    amount: item.amount,
    condition: item.condition,
    type: 'redpacket',
    used: true,
    useTime: new Date().toISOString()
  }
  
  localStorage.setItem('selectedRedPacket', JSON.stringify(redpacketInfo))
  
  alert(`已选择红包 ¥${item.amount}，满${item.condition}元可用`)
  // 跳转到商家列表页面
  router.push('/businessList')
}

// 使用优惠券
const useCoupon = (item) => {
  console.log('使用优惠券:', item)
  alert(`使用优惠券 ¥${item.amount}，满${item.condition}元可用`)
  // 这里可以跳转到商家列表或购物车页面
  router.push('/businessList')
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

/* 标签页 */
.tabs {
  display: flex;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-bottom: 1px solid #e0e0e0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4vw;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
  font-weight: 500;
}

.tab:hover {
  background: rgba(255, 71, 87, 0.05);
}

.tab.active {
  color: #ff4757;
  font-weight: 600;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #ff4757, #ff6b7a);
  border-radius: 2px;
  box-shadow: 0 2px 4px rgba(255, 71, 87, 0.3);
}


/* 筛选栏 */
.filter-bar {
  display: flex;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  padding: 3vw 4vw;
  gap: 3vw;
  overflow-x: auto;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 1vw;
  padding: 2.5vw 4vw;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 3vw;
  cursor: pointer;
  font-size: 3.2vw;
  color: #666;
  flex-shrink: 0;
  transition: all 0.3s ease;
  border: 1px solid rgba(0,0,0,0.05);
  font-weight: 500;
}

.filter-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.filter-item.active {
  background: linear-gradient(135deg, #ff4757, #ff6b7a);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 71, 87, 0.3);
  transform: translateY(-1px);
}

.filter-item i {
  font-size: 2.8vw;
}

/* 红包列表 */
.redpacket-list, .coupon-list {
  padding: 2vw;
}

.redpacket-item, .coupon-item {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 3vw;
  padding: 4vw;
  margin-bottom: 3vw;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border: 1px solid rgba(0,0,0,0.05);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.redpacket-item::before, .coupon-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #ff4757, #ff6b7a, #ff4757);
  background-size: 200% 100%;
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.redpacket-item:hover, .coupon-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.redpacket-left, .coupon-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.redpacket-icon, .coupon-icon {
  width: 14vw;
  height: 14vw;
  border-radius: 3vw;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 4vw;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  position: relative;
  overflow: hidden;
}

.redpacket-icon {
  background: linear-gradient(135deg, #ff4757, #ff6b7a, #ff8a80);
  background-size: 200% 200%;
  animation: gradientShift 3s ease infinite;
}

.coupon-icon {
  background: linear-gradient(135deg, #ff4757, #ff6b7a, #ff8a80);
  background-size: 200% 200%;
  animation: gradientShift 3s ease infinite;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.icon-text {
  color: white;
  font-size: 4vw;
  font-weight: bold;
}

.coupon-icon i {
  color: white;
  font-size: 6vw;
}

.redpacket-info, .coupon-info h4 {
  font-size: 3.6vw;
  color: #333;
  margin: 0 0 1vw 0;
  font-weight: normal;
}

.expire-time {
  font-size: 2.8vw;
  color: #ff4757;
  margin: 0;
}

.redpacket-right, .coupon-right {
  text-align: right;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 1vw;
}

.expire-tag {
  background-color: #ffebee;
  color: #ff4757;
  font-size: 2.4vw;
  padding: 1vw 2vw;
  border-radius: 2vw;
  border: 1px solid #ffcdd2;
}

.amount {
  font-size: 7vw;
  color: #ff4757;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(255, 71, 87, 0.3);
  background: linear-gradient(135deg, #ff4757, #ff6b7a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.condition {
  font-size: 2.8vw;
  color: #666;
}

.detail-rule {
  display: flex;
  align-items: center;
  gap: 1vw;
  font-size: 2.6vw;
  color: #999;
  cursor: pointer;
}

.detail-rule i {
  font-size: 2.4vw;
  transition: transform 0.3s ease;
}

.detail-rule i.rotated {
  transform: rotate(180deg);
}

.use-btn {
  background: linear-gradient(135deg, #ff4757, #ff6b7a, #ff8a80);
  color: white;
  border: none;
  padding: 2.5vw 5vw;
  border-radius: 3vw;
  font-size: 3.2vw;
  font-weight: 600;
  cursor: pointer;
  margin-top: 2vw;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 71, 87, 0.3);
  position: relative;
  overflow: hidden;
}

.use-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s;
}

.use-btn:hover::before {
  left: 100%;
}

.use-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 71, 87, 0.4);
  background: linear-gradient(135deg, #ff3742, #ff4757, #ff6b7a);
}

.use-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(255, 71, 87, 0.3);
}

/* 底部菜单 */
.bottom-menu {
  position: fixed;
  bottom: 14vw;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 3vw 4vw;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4vw;
  border-top: 1px solid #f0f0f0;
  z-index: 100;
}

.menu-item {
  font-size: 3.2vw;
  color: #666;
  cursor: pointer;
}

.menu-divider {
  color: #ddd;
  font-size: 3.2vw;
}

/* 响应式调整 */
@media (max-width: 480px) {
  .redpacket-item, .coupon-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .redpacket-right, .coupon-right {
    align-items: flex-start;
    margin-top: 3vw;
  }
}
</style>
