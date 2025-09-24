<template>
    <div class="wrapper">
      <!-- header部分 -->
      <header>
        <i class="fa fa-arrow-left" @click="goBack"></i>
        <p>套餐支付</p>
      </header>
  
      <!-- 套餐信息部分 -->
      <div class="package-info">
        <h3>{{ packageData.packageName }}</h3>
        <div class="package-summary">
          <div class="price-info">
            <div class="original-price">原价：¥{{ packageData.originalPrice?.toFixed(2) }}</div>
            <div class="final-price">套餐价：¥{{ packageData.finalPrice?.toFixed(2) }}</div>
            <div class="discount-info">
              <i class="fa fa-gift"></i>
              已优惠：¥{{ packageData.discount?.toFixed(2) }}
            </div>
          </div>
        </div>
      </div>
  
      <!-- 套餐明细部分 -->
      <div class="package-details">
        <h4>套餐内容</h4>
        <ul class="item-list">
          <li v-for="item in packageData.items" :key="item.foodId" class="package-item">
            <div class="item-info">
              <div class="item-name">{{ item.foodName }}</div>
              <div class="item-desc">{{ item.foodExplain }}</div>
              <div class="item-quantity">数量：{{ item.quantity }}份</div>
            </div>
            <div class="item-price">¥{{ item.foodPrice }}</div>
          </li>
        </ul>
        
        <!-- 优惠说明 -->
        <div class="discount-detail">
          <div class="discount-item">
            <span>商品小计</span>
            <span>¥{{ packageData.originalPrice?.toFixed(2) }}</span>
          </div>
          <div class="discount-item highlight">
            <span>套餐优惠</span>
            <span>-¥{{ packageData.discount?.toFixed(2) }}</span>
          </div>
          <div class="discount-item total">
            <span>实付金额</span>
            <span>¥{{ packageData.finalPrice?.toFixed(2) }}</span>
          </div>
        </div>
      </div>
  
      <!-- 配送信息 -->
      <div class="delivery-info">
        <h4>配送信息</h4>
        <div class="delivery-content">
          <div class="delivery-item">
            <i class="fa fa-clock-o"></i>
            <span>预计30-45分钟送达</span>
          </div>
          <div class="delivery-item">
            <i class="fa fa-truck"></i>
            <span>免配送费（套餐专享）</span>
          </div>
          <div class="delivery-item">
            <i class="fa fa-shield"></i>
            <span>食品安全保障</span>
          </div>
        </div>
      </div>
  
      <!-- 支付方式 -->
      <div class="payment-methods">
        <h4>支付方式</h4>
        <ul class="payment-list">
          <li 
            :class="['payment-option', { 'selected': selectedPayment === 'alipay' }]"
            @click="selectPayment('alipay')"
          >
            <div class="payment-icon alipay">支</div>
            <span>支付宝</span>
            <i :class="['fa', selectedPayment === 'alipay' ? 'fa-check-circle' : 'fa-circle-o']"></i>
          </li>
          <li 
            :class="['payment-option', { 'selected': selectedPayment === 'wechat' }]"
            @click="selectPayment('wechat')"
          >
            <div class="payment-icon wechat">微</div>
            <span>微信支付</span>
            <i :class="['fa', selectedPayment === 'wechat' ? 'fa-check-circle' : 'fa-circle-o']"></i>
          </li>
        </ul>
      </div>
  
      <!-- 支付按钮 -->
      <div class="payment-footer">
        <div class="footer-price">
          <div class="price-label">实付金额</div>
          <div class="price-value">¥{{ packageData.finalPrice?.toFixed(2) }}</div>
        </div>
        <button class="pay-btn" @click="handlePayment">
          确认支付
        </button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  
  const router = useRouter()
  const route = useRoute()
  
  // 响应式数据
  const packageData = ref({})
  const selectedPayment = ref('alipay')
  
  // 生命周期
  onMounted(() => {
    console.log('=== 套餐支付页面加载 ===')
    
    // 从localStorage获取套餐数据
    const packageOrderStr = localStorage.getItem('packageOrder')
    if (packageOrderStr) {
      packageData.value = JSON.parse(packageOrderStr)
      console.log('套餐数据:', packageData.value)
    } else {
      console.warn('未找到套餐数据，返回首页')
      router.push('/')
      return
    }
    
    // 阻止返回到上一页
    history.pushState(null, null, document.URL)
    window.onpopstate = () => {
      router.push('/index')
    }
  })
  
  // 方法
  const goBack = () => {
    router.push('/packageDeals')
  }
  
  const selectPayment = (method) => {
    selectedPayment.value = method
    console.log('选择支付方式:', method)
  }
  
  const handlePayment = () => {
    const paymentName = selectedPayment.value === 'alipay' ? '支付宝' : '微信支付'
    const amount = packageData.value.finalPrice?.toFixed(2)
    
    console.log('=== 开始支付 ===')
    console.log('支付方式:', paymentName)
    console.log('支付金额:', amount)
    console.log('套餐信息:', packageData.value.packageName)
    
    // 模拟支付成功
    setTimeout(() => {
      alert(`套餐订单支付成功！\n套餐：${packageData.value.packageName}\n支付方式：${paymentName}\n实付金额：¥${amount}`)
      
      // 清除套餐数据
      localStorage.removeItem('packageOrder')
      
      // 返回首页
      router.push('/')
    }, 500)
  }
  </script>
  
  <style scoped>
  .wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 16vw;
  }
  
  /* header */
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
    align-items: center;
    justify-content: center;
  }
  
  .wrapper header .fa-arrow-left {
    position: absolute;
    left: 4vw;
    font-size: 5vw;
    cursor: pointer;
  }
  
  /* 套餐信息 */
  .package-info {
    margin-top: 12vw;
    background: linear-gradient(135deg, #4CAF50, #45a049);
    color: white;
    padding: 6vw 4vw;
  }
  
  .package-info h3 {
    font-size: 5.5vw;
    font-weight: bold;
    margin-bottom: 3vw;
    text-align: center;
  }
  
  .package-summary {
    display: flex;
    justify-content: center;
  }
  
  .price-info {
    text-align: center;
  }
  
  .original-price {
    font-size: 3vw;
    opacity: 0.8;
    text-decoration: line-through;
    margin-bottom: 1vw;
  }
  
  .final-price {
    font-size: 6vw;
    font-weight: bold;
    margin-bottom: 2vw;
  }
  
  .discount-info {
    background-color: rgba(255, 255, 255, 0.2);
    padding: 1.5vw 3vw;
    border-radius: 5vw;
    font-size: 3.5vw;
    display: inline-block;
  }
  
  .discount-info .fa-gift {
    margin-right: 1vw;
  }
  
  /* 套餐明细 */
  .package-details {
    margin: 4vw;
    background: white;
    border-radius: 3vw;
    padding: 4vw;
  }
  
  .package-details h4 {
    font-size: 4.5vw;
    color: #333;
    margin-bottom: 3vw;
    font-weight: bold;
  }
  
  .item-list {
    list-style: none;
    padding: 0;
  }
  
  .package-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 3vw 0;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .package-item:last-child {
    border-bottom: none;
  }
  
  .item-info {
    flex: 1;
  }
  
  .item-name {
    font-size: 3.8vw;
    color: #333;
    font-weight: bold;
    margin-bottom: 1vw;
  }
  
  .item-desc {
    font-size: 3vw;
    color: #666;
    margin-bottom: 1vw;
  }
  
  .item-quantity {
    font-size: 3vw;
    color: #999;
  }
  
  .item-price {
    font-size: 3.8vw;
    color: #ff6600;
    font-weight: bold;
  }
  
  /* 优惠明细 */
  .discount-detail {
    margin-top: 4vw;
    padding-top: 3vw;
    border-top: 2px solid #f0f0f0;
  }
  
  .discount-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 2vw;
    font-size: 3.5vw;
  }
  
  .discount-item.highlight {
    color: #4CAF50;
    font-weight: bold;
  }
  
  .discount-item.total {
    font-size: 4vw;
    font-weight: bold;
    color: #ff4444;
    border-top: 1px solid #eee;
    padding-top: 2vw;
  }
  
  /* 配送信息 */
  .delivery-info {
    margin: 4vw;
    background: white;
    border-radius: 3vw;
    padding: 4vw;
  }
  
  .delivery-info h4 {
    font-size: 4.5vw;
    color: #333;
    margin-bottom: 3vw;
    font-weight: bold;
  }
  
  .delivery-content {
    display: flex;
    flex-direction: column;
    gap: 2.5vw;
  }
  
  .delivery-item {
    display: flex;
    align-items: center;
    font-size: 3.5vw;
    color: #666;
  }
  
  .delivery-item i {
    width: 5vw;
    margin-right: 2vw;
    color: #4CAF50;
  }
  
  /* 支付方式 */
  .payment-methods {
    margin: 4vw;
    background: white;
    border-radius: 3vw;
    padding: 4vw;
  }
  
  .payment-methods h4 {
    font-size: 4.5vw;
    color: #333;
    margin-bottom: 3vw;
    font-weight: bold;
  }
  
  .payment-list {
    list-style: none;
    padding: 0;
  }
  
  .payment-option {
    display: flex;
    align-items: center;
    padding: 3vw;
    border: 1px solid #eee;
    border-radius: 2vw;
    margin-bottom: 2vw;
    cursor: pointer;
    transition: all 0.3s ease;
  }
  
  .payment-option:last-child {
    margin-bottom: 0;
  }
  
  .payment-option:hover {
    background-color: #f9f9f9;
  }
  
  .payment-option.selected {
    border-color: #4CAF50;
    background-color: #f0fdf0;
  }
  
  .payment-icon {
    width: 8vw;
    height: 8vw;
    border-radius: 1vw;
    margin-right: 3vw;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    font-size: 3.5vw;
  }
  
  .payment-icon.alipay {
    background-color: #1677ff;
  }
  
  .payment-icon.wechat {
    background-color: #07c160;
  }
  
  .payment-option span {
    flex: 1;
    font-size: 4vw;
    color: #333;
  }
  
  .payment-option .fa-check-circle {
    color: #4CAF50;
    font-size: 5vw;
  }
  
  .payment-option .fa-circle-o {
    color: #ccc;
    font-size: 5vw;
  }
  
  /* 支付底部 */
  .payment-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 16vw;
    background: white;
    border-top: 1px solid #eee;
    display: flex;
    align-items: center;
    padding: 0 4vw;
  }
  
  .footer-price {
    flex: 1;
    text-align: left;
  }
  
  .price-label {
    font-size: 3vw;
    color: #666;
    margin-bottom: 1vw;
  }
  
  .price-value {
    font-size: 5vw;
    color: #ff4444;
    font-weight: bold;
  }
  
  .pay-btn {
    width: 30vw;
    height: 10vw;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 2vw;
    font-size: 4vw;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .pay-btn:hover {
    background-color: #45a049;
  }
  </style>