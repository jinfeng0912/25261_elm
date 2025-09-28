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

    <div v-if="showPayment" class="payment-overlay">
      <div class="payment-container">
        <header class="payment-header">
          <i class="fa fa-arrow-left" @click="closePayment"></i>
          <span>超级会员支付</span>
        </header>

        <div class="payment-content">
          <div class="info-section">
            <h3>订单信息</h3>
            <div class="order-info">
              <p>订单号：{{ orderId }}</p>
              <p>下单时间：{{ orderTime }}</p>
            </div>
          </div>

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
  
      <div class="recommend">
            <div class="recommend-line"></div>
            <p>推荐商家</p>
            <div class="recommend-line"></div>
        </div>

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

        <ul class="business">
            <li 
                v-for="business in displayedBusinessList" 
                :key="business.id"
                @click="goToBusiness(business.id)"
            >
                <img :src="business.businessImg">
                <div class="business-info">
                    <div class="business-info-h">
                        <h3>{{ business.businessName }}</h3>
                        <div class="business-info-like">&#8226;</div>
                    </div>
                    <div class="business-info-star">
                        <div class="business-info-star-left">
                            <!-- <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i>
                            <i class="fa fa-star"></i> -->
                            <p>{{ business.rating }} 月售{{ business.monthlySales }}单</p>
                        </div>
                        <div class="business-info-star-right">
                            蜂鸟专送
                        </div>
                    </div>
                    <div class="business-info-delivery">
                        <p>&#165;{{ business.startPrice }}起送 | &#165;{{ business.deliveryPrice }}配送</p>
                        <p>{{ business.distance }}km | {{ getDeliveryTime(business.distance) }}{{ typeof getDeliveryTime(business.distance) === 'number' ? '分钟' : '' }}</p>
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
        <Footer></Footer>
    </div>
</template>
  
<script setup>
import Footer from "../components/Footer.vue";
import { ref, onMounted, onUnmounted, inject, nextTick, computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const axios = inject('axios');
const fixedBox = ref(null);
const businessData = ref([]);

// --- 新增：加载、错误、用户位置的状态 ---
const loading = ref(true);
const error = ref(null);
const userLocation = ref(null); // 用于存储用户的经纬度

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
const currentSort = ref('rating');

// 获取用户地理位置的函数
const getUserLocation = () => {
  return new Promise((resolve, reject) => {
    if (navigator.geolocation) {
      console.log('浏览器支持地理位置，正在请求授权...');
      navigator.geolocation.getCurrentPosition(
        (position) => {
          console.log('浏览器获取到的原始坐标:', position.coords);
          const coords = {
            lat: position.coords.latitude,
            lng: position.coords.longitude
          };
          console.log('成功获取用户坐标:', coords);
          userLocation.value = coords; // 保存坐标
          resolve(coords);
        },
        (err) => {
          let reason = '获取地理位置失败。';
          if (err.code === 1) { // code 1 代表用户拒绝授权
             reason = '您已拒绝地理位置授权，无法计算距离和按距离排序。';
          }
          console.warn('⚠️ 获取用户坐标失败:', reason);
          reject(new Error(reason));
        },
        { enableHighAccuracy: true, timeout: 8000, maximumAge: 0 } // 获取位置的配置
      );
    } else {
      reject(new Error('您的浏览器不支持地理位置功能。'));
    }
  });
};

// --- 改造：获取商家数据的函数 ---
const fetchBusinessData = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    // 1. 先尝试获取用户位置
    await getUserLocation();
  } catch (locationError) {
    // 如果获取位置失败，我们仍然可以加载商家列表，只是没有距离信息
    console.log('获取位置失败，将加载无距离的商家列表。');
    // 可以在页面上给用户一个提示
    // error.value = locationError.message; 
  }
  
  try {
    console.log('开始从后端获取商家数据...');
    
    // 2. 构造API请求参数，如果获取到了用户位置，就带上经纬度
    const params = {};
    if (userLocation.value) {
      params.userLat = userLocation.value.lat;
      params.userLng = userLocation.value.lng;
    }
    
    // 3. 发送带有参数的API请求
    const response = await axios.get('/api/businesses', { params });
    
    if (response.data && response.data.code === 'OK') {
      businessData.value = response.data.data;
      console.log(`成功获取 ${businessData.value.length} 家商家数据`,businessData.value);
      
    } else {
      throw new Error(response.data.message || '获取商家数据格式不正确');
    }
  } catch (fetchError) {
    console.error('❌ 获取商家数据失败:', fetchError);
    error.value = '加载商家列表失败，请检查网络连接。';
  } finally {
    loading.value = false;
  }
};

// --- 计算属性 (无需修改，但现在它可以正常工作了) ---
const displayedBusinessList = computed(() => {
  if (!businessData.value || businessData.value.length === 0) {
    return [];
  }
  
  const sortedList = [...businessData.value];
  
  switch (currentSort.value) {
    case 'distance':
      // 先过滤掉没有距离的商家（以防万一），再进行排序
      return sortedList.filter(b => b.distance != null).sort((a, b) => a.distance - b.distance);
    case 'sales':
      return sortedList.sort((a, b) => (b.monthlySales || 0) - (a.monthlySales || 0));
    case 'rating':
    default:
      return sortedList.sort((a, b) => (b.rating || 0) - (a.rating || 0));
  }
});

// --- 改造：排序方法，增加距离排序前的检查 ---
const sortBusinessBy = (sortType) => {
  if (sortType === 'distance' && !userLocation.value) {
    alert('需要您授权地理位置后才能按距离排序，请刷新页面重试。');
    return;
  }
  currentSort.value = sortType;
}

//估算配送时间
const getDeliveryTime = (distance) => {
  if (!distance || distance <= 0) {
    return 25; // 基础时间
  }
  
  // 按照平均速度估算 (例如：平均时速20km/h，即每公里3分钟)
  const timePerKm = 3; // 每公里分钟数
  const baseTime = 20; // 基础出餐、等候时间
  
  let estimatedTime = baseTime + Math.round(distance * timePerKm);

  // 为超长距离设置一个上限或特殊提示
  if (distance > 50) { // 如果距离大于50公里，这通常不是一个有效的外卖单
    return '距离过远，无法配送';
  }

  // 可以设置一个最长时间上限，例如90分钟
  if (estimatedTime > 90) {
    return 90;
  }
  
  return estimatedTime;
}

const goToSearch = () => router.push('/search');
const generateOrderId = () => "VIP" + Date.now() + Math.floor(Math.random() * 900 + 100);

// 刮刮乐相关变量
let canvas = null;
let ctx = null;
let isDrawing = false;
let lastX = 0;
let lastY = 0;

// 初始化画布
const initCanvas = () => {
  nextTick(() => {
    if (scratchCanvas.value && scratchArea.value) {
      canvas = scratchCanvas.value;
      ctx = canvas.getContext('2d');
      
      // 设置画布尺寸
      const rect = scratchArea.value.getBoundingClientRect();
      canvas.width = rect.width;
      canvas.height = rect.height;
      
      // 绘制刮刮乐涂层
      drawScratchLayer();
    }
  });
};

// 绘制刮刮乐涂层
const drawScratchLayer = () => {
  if (!ctx) return;
  
  // 创建渐变背景
  const gradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height);
  gradient.addColorStop(0, '#C0C0C0');
  gradient.addColorStop(0.5, '#D3D3D3');
  gradient.addColorStop(1, '#A9A9A9');
  
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, canvas.width, canvas.height);
  
  // 添加文字提示
  ctx.fillStyle = '#666';
  ctx.font = 'bold 16px Arial';
  ctx.textAlign = 'center';
  ctx.fillText('刮开涂层', canvas.width / 2, canvas.height / 2 - 10);
  ctx.fillText('查看奖励', canvas.width / 2, canvas.height / 2 + 10);
};

// 开始刮奖（鼠标）
const startScratch = (e) => {
  isDrawing = true;
  const rect = canvas.getBoundingClientRect();
  lastX = e.clientX - rect.left;
  lastY = e.clientY - rect.top;
};

// 处理刮奖（鼠标）
const handleScratch = (e) => {
  if (!isDrawing || !ctx) return;
  
  const rect = canvas.getBoundingClientRect();
  const currentX = e.clientX - rect.left;
  const currentY = e.clientY - rect.top;
  
  // 设置刮除效果
  ctx.globalCompositeOperation = 'destination-out';
  ctx.lineWidth = 20;
  ctx.lineCap = 'round';
  ctx.lineJoin = 'round';
  
  ctx.beginPath();
  ctx.moveTo(lastX, lastY);
  ctx.lineTo(currentX, currentY);
  ctx.stroke();
  
  lastX = currentX;
  lastY = currentY;
  
  // 检查刮开面积
  checkScratchProgress();
};

// 停止刮奖（鼠标）
const stopScratch = () => {
  isDrawing = false;
};

// 开始刮奖（触摸）
const startScratchTouch = (e) => {
  e.preventDefault();
  isDrawing = true;
  const rect = canvas.getBoundingClientRect();
  const touch = e.touches[0];
  lastX = touch.clientX - rect.left;
  lastY = touch.clientY - rect.top;
};

// 处理刮奖（触摸）
const handleScratchTouch = (e) => {
  if (!isDrawing || !ctx) return;
  
  e.preventDefault();
  const rect = canvas.getBoundingClientRect();
  const touch = e.touches[0];
  const currentX = touch.clientX - rect.left;
  const currentY = touch.clientY - rect.top;
  
  // 设置刮除效果
  ctx.globalCompositeOperation = 'destination-out';
  ctx.lineWidth = 20;
  ctx.lineCap = 'round';
  ctx.lineJoin = 'round';
  
  ctx.beginPath();
  ctx.moveTo(lastX, lastY);
  ctx.lineTo(currentX, currentY);
  ctx.stroke();
  
  lastX = currentX;
  lastY = currentY;
  
  // 检查刮开面积
  checkScratchProgress();
};

// 检查刮开进度
const checkScratchProgress = () => {
  if (!ctx) return;
  
  // 获取画布图像数据
  const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
  const data = imageData.data;
  
  let transparentPixels = 0;
  let totalPixels = data.length / 4;
  
  // 计算透明像素数量
  for (let i = 3; i < data.length; i += 4) {
    if (data[i] === 0) { // alpha 通道为 0 表示透明
      transparentPixels++;
    }
  }
  
  // 计算刮开百分比
  const scratchPercentage = (transparentPixels / totalPixels) * 100;
  scratchProgress.value = scratchPercentage;
  
  // 如果刮开面积超过 30%，显示结果
  if (scratchPercentage > 30 && !showResult.value) {
    showResult.value = true;
    // 随机生成红包金额
    couponAmount.value = Math.random() > 0.3 ? Math.floor(Math.random() * 5) + 1 : 0;
  }
};

// 重置刮刮乐状态
const resetScratchState = () => {
  showResult.value = false;
  couponAmount.value = 0;
  scratchProgress.value = 0;
  isDrawing = false;
  
  // 重新初始化画布
  setTimeout(() => {
    initCanvas();
  }, 100);
};

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
  if (isDropdownOpen.value) {
    // 打开时初始化画布
    setTimeout(() => {
      initCanvas();
    }, 300);
  } else {
    // 关闭时重置状态
    resetScratchState();
  }
};

const closeDropdown = () => {
  isDropdownOpen.value = false;
  resetScratchState();
};
const goToPayment = () => {
  orderId.value = generateOrderId();
  orderTime.value = new Date().toLocaleString('zh-CN');
  showPayment.value = true;
};
const closePayment = () => showPayment.value = false;

// 处理支付
const handlePayment = async () => {
  const paymentName = selectedPayMethod.value === 'alipay' ? '支付宝' : '微信支付';
  const finalAmount = (9.90 - couponAmount.value).toFixed(2);
  
  console.log('=== 开始超级会员支付 ===');
  console.log('订单号:', orderId.value);
  console.log('支付方式:', paymentName);
  console.log('原价: ¥9.90');
  console.log('红包优惠: -¥' + couponAmount.value.toFixed(2));
  console.log('实付金额: ¥' + finalAmount);
  
  try {
    // 显示支付处理提示
    alert(`正在使用${paymentName}支付超级会员，请稍候...`);
    
    // 模拟支付延迟
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // 模拟支付成功（90%成功率）
    const success = Math.random() > 0.1;
    
    if (success) {
      // 支付成功
      alert(`支付成功！\n\n订单号：${orderId.value}\n超级会员（30天）\n支付方式：${paymentName}\n实付金额：¥${finalAmount}\n\n感谢您的购买！`);
      
      // 保存会员信息到本地存储（演示用）
      const memberInfo = {
        orderId: orderId.value,
        orderTime: orderTime.value,
        paymentMethod: paymentName,
        amount: parseFloat(finalAmount),
        couponAmount: couponAmount.value,
        status: 'active',
        startDate: new Date().toISOString(),
        endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString() // 30天后
      };
      
      // 保存到本地存储
      const existingMembers = JSON.parse(localStorage.getItem('super_members') || '[]');
      existingMembers.push(memberInfo);
      localStorage.setItem('super_members', JSON.stringify(existingMembers));
      
      console.log('超级会员信息已保存:', memberInfo);
      
      // 关闭支付页面，返回首页
      goBackHome();
      
    } else {
      // 支付失败
      alert('支付失败，请重试或选择其他支付方式。');
    }
    
  } catch (error) {
    console.error('支付处理错误:', error);
    alert('支付过程中发生错误，请重试。');
  }
};

const goBackHome = () => {
  isDropdownOpen.value = false;
  showPayment.value = false;
  // 重置刮刮乐状态，允许重新刮奖
  resetScratchState();
};
const toSuperMember = () => router.push('/SuperMember');
const goToBusiness = (businessId) => router.push({ path: '/businessInfo', query: { businessId } });
const toBusinessList = (orderTypeId) => router.push({ path: "/businessList", query: { orderTypeId: orderTypeId } });
const goToFilter = () => router.push('/filter');


// --- 生命周期钩子 ---
onMounted(() => {
  // 页面加载时，自动执行获取位置和加载商家数据的流程
  fetchBusinessData();

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
});

onUnmounted(() => {
  document.onscroll = null;
});
</script>

<style scoped>
  
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
  
  /****************** 点餐分类部分 ******************/
.wrapper .foodtype {
  width: 100%;
  height: 48vw;

  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  align-content: center;
}

.wrapper .foodtype li {
  width: 18vw;
  height: 20vw;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  user-select: none;
  cursor: pointer;
}

.wrapper .foodtype li img {
  width: 12vw;
  height: 10.3vw;
}

.wrapper .foodtype li p {
  font-size: 3.2vw;
  color: #666;
}
  
/****************** 超级会员部分 ******************/
.wrapper .supermember {
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

.wrapper header {
  cursor: pointer;
  user-select: none;
}
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
}