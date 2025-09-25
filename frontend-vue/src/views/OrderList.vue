<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>我的订单</p>
    </header>

    <!-- 订单筛选 -->
    <div class="order-filter">
      <div 
        class="filter-item" 
        :class="{active: activeTab === 'all'}"
        @click="changeTab('all')"
      >
        全部订单
      </div>
      <div 
        class="filter-item"
        :class="{active: activeTab === 'unpaid'}"
        @click="changeTab('unpaid')"
      >
        未支付
      </div>
      <div 
        class="filter-item"
        :class="{active: activeTab === 'paid'}"
        @click="changeTab('paid')"
      >
        已支付
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <div 
        class="order-item" 
        v-for="order in filteredOrders" 
        :key="order.orderId"
      >
        <div class="order-header">
          <span class="order-time">{{ order.orderDate }}</span>
          <span class="order-status">
            {{ getStatusText(order.orderState) }}
          </span>
        </div>
        
        <div class="order-content">
          <div class="business-name">
            {{ order.business.businessName }}
          </div>
          
          <div class="order-detail">
            <p v-for="item in order.list" :key="item.odId">
              {{ item.food.foodName }} × {{ item.quantity }}
            </p>
          </div>
          
          <div class="order-total">
            合计：¥{{ order.orderTotal }}
            <span 
              v-if="order.orderState === 0" 
              class="pay-btn"
              @click.stop="goToPayment(order)"
            >
              去支付
            </span>
          </div>
        </div>
      </div>
    </div>

    <Footer></Footer>
  </div>
</template>

<script setup>
import Footer from "../components/Footer.vue";
import qs from "qs";
import { useRouter, useRoute } from "vue-router";
import { ref, computed, onMounted } from "vue";
import { getSessionStorage } from "../common.js";
import axios from "axios";
const router = useRouter();
const route = useRoute();

const user = getSessionStorage("user");
const orders = ref([]);
const activeTab = ref('all');

// 根据路由参数初始化筛选条件
if (route.query.businessId) {
  activeTab.value = 'paid';
}

// 筛选后的订单
const filteredOrders = computed(() => {
  if (activeTab.value === 'unpaid') {
    return orders.value.filter(o => o.orderState === 0);
  } else if (activeTab.value === 'paid') {
    return orders.value.filter(o => o.orderState === 1);
  }
  return orders.value;
});

// 简单校验：判断 token 是否像一个JWT
const isLikelyJwt = (t) => {
  return typeof t === 'string' && t.split('.').length === 3 && t.length > 20;
};

// 初始化订单数据（改为从后端 /api/orders 获取）
const init = async () => {
  try {
    const headers = {};
    if (user?.token && isLikelyJwt(user.token)) headers['Authorization'] = `Bearer ${user.token}`;

    const numericUserId = Number(user?.userId);
    const effectiveUserId = isNaN(numericUserId) ? 1 : numericUserId; // 非数字则回退到演示用户 1
    if (isNaN(numericUserId)) {
      console.warn('userId 非数字，已回退为演示用户ID=1 以便查看订单');
    }
    // 注意：已在 main.js 设置 axios.defaults.baseURL = '/api'
    // 这里无需再写 /api 前缀，避免出现 /api/api/ 重复
    const response = await axios.get('/orders', {
      params: { userId: effectiveUserId },
      headers
    });

    const data = (response && response.data && response.data.data) ? response.data.data : [];
    orders.value = data.map(order => ({
      ...order,
      business: order.business || { businessName: '未知商家' },
      list: order.list || []
    }));
  } catch (error) {
    console.error(error);
    alert('获取订单失败');
  }
};

// 切换订单状态
const changeTab = (tab) => {
  activeTab.value = tab;
};

// 获取订单状态文本
const getStatusText = (state) => {
  return state === 0 ? '待支付' : '已完成';
};

// 去支付（保留支付功能）
const goToPayment = (order) => {
  router.push({
    path: '/payment',
    query: { 
      orderId: order.orderId,
      from: 'orders'
    }
  });
};

onMounted(() => {
  init();
  
  if (route.query.refreshed) {
    init();
  }
});
</script>

<style scoped>
/* 订单筛选样式 */
.order-filter {
  display: flex;
  padding: 4vw 0;
  margin-top: 12vw;
  background: #fff;
  position: sticky;
  top: 12vw;
  z-index: 999;
}

.filter-item {
  flex: 1;
  text-align: center;
  padding: 2vw 0;
  font-size: 4vw;
  color: #666;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-item.active {
  color: #0097FF;
  border-bottom: 2px solid #0097FF;
}

.filter-item:hover {
  background-color: #f5f5f5;
}

/* 订单列表样式 */
.order-list {
  padding-bottom: 14vw;
}

.order-item {
  margin: 3vw;
  padding: 3vw;
  background: #fff;
  border-radius: 2vw;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  cursor: default;
}

/* 新增悬停效果 */
.order-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-left: 4px solid #0097FF;
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 2vw;
  border-bottom: 1px dashed #eee;
  font-size: 3.5vw;
}

.order-status {
  color: #FF6B00;
}

.order-content {
  padding-top: 2vw;
}

.business-name {
  font-weight: bold;
  font-size: 4vw;
  margin-bottom: 2vw;
  color: #333;
}

.order-detail {
  padding-left: 2vw;
  color: #666;
  font-size: 3.5vw;
}

.order-detail p {
  margin: 1vw 0;
}

.order-total {
  text-align: right;
  font-weight: bold;
  font-size: 4vw;
  margin-top: 2vw;
  padding-top: 2vw;
  border-top: 1px dashed #eee;
}

.pay-btn {
  display: inline-block;
  margin-left: 3vw;
  padding: 1vw 3vw;
  background: #0097FF;
  color: white;
  border-radius: 3vw;
  font-size: 3.5vw;
  cursor: pointer;
  transition: all 0.2s;
}

.pay-btn:hover {
  background: #0080e0;
  transform: scale(1.05);
}

/* 基础样式 */
.wrapper {
  width: 100%;
  height: 100%;
}

header {
  width: 100%;
  height: 12vw;
  background-color: #0097ff;
  color: #fff;
  font-size: 4.8vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>