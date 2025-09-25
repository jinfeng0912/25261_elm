<template>
  <div>
    <h1>Business List</h1>
    <div class="button-group">
      <button @click="fetchBusinesses">测试获取商家列表</button>
      <button @click="testDeleteBusiness" class="delete-button">测试删除商家</button>
      <button @click="testLogin" class="login-button">测试登录功能</button>
      <button @click="fetchFoods">测试获取食品列表</button>
      <button @click="testStorage">测试本地存储</button>
    </div>
    <div v-if="loading">Loading...</div>
    <div v-if="error" class="error">{{ error }}</div>
    
    <!-- API响应信息显示区域 -->
    <div v-if="apiResponse" class="api-response">
      <h3>API Response Info:</h3>
      <p><strong>Status Code:</strong> {{ apiResponse.status }}</p>
      <p><strong>Request URL:</strong> {{ apiResponse.config.url }}</p>
      <p><strong>Request Method:</strong> {{ apiResponse.config.method }}</p>
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
import { saveAuthData, saveIdToken, saveId, saveRole, getId, getRole, getIdToken } from '@/utils/storage'

const apiClient = axios.create({
  baseURL: '/api'
});

export default {
  name: 'BusinessList',
  data() {
    return {
      businesses: [],
      loading: false,
      error: null,
      apiResponse: null
    };
  },
  methods: {
    async testStorage() {
      this.loading = true;
      this.error = null;
      this.apiResponse = null;
      
      try {
        console.group('🧪 存储功能测试开始');
        
        // 1. 测试保存功能
        console.log('📝 测试保存数据...');
        const testData = {
          id_token: 'test_token_' + Date.now(),
          id: 'test_user_' + Math.floor(Math.random() * 1000),
          role: 'tester'
        };
        
        saveAuthData(testData);
        console.log('✅ 数据保存成功:', testData);
        
        // // 2. 测试读取功能
        console.log('🔍 测试单个读取数据...');
        console.log(getIdToken());
        console.log(getId());
        console.log(getRole());

        // 5. 测试单个保存功能
        console.log('📝 测试单个数据保存...');
        const newToken = 'updated_token_' + Date.now();
        saveIdToken(newToken);
        console.log('✅ Token更新成功:', newToken);
        
        const newRole = 'admin';
        saveRole(newRole);
        console.log('✅ Role更新成功:', newRole);
        
        
        console.groupEnd();
        
        // 9. 显示成功消息
        this.successMessage = '存储功能测试完成！所有操作成功。';
        console.log('🎉 ' + this.successMessage);
        
      } catch (error) {
        console.error('❌ 存储测试出错:', error);
        this.error = `测试失败: ${error.message}`;
        this.successMessage = null;
        
        // 详细的错误日志
        console.group('错误详情');
        console.error('错误名称:', error.name);
        console.error('错误信息:', error.message);
        console.error('错误堆栈:', error.stack);
        console.groupEnd();
        
      } finally {
        this.loading = false;
        
        // 测试完成后清理（可选）
        setTimeout(() => {
          console.log('🧹 测试完成，清理临时数据...');
          // clearAuthData(); // 如果需要清理取消注释
          console.log('✅ 清理完成');
        }, 3000);
      }
    },
    async fetchBusinesses() {
      this.loading = true;
      this.error = null;
      this.apiResponse = null;
      
      try {
        const response = await apiClient.get('/businesses');
        this.apiResponse = response;
        this.businesses = response.data;
      } catch (err) {
        this.handleError(err);
      } finally {
        this.loading = false;
      }
    },
    async fetchFoods() {
      this.loading = true;
      this.error = null;
      this.apiResponse = null;
      
      try {
        const response = await apiClient.get('/foods');
        this.apiResponse = response;
        console.log(response.data);
      } catch (err) {
        this.handleError(err);
      } finally {
        this.loading = false;
      }
    },
    async testReadLocalStore() {
      this.loading = true;
      this.error = null;
      this.apiResponse = null;
      // 2. 初始化 store 实例
      const userStore = sessionLoginUserStore()

      // 3. 读取数据
      console.log('用户Token:', userStore.getSessionUser.token)
      console.log('用户ID:', userStore.getSessionUser.userId)
      console.log('用户名:', userStore.getSessionUser.userName)      
      
    },
    
    async testDeleteBusiness() {
      const testBusinessId = 11;
      if (!confirm(`确定要删除ID为 ${testBusinessId} 的商家吗？`)) return;
      
      this.loading = true;
      this.error = null;
      this.apiResponse = null;
      
      try {
        const response = await apiClient.delete(`/businesses/${testBusinessId}`);
        this.apiResponse = response;
        alert('删除成功！');
        this.fetchBusinesses();
      } catch (err) {
        this.handleError(err);
      } finally {
        this.loading = false;
      }
    },
    
    // 新增的测试登录方法
    async testLogin() {
      this.loading = true;
      this.error = null;
      this.apiResponse = null;
      
      try {
        const requestBody = {
        "username": "admin",
        "password": "admin",
        "rememberMe": true
      }
        
        const response = await axios.post('api/auth', requestBody, {
          headers: {
            'Content-Type': 'application/json'
          }
        });
        
        this.apiResponse = response;
        console.log('登录成功:', response.data);
        alert('登录成功！id_token: ' + (response.data.id_token || '无Token返回'));
      } catch (err) {
        this.handleError(err);
        console.error('登录失败:', err);
      } finally {
        this.loading = false;
      }
    },
    
    // 提取公共错误处理方法
    handleError(err) {
      this.error = `Error ${err.response?.status}: ${
        err.response?.data?.message || err.message
      }`;
      
      if (err.response) {
        console.error('API Error Status:', err.response.status);
        console.error('API Error Data:', err.response.data);
        this.apiResponse = err.response;
      } else {
        console.error('API Error:', err.message);
      }
    }
  }
};
</script>

<style scoped>
.button-group {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.delete-button {
  background-color: #ff4444;
  color: white;
}

.delete-button:hover {
  background-color: #cc0000;
}

.login-button {
  background-color: #4285f4;
  color: white;
}

.login-button:hover {
  background-color: #3367d6;
}

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