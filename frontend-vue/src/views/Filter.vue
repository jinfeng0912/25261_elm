<template>
    <div class="wrapper">
      <!-- header -->
      <header>
        <i class="fa fa-arrow-left" @click="goBack"></i>
        <p>筛选商家</p>
        <span class="reset-btn" @click="resetFilters">重置</span>
      </header>
  
      <div class="filter-content">
        <!-- 配送时间筛选 -->
        <div class="filter-section">
          <h3>配送时间</h3>
          <div class="filter-options">
            <label class="filter-option">
              <input type="checkbox" v-model="filters.deliveryTime" value="fast">
              <span class="checkmark"></span>
              30分钟内送达
            </label>
            <label class="filter-option">
              <input type="checkbox" v-model="filters.deliveryTime" value="medium">
              <span class="checkmark"></span>
              30-45分钟送达
            </label>
            <label class="filter-option">
              <input type="checkbox" v-model="filters.deliveryTime" value="slow">
              <span class="checkmark"></span>
              45分钟以上
            </label>
          </div>
        </div>
  
        <!-- 月销量筛选 -->
        <div class="filter-section">
          <h3>月销量</h3>
          <div class="filter-options">
            <label class="filter-option">
              <input type="checkbox" v-model="filters.monthlySales" value="high">
              <span class="checkmark"></span>
              500单以上
            </label>
            <label class="filter-option">
              <input type="checkbox" v-model="filters.monthlySales" value="medium">
              <span class="checkmark"></span>
              200-500单
            </label>
            <label class="filter-option">
              <input type="checkbox" v-model="filters.monthlySales" value="low">
              <span class="checkmark"></span>
              200单以下
            </label>
          </div>
        </div>
  
        <!-- 评分筛选 -->
        <div class="filter-section">
          <h3>商家评分</h3>
          <div class="filter-options">
            <label class="filter-option">
              <input type="checkbox" v-model="filters.rating" value="excellent">
              <span class="checkmark"></span>
              4.5分以上
            </label>
            <label class="filter-option">
              <input type="checkbox" v-model="filters.rating" value="good">
              <span class="checkmark"></span>
              4.0-4.5分
            </label>
            <label class="filter-option">
              <input type="checkbox" v-model="filters.rating" value="average">
              <span class="checkmark"></span>
              3.5-4.0分
            </label>
            <label class="filter-option">
              <input type="checkbox" v-model="filters.rating" value="below">
              <span class="checkmark"></span>
              3.5分以下
            </label>
          </div>
        </div>
  
        <!-- 商家类别筛选 -->
        <div class="filter-section">
          <h3>商家类别</h3>
          <div class="filter-options category-grid">
            <label 
              class="filter-option category-option" 
              v-for="(name, id) in foodTypeNames" 
              :key="id"
            >
              <input type="checkbox" v-model="filters.categories" :value="parseInt(id)">
              <span class="checkmark"></span>
              <div class="category-content">
                <img :src="getCategoryImage(id)" :alt="name">
                <span>{{ name }}</span>
              </div>
            </label>
          </div>
        </div>
      </div>
  
      <!-- 底部按钮 -->
      <div class="bottom-buttons">
        <div class="filter-result">
          <span>共找到 {{ filteredCount }} 家商家</span>
        </div>
        <button class="apply-btn" @click="applyFilters">
          查看结果 ({{ filteredCount }})
        </button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, computed, watch } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  
  // 导入类别图片
  import dcfl01 from '@/assets/dcfl01.png'
  import dcfl02 from '@/assets/dcfl02.png'
  import dcfl03 from '@/assets/dcfl03.png'
  import dcfl04 from '@/assets/dcfl04.png'
  import dcfl05 from '@/assets/dcfl05.png'
  import dcfl06 from '@/assets/dcfl06.png'
  import dcfl07 from '@/assets/dcfl07.png'
  import dcfl08 from '@/assets/dcfl08.png'
  import dcfl09 from '@/assets/dcfl09.png'
  import dcfl10 from '@/assets/dcfl10.png'
  
  const router = useRouter()
  const route = useRoute()
  
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
  
  // 类别图片映射
  const categoryImages = {
    1: dcfl01, 2: dcfl02, 3: dcfl03, 4: dcfl04, 5: dcfl05,
    6: dcfl06, 7: dcfl07, 8: dcfl08, 9: dcfl09, 10: dcfl10
  }
  
  // 筛选条件
  const filters = ref({
    deliveryTime: [],
    monthlySales: [],
    rating: [],
    categories: []
  })
  
  // 导入商家图片
  import sj01 from '@/assets/sj01.png'
  import sj02 from '@/assets/sj02.png'
  import sj03 from '@/assets/sj03.png'
  import sj04 from '@/assets/sj04.png'
  import sj05 from '@/assets/sj05.png'
  import sj06 from '@/assets/sj06.png'
  import sj07 from '@/assets/sj07.png'
  
  // 商家数据（与BusinessList.vue保持一致的完整数据结构）
  const businessData = ref([
    {
      businessId: 10001,
      businessName: '万家饺子（软件园E18店）',
      businessExplain: '各种饺子炒菜',
      businessImg: sj01,
      starPrice: 18.00,
      deliveryPrice: 3.50,
      distance: 3.22,
      monthlySales: 345,
      rating: 3.9,
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
      distance: 4.21,
      monthlySales: 233,
      rating: 3.6,
      orderTypeId: 1,
      quantity: 0
    },
    {
      businessId: 10003,
      businessName: '麦当劳麦乐送（全运路店）',
      businessExplain: '汉堡薯条套餐',
      businessImg: sj03,
      starPrice: 25.00,
      deliveryPrice: 4.00,
      distance: 2.10,
      monthlySales: 562,
      rating: 4.8,
      orderTypeId: 4,
      quantity: 0
    },
    {
      businessId: 10004,
      businessName: '米村拌饭（浑南店）',
      businessExplain: '各种炒菜拌饭',
      businessImg: sj04,
      starPrice: 18.00,
      deliveryPrice: 3.00,
      distance: 4.11,
      monthlySales: 145,
      rating: 3.1,
      orderTypeId: 1,
      quantity: 0
    },
    {
      businessId: 10005,
      businessName: '申记串道（中海康城店）',
      businessExplain: '烤串炸串烧烤',
      businessImg: sj05,
      starPrice: 20.00,
      deliveryPrice: 4.00,
      distance: 2.15,
      monthlySales: 300,
      rating: 3.0,
      orderTypeId: 10,
      quantity: 0
    },
    {
      businessId: 10006,
      businessName: '半亩良田排骨米饭',
      businessExplain: '排骨米饭套餐',
      businessImg: sj06,
      starPrice: 22.00,
      deliveryPrice: 4.00,
      distance: 5.25,
      monthlySales: 410,
      rating: 4.0,
      orderTypeId: 1,
      quantity: 0
    },
    {
      businessId: 10007,
      businessName: '茶兮鲜果饮品（国际软件园店）',
      businessExplain: '甜品饮品奶茶',
      businessImg: sj07,
      starPrice: 12.00,
      deliveryPrice: 3.00,
      distance: 1.50,
      monthlySales: 500,
      rating: 4.2,
      orderTypeId: 5,
      quantity: 0
    },
    // 添加更多商家数据以提供更丰富的筛选结果
    {
      businessId: 10008,
      businessName: '晨光早餐铺',
      businessExplain: '包子粥类早餐',
      businessImg: sj01,
      starPrice: 8.00,
      deliveryPrice: 2.00,
      distance: 1.20,
      monthlySales: 180,
      rating: 3.8,
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
      distance: 2.80,
      monthlySales: 220,
      rating: 4.1,
      orderTypeId: 2,
      quantity: 0
    },
    {
      businessId: 10010,
      businessName: '全能跑腿服务',
      businessExplain: '代买代送各种商品',
      businessImg: sj03,
      starPrice: 10.00,
      deliveryPrice: 5.00,
      distance: 0.50,
      monthlySales: 450,
      rating: 4.6,
      orderTypeId: 3,
      quantity: 0
    },
    {
      businessId: 10011,
      businessName: '必胜客（软件园店）',
      businessExplain: '意式披萨西餐',
      businessImg: sj04,
      starPrice: 35.00,
      deliveryPrice: 6.00,
      distance: 3.50,
      monthlySales: 380,
      rating: 4.3,
      orderTypeId: 4,
      quantity: 0
    },
    {
      businessId: 10012,
      businessName: '星巴克咖啡',
      businessExplain: '精品咖啡甜品',
      businessImg: sj05,
      starPrice: 30.00,
      deliveryPrice: 5.00,
      distance: 2.30,
      monthlySales: 650,
      rating: 4.7,
      orderTypeId: 5,
      quantity: 0
    }
  ])
  
  // 配送时间计算函数（从index.vue复制）
  const getDeliveryTime = (distance) => {
    if (!distance || distance <= 0) return 20;
    if (distance <= 2) return Math.round(20 + (distance - 1) * 6);
    if (distance <= 5) return Math.round(26 + (distance - 2) * 4);
    if (distance <= 8) return Math.round(38 + (distance - 5) * 2);
    return Math.round(44 + Math.min(distance - 8, 2) * 1.5);
  }
  
  // 计算筛选后的商家数量
  const filteredCount = computed(() => {
    return filterBusinesses(businessData.value).length
  })
  
  // 筛选逻辑
  const filterBusinesses = (businesses) => {
    let filtered = [...businesses]
  
    // 配送时间筛选
    if (filters.value.deliveryTime.length > 0) {
      filtered = filtered.filter(business => {
        const deliveryTime = getDeliveryTime(business.distance)
        return filters.value.deliveryTime.some(timeRange => {
          switch (timeRange) {
            case 'fast': return deliveryTime <= 30
            case 'medium': return deliveryTime > 30 && deliveryTime <= 45
            case 'slow': return deliveryTime > 45
            default: return false
          }
        })
      })
    }
  
    // 月销量筛选
    if (filters.value.monthlySales.length > 0) {
      filtered = filtered.filter(business => {
        return filters.value.monthlySales.some(salesRange => {
          switch (salesRange) {
            case 'high': return business.monthlySales >= 500
            case 'medium': return business.monthlySales >= 200 && business.monthlySales < 500
            case 'low': return business.monthlySales < 200
            default: return false
          }
        })
      })
    }
  
    // 评分筛选
    if (filters.value.rating.length > 0) {
      filtered = filtered.filter(business => {
        return filters.value.rating.some(ratingRange => {
          switch (ratingRange) {
            case 'excellent': return business.rating >= 4.5
            case 'good': return business.rating >= 4.0 && business.rating < 4.5
            case 'average': return business.rating >= 3.5 && business.rating < 4.0
            case 'below': return business.rating < 3.5
            default: return false
          }
        })
      })
    }
  
    // 类别筛选
    if (filters.value.categories.length > 0) {
      filtered = filtered.filter(business => {
        return filters.value.categories.includes(business.orderTypeId)
      })
    }
  
    return filtered
  }
  
  // 方法
  const getCategoryImage = (categoryId) => {
    return categoryImages[categoryId] || dcfl01
  }
  
  const goBack = () => {
    router.go(-1)
  }
  
  const resetFilters = () => {
    filters.value = {
      deliveryTime: [],
      monthlySales: [],
      rating: [],
      categories: []
    }
    console.log('筛选条件已重置')
  }
  
  const applyFilters = () => {
    console.log('=== 应用筛选条件 ===')
    console.log('筛选条件:', filters.value)
    
    const filteredBusinesses = filterBusinesses(businessData.value)
    console.log('筛选结果:', filteredBusinesses.length, '家商家')
    
    // 将筛选结果保存到localStorage
    localStorage.setItem('filteredBusinesses', JSON.stringify(filteredBusinesses))
    localStorage.setItem('filterConditions', JSON.stringify(filters.value))
    
    // 跳转到商家列表页面，传递特殊参数表示这是筛选结果
    router.push({
      path: '/businessList',
      query: { 
        filtered: 'true',
        count: filteredBusinesses.length
      }
    })
  }
  
  // 生命周期
  onMounted(() => {
    console.log('=== 筛选页面加载 ===')
    console.log('可筛选商家数量:', businessData.value.length)
    
    // 从localStorage读取之前的筛选条件（如果有的话）
    const savedFilters = localStorage.getItem('filterConditions')
    if (savedFilters) {
      try {
        filters.value = JSON.parse(savedFilters)
        console.log('已恢复筛选条件:', filters.value)
      } catch (error) {
        console.log('筛选条件恢复失败，使用默认值')
      }
    }
  })
  
  // 监听筛选条件变化
  watch(
    () => filters.value,
    (newFilters) => {
      console.log('筛选条件变化:', newFilters)
      console.log('当前筛选结果数量:', filteredCount.value)
    },
    { deep: true }
  )
  </script>
  
  <style scoped>
  /********** 总容器 **********/
  .wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f5f5f5;
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
    justify-content: space-between;
    align-items: center;
    padding: 0 4vw;
    box-sizing: border-box;
  }
  
  .wrapper header .fa-arrow-left {
    font-size: 5.5vw;
    cursor: pointer;
  }
  
  .wrapper header p {
    font-size: 4.8vw;
    font-weight: bold;
  }
  
  .wrapper header .reset-btn {
    font-size: 3.8vw;
    cursor: pointer;
    padding: 1vw 2vw;
    border-radius: 1vw;
    transition: background-color 0.2s ease;
  }
  
  .wrapper header .reset-btn:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }
  
  /********** 筛选内容 **********/
  .filter-content {
    margin-top: 12vw;
    padding: 4vw;
    margin-bottom: 18vw;
  }
  
  .filter-section {
    background-color: white;
    border-radius: 2vw;
    margin-bottom: 4vw;
    padding: 4vw;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }
  
  .filter-section h3 {
    font-size: 4.2vw;
    color: #333;
    margin-bottom: 3vw;
    font-weight: bold;
    border-bottom: 1px solid #eee;
    padding-bottom: 2vw;
  }
  
  .filter-options {
    display: flex;
    flex-direction: column;
    gap: 3vw;
  }
  
  .category-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 3vw;
  }
  
  .filter-option {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 2vw;
    border-radius: 1.5vw;
    transition: background-color 0.2s ease;
    position: relative;
  }
  
  .filter-option:hover {
    background-color: #f9f9f9;
  }
  
  .filter-option input[type="checkbox"] {
    display: none;
  }
  
  .checkmark {
    width: 5vw;
    height: 5vw;
    border: 2px solid #ddd;
    border-radius: 1vw;
    margin-right: 3vw;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    flex-shrink: 0;
  }
  
  .filter-option input[type="checkbox"]:checked + .checkmark {
    background-color: #0097FF;
    border-color: #0097FF;
  }
  
  .filter-option input[type="checkbox"]:checked + .checkmark::after {
    content: '✓';
    color: white;
    font-size: 3vw;
    font-weight: bold;
  }
  
  .filter-option span:not(.checkmark) {
    font-size: 3.8vw;
    color: #333;
    flex: 1;
  }
  
  /* 类别选项特殊样式 */
  .category-option {
    flex-direction: column;
    text-align: center;
    padding: 3vw;
    border: 1px solid #eee;
    border-radius: 2vw;
  }
  
  .category-option .checkmark {
    margin-right: 0;
    margin-bottom: 2vw;
    align-self: flex-end;
  }
  
  .category-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2vw;
  }
  
  .category-content img {
    width: 8vw;
    height: 8vw;
    object-fit: contain;
  }
  
  .category-content span {
    font-size: 3.2vw;
    color: #666;
  }
  
  .category-option input[type="checkbox"]:checked ~ .category-content span {
    color: #0097FF;
    font-weight: bold;
  }
  
  /********** 底部按钮 **********/
  .bottom-buttons {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    background-color: white;
    padding: 3vw 4vw;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
    display: flex;
    justify-content: space-between;
    align-items: center;
    z-index: 1000;
  }
  
  .filter-result {
    color: #666;
    font-size: 3.5vw;
  }
  
  .apply-btn {
    background-color: #0097FF;
    color: white;
    border: none;
    border-radius: 6vw;
    padding: 3vw 8vw;
    font-size: 4vw;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.2s ease;
    box-shadow: 0 2px 8px rgba(0, 151, 255, 0.3);
  }
  
  .apply-btn:hover {
    background-color: #007ACC;
  }
  
  .apply-btn:active {
    transform: translateY(1px);
  }
  
  /* 响应式调整 */
  @media (max-width: 480px) {
    .category-grid {
      grid-template-columns: repeat(3, 1fr);
      gap: 2vw;
    }
    
    .category-option {
      padding: 2vw;
    }
    
    .category-content img {
      width: 6vw;
      height: 6vw;
    }
  }
  </style>