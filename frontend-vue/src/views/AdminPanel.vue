<template>
    <div class="wrapper">
      <header>
        <div class="header-left">
          <p>管理后台</p>
        </div>
        <div class="header-right">
          <span>{{ adminName }}</span>
          <button @click="logout">退出</button>
        </div>
      </header>
  
      <div class="menu-section">
        <div
          class="menu-item"
          :class="{ active: currentTab === 'business' }"
          @click="switchTab('business')"
        >
          <span>商家管理</span>
        </div>
  
        <div
          class="menu-item"
          :class="{ active: currentTab === 'food' }"
          @click="switchTab('food')"
        >
          <span>食品管理</span>
        </div>
  
        <div
          class="menu-item"
          :class="{ active: currentTab === 'analysis' }"
          @click="$router.push('/testPage')"
        >
          <span>智能分析</span>
        </div>
      </div>
  
      <div v-if="currentTab === 'business'" class="content">
        <div class="section-title">
          <h3>商家管理</h3>
          <button class="add-btn" @click="openAddBusinessModal">
            <i class="fa fa-plus"></i> 新增商家
          </button>
        </div>
  
        <div v-if="businessLoading" class="loading-message">加载商家...</div>
        <div v-else-if="businessError" class="error-message">
          <p>{{ businessError }}</p>
          <button @click="fetchBusinesses" class="filter-btn">重试</button>
        </div>
        <div v-else class="list-container card">
          <div class="list-header">
            <div class="col-id">ID</div>
            <div class="col-name">商家名称</div>
            <div class="col-explain">商家简介</div>
            <div class="col-address">地址</div>
            <div class="col-action">操作</div>
          </div>
          <div v-if="businessList.length === 0" class="no-data">暂无商家</div>
          <div v-else v-for="business in businessList" :key="business.businessId || business.id" class="list-item">
            <div class="col-id">{{ business.businessId || business.id }}</div>
            <div class="col-name">{{ business.businessName }}</div>
            <div class="col-explain">{{ business.businessExplain }}</div>
            <div class="col-address">{{ business.businessAddress }}</div>
            <div class="col-action">
              <button class="edit-btn" @click="openEditBusinessModal(business)">
                <i class="fa fa-edit"></i>
              </button>
              <button class="delete-btn" @click="deleteBusiness(business.businessId || business.id)">
                <i class="fa fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
  
      <div v-if="currentTab === 'food'" class="content">
        <div class="section-title">
          <h3>食品管理</h3>
          <div class="filter-section">
            <select v-model="selectedBusinessId" @change="fetchFoodsByBusiness" class="filter-select">
              <option value="">所有店铺</option>
              <option v-for="business in businessList" :key="business.id || business.businessId" :value="business.id || business.businessId">
                {{ business.businessName }}
              </option>
            </select>
            <button class="add-btn" @click="openAddFoodModal">
              <i class="fa fa-plus"></i> 新增食品
            </button>
          </div>
        </div>
  
        <div v-if="foodLoading" class="loading-message">加载食品...</div>
        <div v-else-if="foodError" class="error-message">
          <p>{{ foodError }}</p>
          <button @click="fetchFoodsByBusiness" class="filter-btn">重试</button>
        </div>
        <div v-else class="list-container card">
          <div class="list-header food-header">
            <div class="col-id">ID</div>
            <div class="col-name">食品名称</div>
            <div class="col-explain">食品简介</div>
            <div class="col-price">价格</div>
            <div class="col-img">图片</div>
            <div class="col-status">状态</div>
            <div class="col-action">操作</div>
          </div>
          <div v-if="foodList.length === 0" class="no-data">暂无食品 (请选择店铺或新增)</div>
          <div v-else v-for="food in foodList" :key="food.id" class="list-item food-item">
            <div class="col-id">{{ food.id }}</div>
            <div class="col-name">{{ food.foodName }}</div>
            <div class="col-explain">{{ food.foodExplain || '无简介' }}</div>
            <div class="col-price">¥{{ food.foodPrice.toFixed(2) }}</div>
            <div class="col-img">
              <img v-if="food.foodImg" :src="food.foodImg" :alt="food.foodName" class="food-img" />
              <span v-else class="no-img">无图片</span>
            </div>
            <div class="col-status">
              <span :class="getFoodStatusClass(food.deleted || food.status === 0)">{{ getFoodStatusText(food.deleted || food.status === 0) }}</span>
            </div>
            <div class="col-action">
              <button class="edit-btn" @click="openEditFoodModal(food)">
                <i class="fa fa-edit"></i> 编辑
              </button>
              <button
                :class="['toggle-btn', food.deleted || food.status === 0 ? 'enable-btn' : 'disable-btn']"
                @click="toggleFoodStatus(food.id)"
              >
                <i :class="['fa', food.deleted || food.status === 0 ? 'fa-check' : 'fa-times']"></i>
                {{ food.deleted || food.status === 0 ? '上架' : '下架' }}
              </button>
              <button class="delete-btn" @click="deleteFood(food.id)">
                <i class="fa fa-trash"></i> 删除
              </button>
            </div>
          </div>
        </div>
      </div>
  
      <div class="modal-overlay" v-if="showBusinessModal" @click="closeBusinessModal">
        <div class="modal-box" @click.stop>
          <h3>{{ isEditBusinessMode ? '编辑商家' : '新增商家' }}</h3>
          <div class="form-group">
            <label>商家名称：</label>
            <input type="text" v-model="currentBusiness.businessName" placeholder="请输入商家名称" />
          </div>
          <div class="form-group">
            <label>商家地址：</label>
            <input type="text" v-model="currentBusiness.businessAddress" placeholder="请输入商家地址" />
          </div>
          <div class="form-group">
            <label>商家简介：</label>
            <input type="text" v-model="currentBusiness.businessExplain" placeholder="请输入商家简介" />
          </div>
          <div class="form-group">
            <label>配送费用：</label>
            <input type="number" v-model="currentBusiness.deliveryPrice" step="0.01" placeholder="请输入配送费用" />
          </div>
          <div class="modal-buttons">
            <button class="cancel-btn" @click="closeBusinessModal">取消</button>
            <button class="confirm-btn" @click="saveBusiness" :disabled="!currentBusiness.businessName || !currentBusiness.businessAddress">
              {{ isEditBusinessMode ? '保存' : '新增' }}
            </button>
          </div>
        </div>
      </div>
  
      <div class="modal-overlay" v-if="showFoodModal" @click="closeFoodModal">
        <div class="modal-box" @click.stop>
          <h3>{{ isEditFoodMode ? '编辑食品' : '新增食品' }}</h3>
          <div class="form-group">
            <label>所属店铺：</label>
            <select v-model="currentFood.businessId" :disabled="isEditFoodMode" class="filter-select">
              <option value="">选择店铺</option>
              <option v-for="business in businessList" :key="business.id || business.businessId" :value="business.id || business.businessId">
                {{ business.businessName }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>食品名称 <span class="required">*</span>：</label>
            <input type="text" v-model="currentFood.foodName" placeholder="请输入食品名称" />
          </div>
          <div class="form-group">
            <label>食品简介：</label>
            <input type="text" v-model="currentFood.foodExplain" placeholder="请输入食品简介" maxlength="100" />
          </div>
          <div class="form-group">
            <label>食品价格 <span class="required">*</span>：</label>
            <input type="number" v-model="currentFood.foodPrice" step="0.01" placeholder="请输入食品价格" min="0" />
          </div>
          <div class="form-group">
            <label>食品图片：</label>
            <input type="file" @change="handleFoodImgUpload" accept="image/*" />
            <img v-if="currentFood.foodImgPreview" :src="currentFood.foodImgPreview" class="img-preview" />
            <small>支持 JPG/PNG, 建议 300x300px</small>
          </div>
          <div class="modal-buttons">
            <button class="cancel-btn" @click="closeFoodModal">取消</button>
            <button class="confirm-btn" @click="saveFood" :disabled="!currentFood.foodName || !currentFood.foodPrice || !currentFood.businessId">
              {{ isEditFoodMode ? '保存' : '新增' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        adminName: '管理员',
        currentTab: 'business',
        
        // 商家管理
        businessLoading: false,
        businessError: '',
        businessList: [],
        showBusinessModal: false,
        isEditBusinessMode: false,
        currentBusiness: {
          businessId: null,
          businessName: '',
          businessAddress: '',
          businessExplain: '',
          deliveryPrice: 0
        },
        
        // 食品管理
        foodLoading: false,
        foodError: '',
        selectedBusinessId: '',
        foodList: [],
        showFoodModal: false,
        isEditFoodMode: false,
        currentFood: {
          id: null,
          businessId: '',
          foodName: '',
          foodExplain: '',
          foodPrice: 0,
          foodImg: '',
          foodImgPreview: ''
        },
      }
    },
    mounted() {
      this.initialize();
    },
    methods: {
      async initialize() {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('您尚未登录，请先登录！');
          this.$router.push('/login');
          return;
        }
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        this.adminName = '管理员';
        await this.fetchBusinesses();
      },
  
      switchTab(tab) {
        this.currentTab = tab;
        if (tab === 'business' && this.businessList.length === 0) {
          this.fetchBusinesses();
        }
        if (tab === 'food') {
          this.fetchFoodsByBusiness();
        }
      },
      
      // 商家管理方法
      async fetchBusinesses() {
        this.businessLoading = true;
        this.businessError = '';
        try {
          const response = await axios.get('/api/businesses');
          if (response.data.success && response.data.code === 'OK') {
            this.businessList = response.data.data || [];
          } else {
            throw new Error(response.data.message || '格式异常');
          }
        } catch (error) {
          this.businessError = '加载商家失败: ' + (error.response?.data?.message || error.message);
          alert(this.businessError);
          this.businessList = [];
        } finally {
          this.businessLoading = false;
        }
      },
  
      openAddBusinessModal() {
        this.isEditBusinessMode = false;
        this.currentBusiness = {
          businessId: null,
          businessName: '',
          businessAddress: '',
          businessExplain: '',
          deliveryPrice: ''
        };
        this.showBusinessModal = true;
      },
  
      openEditBusinessModal(business) {
        this.isEditBusinessMode = true;
        this.currentBusiness = { ...business };
        this.showBusinessModal = true;
      },
  
      closeBusinessModal() {
        this.showBusinessModal = false;
      },
  
      async saveBusiness() {
        if (!this.currentBusiness.businessName.trim() || !this.currentBusiness.businessAddress.trim()) {
          alert('商家名称和地址不能为空');
          return;
        }
        try {
          let response;
          const payload = { ...this.currentBusiness };
          if (this.isEditBusinessMode) {
            response = await axios.put(`/api/businesses/${this.currentBusiness.businessId || this.currentBusiness.id}`, payload);
          } else {
            response = await axios.post('/api/businesses', payload);
          }
          if (response.data.success && response.data.code === 'OK') {
            alert(this.isEditBusinessMode ? '更新成功' : '新增成功');
            this.closeBusinessModal();
            await this.fetchBusinesses();
          } else {
            alert('操作失败: ' + (response.data.message || '响应异常'));
          }
        } catch (error) {
          alert('操作失败: ' + (error.response?.data?.message || error.message));
        }
      },
  
      async deleteBusiness(id) {
        if (!confirm('确定要删除这个商家吗？')) return;
        try {
          const response = await axios.delete(`/api/businesses/${id}`);
          if (response.data.success && response.data.code === 'OK') {
            alert('删除成功');
            await this.fetchBusinesses();
          } else {
            alert('删除失败: ' + (response.data.message || '响应异常'));
          }
        } catch (error) {
          alert('删除失败: ' + (error.response?.data?.message || error.message));
        }
      },
  
      // 食品管理方法
      async fetchFoodsByBusiness() {
        this.foodLoading = true;
        this.foodError = '';
        try {
          if (!this.businessList.length) {
            this.foodList = [];
            this.foodLoading = false;
            return;
          }
          const response = await axios.get('/api/foods');
          if (response.data.success && response.data.code === 'OK') {
            let foods = response.data.data || [];
            if (this.selectedBusinessId) {
              const businessId = parseInt(this.selectedBusinessId);
              foods = foods.filter(food => {
                const foodBusinessId = food.business?.id || food.businessId;
                return foodBusinessId === businessId;
              });
            }
            this.foodList = foods;
          } else {
            throw new Error(response.data.message || '格式异常');
          }
        } catch (error) {
          this.foodError = '加载食品失败: ' + (error.response?.data?.message || error.message);
          alert(this.foodError);
          this.foodList = [];
        } finally {
          this.foodLoading = false;
        }
      },
  
      openAddFoodModal() {
        this.isEditFoodMode = false;
        this.currentFood = {
          id: null,
          businessId: this.selectedBusinessId || '',
          foodName: '',
          foodExplain: '',
          foodPrice: '',
          foodImg: '',
          foodImgPreview: ''
        };
        this.showFoodModal = true;
      },
  
      openEditFoodModal(food) {
        this.isEditFoodMode = true;
        this.currentFood = {
          ...food,
          foodImgPreview: food.foodImg
        };
        this.selectedBusinessId = food.business ? food.business.id || food.business.businessId : '';
        this.showFoodModal = true;
      },
  
      closeFoodModal() {
        this.showFoodModal = false;
      },
  
      handleFoodImgUpload(event) {
        const file = event.target.files[0];
        if (file) {
          const reader = new FileReader();
          reader.onload = (e) => {
            this.currentFood.foodImgPreview = e.target.result;
            this.currentFood.foodImg = e.target.result.split(',')[1];
          };
          reader.readAsDataURL(file);
        }
      },
  
      async saveFood() {
        if (!this.currentFood.foodName.trim() || !this.currentFood.foodPrice || !this.currentFood.businessId) {
          alert('食品名称、价格和所属店铺不能为空');
          return;
        }
        try {
          let response;
          const payload = {
            ...this.currentFood,
            business: { id: this.currentFood.businessId }
          };
          if (this.isEditFoodMode) {
            response = await axios.put(`/api/foods/${this.currentFood.id}`, payload);
          } else {
            response = await axios.post('/api/foods', payload);
          }
          if (response.data.success && response.data.code === 'OK') {
            alert(this.isEditFoodMode ? '更新成功' : '新增成功');
            this.closeFoodModal();
            await this.fetchFoodsByBusiness();
          } else {
            alert('操作失败: ' + (response.data.message || '响应异常'));
          }
        } catch (error) {
          alert('操作失败: ' + (error.response?.data?.message || error.message));
        }
      },
  
      async deleteFood(id) {
        if (!confirm('确定要删除这个食品吗？ (软删除)')) return;
        try {
          const response = await axios.delete(`/api/foods/${id}`);
          if (response.data.success && response.data.code === 'OK') {
            alert('删除成功');
            await this.fetchFoodsByBusiness();
          } else {
            alert('删除失败: ' + (response.data.message || '响应异常'));
          }
        } catch (error) {
          alert('删除失败: ' + (error.response?.data?.message || error.message));
        }
      },
  
      async toggleFoodStatus(id) {
        const food = this.foodList.find(f => f.id === id);
        if (!food) return;
        const isDown = food.deleted;
        const action = isDown ? '上架' : '下架';
       
        if (!confirm(`确定要${action}这个食品吗？`)) return;
        try {
          const payload = { ...food, deleted: !isDown };
          const response = await axios.put(`/api/foods/${id}`, payload);
          if (response.data.success && response.data.code === 'OK') {
            alert(`${action}成功`);
            await this.fetchFoodsByBusiness();
          } else {
            alert(`${action}失败: ${response.data.message || '异常'}`);
          }
        } catch (error) {
          alert(`${action}失败: ${error.response?.data?.message || error.message}`);
        }
      },
      
      getFoodStatusText(deleted) {
        return deleted ? '下架' : '上架';
      },
  
      getFoodStatusClass(deleted) {
        return deleted ? 'status-canceled' : 'status-completed';
      },
  
      logout() {
        localStorage.clear();
        axios.defaults.headers.common['Authorization'] = null;
        this.$router.push('/login');
      }
    }
  }
  </script>
  
  <style scoped>
  /* 基础样式 */
  .wrapper {
    width: 100%;
    min-height: 100vh;
    background-color: #f8f9fa;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    color: #333;
  }
  
  /* Header */
  header {
    width: 100%;
    height: 64px;
    background: linear-gradient(135deg, #0097FF 0%, #007ACC 100%);
    color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    box-shadow: 0 2px 8px rgba(0, 151, 255, 0.2);
    box-sizing: border-box;
  }
  .header-left p {
    font-size: 20px;
    font-weight: 600;
  }
  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .header-right span {
    font-size: 16px;
  }
  .header-right button {
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    padding: 8px 16px;
    border-radius: 6px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  .header-right button:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-1px);
  }
  
  /* 菜单 */
  .menu-section {
    background-color: #fff;
    padding: 0 24px;
    display: flex;
    gap: 24px;
    border-bottom: 1px solid #eee;
  }
  .menu-item {
    padding: 16px 0;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 16px;
    font-weight: 500;
    color: #666;
    border-bottom: 3px solid transparent;
  }
  .menu-item:hover {
    color: #0097FF;
  }
  .menu-item.active {
    color: #0097FF;
    border-bottom-color: #0097FF;
  }
  
  /* 内容区 */
  .content {
    padding: 24px;
  }
  .section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }
  .section-title h3 {
    font-size: 22px;
    color: #2c3e50;
    font-weight: 600;
  }
  .add-btn {
    background-color: #28a745;
    color: #fff;
    border: none;
    padding: 10px 16px;
    border-radius: 6px;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: all 0.2s ease;
  }
  .add-btn:hover {
    background-color: #218838;
    transform: translateY(-1px);
  }
  .add-btn i {
    font-size: 14px;
  }
  .filter-section {
    display: flex;
    gap: 12px;
    align-items: center;
  }
  .filter-select {
    padding: 8px 12px;
    border: 1px solid #dee2e6;
    border-radius: 6px;
    font-size: 14px;
  }
  
  /* 列表容器 */
  .list-container.card {
    background: #fff;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
  .list-header, .list-item {
    display: grid;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #e9ecef;
    font-size: 14px;
  }
  .list-header {
    grid-template-columns: 0.5fr 1.5fr 2fr 2fr 1fr;
    background: #f8f9fa;
    font-weight: 600;
    color: #495057;
  }
  .list-item {
    grid-template-columns: 0.5fr 1.5fr 2fr 2fr 1fr;
    transition: background-color 0.2s ease;
  }
  .list-item:hover {
    background-color: #f0f8ff;
  }
  .list-item:last-child {
    border-bottom: none;
  }
  .food-header, .food-item {
    grid-template-columns: 0.5fr 1.5fr 2fr 1fr 1fr 1fr 2fr;
  }
  .col-id { font-weight: 600; color: #495057; }
  .col-explain, .col-address { color: #6c757d; }
  .col-price { font-weight: 500; color: #28a745; }
  .col-action { display: flex; gap: 8px; justify-content: flex-end; }
  
  /* 食品列表特定样式 */
  .col-img { text-align: center; }
  .food-img { width: 40px; height: 40px; object-fit: cover; border-radius: 4px; }
  .no-img { color: #999; font-size: 12px; }
  .col-status span {
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
  }
  .status-completed { background: #d4edda; color: #155724; }
  .status-canceled { background: #f8d7da; color: #721c24; }
  
  /* 按钮 */
  .edit-btn, .delete-btn, .toggle-btn {
    padding: 6px 12px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 12px;
    transition: all 0.2s ease;
  }
  .edit-btn { background-color: #ffc107; color: #212529; }
  .delete-btn { background-color: #dc3545; color: #fff; }
  .toggle-btn { min-width: 60px; }
  .enable-btn { background: #28a745; color: white; }
  .disable-btn { background: #6c757d; color: white; }
  .edit-btn:hover, .delete-btn:hover, .toggle-btn:hover {
    transform: scale(1.05);
  }
  
  /* 模态框 */
  .modal-overlay {
    position: fixed;
    top: 0; left: 0;
    width: 100%; height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  .modal-box {
    width: 90%;
    max-width: 500px;
    max-height: 80vh;
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    overflow-y: auto;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
    animation: fadeIn 0.2s ease;
  }
  @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
  .modal-box h3 { font-size: 20px; margin-bottom: 20px; text-align: center; }
  .form-group { margin-bottom: 20px; }
  .form-group label { display: block; font-size: 14px; margin-bottom: 6px; }
  .form-group input, .form-group select {
    width: 100%;
    padding: 12px;
    border: 1px solid #dee2e6;
    border-radius: 6px;
    font-size: 14px;
    box-sizing: border-box;
  }
  .img-preview { max-width: 100px; max-height: 100px; margin-top: 8px; border-radius: 4px; }
  .required { color: red; }
  .modal-buttons { display: flex; gap: 12px; margin-top: 24px; justify-content: flex-end; }
  .cancel-btn, .confirm-btn {
    padding: 12px 24px;
    border: none;
    border-radius: 6px;
    font-size: 14px;
    cursor: pointer;
  }
  .cancel-btn { background-color: #6c757d; color: #fff; }
  .confirm-btn { background-color: #0097FF; color: #fff; }
  .confirm-btn:disabled { background-color: #adb5bd; cursor: not-allowed; }
  </style>