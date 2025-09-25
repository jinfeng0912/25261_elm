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
import { getSessionStorage } from '../common.js'
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

// 获取路由参数
const businessId = parseInt(route.query.businessId)

// 商家数据现在完全从后端API获取，不再使用硬编码数据

// 商品数据现在完全从后端API获取，不再使用硬编码数据

// 生命周期
onMounted(async () => {
console.log('=== BusinessInfo 页面加载 ===')
console.log('商家ID:', businessId)

try {


  loading.value = true
  error.value = null
  
  // 获取用户信息
  const userInfo = getSessionStorage("user")
  if (userInfo) {
    user.value = userInfo
    console.log('用户已登录:', user.value.userId)
  } else {
    console.log('用户未登录')
  }
  
  // 从后端加载商家信息
  await loadBusinessInfo()
  
  // 加载食品列表并同步购物车数据
  await loadFoodList()
  
} catch (err) {
  console.error('页面加载失败:', err)
  error.value = '页面加载失败，请刷新重试'
} finally {
  loading.value = false
}
})

// 重试加载
const retryLoad = () => {
console.log('🔄 用户点击重试按钮')
error.value = null
loading.value = true
// 重新加载数据
loadBusinessInfo()
loadFoodList()
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
      quantity: 0  // 初始数量为0
    }))
    console.log('✅ 成功从后端加载商品数据:', foodArr.value.length, '个商品')
    
} else {
    console.error('❌ 后端获取商品列表失败:', response.data?.message || '未知错误')
    error.value = '获取商品列表失败，请刷新重试'
    return
  }
  
} catch (error) {
  console.error('❌ 获取商品列表失败:', error)
  error.value = '网络连接失败，请检查网络后重试'
}

// 2. 如果用户已登录，从后端同步购物车数据
if (user.value && user.value.userId) {
  try {
    console.log('同步后端购物车数据...')
    // 使用GET请求获取购物车列表
    const response = await axios.get('/api/carts')
    
    console.log('后端购物车响应:', response.data)
    
    if (response.data && response.data.code === "OK" && Array.isArray(response.data.data)) {
      // 同步后端购物车数据到本地食品列表
      response.data.data.forEach(cartItem => {
        const foodIndex = foodArr.value.findIndex(food => food.foodId === cartItem.food.id)
        if (foodIndex !== -1) {
          foodArr.value[foodIndex].quantity = cartItem.quantity
          console.log(`同步商品 ${cartItem.food.foodName} 数量: ${cartItem.quantity}`)
        }
      })
      console.log('购物车数据同步完成')
    } else {
      console.log('后端购物车为空或数据格式错误')
    }
    
  } catch (error) {
    console.error('同步购物车数据失败:', error)
    // 购物车同步失败不影响页面正常显示，只记录错误
  }
}
}

// 计算属性
const totalPrice = computed(() => {
let total = 0
for (let item of foodArr.value) {
  total += item.foodPrice * item.quantity
}
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

// 修改 add 方法 - 支持演示数据和后端数据
const add = async (index) => {
// 检查登录状态
if (!user.value) {
  console.log('用户未登录，跳转到登录页')
  alert('请先登录后再添加商品到购物车')
  router.push('/login')
  return
}

console.log('=== 添加食品到购物车 ===')
const food = foodArr.value[index]
console.log('食品:', food.foodName)
console.log('价格:', food.foodPrice)
console.log('食品ID:', food.foodId)
console.log('商家ID:', businessId)
console.log('用户ID:', user.value.userId)

// 先更新本地数量（立即反馈）
foodArr.value[index].quantity++
console.log('本地数量更新为:', foodArr.value[index].quantity)

// 检查是否是演示数据（foodId >= 1000）
if (food.foodId >= 1000) {
  console.log('📝 这是演示数据，只更新本地状态')
  console.log('💡 提示：演示数据不会同步到后端购物车')
} else {
// 同步到后端 - 调用你的后端接口
try {
    const response = await axios.post('/api/carts', {
      food: {
        id: food.foodId
      },
      quantity: 1
    })
  
  console.log('后端添加购物车响应:', response.data)
  
    if (response.data && response.data.code === "OK") {
    console.log('✅ 成功添加到后端购物车')
  } else {
    console.warn('⚠️ 后端添加失败，但本地已更新')
  }
  
} catch (error) {
  console.error('❌ 添加到后端购物车失败:', error)
  console.log('本地数据已更新，将在网络恢复时重试')
  }
}

console.log('当前总价:', totalPrice.value)
console.log('当前总数量:', totalQuantity.value)
}

// 修改 minus 方法 - 支持演示数据和后端数据
const minus = async (index) => {
// 检查登录状态
if (!user.value) {
  console.log('用户未登录，跳转到登录页')
  alert('请先登录后再操作购物车')
  router.push('/login')
  return
}

if (foodArr.value[index].quantity <= 0) {
  return
}

console.log('=== 减少购物车食品 ===')
const food = foodArr.value[index]
console.log('食品:', food.foodName)
console.log('当前数量:', food.quantity)

// 先更新本地数量（立即反馈）
foodArr.value[index].quantity--
console.log('本地数量更新为:', foodArr.value[index].quantity)

// 检查是否是演示数据（foodId >= 1000）
if (food.foodId >= 1000) {
  console.log('📝 这是演示数据，只更新本地状态')
  console.log('💡 提示：演示数据不会同步到后端购物车')
} else {
// 同步到后端
try {
    // 先获取购物车中该商品的cartId
    const cartResponse = await axios.get('/api/carts')
    if (cartResponse.data && cartResponse.data.code === "OK") {
      const cartItem = cartResponse.data.data.find(item => item.food.id === food.foodId)
      
      if (cartItem) {
  if (foodArr.value[index].quantity === 0) {
    // 数量为0时，从购物车移除
          const response = await axios.delete(`/api/carts/${cartItem.id}`)
    console.log('后端移除购物车响应:', response.data)
  } else {
    // 否则更新数量
          const response = await axios.put(`/api/carts/${cartItem.id}`, {
      quantity: foodArr.value[index].quantity
          })
    console.log('后端更新购物车响应:', response.data)
  }
  console.log('✅ 成功更新后端购物车')
      } else {
        console.warn('⚠️ 未找到购物车中的商品')
      }
    }
  
} catch (error) {
  console.error('❌ 更新后端购物车失败:', error)
  console.log('本地数据已更新，将在网络恢复时重试')
  }
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

// 检查用户是否登录
if (!user.value || !user.value.userId) {
  alert('请先登录')
  router.push('/login')
  return
}

console.log('准备跳转到订单页面...')

// 等待一下确保所有购物车操作都已同步到后端
await new Promise(resolve => setTimeout(resolve, 500))

// 跳转到订单页面
router.push({
  path: '/orders',
  query: { 
    businessId: businessId
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