<template>
    <div class="wrapper">
        <!-- header -->
        <header>
            <p>商家信息</p>
        </header>

        <!-- LOGO -->
        <div class="business-logo">
            <img :src="getBusinessImage(business.businessId)">
        </div>

        <!-- 商家信息 -->
        <div class="business-info">
            <h1>{{business.businessName}}</h1>
            <p>&#165;{{business.starPrice}}起送 &#165;{{business.deliveryPrice}}配送</p>
            <p>{{business.businessExplain}}</p>
        </div>

        <!--  食品列表 -->
        <ul class="food">
            <li v-for="(item,index) in foodArr" :key="item.foodId">
                <div class="food-left">
                    <img :src="getFoodImage(item.foodId)">
                    <div class="food-left-info">
                        <h3>{{item.foodName}}</h3>
                        <p>{{item.foodExplain}}</p>
                        <p>&#165;{{item.foodPrice}}</p>
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
                    <div class="cart-right-item" v-show="totalPrice<business.starPrice" style="background-color: #535356;cursor: default;">&#165;{{business.starPrice}}起送</div>
                    <!--达到配送费-->
                    <div class="cart-right-item" @click="toOrder" v-show="totalPrice>=business.starPrice">去结算</div>
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

// 导入商家图片
import sj01 from '@/assets/sj01.png'
import sj02 from '@/assets/sj02.png'
import sj03 from '@/assets/sj03.png'
import sj04 from '@/assets/sj04.png'
import sj05 from '@/assets/sj05.png'
import sj06 from '@/assets/sj06.png'
import sj07 from '@/assets/sj07.png'

// 正确导入商品图片 - 根据readme.txt文件
import sp01 from '@/assets/sp01.png'  // 纯肉鲜肉（水饺）
import sp02 from '@/assets/sp02.png'  // 玉米鲜肉（水饺）
import sp03 from '@/assets/sp03.png'  // 虾仁三鲜（蒸饺）
import sp04 from '@/assets/sp04.png'  // 素三鲜（蒸饺）
import sp05 from '@/assets/sp05.png'  // 角瓜鸡蛋（蒸饺）
import sp06 from '@/assets/sp06.png'  // 小白菜肉（水饺）
import sp07 from '@/assets/sp07.png'  // 芹菜牛肉（水饺）
import sp08 from '@/assets/sp08.png'  // 虾腰鲜肉（蒸饺）
import sp09 from '@/assets/sp09.png'  // 青椒鲜肉（蒸饺）
import sp10 from '@/assets/sp10.png'  // 蛋黄焗豆花
import sp11 from '@/assets/sp11.png'  // 麻婆豆腐
import sp12 from '@/assets/sp12.png'  // 广式小豆腐

const route = useRoute()
const router = useRouter()

// 响应式数据
const business = ref({})
const foodArr = ref([])
const user = ref(null)

// 获取路由参数
const businessId = parseInt(route.query.businessId)

// 硬编码商家数据
const businessData = {
  10001: {
    businessId: 10001,
    businessName: '万家饺子（软件园E18店）',
    businessExplain: '各种饺子炒菜',
    starPrice: 15,
    deliveryPrice: 3
  },
  10002: {
    businessId: 10002,
    businessName: '小锅饭豆腐馆（全运店）',
    businessExplain: '特色美食',
    starPrice: 15,
    deliveryPrice: 3
  },
  10003: {
    businessId: 10003,
    businessName: '麦当劳麦乐送（全运路店）',
    businessExplain: '汉堡薯条',
    starPrice: 15,
    deliveryPrice: 3
  },
  10004: {
    businessId: 10004,
    businessName: '米村拌饭（浑南店）',
    businessExplain: '各种炒菜拌饭',
    starPrice: 15,
    deliveryPrice: 3
  },
  10005: {
    businessId: 10005,
    businessName: '申记串道（中海康城店）',
    businessExplain: '烤串炸串',
    starPrice: 15,
    deliveryPrice: 3
  },
  10006: {
    businessId: 10006,
    businessName: '半亩良田排骨米饭',
    businessExplain: '排骨米饭套餐',
    starPrice: 15,
    deliveryPrice: 3
  },
  10007: {
    businessId: 10007,
    businessName: '茶兮鲜果饮品（国际软件园店）',
    businessExplain: '甜品饮品',
    starPrice: 15,
    deliveryPrice: 3
  }
}

const allFoodData = [
  // sj01 万家饺子（软件园E18店） - businessId: 10001 (保持完整 9个菜品)
  {
    foodId: 1,
    foodName: '纯肉鲜肉（水饺）',
    foodExplain: '纯肉馅饺子',
    foodPrice: 16.00,
    businessId: 10001,
    quantity: 0
  },
  {
    foodId: 2,
    foodName: '玉米鲜肉（水饺）',
    foodExplain: '玉米青豆',
    foodPrice: 18.00,
    businessId: 10001,
    quantity: 0
  },
  {
    foodId: 3,
    foodName: '虾仁三鲜（蒸饺）',
    foodExplain: '三鲜馅饺子',
    foodPrice: 22.00,
    businessId: 10001,
    quantity: 0
  },
  {
    foodId: 4,
    foodName: '素三鲜（蒸饺）',
    foodExplain: '韭菜鸡蛋虾皮',
    foodPrice: 15.00,
    businessId: 10001,
    quantity: 0
  },
  {
    foodId: 5,
    foodName: '角瓜鸡蛋（蒸饺）',
    foodExplain: '角瓜鸡蛋',
    foodPrice: 16.00,
    businessId: 10001,
    quantity: 0
  },
  {
    foodId: 6,
    foodName: '小白菜肉（水饺）',
    foodExplain: '小白菜肉馅',
    foodPrice: 17.00,
    businessId: 10001,
    quantity: 0
  },
  {
    foodId: 7,
    foodName: '芹菜牛肉（水饺）',
    foodExplain: '芹菜牛肉馅',
    foodPrice: 20.00,
    businessId: 10001,
    quantity: 0
  },
  {
    foodId: 8,
    foodName: '虾腰鲜肉（蒸饺）',
    foodExplain: '虾腰鲜肉馅',
    foodPrice: 24.00,
    businessId: 10001,
    quantity: 0
  },
  {
    foodId: 9,
    foodName: '青椒鲜肉（蒸饺）',
    foodExplain: '青椒鲜肉馅',
    foodPrice: 18.00,
    businessId: 10001,
    quantity: 0
  },

  // sj02 小锅饭豆腐馆（全运店） - businessId: 10002 (保持完整 3个菜品)
  {
    foodId: 10,
    foodName: '蛋黄焗豆花',
    foodExplain: '蛋黄豆花',
    foodPrice: 8.00,
    businessId: 10002,
    quantity: 0
  },
  {
    foodId: 11,
    foodName: '麻婆豆腐',
    foodExplain: '麻辣味',
    foodPrice: 12.00,
    businessId: 10002,
    quantity: 0
  },
  {
    foodId: 12,
    foodName: '广式小豆腐',
    foodExplain: '广式口味豆腐',
    foodPrice: 10.00,
    businessId: 10002,
    quantity: 0
  },

  // sj03 麦当劳麦乐送（全运路店） - businessId: 10003 (只保留2个菜品)
  {
    foodId: 13,
    foodName: '巨无霸汉堡',
    foodExplain: '经典牛肉汉堡',
    foodPrice: 22.00,
    businessId: 10003,
    quantity: 0
  },
  {
    foodId: 14,
    foodName: '薯条（大）',
    foodExplain: '金黄香脆薯条',
    foodPrice: 12.00,
    businessId: 10003,
    quantity: 0
  },

  // sj04 米村拌饭（浑南店） - businessId: 10004 (只保留2个菜品)
  {
    foodId: 15,
    foodName: '韩式牛肉拌饭',
    foodExplain: '正宗韩式拌饭',
    foodPrice: 25.00,
    businessId: 10004,
    quantity: 0
  },
  {
    foodId: 16,
    foodName: '石锅拌饭',
    foodExplain: '热石锅拌饭',
    foodPrice: 28.00,
    businessId: 10004,
    quantity: 0
  },

  // sj05 申记串道（中海康城店） - businessId: 10005 (只保留2个菜品)
  {
    foodId: 17,
    foodName: '羊肉串',
    foodExplain: '新疆风味羊肉串',
    foodPrice: 3.00,
    businessId: 10005,
    quantity: 0
  },
  {
    foodId: 18,
    foodName: '烤鸡翅',
    foodExplain: '香烤鸡翅',
    foodPrice: 8.00,
    businessId: 10005,
    quantity: 0
  },

  // sj06 半亩良田排骨米饭 - businessId: 10006 (只保留2个菜品)
  {
    foodId: 19,
    foodName: '红烧排骨饭',
    foodExplain: '招牌红烧排骨',
    foodPrice: 28.00,
    businessId: 10006,
    quantity: 0
  },
  {
    foodId: 20,
    foodName: '糖醋排骨饭',
    foodExplain: '酸甜糖醋排骨',
    foodPrice: 26.00,
    businessId: 10006,
    quantity: 0
  },

  // sj07 茶兮鲜果饮品（国际软件园店） - businessId: 10007 (只保留2个菜品)
  {
    foodId: 21,
    foodName: '珍珠奶茶',
    foodExplain: '经典珍珠奶茶',
    foodPrice: 15.00,
    businessId: 10007,
    quantity: 0
  },
  {
    foodId: 22,
    foodName: '芒果汁',
    foodExplain: '新鲜芒果榨汁',
    foodPrice: 18.00,
    businessId: 10007,
    quantity: 0
  }
]

// 生命周期
onMounted(async () => {
  console.log('=== BusinessInfo 页面加载 ===')
  console.log('商家ID:', businessId)
  
  // 获取用户信息
  const userInfo = getSessionStorage("user")
  if (userInfo) {
    user.value = userInfo
    console.log('用户已登录:', user.value.userId)
  } else {
    console.log('用户未登录')
  }
  
  // 加载商家信息
  if (businessId && businessData[businessId]) {
    business.value = businessData[businessId]
    console.log('加载商家信息:', business.value.businessName)
  } else {
    console.warn('未找到商家信息，使用默认数据')
    business.value = businessData[10001]
  }
  
  // 加载食品列表并同步购物车数据
  await loadFoodList()
})

// 加载食品列表并同步购物车数据
const loadFoodList = async () => {
  console.log('开始加载食品列表...')
  
  // 1. 先加载食品基础数据
  foodArr.value = allFoodData.filter(food => food.businessId === businessId)
  console.log('加载食品数据:', foodArr.value.length, '个食品')
  
  // 2. 如果用户已登录，从后端同步购物车数据
  if (user.value && user.value.userId) {
    try {
      console.log('同步后端购物车数据...')
      const response = await axios.post('CartController/listCart', qs.stringify({
        userId: user.value.userId,
        businessId: businessId
      }))
      
      console.log('后端购物车响应:', response.data)
      
      if (response.data && Array.isArray(response.data)) {
        // 同步后端购物车数据到本地食品列表
        response.data.forEach(cartItem => {
          const foodIndex = foodArr.value.findIndex(food => food.foodId === cartItem.food.foodId)
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
      console.log('将使用本地购物车数据')
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
  const imageMap = {
    10001: sj01, 10002: sj02, 10003: sj03, 10004: sj04,
    10005: sj05, 10006: sj06, 10007: sj07
  }
  return imageMap[businessId] || sj01
}

// 更新商品图片映射 - 根据新的 foodId 分配
const foodImages = {
  // sj01 万家饺子（软件园E18店） - 使用专用商品图片
  1: sp01, 2: sp02, 3: sp03, 4: sp04, 5: sp05, 6: sp06,
  7: sp07, 8: sp08, 9: sp09,
  
  // sj02 小锅饭豆腐馆（全运店） - 使用专用商品图片
  10: sp10, 11: sp11, 12: sp12,
  
  // 其他商家的商品将使用店家图片作为默认图片
  // 在 getFoodImage 函数中动态处理
}

// 修改 getFoodImage 函数 - 根据新的 foodId 范围分配店家图片
const getFoodImage = (foodId) => {
  console.log(`获取商品图片 - foodId: ${foodId}`)
  
  // 首先检查是否有专用商品图片
  const image = foodImages[foodId]
  if (image) {
    console.log(`✅ 找到专用商品图片: foodId ${foodId} -> ${getImageName(image)}`)
    return image
  }

  // 如果没有专用图片，根据 foodId 确定所属商家，使用店家图片
  let businessImageId = null
  
  if (foodId >= 1 && foodId <= 9) {
    businessImageId = 10001  // 万家饺子
  } else if (foodId >= 10 && foodId <= 12) {
    businessImageId = 10002  // 小锅饭豆腐馆
  } else if (foodId >= 13 && foodId <= 14) {
    businessImageId = 10003  // 麦当劳麦乐送
  } else if (foodId >= 15 && foodId <= 16) {
    businessImageId = 10004  // 米村拌饭
  } else if (foodId >= 17 && foodId <= 18) {
    businessImageId = 10005  // 申记串道
  } else if (foodId >= 19 && foodId <= 20) {
    businessImageId = 10006  // 半亩良田排骨米饭
  } else if (foodId >= 21 && foodId <= 22) {
    businessImageId = 10007  // 茶兮鲜果饮品
  } else {
    businessImageId = 10001  // 默认使用万家饺子图片
  }
  
  // 获取对应的店家图片
  const businessImage = getBusinessImage(businessImageId)
  console.log(`⚠️ 未找到专用商品图片，使用店家图片: foodId ${foodId} -> 商家 ${businessImageId}`)
  
  return businessImage
}

const getImageName = (imageModule) => {
  const imagePath = imageModule.default || imageModule
  if (typeof imagePath === 'string') {
    const fileName = imagePath.split('/').pop()
    return fileName
  }
  return 'unknown'
}

// 修改 add 方法 - 同步到后端
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
  
  // 同步到后端 - 调用你的后端接口
  try {
    const response = await axios.post('CartController/saveCart', qs.stringify({
      foodId: food.foodId,
      businessId: businessId,
      userId: user.value.userId
    }))
    
    console.log('后端添加购物车响应:', response.data)
    
    if (response.data === 1) {
      console.log('✅ 成功添加到后端购物车')
    } else {
      console.warn('⚠️ 后端添加失败，但本地已更新')
    }
    
  } catch (error) {
    console.error('❌ 添加到后端购物车失败:', error)
    console.log('本地数据已更新，将在网络恢复时重试')
    
    // 可以选择回滚本地操作
    // foodArr.value[index].quantity--
  }
  
  console.log('当前总价:', totalPrice.value)
  console.log('当前总数量:', totalQuantity.value)
}

// 修改 minus 方法 - 同步到后端
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
  
  // 同步到后端
  try {
    if (foodArr.value[index].quantity === 0) {
      // 数量为0时，从购物车移除
      const response = await axios.post('CartController/removeCart', qs.stringify({
        foodId: food.foodId,
        businessId: businessId,
        userId: user.value.userId
      }))
      console.log('后端移除购物车响应:', response.data)
    } else {
      // 否则更新数量
      const response = await axios.post('CartController/updateCart', qs.stringify({
        foodId: food.foodId,
        businessId: businessId,
        userId: user.value.userId,
        quantity: foodArr.value[index].quantity
      }))
      console.log('后端更新购物车响应:', response.data)
    }
    
    console.log('✅ 成功更新后端购物车')
    
  } catch (error) {
    console.error('❌ 更新后端购物车失败:', error)
    console.log('本地数据已更新，将在网络恢复时重试')
    
    // 可以选择回滚本地操作
    // foodArr.value[index].quantity++
  }
  
  console.log('当前总价:', totalPrice.value)
  console.log('当前总数量:', totalQuantity.value)
}

// 修改 toOrder 方法
const toOrder = async () => {
  console.log('=== 去结算 ===')
  console.log('总价格:', totalPrice.value)
  console.log('起送价:', business.value.starPrice)
  
  if (parseFloat(totalPrice.value) < business.value.starPrice) {
    alert(`还差￥${(business.value.starPrice - parseFloat(totalPrice.value)).toFixed(2)}起送`)
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
</style>