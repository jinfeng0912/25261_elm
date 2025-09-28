<template>
  <div class="wrapper">
    <header>
      <p>搜索商家</p>
    </header>

    <div class="search-bar">
      <div class="search-box">
        <i class="fa fa-search"></i>
        <input
          v-model="keyword"
          @keyup.enter="handleSearch"
          placeholder="搜索饿了么商家、商品名称"
        />
      </div>
      <button @click="handleSearch">搜索</button>
    </div>

    <div v-if="history.length && filteredBusinesses.length === 0" class="section">
      <h4>历史搜索</h4>
      <div class="tags">
        <span
          v-for="(item, index) in history"
          :key="index"
          @click="searchFromTag(item)"
        >
          {{ item }}
        </span>
      </div>
    </div>

    <div v-if="filteredBusinesses.length === 0" class="section">
      <h4>搜索发现</h4>
      <div class="tags hot">
        <span @click="searchFromTag('饺子')">🔥 饺子</span>
        <span @click="searchFromTag('奶茶')">🔥 奶茶</span>
        <span @click="searchFromTag('麦当劳')">🔥 麦当劳</span>
      </div>
    </div>

    <div class="section">
      <h4 v-if="filteredBusinesses.length > 0">搜索结果</h4>
      
      <div v-if="loading" class="loading-state">正在加载...</div>
      
      <ul v-else-if="filteredBusinesses.length > 0" class="business">
        <li
          v-for="business in filteredBusinesses"
          :key="business.id"
          @click="goToBusiness(business.id)"
        >
          <img :src="business.businessImg" />
          <div class="business-info">
            <h3>{{ business.businessName }}</h3>
            <div class="business-info-star">
              <div class="business-info-star-left">
                <!-- <i v-for="n in 5" :key="n" class="fa fa-star" :style="{color: n <= business.rating ? '#fec80e' : '#ddd'}"></i> -->
                <p>月售{{ business.monthlySales || 0 }}单</p>
              </div>
            </div>
            <div class="business-info-delivery">
              <p>&#165;{{ business.startPrice || 0 }}起送 | &#165;{{ business.deliveryPrice || 0 }}配送</p>
              <p v-if="business.distance">{{ business.distance }}km | {{ getDeliveryTime(business.distance) }}分钟</p>
            </div>
          </div>
        </li>
      </ul>
      
      <p v-else-if="searched" class="empty-result">没有匹配的商家~</p>
    </div>

    <div v-if="filteredBusinesses.length === 0" class="section">
      <h4>为你推荐</h4>
      <ul class="business">
        <li
          v-for="biz in recommendedBusinesses"
          :key="'rec-' + biz.id"
          @click="goToBusiness(biz.id)"
        >
          <img :src="biz.businessImg" />
          <div class="business-info">
            <h3>{{ biz.businessName }}</h3>
             <div class="business-info-star">
              <div class="business-info-star-left">
                <!-- <i v-for="n in 5" :key="n" class="fa fa-star" :style="{color: n <= biz.rating ? '#fec80e' : '#ddd'}"></i> -->
                <!-- <p>{{ biz.rating || 0 }}</p> -->
              </div>
            </div>
            <div class="business-info-delivery">
              <p>&#165;{{ biz.startPrice || 0 }}起送 | &#165;{{ biz.deliveryPrice || 0 }}配送</p>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
// --- 脚本部分完全不变，只关注样式和模板 ---
import { ref, onMounted, computed, inject } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const axios = inject('axios');

const keyword = ref('')
const history = ref(['麦当劳', '饺子', '奶茶'])
const allBusinesses = ref([]) 
const filteredBusinesses = ref([]) 
const loading = ref(false)
const searched = ref(false) 

onMounted(() => {
  fetchBusinesses();
});

const fetchBusinesses = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/businesses');
    if (response.data && response.data.code === 'OK') {
      allBusinesses.value = response.data.data;
    }
  } catch (error) {
    console.error('获取商家数据失败:', error);
  } finally {
    loading.value = false;
  }
};

const recommendedBusinesses = computed(() => {
  return [...allBusinesses.value]
    .sort((a, b) => (b.rating || 0) - (a.rating || 0))
    .slice(0, 3);
});

const handleSearch = () => {
  searched.value = true;
  const key = keyword.value.trim();
  if (!key) {
    filteredBusinesses.value = [];
    return;
  }
  
  if (!history.value.includes(key)) {
    history.value.unshift(key);
    if (history.value.length > 8) {
      history.value = history.value.slice(0, 8);
    }
  }
  
  filteredBusinesses.value = allBusinesses.value.filter(b =>
    b.businessName.toLowerCase().includes(key.toLowerCase())
  );
};

const searchFromTag = (tag) => {
  keyword.value = tag;
  handleSearch();
};

const goToBusiness = (id) => {
  router.push({ 
    path: '/businessInfo', 
    query: { businessId: id } 
  });
};

const getDeliveryTime = (distance) => {
  if (!distance || distance <= 0) return 25;
  const timePerKm = 3;
  const baseTime = 20;
  let estimatedTime = baseTime + Math.round(distance * timePerKm);
  if (distance > 50) return '距离过远';
  if (estimatedTime > 90) return 90;
  return estimatedTime;
}
</script>

<style scoped>
/* --- 全局样式 --- */
.wrapper {
  width: 100%;
  height: 100%;
  background-color: #f5f5f5;
}

/* --- 顶栏 (来自index.vue) --- */
header {
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

/* --- 搜索栏 --- */
.search-bar {
  margin-top: 12vw;
  padding: 2vw 4vw;
  background-color: #0097FF;
  display: flex;
  gap: 8px;
}
.search-bar .search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 4px;
  padding: 0 2vw;
}
.search-bar .search-box .fa-search {
  color: #999;
}
.search-bar .search-box input {
  width: 100%;
  border: none;
  height: 9vw;
  padding: 0 2vw;
  font-size: 3.5vw;
}
.search-bar button {
  padding: 0 4vw;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 3.5vw;
  font-weight: bold;
}

/* --- 内容区域 --- */
.section {
  padding: 0 4vw;
  margin-top: 4vw;
  background-color: #fff;
  padding-bottom: 3vw;
}
.section h4 {
  font-size: 4vw;
  font-weight: bold;
  color: #333;
  margin: 0;
  padding: 3vw 0;
  border-bottom: 1px solid #f5f5f5;
}

/* --- 标签样式 --- */
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 2vw;
  padding-top: 3vw;
}
.tags span {
  padding: 1.5vw 3vw;
  background: #f5f5f5;
  border-radius: 4vw;
  font-size: 3.2vw;
  color: #555;
  cursor: pointer;
}
.tags.hot span {
  background-color: #fff0f0;
  color: #f56c6c;
}
.empty-result {
  text-align: center;
  color: #999;
  padding: 10vw 0;
}

/* --- 商家列表样式 (来自index.vue) --- */
.business {
  width: 100%;
}
.business li {
  width: 100%;
  box-sizing: border-box;
  padding: 2.5vw 0;
  user-select: none;
  border-bottom: solid 1px #f5f5f5;
  cursor: pointer;
  display: flex;
  align-items: center;
}
.business li:last-child {
  border-bottom: none;
}
.business li img {
  width: 18vw;
  height: 18vw;
  border-radius: 1vw;
  object-fit: cover;
}
.business li .business-info {
  margin-left: 3vw;
  flex: 1;
}
.business li .business-info h3 {
  font-size: 4vw;
  color: #333;
  margin-bottom: 1vw;
}
.business li .business-info .business-info-star {
  display: flex;
  align-items: center;
  margin-bottom: 1vw;
  font-size: 3.1vw;
}
.business li .business-info .business-info-star .business-info-star-left {
  display: flex;
  align-items: center;
}
.business li .business-info .business-info-star .business-info-star-left .fa-star {
  margin-right: 0.5vw;
}
.business li .business-info .business-info-star .business-info-star-left p {
  color: #666;
  margin-left: 1vw;
}
.business li .business-info .business-info-delivery {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #666;
  font-size: 3.1vw;
}

</style>