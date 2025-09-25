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

    <!-- 订单管理 tab -->
    <div v-if="currentTab === 'order' && business" class="content">
      <div class="section-title">
        <h3>订单管理 (店铺: {{ business.businessName }})</h3>
        <div class="filter-section">
          <select v-model="orderFilter.status" @change="loadOrders" class="filter-select">
            <option value="">全部状态</option>
            <option value="0">待支付</option>
            <option value="1">已支付</option>
            <option value="2">已接单</option>
            <option value="3">配送中</option>
            <option value="4">已完成</option>
            <option value="5">已取消</option>
          </select>
          <input type="date" v-model="orderFilter.startDate" @change="loadOrders" placeholder="开始日期" />
          <span>至</span>
          <input type="date" v-model="orderFilter.endDate" @change="loadOrders" placeholder="结束日期" />
          <button @click="loadOrders" class="filter-btn">筛选</button>
        </div>
      </div>

      <div v-if="orderLoading" class="loading-message">加载订单...</div>
      <div v-else-if="orderList.length === 0" class="no-data">暂无订单</div>
      <div v-else class="order-list">  <!-- 复用 food-list 样式 -->
        <div class="list-header">
          <div class="col-id">订单ID</div>
          <div class="col-user">用户</div>
          <div class="col-price">总价</div>
          <div class="col-explain">地址</div>
          <div class="col-status">状态</div>
          <div class="col-action">操作</div>
        </div>
        <div v-for="order in orderList" :key="order.id" class="list-item">
          <div class="col-id">{{ order.id }}</div>
          <div class="col-user">{{ order.customer.username }}</div>
          <div class="col-price">¥{{ order.orderTotal }}</div>
          <div class="col-explain">{{ order.deliveryAddress.address }}</div>
          <div class="col-status">
            <span :class="getStatusClass(order.orderState)">{{ getStatusText(order.orderState) }}</span>
          </div>
          <div class="col-action">
            <button class="detail-btn" @click="showOrderDetail(order)">详情</button>
            <button v-if="order.orderState === 1" class="accept-btn" @click="changeStatus(order.id, 2)">接单</button>
            <button v-if="order.orderState === 2" class="deliver-btn" @click="changeStatus(order.id, 3)">配送</button>
            <button v-if="order.orderState === 3" class="complete-btn" @click="changeStatus(order.id, 4)">完成</button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="orderList.length > 0" class="pagination">
        <button @click="changePage(-1)" :disabled="orderPage === 1">上一页</button>
        <span>第 {{ orderPage + 1 }} 页 (共 {{ totalOrders }} 条)</span>
        <button @click="changePage(1)">下一页</button>
      </div>

      <!-- 订单详情模态 -->
      <div class="modal-overlay" v-if="showOrderDetailModal" @click="closeOrderDetail">
        <div class="order-detail-modal" @click.stop>
          <h3>订单详情 #{{ currentOrder.id }}</h3>
          <div class="order-info">
            <div class="info-row">
              <span class="label">用户：</span>
              <span class="value">{{ currentOrder.customer.username }}</span>
            </div>
            <div class="info-row">
              <span class="label">总价：</span>
              <span class="value">¥{{ currentOrder.orderTotal }}</span>
            </div>
            <div class="info-row">
              <span class="label">地址：</span>
              <span class="value">{{ currentOrder.deliveryAddress.address }}</span>
            </div>
            <div class="info-row">
              <span class="label">下单时间：</span>
              <span class="value">{{ currentOrder.createTime }}</span>
            </div>
            <div class="info-row">
              <span class="label">状态：</span>
              <span :class="getStatusClass(currentOrder.orderState)">{{ getStatusText(currentOrder.orderState) }}</span>
            </div>
          </div>
          <div class="modal-buttons">
            <button class="confirm-btn" @click="closeOrderDetail">关闭</button>
          </div>
        </div>
      </div>
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
      <div class="section-title">
        <h3>数据管理 (店铺: {{ business.businessName }})</h3>
        <div class="filter-section">
          <input type="date" v-model="statsStartDate" @change="loadStats" class="filter-input" />
          <span>至</span>
          <input type="date" v-model="statsEndDate" @change="loadStats" class="filter-input" />
          <button @click="loadStats" class="filter-btn">刷新统计</button>
        </div>
      </div>

      <div v-if="statsLoading" class="loading-message">加载统计数据...</div>
      <div v-else-if="statsError" class="error-message">{{ statsError }}</div>
      <div v-else class="stats-container">
        <!-- 统计卡片 (4 张) -->
        <div class="stats-cards">
          <div class="card">
            <div class="card-title">总订单数</div>
            <div class="card-value">{{ statistics.totalOrders }}</div>
            <div class="card-change" :class="getChangeClass(statistics.orderChange)">
              <i :class="getChangeIcon(statistics.orderChange)"></i>
              {{ Math.abs(statistics.orderChange) }}% {{ statistics.orderChange >= 0 ? '增长' : '下降' }} (vs 上月)
            </div>
          </div>
          <div class="card">
            <div class="card-title">总营收</div>
            <div class="card-value">¥{{ statistics.totalRevenue.toFixed(2) }}</div>
            <div class="card-change" :class="getChangeClass(statistics.revenueChange)">
              <i :class="getChangeIcon(statistics.revenueChange)"></i>
              {{ Math.abs(statistics.revenueChange) }}% {{ statistics.revenueChange >= 0 ? '增长' : '下降' }} (vs 上月)
            </div>
          </div>
          <div class="card">
            <div class="card-title">完成订单</div>
            <div class="card-value">{{ statistics.completedOrders }}</div>
            <div class="card-change positive">
              <i class="fa fa-arrow-up"></i> {{ statistics.completionRate }}
            </div>
          </div>
          <div class="card">
            <div class="card-title">平均客单价</div>
            <div class="card-value">¥{{ statistics.avgOrderValue.toFixed(2) }}</div>
            <div class="card-change positive">
              <i class="fa fa-arrow-up"></i> 稳定
            </div>
          </div>
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
      adminName: '商家管理员',
      currentTab: 'food',
      business: null,
      isLoading: true,  // 加载商家
      foodLoading: false,  // 加载食品
      errorMsg: '',  // 错误提示

     // 订单相关 (新)
      orderList: [],  // 真实订单
      orderLoading: false,
      totalOrders: 0,  // 总计数
      orderPage: 0,  // 当前页 (0-based)
      orderPageSize: 10,
      orderFilter: {
        status: '',  // 0-5
        startDate: '',
        endDate: ''
      },
    showOrderDetailModal: false,
      currentOrder: {},  // 详情
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
      
      // 数据管理相关 (修改: 移除静态假数据, 初始化 empty/true 0/null – force API load)
      statistics: {  // empty, from API (no 125/3568 fake)
        totalOrders: 0,
        totalRevenue: 0,
        completedOrders: 0,
        avgOrderValue: 0,
        completionRate: '0%',
        orderChange: 0,
        revenueChange: 0,
        topFoodCount: 0,  // if need
        foodChange: 0
      },
      statsStartDate: '',  // default empty = API all/this month
      statsEndDate: '',
      topFoods: [],  // empty, from /topFoods API (no 测试食品1-5 fake)
      orderTrends: [],  // empty now; static as fallback only if API fail (future /trends)
      statsLoading: false,  // 新增: loading state
      statsError: ''  // 新增: error state (show instead of fake fallback)
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
    
// 新：加载订单
    async loadOrders() {
      if (!this.business) return;
      this.orderLoading = true;
      try {
        const params = new URLSearchParams({
          status: this.orderFilter.status || '',
          startDate: this.orderFilter.startDate || '',
          endDate: this.orderFilter.endDate || '',
          page: this.orderPage,
          size: this.orderPageSize
        });
        const response = await axios.get(`/api/orders/business/${this.business.id}?${params}`);
        if (response.data.success && response.data.code === 'OK') {
          this.orderList = response.data.data || [];
          this.totalOrders = response.data.total || 0;  // 若 Controller 返回 count
          console.log('订单加载成功:', this.orderList.length);
        } else {
          throw new Error(response.data.message);
        }
      } catch (error) {
        console.error('加载订单失败:', error);
        alert('加载订单失败: ' + error.message);
        this.orderList = [];
      } finally {
        this.orderLoading = false;
      }
    },

    // 新：变更状态
    async changeStatus(orderId, newState) {
      const states = {2: '接单', 3: '开始配送', 4: '完成订单'};
      if (!confirm(`确认${states[newState]}?`)) return;
      try {
        const response = await axios.put(`/api/orders/${orderId}/status?newState=${newState}`);
        if (response.data.success && response.data.code === 'OK') {
          alert('状态更新成功');
          await this.loadOrders();  // 刷新
        } else {
          throw new Error(response.data.message);
        }
      } catch (error) {
        console.error('更新状态失败:', error);
        alert('更新失败: ' + error.message);
      }
    },

    // 新：显示详情
    showOrderDetail(order) {
      this.currentOrder = { ...order };  // 复制
      this.showOrderDetailModal = true;
    },

    // 新：关闭详情
    closeOrderDetail() {
      this.showOrderDetailModal = false;
      this.currentOrder = {};
    },

    // 新：状态文本/类
    getStatusText(state) {
      const map = {0: '待支付', 1: '已支付', 2: '已接单', 3: '配送中', 4: '已完成', 5: '已取消'};
      return map[state] || '未知';
    },

    getStatusClass(state) {
      const classes = {
        0: 'status-pending',
        1: 'status-paid',
        2: 'status-accepted',
        3: 'status-delivering',
        4: 'status-completed',
        5: 'status-canceled'
      };
      return classes[state] || 'status-unknown';
    },

    // 新：分页
    changePage(delta) {
      const newPage = this.orderPage + delta;
      if (newPage >= 0 && (newPage * this.orderPageSize < this.totalOrders)) {
        this.orderPage = newPage;
        this.loadOrders();
      }
    },


    async loadStats() {
      if (!this.business) {
        this.statsError = '商家信息未加载';
        return;
      }
      this.statsLoading = true;
      this.statsError = '';
      const businessId = this.business.id;
      try {
        // 日期 param (empty = default this month)
        const params = new URLSearchParams({
          startDate: this.statsStartDate || '',
          endDate: this.statsEndDate || ''
        });
        const url = `/api/businesses/${businessId}/stats?${params}`;
        console.log('调用统计 API:', url);

        // 统计
        const statsRes = await axios.get(url);
        if (statsRes.data.success && statsRes.data.code === 'OK') {
          this.statistics = { ...statsRes.data.data };
        } else {
          throw new Error(statsRes.data.message || '响应异常');
        }

        // Top Foods
        const topUrl = `/api/businesses/${businessId}/topFoods?topN=5`;
        console.log('调用 Top Foods API:', topUrl);
        const topRes = await axios.get(topUrl);
        if (topRes.data.success && topRes.data.code === 'OK') {
          this.topFoods = topRes.data.data || [];
          // if no sales in Food, assume frontend handle NaN
        } else {
          throw new Error(topRes.data.message || 'Top Foods 异常');
        }

        // Trends: static for now; future add /trends API (calc from orders)
        // this.orderTrends = statsRes.data.data.trends || this.orderTrends; 

        console.log('统计加载成功:', this.statistics, this.topFoods);
      } catch (error) {
        console.error('加载统计失败:', error);
        this.statsError = '加载失败: ' + (error.response?.data?.message || error.message) + ' (使用默认数据)';
        // fallback to your static in data()
        alert(this.statsError);
      } finally {
        this.statsLoading = false;
      }
    },

    // 你的 getChangeClass (keep)
    getChangeClass(change) {
      return change >= 0 ? 'positive' : 'negative';
    },

    // 你的 getChangeIcon (keep, add fa class)
    getChangeIcon(change) {
      return change >= 0 ? 'fa fa-arrow-up' : 'fa fa-arrow-down';
    },

    switchTab(tab) {
      if (!this.business && tab !== 'data') {
        alert('请先加载店铺');
        return;
      }
      this.currentTab = tab;
      if (tab === 'order' && this.orderList.length === 0) {
        this.loadOrders();
      }
      if (tab === 'food' && this.foodList.length === 0 && this.business) {
        this.loadFoods();
      }
      if (tab === 'data' && (this.statistics.totalOrders === 125 || this.topFoods.length === 5)) {  // lazy if static
        this.statsStartDate = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0];  // this month start
        this.statsEndDate = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).toISOString().split('T')[0];  // end
        this.loadStats();
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
/* 整体重置：基础字体、颜色、间距 (简洁现代) */
.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f8f9fa;  /* 浅灰背景，更柔和 */
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;  /* 系统字体，美观 */
  color: #333;
  line-height: 1.5;
}

/* Header：紧凑、专业 */
header {
  width: 100%;
  height: 64px;  /* 稍高，平衡 */
  background: linear-gradient(135deg, #0097FF 0%, #007ACC 100%);  /* 轻微渐变，不花哨 */
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 151, 255, 0.2);  /* 轻蓝影 */
  box-sizing: border-box;
}

.header-left p {
  font-size: 20px;  /* 稍大 */
  font-weight: 600;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right span {
  font-size: 16px;
  font-weight: 500;
}

.header-right button {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.header-right button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);  /* 轻微提升 */
}

/* Menu：水平导航，active 突出 */
.menu-section {
  background-color: #fff;
  padding: 15px;  /* 保持原 padding (左右总 30px) */
  display: flex;
  gap: 0;  /* 新：gap 0 (去除 15px 间距，避免总宽超屏) */
  border-bottom: 1px solid #eee;
  width: 100%;  /* 容器满宽 */
  justify-content: space-between;  /* 新：space-between (两端对齐，中间自然空，总宽精确=屏宽 - padding，无超) */
  flex-wrap: nowrap;  /* 强制横排 */
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;  /* 项内 gap 保持 (图标/文本间距) */
  padding: 8px 15px;  /* 左右 padding 保持 (每个项内边) */
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #666;
  flex: 1;  /* 等宽分屏 (3 项总 = 屏宽 - padding) */
  min-width: 0;  /* 允许收缩 */
  justify-content: center;  /* 居中 */
  text-align: center;  /* 文本居中 */
  overflow: hidden;  /* 新：隐藏长文本溢出 (e.g., "数据管理" 在极窄不超) */
  text-overflow: ellipsis;  /* 新：长文本省略 (...)，确保不超 */
  white-space: nowrap;  /* 新：不换行，保持单行 */
}

.menu-item span {  /* 新：针对 <span> 文本加，保持短文本正常 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.menu-item:hover,
.menu-item.active {
  background: #0097FF;
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 151, 255, 0.2);
}

.menu-item i {  /* 如果有图标 */
  font-size: 16px;
}

/* Content：统一卡片布局 */
.content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;  /* 居中 */
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e9ecef;
}

.section-title h3 {
  font-size: 22px;
  color: #2c3e50;  /* 深灰，专业 */
  font-weight: 600;
  margin: 0;
}

.no-data {
  text-align: center;
  padding: 48px 24px;
  color: #6c757d;
  font-size: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* 过滤器：inline，紧凑 */
.filter-section {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;  /* 响应式换行 */
}

.filter-section select,
.filter-section input {
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  background: #fff;
  transition: border-color 0.2s ease;
  min-width: 120px;  /* 日期 input 宽 */
}

.filter-section input:focus,
.filter-section select:focus {
  outline: none;
  border-color: #0097FF;
  box-shadow: 0 0 0 2px rgba(0, 151, 255, 0.1);
}

.filter-section span {
  color: #6c757d;
  font-size: 14px;
  white-space: nowrap;
}

.filter-btn {
  background: #0097FF;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.filter-btn:hover {
  background: #007ACC;
}

/* 添加按钮：统一圆角 */
.add-btn {
  background: #28a745;
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
  box-shadow: 0 1px 3px rgba(40, 167, 69, 0.3);
}

.add-btn:hover {
  background: #218838;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(40, 167, 69, 0.4);
}

.add-btn i {
  font-size: 14px;
}

/* 列表：统一卡片，hover 美化 */
.order-list,
.food-list {
  background: #fff;
  border-radius: 10px;  /* 稍圆 */
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);  /* 柔影 */
  margin-bottom: 24px;
  overflow-x: hidden;  /* 新：隐藏多余滚动，自适应优先 */
}

.loading-message,
.error-message {
  text-align: center;
  padding: 48px 24px;
  color: #6c757d;
  font-size: 16px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.list-header,
.list-item {
  display: grid;
  align-items: center;
  padding: 16px 20px;  /* 增加间距 */
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.list-header {
  background: #f8f9fa;
  font-weight: 600;
  color: #495057;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 订单列表：6 列均衡 */
.order-list .list-header,
.order-list .list-item {
  grid-template-columns: 0.8fr 1fr 0.8fr 2fr 1fr 1fr;  /* 新：全 fr 自适应 - ID(窄)/用户(中)/总价(窄)/地址(宽2fr，长文本)/状态(中)/操作(中)；总宽100%，无滚 */
}

.col-id {
  font-weight: 600;
  color: #495057;
}

.col-user {
  text-align: left;
}

.col-price {
  font-weight: 500;
  color: #28a745;  /* 绿价 */
}

.col-explain {
  color: #6c757d;  /* 灰地址 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.col-status {
  text-align: center;
}

.col-action {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.list-item:hover {
  background: #f0f8ff;  /* 淡蓝 hover，不花哨 */
  transition: background 0.2s ease;
}

/* 食品列表：5 列 */
.food-list .list-header,
.food-list .list-item {
  grid-template-columns: 0.8fr 1.5fr 2fr 0.8fr 1fr;  /* 新：全 fr 自适应 - ID(窄)/名称(中)/简介(宽2fr，长)/价格(窄)/操作(中)；总宽100%，无滚 */
}

/* 状态 Badge：pill 形，彩色背景 */
.col-status span {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 20px;  /* 圆 pill */
  font-size: 12px;
  font-weight: 600;
  min-width: 70px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 状态颜色：统一背景+白字 */
.status-pending { background: #fff3cd; color: #856404; }
.status-paid { background: #d1ecf1; color: #0c5460; }
.status-accepted { background: #d4edda; color: #155724; }
.status-delivering { background: #e2e3e5; color: #495057; }
.status-completed { background: #d4edda; color: #155724; }
.status-canceled { background: #f8d7da; color: #721c24; }
.status-unknown { background: #f8f9fa; color: #6c757d; }

/* 操作按钮：小圆，hover 缩放 */
.detail-btn,
.accept-btn,
.deliver-btn,
.complete-btn,
.edit-btn,
.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.detail-btn:hover,
.accept-btn:hover,
.deliver-btn:hover,
.complete-btn:hover,
.edit-btn:hover,
.delete-btn:hover {
  transform: scale(1.05);  /* 轻缩放 */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.detail-btn { background: #17a2b8; color: #fff; }
.accept-btn { background: #28a745; color: #fff; }
.deliver-btn { background: #007bff; color: #fff; }
.complete-btn { background: #ffc107; color: #212529; }  /* 黄字黑 */
.edit-btn { background: #ffc107; color: #212529; }
.delete-btn { background: #dc3545; color: #fff; }

/* 分页：居中，按钮圆 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin: 24px 0;
  padding: 16px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.pagination button {
  background: #0097FF;
  color: #fff;
  border: none;
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
}

.pagination button:hover:not(:disabled) {
  background: #007ACC;
  transform: translateY(-1px);
}

.pagination button:disabled {
  background: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
}

.pagination span {
  font-size: 14px;
  color: #495057;
  font-weight: 500;
}

/* 模态：阴影、滚动 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);  /* 半透 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(2px);  /* 轻模糊，不花哨 */
}

.order-detail-modal,
.business-modal {
  width: 90%;
  max-width: 500px;  /* 订单详情窄些 */
  max-height: 80vh;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);  /* 深影 */
  animation: fadeIn 0.2s ease;  /* 轻入场 */
}

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}

.order-detail-modal h3,
.business-modal h3 {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;  /* 增加间距 */
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #495057;
  margin-bottom: 6px;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {  /* 如果有 textarea */
  width: 100%;
  padding: 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.2s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #0097FF;
  box-shadow: 0 0 0 3px rgba(0, 151, 255, 0.1);
}

.modal-buttons {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  justify-content: flex-end;
}

.cancel-btn,
.confirm-btn {
  flex: 1;
  max-width: 120px;  /* 固定宽 */
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn {
  background: #6c757d;
  color: #fff;
}

.cancel-btn:hover {
  background: #5a6268;
}

.confirm-btn {
  background: #0097FF;
  color: #fff;
}

.confirm-btn:hover:not(:disabled) {
  background: #007ACC;
  transform: translateY(-1px);
}

.confirm-btn:disabled {
  background: #adb5bd;
  cursor: not-allowed;
}

/* 订单详情：卡片行 */
.order-info .info-row {
  display: flex;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #0097FF;  /* 左线突出 */
}

.order-info .label {
  width: 80px;
  font-weight: 600;
  color: #495057;
  margin-right: 12px;
}

.order-info .value {
  flex: 1;
  color: #333;
  word-break: break-all;  /* 长地址换行 */
}

/* 数据统计：卡片网格 (如果你用) */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid #e9ecef;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.card-title {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 8px;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.card-change {
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-change.positive { color: #28a745; }
.card-change.negative { color: #dc3545; }


</style>