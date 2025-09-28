<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <div class="header-left" @click="goBack">
        <i class="fa fa-angle-left"></i>
        <span>返回</span>
      </div>
      <p>在线支付</p >
      <div class="header-right"></div>
    </header>
    <h3>订单信息：</h3>
    <div class="order-info">
      <p>
        {{ orders.business?.businessName || '商家名称' }}
        <i class="fa fa-caret-down" @click="detailetShow"></i>
      </p >
      <p>&#165;{{ orders.orderTotal || 0 }}</p >
    </div>
    <ul class="order-detailet" v-show="isShowDetailet">
      <li v-for="item in orders.list" :key="item.odId">
        <p>{{ item.food.foodName }} x {{ item.quantity }}</p >
        <p>&#165;{{ (item.food.foodPrice * item.quantity).toFixed(2) }}</p >
      </li>
      <li>
        <p>配送费</p >
        <p>&#165;{{ orders.business?.deliveryPrice || 0 }}</p >
      </li>
      <li v-if="orders.selectedRedPacket" class="redpacket-item">
        <div class="redpacket-info">
          <i class="fa fa-gift"></i>
          <span>红包抵扣</span>
        </div>
        <div class="redpacket-amount">
          <span>-&#165;{{ orders.selectedRedPacket.amount }}</span>
          <div class="redpacket-detail">{{ orders.selectedRedPacket.title }}</div>
        </div>
      </li>
    </ul>
    <ul class="payment-type">
      <li :class="{ active: selectedPayment === 'alipay' }" @click="selectPayment('alipay')">
        <img src="../assets/alipay.png" />
        <i class="fa fa-check-circle" v-show="selectedPayment === 'alipay'"></i>
      </li>
      <li :class="{ active: selectedPayment === 'wechat' }" @click="selectPayment('wechat')">
        <img src="../assets/wechat.png" />
        <i class="fa fa-check-circle" v-show="selectedPayment === 'wechat'"></i>
      </li>
    </ul>
    <div class="payment-button">
      <button @click="confirmPayment" :disabled="isProcessing">
        {{ isProcessing ? '支付中...' : '确认支付' }}
      </button>
    </div>
    <Footer></Footer>
  </div>
</template>

<script setup>
import Footer from "../components/Footer.vue";
import qs from "qs";
import { useRouter, useRoute } from "vue-router";
import { ref, onMounted, onUnmounted } from "vue";
import { getSessionStorage, getLocalStorage } from "../common.js";
import axios from "axios";
const router = useRouter();
const route = useRoute();

const orderId = route.query.orderId;
const orders = ref({ business: {}, list: [] });
const isShowDetailet = ref(false);
const selectedPayment = ref('alipay');
const isProcessing = ref(false);

// 初始化方法：从localStorage获取订单数据
const init = async () => {
  console.log('=== 支付页面初始化 ===');
  console.log('订单ID:', orderId);
  
  // 检查登录状态
  const isLoggedIn = localStorage.getItem('isLoggedIn');
  const userInfo = getSessionStorage("user") || getLocalStorage("userInfo");
  
  console.log('登录状态检查:');
  console.log('- isLoggedIn:', isLoggedIn);
  console.log('- userInfo:', userInfo);
  
  if (isLoggedIn !== 'true') {
    console.log('用户未登录，跳转到登录页');
    alert('请先登录后再进行支付');
    router.push('/login');
    return;
  }
  
  try {
    // 从localStorage获取订单数据
    const orderData = localStorage.getItem('currentOrder');
    console.log('localStorage中的订单数据:', orderData);
    
    if (orderData) {
      const parsedOrderData = JSON.parse(orderData);
      console.log('解析后的订单数据:', parsedOrderData);
      
      // 设置订单信息
      orders.value = {
        orderId: parsedOrderData.orderId,
        orderTotal: parsedOrderData.totalPrice,
        business: parsedOrderData.business || { businessName: '未知商家', deliveryPrice: 0 },
        list: parsedOrderData.items.map(item => ({
          odId: item.cartId,
          food: {
            foodName: item.food.foodName,
            foodPrice: item.food.foodPrice
          },
          quantity: item.quantity
        })),
        selectedRedPacket: parsedOrderData.selectedRedPacket || null
      };
      
      console.log('订单数据设置成功:', orders.value);
      
      // 清除localStorage中的订单数据和购物车数据
      localStorage.removeItem('currentOrder');
      localStorage.removeItem('currentCart'); // 清除购物车数据，因为已经进入支付流程
      
    } else {
      console.error('未找到订单数据，尝试从购物车数据创建订单');
      
      // 尝试从购物车数据创建订单
      const cartData = localStorage.getItem('currentCart');
      if (cartData) {
        const parsedCartData = JSON.parse(cartData);
        console.log('从购物车数据创建订单:', parsedCartData);
        
        orders.value = {
          orderId: orderId,
          orderTotal: parsedCartData.totalPrice,
          business: parsedCartData.business || { businessName: '未知商家', deliveryPrice: 0 },
          list: parsedCartData.items.map(item => ({
            odId: item.foodId,
            food: {
              foodName: item.foodName,
              foodPrice: item.foodPrice
            },
            quantity: item.quantity
          })),
          selectedRedPacket: parsedCartData.selectedRedPacket || null
        };
        
        console.log('从购物车数据创建订单成功:', orders.value);
        localStorage.removeItem('currentCart');
      } else {
        console.error('既没有订单数据也没有购物车数据');
        alert('订单信息不存在，请重新下单');
        router.push('/');
        return;
      }
    }
  } catch (e) {
    console.error('加载订单数据失败:', e);
    alert('加载订单信息失败');
    router.push('/');
  }
};

// 页面初始化
init();

onMounted(() => {
  history.pushState(null, null, document.URL);
  window.onpopstate = () => {
    router.push({ path: "/index" });
  };
});

onUnmounted(() => {
  window.onpopstate = null;
});

const detailetShow = () => {
  isShowDetailet.value = !isShowDetailet.value;
};

// 选择支付方式
const selectPayment = (paymentType) => {
  selectedPayment.value = paymentType;
  console.log('选择支付方式:', paymentType);
};

// 确认支付
const confirmPayment = async () => {
  if (isProcessing.value) return;
  
  console.log('=== 开始支付 ===');
  console.log('订单ID:', orderId);
  console.log('支付方式:', selectedPayment.value);
  console.log('支付金额:', orders.value.orderTotal);
  
  isProcessing.value = true;
  
  const paymentMethods = {
    'alipay': '支付宝',
    'wechat': '微信支付'
  };
  
  const methodName = paymentMethods[selectedPayment.value];
  
  try {
    // 显示支付处理提示
    alert(`正在使用${methodName}支付，请稍候...`);
    
    // 模拟支付延迟
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 如果你的后端有支付接口，可以调用
    // 目前模拟支付成功
    const success = Math.random() > 0.1; // 90%成功率
    
    if (success) {
      paymentSuccess();
    } else {
      paymentFailure();
    }
    
  } catch (error) {
    console.error('支付处理错误:', error);
    paymentFailure();
  } finally {
    isProcessing.value = false;
  }
};

// 支付成功处理
const paymentSuccess = () => {
  console.log('=== 支付成功 ===');
  
  // 使用本地标记订单为已支付（因为后端没有支付API）
  try {
    const key = 'paid_orders_demo';
    const raw = localStorage.getItem(key);
    const arr = raw ? JSON.parse(raw) : [];
    if (!arr.includes(String(orderId))) arr.push(String(orderId));
    localStorage.setItem(key, JSON.stringify(arr));
    console.log('订单已标记为已支付:', orderId);

    // 从未支付列表中移除该订单
    try {
      const unKey = 'unpaid_orders_demo';
      const unRaw = localStorage.getItem(unKey);
      const unArr = unRaw ? JSON.parse(unRaw) : [];
      const filtered = unArr.filter(id => String(id) !== String(orderId));
      localStorage.setItem(unKey, JSON.stringify(filtered));
    } catch (e) {}

    // 将本次支付的订单写入本地订单历史，供订单页展示（仅联调演示用）
    const histKey = 'orders_history_demo';
    const histRaw = localStorage.getItem(histKey);
    const histArr = histRaw ? JSON.parse(histRaw) : [];
    const brief = {
      id: String(orderId),
      orderId: String(orderId),
      orderTotal: orders.value.orderTotal,
      orderState: 1,
      orderDate: new Date().toISOString().slice(0,10),
      business: orders.value.business || { businessName: '未知商家' },
      list: orders.value.list || []
    };
    // 去重后追加
    const exists = histArr.some(x => String(x.id ?? x.orderId) === String(orderId));
    if (!exists) histArr.unshift(brief);
    localStorage.setItem(histKey, JSON.stringify(histArr));
  } catch (e) {
    console.error('标记订单支付状态失败:', e);
  }
  
  const paymentMethods = {
    'alipay': '支付宝',
    'wechat': '微信支付'
  };
  
  alert(`${paymentMethods[selectedPayment.value]}支付成功！\n\n订单号：${orderId}\n支付金额：￥${orders.value.orderTotal}\n\n预计30分钟内送达，请留意配送员电话。`);
  
  // 跳转到订单列表并刷新
  router.push({ path: '/orderList', query: { refreshed: Date.now() } });
};

// 支付失败处理
const paymentFailure = () => {
  console.log('=== 支付失败 ===');
  alert('支付失败，请检查网络连接或稍后重试');
};

// 返回功能
const goBack = () => {
  // 支付页面应该返回到确认订单页面
  // 检查路由参数中是否有来源信息
  const from = route.query.from;
  
  if (from === 'orders') {
    // 如果是从订单页面来的，需要带上businessId参数返回
    const orderData = localStorage.getItem('currentOrder');
    if (orderData) {
      try {
        const parsedOrderData = JSON.parse(orderData);
        const businessId = parsedOrderData.business?.businessId || parsedOrderData.business?.id;
        if (businessId) {
          router.push({
            path: '/orders',
            query: { businessId: businessId }
          });
          return;
        }
      } catch (e) {
        console.error('解析订单数据失败:', e);
      }
    }
    // 如果无法获取businessId，返回到订单列表页面
    router.push('/orderList');
  } else {
    // 否则返回到订单列表页面
    router.push('/orderList');
  }
};
</script>

<style scoped>
/* 保持你原有的样式 */
/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  height: 100%;
}

/****************** header部分 ******************/
.wrapper header {
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
  justify-content: space-between;
  align-items: center;
  padding: 0 4vw;
  box-sizing: border-box;
}

.header-left {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 4vw;
  transition: opacity 0.2s;
}

.header-left:hover {
  opacity: 0.8;
}

.header-left i {
  margin-right: 1vw;
  font-size: 5vw;
}

.header-left span {
  font-size: 3.8vw;
}

header p {
  margin: 0;
  font-size: 4.8vw;
  font-weight: 500;
}

.header-right {
  width: 20vw; /* 占位，保持标题居中 */
}

/****************** 订单信息部分 ******************/
.wrapper h3 {
  margin-top: 12vw;
  box-sizing: border-box;
  padding: 4vw 4vw 0;

  font-size: 4vw;
  font-weight: 300;
  color: #999;
}

.wrapper .order-info {
  box-sizing: border-box;
  padding: 4vw;
  font-size: 4vw;
  color: #666;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .order-info p:last-child {
  color: orangered;
}

/****************** 订单明细部分 ******************/
.wrapper .order-detailet {
  width: 100%;
}

.wrapper .order-detailet li {
  width: 100%;
  box-sizing: border-box;
  padding: 1vw 4vw;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .order-detailet li p {
  font-size: 3vw;
  color: #666;
}

.redpacket-item {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe8e8 100%) !important;
  border-left: 4px solid #ff4757;
  border-radius: 0 2vw 2vw 0;
  margin: 1vw 0;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.1);
}

.redpacket-info {
  display: flex;
  align-items: center;
  gap: 2vw;
  color: #ff4757;
  font-size: 3vw;
  font-weight: 600;
}

.redpacket-info i {
  font-size: 3.5vw;
  color: #ff4757;
  background: rgba(255, 71, 87, 0.1);
  padding: 1.2vw;
  border-radius: 50%;
}

.redpacket-amount {
  text-align: right;
  color: #ff4757;
}

.redpacket-amount span {
  font-size: 3.5vw;
  font-weight: bold;
  color: #ff4757;
}

.redpacket-detail {
  font-size: 2.4vw;
  color: #999;
  margin-top: 0.5vw;
  opacity: 0.8;
}

/****************** 支付方式部分 ******************/
.wrapper .payment-type {
  width: 100%;
  margin-top: 4vw;
}

.wrapper .payment-type li {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s ease;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .payment-type li.active {
  border-color: #38ca73;
  background-color: #f8fff8;
}

.wrapper .payment-type li img {
  width: 33vw;
  height: 8.9vw;
}

.wrapper .payment-type li .fa-check-circle {
  font-size: 5vw;
  color: #38ca73;
}

.wrapper .payment-button {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw;
}

.wrapper .payment-button button {
  width: 100%;
  height: 10vw;
  border: none;
  outline: none;
  border-radius: 4px;
  background-color: #38ca73;
  color: #fff;
  font-size: 4vw;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.wrapper .payment-button button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.wrapper .payment-button button:not(:disabled):hover {
  background-color: #32b865;
}
</style>