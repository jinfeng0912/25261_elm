<template>
  <div class="wrapper">
    <!-- Header -->
    <header>
      <div class="header-left">
        <p>商家管理后台</p>
      </div>
      <div class="header-right">
        <span>{{ adminName }}</span>
        <button @click="logout">退出</button>
      </div>
    </header>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <div 
        class="menu-item" 
        :class="{ active: currentTab === 'order' }"
        @click="switchTab('order')"
      >
        <i class="fa fa-list-alt"></i>
        <span>订单管理</span>
      </div>
      <div 
        class="menu-item" 
        :class="{ active: currentTab === 'food' }"
        @click="switchTab('food')"
      >
        <i class="fa fa-cutlery"></i>
        <span>食品管理</span>
      </div>
      <div 
        class="menu-item" 
        :class="{ active: currentTab === 'data' }"
        @click="switchTab('data')"
      >
        <i class="fa fa-chart-bar"></i>
        <span>数据管理</span>
      </div>
    </div>

    <!-- 订单管理 -->
    <div v-if="currentTab === 'order'" class="content">
      <div class="section-title">
        <h3>订单管理</h3>
        <div class="filter-section">
          <select v-model="orderFilter.status" @change="loadOrders">
            <option value="">全部状态</option>
            <option value="1">待支付</option>
            <option value="2">已支付</option>
            <option value="3">已接单</option>
            <option value="4">配送中</option>
            <option value="5">已完成</option>
            <option value="6">已取消</option>
          </select>
          <input type="date" v-model="orderFilter.startDate" @change="loadOrders">
          <input type="date" v-model="orderFilter.endDate" @change="loadOrders">
        </div>
      </div>

      <div class="order-list">
        <div class="list-header">
          <div class="col-id">订单号</div>
          <div class="col-user">用户</div>
          <div class="col-amount">金额</div>
          <div class="col-time">下单时间</div>
          <div class="col-status">状态</div>
          <div class="col-action">操作</div>
        </div>
        
        <div class="list-item" v-for="order in orderList" :key="order.orderId">
          <div class="col-id">{{ order.orderId }}</div>
          <div class="col-user">{{ order.userName }}</div>
          <div class="col-amount">¥{{ order.orderAmount }}</div>
          <div class="col-time">{{ formatDateTime(order.orderTime) }}</div>
          <div class="col-status">
            <span :class="getOrderStatusClass(order.orderStatus)">
              {{ getOrderStatusText(order.orderStatus) }}
            </span>
          </div>
          <div class="col-action">
            <button class="detail-btn" @click="viewOrderDetails(order)">
              <i class="fa fa-eye"></i> 详情
            </button>
          </div>
        </div>
      </div>

      <!-- 分页控件 -->
      <div class="pagination">
        <button @click="prevOrderPage" :disabled="orderPage === 1">上一页</button>
        <span>第 {{ orderPage }} 页</span>
        <button @click="nextOrderPage" :disabled="orderList.length < orderPageSize">下一页</button>
      </div>

      <!-- 订单详情模态框 -->
      <div class="modal-overlay" v-if="showOrderDetailModal" @click="closeOrderDetailModal">
        <div class="order-detail-modal" @click.stop>
          <h3>订单详情 #{{ currentOrder.orderId }}</h3>
          
          <div class="order-info">
            <div class="info-row">
              <span class="label">用户：</span>
              <span class="value">{{ currentOrder.userName }} ({{ currentOrder.userPhone }})</span>
            </div>
            <div class="info-row">
              <span class="label">下单时间：</span>
              <span class="value">{{ formatDateTime(currentOrder.orderTime) }}</span>
            </div>
            <div class="info-row">
              <span class="label">订单状态：</span>
              <span class="value">{{ getOrderStatusText(currentOrder.orderStatus) }}</span>
            </div>
            <div class="info-row">
              <span class="label">配送地址：</span>
              <span class="value">{{ currentOrder.deliveryAddress }}</span>
            </div>
          </div>
          
          <div class="order-items">
            <h4>订单商品</h4>
            <div class="item" v-for="item in currentOrder.items" :key="item.foodId">
              <span class="name">{{ item.foodName }}</span>
              <span class="quantity">×{{ item.quantity }}</span>
              <span class="price">¥{{ item.foodPrice }}</span>
            </div>
          </div>
          
          <div class="order-summary">
            <div class="summary-row">
              <span class="label">商品总价：</span>
              <span class="value">¥{{ currentOrder.foodAmount }}</span>
            </div>
            <div class="summary-row">
              <span class="label">配送费：</span>
              <span class="value">¥{{ currentOrder.deliveryPrice }}</span>
            </div>
            <div class="summary-row total">
              <span class="label">实付金额：</span>
              <span class="value">¥{{ currentOrder.orderAmount }}</span>
            </div>
          </div>
          
          <div class="modal-buttons">
            <button class="cancel-btn" @click="closeOrderDetailModal">关闭</button>
            <button 
              class="confirm-btn" 
              v-if="currentOrder.orderStatus === 2" 
              @click="confirmOrder(currentOrder.orderId)"
            >
              确认接单
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 食品管理 -->
    <div v-if="currentTab === 'food'" class="content">
      <div class="section-title">
        <h3>食品管理</h3>
        <button class="add-btn" @click="showAddFoodModal = true">
          <i class="fa fa-plus"></i> 新增食品
        </button>
      </div>

      <div class="food-list">
        <div class="list-header">
          <div class="col-id">ID</div>
          <div class="col-name">食品名称</div>
          <div class="col-explain">食品简介</div>
          <div class="col-price">价格</div>
          <div class="col-action">操作</div>
        </div>
        
        <div class="list-item" v-for="food in foodList" :key="food.foodId">
          <div class="col-id">{{ food.foodId }}</div>
          <div class="col-name">{{ food.foodName }}</div>
          <div class="col-explain">{{ food.foodExplain }}</div>
          <div class="col-price">¥{{ food.foodPrice }}</div>
          <div class="col-action">
            <button class="edit-btn" @click="editFood(food)">
              <i class="fa fa-edit"></i>
            </button>
            <button class="delete-btn" @click="deleteFood(food.foodId)">
              <i class="fa fa-trash"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- 新增/编辑食品模态框 -->
      <div class="modal-overlay" v-if="showAddFoodModal || showEditFoodModal" @click="closeFoodModal">
        <div class="business-modal" @click.stop>
          <h3>{{ showAddFoodModal ? '新增食品' : '编辑食品' }}</h3>
          
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
            <input type="number" v-model="currentFood.foodPrice" placeholder="请输入食品价格" />
          </div>
          
          <div class="modal-buttons">
            <button class="cancel-btn" @click="closeFoodModal">取消</button>
            <button class="confirm-btn" @click="saveFood">
              {{ showAddFoodModal ? '新增' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据管理 -->
    <div v-if="currentTab === 'data'" class="content">
      <div class="section-title">
        <h3>数据统计</h3>
        <div class="date-range">
          <input type="date" v-model="statsStartDate" @change="loadStatistics">
          <span>至</span>
          <input type="date" v-model="statsEndDate" @change="loadStatistics">
        </div>
      </div>

      <div class="stats-cards">
        <div class="card">
          <div class="card-title">总订单数</div>
          <div class="card-value">{{ statistics.totalOrders }}</div>
          <div class="card-change" :class="getChangeClass(statistics.orderChange)">
            <i class="fa" :class="getChangeIcon(statistics.orderChange)"></i>
            {{ Math.abs(statistics.orderChange) }}%
          </div>
        </div>
        <div class="card">
          <div class="card-title">总收入</div>
          <div class="card-value">¥{{ statistics.totalRevenue }}</div>
          <div class="card-change" :class="getChangeClass(statistics.revenueChange)">
            <i class="fa" :class="getChangeIcon(statistics.revenueChange)"></i>
            {{ Math.abs(statistics.revenueChange) }}%
          </div>
        </div>
        <div class="card">
          <div class="card-title">热销食品</div>
          <div class="card-value">{{ statistics.topFoodCount }}</div>
          <div class="card-change" :class="getChangeClass(statistics.foodChange)">
            <i class="fa" :class="getChangeIcon(statistics.foodChange)"></i>
            {{ Math.abs(statistics.foodChange) }}%
          </div>
        </div>
      </div>

      <div class="top-lists">
        <div class="top-list">
          <h4>热销食品TOP5</h4>
          <div class="list-item" v-for="(food, index) in topFoods" :key="food.foodId">
            <span class="rank">{{ index + 1 }}</span>
            <span class="name">{{ food.foodName }}</span>
            <span class="count">{{ food.sales }}份</span>
          </div>
        </div>
        <div class="top-list">
          <h4>订单趋势</h4>
          <div class="trend-item" v-for="(trend, index) in orderTrends" :key="index">
            <span class="date">{{ trend.date }}</span>
            <span class="count">{{ trend.count }}单</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      adminName: '商家管理员',
      currentTab: 'order',
      
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
      foodList: [
        {
          foodId: 1,
          foodName: '测试食品1',
          foodExplain: '测试食品简介1',
          foodPrice: 15
        },
        {
          foodId: 2,
          foodName: '测试食品2',
          foodExplain: '测试食品简介2',
          foodPrice: 20
        },
        {
          foodId: 3,
          foodName: '测试食品3',
          foodExplain: '测试食品简介3',
          foodPrice: 25
        }
      ],
      showAddFoodModal: false,
      showEditFoodModal: false,
      currentFood: {
        foodId: null,
        foodName: '',
        foodExplain: '',
        foodPrice: ''
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
  },
  mounted() {
    this.checkAdminAuth();
  },
  methods: {
    checkAdminAuth() {
      this.adminName = '商家管理员';
    },
    
    switchTab(tab) {
      this.currentTab = tab;
    },
    
    logout() {
      if (confirm('确定要退出登录吗？')) {
        this.$router.push('/login');
      }
    },
    
    // 订单管理方法
    loadOrders() {
      console.log('加载订单列表');
    },
    
    viewOrderDetails(order) {
      this.currentOrder = { ...order };
      this.showOrderDetailModal = true;
    },
    
    confirmOrder(orderId) {
      if (confirm('确定要接单吗？')) {
        const order = this.orderList.find(o => o.orderId === orderId);
        if (order) {
          order.orderStatus = 3;
        }
        alert('接单成功！');
        this.closeOrderDetailModal();
      }
    },
    
    closeOrderDetailModal() {
      this.showOrderDetailModal = false;
    },
    
    prevOrderPage() {
      if (this.orderPage > 1) {
        this.orderPage--;
        this.loadOrders();
      }
    },
    
    nextOrderPage() {
      this.orderPage++;
      this.loadOrders();
    },
    
    formatDateTime(datetime) {
      if (!datetime) return '';
      return new Date(datetime).toLocaleString();
    },
    
    getOrderStatusText(status) {
      const statusMap = {
        1: '待支付',
        2: '已支付',
        3: '已接单',
        4: '配送中',
        5: '已完成',
        6: '已取消'
      };
      return statusMap[status] || '未知状态';
    },
    
    getOrderStatusClass(status) {
      const classMap = {
        1: 'status-pending',
        2: 'status-paid',
        3: 'status-accepted',
        4: 'status-delivering',
        5: 'status-completed',
        6: 'status-canceled'
      };
      return classMap[status] || 'status-unknown';
    },
    
    // 食品管理方法
    editFood(food) {
      this.currentFood = { ...food };
      this.showEditFoodModal = true;
    },
    
    deleteFood(foodId) {
      if (confirm('确定要删除这个食品吗？')) {
        this.foodList = this.foodList.filter(f => f.foodId !== foodId);
        alert('删除成功！');
      }
    },
    
    saveFood() {
      if (!this.currentFood.foodName.trim()) {
        alert('请输入食品名称');
        return;
      }

      const isEdit = this.showEditFoodModal;
      
      if (isEdit) {
        const index = this.foodList.findIndex(f => f.foodId === this.currentFood.foodId);
        if (index !== -1) {
          this.foodList[index] = { ...this.currentFood };
        }
      } else {
        this.currentFood.foodId = Math.max(...this.foodList.map(f => f.foodId)) + 1;
        this.foodList.push({ ...this.currentFood });
      }
      
      alert(isEdit ? '编辑成功！' : '新增成功！');
      this.closeFoodModal();
    },
    
    closeFoodModal() {
      this.showAddFoodModal = false;
      this.showEditFoodModal = false;
      this.currentFood = {
        foodId: null,
        foodName: '',
        foodExplain: '',
        foodPrice: ''
      };
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