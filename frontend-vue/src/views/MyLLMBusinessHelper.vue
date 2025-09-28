<template>
  <div class="nutrition-expert-container">
    <!-- 顶部标题栏 -->
    <div class="header">
      <div class="header-content">
        <i class="fas fa-apple-alt header-icon"></i>
        <h1>外卖商家经营助手</h1>
        <p>专业解答店铺运营问题</p>
      </div>
    </div>

    <!-- API 密钥输入区 -->
    <!-- <div class="api-key-section">
      <div class="section-title">
        <i class="fas fa-key"></i>
        <span>API 密钥设置</span>
      </div>
      <div class="input-group">
        <input
          type="password"
          v-model="apiKey"
          placeholder="请输入您的 DeepSeek API 密钥"
          class="api-input"
        />
        <button 
          @click="saveApiKey" 
          :disabled="!apiKey"
          class="save-btn"
          :class="{ saved: isKeySaved }"
        >
          <i class="fas" :class="isKeySaved ? 'fa-check' : 'fa-save'"></i>
          {{ isKeySaved ? '已保存' : '保存密钥' }}
        </button>
      </div>
      <div class="api-status" :class="{ connected: isApiConnected }">
        <i class="fas" :class="isApiConnected ? 'fa-check-circle' : 'fa-times-circle'"></i>
        {{ isApiConnected ? 'API 已连接，可以开始咨询' : 'API 未连接' }}
      </div>
    </div> -->

    <!-- 聊天界面 -->
    <div class="chat-container" :class="{ connected: isApiConnected }">
      <div class="chat-header">
        <div class="expert-info">
          <div class="expert-avatar">
            <i class="fas fa-user-md"></i>
          </div>
          <div class="expert-details">
            <h3>外卖商家经营助手</h3>
             <p>专业解答店铺运营问题</p>
          </div>
        </div>
        <div class="connection-status">
          <span class="status-dot" :class="{ online: isApiConnected }"></span>
          {{ isApiConnected ? '在线' : '离线' }}
        </div>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <!-- 欢迎消息 -->
        <div class="welcome-message">
          <div class="welcome-content">
            <i class="fas fa-heart"></i>
            <h1>外卖商家经营助手</h1>
            <p>您的智能餐饮经营顾问，提升店铺运营效率</p>
            <div class="welcome-examples">
              <span>例如：</span>
              <ul>
                <li>「如何提升店铺曝光率？」</li>
                <li>「菜单优化有什么技巧？」</li>
                <li>「怎样设计促销活动更有效？」</li>
                <li>「如何降低运营成本？」</li>
                <li>「客户差评如何处理？」</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div
          v-for="(message, index) in messages"
          :key="index"
          :class="['message', message.role]"
        >
          <div class="message-avatar">
            <i v-if="message.role === 'assistant'" class="fas fa-user-md"></i>
            <i v-else class="fas fa-user"></i>
          </div>
          <div class="message-content">
            <p>{{ message.content }}</p>
            <span class="message-time">{{ formatTime(message.timestamp) }}</span>
          </div>
        </div>

        <!-- 输入中状态 -->
        <div v-if="isLoading" class="message assistant">
          <div class="message-avatar">
            <i class="fas fa-user-md"></i>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span>营养专家正在思考</span>
              <div class="dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-container">
        <div class="input-wrapper">
          <textarea
            v-model="userInput"
            @keyup.enter="sendMessage"
            placeholder="请输入您的营养健康问题..."
            :disabled="!isApiConnected || isLoading"
            rows="1"
          ></textarea>
          <button
            @click="sendMessage"
            :disabled="!userInput || !isApiConnected || isLoading"
            class="send-button"
          >
            <i class="fas fa-paper-plane"></i>
          </button>
        </div>
        <div class="input-hint">
          <i class="fas fa-lightbulb"></i>
          提示：可以咨询运营策略、菜单优化、促销活动、成本控制等问题
        </div>
      </div>
    </div>

    <!-- 简易设置 -->
    <div class="simple-settings">
      <div class="settings-toggle" @click="showSettings = !showSettings">
        <i class="fas fa-cog"></i>
        <span>高级设置</span>
        <i class="fas" :class="showSettings ? 'fa-chevron-up' : 'fa-chevron-down'"></i>
      </div>
      
      <div v-if="showSettings" class="settings-content">
        <div class="setting-item">
          <label>回复温度：</label>
          <div class="slider-container">
            <input
              type="range"
              v-model="temperature"
              min="0"
              max="2"
              step="0.1"
            />
            <span>{{ temperature }}（{{ getTemperatureDesc() }}）</span>
          </div>
        </div>
        <div class="setting-item">
          <label>回复长度：</label>
          <select v-model="maxTokens">
            <option value="500">简短</option>
            <option value="1000">适中</option>
            <option value="2000">详细</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="footer">
      <p>Powered by DeepSeek AI</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';

const DEEPSEEK_API_URL = 'https://api.deepseek.com/v1/chat/completions';

// 响应式数据
const apiKey = ref('');
const isKeySaved = ref(false);
const isApiConnected = ref(false);
const messages = ref([]);
const userInput = ref('');
const isLoading = ref(false);
const messagesContainer = ref(null);
const showSettings = ref(false);

// 模型参数
const temperature = ref(0.7);
const maxTokens = ref(1000);

// 营养专家系统提示词
const businessAssistantPrompt = `你是一位专业的外卖商家经营助手，专门帮助餐饮商家提升经营效率、优化菜单、提高销量和改善服务质量。

请遵循以下原则：
1. 提供具体可操作的建议，避免空泛理论
2. 结合外卖平台特点和用户行为数据
3. 考虑不同品类餐厅的特殊性
4. 注重成本控制和实际效果
5. 用通俗易懂的语言解释专业概念
6. 不要输出markdown格式 使用纯文本

核心服务领域：

【菜单优化】
- 分析热销菜品和滞销菜品
- 建议菜品组合和套餐设计
- 定价策略和利润空间优化
- 季节性菜单调整建议

【运营策略】
- 促销活动设计（满减、折扣、优惠券）
- 高峰期备货和出餐效率提升
- 骑手配送协调和时效管理
- 评价管理和客户关系维护

【数据分析】
- 销售数据解读和趋势分析
- 客户画像和消费习惯分析
- 竞品分析和差异化策略
- ROI评估和投入产出比计算

【服务质量】
- 包装改进和食品安全
- 客户投诉处理标准流程
- 复购率提升策略
- 品牌形象塑造

请根据商家的具体问题，提供针对性、可落地的解决方案。`;

// 保存 API 密钥
const saveApiKey = () => {
  if (apiKey.value) {
    localStorage.setItem('deepseek_api_key', apiKey.value);
    isKeySaved.value = true;
    checkApiConnection();
  }
};

// 检查 API 连接
const checkApiConnection = async () => {
  if (!apiKey.value) return;

  try {
    isLoading.value = true;
    const response = await axios.post(
      DEEPSEEK_API_URL,
      {
        model: 'deepseek-chat',
        messages: [{ role: 'user', content: '测试连接' }],
        max_tokens: 5,
      },
      {
        headers: {
          'Authorization': `Bearer sk-ea65bb27c3a642718ecbea5ea3980779`,
          'Content-Type': 'application/json',
        },
      }
    );

    isApiConnected.value = response.status === 200;
    if (isApiConnected.value) {
      addSystemMessage('您好！我是您的店铺经营，很高兴为您服务。请问有什么饮食健康方面的问题需要咨询吗？');
    }
  } catch (error) {
    console.error('API 连接失败:', error);
    isApiConnected.value = false;
    addSystemMessage('API 连接失败，请检查密钥是否正确');
  } finally {
    isLoading.value = false;
  }
};

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || !isApiConnected.value || isLoading.value) return;

  const userMessage = {
    role: 'user',
    content: userInput.value,
    timestamp: new Date(),
  };

  messages.value.push(userMessage);
  userInput.value = '';
  isLoading.value = true;

  scrollToBottom();

  try {
    const response = await axios.post(
      DEEPSEEK_API_URL,
      {
        model: 'deepseek-chat',
        messages: [
          {
            role: 'system',
            content: nutritionSystemPrompt,
          },
          ...messages.value.map((msg) => ({
            role: msg.role,
            content: msg.content,
          })),
        ],
        temperature: parseFloat(temperature.value),
        max_tokens: parseInt(maxTokens.value),
      },
      {
        headers: {
          'Authorization': `Bearer ${apiKey.value}`,
          'Content-Type': 'application/json',
        },
      }
    );

    const aiResponse = response.data.choices[0].message.content;
    messages.value.push({
      role: 'assistant',
      content: aiResponse,
      timestamp: new Date(),
    });
  } catch (error) {
    console.error('API 调用失败:', error);
    addSystemMessage('抱歉，暂时无法处理您的请求。请稍后再试或检查网络连接。');
  } finally {
    isLoading.value = false;
    scrollToBottom();
  }
};

// 添加系统消息
const addSystemMessage = (content) => {
  messages.value.push({
    role: 'system',
    content,
    timestamp: new Date(),
  });
  scrollToBottom();
};

// 格式化时间
const formatTime = (date) => {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

// 获取温度描述
const getTemperatureDesc = () => {
  const temp = parseFloat(temperature.value);
  if (temp < 0.5) return '精准';
  if (temp < 1.0) return '平衡';
  if (temp < 1.5) return '创意';
  return '非常创意';
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

// 初始化
onMounted(() => {
  const savedKey = localStorage.getItem('deepseek_api_key');
  if (savedKey) {
    apiKey.value = savedKey;
    isKeySaved.value = true;
    checkApiConnection();
  }
});
</script>

<style scoped>
.nutrition-expert-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f2ff 100%);
  min-height: 100vh;
}

/* 头部样式 */
.header {
  background: linear-gradient(135deg, #0097ff 0%, #0077e6 100%);
  color: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 24px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 151, 255, 0.15);
}

.header-content {
  max-width: 500px;
  margin: 0 auto;
}

.header-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: rgba(255, 255, 255, 0.9);
}

.header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.header p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

/* API 密钥区域 */
.api-key-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  color: #0066cc;
  font-weight: 600;
}

.section-title i {
  font-size: 20px;
}

.input-group {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.api-input {
  flex: 1;
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.api-input:focus {
  outline: none;
  border-color: #0097ff;
}

.save-btn {
  padding: 16px 24px;
  background: #0097ff;
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  min-width: 120px;
}

.save-btn:hover:not(:disabled) {
  background: #0088ee;
  transform: translateY(-2px);
}

.save-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.save-btn.saved {
  background: #0077e6;
}

.api-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  padding: 12px;
  border-radius: 8px;
  background: #fff3cd;
  color: #856404;
}

.api-status.connected {
  background: #e6f7ff;
  color: #0066cc;
  border: 1px solid #b3e0ff;
}

.api-status i {
  font-size: 18px;
}

/* 聊天容器 */
.chat-container {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 151, 255, 0.12);
  margin-bottom: 24px;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.chat-container.connected {
  opacity: 1;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #0097ff 0%, #0066cc 100%);
  color: white;
}

.expert-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.expert-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.expert-details h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
}

.expert-details p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ccc;
}

.status-dot.online {
  background: #0097ff;
  box-shadow: 0 0 8px rgba(0, 151, 255, 0.5);
}

/* 聊天消息 */
.chat-messages {
  height: 500px;
  overflow-y: auto;
  padding: 24px;
  background: #f7fbff;
}

.welcome-message {
  text-align: center;
  margin-bottom: 32px;
}

.welcome-content {
  background: linear-gradient(135deg, #e6f2ff 0%, #cce5ff 100%);
  padding: 24px;
  border-radius: 16px;
  border-left: 4px solid #0097ff;
}

.welcome-content i {
  font-size: 32px;
  color: #0097ff;
  margin-bottom: 16px;
}

.welcome-content h3 {
  margin: 0 0 12px 0;
  color: #0066cc;
}

.welcome-content p {
  margin: 0 0 16px 0;
  color: #333;
}

.welcome-examples {
  text-align: left;
}

.welcome-examples span {
  font-weight: 600;
  color: #555;
}

.welcome-examples ul {
  margin: 8px 0 0 0;
  padding-left: 20px;
}

.welcome-examples li {
  color: #666;
  margin-bottom: 4px;
}

.message {
  display: flex;
  margin-bottom: 20px;
  gap: 16px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #0097ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  font-size: 18px;
}

.message.assistant .message-avatar {
  background: #0066cc;
}

.message-content {
  max-width: 70%;
  padding: 16px 20px;
  border-radius: 18px;
  position: relative;
}

.message.assistant .message-content {
  background: white;
  border: 1px solid #e0e0e0;
  color: #333;
}

.message.user .message-content {
  background: #0097ff;
  color: white;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  display: block;
}

.message.user .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #666;
}

.dots {
  display: flex;
  gap: 4px;
}

.dots span {
  width: 6px;
  height: 6px;
  background: #0097ff;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.dots span:nth-child(2) { animation-delay: 0.2s; }
.dots span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.6; }
  30% { transform: translateY(-4px); opacity: 1; }
}

/* 输入区域 */
.chat-input-container {
  padding: 20px;
  border-top: 1px solid #e6e6e6;
  background: white;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.input-wrapper textarea {
  flex: 1;
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  resize: none;
  font-size: 16px;
  transition: border-color 0.3s ease;
  min-height: 60px;
  max-height: 120px;
}

.input-wrapper textarea:focus {
  outline: none;
  border-color: #0097ff;
}

.send-button {
  width: 50px;
  height: 50px;
  border: none;
  border-radius: 50%;
  background: #0097ff;
  color: white;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-button:hover:not(:disabled) {
  background: #0088ee;
  transform: scale(1.05);
}

.send-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.input-hint {
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.input-hint i {
  color: #0097ff;
}

/* 设置区域 */
.simple-settings {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.settings-toggle {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  color: #666;
  font-weight: 500;
}

.settings-content {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.setting-item {
  margin-bottom: 16px;
}

.setting-item label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.slider-container {
  display: flex;
  align-items: center;
  gap: 16px;
}

.slider-container input[type="range"] {
  flex: 1;
}

.setting-item select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
}

/* 底部 */
.footer {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 32px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nutrition-expert-container {
    padding: 16px;
  }
  
  .header {
    padding: 24px 20px;
  }
  
  .header h1 {
    font-size: 24px;
  }
  
  .input-group {
    flex-direction: column;
  }
  
  .chat-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .message-content {
    max-width: 85%;
  }
}
</style>