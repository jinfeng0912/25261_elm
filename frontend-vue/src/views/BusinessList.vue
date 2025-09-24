<template>
  <div class="wrapper">

      <!-- header -->
      <header>
          <p>{{ getHeaderTitle() }}</p>
      </header>

      <!-- 商家列表 -->
      <ul class="business">
          <li v-for="item in businessArr" :key="item.businessId" @click="toBusinessInfo(item.businessId)">
              <div class="business-img">
                  <img :src="item.businessImg">
                  <div class="business-img-quantity" v-show="item.quantity>0">{{item.quantity}}</div>
              </div>
              <div class="business-info">
                  <h3>{{item.businessName}}</h3>
                  <p>&#165;{{item.starPrice}}起送 | &#165;{{item.deliveryPrice}}配送</p>
                  <p>{{item.businessExplain}}</p>
              </div>
          </li>
      </ul>

      <!-- 空状态 -->
<div v-if="businessArr.length === 0" class="empty">
    <div v-if="isFilteredResult">
        <p>没有符合筛选条件的商家</p>
        <button class="retry-btn" @click="goToFilter">重新筛选</button>
        <button class="show-all-btn" @click="showAllBusinesses">查看所有商家</button>
    </div>
    <div v-else>
        <p>该分类暂无商家</p>
    </div>
</div>

      <!-- 底部菜单 -->
      <Footer></Footer>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Footer from '../components/Footer.vue'

// 导入商家图片
import sj01 from '@/assets/sj01.png'
import sj02 from '@/assets/sj02.png'
import sj03 from '@/assets/sj03.png'
import sj04 from '@/assets/sj04.png'
import sj05 from '@/assets/sj05.png'
import sj06 from '@/assets/sj06.png'
import sj07 from '@/assets/sj07.png'

const route = useRoute()
const router = useRouter()

// 响应式数据
const businessArr = ref([])
const orderTypeId = route.query.orderTypeId

// 硬编码商家数据（按食物类型归类）
const allBusinessData = [
// 类型1 - 美食
{
  businessId: 10001,
  businessName: '万家饺子（软件园E18店）',
  businessExplain: '各种饺子炒菜',
  businessImg: sj01,
  starPrice: 18.00,
  deliveryPrice: 3.50,
  orderTypeId: 1,
  quantity: 0
},
{
  businessId: 10002,
  businessName: '小锅饭豆腐馆（全运店）',
  businessExplain: '小锅套餐特色美食',
  businessImg: sj02,
  starPrice: 15.00,
  deliveryPrice: 3.00,
  orderTypeId: 1,
  quantity: 0
},
{
  businessId: 10004,
  businessName: '米村拌饭（浑南店）',
  businessExplain: '各种炒菜拌饭',
  businessImg: sj04,
  starPrice: 18.00,
  deliveryPrice: 3.00,
  orderTypeId: 1,
  quantity: 0
},
{
  businessId: 10006,
  businessName: '半亩良田排骨米饭',
  businessExplain: '排骨米饭套餐',
  businessImg: sj06,
  starPrice: 22.00,
  deliveryPrice: 4.00,
  orderTypeId: 1,
  quantity: 0
},

// 类型2 - 早餐
{
  businessId: 10008,
  businessName: '晨光早餐铺',
  businessExplain: '包子粥类早餐',
  businessImg: sj01,
  starPrice: 8.00,
  deliveryPrice: 2.00,
  orderTypeId: 2,
  quantity: 0
},
{
  businessId: 10009,
  businessName: '老北京豆浆油条',
  businessExplain: '传统早餐',
  businessImg: sj02,
  starPrice: 6.00,
  deliveryPrice: 2.00,
  orderTypeId: 2,
  quantity: 0
},

// 类型3 - 跑腿代购
{
  businessId: 10010,
  businessName: '全能跑腿服务',
  businessExplain: '代买代送各种商品',
  businessImg: sj03,
  starPrice: 10.00,
  deliveryPrice: 5.00,
  orderTypeId: 3,
  quantity: 0
},

// 类型4 - 汉堡披萨
{
  businessId: 10003,
  businessName: '麦当劳麦乐送（全运路店）',
  businessExplain: '汉堡薯条套餐',
  businessImg: sj03,
  starPrice: 25.00,
  deliveryPrice: 4.00,
  orderTypeId: 4,
  quantity: 0
},
{
  businessId: 10011,
  businessName: '必胜客（软件园店）',
  businessExplain: '意式披萨西餐',
  businessImg: sj04,
  starPrice: 35.00,
  deliveryPrice: 6.00,
  orderTypeId: 4,
  quantity: 0
},

// 类型5 - 甜品饮品
{
  businessId: 10007,
  businessName: '茶兮鲜果饮品（国际软件园店）',
  businessExplain: '甜品饮品奶茶',
  businessImg: sj07,
  starPrice: 12.00,
  deliveryPrice: 3.00,
  orderTypeId: 5,
  quantity: 0
},
{
  businessId: 10012,
  businessName: '星巴克咖啡',
  businessExplain: '精品咖啡甜品',
  businessImg: sj05,
  starPrice: 30.00,
  deliveryPrice: 5.00,
  orderTypeId: 5,
  quantity: 0
},

// 类型6 - 速食简餐
{
  businessId: 10013,
  businessName: '真功夫中式快餐',
  businessExplain: '营养中式简餐',
  businessImg: sj06,
  starPrice: 16.00,
  deliveryPrice: 3.00,
  orderTypeId: 6,
  quantity: 0
},
{
  businessId: 10014,
  businessName: '吉野家',
  businessExplain: '日式牛肉饭',
  businessImg: sj07,
  starPrice: 18.00,
  deliveryPrice: 3.50,
  orderTypeId: 6,
  quantity: 0
},

// 类型7 - 地方小吃
{
  businessId: 10015,
  businessName: '新疆大盘鸡',
  businessExplain: '正宗新疆风味',
  businessImg: sj01,
  starPrice: 28.00,
  deliveryPrice: 4.00,
  orderTypeId: 7,
  quantity: 0
},
{
  businessId: 10016,
  businessName: '兰州拉面馆',
  businessExplain: '正宗兰州牛肉面',
  businessImg: sj02,
  starPrice: 15.00,
  deliveryPrice: 3.00,
  orderTypeId: 7,
  quantity: 0
},

// 类型8 - 米粉面馆
{
  businessId: 10017,
  businessName: '重庆小面',
  businessExplain: '地道重庆小面',
  businessImg: sj03,
  starPrice: 12.00,
  deliveryPrice: 2.50,
  orderTypeId: 8,
  quantity: 0
},
{
  businessId: 10018,
  businessName: '桂林米粉',
  businessExplain: '正宗桂林米粉',
  businessImg: sj04,
  starPrice: 10.00,
  deliveryPrice: 2.50,
  orderTypeId: 8,
  quantity: 0
},

// 类型9 - 包子粥铺
{
  businessId: 10019,
  businessName: '庆丰包子铺',
  businessExplain: '传统包子粥品',
  businessImg: sj05,
  starPrice: 8.00,
  deliveryPrice: 2.00,
  orderTypeId: 9,
  quantity: 0
},
{
  businessId: 10020,
  businessName: '小杨生煎',
  businessExplain: '上海生煎包',
  businessImg: sj06,
  starPrice: 12.00,
  deliveryPrice: 3.00,
  orderTypeId: 9,
  quantity: 0
},

// 类型10 - 炸鸡炸串
{
  businessId: 10005,
  businessName: '申记串道（中海康城店）',
  businessExplain: '烤串炸串烧烤',
  businessImg: sj05,
  starPrice: 20.00,
  deliveryPrice: 4.00,
  orderTypeId: 10,
  quantity: 0
},
{
  businessId: 10021,
  businessName: 'KFC肯德基',
  businessExplain: '炸鸡汉堡套餐',
  businessImg: sj07,
  starPrice: 22.00,
  deliveryPrice: 4.00,
  orderTypeId: 10,
  quantity: 0
}
]

// 食物类型名称映射
const foodTypeNames = {
1: '美食',
2: '早餐', 
3: '跑腿代购',
4: '汉堡披萨',
5: '甜品饮品',
6: '速食简餐',
7: '地方小吃',
8: '米粉面馆',
9: '包子粥铺',
10: '炸鸡炸串'
}

// 生命周期
onMounted(() => {
  // 检查是否是筛选结果页面
  if (route.query.filtered === 'true') {
    loadFilteredBusinessList()
  } else {
    loadBusinessList()
  }
})
//筛选
const isFilteredResult = ref(false)
const filterConditions = ref(null)
// 获取页面标题 筛选
const getHeaderTitle = () => {
  if (isFilteredResult.value) {
    return `筛选结果 (${businessArr.value.length}家)`
  }
  
  const typeId = parseInt(orderTypeId)
  return foodTypeNames[typeId] || '商家列表'
}
// 加载筛选结果
const loadFilteredBusinessList = () => {
  console.log('=== 加载筛选结果 ===')
  
  try {
    const filteredData = localStorage.getItem('filteredBusinesses')
    const filterData = localStorage.getItem('filterConditions')
    
    if (filteredData) {
      businessArr.value = JSON.parse(filteredData)
      isFilteredResult.value = true
      
      if (filterData) {
        filterConditions.value = JSON.parse(filterData)
        console.log('筛选条件:', filterConditions.value)
      }
      
      console.log('筛选结果加载完成，共', businessArr.value.length, '家商家')
    } else {
      console.warn('未找到筛选结果，显示所有商家')
      loadBusinessList()
    }
  } catch (error) {
    console.error('筛选结果加载失败:', error)
    loadBusinessList()
  }
}

const goToFilter = () => {
  router.push('/filter')
}

const showAllBusinesses = () => {
  localStorage.removeItem('filteredBusinesses')
  localStorage.removeItem('filterConditions')
  router.push({
    path: '/businessList',
    query: { orderTypeId: 'all' }
  })
}

// 加载商家列表
const loadBusinessList = () => {
console.log('=== 加载商家列表 ===')
console.log('食物类型ID:', orderTypeId)
console.log('类型名称:', getHeaderTitle())

if (orderTypeId && orderTypeId !== 'all') {
  // 根据类型筛选
  businessArr.value = allBusinessData.filter(business => 
    business.orderTypeId === parseInt(orderTypeId)
  )
  console.log(`按类型${orderTypeId}筛选后的商家数量:`, businessArr.value.length)
} else {
  // 显示所有商家
  businessArr.value = [...allBusinessData]
  console.log('显示所有商家数量:', businessArr.value.length)
}

// 打印筛选结果
businessArr.value.forEach((business, index) => {
  console.log(`[${index + 1}] ${business.businessName} - ${business.businessExplain}`)
})
}

// 跳转到商家详情
const toBusinessInfo = (businessId) => {
console.log('跳转到商家详情:', businessId)
router.push({
  path: '/businessInfo',
  query: { businessId }
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

  /**********商家列表************/
  .wrapper .business {
      width: 100%;
      margin-top: 12vw;
      margin-bottom: 14vw;
  }

  .wrapper .business li {
      width: 100%;
      box-sizing: border-box;
      padding: 2.5vw;
      border-bottom: solid 1px #DDD;
      user-select: none;
      cursor: pointer;
      display: flex;
      align-items: center;
      transition: background-color 0.2s ease;
  }

  .wrapper .business li:hover {
      background-color: #f9f9f9;
  }

  .wrapper .business li .business-img {
      position: relative;
  }

  .wrapper .business li .business-img img {
      width: 20vw;
      height: 20vw;
      border-radius: 2vw;
      object-fit: cover;
  }

  .wrapper .business li .business-img .business-img-quantity {
      width: 5vw;
      height: 5vw;
      background-color: red;
      color: #fff;
      font-size: 3.6vw;
      border-radius: 2.5vw;
      display: flex;
      justify-content: center;
      align-items: center;
      position: absolute;
      right: -1.5vw;
      top: -1.5vw;
  }

  .wrapper .business li .business-info {
      margin-left: 3vw;
      flex: 1;
  }

  .wrapper .business li .business-info h3 {
      font-size: 3.8vw;
      color: #555;
      margin-bottom: 1vw;
  }

  .wrapper .business li .business-info p {
      font-size: 3vw;
      color: #888;
      margin-top: 2vw;
  }

  /* 空状态样式 */
  .empty {
      text-align: center;
      padding: 20vw 0;
      color: #999;
      font-size: 4vw;
  }
  /* 筛选结果相关样式 */
.retry-btn, .show-all-btn {
    background-color: #0097FF;
    color: white;
    border: none;
    border-radius: 2vw;
    padding: 3vw 6vw;
    font-size: 3.5vw;
    cursor: pointer;
    margin: 2vw 1vw;
    transition: background-color 0.2s ease;
}

.show-all-btn {
    background-color: #666;
}

.retry-btn:hover {
    background-color: #007ACC;
}

.show-all-btn:hover {
    background-color: #555;
}
</style>