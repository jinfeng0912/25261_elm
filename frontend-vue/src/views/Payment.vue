<template>
    <div class="wrapper">
      <!-- header部分 -->
      <header>
        <p>在线支付</p>
      </header>
      <h3>订单信息：</h3>
      <div class="order-info">
        <p>
          {{ orders.business?.businessName || '商家名称' }}
          <i class="fa fa-caret-down" @click="detailetShow"></i>
        </p>
        <p>&#165;{{ orders.orderTotal || 0 }}</p>
      </div>
      <ul class="order-detailet" v-show="isShowDetailet">
        <li v-for="item in orders.list" :key="item.odId">
          <p>{{ item.food.foodName }} x {{ item.quantity }}</p>
          <p>&#165;{{ (item.food.foodPrice * item.quantity).toFixed(2) }}</p>
        </li>
        <li>
          <p>配送费</p>
          <p>&#165;{{ orders.business?.deliveryPrice || 0 }}</p>
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
  import { ref, inject, onMounted, onUnmounted } from "vue";
  
  const axios = inject("axios");
  const router = useRouter();
  const route = useRoute();
  
  const orderId = route.query.orderId;
  const orders = ref({ business: {}, list: [] });
  const isShowDetailet = ref(false);
  const selectedPayment = ref('alipay');
  const isProcessing = ref(false);
  
  // 初始化方法 - 使用你后端的 OrdersController/getOrdersById
  const init = () => {
    console.log('=== 支付页面初始化 ===');
    console.log('订单ID:', orderId);
    
    // 调用你后端的获取订单接口
    axios
      .post(
        "OrdersController/getOrdersById",  // 匹配你的后端接口名
        qs.stringify({
          orderId: orderId,
        })
      )
      .then((response) => {
        if (response.data) {
          orders.value = response.data;
          console.log('订单数据加载成功:', orders.value);
        } else {
          console.error('订单数据为空');
          alert('订单信息不存在');
          router.go(-1);
        }
      })
      .catch((error) => {
        console.error('加载订单数据失败:', error);
        alert('加载订单信息失败');
        router.go(-1);
      });
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
    
    // 可以调用后端更新订单状态的接口
    // axios.post("OrdersController/updateOrder", qs.stringify({ orderId: orderId }))
    
    const paymentMethods = {
      'alipay': '支付宝',
      'wechat': '微信支付'
    };
    
    alert(`${paymentMethods[selectedPayment.value]}支付成功！\n\n订单号：${orderId}\n支付金额：￥${orders.value.orderTotal}\n\n预计30分钟内送达，请留意配送员电话。`);
    
    // 跳转到首页或订单列表
    router.push('/index');
  };
  
  // 支付失败处理
  const paymentFailure = () => {
    console.log('=== 支付失败 ===');
    alert('支付失败，请检查网络连接或稍后重试');
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
    justify-content: center;
    align-items: center;
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