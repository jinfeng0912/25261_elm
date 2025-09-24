<template>
  <div class="search-page">
    <!-- 搜索框 -->
    <div class="search-header">
      <input
        v-model="keyword"
        @keyup.enter="handleSearch"
        placeholder="🔍 搜索饿了么商家、商品名称"
      />
      <button @click="handleSearch">搜索</button>
    </div>

    <!-- 历史搜索 -->
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

    <!-- 推荐关键词 -->
    <div class="section">
      <h4>搜索发现</h4>
      <div class="tags hot">
        <span @click="searchFromTag('饺子')">🔥 饺子</span>
        <span @click="searchFromTag('奶茶')">🔥 奶茶</span>
        <span @click="searchFromTag('麦当劳')">🔥 麦当劳</span>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="section">
      <h4>搜索结果</h4>
      <div v-if="filteredBusinesses.length" class="card-list">
        <div
          v-for="business in filteredBusinesses"
          :key="business.businessId"
          class="card"
          @click="goToBusiness(business.businessId)"
        >
          <img :src="getImage(business.businessImg)" />
          <div class="info">
            <h3>{{ business.businessName }}</h3>
            <p>评分：{{ business.rating }} ★</p>
            <p>月售：{{ business.monthlySales }} 单</p>
            <p>距离：{{ business.distance }}</p>
          </div>
        </div>
      </div>
      <p v-else-if="keyword">没有匹配的商家~</p>
    </div>

    <!-- 为你推荐模块 -->
    <div class="section">
      <h4>为你推荐</h4>
      <div class="card-list">
        <div
          v-for="(biz, index) in businesses.slice(0, 3)"
          :key="'sp-' + biz.businessId"
          class="card"
          @click="goToBusiness(biz.businessId)"
        >
          <img :src="getImage(biz.businessImg)" />
          <div class="info">
            <h3>{{ index + 1 }}. {{ biz.businessName }}</h3>
            <p>推荐数：{{ biz.monthlySales * 100 }}</p>
            <p>评分：{{ biz.rating }} ★</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const keyword = ref('')
const history = ref(['麦当劳', '饺子', '奶茶'])

// ========== 修改：完善商家数据，确保与首页一致 ==========
const businesses = ref([
  {
    businessId: 10001,
    businessName: '万家饺子（软件园E18店）',
    businessImg: 'sj01.png',
    rating: 4.5,
    monthlySales: 156,
    distance: '1.2km'
  },
  {
    businessId: 10007,
    businessName: '茶合鲜果饮品（国际软件园）',
    businessImg: 'sj07.png', // 修正图片名称
    rating: 4.1,
    monthlySales: 98,
    distance: '1.5km'
  },
  {
    businessId: 10003,
    businessName: '麦当劳麦乐送（全运店）',
    businessImg: 'sj03.png',
    rating: 4.6,
    monthlySales: 234,
    distance: '1.8km'
  },
  {
    businessId: 10005,
    businessName: '申记串道（中海康城店）',
    businessImg: 'sj05.png',
    rating: 4.4,
    monthlySales: 145,
    distance: '2.7km'
  },
  {
    businessId: 10004,
    businessName: '米村拌饭（浑南店）',
    businessImg: 'sj04.png',
    rating: 4.2,
    monthlySales: 67,
    distance: '3.1km'
  },
  {
    businessId: 10002,
    businessName: '小锅饭豆腐馆（全运店）',
    businessImg: 'sj02.png',
    rating: 4.3,
    monthlySales: 89,
    distance: '2.3km'
  },
  {
    businessId: 10006,
    businessName: '半亩良田排骨米饭',
    businessImg: 'sj06.png',
    rating: 4.7,
    monthlySales: 312,
    distance: '4.2km'
  }
])
// ========== 商家数据修改结束 ==========

const filteredBusinesses = ref([])

// ========== 修改：完善搜索功能 ==========
const handleSearch = () => {
  const key = keyword.value.trim()
  if (!key) {
    filteredBusinesses.value = []
    return
  }
  
  // 添加到搜索历史
  if (!history.value.includes(key)) {
    history.value.unshift(key)
    // 限制历史记录数量
    if (history.value.length > 8) {
      history.value = history.value.slice(0, 8)
    }
  }
  
  // 搜索商家（支持名称和简介搜索）
  filteredBusinesses.value = businesses.value.filter(b =>
    b.businessName.toLowerCase().includes(key.toLowerCase())
  )
  
  console.log('搜索关键词:', key)
  console.log('搜索结果:', filteredBusinesses.value)
}
// ========== 搜索功能修改结束 ==========

const searchFromTag = (tag) => {
  keyword.value = tag
  handleSearch()
}

// ========== 修改：确保跳转功能正确 ==========
const goToBusiness = (id) => {
  console.log('=== 跳转到商家详情 ===')
  console.log('商家ID:', id)
  
  // 跳转到商家详情页面
  router.push({ 
    path: '/businessInfo', 
    query: { businessId: id } 
  })
}
// ========== 跳转功能修改结束 ==========

// ========== 修改：完善图片获取功能 ==========
const getImage = (imgName) => {
  try {
    return require(`@/assets/${imgName}`)
  } catch (error) {
    console.warn('图片加载失败:', imgName)
    // 返回默认图片
    return require('@/assets/sj01.png')
  }
}
// ========== 图片功能修改结束 ==========
</script>

<style scoped>
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
}

/* ========== 新增：按钮悬停效果 ========== */
.search-header button:hover {
  background-color: #337ecc;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}
/* ========== 按钮效果结束 ========== */

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

/* ========== 新增：标签悬停效果 ========== */
.tags span:hover {
  background: #ddd;
  transform: translateY(-1px);
}
/* ========== 标签效果结束 ========== */

.tags.hot span {
  background-color: #ffecec;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

/* ========== 新增：热门标签悬停效果 ========== */
.tags.hot span:hover {
  background-color: #ffe0e0;
  border-color: #f7a3a3;
}
/* ========== 热门标签效果结束 ========== */

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

/* ========== 新增：卡片悬停效果 ========== */
.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  background: #fafafa;
}
/* ========== 卡片效果结束 ========== */

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