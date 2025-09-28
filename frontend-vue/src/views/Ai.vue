<template>
    <div class="wrapper">
        <!-- Header -->
        <header>
            <div class="header-left">
                <p>智能数据分析平台</p>
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
                :class="{ active: currentTab === 'dashboard' }"
                @click="switchTab('dashboard')"
            >
                <span>数据概览</span>
            </div>
            <div 
                class="menu-item" 
                :class="{ active: currentTab === 'analysis' }"
                @click="switchTab('analysis')"
            >
                <span>智能分析</span>
            </div>
            <div 
                class="menu-item" 
                :class="{ active: currentTab === 'history' }"
                @click="switchTab('history')"
            >
                <span>分析历史</span>
            </div>
        </div>

        <!-- 数据概览 -->
        <div v-if="currentTab === 'dashboard'" class="content">
            <div class="section-title">
                <h3>数据概览</h3>
                <div class="date-range">
                    <input type="date" v-model="statsStartDate" @change="loadStatistics">
                    <span>至</span>
                    <input type="date" v-model="statsEndDate" @change="loadStatistics">
                </div>
            </div>

            <div class="stats-cards">
                <div class="card">
                    <div class="card-title">总商家数</div>
                    <div class="card-value">{{ statistics.totalBusinesses }}</div>
                    <div class="card-change" :class="getChangeClass(statistics.businessChange)">
                        <i class="fa" :class="getChangeIcon(statistics.businessChange)"></i>
                        {{ Math.abs(statistics.businessChange) }}%
                    </div>
                </div>
                <div class="card">
                    <div class="card-title">活跃商家</div>
                    <div class="card-value">{{ statistics.activeBusinesses }}</div>
                    <div class="card-change" :class="getChangeClass(statistics.activeChange)">
                        <i class="fa" :class="getChangeIcon(statistics.activeChange)"></i>
                        {{ Math.abs(statistics.activeChange) }}%
                    </div>
                </div>
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
            </div>

            <div class="data-actions">
                <button class="action-btn primary" @click="fetchAllData">
                    <i class="fa fa-sync"></i> 刷新数据
                </button>
                <button class="action-btn secondary" @click="prepareAnalysis">
                    <i class="fa fa-brain"></i> 智能分析
                </button>
            </div>
        </div>

        <!-- 智能分析 -->
        <div v-if="currentTab === 'analysis'" class="content">
            <div class="section-title">
                <h3>智能数据分析</h3>
                <div class="analysis-controls">
                    <select v-model="selectedDataType">
                        <option value="business">商家数据</option>
                        <option value="sales">销售数据</option>
                        <option value="user">用户行为</option>
                        <option value="trend">趋势分析</option>
                    </select>
                    <button class="analyze-btn" @click="startAnalysis" :disabled="analyzing">
                        <i class="fa" :class="analyzing ? 'fa-spinner fa-spin' : 'fa-magic'"></i>
                        {{ analyzing ? '分析中...' : '开始分析' }}
                    </button>
                </div>
            </div>

            <!-- 数据加载状态 -->
            <div v-if="loading" class="loading-state">
                <i class="fa fa-spinner fa-spin"></i> 正在获取数据...
            </div>

            <!-- 分析结果 -->
            <div v-if="analysisResult" class="analysis-result">
                <div class="result-header">
                    <h4>AI分析结果</h4>
                    <span class="result-time">{{ formatDateTime(analysisResult.timestamp) }}</span>
                </div>
                
                <div class="insights-section">
                    <h5>核心洞察</h5>
                    <div class="insight-cards">
                        <div class="insight-card" v-for="(insight, index) in analysisResult.insights" :key="index">
                            <div class="insight-icon">
                                <i class="fa" :class="getInsightIcon(insight.type)"></i>
                            </div>
                            <div class="insight-content">
                                <h6>{{ insight.title }}</h6>
                                <p>{{ insight.description }}</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="recommendations-section">
                    <h5>优化建议</h5>
                    <ul class="recommendation-list">
                        <li v-for="(recommendation, index) in analysisResult.recommendations" :key="index">
                            <i class="fa fa-check"></i> {{ recommendation }}
                        </li>
                    </ul>
                </div>

                <div class="metrics-section">
                    <h5>关键指标</h5>
                    <div class="metrics-grid">
                        <div class="metric-item" v-for="(metric, key) in analysisResult.metrics" :key="key">
                            <span class="metric-label">{{ metric.label }}</span>
                            <span class="metric-value" :class="getMetricClass(metric.value, metric.threshold)">
                                {{ metric.value }}{{ metric.unit }}
                            </span>
                            <span class="metric-trend" v-if="metric.trend">
                                <i class="fa" :class="metric.trend === 'up' ? 'fa-arrow-up' : 'fa-arrow-down'"></i>
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 分析历史快捷入口 -->
            <div class="history-quick-access">
                <h5>最近分析</h5>
                <div class="history-items">
                    <div class="history-item" v-for="item in recentAnalysis" :key="item.id" @click="loadAnalysisResult(item)">
                        <div class="history-type">{{ item.dataType }}</div>
                        <div class="history-time">{{ formatDate(item.timestamp) }}</div>
                        <div class="history-summary">{{ item.summary }}</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 分析历史 -->
        <div v-if="currentTab === 'history'" class="content">
            <div class="section-title">
                <h3>分析历史记录</h3>
                <div class="history-filters">
                    <select v-model="historyFilter.type">
                        <option value="">全部类型</option>
                        <option value="business">商家分析</option>
                        <option value="sales">销售分析</option>
                        <option value="user">用户分析</option>
                    </select>
                    <input type="date" v-model="historyFilter.startDate" placeholder="开始日期">
                    <input type="date" v-model="historyFilter.endDate" placeholder="结束日期">
                </div>
            </div>

            <div class="history-list">
                <div class="list-header">
                    <div class="col-type">分析类型</div>
                    <div class="col-time">分析时间</div>
                    <div class="col-summary">分析摘要</div>
                    <div class="col-action">操作</div>
                </div>
                
                <div class="list-item" v-for="item in filteredHistory" :key="item.id">
                    <div class="col-type">
                        <span class="type-badge" :class="item.dataType">{{ getTypeLabel(item.dataType) }}</span>
                    </div>
                    <div class="col-time">{{ formatDateTime(item.timestamp) }}</div>
                    <div class="col-summary">{{ item.summary }}</div>
                    <div class="col-action">
                        <button class="view-btn" @click="loadAnalysisResult(item)">
                            <i class="fa fa-eye"></i> 查看
                        </button>
                        <button class="delete-btn" @click="deleteAnalysis(item.id)">
                            <i class="fa fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>

            <!-- 分页控件 -->
            <div class="pagination">
                <button @click="prevHistoryPage" :disabled="historyPage === 1">上一页</button>
                <span>第 {{ historyPage }} 页</span>
                <button @click="nextHistoryPage" :disabled="filteredHistory.length < historyPageSize">下一页</button>
            </div>
        </div>

        <!-- 详细分析结果模态框 -->
        <div class="modal-overlay" v-if="showAnalysisDetail" @click="closeAnalysisDetail">
            <div class="analysis-detail-modal" @click.stop>
                <h3>详细分析报告</h3>
                
                <div class="detail-content">
                    <div class="detail-section">
                        <h4>数据概览</h4>
                        <p>{{ currentAnalysis.dataOverview }}</p>
                    </div>
                    
                    <div class="detail-section">
                        <h4>深度分析</h4>
                        <div class="deep-analysis">
                            <div v-for="(point, index) in currentAnalysis.deepAnalysis" :key="index" class="analysis-point">
                                <h5>{{ point.title }}</h5>
                                <p>{{ point.content }}</p>
                                <ul v-if="point.examples">
                                    <li v-for="(example, exIndex) in point.examples" :key="exIndex">{{ example }}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    
                    <div class="detail-section">
                        <h4>执行计划</h4>
                        <div class="action-plan">
                            <div class="plan-phase" v-for="(phase, index) in currentAnalysis.actionPlan" :key="index">
                                <h5>阶段 {{ index + 1 }}: {{ phase.title }}</h5>
                                <ul>
                                    <li v-for="(action, actionIndex) in phase.actions" :key="actionIndex">{{ action }}</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="modal-buttons">
                    <button class="cancel-btn" @click="closeAnalysisDetail">关闭</button>
                    <button class="export-btn" @click="exportAnalysis">
                        <i class="fa fa-download"></i> 导出报告
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    name: 'SmartDataAnalysis',
    data() {
        return {
            adminName: '数据分析师',
            currentTab: 'dashboard',
            loading: false,
            analyzing: false,
            
            // 数据统计
            statistics: {
                totalBusinesses: 0,
                activeBusinesses: 0,
                totalOrders: 0,
                totalRevenue: 0,
                businessChange: 0,
                activeChange: 0,
                orderChange: 0,
                revenueChange: 0
            },
            
            // 分析相关
            selectedDataType: 'business',
            analysisResult: null,
            currentAnalysis: null,
            showAnalysisDetail: false,
            
            // 历史记录
            analysisHistory: [],
            recentAnalysis: [],
            historyFilter: {
                type: '',
                startDate: '',
                endDate: ''
            },
            historyPage: 1,
            historyPageSize: 10
        }
    },
    computed: {
        filteredHistory() {
            return this.analysisHistory.filter(item => {
                let match = true;
                if (this.historyFilter.type) {
                    match = match && item.dataType === this.historyFilter.type;
                }
                if (this.historyFilter.startDate) {
                    match = match && new Date(item.timestamp) >= new Date(this.historyFilter.startDate);
                }
                if (this.historyFilter.endDate) {
                    match = match && new Date(item.timestamp) <= new Date(this.historyFilter.endDate);
                }
                return match;
            }).slice((this.historyPage - 1) * this.historyPageSize, this.historyPage * this.historyPageSize);
        }
    },
    mounted() {
        this.loadStatistics();
        this.loadAnalysisHistory();
    },
    methods: {
        // 切换标签页
        switchTab(tab) {
            this.currentTab = tab;
        },
        
        // 加载统计数据
        async loadStatistics() {
            this.loading = true;
            try {
                // 模拟API调用
                const response = await axios.get('/api/statistics');
                this.statistics = response.data;
            } catch (error) {
                console.error('加载统计数据失败:', error);
            } finally {
                this.loading = false;
            }
        },
        
        // 获取所有数据用于分析
        async fetchAllData() {
            this.loading = true;
            try {
                const [businessData, salesData, userData] = await Promise.all([
                    axios.get('/api/businesses'),
                    axios.get('/api/sales'),
                    axios.get('/api/users')
                ]);
                
                this.preparedData = {
                    businesses: businessData.data,
                    sales: salesData.data,
                    users: userData.data,
                    timestamp: new Date()
                };
                
                alert('数据准备完成，可以开始分析');
            } catch (error) {
                console.error('获取数据失败:', error);
                alert('数据获取失败，请检查网络连接');
            } finally {
                this.loading = false;
            }
        },
        
        // 准备分析数据
        prepareAnalysis() {
            this.switchTab('analysis');
            this.fetchAllData();
        },
        
        // 开始智能分析
        async startAnalysis() {
            if (!this.preparedData) {
                alert('请先获取数据');
                return;
            }
            
            this.analyzing = true;
            try {
                const analysisPayload = {
                    dataType: this.selectedDataType,
                    data: this.preparedData,
                    analysisConfig: {
                        depth: 'detailed',
                        includeRecommendations: true,
                        includeMetrics: true
                    }
                };
                
                const response = await axios.post('/api/llm/analyze', analysisPayload);
                this.analysisResult = response.data;
                
                // 保存到历史记录
                this.saveToHistory(response.data);
                
            } catch (error) {
                console.error('分析请求失败:', error);
                alert('分析失败，请稍后重试');
            } finally {
                this.analyzing = false;
            }
        },
        
        // 保存分析结果到历史
        saveToHistory(result) {
            const historyItem = {
                id: Date.now(),
                dataType: this.selectedDataType,
                timestamp: new Date(),
                summary: result.summary || '分析完成',
                fullResult: result
            };
            
            this.analysisHistory.unshift(historyItem);
            this.recentAnalysis = this.analysisHistory.slice(0, 5);
            
            // 保存到localStorage
            localStorage.setItem('analysisHistory', JSON.stringify(this.analysisHistory));
        },
        
        // 加载分析历史
        loadAnalysisHistory() {
            const savedHistory = localStorage.getItem('analysisHistory');
            if (savedHistory) {
                this.analysisHistory = JSON.parse(savedHistory);
                this.recentAnalysis = this.analysisHistory.slice(0, 5);
            }
        },
        
        // 加载历史分析结果
        loadAnalysisResult(item) {
            this.analysisResult = item.fullResult;
            this.currentTab = 'analysis';
        },
        
        // 查看详细分析
        showDetailAnalysis() {
            this.currentAnalysis = this.analysisResult.detail || this.analysisResult;
            this.showAnalysisDetail = true;
        },
        
        // 导出分析报告
        exportAnalysis() {
            const dataStr = JSON.stringify(this.currentAnalysis, null, 2);
            const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr);
            
            const exportFileDefaultName = `analysis-report-${new Date().toISOString().split('T')[0]}.json`;
            
            const linkElement = document.createElement('a');
            linkElement.setAttribute('href', dataUri);
            linkElement.setAttribute('download', exportFileDefaultName);
            linkElement.click();
        },
        
        // 删除分析记录
        deleteAnalysis(id) {
            if (confirm('确定要删除这条分析记录吗？')) {
                this.analysisHistory = this.analysisHistory.filter(item => item.id !== id);
                this.recentAnalysis = this.analysisHistory.slice(0, 5);
                localStorage.setItem('analysisHistory', JSON.stringify(this.analysisHistory));
            }
        },
        
        // 工具方法
        formatDate(date) {
            return new Date(date).toLocaleDateString();
        },
        
        formatDateTime(datetime) {
            return new Date(datetime).toLocaleString();
        },
        
        getChangeClass(change) {
            return change >= 0 ? 'positive' : 'negative';
        },
        
        getChangeIcon(change) {
            return change >= 0 ? 'fa-arrow-up' : 'fa-arrow-down';
        },
        
        getInsightIcon(type) {
            const icons = {
                warning: 'fa-exclamation-triangle',
                success: 'fa-check-circle',
                opportunity: 'fa-lightbulb',
                risk: 'fa-shield-alt'
            };
            return icons[type] || 'fa-info-circle';
        },
        
        getMetricClass(value, threshold) {
            if (threshold && value < threshold) return 'low';
            if (value > threshold * 1.2) return 'high';
            return 'normal';
        },
        
        getTypeLabel(type) {
            const labels = {
                business: '商家分析',
                sales: '销售分析',
                user: '用户分析',
                trend: '趋势分析'
            };
            return labels[type] || type;
        },
        
        closeAnalysisDetail() {
            this.showAnalysisDetail = false;
        },
        
        prevHistoryPage() {
            if (this.historyPage > 1) this.historyPage--;
        },
        
        nextHistoryPage() {
            this.historyPage++;
        },
        
        logout() {
            if (confirm('确定要退出系统吗？')) {
                // 实际项目中这里会有登出逻辑
                console.log('用户登出');
            }
        }
    }
}
</script>

<style scoped>
/* 基础样式保持不变，新增智能分析特有样式 */

.analysis-controls {
    display: flex;
    gap: 2vw;
    align-items: center;
}

.analyze-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    padding: 2vw 4vw;
    border-radius: 1.5vw;
    cursor: pointer;
    font-size: 3.5vw;
    display: flex;
    align-items: center;
    gap: 1vw;
}

.analyze-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.analysis-result {
    background: white;
    border-radius: 3vw;
    padding: 4vw;
    margin-top: 4vw;
    box-shadow: 0 2px 20px rgba(0,0,0,0.1);
}

.result-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4vw;
    padding-bottom: 2vw;
    border-bottom: 2px solid #f0f0f0;
}

.insight-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 3vw;
    margin: 3vw 0;
}

.insight-card {
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    padding: 3vw;
    border-radius: 2vw;
    border-left: 4px solid #007bff;
}

.insight-icon {
    font-size: 5vw;
    color: #007bff;
    margin-bottom: 2vw;
}

.recommendation-list {
    list-style: none;
    padding: 0;
}

.recommendation-list li {
    padding: 2vw;
    margin: 1vw 0;
    background: #f8f9fa;
    border-radius: 1vw;
    border-left: 3px solid #28a745;
}

.metrics-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 2vw;
    margin-top: 3vw;
}

.metric-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2vw;
    background: #f8f9fa;
    border-radius: 1vw;
}

.metric-value.low { color: #dc3545; }
.metric-value.normal { color: #28a745; }
.metric-value.high { color: #ffc107; }

.history-quick-access {
    margin-top: 6vw;
}

.history-items {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 3vw;
    margin-top: 3vw;
}

.history-item {
    background: white;
    padding: 3vw;
    border-radius: 2vw;
    cursor: pointer;
    transition: transform 0.3s;
    border: 2px solid #e9ecef;
}

.history-item:hover {
    transform: translateY(-2px);
    border-color: #007bff;
}

.type-badge {
    padding: 1vw 2vw;
    border-radius: 1vw;
    font-size: 2.8vw;
}

.type-badge.business { background: #e3f2fd; color: #1976d2; }
.type-badge.sales { background: #f3e5f5; color: #7b1fa2; }
.type-badge.user { background: #e8f5e8; color: #2e7d32; }

.analysis-detail-modal {
    max-width: 90%;
    max-height: 80vh;
    overflow-y: auto;
}

.detail-section {
    margin-bottom: 4vw;
}

.deep-analysis, .action-plan {
    margin-left: 3vw;
}

.analysis-point, .plan-phase {
    margin-bottom: 3vw;
    padding: 2vw;
    background: #f8f9fa;
    border-radius: 1vw;
}

.export-btn {
    background: #28a745;
    color: white;
}

.data-actions {
    display: flex;
    gap: 3vw;
    margin-top: 4vw;
    justify-content: center;
}

.action-btn {
    padding: 3vw 6vw;
    border: none;
    border-radius: 2vw;
    font-size: 3.5vw;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 1vw;
}

.action-btn.primary {
    background: #007bff;
    color: white;
}

.action-btn.secondary {
    background: #6c757d;
    color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .insight-cards,
    .metrics-grid,
    .history-items {
        grid-template-columns: 1fr;
    }
    
    .analysis-controls {
        flex-direction: column;
        align-items: stretch;
    }
    
    .data-actions {
        flex-direction: column;
    }
}
</style>