<template>
    <div class="wrapper">
        <!-- header -->
        <header>
            <p>商家信息</p>
        </header>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>正在加载商家信息...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-container">
          <p>{{ error }}</p>
          <button @click="retryLoad" class="retry-btn">重新加载</button>
      </div>

      <!-- 正常内容 -->
      <div v-else>
        <!-- LOGO -->
        <div class="business-logo">
              <img :src="business.businessImg" :alt="business.businessName">
        </div>

        <!-- 商家信息 -->
        <div class="business-info">
              <h1>{{business.businessName || '商家名称'}}</h1>
              <p>&#165;{{business.startPrice || 0}}起送 &#165;{{business.deliveryPrice || 0}}配送</p>
              <p>{{business.businessExplain || '暂无商家介绍'}}</p>
        </div>

          <!-- 食品列表 -->
          <div v-if="foodArr.length > 0">
        <ul class="food">
            <li v-for="(item,index) in foodArr" :key="item.foodId">
                <div class="food-left">
                          <img :src="item.foodImg" :alt="item.foodName">
                    <div class="food-left-info">
                              <h3>{{item.foodName || '商品名称'}}</h3>
                              <p>{{item.foodExplain || '暂无商品介绍'}}</p>
                              <p>&#165;{{item.foodPrice || 0}}</p>
                    </div>
                </div>
                <div class="food-right">
                    <div>
                        <i class="fa fa-minus-circle" @click="minus(index)" v-show="item.quantity!=0"></i>
                    </div>
                    <p><span v-show="item.quantity!=0">{{item.quantity}}</span></p>
                    <div>
                        <i class="fa fa-plus-circle" @click="add(index)"></i>
                    </div>
                </div>
            </li>
        </ul>
          </div>
          
          <!-- 空状态 -->
          <div v-else class="empty-state">
              <p>该商家暂无商品</p>
          </div>

        <!-- 购物车 -->
        <div class="cart">
            <div class="cart-left">
                <div class="cart-left-icon" :style="totalQuantity==0?'background-color:#505051;':'background-color:#3190E8;'">
                    <i class="fa fa-shopping-cart"></i>
                    <div class="cart-left-icon-quantity" v-show="totalQuantity!=0">{{totalQuantity}}</div>
                </div>
                <div class="cart-left-info">
                    <p>&#165;{{totalPrice}}</p>
                  <div v-if="selectedRedPacket" class="redpacket-discount">
                      <i class="fa fa-gift"></i>
                      <span>红包抵扣-&#165;{{selectedRedPacket.amount}}</span>
                  </div>
                    <p>另需配送费{{business.deliveryPrice}}元</p>
                </div>
                <div class="cart-right">
                    <!--不够配送费-->
                  <div class="cart-right-item" v-show="totalPrice<business.startPrice" style="background-color: #535356;cursor: default;">&#165;{{business.startPrice}}起送</div>
                    <!--达到配送费-->
                  <div class="cart-right-item" @click="toOrder" v-show="totalPrice>=business.startPrice">去结算</div>
                  </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSessionStorage, getLocalStorage } from '../common.js'
import qs from 'qs'

// 添加 axios 注入
const axios = inject("axios")

// 图片现在完全从后端API获取，不再使用本地硬编码图片

const route = useRoute()
const router = useRouter()

// 响应式数据
const business = ref({})
const foodArr = ref([])
const user = ref(null)
const loading = ref(true)
const error = ref(null)
const selectedRedPacket = ref(null)

// 获取路由参数
const businessId = parseInt(route.query.businessId)

// 标准化用户信息，确保有userId字段
const normalizeUserInfo = (userInfo) => {
  if (!userInfo) return null
  
  // 如果已经有userId字段，直接返回
  if (userInfo.userId) return userInfo
  
  // 尝试从其他可能的字段获取用户ID
  const userId = userInfo.id || userInfo.username || userInfo.sub || localStorage.getItem('userId')
  
  return {
    ...userInfo,
    userId: userId
  }
}

// 商家数据现在完全从后端API获取，不再使用硬编码数据

// 商品数据现在完全从后端API获取，不再使用硬编码数据

// 生命周期
onMounted(async () => {
console.log('=== BusinessInfo 页面加载 ===')
console.log('商家ID:', businessId)

// 使用统一的方法检查登录状态并加载数据
await checkLoginAndLoadData()

// 监听页面可见性变化，当用户从登录页面返回时重新检查登录状态
document.addEventListener('visibilitychange', handleVisibilityChange)
})

// 页面可见性变化处理
const handleVisibilityChange = async () => {
if (!document.hidden) {
  console.log('页面重新可见，重新检查登录状态')
  // 检查登录状态是否发生变化
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  if (isLoggedIn === 'true' && !user.value) {
    console.log('检测到登录状态变化，重新加载数据')
    await checkLoginAndLoadData()
  }
}
}

// 重试加载
const retryLoad = () => {
console.log('🔄 用户点击重试按钮')
error.value = null
loading.value = true
// 重新检查登录状态并加载数据
checkLoginAndLoadData()
}

// 检查登录状态并加载数据
const checkLoginAndLoadData = async () => {
try {
  // 简单直接的登录状态检查
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  const userInfo = getSessionStorage("user") || getLocalStorage("userInfo")
  
  console.log('登录状态检查:')
  console.log('- isLoggedIn:', isLoggedIn)
  console.log('- userInfo:', userInfo)
  
  if (isLoggedIn === 'true' && userInfo) {
    user.value = userInfo
    console.log('✅ 用户已登录:', user.value)
  } else {
    console.log('❌ 用户未登录')
    user.value = null
  }
  
  // 从后端加载商家信息
  await loadBusinessInfo()
  
  // 加载食品列表并同步购物车数据
  await loadFoodList()
  
  // 加载红包信息
  loadRedPacketInfo()
  
} catch (err) {
  console.error('页面加载失败:', err)
  error.value = '页面加载失败，请刷新重试'
} finally {
  loading.value = false
}
}

// 从后端获取商家信息
const loadBusinessInfo = async () => {
try {
  console.log('从后端获取商家信息...')
  // 使用GET请求，匹配你的后端RESTful API
  const response = await axios.get(`/api/businesses/${businessId}`)
  
  console.log('商家信息响应:', response.data)
  
  // 根据你的HttpResult格式判断
  if (response.data && response.data.code === "OK" && response.data.data) { // 成功状态码是"OK"且有数据
    business.value = {
      ...response.data.data,
      businessId: response.data.data.id  // 统一使用businessId字段
    }
    console.log('✅ 成功加载商家信息:', business.value.businessName)
  } else {
    console.error('❌ 后端获取商家信息失败:', response.data?.message || '未知错误')
    error.value = '获取商家信息失败，请刷新重试'
    return
  }
  
} catch (error) {
  console.error('❌ 获取商家信息失败:', error)
  error.value = '网络连接失败，请检查网络后重试'
}
}

// 加载食品列表并同步购物车数据
const loadFoodList = async () => {
  console.log('开始加载食品列表...')
  
try {
  // 1. 从后端获取商品数据
  console.log('从后端获取商品列表...')
  // 使用GET请求，匹配你的后端RESTful API
  const response = await axios.get(`/api/foods?business=${businessId}`)
  
  console.log('商品列表响应:', response.data)
  
  // 根据你的HttpResult格式判断
  if (response.data && response.data.code === "OK" && Array.isArray(response.data.data)) {
    // 后端返回的商品数据，添加quantity字段，并统一ID字段
    foodArr.value = response.data.data.map(food => ({
      ...food,
      foodId: food.id,  // 统一使用foodId字段
      foodPrice: food.price || food.foodPrice,  // 统一价格字段
      quantity: 0  // 初始数量为0
    }))
    console.log('✅ 成功从后端加载商品数据:', foodArr.value.length, '个商品')
    console.log('商品数据结构示例:', foodArr.value[0])
    
      } else {
    console.error('❌ 后端获取商品列表失败:', response.data?.message || '未知错误')
    error.value = '获取商品列表失败，请刷新重试'
    return
      }
      
    } catch (error) {
  console.error('❌ 获取商品列表失败:', error)
  error.value = '网络连接失败，请检查网络后重试'
}

// 2. 暂时跳过购物车同步，专注于基本功能
console.log('跳过购物车同步，专注于基本功能')
}

// 加载红包信息
const loadRedPacketInfo = () => {
  try {
    const redpacketData = localStorage.getItem('selectedRedPacket')
    if (redpacketData) {
      const redpacket = JSON.parse(redpacketData)
      selectedRedPacket.value = redpacket
      console.log('已加载红包信息:', redpacket)
    } else {
      console.log('没有可用的红包')
    }
  } catch (error) {
    console.error('加载红包信息失败:', error)
  }
}

// 计算属性
const totalPrice = computed(() => {
  let total = 0
  for (let item of foodArr.value) {
  const price = item.foodPrice || item.price || 0
  const quantity = item.quantity || 0
  total += price * quantity
  console.log(`商品 ${item.foodName}: 价格=${price}, 数量=${quantity}, 小计=${price * quantity}`)
}

// 应用红包抵扣
if (selectedRedPacket.value && total >= selectedRedPacket.value.condition) {
  const originalTotal = total
  total = Math.max(0, total - selectedRedPacket.value.amount)
  console.log(`红包抵扣: 原价=${originalTotal}, 抵扣=${selectedRedPacket.value.amount}, 实付=${total}`)
}

console.log('总价计算:', total)
  return total.toFixed(2)
})

const totalQuantity = computed(() => {
  let quantity = 0
  for (let item of foodArr.value) {
    quantity += item.quantity
  }
  return quantity
})

// 方法
const getBusinessImage = (businessId) => {
// 使用后端返回的图片URL
if (business.value && business.value.businessImg) {
  return business.value.businessImg
}

// 如果没有图片URL，返回默认占位图
return '/images/default-business.png'
}

// 商品图片现在完全从后端API获取，不再使用硬编码图片映射

// 获取商品图片 - 完全使用后端返回的图片URL
const getFoodImage = (foodId) => {
  console.log(`获取商品图片 - foodId: ${foodId}`)
  
// 使用后端返回的商品图片URL
const foodItem = foodArr.value.find(food => food.foodId === foodId)
if (foodItem && foodItem.foodImg) {
  console.log(`✅ 使用后端商品图片: foodId ${foodId}`)
  return foodItem.foodImg
}

// 如果没有商品图片，返回默认占位图
console.log(`⚠️ 未找到商品图片，使用默认占位图: foodId ${foodId}`)
return '/images/default-food.png'
}

// 移除不再需要的图片名称获取函数

// 修改 add 方法 - 调用后端API
const add = async (index) => {
  console.log('=== 添加食品到购物车 ===')
  const food = foodArr.value[index]
  console.log('食品:', food.foodName)
  console.log('价格:', food.foodPrice)
  console.log('食品ID:', food.foodId)
  console.log('商家ID:', businessId)
console.log('用户状态:', user.value ? '已登录' : '未登录')

if (!user.value) {
  alert('请先登录')
  router.push('/login')
  return
}

try {
  // 添加认证头信息
  const headers = {};
  if (user.value?.token) {
    headers['Authorization'] = `Bearer ${user.value.token}`;
  }

  // 调用后端API添加商品到购物车
  const response = await axios.post('/api/carts', {
    food: {
      id: food.foodId
    },
    quantity: 1
  }, { headers })
  
  if (response.data && response.data.code === 'OK') {
    // 更新本地数量（立即反馈）
    foodArr.value[index].quantity++
    console.log('本地数量更新为:', foodArr.value[index].quantity)
    console.log('✅ 商品已添加到购物车')
    } else {
    console.error('添加购物车失败:', response.data?.message)
    alert('添加购物车失败')
    }
  } catch (error) {
  console.error('添加购物车失败:', error)
  // 如果后端调用失败，使用本地模式作为兜底
  foodArr.value[index].quantity++
  console.log('后端调用失败，使用本地模式')
  console.log('本地数量更新为:', foodArr.value[index].quantity)
  }
  
  console.log('当前总价:', totalPrice.value)
  console.log('当前总数量:', totalQuantity.value)
}

// 修改 minus 方法 - 调用后端API
const minus = async (index) => {
console.log('=== 减少购物车食品 ===')
const food = foodArr.value[index]
console.log('食品:', food.foodName)
console.log('当前数量:', food.quantity)
console.log('用户状态:', user.value ? '已登录' : '未登录')

if (foodArr.value[index].quantity <= 0) {
  return
}

  if (!user.value) {
  alert('请先登录')
    router.push('/login')
    return
  }
  
try {
  // 添加认证头信息
  const headers = {};
  if (user.value?.token) {
    headers['Authorization'] = `Bearer ${user.value.token}`;
  }

  // 调用后端API更新购物车商品数量
  const response = await axios.put(`/api/carts/${food.cartId || food.foodId}`, {
    quantity: foodArr.value[index].quantity - 1
  }, { headers })
  
  if (response.data && response.data.code === 'OK') {
    // 更新本地数量（立即反馈）
  foodArr.value[index].quantity--
  console.log('本地数量更新为:', foodArr.value[index].quantity)
    console.log('✅ 商品数量已减少')
    } else {
    console.error('更新购物车失败:', response.data?.message)
    alert('更新购物车失败')
  }
  } catch (error) {
  console.error('更新购物车失败:', error)
  // 如果后端调用失败，使用本地模式作为兜底
  foodArr.value[index].quantity--
  console.log('后端调用失败，使用本地模式')
  console.log('本地数量更新为:', foodArr.value[index].quantity)
  }
  
  console.log('当前总价:', totalPrice.value)
  console.log('当前总数量:', totalQuantity.value)
}

// 修改 toOrder 方法
const toOrder = async () => {
  console.log('=== 去结算 ===')
  console.log('总价格:', totalPrice.value)
console.log('起送价:', business.value.startPrice)
  
if (parseFloat(totalPrice.value) < business.value.startPrice) {
  alert(`还差￥${(business.value.startPrice - parseFloat(totalPrice.value)).toFixed(2)}起送`)
    return
  }
  
  if (totalQuantity.value === 0) {
    alert('请先选择商品')
    return
  }
  
// 重新检查登录状态和用户信息
const isLoggedIn = localStorage.getItem('isLoggedIn')
let userInfo = getSessionStorage("user") || getLocalStorage("userInfo")

console.log('=== 登录状态检查 ===')
console.log('isLoggedIn:', isLoggedIn)
console.log('userInfo:', userInfo)

if (isLoggedIn !== 'true') {
    alert('请先登录')
    router.push('/login')
    return
  }

// 如果用户信息为空，创建测试用户信息
if (!userInfo) {
  console.log('用户信息为空，创建测试用户信息')
  userInfo = {
    id: 'test_user',
    userId: 'test_user',
    username: 'test_user',
    name: '测试用户',
    role: 'user'
  }
  
  // 保存测试用户信息
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
  sessionStorage.setItem('user', JSON.stringify(userInfo))
  console.log('已创建并保存测试用户信息:', userInfo)
}

// 更新用户信息
user.value = userInfo
console.log('✅ 用户已登录，用户信息:', user.value)
  
  console.log('准备跳转到订单页面...')
  
// 收集购物车数据
const cartItems = foodArr.value.filter(item => item.quantity > 0)
console.log('购物车商品:', cartItems)

if (cartItems.length === 0) {
  alert('购物车为空，请先添加商品')
  return
}

// 将购物车数据保存到localStorage，供订单页面使用
const cartData = {
  businessId: businessId,
  business: business.value,
  items: cartItems,
  totalPrice: totalPrice.value,
  totalQuantity: totalQuantity.value,
  selectedRedPacket: selectedRedPacket.value,
  timestamp: Date.now()
}

localStorage.setItem('currentCart', JSON.stringify(cartData))
console.log('购物车数据已保存:', cartData)
  
  // 跳转到订单页面
  router.push({
    path: '/orders',
    query: { 
    businessId: businessId,
    fromCart: 'true'
    }
  })
}
</script>

<style scoped>
    /********** 总容器 **********/
    .wrapper {
        width: 100%;
        height: 100%;
    }

    /********** header **********/
    .wrapper header {
        width: 100%;
        height: 12vw;
        background-color: #0097FF;
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

    /***********LOGO***********/
    .wrapper .business-logo {
        width: 100%;
        height: 35vw;
        margin-top: 12vw;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .wrapper .business-logo img {
        width: 40vw;
        height: 30vw;
        border-radius: 5px;
        object-fit: cover;
    }

    /*******商家信息*********/
    .wrapper .business-info {
        width: 100%;
        height: 20vw;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    .wrapper .business-info h1 {
        font-size: 5vw;
    }

    .wrapper .business-info p {
        font-size: 3vw;
        color: #666;
        margin-top: 1vw;
    }

    /********食品列表********/
    .wrapper .food {
        width: 100%;
        margin-bottom: 14vw;
    }

    .wrapper .food li {
        width: 100%;
        box-sizing: border-box;
        padding: 2.5vw;
        user-select: none;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1px solid #eee;
    }

    .wrapper .food li .food-left {
        display: flex;
        align-items: center;
        flex: 1;
    }

    .wrapper .food li .food-left img {
        width: 20vw;
        height: 20vw;
        border-radius: 5px;
        object-fit: cover;
    }

    .wrapper .food li .food-left .food-left-info {
        margin-left: 3vw;
        flex: 1;
    }

    .wrapper .food li .food-left .food-left-info h3 {
        font-size: 3.8vw;
        color: #555;
        margin-bottom: 1vw;
    }

    .wrapper .food li .food-left .food-left-info p {
        font-size: 3vw;
        color: #888;
        margin-top: 2vw;
    }

    .wrapper .food li .food-left .food-left-info p:last-child {
        color: #ff6600;
        font-weight: bold;
        font-size: 3.5vw;
    }

    .wrapper .food li .food-right {
        width: 16vw;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .wrapper .food li .food-right .fa-minus-circle {
        font-size: 5.5vw;
        color: #999;
        cursor: pointer;
        transition: color 0.2s ease;
    }

    .wrapper .food li .food-right .fa-minus-circle:hover {
        color: #666;
    }

    .wrapper .food li .food-right p {
        font-size: 3.6vw;
        color: #333;
        min-width: 4vw;
        text-align: center;
    }

    .wrapper .food li .food-right .fa-plus-circle {
        font-size: 5.5vw;
        color: #0097EF;
        cursor: pointer;
        transition: color 0.2s ease;
    }

    .wrapper .food li .food-right .fa-plus-circle:hover {
        color: #007ACC;
    }

    /*********购物车********/
    .wrapper .cart {
        width: 100%;
        height: 14vw;
        position: fixed;
        left: 0;
        bottom: 0;
        display: flex;
    }

    .wrapper .cart .cart-left {
        flex: 2;
        background-color: #505051;
        display: flex;
    }

    .wrapper .cart .cart-left .cart-left-icon {
        width: 16vw;
        height: 16vw;
        box-sizing: border-box;
        border: solid 1.6vw #444;
        border-radius: 8vw;
        background-color: #3190E8;
        font-size: 7vw;
        color: #fff;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: -4vw;
        margin-left: 3vw;
        position: relative;
    }

    .wrapper .cart .cart-left .cart-left-icon-quantity {
        width: 5vw;
        height: 5vw;
        border-radius: 2.5vw;
        background-color: red;
        color: #fff;
        font-size: 3.6vw;
        display: flex;
        justify-content: center;
        align-items: center;
        position: absolute;
        right: -1.5vw;
        top: -1.5vw;
    }

    .wrapper .cart .cart-left .cart-left-info {
        margin-left: 3vw;
        display: flex;
        flex-direction: column;
        justify-content: center;
    }

    .wrapper .cart .cart-left .cart-left-info p:first-child {
        font-size: 4.5vw;
        color: #fff;
        margin-bottom: 1vw;
    }

    .wrapper .cart .cart-left .cart-left-info p:last-child {
        font-size: 2.8vw;
        color: #AAA;
    }

  .redpacket-discount {
      display: flex;
      align-items: center;
      gap: 1vw;
      font-size: 2.6vw;
      color: #ff4757;
      background: rgba(255, 71, 87, 0.1);
      padding: 1vw 2vw;
      border-radius: 1.5vw;
      margin: 1vw 0;
      border: 1px solid rgba(255, 71, 87, 0.2);
  }

  .redpacket-discount i {
      font-size: 2.4vw;
      color: #ff4757;
  }

    .wrapper .cart .cart-right {
        flex: 1;
    }

    /***达到配送费***/
    .wrapper .cart .cart-right .cart-right-item {
        width: 50%;
        height: 100%;
        background-color: #38CA73;
        color: #fff;
        font-size: 4.5vw;
        font-weight: 700;
        user-select: none;
        cursor: pointer;
        margin-left: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        transition: background-color 0.2s ease;
    }

    .wrapper .cart .cart-right .cart-right-item:hover {
        background-color: #32B865;
    }

  /********** 加载状态样式 **********/
  .loading-container {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      height: 50vh;
      margin-top: 12vw;
  }

  .loading-spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #f3f3f3;
      border-top: 4px solid #0097FF;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin-bottom: 20px;
  }

  @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
  }

  .loading-container p {
      font-size: 4vw;
      color: #666;
  }

  /********** 错误状态样式 **********/
  .error-container {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      height: 50vh;
      margin-top: 12vw;
      padding: 20px;
  }

  .error-container p {
      font-size: 4vw;
      color: #ff4444;
      margin-bottom: 20px;
      text-align: center;
  }

  .retry-btn {
      background-color: #0097FF;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 5px;
      font-size: 3.5vw;
      cursor: pointer;
      transition: background-color 0.2s ease;
  }

  .retry-btn:hover {
      background-color: #007ACC;
  }

  /********** 空状态样式 **********/
  .empty-state {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 30vh;
      margin: 20px;
  }

  .empty-state p {
      font-size: 4vw;
      color: #999;
      text-align: center;
  }

  /********** 演示数据提示样式 **********/
  .demo-notice {
      background-color: #fff3cd;
      border: 1px solid #ffeaa7;
      border-radius: 5px;
      padding: 10px;
      margin: 10px;
      text-align: center;
  }

  .demo-notice p {
      font-size: 3.5vw;
      color: #856404;
      margin: 0;
  }
</style>