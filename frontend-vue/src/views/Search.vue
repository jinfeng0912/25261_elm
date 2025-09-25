<template>
  <div class="search-page">
    <div class="search-header">
      <input
        v-model="keyword"
        @keyup.enter="handleSearch"
        placeholder="🔍 搜索饿了么商家、商品名称"
      />
      <button @click="handleSearch">搜索</button>
    </div>

    <div v-if="history.length" class="section">
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

    <div class="section">
      <h4>搜索发现</h4>
      <div class="tags hot">
        <span @click="searchFromTag('饺子')">🔥 饺子</span>
        <span @click="searchFromTag('奶茶')">🔥 奶茶</span>
        <span @click="searchFromTag('麦当劳')">🔥 麦当劳</span>
      </div>
    </div>

    <div class="section">
      <h4>搜索结果</h4>
      <div v-if="loading" class="loading-state">正在搜索...</div>
      <div v-else-if="filteredBusinesses.length > 0" class="card-list">
        <div
          v-for="business in filteredBusinesses"
          :key="business.id"
          class="card"
          @click="goToBusiness(business.id)"
        >
          <img :src="business.businessImg" />
          <div class="info">
            <h3>{{ business.businessName }}</h3>
            <p>评分：{{ business.rating || 0 }} ★</p>
            <p>月售：{{ business.monthlySales || 0 }} 单</p>
            <p>距离：{{ business.distance || 0 }}km</p>
          </div>
        </div>
      </div>
      <p v-else-if="searched">没有匹配的商家~</p>
    </div>

    <div class="section">
      <h4>为你推荐</h4>
      <div class="card-list">
        <div
          v-for="biz in recommendedBusinesses"
          :key="'rec-' + biz.id"
          class="card"
          @click="goToBusiness(biz.id)"
        >
          <img :src="biz.businessImg" />
          <div class="info">
            <h3>{{ biz.businessName }}</h3>
            <p>评分：{{ biz.rating || 0 }} ★</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const axios = inject('axios');

// --- 响应式数据 ---
const keyword = ref('')
const history = ref(['麦当劳', '饺子', '奶茶'])
const allBusinesses = ref([]) // 存储从后端获取的所有商家
const filteredBusinesses = ref([]) // 存储搜索结果
const loading = ref(false)
const searched = ref(false) // 标记是否已执行过搜索

// --- 从后端获取数据 ---
onMounted(() => {
  fetchBusinesses();
});

const fetchBusinesses = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/businesses');
    if (response.data && response.data.code === 'OK') {
      allBusinesses.value = response.data.data;
      console.log('成功从后端获取所有商家数据用于搜索:', allBusinesses.value);
    }
  } catch (error) {
    console.error('获取商家数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// --- 计算属性 ---
// "为你推荐" 列表，这里简单地取前3个评分最高的商家
const recommendedBusinesses = computed(() => {
  return [...allBusinesses.value]
    .sort((a, b) => (b.rating || 0) - (a.rating || 0))
    .slice(0, 3);
});

// --- 方法 ---
const handleSearch = () => {
  searched.value = true; // 标记已搜索
  const key = keyword.value.trim();
  if (!key) {
    filteredBusinesses.value = [];
    return;
  }
  
  // 添加到搜索历史
  if (!history.value.includes(key)) {
    history.value.unshift(key);
    if (history.value.length > 8) {
      history.value = history.value.slice(0, 8);
    }
  }
  
  // 从已获取的商家列表中筛选
  filteredBusinesses.value = allBusinesses.value.filter(b =>
    b.businessName.toLowerCase().includes(key.toLowerCase())
  );
  
  console.log('搜索关键词:', key);
  console.log('搜索结果:', filteredBusinesses.value);
};

const searchFromTag = (tag) => {
  keyword.value = tag;
  handleSearch();
};

const goToBusiness = (id) => {
  console.log('=== 跳转到商家详情 (来自搜索页) ===');
  console.log('商家真实ID:', id);
  router.push({ 
    path: '/businessInfo', 
    query: { businessId: id } 
  });
};

</script>

<style scoped>
/* 您的样式代码保持不变，这里只添加加载状态的样式 */
.loading-state {
  padding: 20px;
  text-align: center;
  color: #999;
}
.search-page {
  padding: 14px;
  font-family: -apple-system, BlinkMacSystemFont, "Helvetica Neue", Arial, sans-serif;
  background: #f6f6f6;
}
.search-header {
  display: flex;
  gap: 8px;
  margin-bottom: 18px;
}
.search-header input {
  flex: 1;
  padding: 10px 14px;
  font-size: 15px;
  border-radius: 20px;
  border: 1px solid #ddd;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.search-header button {
  padding: 10px 16px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 18px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.2s ease;
}
.search-header button:hover {
  background-color: #337ecc;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}
.section {
  margin-bottom: 18px;
}
.section h4 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 6px 0 10px;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.tags span {
  padding: 6px 14px;
  background: #eee;
  border-radius: 20px;
  font-size: 13px;
  color: #444;
  cursor: pointer;
  transition: all 0.2s ease;
}
.tags span:hover {
  background: #ddd;
  transform: translateY(-1px);
}
.tags.hot span {
  background-color: #ffecec;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}
.tags.hot span:hover {
  background-color: #ffe0e0;
  border-color: #f7a3a3;
}
.card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.card {
  display: flex;
  gap: 12px;
  background: white;
  padding: 12px;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.2s ease;
}
.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  background: #fafafa;
}
.card img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
}
.card .info {
  flex: 1;
}
.card .info h3 {
  font-size: 16px;
  margin-bottom: 4px;
  color: #333;
}
.card .info p {
  font-size: 13px;
  color: #666;
  margin: 2px 0;
}
</style>