<template>
    <div class="wrapper">
        <!-- Header -->
        <header>
            <div class="header-left">
                <p>管理后台</p>
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
                :class="{ active: currentTab === 'business' }"
                @click="switchTab('business')"
            >
                <span>商家管理</span>
            </div>
            <div 
                class="menu-item" 
                :class="{ active: currentTab === 'user' }"
                @click="switchTab('user')"
            >
                <span>用户管理</span>
            </div>
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

        <!-- 商家管理 -->
        <div v-if="currentTab === 'business'" class="content">
            <div class="section-title">
                <h3>商家管理</h3>
                <button class="add-btn" @click="showAddBusinessModal = true">
                    <i class="fa fa-plus"></i> 新增商家
                </button>
            </div>

            <!-- 加载状态 -->
            <div v-if="loading" class="loading-state">
                <i class="fa fa-spinner fa-spin"></i> 加载中...
            </div>

            <!-- 错误提示 -->
            <div v-if="error" class="error-message">
                {{ error }}
                <button @click="fetchBusinesses">重试</button>
            </div>

            <div class="business-list" v-if="!loading && !error">
                <div class="list-header">
                    <div class="col-id">ID</div>
                    <div class="col-name">商家名称</div>
                    <div class="col-explain">商家简介</div>
                    <div class="col-action">操作</div>
                </div>
                
                <div 
                    class="list-item" 
                    v-for="business in businessList" 
                    :key="business.businessId"
                >
                    <div class="col-id">{{ business.businessId }}</div>
                    <div class="col-name">{{ business.businessName }}</div>
                    <div class="col-explain">{{ business.businessExplain }}</div>
                    <div class="col-action">
                        <button class="edit-btn" @click="editBusiness(business)">
                            <i class="fa fa-edit"></i>
                        </button>
                        <button class="delete-btn" @click="deleteBusiness(business.businessId)">
                            <i class="fa fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 食品管理 -->
        <div v-if="currentTab === 'food'" class="content">
            <div class="section-title">
                <h3>食品管理</h3>
                <button class="add-btn" @click="showAddBusinessModal = true">
                    <i class="fa fa-plus"></i> 新增食品
                </button>
            </div>

            <!-- 加载状态 -->
            <div v-if="loading" class="loading-state">
                <i class="fa fa-spinner fa-spin"></i> 加载中...
            </div>

            <!-- 错误提示 -->
            <div v-if="error" class="error-message">
                {{ error }}
                <button @click="fetchFoods">重试</button>
            </div>

            <div class="food-list" v-if="!loading && !error">
                <div class="list-header">
                    <div class="col-id">ID</div>
                    <div class="col-name">食品名称</div>
                    <div class="col-explain">食品价格</div>
                    <div class="col-action">操作</div>
                </div>
                
                <div 
                    class="list-item" 
                    v-for="food in this.foodList" 
                    :key="food.id"
                >
                    <div class="col-id">{{ food.id }}</div>
                    <div class="col-name">{{ food.foodName }}</div>
                    <div class="col-explain">{{ food.foodPrice }}</div>
                    <div class="col-action">
                        <button class="edit-btn" @click="editFood(food)">
                            <i class="fa fa-edit"></i>
                        </button>
                        <button class="delete-btn" @click="deleteFood(food.id)">
                            <i class="fa fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="currentTab === 'user'" class="content">
            <div class="section-title">
                <h3>用户管理</h3>
                <div class="search-box">
                    <input type="text" v-model="userSearchKeyword" placeholder="搜索用户名/手机号">
                    <button @click="searchUsers">搜索</button>
                </div>
            </div>

            <div class="user-list">
                <div class="list-header">
                    <div class="col-id">用户ID</div>
                    <div class="col-name">用户名</div>
                    <div class="col-phone">手机号</div>
                    <div class="col-register">注册时间</div>
                    <div class="col-status">状态</div>
                    <div class="col-action">操作</div>
                </div>
                
                <div class="list-item" v-for="user in userList" :key="user.userId">
                    <div class="col-id">{{ user.userId }}</div>
                    <div class="col-name">{{ user.userName }}</div>
                    <div class="col-phone">{{ user.userPhone }}</div>
                    <div class="col-register">{{ formatDate(user.registerTime) }}</div>
                    <div class="col-status">
                        <span :class="{'active': user.status === 1, 'inactive': user.status === 0}">
                            {{ user.status === 1 ? '正常' : '禁用' }}
                        </span>
                    </div>
                    <div class="col-action">
                        <button class="edit-btn" @click="editUser(user)">
                            <i class="fa fa-edit"></i>
                        </button>
                        <button 
                            class="toggle-btn" 
                            :class="{'disable-btn': user.status === 1, 'enable-btn': user.status === 0}"
                            @click="toggleUserStatus(user)"
                        >
                            {{ user.status === 1 ? '禁用' : '启用' }}
                        </button>
                    </div>
                </div>
            </div>

            <!-- 分页控件 -->
            <div class="pagination">
                <button @click="prevUserPage" :disabled="userPage === 1">上一页</button>
                <span>第 {{ userPage }} 页</span>
                <button @click="nextUserPage" :disabled="userList.length < userPageSize">下一页</button>
            </div>

            <!-- 用户编辑模态框 -->
            <div class="modal-overlay" v-if="showUserModal" @click="closeUserModal">
                <div class="business-modal" @click.stop>
                    <h3>编辑用户</h3>
                    
                    <div class="form-group">
                        <label>用户名：</label>
                        <input type="text" v-model="currentUser.userName" disabled />
                    </div>
                    
                    <div class="form-group">
                        <label>手机号：</label>
                        <input type="text" v-model="currentUser.userPhone" />
                    </div>
                    
                    <div class="form-group">
                        <label>用户状态：</label>
                        <select v-model="currentUser.status">
                            <option value="1">正常</option>
                            <option value="0">禁用</option>
                        </select>
                    </div>
                    
                    <div class="modal-buttons">
                        <button class="cancel-btn" @click="closeUserModal">取消</button>
                        <button class="confirm-btn" @click="saveUser">保存</button>
                    </div>
                </div>
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
                    <div class="col-business">商家</div>
                    <div class="col-amount">金额</div>
                    <div class="col-time">下单时间</div>
                    <div class="col-status">状态</div>
                    <div class="col-action">操作</div>
                </div>
                
                <div class="list-item" v-for="order in orderList" :key="order.orderId">
                    <div class="col-id">{{ order.orderId }}</div>
                    <div class="col-user">{{ order.userName }}</div>
                    <div class="col-business">{{ order.businessName }}</div>
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
                            <span class="label">商家：</span>
                            <span class="value">{{ currentOrder.businessName }}</span>
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
                        <div class="item" v-for="item in currentOrder.items" :key="item.id">
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
                </div>
                <div class="card">
                    <div class="card-title">总收入</div>
                    <div class="card-value">¥{{ statistics.totalRevenue }}</div>
                </div>
                <div class="card">
                    <div class="card-title">总用户数</div>
                    <div class="card-value">{{ statistics.newUsers }}</div>
                </div>
                <div class="card">
                    <div class="card-title">总商家数</div>
                    <div class="card-value">{{ statistics.activeBusinesses }}</div>
                </div>
            </div>

            <div class="top-lists">
                <div class="top-list">
                    <h4>热销商品TOP</h4>
                    <div class="list-item" v-for="(food, index) in topFoods" :key="food.id">
                        <span class="rank">{{ index + 1 }}</span>
                        <span class="name">{{ food.foodName }}</span>
                        <span class="count">{{ food.sales }}份</span>
                    </div>
                </div>
                <div class="top-list">
                    <h4>热门商家TOP</h4>
                    <div class="list-item" v-for="(business, index) in topBusinesses" :key="business.businessId">
                        <span class="rank">{{ index + 1 }}</span>
                        <span class="name">{{ business.businessName }}</span>
                        <span class="count">{{ business.orders }}单</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 新增/编辑商家模态框 -->
        <div class="modal-overlay" v-if="showAddBusinessModal || showEditBusinessModal" @click="closeBusinessModal">
            <div class="business-modal" @click.stop>
                <h3>{{ showAddBusinessModal ? '新增商家' : '编辑商家' }}</h3>
                
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
                    <input type="number" v-model="currentBusiness.deliveryPrice" placeholder="请输入配送费用" />
                </div>
                
                <div class="modal-buttons">
                    <button class="cancel-btn" @click="closeBusinessModal">取消</button>
                    <button class="confirm-btn" @click="saveBusiness">
                        {{ showAddBusinessModal ? '新增' : '保存' }}
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
</template>

<script>
import axios from 'axios';
const apiClient = axios.create({
  baseURL : '/api'
});

export default {
    data() {
        return {
            adminName: '管理员',
            currentTab: 'business',
            selectedBusinessId: '',
            
            // 模态框状态
            showAddBusinessModal: false,
            showEditBusinessModal: false,
            showAddFoodModal: false,
            showEditFoodModal: false,
            showUserModal: false,
            showOrderDetailModal: false,
            
            // 数据列表
            businessList: [
            ],
            foodList: [
            ],
            
            // 用户管理相关
            userList: [
                {
                    userId: 1,
                    userName: '测试用户1',
                    userPhone: '13800138001',
                    status: 1,
                    registerTime: '2023-01-01 10:00:00'
                },
                {
                    userId: 2,
                    userName: '测试用户2',
                    userPhone: '13800138002',
                    status: 0,
                    registerTime: '2023-01-02 11:00:00'
                }
            ],
            userSearchKeyword: '',
            userPage: 1,
            userPageSize: 10,
            currentUser: {
                userId: null,
                userName: '',
                userPhone: '',
                status: 1,
                registerTime: ''
            },
            
            // 订单管理相关
            orderList: [
                {
                    orderId: 'ORD20230001',
                    userName: '测试用户1',
                    userPhone: '13800138001',
                    businessName: '测试商家1',
                    orderTime: '2023-01-01 12:00:00',
                    orderStatus: 5,
                    orderAmount: 35,
                    items: [
                        { id: 1, foodName: '测试食品1', quantity: 2, foodPrice: 15 },
                        { id: 2, foodName: '测试食品2', quantity: 1, foodPrice: 20 }
                    ]
                },
                {
                    orderId: 'ORD20230002',
                    userName: '测试用户2',
                    userPhone: '13800138002',
                    businessName: '测试商家2',
                    orderTime: '2023-01-02 13:00:00',
                    orderStatus: 2,
                    orderAmount: 48,
                    items: [
                        { id: 3, foodName: '测试食品3', quantity: 3, foodPrice: 16 }
                    ]
                }
            ],
            orderFilter: {
                status: '',
                startDate: '',
                endDate: ''
            },
            orderPage: 1,
            orderPageSize: 10,
            currentOrder: {
                orderId: '',
                userName: '',
                userPhone: '',
                businessName: '',
                orderTime: '',
                orderStatus: '',
                deliveryAddress: '测试地址',
                items: [],
                foodAmount: 0,
                deliveryPrice: 0,
                orderAmount: 0
            },
            
            // 数据管理相关
            statistics: {
                totalOrders: 125,
                totalRevenue: 3568,
                newUsers: 12,
                activeBusinesses: 8,
                orderChange: 15,
                revenueChange: 20,
                userChange: 5,
                businessChange: 10
            },
            statsStartDate: '',
            statsEndDate: '',
            topFoods: [
                { id: 1, foodName: '测试食品1', sales: 45 },
                { id: 2, foodName: '测试食品2', sales: 38 },
            ],
            topBusinesses: [
                { businessId: 1, businessName: '测试商家1', orders: 62 },
                { businessId: 2, businessName: '测试商家2', orders: 58 },
            ],
            
            // 当前编辑项
            currentBusiness: {
                businessId: null,
                businessName: '',
                businessAddress: '',
                businessExplain: '',
                deliveryPrice: ''
            },
            currentFood: {
                id: null,
                foodName: '',
                foodExplain: '',
                foodPrice: '',
                businessId: ''
            }
        }
    },
    mounted() {
        this.checkAdminAuth();
        this.fetchBusinesses();
        this.fetchFoods();
    },
    methods: {
        // 检查管理员权限

        async fetchBusinesses() {
            this.loading = true;
            this.error = null;
            
            try {
            const response = await apiClient.get('/businesses');
            // 将API返回的数据格式转换为组件需要的格式
            this.businessList = response.data.data.map(business => ({
                businessId: business.id,
                businessName: business.businessName,
                businessAddress: business.businessAddress,
                businessExplain: business.businessExplain,
                deliveryPrice: business.deliveryPrice,
                // 可以根据需要添加其他字段
                businessOwner: business.businessOwner.username // 如果需要显示所有者
            }));
            } catch (err) {
            this.error = `加载商家数据失败: ${err.message}`;
            console.error('API Error:', err);
            } finally {
            this.loading = false;
            }
        },
        async deleteBusiness(userId) {
            console.log('删除商家:', userId);
            console.log('传入商家id:', userId);
            const confirmText = `确定要删除ID为 ${userId} 的用户吗？`;
            if (!confirm(`确定要删除ID为 ${userId} 的商家吗？`)) return;
            
            this.loading = true;
            this.error = null;
            this.apiResponse = null;
            
            try {
                const response = await apiClient.delete(`/businesses/${userId}`);
                this.apiResponse = response;
                alert('删除成功！');
                this.fetchBusinesses();
            } catch (err) {
                this.handleError(err);
            } finally {
                this.loading = false;
            }
        },
        async fetchFoods() {
            this.loading = true;
            this.error = null;
            
            try {
                const response = await apiClient.get('/foods');
                // 将API返回的数据格式转换为组件需要的格式
                this.foodList = response.data.data.map(food => ({
                    id: food.id,
                    foodName: food.foodName,
                    foodExplain: food.foodExplain,
                    foodPrice: food.foodPrice,
                    businessId: food.business.id,
                    businessName: food.business.businessName
                }));
                console.log("foodList:");
                console.log(this.foodList);
            } catch (err) {
                this.error = `加载食品数据失败: ${err.message}`;
                console.error('API Error:', err);
            } finally {
            this.loading = false;
            }
        },
        checkAdminAuth() {
            // 这里应该是从sessionStorage获取管理员信息
            // 测试阶段直接设置管理员名称
            this.adminName = '管理员';
        },

        // 切换标签页
        switchTab(tab) {
            this.currentTab = tab;
            if (tab === 'food') {
                this.selectedBusinessId = '';
                this.foodList = [];
            }
        },

        // 加载食品列表
        async loadFoodList() {
            if (!this.selectedBusinessId) {
                this.foodList = [];
                return;
            }

            // 测试阶段使用硬编码数据
            console.log('加载食品列表，商家ID:', this.selectedBusinessId);
            // 实际开发中替换为API调用
            // const response = await this.$axios.get(`/food/listFoodByBusinessId?businessId=${this.selectedBusinessId}`);
            // this.foodList = response.data.data || [];
        },

        // 编辑商家
        editBusiness(business) {
            console.log('编辑商家:', business);
            this.currentBusiness = { ...business };
            this.showEditBusinessModal = true;
        },


        // 保存商家
        async saveBusiness() {
            if (!this.currentBusiness.businessName.trim()) {
                alert('请输入商家名称');
                return;
            }

            try {
                const isEdit = this.showEditBusinessModal;
                console.log(isEdit ? '编辑商家:' : '新增商家:', this.currentBusiness);
                
                // 实际开发中替换为API调用

                // 测试阶段直接更新本地数据
                if (isEdit) {
                    const index = this.businessList.findIndex(b => b.businessId === this.currentBusiness.businessId);
                    if (index !== -1) {
                        this.businessList[index] = { ...this.currentBusiness };
                    }
                } else {
                    this.currentBusiness.businessId = Math.max(...this.businessList.map(b => b.businessId)) + 1;
                    this.businessList.push({ ...this.currentBusiness });
                }
                
                alert(isEdit ? '编辑成功！' : '新增成功！');
                this.closeBusinessModal();
                this.loadBusinessList();
            } catch (error) {
                console.error('保存错误:', error);
                alert('操作失败，请稍后重试');
            }
        },

        // 编辑食品
        editFood(food) {
            console.log('编辑食品:', food);
            this.currentFood = { ...food };
            this.showEditFoodModal = true;
        },

        // 删除食品
        async deleteFood(id) {
            if (!confirm('确定要删除这个食品吗？')) {
                return;
            }
            try {
                console.log('删除食品:', id);
            } catch (error) {
                console.error('删除错误:', error);
                alert('删除失败，请稍后重试');
            }
        },

        // 保存食品
        async saveFood() {
            if (!this.currentFood.foodName.trim()) {
                alert('请输入食品名称');
                return;
            }

            try {
                // 设置商家ID
                this.currentFood.businessId = this.selectedBusinessId;
                
                const isEdit = this.showEditFoodModal;
                console.log(isEdit ? '编辑食品:' : '新增食品:', this.currentFood);
                
                // 实际开发中替换为API调用
                // let endpoint = isEdit ? '/food/updateFood' : '/food/addFood';
                // const response = await this.$axios.post(endpoint, this.$qs.stringify(this.currentFood));
                // if (response.data.code === 1) {
                //     alert(isEdit ? '编辑成功！' : '新增成功！');
                //     this.closeFoodModal();
                //     this.loadFoodList();
                // }
                
                // 测试阶段直接更新本地数据
                if (isEdit) {
                    const index = this.foodList.findIndex(f => f.id === this.currentFood.id);
                    if (index !== -1) {
                        this.foodList[index] = { ...this.currentFood };
                    }
                } else {
                    this.currentFood.id = Math.max(...this.foodList.map(f => f.id)) + 1;
                    this.foodList.push({ ...this.currentFood });
                }
                
                alert(isEdit ? '编辑成功！' : '新增成功！');
                this.closeFoodModal();
                this.loadFoodList();
            } catch (error) {
                console.error('保存错误:', error);
                alert('操作失败，请稍后重试');
            }
        },

        // 关闭商家模态框
        closeBusinessModal() {
            this.showAddBusinessModal = false;
            this.showEditBusinessModal = false;
            this.currentBusiness = {
                businessId: null,
                businessName: '',
                businessAddress: '',
                businessExplain: '',
                deliveryPrice: ''
            }
        },

        // 关闭食品模态框
        closeFoodModal() {
            this.showAddFoodModal = false;
            this.showEditFoodModal = false;
            this.currentFood = {
                id: null,
                foodName: '',
                foodExplain: '',
                foodPrice: '',
                businessId: ''
            }
        },

        // 退出登录
        logout() {
            if (confirm('确定要退出登录吗？')) {
                // this.$removeSessionStorage('admin');
                this.$router.push('/login');
            }
        },

        // ========== 用户管理方法 ==========
        async loadUsers() {
            // 测试阶段使用硬编码数据
            console.log('加载用户列表');
            // 实际开发中替换为API调用
            // const response = await this.$axios.get('/admin/users', {
            //     params: {
            //         page: this.userPage,
            //         pageSize: this.userPageSize,
            //         keyword: this.userSearchKeyword
            //     }
            // });
            // this.userList = response.data.data || [];
        },
        
        async searchUsers() {
            this.userPage = 1;
            await this.loadUsers();
        },
        
        async toggleUserStatus(user) {
            const newStatus = user.status === 1 ? 0 : 1;
            if (confirm(`确定要${newStatus === 1 ? '启用' : '禁用'}该用户吗？`)) {
                try {
                    // 实际开发中替换为API调用
                    // const response = await this.$axios.put(`/admin/users/${user.userId}/status`, {
                    //     status: newStatus
                    // });
                    // if (response.data.code === 1) {
                    //     user.status = newStatus;
                    //     alert('操作成功！');
                    // }
                    
                    // 测试阶段直接更新本地数据
                    user.status = newStatus;
                    alert('操作成功！');
                } catch (error) {
                    console.error('切换用户状态错误:', error);
                    alert('操作失败，请稍后重试');
                }
            }
        },
        
        editUser(user) {
            this.currentUser = { ...user };
            this.showUserModal = true;
        },
        
        async saveUser() {
            try {
                // 实际开发中替换为API调用
                // const response = await this.$axios.put(`/admin/users/${this.currentUser.userId}`, this.currentUser);
                // if (response.data.code === 1) {
                //     alert('保存成功！');
                //     this.closeUserModal();
                //     this.loadUsers();
                // }
                
                // 测试阶段直接更新本地数据
                const index = this.userList.findIndex(u => u.userId === this.currentUser.userId);
                if (index !== -1) {
                    this.userList[index] = { ...this.currentUser };
                }
                alert('保存成功！');
                this.closeUserModal();
                this.loadUsers();
            } catch (error) {
                console.error('保存用户信息错误:', error);
                alert('保存失败，请稍后重试');
            }
        },
        
        closeUserModal() {
            this.showUserModal = false;
            this.currentUser = {
                userId: null,
                userName: '',
                userPhone: '',
                status: 1,
                registerTime: ''
            };
        },
        
        prevUserPage() {
            if (this.userPage > 1) {
                this.userPage--;
                this.loadUsers();
            }
        },
        
        nextUserPage() {
            this.userPage++;
            this.loadUsers();
        },

        // ========== 订单管理方法 ==========
        async loadOrders() {
            // 测试阶段使用硬编码数据
            console.log('加载订单列表');
            // 实际开发中替换为API调用
            // const response = await this.$axios.get('/admin/orders', {
            //     params: {
            //         page: this.orderPage,
            //         pageSize: this.orderPageSize,
            //         status: this.orderFilter.status,
            //         startDate: this.orderFilter.startDate,
            //         endDate: this.orderFilter.endDate
            //     }
            // });
            // this.orderList = response.data.data || [];
        },
        
        async viewOrderDetails(order) {
            // 测试阶段使用硬编码数据
            console.log('查看订单详情:', order.orderId);
            // 实际开发中替换为API调用
            // const response = await this.$axios.get(`/admin/orders/${order.orderId}`);
            // this.currentOrder = response.data.data || {};
            
            this.currentOrder = { ...order };
            this.showOrderDetailModal = true;
        },
        
        async confirmOrder(orderId) {
            if (confirm('确定要接单吗？')) {
                try {

                    
                    // 测试阶段直接更新本地数据
                    const order = this.orderList.find(o => o.orderId === orderId);
                    if (order) {
                        order.orderStatus = 3; // 已接单状态
                    }
                    alert('接单成功！');
                    this.closeOrderDetailModal();
                    this.loadOrders();
                } catch (error) {
                    console.error('接单错误:', error);
                    alert('接单失败，请稍后重试');
                }
            }
        },
        
        closeOrderDetailModal() {
            this.showOrderDetailModal = false;
            this.currentOrder = {
                orderId: '',
                userName: '',
                userPhone: '',
                businessName: '',
                orderTime: '',
                orderStatus: '',
                deliveryAddress: '',
                items: [],
                foodAmount: 0,
                deliveryPrice: 0,
                orderAmount: 0
            };
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

        // ========== 数据管理方法 ==========
        async loadStatistics() {
            // 测试阶段使用硬编码数据
            console.log('加载统计数据');
            // 实际开发中替换为API调用
            // const response = await this.$axios.get('/admin/stats/summary', {
            //     params: {
            //         startDate: this.statsStartDate,
            //         endDate: this.statsEndDate
            //     }
            // });
            // this.statistics = response.data.data || {};
            
            // 模拟数据变化
            this.statistics = {
                totalOrders: Math.floor(Math.random() * 200) + 100,
                totalRevenue: Math.floor(Math.random() * 5000) + 2000,
                newUsers: Math.floor(Math.random() * 20) + 5,
                activeBusinesses: Math.floor(Math.random() * 15) + 5,
                orderChange: Math.floor(Math.random() * 30) - 10,
                revenueChange: Math.floor(Math.random() * 30) - 10,
                userChange: Math.floor(Math.random() * 30) - 10,
                businessChange: Math.floor(Math.random() * 30) - 10
            };
        },

        // ========== 辅助方法 ==========
        formatDate(date) {
            if (!date) return '';
            return new Date(date).toLocaleDateString();
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
    height: 12vw;
    background-color: #0097FF;
    color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 4vw;
    box-sizing: border-box;
}

.header-left p {
    font-size: 4.5vw;
    font-weight: bold;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 3vw;
}

.header-right span {
    font-size: 3.2vw;
}

.header-right button {
    background-color: rgba(255,255,255,0.2);
    color: #fff;
    border: 1px solid #fff;
    padding: 1.5vw 3vw;
    border-radius: 1vw;
    font-size: 3vw;
    cursor: pointer;
    transition: background-color 0.3s;
}

.header-right button:hover {
    background-color: rgba(255,255,255,0.3);
}

/* 菜单 */
.menu-section {
    background-color: #fff;
    padding: 4vw;
    display: flex;
    gap: 4vw;
    border-bottom: 1px solid #eee;
    flex-wrap: wrap;
}

.menu-item {
    display: flex;
    align-items: center;
    gap: 2vw;
    padding: 2vw 4vw;
    border-radius: 2vw;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 3.5vw;
    color: #666;
}

.menu-item:hover,
.menu-item.active {
    background-color: #0097FF;
    color: #fff;
}

.menu-item i {
    font-size: 4vw;
}

/* 内容区 */
.content {
    padding: 4vw;
}

.section-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4vw;
    flex-wrap: wrap;
    gap: 2vw;
}

.section-title h3 {
    font-size: 4.5vw;
    color: #333;
    margin: 0;
}

.search-box {
    display: flex;
    gap: 2vw;
}

.search-box input {
    padding: 2vw;
    border: 1px solid #ddd;
    border-radius: 1vw;
    font-size: 3vw;
    flex: 1;
    min-width: 40vw;
}

.search-box button {
    background-color: #0097FF;
    color: #fff;
    border: none;
    padding: 0 4vw;
    border-radius: 1vw;
    font-size: 3vw;
    cursor: pointer;
}

.filter-section {
    display: flex;
    gap: 2vw;
    flex-wrap: wrap;
}

.filter-section select,
.filter-section input {
    padding: 2vw;
    border: 1px solid #ddd;
    border-radius: 1vw;
    font-size: 3vw;
    min-width: 20vw;
}

.date-range {
    display: flex;
    align-items: center;
    gap: 2vw;
}

.date-range input {
    padding: 2vw;
    border: 1px solid #ddd;
    border-radius: 1vw;
    font-size: 3vw;
}

.date-range span {
    font-size: 3vw;
    color: #666;
}

.business-selector select {
    padding: 2vw;
    border: 1px solid #ddd;
    border-radius: 1vw;
    font-size: 3vw;
}

.add-btn {
    background-color: #28a745;
    color: #fff;
    border: none;
    padding: 2vw 3vw;
    border-radius: 1.5vw;
    font-size: 3vw;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 1vw;
    transition: background-color 0.3s;
}

.add-btn:hover {
    background-color: #218838;
}

.add-btn:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.add-btn i {
    font-size: 3vw;
}

/* 列表样式 */
.business-list,
.food-list,
.user-list,
.order-list {
    background-color: #fff;
    border-radius: 2vw;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    margin-bottom: 4vw;
}

.list-header,
.list-item {
    display: grid;
    align-items: center;
    padding: 3vw 2vw;
    border-bottom: 1px solid #eee;
    font-size: 3vw;
}

.business-list .list-header,
.business-list .list-item {
    grid-template-columns: 1fr 3fr 4fr 2fr;
}

.food-list .list-header,
.food-list .list-item {
    grid-template-columns: 1fr 3fr 4fr 1.5fr 2fr;
}

.user-list .list-header,
.user-list .list-item {
    grid-template-columns: 1fr 2fr 3fr 3fr 1.5fr 2fr;
}

.order-list .list-header,
.order-list .list-item {
    grid-template-columns: 2fr 2fr 2fr 1.5fr 2fr 1.5fr 2fr;
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
.col-user, .col-business, .col-amount, .col-time,
.col-phone, .col-register, .col-status {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.col-action {
    display: flex;
    gap: 2vw;
    justify-content: flex-end;
}

.edit-btn,
.delete-btn,
.toggle-btn,
.detail-btn {
    min-width: 8vw;
    height: 6vw;
    border: none;
    border-radius: 1vw;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2.5vw;
    padding: 0 2vw;
    transition: opacity 0.3s;
}

.edit-btn:hover,
.delete-btn:hover,
.toggle-btn:hover,
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

.toggle-btn {
    color: #fff;
    font-size: 2.8vw;
}

.disable-btn {
    background-color: #dc3545;
}

.enable-btn {
    background-color: #28a745;
}

.detail-btn {
    background-color: #17a2b8;
    color: #fff;
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

.active {
    color: #28a745;
}

.inactive {
    color: #dc3545;
}

/* 空状态 */
.empty-state {
    text-align: center;
    padding: 10vw;
    color: #999;
    font-size: 4vw;
    background-color: #fff;
    border-radius: 2vw;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

/* 分页控件 */
.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 4vw;
    margin-top: 4vw;
}

.pagination button {
    background-color: #0097FF;
    color: #fff;
    border: none;
    padding: 2vw 4vw;
    border-radius: 1vw;
    font-size: 3vw;
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
    font-size: 3.5vw;
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

.business-modal,
.order-detail-modal {
    width: 90%;
    max-width: 500px;
    background-color: #fff;
    border-radius: 3vw;
    padding: 6vw;
    max-height: 80vh;
    overflow-y: auto;
}

.business-modal h3,
.order-detail-modal h3 {
    font-size: 4.5vw;
    color: #333;
    margin-bottom: 4vw;
    text-align: center;
}

.form-group {
    margin-bottom: 4vw;
}

.form-group label {
    display: block;
    font-size: 3.5vw;
    color: #333;
    margin-bottom: 2vw;
}

.form-group input,
.form-group select {
    width: 100%;
    height: 10vw;
    border: 1px solid #ddd;
    border-radius: 1.5vw;
    padding: 0 3vw;
    font-size: 3.5vw;
    box-sizing: border-box;
}

.modal-buttons {
    display: flex;
    gap: 3vw;
    margin-top: 6vw;
}

.cancel-btn,
.confirm-btn {
    flex: 1;
    height: 10vw;
    border: none;
    border-radius: 1.5vw;
    font-size: 3.5vw;
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
    margin-bottom: 2vw;
    font-size: 3.5vw;
}

.order-info .label {
    width: 25%;
    color: #666;
}

.order-info .value {
    flex: 1;
}

.order-items {
    margin: 4vw 0;
    border-top: 1px solid #eee;
    padding-top: 3vw;
}

.order-items h4 {
    font-size: 3.5vw;
    margin-bottom: 2vw;
    color: #333;
}

.order-items .item {
    display: flex;
    justify-content: space-between;
    padding: 1.5vw 0;
    border-bottom: 1px solid #eee;
    font-size: 3vw;
}

.order-summary {
    border-top: 1px solid #eee;
    padding-top: 3vw;
}

.order-summary .summary-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 2vw;
    font-size: 3.5vw;
}

.order-summary .total {
    font-weight: bold;
    color: #333;
    font-size: 4vw;
}

/* 数据统计样式 */
.stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 2vw;
    margin-bottom: 4vw;
}

.card {
    background: #fff;
    border-radius: 2vw;
    padding: 3vw;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    transition: transform 0.3s;
}

.card:hover {
    transform: translateY(-5px);
}

.card-title {
    font-size: 3vw;
    color: #666;
    margin-bottom: 1vw;
}

.card-value {
    font-size: 4.5vw;
    font-weight: bold;
    color: #333;
    margin-bottom: 1vw;
}

.top-lists {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 2vw;
}

.top-list {
    background: #fff;
    border-radius: 2vw;
    padding: 3vw;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.top-list h4 {
    font-size: 3.5vw;
    margin-bottom: 2vw;
    color: #333;
    text-align: center;
}

.top-list .list-item {
    display: flex;
    align-items: center;
    padding: 1.5vw 0;
    border-bottom: 1px solid #eee;
}

.top-list .list-item:last-child {
    border-bottom: none;
}

.top-list .rank {
    width: 5vw;
    height: 5vw;
    background: #0097FF;
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2.5vw;
    margin-right: 2vw;
}

.top-list .name {
    flex: 1;
    font-size: 3vw;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.top-list .count {
    font-size: 3vw;
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
}

@media (max-width: 480px) {
    .stats-cards {
        grid-template-columns: 1fr;
    }
    
    .business-list .list-header,
    .business-list .list-item {
        grid-template-columns: 1fr 2fr 1fr;
    }
    
    .food-list .list-header,
    .food-list .list-item {
        grid-template-columns: 1fr 2fr 1fr;
    }
    
    .user-list .list-header,
    .user-list .list-item {
        grid-template-columns: 1fr 2fr 1fr;
    }
    
    .order-list .list-header,
    .order-list .list-item {
        grid-template-columns: 2fr 1fr 1fr;
    }
    
    .col-explain, .col-register, .col-business, .col-phone {
        display: none;
    }
}
</style>