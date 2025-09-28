<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>我的订单</p >
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
        @click="openDetail(order)"
      >
        <div class="order-header">
          <span class="order-time">{{ formatDateTime(order) }}</span>
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
            </p >
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
            <span 
              v-if="order.orderState === 0" 
              class="cancel-btn"
              @click.stop="cancelOrder(order)"
            >
              取消订单
            </span>
          </div>
        </div>
      </div>

      <!-- 订单详情弹窗 -->
      <div v-if="showDetailModal" class="detail-mask" @click.self="closeDetail">
        <div class="detail-modal">
          <div class="detail-row"><span>下单时间</span><span>{{ formatDateTime(detailOrder) }}</span></div>
          <div class="detail-row"><span>状态</span><span>{{ getStatusText(detailOrder?.orderState) }}</span></div>
          <div class="detail-row"><span>商家</span><span>{{ detailOrder?.business?.businessName || '未知商家' }}</span></div>
          <div class="detail-row"><span>合计</span><span>¥{{ detailOrder?.orderTotal || 0 }}</span></div>
          <div class="detail-row"><span>地址</span><span>{{ detailOrder?.deliveryAddress?.address || '-' }}</span></div>
          <div class="detail-items">
            <div class="detail-row" v-for="it in (detailOrder?.list || [])" :key="it?.odId">
              <span>{{ it?.food?.foodName }} × {{ it?.quantity }}</span>
              <span>¥{{ (it?.food?.foodPrice ?? 0) * (it?.quantity ?? 1) }}</span>
            </div>
          </div>
          <div class="detail-actions">
            <button class="detail-close" @click="closeDetail">关闭</button>
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
console.log("从本地储存读取token")
console.log(localStorage.getItem("token"))
console.log("从本地读取角色")
console.log(localStorage.getItem("role"))
console.log("从本地读取id")
console.log(localStorage.getItem("userId"))
console.log("从本地读取登录状态")
console.log(localStorage.getItem("loginStatus"))

// 根据路由参数初始化筛选条件
if (route.query.businessId) {
  activeTab.value = 'paid';
}

// 筛选后的订单
const filteredOrders = computed(() => {
  const key = 'paid_orders_demo';
  let paidIds = [];
  try { paidIds = JSON.parse(localStorage.getItem(key) || '[]'); } catch (e) {}

  // 追加：从本地历史中补充刚支付的订单
  const histKey = 'orders_history_demo';
  let historyOrders = [];
  try { historyOrders = JSON.parse(localStorage.getItem(histKey) || '[]'); } catch (e) {}

  // 读取“未支付”集合（创建了但未点击去支付）
  const unKey = 'unpaid_orders_demo';
  let unpaidIds = [];
  try { unpaidIds = JSON.parse(localStorage.getItem(unKey) || '[]'); } catch (e) {}

  const merged = [...historyOrders, ...orders.value].map(o => {
    const idStr = String(o.id ?? o.orderId);
    // 如果命中 unpaid 集合且未被标记为已支付，则强制视为未支付
    if (unpaidIds.includes(idStr) && !paidIds.includes(idStr)) {
      return { ...o, orderState: 0 };
    }
    return o;
  });

  const withLocalMark = merged.map(o => ({
    ...o,
    orderState: paidIds.includes(String(o.id ?? o.orderId)) ? 1 : o.orderState
  }));

  if (activeTab.value === 'unpaid') {
    return withLocalMark.filter(o => o.orderState === 0);
  } else if (activeTab.value === 'paid') {
    return withLocalMark.filter(o => o.orderState === 1);
  }
  return withLocalMark;
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

  // 使用当前登录用户的ID
  const effectiveUserId = localStorage.getItem("userId") || 1;
  console.log('获取订单列表，用户ID:', effectiveUserId);
  
  // 调用后端API获取订单列表
  const response = await axios.get('/api/orders', {
    params: { userId: effectiveUserId },
    headers
  });

  console.log('订单列表响应:', response.data);
  
  if (response.data && response.data.code === 'OK') {
    const data = response.data.data || [];
    orders.value = data.map(order => ({
      ...order,
      business: order.business || { businessName: '未知商家' },
      list: order.list || []
    }));
    console.log('订单列表加载成功:', orders.value);
  } else {
    console.error('获取订单列表失败:', response.data?.message);
    orders.value = [];
  }
} catch (error) {
  console.error('获取订单列表失败:', error);
  // 如果后端调用失败，使用本地数据作为兜底
  const paidOrdersKey = 'paid_orders_demo';
  const paidOrders = localStorage.getItem(paidOrdersKey);
  const paidOrderIds = paidOrders ? JSON.parse(paidOrders) : [];
  
  const mockOrders = paidOrderIds.map(orderId => ({
    id: orderId,
    orderId: orderId,
    orderTotal: '61.00',
    orderState: 1,
    business: {
      businessName: '测试餐厅',
    },
    list: [
      {
        odId: 1,
        food: {
          foodName: '宫保鸡丁',
          foodPrice: 28
        },
        quantity: 2
      }
    ]
  }));
  
  orders.value = mockOrders;
  console.log('使用本地订单数据作为兜底:', orders.value);
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
  // 为支付页准备本地订单数据，便于展示（未支付从订单页进入时没有 currentOrder）
  try {
    const paymentData = {
      orderId: (order && (order.orderId ?? order.id)),
      business: order?.business || { businessName: '未知商家', deliveryPrice: 0 },
      items: (order?.list || []).map(it => ({
        cartId: it?.odId || it?.id || Date.now(),
        food: {
          foodName: it?.food?.foodName || '未知菜品',
          foodPrice: it?.food?.foodPrice ?? it?.price ?? 0
        },
        quantity: it?.quantity ?? 1
      })),
      totalPrice: order?.orderTotal || 0
    };
    localStorage.setItem('currentOrder', JSON.stringify(paymentData));
  } catch (e) {}

  router.push({
    path: '/payment',
    query: { 
      orderId: (order && (order.orderId ?? order.id)) ,
      from: 'orders'
    }
  });
};

// 订单详情弹窗
const showDetailModal = ref(false);
const detailOrder = ref(null);
const openDetail = (order) => {
  detailOrder.value = order;
  showDetailModal.value = true;
};
const closeDetail = () => { showDetailModal.value = false; };

// 显示下单时间，优先使用 orderTime（ISO），其次 orderDate（仅日期），否则 '-'
const formatDateTime = (order) => {
  if (!order) return '-';
  if (order.orderTime) {
    // 规范化 ISO 到本地 yyyy-MM-dd HH:mm:ss
    try {
      const d = new Date(order.orderTime);
      const pad = (n) => (n < 10 ? '0' + n : n);
      return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
    } catch (e) {}
  }
  if (order.orderDate) {
    return order.orderDate; // 后端若只给日期
  }
  return '-';
};

// 取消订单：从本地未支付与历史中移除，并加入取消集合用于过滤
const cancelOrder = (order) => {
  const idStr = String(order?.id ?? order?.orderId);
  try {
    // 移除未支付集合
    const unKey = 'unpaid_orders_demo';
    const unArr = JSON.parse(localStorage.getItem(unKey) || '[]');
    const newUn = unArr.filter(id => String(id) !== idStr);
    localStorage.setItem(unKey, JSON.stringify(newUn));

    // 从本地历史中移除该订单（仅本地生成的）
    const histKey = 'orders_history_demo';
    const histArr = JSON.parse(localStorage.getItem(histKey) || '[]');
    const newHist = histArr.filter(o => String(o.id ?? o.orderId) !== idStr);
    localStorage.setItem(histKey, JSON.stringify(newHist));

    // 记录已取消集合，避免后端返回的同一订单继续显示
    const cancelKey = 'cancelled_orders_demo';
    const cArr = JSON.parse(localStorage.getItem(cancelKey) || '[]');
    if (!cArr.includes(idStr)) cArr.push(idStr);
    localStorage.setItem(cancelKey, JSON.stringify(cArr));

    // 即时更新当前列表中的 orders（仅影响运行时内存）
    orders.value = orders.value.filter(o => String(o.id ?? o.orderId) !== idStr);
    alert('已取消该订单');
  } catch (e) {
    console.error('取消订单失败:', e);
    alert('取消失败，请稍后重试');
  }
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

.cancel-btn {
  display: inline-block;
  margin-left: 2vw;
  padding: 1vw 3vw;
  background: #ff4d4f;
  color: white;
  border-radius: 3vw;
  font-size: 3.5vw;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: #e04345;
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

/* 详情弹窗样式 */
.detail-mask {
  position: fixed;
  left: 0; right: 0; top: 0; bottom: 0;
  background: rgba(0,0,0,0.35);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}
.detail-modal {
  width: 90vw;
  background: #fff;
  border-radius: 3vw;
  padding: 4vw;
}
.detail-row { display: flex; justify-content: space-between; margin: 1.5vw 0; font-size: 3.6vw; color: #333; }
.detail-items { margin-top: 2vw; border-top: 1px dashed #eee; padding-top: 2vw; }
.detail-actions { display: flex; justify-content: flex-end; margin-top: 3vw; }
.detail-close { background: #0097FF; color: #fff; border: none; border-radius: 2vw; height: 10vw; padding: 0 4vw; font-size: 3.8vw; }
</style>