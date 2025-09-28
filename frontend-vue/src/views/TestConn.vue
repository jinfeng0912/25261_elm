<template>
  <div class="container">
    <div class="header">
      <h1 class="title">Business List</h1>
      <p class="subtitle">API 测试与管理界面</p>
    </div>
    
    <div class="button-group">
  <button @click="fetchBusinesses" class="btn success">
    测试获取商家列表
  </button>
  <button @click="testDeleteBusiness" class="btn success">
    测试删除商家
  </button>
  <button @click="testLogin" class="btn success">
    测试auth获取id_token
  </button>
  <button @click="fetchFoods" class="btn success">
    测试获取食品列表
  </button>
  <button @click="testStorage" class="btn success">
    测试存储功能
  </button>
  <button @click="gotoAdminPanel" class="btn success">
  测试管理员面板
  </button>
  <button @click="gotoBusinessPanel" class="btn success">
    测试商家面板
  </button>
  <button @click="gotoUserAI" class="btn success">
    测试用户AI助手
  </button>
  <button @click="gotoBusinessAI" class="btn success">
    测试商家AI助手
  </button>
  <button @click="gotoAdminAI" class="btn success">
    测试管理员AI数据分析
  </button>
  <button @click="showUpload = true" class="btn success">
    测试图片上传
  </button>
</div>

    <!-- 图片上传模态框 -->
    <transition name="fade">
      <div v-if="showUpload" class="upload-modal">
        <div class="upload-content">
          <div class="modal-header">
            <h3><i class="fas fa-image"></i> 图片拖拽上传测试</h3>
            <button @click="closeUpload" class="close-icon">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div 
            class="drop-zone"
            @dragover.prevent="isDragging = true"
            @dragleave.prevent="isDragging = false"
            @drop.prevent="handleDrop"
            :class="{ 'dragging': isDragging }"
            @click="triggerFileInput"
          >
            <div class="drop-content">
              <i class="fas fa-cloud-upload-alt"></i>
              <p>拖拽图片到此处或<em>点击选择文件</em></p>
              <small>支持 JPEG, PNG, GIF (最大25MB)</small>
            </div>
            <input 
              type="file" 
              ref="fileInput"
              @change="handleFileSelect"
              accept="image/*"
              style="display: none"
            >
          </div>
          <div v-if="previewImage" class="preview-area">
            <div class="preview-container">
              <img :src="previewImage" class="preview-image">
              <div class="image-info">
                <div class="info-item">
                  <i class="fas fa-file-alt"></i>
                  <span>文件名: {{ selectedFile.name }}</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-weight-hanging"></i>
                  <span>文件大小: {{ formatFileSize(selectedFile.size) }}</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-info-circle"></i>
                  <span>文件类型: {{ selectedFile.type }}</span>
                </div>
              </div>
            </div>
            <div class="action-buttons">
              <button @click="clearImage" class="btn danger">
                <i class="fas fa-trash"></i> 清除
              </button>
              <button @click="closeUpload" class="btn secondary">
                <i class="fas fa-check"></i> 完成
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <!-- 错误提示 -->
    <transition name="fade">
      <div v-if="error" class="error-message">
        <i class="fas fa-exclamation-circle"></i>
        <span>{{ error }}</span>
      </div>
    </transition>
    
    <!-- API 响应信息 -->
    <transition name="slide">
      <div v-if="apiResponse" class="api-response-card">
        <div class="response-header">
          <h3><i class="fas fa-code"></i> API 响应信息</h3>
        </div>
        <div class="response-body">
          <div class="response-item">
            <span class="label">状态码:</span>
            <span class="value" :class="{'success': apiResponse.status >= 200 && apiResponse.status < 300, 'error': apiResponse.status >= 400}">
              {{ apiResponse.status }}
            </span>
          </div>
          <div class="response-item">
            <span class="label">请求URL:</span>
            <span class="value">{{ apiResponse.config.url }}</span>
          </div>
          <div class="response-item">
            <span class="label">请求方法:</span>
            <span class="value">{{ apiResponse.config.method.toUpperCase() }}</span>
          </div>
          <div class="response-item full-width">
            <span class="label">响应数据:</span>
            <div class="json-display">
              <pre>{{ JSON.stringify(apiResponse.data, null, 2) }}</pre>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- 商家列表 -->
    <div v-if="businesses.length" class="business-list">
      <div class="list-header">
        <h3><i class="fas fa-store"></i> 商家列表</h3>
        <span class="badge">{{ businesses.length }} 家</span>
      </div>
      <ul class="business-items">
        <li v-for="business in businesses" :key="business.id" class="business-item">
          <div class="business-info">
            <span class="business-name">{{ business.name }}</span>
            <span class="business-id">ID: {{ business.id }}</span>
          </div>
          <i class="fas fa-chevron-right"></i>
        </li>
      </ul>
    </div>
    
    <div v-else-if="!loading && !error && businesses.length === 0" class="empty-state">
      <i class="fas fa-search"></i>
      <p>当前无数据</p>
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
      apiResponse: null,
      // 上传相关数据
      showUpload: false,
      isDragging: false,
      previewImage: null,
      selectedFile: null
    };
  },
  methods: {
    // 上传相关方法 - 纯前端实现
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    handleFileSelect(e) {
      const file = e.target.files[0];
      if (file) {
        this.handleFile(file);
      }
    },
    handleDrop(e) {
      this.isDragging = false;
      const file = e.dataTransfer.files[0];
      if (file) {
        this.handleFile(file);
      }
    },
    handleFile(file) {
      // 验证文件类型
      if (!file.type.match('image.*')) {
        alert('请选择图片文件 (JPEG, PNG, GIF等)');
        return;
      }

      // 验证文件大小 (限制5MB)
      const maxSize = 25 * 1024 * 1024;
      if (file.size > maxSize) {
        alert('图片大小不能超过25MB');
        return;
      }

      this.selectedFile = file;
      
      // 使用FileReader生成预览
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewImage = e.target.result;
      };
      reader.readAsDataURL(file);
    },
    clearImage() {
      this.previewImage = null;
      this.selectedFile = null;
      this.$refs.fileInput.value = ''; // 清除input的值
    },
    closeUpload() {
      this.showUpload = false;
      this.clearImage();
      this.isDragging = false;
    },
    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes';
      const k = 1024;
      const sizes = ['Bytes', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },

    // 原有方法保持不变
    gotoUserAI() {
      this.$router.push('/myLLM');
    },
    gotoBusinessAI() {
      this.$router.push('/myLLMBusinessHelper');
    },
    gotoAdminAI() {
      this.$router.push('/testPage');
    },
    gotoAdminPanel() {
      this.$router.push('/adminPanel');
    },
    gotoBusinessPanel() {
      this.$router.push('/businessPanel');
    },
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
        // alert('登录成功！id_token: ' + (response.data.id_token || '无Token返回'));
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
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');

:root {
  --primary: #4361ee;
  --primary-light: #3f37c9;
  --secondary: #3a0ca3;
  --success: #4cc9f0;
  --info: #4895ef;
  --warning: #f72585;
  --danger: #f72585;
  --dark: #212529;
  --light: #f8f9fa;
  --accent: #7209b7;
  --gray: #6c757d;
  --gray-light: #e9ecef;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Poppins', sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.header {
  text-align: center;
  margin-bottom: 2rem;
}

.title {
  font-size: 2.5rem;
  font-weight: 600;
  color: var(--primary);
  margin-bottom: 0.5rem;
  background: linear-gradient(90deg, var(--primary), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  font-size: 1rem;
  color: var(--gray);
  font-weight: 300;
}

.button-group {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-bottom: 2rem;
  justify-content: center;
}

.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem 1.25rem;
  border: none;
  border-radius: 50px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.btn:active {
  transform: translateY(0);
}

.icon {
  margin-right: 0.5rem;
  font-size: 0.9rem;
}

.primary {
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  color: white;
}

.secondary {
  background: linear-gradient(135deg, var(--secondary), var(--dark));
  color: white;
}

.success {
  background: linear-gradient(135deg, #4cc9f0, #4895ef);
  color: white;
}

.info {
  background: linear-gradient(135deg, #4895ef, #4361ee);
  color: white;
}

.warning {
  background: linear-gradient(135deg, #f8961e, #f3722c);
  color: white;
}

.danger {
  background: linear-gradient(135deg, var(--danger), #b5179e);
  color: white;
}

.dark {
  background: linear-gradient(135deg, var(--dark), #343a40);
  color: white;
}

.accent {
  background: linear-gradient(135deg, var(--accent), #560bad);
  color: white;
}

/* 上传模态框 */
.upload-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.upload-content {
  background-color: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
}

.modal-header h3 {
  font-size: 1.25rem;
  font-weight: 500;
  margin: 0;
}

.modal-header i {
  margin-right: 0.5rem;
}

.close-icon {
  background: none;
  border: none;
  color: white;
  font-size: 1.25rem;
  cursor: pointer;
  transition: transform 0.2s;
}

.close-icon:hover {
  transform: rotate(90deg);
}

.drop-zone {
  border: 2px dashed var(--gray-light);
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  margin: 1.25rem;
  transition: all 0.3s;
  background-color: #fafafa;
}

.drop-zone.dragging {
  border-color: var(--primary);
  background-color: rgba(67, 97, 238, 0.05);
}

.drop-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.drop-zone i {
  font-size: 2.5rem;
  color: var(--primary);
  margin-bottom: 1rem;
}

.drop-zone p {
  margin: 0.5rem 0;
  color: var(--dark);
  font-size: 1rem;
}

.drop-zone p em {
  color: var(--primary);
  font-style: normal;
  font-weight: 500;
  text-decoration: underline;
}

.drop-zone small {
  color: var(--gray);
  font-size: 0.8rem;
}

.preview-area {
  padding: 0 1.25rem 1.25rem;
}

.preview-container {
  border: 1px solid var(--gray-light);
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 1rem;
}

.preview-image {
  width: 100%;
  max-height: 250px;
  object-fit: contain;
  background-color: #f5f5f5;
}

.image-info {
  padding: 1rem;
  background-color: var(--light);
  border-top: 1px solid var(--gray-light);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item i {
  margin-right: 0.75rem;
  color: var(--primary);
  width: 1.25rem;
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(67, 97, 238, 0.2);
  border-radius: 50%;
  border-top-color: var(--primary);
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-container p {
  color: var(--gray);
  font-weight: 300;
}

/* 错误提示 */
.error-message {
  display: flex;
  align-items: center;
  padding: 1rem;
  background-color: #fff0f0;
  border-left: 4px solid var(--danger);
  border-radius: 4px;
  margin: 1rem 0;
  color: var(--danger);
}

.error-message i {
  margin-right: 0.75rem;
  font-size: 1.25rem;
}

/* API 响应卡片 */
.api-response-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin: 1.5rem 0;
  overflow: hidden;
}

.response-header {
  padding: 1rem 1.5rem;
  background-color: var(--light);
  border-bottom: 1px solid var(--gray-light);
}

.response-header h3 {
  font-size: 1.1rem;
  font-weight: 500;
  margin: 0;
  color: var(--dark);
}

.response-header i {
  color: var(--primary);
  margin-right: 0.75rem;
}

.response-body {
  padding: 1.5rem;
}

.response-item {
  display: flex;
  margin-bottom: 1rem;
}

.response-item.full-width {
  flex-direction: column;
}

.label {
  font-weight: 500;
  color: var(--dark);
  min-width: 100px;
}

.value {
  color: var(--gray);
  word-break: break-word;
}

.value.success {
  color: var(--success);
  font-weight: 500;
}

.value.error {
  color: var(--danger);
  font-weight: 500;
}

.json-display {
  background-color: #f8f9fa;
  border-radius: 4px;
  padding: 1rem;
  margin-top: 0.5rem;
  overflow-x: auto;
}

.json-display pre {
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.85rem;
  line-height: 1.5;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 商家列表 */
.business-list {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 1.5rem;
  overflow: hidden;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background-color: var(--light);
  border-bottom: 1px solid var(--gray-light);
}

.list-header h3 {
  font-size: 1.1rem;
  font-weight: 500;
  margin: 0;
  color: var(--dark);
}

.list-header i {
  color: var(--primary);
  margin-right: 0.75rem;
}

.badge {
  background-color: var(--primary);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 500;
}

.business-items {
  list-style: none;
}

.business-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--gray-light);
  transition: background-color 0.2s;
}

.business-item:last-child {
  border-bottom: none;
}

.business-item:hover {
  background-color: var(--light);
}

.business-info {
  display: flex;
  flex-direction: column;
}

.business-name {
  font-weight: 500;
  color: var(--dark);
}

.business-id {
  font-size: 0.8rem;
  color: var(--gray);
}

.business-item i {
  color: var(--gray);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  text-align: center;
  color: var(--gray);
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: var(--gray-light);
}

.empty-state p {
  font-size: 1.1rem;
  font-weight: 300;
}

/* 过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}

.slide-enter-active, .slide-leave-active {
  transition: all 0.3s ease;
}
.slide-enter, .slide-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }
  
  .title {
    font-size: 2rem;
  }
  
  .button-group {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .btn {
    width: 100%;
  }
  
  .upload-content {
    width: 95%;
  }
}
</style>