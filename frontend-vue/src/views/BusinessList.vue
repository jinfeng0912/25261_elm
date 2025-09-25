<template>
  <div class="wrapper">
    <header>
      <p>{{ headerTitle }}</p>
    </header>

    <div v-if="loading" class="loading-state">
      <p>正在加载商家列表...</p>
    </div>

    <div v-else-if="error" class="empty-state">
      <p>{{ error }}</p>
      <button class="retry-btn" @click="fetchBusinessData">点击重试</button>
    </div>

    <ul v-else-if="businessArr.length > 0" class="business">
      <li v-for="item in businessArr" :key="item.id" @click="toBusinessInfo(item.id)">
        <div class="business-img">
          <img :src="item.businessImg" :alt="item.businessName">
        </div>
        <div class="business-info">
          <h3>{{ item.businessName }}</h3>
          <p>&#165;{{ item.startPrice }}起送 | &#165;{{ item.deliveryPrice }}配送</p>
          <p>{{ item.businessExplain }}</p>
        </div>
      </li>
    </ul>

    <div v-else class="empty-state">
      <p>该分类下暂无商家</p>
    </div>

    <Footer></Footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Footer from '../components/Footer.vue';

const axios = inject('axios');
const route = useRoute();
const router = useRouter();

// 响应式数据
const allBusinesses = ref([]); // 存储从后端获取的所有商家
const businessArr = ref([]);   // 存储筛选后要显示的商家
const loading = ref(true);
const error = ref(null);
const orderTypeId = route.query.orderTypeId;

// 食物类型名称映射
const foodTypeNames = {
  1: '美食', 2: '早餐', 3: '跑腿代购', 4: '汉堡披萨',
  5: '甜品饮品', 6: '速食简餐', 7: '地方小吃', 8: '米粉面馆',
  9: '包子粥铺', 10: '炸鸡炸串'
};

// 计算属性，用于动态显示标题
const headerTitle = computed(() => {
  if (loading.value) return '加载中...';
  if (error.value) return '出错了';
  const typeId = parseInt(orderTypeId);
  return foodTypeNames[typeId] || '商家列表';
});

// 从后端API获取所有商家数据
const fetchBusinessData = async () => {
  loading.value = true;
  error.value = null;
  console.log('=== 开始从后端获取商家数据 ===');
  try {
    const response = await axios.get('/api/businesses');
    if (response.data && response.data.code === 'OK' && Array.isArray(response.data.data)) {
      allBusinesses.value = response.data.data;
      console.log(`✅ 成功获取 ${allBusinesses.value.length} 家商家数据`);
      // 数据获取成功后，进行筛选显示
      filterBusinessList();
    } else {
      throw new Error(response.data.message || '获取商家数据格式不正确');
    }
  } catch (err) {
    console.error('❌ 获取商家数据失败:', err);
    error.value = '加载商家失败，请检查网络连接。';
  } finally {
    loading.value = false;
  }
};

// 根据类型筛选商家
const filterBusinessList = () => {
  console.log('根据类型ID进行筛选:', orderTypeId);
  if (orderTypeId && orderTypeId !== 'all') {
    const typeId = parseInt(orderTypeId);
    businessArr.value = allBusinesses.value.filter(business => business.orderTypeId === typeId);
    console.log(`筛选出 ${businessArr.value.length} 家 [${foodTypeNames[typeId]}] 类型的商家`);
  } else {
    // 如果没有类型ID，显示所有商家
    businessArr.value = allBusinesses.value;
    console.log('显示所有商家');
  }
};

// 跳转到商家详情页
const toBusinessInfo = (id) => {
  // 确保传递的是从数据库来的真实ID
  console.log('跳转到商家详情，真实ID:', id);
  router.push({
    path: '/businessInfo',
    query: { businessId: id }
  });
};

// 生命周期钩子
onMounted(() => {
  fetchBusinessData();
});
</script>

<style scoped>
.loading-state, .empty-state {
  text-align: center;
  padding: 20vw 0;
  color: #999;
  font-size: 4vw;
  margin-top: 12vw;
}
.retry-btn {
    background-color: #0097FF;
    color: white;
    border: none;
    border-radius: 2vw;
    padding: 3vw 6vw;
    font-size: 3.5vw;
    cursor: pointer;
    margin-top: 4vw;
    transition: background-color 0.2s ease;
}
.retry-btn:hover {
    background-color: #007ACC;
}
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
</style>