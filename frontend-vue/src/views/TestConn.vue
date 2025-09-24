<template>
  <div>
    <h1>Business List</h1>
    <button @click="fetchBusinesses">Get Businesses</button>
    <div v-if="loading">Loading...</div>
    <div v-if="error" class="error">{{ error }}</div>
    
    <!-- 新增：API响应信息显示区域 -->
    <div v-if="apiResponse" class="api-response">
      <h3>API Response Info:</h3>
      <p><strong>Status Code:</strong> {{ apiResponse.status }}</p>
      <div class="json-display">
        <strong>Response Data:</strong>
        <pre>{{ JSON.stringify(apiResponse.data, null, 2) }}</pre>
      </div>
    </div>

    <ul v-if="businesses.length">
      <li v-for="business in businesses" :key="business.id">
        {{ business.name }}
      </li>
    </ul>
    <div v-else-if="!loading && !error && businesses.length === 0">
      No businesses found
    </div>
  </div>
</template>

<script>
import axios from 'axios';

// 根据环境设置基础URL
const apiClient = axios.create({
  baseURL : '/api'
});

export default {
  name: 'BusinessList',
  data() {
    return {
      businesses: [],
      loading: false,
      error: null,
      apiResponse: null // 新增：存储完整的API响应
    };
  },
  methods: {
    async fetchBusinesses() {
   
      try {
        const response = await apiClient.get('/businesses');
        this.apiResponse = response;
        this.businesses = response.data;
        
      } catch (err) {
        this.error = `Error ${err.response?.status}: ${
          err.response?.data?.message || err.message
        }`;
        
        // 错误响应也显示在控制台和页面上
        if (err.response) {
          console.error('API Error Status:', err.response.status);
          console.error('API Error Data:', err.response.data);
          this.apiResponse = err.response;
        } else {
          console.error('API Error:', err.message);
        }
      } finally {
        this.loading = false;
      }
    }
  },
};
</script>

<style scoped>
.api-response {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 5px;
  border-left: 4px solid #42b983;
}

.json-display {
  margin-top: 10px;
}

.json-display pre {
  background-color: #fff;
  padding: 10px;
  border-radius: 3px;
  overflow-x: auto;
  font-size: 14px;
}

.error {
  color: #ff4444;
  background-color: #ffe6e6;
  padding: 10px;
  border-radius: 3px;
  margin: 10px 0;
}
</style>