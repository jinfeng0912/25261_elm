<template>
    <div class="wrapper">
      <header>
        <div class="header-content">
          <i class="fa fa-arrow-left" @click="goBack"></i>
          <span>超级会员支付</span>
        </div>
      </header>
  
      <h3>订单信息</h3>
      <div class="order-info">
        <p>订单号：{{ orderId }}</p>
        <p>下单时间：{{ orderTime }}</p>
      </div>
  
      <h3>会员项目</h3>
      <ul class="order-detailet">
        <li>
          <p>超级会员（30天）</p>
          <p>¥9.90</p>
        </li>
      </ul>
  
      <h3>支付方式</h3>
      <ul class="payment-type">
        <li>
          <label>
            <input type="radio" name="payMethod" value="alipay" v-model="selectedPayMethod">
            <img src="@/assets/alipay.png" alt="支付宝"> 支付宝
          </label>
        </li>
        <li>
          <label>
            <input type="radio" name="payMethod" value="wechat" v-model="selectedPayMethod">
            <img src="@/assets/wechat.png" alt="微信支付"> 微信支付
          </label>
        </li>
      </ul>
  
      <div class="payment-button">
        <button class="pay-btn" @click="pay">确认支付</button>
        <button class="back-btn" @click="goBack">返回首页</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  
  // 使用 Vue Router
  const router = useRouter();
  
  // 响应式变量
  const orderId = ref('');
  const orderTime = ref('');
  const selectedPayMethod = ref('alipay'); // 默认选中支付宝
  
  // 生成订单号的函数
  const generateOrderId = () => {
    const now = new Date();
    const timestamp = now.getFullYear().toString()
      + (now.getMonth() + 1).toString().padStart(2, '0')
      + now.getDate().toString().padStart(2, '0')
      + now.getHours().toString().padStart(2, '0')
      + now.getMinutes().toString().padStart(2, '0')
      + now.getSeconds().toString().padStart(2, '0');
    const random = Math.floor(Math.random() * 900 + 100); // 三位随机数
    return "VIP" + timestamp + random;
  };
  
  // 支付逻辑
  const pay = () => {
    const paymentName = selectedPayMethod.value === 'alipay' ? '支付宝' : '微信支付';
    alert(`订单 ${orderId.value} 已通过 ${paymentName} 成功付款！`);
    router.push('/'); // 支付成功后跳转回首页
  };
  
  // 返回首页
  const goBack = () => {
    router.push('/');
  };
  
  // 组件挂载后执行初始化
  onMounted(() => {
    orderId.value = generateOrderId();
    orderTime.value = new Date().toLocaleString('zh-CN');
  });
  </script>
  
  <style scoped>
  /* 样式直接从你的 superMember.html 文件中复制，并稍作调整 */
  .wrapper {
    width: 100%;
    height: 100%;
    background-color: #f5f5f5;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  }
  
  header {
    width: 100%;
    height: 12vw;
    background-color: #0097FF;
    color: white;
    font-size: 4.8vw;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  header .header-content {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
  }
  
  header .fa-arrow-left {
    position: absolute;
    left: 3vw;
    top: 50%;
    transform: translateY(-50%);
    cursor: pointer;
    font-size: 5vw;
  }
  
  h3 {
    padding: 4vw 4vw 2vw;
    font-size: 4vw;
    font-weight: 300;
    color: #999;
    box-sizing: border-box;
    margin-top: 12vw; /* 避开固定的header */
  }
  h3:first-of-type {
    margin-top: 12vw; /* 确保第一个h3也避开header */
  }
  h3:not(:first-of-type) {
    margin-top: 0; /* 其他h3不需要额外margin */
  }
  
  
  .order-info, .order-detailet, .payment-type {
    background-color: #fff;
    padding: 2vw 4vw;
    color: #666;
    font-size: 3.5vw;
  }
  
  .order-info p {
    line-height: 2;
  }
  
  .order-detailet li, .payment-type li {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2vw 0;
    border-bottom: 1px solid #f0f0f0;
  }
  .order-detailet li:last-child, .payment-type li:last-child {
    border-bottom: none;
  }
  
  .payment-type label {
    display: flex;
    align-items: center;
    font-size: 4vw;
    color: #333;
    width: 100%;
    cursor: pointer;
  }
  
  .payment-type img {
    width: 10vw;
    height: auto;
    margin-right: 3vw;
  }
  
  .payment-type input[type="radio"] {
    transform: scale(1.2);
    margin-right: 4vw;
    accent-color: #0097FF;
  }
  
  .payment-button {
    width: 100%;
    box-sizing: border-box;
    padding: 4vw;
    display: flex;
    flex-direction: column;
    gap: 3vw;
    margin-top: 5vw;
  }
  
  .payment-button button {
    width: 100%;
    height: 12vw;
    border: none;
    outline: none;
    border-radius: 6px;
    font-size: 4.5vw;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .payment-button .pay-btn {
    background-color: #38CA73;
    color: white;
  }
  .payment-button .pay-btn:hover {
    background-color: #2ca25f;
  }
  
  .payment-button .back-btn {
    background-color: #0097FF;
    color: white;
  }
  .payment-button .back-btn:hover {
    background-color: #0074cc;
  }
  </style>