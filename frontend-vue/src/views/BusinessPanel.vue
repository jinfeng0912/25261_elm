<template>
  <div class="wrapper">
    <header>
      <div class="header-left">
        <p>商家管理后台</p>
      </div>
      <div class="header-right">
        <span>{{ adminName }}</span>
        <button @click="logout">退出</button>
      </div>
    </header>

    <div class="menu-section">
      <div
        class="menu-item"
        :class="{ active: currentTab === 'order' }"
        @click="switchTab('order')"
      >
        <span>订单管理</span>
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
        :class="{ active: currentTab === 'data' }"
        @click="switchTab('data')"
      >
        <span>数据管理</span>
      </div>
      
    </div>

    <!-- 加载中或无权限提示 -->
    <div v-if="isLoading" class="loading-message">加载商家信息...</div>
    <div v-else-if="!business" class="error-message">
      <p>权限不足或无关联店铺。</p>
      <button @click="logout">重新登录</button>
      <p v-if="errorMsg">{{ errorMsg }}</p>
    </div>

    <div v-if="currentTab === 'order' && business" class="content">
      </div>

    <div v-if="currentTab === 'food' && business" class="content">
      <div class="section-title">
        <h3>食品管理 (店铺: {{ business.businessName }})</h3>
        <button class="add-btn" @click="openAddFoodModal">
          <i class="fa fa-plus"></i> 新增食品
        </button>
      </div>

      <div v-if="foodLoading" class="loading-message">加载食品...</div>
      <div v-else-if="foodList.length === 0" class="error-message">暂无食品</div>
      <div else class="food-list">
        <div class="list-header">
          <div class="col-id">ID</div>
          <div class="col-name">食品名称</div>
          <div class="col-explain">食品简介</div>
          <div class="col-price">价格</div>
          <div class="col-action">操作</div>
        </div>
        
         <div class="list-item" v-for="food in foodList" :key="food.id">
          <div class="col-id">{{ food.id }}</div>
          <div class="col-name">{{ food.foodName }}</div>
          <div class="col-explain">{{ food.foodExplain }}</div>
          <div class="col-price">¥{{ food.foodPrice }}</div>
          <div class="col-action">
            <button class="edit-btn" @click="openEditFoodModal(food)">
              <i class="fa fa-edit"></i>
            </button>
            <button class="delete-btn" @click="deleteFood(food.id)">
              <i class="fa fa-trash"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="modal-overlay" v-if="showFoodModal" @click="closeFoodModal">
        <div class="business-modal" @click.stop>
          <h3>{{ isEditMode ? '编辑食品' : '新增食品' }}</h3>
          
          <div class="form-group">
            <label>食品名称：</label>
            <input type="text" v-model="currentFood.foodName" placeholder="请输入食品名称" />
          </div>
          
          <div class="form-group">
            <label>食品简介：</label>
            <input type="text" v-model="currentFood.foodExplain" placeholder="请输入食品简介" />
          </div>
          
          <div class="form-group">
            <label>食品价格：</label>
            <input type="number" v-model="currentFood.foodPrice" step="0.01" placeholder="请输入食品价格" />
          </div>
          
          <div class="modal-buttons">
            <button class="cancel-btn" @click="closeFoodModal">取消</button>
            <button class="confirm-btn" @click="saveFood" :disabled="!currentFood.foodName || !currentFood.foodPrice">
              {{ isEditMode ? '保存' : '新增' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="currentTab === 'data' && business" class="content">
      <!-- 数据内容... (你的原代码) -->
      <div class="stats-cards">
        <!-- ... 统计卡片 ... -->
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      adminName: '商家管理员',
      currentTab: 'food',
      business: null,
      isLoading: true,  // 加载商家
      foodLoading: false,  // 加载食品
      errorMsg: '',  // 错误提示

      // 订单管理相关
      orderList: [
        {
          orderId: 'ORD20230001',
          userName: '测试用户1',
          userPhone: '13800138001',
          orderTime: '2023-01-01 12:00:00',
          orderStatus: 5,
          orderAmount: 35,
          deliveryAddress: '北京市海淀区中关村大街1号',
          items: [
            { foodId: 1, foodName: '测试食品1', quantity: 2, foodPrice: 15 },
            { foodId: 2, foodName: '测试食品2', quantity: 1, foodPrice: 20 }
          ],
          foodAmount: 50,
          deliveryPrice: 5,
        },
        {
          orderId: 'ORD20230002',
          userName: '测试用户2',
          userPhone: '13800138002',
          orderTime: '2023-01-02 13:00:00',
          orderStatus: 2,
          orderAmount: 48,
          deliveryAddress: '北京市朝阳区建国路88号',
          items: [
            { foodId: 3, foodName: '测试食品3', quantity: 3, foodPrice: 16 }
          ],
          foodAmount: 48,
          deliveryPrice: 0,
        }
      ],
      orderFilter: {
        status: '',
        startDate: '',
        endDate: ''
      },
      orderPage: 1,
      orderPageSize: 10,
      showOrderDetailModal: false,
      currentOrder: {
        orderId: '',
        userName: '',
        userPhone: '',
        orderTime: '',
        orderStatus: '',
        deliveryAddress: '',
        items: [],
        foodAmount: 0,
        deliveryPrice: 0,
        orderAmount: 0
      },

      // 食品管理相关
      foodList: [],  // 真实食品列表
      showFoodModal: false,
      isEditMode: false,
      currentFood: {
        id: null,
        foodName: '',
        foodExplain: '',
        foodPrice: '',
        business: null
      },
      
      // 数据管理相关
      statistics: {
        totalOrders: 125,
        totalRevenue: 3568,
        topFoodCount: 45,
        orderChange: 15,
        revenueChange: 20,
        foodChange: 10
      },
      statsStartDate: '',
      statsEndDate: '',
      topFoods: [
        { foodId: 1, foodName: '测试食品1', sales: 45 },
        { foodId: 2, foodName: '测试食品2', sales: 38 },
        { foodId: 3, foodName: '测试食品3', sales: 32 },
        { foodId: 4, foodName: '测试食品4', sales: 28 },
        { foodId: 5, foodName: '测试食品5', sales: 25 }
      ],
      orderTrends: [
        { date: '周一', count: 35 },
        { date: '周二', count: 42 },
        { date: '周三', count: 38 },
        { date: '周四', count: 45 },
        { date: '周五', count: 52 },
        { date: '周六', count: 48 },
        { date: '周日', count: 40 }
      ]
      
    }
    //isDev: true  // 开发模式开关，生产设 false
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

      // 关键：设置 axios header 携带 token
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      console.log('已设置 token header:', token.substring(0, 20) + '...');

      this.adminName = '商家管理员';  // 或从 localStorage userInfo 取
      await this.loadBusinessInfo();
    },

    async loadBusinessInfo() {
      this.isLoading = true;
      this.errorMsg = '';
      try {
        console.log('调用 /api/businesses/my，带 token...');
        const response = await axios.get('/api/businesses/my');
        console.log('响应完整:', response.data);  // 调试：打印全响应

        // 修改：检查 success true 和 code 'OK' (匹配你的 HttpResult)
        if (response.data.success === true && response.data.code === 'OK') {
          const businesses = response.data.data || [];
          console.log('商家列表:', businesses);  // 调试：看 Array(1)

          if (businesses.length > 0) {
            this.business = businesses[0];  // 取第一个店铺
            console.log("成功加载商家:", this.business);  // 调试：店铺详情
            await this.loadFoods();  // 加载食品
            this.isLoading = false;
          } else {
            this.errorMsg = '当前账号无关联商家，请创建店铺。';  // 友好提示，不 throw
            console.warn('无店铺数据');
            this.isLoading = false;
          }
        } else {
          // 非 OK 响应
          throw new Error(response.data.message || '响应格式异常');
        }
      } catch (error) {
        console.error('加载商家信息API失败:', error);
        this.errorMsg = '加载失败: ' + (error.response?.data?.message || error.message);
        this.isLoading = false;
        // 不立即 logout：显示 errorMsg，让用户手动退出
        alert(this.errorMsg + '，建议重新登录。');
      }
    },

    // 增强：加全日志，处理空列表
    async loadFoods() {
      if (!this.business) {
        console.warn('7. 无商家，跳过 loadFoods');
        this.foodLoading = false;
        return;
      }

      this.foodLoading = true;
      console.log('8. 开始调用 /api/foods/my (商家ID:', this.business.id, ')');  // 日志8
      try {
        const response = await axios.get('/api/foods/my');  // 带 token
        console.log('9. 食品响应:', response.data);  // 日志9

        if (response.data.success === true && response.data.code === 'OK') {
          this.foodList = response.data.data || [];
          console.log('10. 食品加载成功:', this.foodList.length, '个');  // 日志10
          // 如果空，UI 会提示 "暂无食品"
        } else {
          console.warn('11. 食品响应非 OK:', response.data);
          throw new Error(response.data.message || '格式异常');
        }
      } catch (error) {
        console.error('12. 食品加载失败:', error);  // 关键日志：如果错误，这里打印
        console.error('响应状态:', error.response?.status);
        console.error('响应数据:', error.response?.data);
        alert('食品列表加载失败: ' + (error.response?.data?.message || error.message));
        this.foodList = [];  // 清空，避免旧数据
      } finally {
        this.foodLoading = false;
        console.log('13. loadFoods 结束，foodList.length:', this.foodList.length);  // 日志13
      }
    },

    
    

    // 打开新增模态框
    openAddFoodModal() {
      this.isEditMode = false;
      this.currentFood = {
        id: null,
        foodName: '',
        foodExplain: '',
        foodPrice: '',
      };
      this.showFoodModal = true;
    },

    // 打开编辑模态框
    openEditFoodModal(food) {
      this.isEditMode = true;
      this.currentFood = { ...food };
      this.showFoodModal = true;
    },
    
    // 关闭模态框
    closeFoodModal() {
      this.showFoodModal = false;
    },

    
    // 修复 saveFood：统一响应检查
    async saveFood() {
      if (!this.business) {
        alert("商家信息未加载，无法保存食品！");
        return;
      }
      if (!this.currentFood.foodName || !this.currentFood.foodPrice) {
        alert('食品名称和价格不能为空');
        return;
      }

      try {
        let response;
        if (this.isEditMode) {
          response = await axios.put(`/api/foods/${this.currentFood.id}`, this.currentFood);
        } else {
          const newFood = {
            ...this.currentFood,
            business: { id: this.business.id }  // 确保
          };
          console.log('14. 保存食品 payload:', newFood);  // 调试
          response = await axios.post('/api/foods', newFood);
        }

        if (response.data.success === true && response.data.code === 'OK') {
          alert(this.isEditMode ? '更新成功' : '新增成功');
          this.closeFoodModal();
          await this.loadFoods();  // 刷新
        } else {
          alert('操作失败: ' + (response.data.message || '响应异常'));
        }
      } catch (error) {
        console.error('15. 保存食品失败:', error);
        alert('操作失败: ' + (error.response?.data?.message || error.message));
      }
    },

    // deleteFood 类似修复...
    async deleteFood(foodId) {
      if (confirm('确定要删除这个食品吗？')) {
        try {
          const response = await axios.delete(`/api/foods/${foodId}`);
          if (response.data.success === true && response.data.code === 'OK') {
            alert('删除成功');
            await this.loadFoods();
          } else {
            alert('删除失败: ' + (response.data.message || '响应异常'));
          }
        } catch (error) {
          console.error('删除失败:', error);
          alert('删除失败: ' + (error.response?.data?.message || error.message));
        }
      }
    },
    
    // 数据管理方法
    loadStatistics() {
      console.log('加载统计数据');
    },
    
    getChangeClass(change) {
      return change >= 0 ? 'positive' : 'negative';
    },
    
    getChangeIcon(change) {
      return change >= 0 ? 'fa-arrow-up' : 'fa-arrow-down';
    },

    switchTab(tab) {
        this.currentTab = tab;
      if (!this.business && tab !== 'data') {
        alert('请先加载店铺');
        return;
      }
      if (tab === 'food' && this.foodList.length === 0 && this.business) {
        console.log('16. 切换到 food tab，懒加载食品');  // 日志16
        this.loadFoods();  // 确保加载
      }
      
    },

    logout() {
      localStorage.clear();  // 清 token
      axios.defaults.headers.common['Authorization'] = null;  // 清 header
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
  background-color: #f5f5f5;
  font-family: 'Arial', sans-serif;
}

/* Header */
header {
  width: 100%;
  height: 60px;
  background-color: #0097FF;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-sizing: border-box;
}

.header-left p {
  font-size: 18px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-right span {
  font-size: 16px;
}

.header-right button {
  background-color: rgba(255,255,255,0.2);
  color: #fff;
  border: 1px solid #fff;
  padding: 5px 15px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.header-right button:hover {
  background-color: rgba(255,255,255,0.3);
}

/* 菜单 */
.menu-section {
  background-color: #fff;
  padding: 15px;
  display: flex;
  gap: 15px;
  border-bottom: 1px solid #eee;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #666;
}

.menu-item:hover,
.menu-item.active {
  background-color: #0097FF;
  color: #fff;
}

.menu-item i {
  font-size: 16px;
}

/* 内容区 */
.content {
  padding: 20px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.filter-section {
  display: flex;
  gap: 10px;
}

.filter-section select,
.filter-section input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-range input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.date-range span {
  font-size: 14px;
  color: #666;
}

.add-btn {
  background-color: #28a745;
  color: #fff;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: background-color 0.3s;
}

.add-btn:hover {
  background-color: #218838;
}

.add-btn i {
  font-size: 14px;
}

/* 列表样式 */
.order-list,
.food-list {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.loading-message, .error-message {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  color: #666;
  background-color: #fff;
  border-radius: 8px;
}

.list-header,
.list-item {
  display: grid;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.order-list .list-header,
.order-list .list-item {
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
}

.food-list .list-header,
.food-list .list-item {
  grid-template-columns: 80px 2fr 3fr 1fr 120px;
}

.list-header {
  background-color: #f8f9fa;
  font-weight: bold;
  color: #666;
}

.list-item:hover {
  background-color: #f9f9f9;
}

.col-id, .col-name, .col-explain, .col-price, 
.col-user, .col-amount, .col-time, .col-status {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.col-action {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.edit-btn,
.delete-btn,
.detail-btn {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: opacity 0.3s;
}

.edit-btn:hover,
.delete-btn:hover,
.detail-btn:hover {
  opacity: 0.8;
}

.edit-btn {
  background-color: #ffc107;
  color: #fff;
}

.delete-btn {
  background-color: #dc3545;
  color: #fff;
}

.detail-btn {
  background-color: #17a2b8;
  color: #fff;
  padding: 5px 10px;
  width: auto;
}

.status-pending {
  color: #ffc107;
}

.status-paid {
  color: #17a2b8;
}

.status-accepted {
  color: #007bff;
}

.status-delivering {
  color: #6f42c1;
}

.status-completed {
  color: #28a745;
}

.status-canceled {
  color: #dc3545;
}

.status-unknown {
  color: #6c757d;
}

/* 分页控件 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.pagination button {
  background-color: #0097FF;
  color: #fff;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.pagination button:hover {
  background-color: #007bff;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination span {
  font-size: 14px;
  color: #333;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.order-detail-modal,
.business-modal {
  width: 90%;
  max-width: 600px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  max-height: 80vh;
  overflow-y: auto;
}

.order-detail-modal h3,
.business-modal h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.form-group input,
.form-group select {
  width: 100%;
  height: 40px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0 10px;
  font-size: 14px;
  box-sizing: border-box;
}

.modal-buttons {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn,
.confirm-btn {
  flex: 1;
  height: 40px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.cancel-btn:hover,
.confirm-btn:hover {
  opacity: 0.9;
}

.cancel-btn {
  background-color: #6c757d;
  color: #fff;
}

.confirm-btn {
  background-color: #0097FF;
  color: #fff;
}

/* 订单详情样式 */
.order-info .info-row {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
}

.order-info .label {
  width: 80px;
  color: #666;
}

.order-info .value {
  flex: 1;
}

.order-items {
  margin: 20px 0;
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.order-items h4 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
}

.order-items .item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.order-summary {
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.order-summary .summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}

.order-summary .total {
  font-weight: bold;
  color: #333;
  font-size: 16px;
}

/* 数据统计样式 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.card {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.card:hover {
  transform: translateY(-5px);
}

.card-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.card-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 3px;
}

.card-change.positive {
  color: #28a745;
}

.card-change.negative {
  color: #dc3545;
}

.top-lists {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.top-list {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.top-list h4 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
  text-align: center;
}

.top-list .list-item,
.top-list .trend-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.top-list .list-item:last-child,
.top-list .trend-item:last-child {
  border-bottom: none;
}

.top-list .rank {
  width: 25px;
  height: 25px;
  background: #0097FF;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  margin-right: 10px;
}

.top-list .name,
.top-list .date {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.top-list .count {
  font-size: 14px;
  color: #666;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr 1fr;
  }
  
  .top-lists {
    grid-template-columns: 1fr;
  }
  
  .order-list .list-header,
  .order-list .list-item {
    grid-template-columns: 1fr 1fr 1fr;
  }
  
  .col-status, .col-time {
    display: none;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .food-list .list-header,
  .food-list .list-item {
    grid-template-columns: 60px 1fr 1fr;
  }
  
  .col-explain {
    display: none;
  }
  
  .menu-section {
    flex-direction: column;
    gap: 10px;
  }
  
  .menu-item {
    justify-content: center;
  }
}
</style>