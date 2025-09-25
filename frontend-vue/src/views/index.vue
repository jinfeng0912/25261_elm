<template>
  <div class="wrapper">
   <header @click="toggleDropdown">
      <div class="icon-location-box">
        <div class="icon-location"></div>
      </div>
      <div class="location-text">
        天津大学 诚园七斋<i class="fa fa-caret-down"></i>
      </div>
    </header>
  
      <!-- 刮刮乐下拉界面 -->
    <div :class="['scratch-dropdown', { 'active': isDropdownOpen }]">
      <div class="scratch-container">
        <div class="scratch-header">
          <i class="fa fa-gift gift-icon"></i>
          <h2>惊喜优惠券</h2>
          <p>刮开涂层，领取专属红包！</p>
        </div>

        <div class="scratch-card">
          <div class="card-header">
            <i class="fa fa-star star-icon"></i>
            <h3>超级会员红包</h3>
          </div>
          
          <div class="scratch-area" ref="scratchArea">
            <!-- 背景奖励显示 -->
            <div class="reward-display">
              <div v-if="showResult" class="reward-content">
                <div class="reward-amount">
                  {{ couponAmount > 0 ? `¥${couponAmount}` : '谢谢参与' }}
                </div>
                <div class="reward-text">
                  {{ couponAmount > 0 ? '恭喜获得红包！' : '下次再来试试吧' }}
                </div>
              </div>
              <div v-else class="reward-content">
                <div class="reward-amount">？</div>
              </div>
            </div>
            
            <!-- 刮刮层画布 -->
            <canvas
              ref="scratchCanvas"
              class="scratch-canvas"
              @mousedown="startScratch"
              @mousemove="handleScratch"
              @mouseup="stopScratch"
              @mouseleave="stopScratch"
              @touchstart="startScratchTouch"
              @touchmove="handleScratchTouch"
              @touchend="stopScratch"
            ></canvas>
          </div>
          
          <!-- 操作按钮 -->
          <div v-if="showResult" class="action-buttons">
            <button v-if="couponAmount > 0" @click="goToPayment" class="use-coupon-btn">
              立即使用红包购买会员
            </button>
            <button @click="closeDropdown" class="back-home-btn">
              返回首页
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付页面 -->
    <div v-if="showPayment" class="payment-overlay">
      <div class="payment-container">
        <header class="payment-header">
          <i class="fa fa-arrow-left" @click="closePayment"></i>
          <span>超级会员支付</span>
        </header>

        <div class="payment-content">
          <!-- 订单信息 -->
          <div class="info-section">
            <h3>订单信息</h3>
            <div class="order-info">
              <p>订单号：{{ orderId }}</p>
              <p>下单时间：{{ orderTime }}</p>
            </div>
          </div>

          <!-- 会员项目 -->
          <div class="info-section">
            <h3>会员项目</h3>
            <div class="member-info">
              <div class="member-item">
                <span>超级会员（30天）</span>
                <div class="price-info">
                  <div class="original-price">¥9.90</div>
                  <div class="final-price">¥{{ (9.90 - couponAmount).toFixed(2) }}</div>
                </div>
              </div>
              <div v-if="couponAmount > 0" class="coupon-discount">
                已使用红包：-¥{{ couponAmount.toFixed(2) }}
              </div>
            </div>
          </div>

          
          <div class="info-section">
            <h3>支付方式</h3>
            <div class="payment-methods">
              <label class="payment-method">
                <input
                  type="radio"
                  name="payMethod"
                  value="alipay"
                  v-model="selectedPayMethod"
                />
                <div class="payment-icon alipay">支</div>
                <span>支付宝</span>
              </label>
              <label class="payment-method">
                <input
                  type="radio"
                  name="payMethod"
                  value="wechat"
                  v-model="selectedPayMethod"
                />
                <div class="payment-icon wechat">微</div>
                <span>微信支付</span>
              </label>
            </div>
          </div>

          <!-- 支付按钮 -->
          <div class="payment-buttons">
            <button @click="handlePayment" class="confirm-pay-btn">
              确认支付 ¥{{ (9.90 - couponAmount).toFixed(2) }}
            </button>
            <button @click="goBackHome" class="back-home-btn">
              返回首页
            </button>
          </div>
        </div>
      </div>
    </div>

  <div class="search">
  <div class="search-fixed-top" ref="fixedBox">
		 <div class="search-box" @click="goToSearch">
  <i class="fa fa-search"></i>
  <span>搜索饿了么商家、商品名称</span>
</div>
  </div>
  </div>
  
      <ul class="foodtype">
        <li @click="toBusinessList(1)">
          <img src="../assets/dcfl01.png" />
          <p>美食</p>
        </li>
        <li @click="toBusinessList(2)">
          <img src="../assets/dcfl02.png" />
          <p>早餐</p>
        </li>
        <li @click="toBusinessList(3)">
          <img src="../assets/dcfl03.png" />
          <p>跑腿代购</p>
        </li>
        <li @click="toBusinessList(4)">
          <img src="../assets/dcfl04.png" />
          <p>汉堡披萨</p>
        </li>
        <li @click="toBusinessList(5)">
          <img src="../assets/dcfl05.png" />
          <p>甜品饮品</p>
        </li>
        <li @click="toBusinessList(6)">
          <img src="../assets/dcfl06.png" />
          <p>速食简餐</p>
        </li>
        <li @click="toBusinessList(7)">
          <img src="../assets/dcfl07.png" />
          <p>地方小吃</p>
        </li>
        <li @click="toBusinessList(8)">
          <img src="../assets/dcfl08.png" />
          <p>米粉面馆</p>
        </li>
        <li @click="toBusinessList(9)">
          <img src="../assets/dcfl09.png" />
          <p>包子粥铺</p>
        </li>
        <li @click="toBusinessList(10)">
          <img src="../assets/dcfl10.png" />
          <p>炸鸡炸串</p>
        </li>
      </ul>
  

  
      <div class="supermember" @click="toSuperMember" style="cursor: pointer;">
          <div class="left">
            <img src="../assets/super_member.png" />
            <h3>超级会员</h3>
            <p>&#8226; 每月享超值权益</p>
          </div>
          <div class="right">立即开通 &gt;</div>
      </div>
  
      <!-- 推荐商家 -->
        <div class="recommend">
            <div class="recommend-line"></div>
            <p>推荐商家</p>
            <div class="recommend-line"></div>
        </div>

        <!-- 推荐方式（修改为可点击的排序） -->
        <ul class="recommendtype">
            <li 
                :class="{ active: currentSort === 'rating' }"
                @click="sortBusinessBy('rating')"
            >
                综合排序<i class="fa fa-caret-down"></i>
            </li>
            <li 
                :class="{ active: currentSort === 'distance' }"
                @click="sortBusinessBy('distance')"
            >
                距离最近
            </li>
            <li 
                :class="{ active: currentSort === 'sales' }"
                @click="sortBusinessBy('sales')"
            >
                销量最高
            </li>
              <li @click="goToFilter">筛选<i class="fa fa-filter"></i></li>
        </ul>

        <!-- 推荐商家列表（修改为动态渲染） -->
        <ul class="business">
            <li 
                v-for="business in displayedBusinessList" 
                :key="business.businessId"
                @click="goToBusiness(business.businessId)"
            >
                <img :src="getBusinessImage(business.businessId)">
                <div class="business-info">
                    <div class="business-info-h">
                        <h3>{{ business.businessName }}</h3>
                        <div class="business-info-like">&#8226;</div>
                    </div>
                    <div class="business-info-star">
                        <div class="business-info-star-left">
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <p>{{ business.rating }} 月售{{ business.monthlySales }}单</p>
                        </div>
                        <div class="business-info-star-right">
                            蜂鸟专送
                        </div>
                    </div>
                    <div class="business-info-delivery">
                        <p>&#165;{{ business.starPrice }}起送 | &#165;{{ business.deliveryPrice }}配送</p>
                        <p>{{ business.distance }}km | {{ getDeliveryTime(business.distance) }}分钟</p>
                    </div>
                    <div class="business-info-explain">
                        <div>{{ business.businessExplain }}</div>
                    </div>
                    <div class="business-info-promotion">
                        <div class="business-info-promotion-left">
                            <div class="business-info-promotion-left-icon">新</div>
                            <p>饿了么新用户首单立减9元</p>
                        </div>
                        <div class="business-info-promotion-right">
                            <p>两个活动</p>
                            <i class="fa fa-caret-down"></i>
                        </div>
                    </div>
                    <div class="business-info-promotion">
                        <div class="business-info-promotion-left">
                            <div class="business-info-promotion-left-icon" style="background-color: #F1884F;">特</div>
                            <p>特价商品5元起</p>
                        </div>
                    </div>
                </div>
            </li>
        </ul>
        <!-- 底部菜单 -->
        <Footer></Footer>
    </div>
</template>
  
<script setup>
  import Footer from "../components/Footer.vue";
  import { ref, onMounted, onUnmounted, inject, nextTick, computed } from "vue"; // 添加 computed 导入
  import { useRouter } from "vue-router";
  import sj01 from '@/assets/sj01.png'
  import sj02 from '@/assets/sj02.png'
  import sj03 from '@/assets/sj03.png'
  import sj04 from '@/assets/sj04.png'
  import sj05 from '@/assets/sj05.png'
  import sj06 from '@/assets/sj06.png'
  import sj07 from '@/assets/sj07.png'

const router = useRouter();
const axios = inject('axios');
const fixedBox = ref(null);

// 原有的业务逻辑变量
const businessList = ref([]);
const sortType = ref('rating'); 

// 刮刮乐相关变量
const isDropdownOpen = ref(false);
const isScratching = ref(false);
const scratchProgress = ref(0);
const couponAmount = ref(0);
const showResult = ref(false);
const showPayment = ref(false);
const selectedPayMethod = ref('alipay');
const orderId = ref('');
const orderTime = ref('');
const scratchCanvas = ref(null);
const scratchArea = ref(null);
const isMouseDown = ref(false);

// 响应式数据
const currentSort = ref('rating'); // 当前排序类型

// 搜索跳转方法
const goToSearch = () => {
  console.log('跳转到搜索页面')
  router.push('/search')
}

const toPackageDeals = () => {
  console.log('跳转到品质套餐页面');
  router.push('/packageDeals');
};

// 硬编码的商家数据
const businessData = ref([
  {
    businessId: 10001,
    businessName: '万家饺子（软件园E18店）',
    businessExplain: '各种饺子炒菜',
    starPrice: 15,
    deliveryPrice: 3,
    distance: 3.22,
    monthlySales: 345,
    rating: 3.9
  },
  {
    businessId: 10002,
    businessName: '小锅饭豆腐馆（全运店）',
    businessExplain: '特色美食',
    starPrice: 15,
    deliveryPrice: 3,
    distance: 4.21,
    monthlySales: 233,
    rating: 3.6
  },
  {
    businessId: 10003,
    businessName: '麦当劳麦乐送（全运路店）',
    businessExplain: '汉堡薯条',
    starPrice: 15,
    deliveryPrice: 3,
    distance: 2.10,
    monthlySales: 562,
    rating: 4.8
  },
  {
    businessId: 10004,
    businessName: '米村拌饭（浑南店）',
    businessExplain: '各种炒菜拌饭',
    starPrice: 15,
    deliveryPrice: 3,
    distance: 4.11,
    monthlySales: 145,
    rating: 3.1
  },
  {
    businessId: 10005,
    businessName: '申记串道（中海康城店）',
    businessExplain: '烤串炸串',
    starPrice: 15,
    deliveryPrice: 3,
    distance: 2.15,
    monthlySales: 300,
    rating: 3.0
  },
  {
    businessId: 10006,
    businessName: '半亩良田排骨米饭',
    businessExplain: '排骨米饭套餐',
    starPrice: 15,
    deliveryPrice: 3,
    distance: 5.25,
    monthlySales: 410,
    rating: 4.0
  },
  {
    businessId: 10007,
    businessName: '茶兮鲜果饮品（国际软件园店）',
    businessExplain: '甜品饮品',
    starPrice: 15,
    deliveryPrice: 3,
    distance: 1.50,
    monthlySales: 500,
    rating: 4.2
  }
]);

// 计算属性：根据排序类型返回排序后的商家列表
const displayedBusinessList = computed(() => {
  if (!businessData.value || businessData.value.length === 0) {
    return [];
  }
  
  const sortedList = [...businessData.value]; // 创建副本避免修改原数组
  
  console.log('=== 开始排序 ===');
  console.log('排序类型:', currentSort.value);
  
  switch (currentSort.value) {
    case 'distance':
      // 按距离排序（升序）
      sortedList.sort((a, b) => a.distance - b.distance);
      console.log('按距离排序完成');
      break;
    case 'sales':
      // 按销量排序（降序）
      sortedList.sort((a, b) => b.monthlySales - a.monthlySales);
      console.log('按销量排序完成');
      break;
    case 'rating':
    default:
      // 按评分排序（降序）
      sortedList.sort((a, b) => b.rating - a.rating);
      console.log('按评分排序完成');
      break;
  }
  
  // 打印前3个商家的排序信息
  sortedList.slice(0, 3).forEach((business, index) => {
    console.log(`排序结果[${index + 1}]: ${business.businessName} - 距离:${business.distance}km 销量:${business.monthlySales} 评分:${business.rating}`);
  });
  
  return sortedList;
});

// 生成订单号
const generateOrderId = () => {
  const now = new Date();
  const timestamp = now.getFullYear().toString()
    + (now.getMonth() + 1).toString().padStart(2, '0')
    + now.getDate().toString().padStart(2, '0')
    + now.getHours().toString().padStart(2, '0')
    + now.getMinutes().toString().padStart(2, '0')
    + now.getSeconds().toString().padStart(2, '0');
  const random = Math.floor(Math.random() * 900 + 100);
  return "VIP" + timestamp + random;
};

// 初始化刮刮乐画布
const initCanvas = () => {
  const canvas = scratchCanvas.value;
  if (!canvas) return;
  
  const ctx = canvas.getContext('2d');
  const rect = canvas.getBoundingClientRect();
  canvas.width = rect.width;
  canvas.height = rect.height;
  
  // 绘制银色刮刮层
  const gradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height);
  gradient.addColorStop(0, '#C0C0C0');
  gradient.addColorStop(0.5, '#E6E6E6');
  gradient.addColorStop(1, '#C0C0C0');
  
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, canvas.width, canvas.height);
  
  // 添加刮刮乐文字
  ctx.fillStyle = '#666';
  ctx.font = 'bold 16px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('刮开有惊喜', canvas.width / 2, canvas.height / 2);
};

// 处理刮刮乐逻辑
const handleScratch = (e) => {
  if (!isMouseDown.value || showResult.value) return;
  
  const canvas = scratchCanvas.value;
  if (!canvas) return;
  
  const ctx = canvas.getContext('2d');
  const rect = canvas.getBoundingClientRect();
  
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;
  
  // 擦除效果
  ctx.globalCompositeOperation = 'destination-out';
  ctx.beginPath();
  ctx.arc(x, y, 20, 0, 2 * Math.PI);
  ctx.fill();
  
  // 计算刮开进度
  const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
  const pixels = imageData.data;
  let transparentPixels = 0;
  
  for (let i = 3; i < pixels.length; i += 4) {
    if (pixels[i] === 0) transparentPixels++;
  }
  
  const progress = (transparentPixels / (pixels.length / 4)) * 100;
  scratchProgress.value = progress;
  
  // 当刮开超过30%时显示结果
  if (progress > 30 && !showResult.value) {
    const randomAmount = Math.floor(Math.random() * 4); // 0-3元
    couponAmount.value = randomAmount;
    showResult.value = true;
    isScratching.value = false;
  }
};

// 开始刮奖
const startScratch = (e) => {
  if (showResult.value) return;
  e.preventDefault();
  isMouseDown.value = true;
  isScratching.value = true;
  handleScratch(e);
};

// 停止刮奖
const stopScratch = () => {
  isMouseDown.value = false;
};

// 触摸开始
const startScratchTouch = (e) => {
  if (showResult.value) return;
  e.preventDefault();
  isMouseDown.value = true;
  isScratching.value = true;
  const touch = e.touches[0];
  const mouseEvent = {
    clientX: touch.clientX,
    clientY: touch.clientY
  };
  handleScratch(mouseEvent);
};

// 触摸移动
const handleScratchTouch = (e) => {
  if (!isMouseDown.value) return;
  e.preventDefault();
  const touch = e.touches[0];
  const mouseEvent = {
    clientX: touch.clientX,
    clientY: touch.clientY
  };
  handleScratch(mouseEvent);
};

// 切换下拉框
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
  if (isDropdownOpen.value) {
    nextTick(() => {
      setTimeout(() => {
        initCanvas();
      }, 100);
    });
  }
};

// 关闭下拉框
const closeDropdown = () => {
  isDropdownOpen.value = false;
  resetScratchState();
};

// 重置刮刮乐状态
const resetScratchState = () => {
  showResult.value = false;
  isScratching.value = false;
  scratchProgress.value = 0;
  couponAmount.value = 0;
};

// 跳转到支付页面
const goToPayment = () => {
  orderId.value = generateOrderId();
  orderTime.value = new Date().toLocaleString('zh-CN');
  showPayment.value = true;
};

// 关闭支付页面
const closePayment = () => {
  showPayment.value = false;
};

// 支付逻辑
const handlePayment = () => {
  const paymentName = selectedPayMethod.value === 'alipay' ? '支付宝' : '微信支付';
  const finalAmount = (9.90 - couponAmount.value).toFixed(2);
  alert(`订单 ${orderId.value} 已通过 ${paymentName} 成功付款！实付金额：¥${finalAmount}`);
  goBackHome();
};

// 返回首页
const goBackHome = () => {
  isDropdownOpen.value = false;
  showPayment.value = false;
  resetScratchState();
};

const toSuperMember = () => {
  console.log('正在跳转到超级会员页面...');
  router.push('/SuperMember');
};

// 生命周期
onMounted(() => {
  document.onscroll = () => {
    let s1 = document.documentElement.scrollTop;
    let s2 = document.body.scrollTop;
    let scroll = s1 == 0 ? s2 : s1;
    let width = document.documentElement.clientWidth;
    let search = fixedBox.value;
    if (search) {
      if (scroll > width * 0.12) {
        search.style.position = "fixed";
        search.style.left = "0";
        search.style.top = "0";
      } else {
        search.style.position = "static";
      }
    }
  };
  
  console.log('首页初始化完成，默认按评分排序');
});

onUnmounted(() => {
  document.onscroll = null;
});

// 方法定义
const getBusinessImage = (businessId) => {
  const imageMap = {
    10001: sj01, 10002: sj02, 10003: sj03, 10004: sj04,
    10005: sj05, 10006: sj06, 10007: sj07
  };
  return imageMap[businessId] || sj01;
}

const getDeliveryTime = (distance) => {
  if (!distance || distance <= 0) return 20;
  if (distance <= 2) return Math.round(20 + (distance - 1) * 6);
  if (distance <= 5) return Math.round(26 + (distance - 2) * 4);
  if (distance <= 8) return Math.round(38 + (distance - 5) * 2);
  return Math.round(44 + Math.min(distance - 8, 2) * 1.5);
}

// 排序方法
const sortBusinessBy = (sortType) => {
  console.log('=== 点击排序按钮 ===');
  console.log('排序类型:', sortType);
  console.log('当前排序类型:', currentSort.value);
  
  if (currentSort.value === sortType) {
    console.log('相同排序类型，跳过');
    return;
  }
  
  currentSort.value = sortType;
  console.log('排序类型已更新为:', sortType);
}

// 跳转到商家详情
const goToBusiness = (businessId) => {
  console.log('=== 跳转到商家详情 ===');
  console.log('商家ID:', businessId);
  router.push({ 
    path: '/businessInfo', 
    query: { businessId } 
  });
}

// 跳转到商家列表
const toBusinessList = (orderTypeId) => {
  console.log('跳转到商家列表，类型ID:', orderTypeId);
  router.push({ 
    path: "/businessList", 
    query: { orderTypeId: orderTypeId } 
  });
};

// 跳转到筛选页面
const goToFilter = () => {
  console.log('跳转到筛选页面');
  router.push('/filter');
};

</script>

<style scoped>
  /* 现有样式保持不变，添加新的排序激活状态样式 */
  
  /****************** 总容器 ******************/
  .wrapper {
    width: 100%;
    height: 100%;
  }
  
  /****************** header ******************/
  .wrapper header {
    width: 100%;
    height: 12vw;
    background-color: #0097ff;
  
    display: flex;
    align-items: center;
  }
  
  .wrapper header .icon-location-box {
    width: 3.5vw;
    height: 3.5vw;
    margin: 0 1vw 0 3vw;
  }
  
  .wrapper header .location-text {
    font-size: 4.5vw;
    font-weight: 700;
    color: #fff;
  }
  
  .wrapper header .location-text .fa-caret-down {
    margin-left: 1vw;
  }
  
  /****************** search ******************/
  .wrapper .search {
    width: 100%;
    height: 13vw;
  }
  
  .wrapper .search .search-fixed-top {
    width: 100%;
    height: 13vw;
    background-color: #0097ff;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .wrapper .search .search-fixed-top .search-box {
    width: 90%;
    height: 9vw;
    background-color: #fff;
    border-radius: 2px;
  
    display: flex;
    justify-content: center;
    align-items: center;
  
    font-size: 3.5vw;
    color: #aeaeae;
    font-family: "宋体";
    user-select: none;
  }
  
  .wrapper .search .search-fixed-top .search-box .fa-search {
    margin-right: 1vw;
  }
  
   /**套餐**/
  .wrapper .banner {
  width: 95%;
  margin: 0 auto;
  height: 29vw;
  background-image: url(../assets/index_banner.png);
  background-repeat: no-repeat;
  background-size: cover;
  box-sizing: border-box;
  padding: 2vw 6vw;
  
  /* 新增的可点击样式 */
  cursor: pointer;
  user-select: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 2vw;
  position: relative;
  overflow: hidden;
}

.wrapper .banner::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0);
  transition: background 0.2s ease;
}

.wrapper .banner:hover {
  transform: translateY(-1px);
  box-shadow: 0 2vw 4vw rgba(0, 0, 0, 0.1);
}

.wrapper .banner:hover::before {
  background: rgba(0, 0, 0, 0.05);
}

.wrapper .banner:active {
  transform: translateY(0);
}

.wrapper .banner h3 {
  font-size: 4.2vw;
  margin-bottom: 1.2vw;
  position: relative;
  z-index: 1;
}

.wrapper .banner p {
  font-size: 3.4vw;
  color: #666;
  margin-bottom: 2.4vw;
  position: relative;
  z-index: 1;
}

.wrapper .banner a {
  font-size: 3vw;
  color: #c79060;
  font-weight: 700;
  position: relative;
  z-index: 1;
  text-decoration: none;
}

  /****************** 点餐分类部分 ******************/
.wrapper .foodtype {
  width: 100%;
  height: 48vw;

  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  /*要使用align-content。10个子元素将自动换行为两行，而且两行作为一个整体垂直居中*/
  align-content: center;
}

.wrapper .foodtype li {
  /*一共10个子元素，通过计算，子元素宽度在16.7 ~ 20 之间，才能保证换两行*/
  width: 18vw;
  height: 20vw;

  display: flex;
  /*弹性盒子主轴方向设为column，然后仍然是垂直水平方向居中*/
  flex-direction: column;
  justify-content: center;
  align-items: center;

  user-select: none;
  cursor: pointer;
}

.wrapper .foodtype li img {
  width: 12vw;
  /*视频讲解时高度设置为12vw，实际上设置为10.3vw更佳*/
  height: 10.3vw;
}

.wrapper .foodtype li p {
  font-size: 3.2vw;
  color: #666;
}
  
.wrapper .banner {
  /**
         * 设置容器宽度95%，然后水平居中，这样两边留白; 
         * 这里不能用padding，因为背景图片也会覆盖padding
         */
  width: 95%;
  margin: 0 auto;
  height: 29vw;

  /*此三个样式组合，可以保证背景图片充满整个容器*/
  background-image: url(../assets/index_banner.png);
  background-repeat: no-repeat;
  background-size: cover;

  box-sizing: border-box;
  padding: 2vw 6vw;
}

.wrapper .banner h3 {
  font-size: 4.2vw;
  margin-bottom: 1.2vw;
}

.wrapper .banner p {
  font-size: 3.4vw;
  color: #666;
  margin-bottom: 2.4vw;
}

.wrapper .banner a {
  font-size: 3vw;
  color: #c79060;
  font-weight: 700;
}

/****************** 超级会员部分 ******************/
.wrapper .supermember {
  /*这里也设置容器宽度95%，不能用padding，因为背景色也会充满padding*/
  width: 95%;
  margin: 0 auto;
  height: 11.5vw;
  background-color: #feedc1;
  margin-top: 1.3vw;
  border-radius: 2px;
  color: #644f1b;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .supermember .left {
  display: flex;
  align-items: center;
  margin-left: 4vw;
  user-select: none;
}

.wrapper .supermember .left img {
  width: 6vw;
  height: 6vw;
  margin-right: 2vw;
}

.wrapper .supermember .left h3 {
  font-size: 4vw;
  margin-right: 2vw;
}

.wrapper .supermember .left p {
  font-size: 3vw;
}

.wrapper .supermember .right {
  font-size: 3vw;
  margin-right: 4vw;
  cursor: pointer;
}
  
  /****************** 推荐商家部分 ******************/
  .wrapper .recommend {
    width: 100%;
    height: 14vw;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .wrapper .recommend .recommend-line {
    width: 6vw;
    height: 0.2vw;
    background-color: #888;
  }
  
  .wrapper .recommend p {
    font-size: 4vw;
    margin: 0 4vw;
  }
  
  /****************** 推荐方式部分（排序功能） ******************/
  .wrapper .recommendtype {
    width: 100%;
    height: 5vw;
    margin-bottom: 5vw;
  
    display: flex;
    justify-content: space-around;
    align-items: center;
  }
  
  .wrapper .recommendtype li {
    font-size: 3.5vw;
    color: #555;
    cursor: pointer;
    transition: all 0.3s ease;
    padding: 1vw 2vw;
    border-radius: 2vw;
  }

  /* 排序激活状态样式 */
  .wrapper .recommendtype li.active {
    color: #0097ff;
    background-color: rgba(0, 151, 255, 0.1);
    font-weight: bold;
  }

  .wrapper .recommendtype li:hover {
    background-color: #f5f5f5;
  }
  
  /****************** 推荐商家列表部分 ******************/
  .wrapper .business {
    width: 100%;
    margin-bottom: 14vw;
  }
  
  .wrapper .business li {
    width: 100%;
    box-sizing: border-box;
    padding: 2.5vw;
    user-select: none;
    border-bottom: solid 1px #ddd;
    cursor: pointer;
  
    display: flex;
    transition: background-color 0.2s ease;
  }

  /* 商家项hover效果 */
  .wrapper .business li:hover {
    background-color: #f9f9f9;
  }
  
  .wrapper .business li img {
    width: 18vw;
    height: 18vw;
  }
  
  .wrapper .business li .business-info {
    width: 100%;
    box-sizing: border-box;
    padding-left: 3vw;
  }
  
  .wrapper .business li .business-info .business-info-h {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2vw;
  }
  
  .wrapper .business li .business-info .business-info-h h3 {
    font-size: 4vw;
    color: #333;
  }
  
  .wrapper .business li .business-info .business-info-h .business-info-like {
    width: 1.6vw;
    height: 3.4vw;
    background-color: #666;
    color: #fff;
    font-size: 4vw;
    margin-right: 4vw;
  
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .wrapper .business li .business-info .business-info-star {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2vw;
    font-size: 3.1vw;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-star
    .business-info-star-left {
    display: flex;
    align-items: center;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-star
    .business-info-star-left
    .fa-star {
    color: #fec80e;
    margin-right: 0.5vw;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-star
    .business-info-star-left
    p {
    color: #666;
    margin-left: 1vw;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-star
    .business-info-star-right {
    background-color: #0097ff;
    color: #fff;
    font-size: 2.4vw;
    border-radius: 2px;
    padding: 0 0.6vw;
  }
  
  .wrapper .business li .business-info .business-info-delivery {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2vw;
  
    color: #666;
    font-size: 3.1vw;
  }
  
  .wrapper .business li .business-info .business-info-explain {
    display: flex;
    align-items: center;
    margin-bottom: 3vw;
  }
  
  .wrapper .business li .business-info .business-info-explain div {
    border: solid 1px #ddd;
    font-size: 2.8vw;
    color: #666;
    border-radius: 3px;
    padding: 0 0.1vw;
  }
  
  .wrapper .business li .business-info .business-info-promotion {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.8vw;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-promotion
    .business-info-promotion-left {
    display: flex;
    align-items: center;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-promotion
    .business-info-promotion-left
    .business-info-promotion-left-icon {
    width: 4vw;
    height: 4vw;
    background-color: #70bc46;
    border-radius: 3px;
    font-size: 3vw;
    color: #fff;
  
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-promotion
    .business-info-promotion-left
    p {
    color: #666;
    font-size: 3vw;
    margin-left: 2vw;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-promotion
    .business-info-promotion-right {
    display: flex;
    align-items: center;
    font-size: 2.5vw;
    color: #999;
  }
  
  .wrapper
    .business
    li
    .business-info
    .business-info-promotion
    .business-info-promotion-right
    p {
    margin-right: 2vw;
  }
  /* 让header可点击 */
.wrapper header {
  cursor: pointer;
  user-select: none;
}
/* 刮刮乐下拉界面 */
.scratch-dropdown {
  width: 100%;
  overflow: hidden;
  transition: all 0.3s ease-in-out;
  max-height: 0;
  opacity: 0;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
}

.scratch-dropdown.active {
  max-height: 100vh;
  opacity: 1;
}

.scratch-container {
  padding: 6vw;
  color: white;
}

.scratch-header {
  text-align: center;
  margin-bottom: 6vw;
}

.gift-icon {
  font-size: 12vw;
  margin-bottom: 2vw;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

.scratch-header h2 {
  font-size: 6vw;
  font-weight: bold;
  margin-bottom: 2vw;
}

.scratch-header p {
  font-size: 3.5vw;
  color: rgba(255, 255, 255, 0.8);
}

.scratch-card {
  background-color: white;
  border-radius: 3vw;
  padding: 6vw;
  margin: 0 auto;
  max-width: 80vw;
  position: relative;
}

.card-header {
  text-align: center;
  margin-bottom: 4vw;
}

.star-icon {
  font-size: 8vw;
  color: #ffd700;
  margin-bottom: 2vw;
}

.card-header h3 {
  font-size: 4.5vw;
  font-weight: bold;
  color: #333;
  margin: 0;
  padding: 0;
}

.scratch-area {
  position: relative;
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  border-radius: 2vw;
  padding: 8vw;
  text-align: center;
  min-height: 20vw;
}

.reward-display {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.reward-content {
  text-align: center;
}

.reward-amount {
  font-size: 8vw;
  font-weight: bold;
  color: white;
  margin-bottom: 2vw;
}

.reward-text {
  font-size: 3.5vw;
  color: rgba(255, 255, 255, 0.9);
}

.scratch-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  border-radius: 2vw;
  cursor: pointer;
}

.action-buttons {
  margin-top: 6vw;
  display: flex;
  flex-direction: column;
  gap: 3vw;
}

.use-coupon-btn {
  width: 100%;
  background-color: #4CAF50;
  color: white;
  font-weight: bold;
  padding: 3vw;
  border: none;
  border-radius: 2vw;
  font-size: 4vw;
  cursor: pointer;
  transition: background-color 0.3s;
}

.use-coupon-btn:hover {
  background-color: #45a049;
}

.back-home-btn {
  width: 100%;
  background-color: #666;
  color: white;
  font-weight: bold;
  padding: 3vw;
  border: none;
  border-radius: 2vw;
  font-size: 4vw;
  cursor: pointer;
  transition: background-color 0.3s;
}

.back-home-btn:hover {
  background-color: #555;
}

/* 支付页面样式 */
.payment-overlay {
  position: fixed;
  inset: 0;
  background-color: white;
  z-index: 1000;
  overflow-y: auto;
}

.payment-container {
  width: 100%;
  height: 100%;
}

.payment-header {
  width: 100%;
  height: 12vw;
  background-color: #0097ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  font-size: 4.5vw;
  font-weight: bold;
}

.payment-header .fa-arrow-left {
  position: absolute;
  left: 4vw;
  font-size: 6vw;
  cursor: pointer;
}

.payment-content {
  padding: 4vw;
  background-color: #f5f5f5;
  min-height: calc(100vh - 12vw);
}

.info-section {
  margin-bottom: 6vw;
}

.info-section h3 {
  font-size: 4vw;
  color: #666;
  margin-bottom: 3vw;
  padding: 0;
}

.order-info, .member-info, .payment-methods {
  background-color: white;
  border-radius: 2vw;
  padding: 4vw;
}

.order-info p {
  font-size: 3.5vw;
  color: #666;
  margin-bottom: 2vw;
  line-height: 1.5;
}

.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 4vw;
  color: #333;
}

.price-info {
  text-align: right;
}

.original-price {
  font-size: 3vw;
  color: #999;
  text-decoration: line-through;
}

.final-price {
  font-size: 4.5vw;
  color: #ff4444;
  font-weight: bold;
}

.coupon-discount {
  margin-top: 2vw;
  font-size: 3.5vw;
  color: #4CAF50;
}

.payment-method {
  display: flex;
  align-items: center;
  padding: 3vw;
  border: 1px solid #eee;
  border-radius: 2vw;
  margin-bottom: 3vw;
  cursor: pointer;
  transition: background-color 0.3s;
}

.payment-method:hover {
  background-color: #f9f9f9;
}

.payment-method:last-child {
  margin-bottom: 0;
}

.payment-method input[type="radio"] {
  margin-right: 3vw;
  transform: scale(1.2);
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

.payment-method span {
  font-size: 4vw;
  color: #333;
}

.payment-buttons {
  display: flex;
  flex-direction: column;
  gap: 3vw;
  margin-top: 5vw;
}

.confirm-pay-btn {
  width: 100%;
  background-color: #4CAF50;
  color: white;
  font-weight: bold;
  padding: 4vw;
  border: none;
  border-radius: 2vw;
  font-size: 4.5vw;
  cursor: pointer;
  transition: background-color 0.3s;
}

.confirm-pay-btn:hover {
  background-color: #45a049;
}

/* 响应式调整 */
@media (max-width: 480px) {
  .scratch-card {
    max-width: 90vw;
  }
  
  .reward-amount {
    font-size: 10vw;
  }
  
  .gift-icon {
    font-size: 15vw;
  }
}
</style>