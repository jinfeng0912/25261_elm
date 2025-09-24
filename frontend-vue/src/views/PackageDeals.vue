<template>
    <div class="wrapper">
      <!-- header -->
      <header>
        <i class="fa fa-arrow-left" @click="goBack"></i>
        <p>品质套餐</p>
      </header>
  
      <!-- 套餐介绍 -->
      <div class="package-intro">
        <h2>精选品质套餐</h2>
        <p>优质搭配，营养均衡，超值优惠</p>
      </div>
  
      <!-- 套餐1 -->
      <div class="package-card">
        <div class="package-header">
          <h3>🌟 经典商务套餐</h3>
          <p>星巴克饮品 + 米村拌饭</p>
        </div>
        
        <div class="package-content">
          <!-- 星巴克饮品选择 -->
          <div class="food-section">
            <h4>选择星巴克饮品（必选）</h4>
            <div class="food-list">
              <div 
                v-for="drink in starbucksDrinks" 
                :key="drink.foodId"
                :class="['food-item', { 'selected': package1.drink?.foodId === drink.foodId }]"
                @click="selectDrink(1, drink)"
              >
                <div class="food-info">
                  <h5>{{ drink.foodName }}</h5>
                  <p>{{ drink.foodExplain }}</p>
                  <span class="price">¥{{ drink.foodPrice }}</span>
                </div>
                <i v-if="package1.drink?.foodId === drink.foodId" class="fa fa-check-circle selected-icon"></i>
              </div>
            </div>
          </div>
  
          <!-- 米村拌饭选择 -->
          <div class="food-section">
            <h4>选择米村拌饭（必选）</h4>
            <div class="food-list">
              <div 
                v-for="rice in riceDeals" 
                :key="rice.foodId"
                :class="['food-item', { 'selected': package1.rice?.foodId === rice.foodId }]"
                @click="selectRice(1, rice)"
              >
                <div class="food-info">
                  <h5>{{ rice.foodName }}</h5>
                  <p>{{ rice.foodExplain }}</p>
                  <span class="price">¥{{ rice.foodPrice }}</span>
                </div>
                <i v-if="package1.rice?.foodId === rice.foodId" class="fa fa-check-circle selected-icon"></i>
              </div>
            </div>
          </div>
  
          <!-- 套餐1价格和购买按钮 -->
          <div class="package-footer">
            <div class="package-price">
              <span class="original-price">原价：¥{{ package1OriginalPrice }}</span>
              <span class="package-price-final">套餐价：¥{{ package1FinalPrice }}</span>
              <span class="discount">立省¥{{ (package1OriginalPrice - package1FinalPrice).toFixed(2) }}</span>
            </div>
            <button 
              :disabled="!isPackage1Complete"
              :class="['buy-btn', { 'disabled': !isPackage1Complete }]"
              @click="buyPackage(1)"
            >
              {{ isPackage1Complete ? '立即购买' : '请选择完整套餐' }}
            </button>
          </div>
        </div>
      </div>
  
      <!-- 套餐2 -->
      <div class="package-card">
        <div class="package-header">
          <h3>🍜 美食三重奏套餐</h3>
          <p>茶兮饮品 + 万家饺子 + 申记串道</p>
        </div>
        
        <div class="package-content">
          <!-- 茶兮饮品选择 -->
          <div class="food-section">
            <h4>选择茶兮饮品（必选）</h4>
            <div class="food-list">
              <div 
                v-for="drink in teaDrinks" 
                :key="drink.foodId"
                :class="['food-item', { 'selected': package2.drink?.foodId === drink.foodId }]"
                @click="selectDrink(2, drink)"
              >
                <div class="food-info">
                  <h5>{{ drink.foodName }}</h5>
                  <p>{{ drink.foodExplain }}</p>
                  <span class="price">¥{{ drink.foodPrice }}</span>
                </div>
                <i v-if="package2.drink?.foodId === drink.foodId" class="fa fa-check-circle selected-icon"></i>
              </div>
            </div>
          </div>
  
          <!-- 万家饺子选择 -->
          <div class="food-section">
            <h4>选择万家饺子（必选）</h4>
            <div class="food-list">
              <div 
                v-for="dumpling in dumplings" 
                :key="dumpling.foodId"
                :class="['food-item', { 'selected': package2.dumpling?.foodId === dumpling.foodId }]"
                @click="selectDumpling(dumpling)"
              >
                <div class="food-info">
                  <h5>{{ dumpling.foodName }}</h5>
                  <p>{{ dumpling.foodExplain }}</p>
                  <span class="price">¥{{ dumpling.foodPrice }}</span>
                </div>
                <i v-if="package2.dumpling?.foodId === dumpling.foodId" class="fa fa-check-circle selected-icon"></i>
              </div>
            </div>
          </div>
  
          <!-- 申记串道选择 -->
          <div class="food-section">
            <h4>选择申记串道（必选）</h4>
            <div class="food-list">
              <div 
                v-for="skewer in skewers" 
                :key="skewer.foodId"
                :class="['food-item', { 'selected': package2.skewer?.foodId === skewer.foodId }]"
                @click="selectSkewer(skewer)"
              >
                <div class="food-info">
                  <h5>{{ skewer.foodName }}</h5>
                  <p>{{ skewer.foodExplain }}</p>
                  <span class="price">¥{{ skewer.foodPrice }}</span>
                </div>
                <i v-if="package2.skewer?.foodId === skewer.foodId" class="fa fa-check-circle selected-icon"></i>
              </div>
            </div>
          </div>
  
          <!-- 套餐2价格和购买按钮 -->
          <div class="package-footer">
            <div class="package-price">
              <span class="original-price">原价：¥{{ package2OriginalPrice }}</span>
              <span class="package-price-final">套餐价：¥{{ package2FinalPrice }}</span>
              <span class="discount">立省¥{{ (package2OriginalPrice - package2FinalPrice).toFixed(2) }}</span>
            </div>
            <button 
              :disabled="!isPackage2Complete"
              :class="['buy-btn', { 'disabled': !isPackage2Complete }]"
              @click="buyPackage(2)"
            >
              {{ isPackage2Complete ? '立即购买' : '请选择完整套餐' }}
            </button>
          </div>
        </div>
      </div>
  
      <!-- 底部说明 -->
      <div class="package-notice">
        <p>• 套餐商品均为新鲜制作，品质保证</p>
        <p>• 套餐价格已包含配送费</p>
        <p>• 活动最终解释权归平台所有</p>
      </div>
  
      <!-- Footer -->
      <Footer></Footer>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import Footer from '../components/Footer.vue'
  
  const router = useRouter()
  
  // 套餐选择数据
  const package1 = ref({
    drink: null,
    rice: null
  })
  
  const package2 = ref({
    drink: null,
    dumpling: null,
    skewer: null
  })
  
  // 扩展的食品数据，添加星巴克饮品
  const starbucksDrinks = ref([
    {
      foodId: 101,
      foodName: '美式咖啡',
      foodExplain: '经典浓郁美式咖啡',
      foodPrice: 25.00,
      businessId: 10012
    },
    {
      foodId: 102,
      foodName: '拿铁咖啡',
      foodExplain: '香滑牛奶拿铁',
      foodPrice: 30.00,
      businessId: 10012
    },
    {
      foodId: 103,
      foodName: '卡布奇诺',
      foodExplain: '经典意式卡布奇诺',
      foodPrice: 28.00,
      businessId: 10012
    },
    {
      foodId: 104,
      foodName: '焦糖玛奇朵',
      foodExplain: '香甜焦糖玛奇朵',
      foodPrice: 35.00,
      businessId: 10012
    }
  ])
  
  const riceDeals = ref([
    {
      foodId: 11,
      foodName: '韩式拌饭',
      foodExplain: '经典韩式拌饭',
      foodPrice: 18.00,
      businessId: 10004
    },
    {
      foodId: 12,
      foodName: '石锅拌饭',
      foodExplain: '热乎乎的石锅拌饭',
      foodPrice: 22.00,
      businessId: 10004
    }
  ])
  
  const teaDrinks = ref([
    {
      foodId: 18,
      foodName: '芒果汁',
      foodExplain: '新鲜芒果榨汁',
      foodPrice: 15.00,
      businessId: 10007
    },
    {
      foodId: 19,
      foodName: '珍珠奶茶',
      foodExplain: '经典珍珠奶茶',
      foodPrice: 12.00,
      businessId: 10007
    },
    {
      foodId: 20,
      foodName: '布丁奶茶',
      foodExplain: '香滑布丁奶茶',
      foodPrice: 14.00,
      businessId: 10007
    }
  ])
  
  const dumplings = ref([
    {
      foodId: 1,
      foodName: '纯肉鲜饺（水饺）',
      foodExplain: '纯肉馅饺子',
      foodPrice: 16.00,
      businessId: 10001
    },
    {
      foodId: 2,
      foodName: '玉米鲜肉（水饺）',
      foodExplain: '玉米青豆',
      foodPrice: 18.00,
      businessId: 10001
    },
    {
      foodId: 3,
      foodName: '虾仁三鲜（蒸饺）',
      foodExplain: '三鲜馅饺子',
      foodPrice: 22.00,
      businessId: 10001
    },
    {
      foodId: 4,
      foodName: '素三鲜（蒸饺）',
      foodExplain: '韭菜鸡蛋虾皮',
      foodPrice: 15.00,
      businessId: 10001
    }
  ])
  
  const skewers = ref([
    {
      foodId: 13,
      foodName: '烤羊肉串',
      foodExplain: '新疆风味烤串',
      foodPrice: 3.00,
      businessId: 10005
    },
    {
      foodId: 14,
      foodName: '烤鸡翅',
      foodExplain: '香嫩烤鸡翅',
      foodPrice: 8.00,
      businessId: 10005
    },
    {
      foodId: 15,
      foodName: '烤茄子',
      foodExplain: '蒜蓉烤茄子',
      foodPrice: 6.00,
      businessId: 10005
    }
  ])
  
  // 计算属性
  const isPackage1Complete = computed(() => {
    return package1.value.drink && package1.value.rice
  })
  
  const isPackage2Complete = computed(() => {
    return package2.value.drink && package2.value.dumpling && package2.value.skewer
  })
  
  const package1OriginalPrice = computed(() => {
    if (!isPackage1Complete.value) return 0
    return package1.value.drink.foodPrice + package1.value.rice.foodPrice
  })
  
  const package1FinalPrice = computed(() => {
    if (!isPackage1Complete.value) return 0
    return (package1OriginalPrice.value * 0.85).toFixed(2) // 8.5折
  })
  
  const package2OriginalPrice = computed(() => {
    if (!isPackage2Complete.value) return 0
    return package2.value.drink.foodPrice + package2.value.dumpling.foodPrice + package2.value.skewer.foodPrice
  })
  
  const package2FinalPrice = computed(() => {
    if (!isPackage2Complete.value) return 0
    return (package2OriginalPrice.value * 0.8).toFixed(2) // 8折
  })
  
  // 方法
  const goBack = () => {
    router.back()
  }
  
  const selectDrink = (packageNum, drink) => {
    if (packageNum === 1) {
      package1.value.drink = drink
    } else {
      package2.value.drink = drink
    }
    console.log(`套餐${packageNum}选择饮品:`, drink.foodName)
  }
  
  const selectRice = (packageNum, rice) => {
    package1.value.rice = rice
    console.log('套餐1选择米饭:', rice.foodName)
  }
  
  const selectDumpling = (dumpling) => {
    package2.value.dumpling = dumpling
    console.log('套餐2选择饺子:', dumpling.foodName)
  }
  
  const selectSkewer = (skewer) => {
    package2.value.skewer = skewer
    console.log('套餐2选择串串:', skewer.foodName)
  }
  
  const buyPackage = (packageNum) => {
    console.log(`=== 购买套餐${packageNum} ===`)
    
    let packageData = {}
    let finalPrice = 0
    
    if (packageNum === 1) {
      packageData = {
        packageType: 1,
        packageName: '经典商务套餐',
        items: [
          { ...package1.value.drink, quantity: 1 },
          { ...package1.value.rice, quantity: 1 }
        ],
        originalPrice: package1OriginalPrice.value,
        finalPrice: parseFloat(package1FinalPrice.value),
        discount: package1OriginalPrice.value - parseFloat(package1FinalPrice.value)
      }
      finalPrice = parseFloat(package1FinalPrice.value)
    } else {
      packageData = {
        packageType: 2,
        packageName: '美食三重奏套餐',
        items: [
          { ...package2.value.drink, quantity: 1 },
          { ...package2.value.dumpling, quantity: 1 },
          { ...package2.value.skewer, quantity: 1 }
        ],
        originalPrice: package2OriginalPrice.value,
        finalPrice: parseFloat(package2FinalPrice.value),
        discount: package2OriginalPrice.value - parseFloat(package2FinalPrice.value)
      }
      finalPrice = parseFloat(package2FinalPrice.value)
    }
    
    console.log('套餐数据:', packageData)
    console.log('最终价格:', finalPrice)
    
    // 保存套餐数据到localStorage
    localStorage.setItem('packageOrder', JSON.stringify(packageData))
    
    // 跳转到套餐支付页面
    router.push({
      path: '/packagePayment',
      query: { 
        packageType: packageNum,
        totalPrice: finalPrice
      }
    })
  }
  
  onMounted(() => {
    console.log('=== 品质套餐页面加载 ===')
  })
  </script>
  
  <style scoped>
  .wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 14vw;
  }
  
  /* header */
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
    align-items: center;
    justify-content: center;
  }
  
  .wrapper header .fa-arrow-left {
    position: absolute;
    left: 4vw;
    font-size: 5vw;
    cursor: pointer;
  }
  
  /* 套餐介绍 */
  .package-intro {
    margin-top: 12vw;
    padding: 6vw 4vw 4vw;
    text-align: center;
    background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
    color: white;
  }
  
  .package-intro h2 {
    font-size: 6vw;
    font-weight: bold;
    margin-bottom: 2vw;
  }
  
  .package-intro p {
    font-size: 3.5vw;
    opacity: 0.9;
  }
  
  /* 套餐卡片 */
  .package-card {
    margin: 4vw;
    background: white;
    border-radius: 3vw;
    overflow: hidden;
    box-shadow: 0 0.5vw 2vw rgba(0,0,0,0.1);
  }
  
  .package-header {
    background: linear-gradient(135deg, #4CAF50, #45a049);
    color: white;
    padding: 4vw;
    text-align: center;
  }
  
  .package-header h3 {
    font-size: 5vw;
    font-weight: bold;
    margin-bottom: 1vw;
  }
  
  .package-header p {
    font-size: 3.5vw;
    opacity: 0.9;
  }
  
  .package-content {
    padding: 4vw;
  }
  
  /* 食品选择区域 */
  .food-section {
    margin-bottom: 5vw;
  }
  
  .food-section h4 {
    font-size: 4vw;
    color: #333;
    margin-bottom: 3vw;
    font-weight: bold;
  }
  
  .food-list {
    display: flex;
    flex-direction: column;
    gap: 2vw;
  }
  
  .food-item {
    padding: 3vw;
    border: 0.3vw solid #eee;
    border-radius: 2vw;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .food-item:hover {
    border-color: #4CAF50;
    background-color: #f9f9f9;
  }
  
  .food-item.selected {
    border-color: #4CAF50;
    background-color: #e8f5e8;
  }
  
  .food-info {
    flex: 1;
  }
  
  .food-info h5 {
    font-size: 3.8vw;
    color: #333;
    margin-bottom: 1vw;
  }
  
  .food-info p {
    font-size: 3vw;
    color: #666;
    margin-bottom: 1vw;
  }
  
  .food-info .price {
    font-size: 3.5vw;
    color: #ff6600;
    font-weight: bold;
  }
  
  .selected-icon {
    font-size: 5vw;
    color: #4CAF50;
  }
  
  /* 套餐底部 */
  .package-footer {
    margin-top: 4vw;
    padding-top: 4vw;
    border-top: 1px solid #eee;
  }
  
  .package-price {
    text-align: center;
    margin-bottom: 3vw;
  }
  
  .original-price {
    display: block;
    font-size: 3vw;
    color: #999;
    text-decoration: line-through;
    margin-bottom: 1vw;
  }
  
  .package-price-final {
    display: block;
    font-size: 5vw;
    color: #ff4444;
    font-weight: bold;
    margin-bottom: 1vw;
  }
  
  .discount {
    display: inline-block;
    background-color: #ff4444;
    color: white;
    padding: 1vw 2vw;
    border-radius: 1vw;
    font-size: 2.8vw;
  }
  
  .buy-btn {
    width: 100%;
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
  
  .buy-btn:hover:not(.disabled) {
    background-color: #45a049;
  }
  
  .buy-btn.disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  
  /* 底部说明 */
  .package-notice {
    margin: 4vw;
    padding: 4vw;
    background: white;
    border-radius: 2vw;
    font-size: 3vw;
    color: #666;
    line-height: 1.6;
  }
  
  .package-notice p {
    margin-bottom: 1vw;
  }
  </style>