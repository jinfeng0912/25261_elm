<template>
  <div class="nutrition-expert-container">
    <!-- 顶部标题栏 -->
    <div class="header">
      <div class="header-content">
        <!-- 使用文本的返回按钮 -->
        <button class="back-button" @click="router.go(-1)">
          <span class="back-text">返回</span>
        </button>
        <i class="fas fa-apple-alt header-icon"></i>
        <h1>营养健康专家咨询</h1>
        <p>您的私人营养顾问，解答饮食健康问题</p>
        <p>接入DeepSeek模型</p>
      </div>
    </div>

    <!-- 连接状态 -->
    <div class="connection-status" :class="{ connected: isApiConnected }">
      <div class="status-content">
        <span class="status-indicator" :class="{ online: isApiConnected }"></span>
        <span v-if="isApiConnected" class="status-text">
          <i class="fas fa-check-circle"></i> API连接正常
        </span>
        <span v-else class="status-text">
          <i class="fas fa-exclamation-triangle"></i> API未连接
        </span>
      </div>
    </div>

    <!-- 聊天界面 -->
    <div class="chat-container" :class="{ connected: isApiConnected }">
      <div class="chat-header">
        <div class="expert-info">
          <div class="expert-avatar">
            <i class="fas fa-user-md"></i>
          </div>
          <div class="expert-details">
            <h3>营养专家模式</h3>
            <p>专业解答饮食健康问题</p>
          </div>
        </div>
        <div class="connection-status">
          <span class="status-dot" :class="{ online: isApiConnected }"></span>
          {{ isApiConnected ? '在线' : '离线' }}
        </div>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <!-- 欢迎消息 -->
        <div class="welcome-message" v-if="messages.length === 0">
          <div class="welcome-content">
            <i class="fas fa-heart"></i>
            <h3>欢迎咨询营养健康问题！</h3>
            <p>我可以为您提供专业的饮食建议、营养搭配和健康指导</p>
            <div class="welcome-examples">
              <span>例如：</span>
              <ul>
                <li>「减肥期间应该怎么吃？」</li>
                <li>「高血压患者饮食注意事项」</li>
                <li>「早餐吃什么最健康？」</li>
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
      <div class="chat-input-container" v-if="isApiConnected">
        <div class="input-wrapper">
          <textarea
            v-model="userInput"
            @keyup.enter="sendMessage"
            placeholder="请输入您的营养健康问题..."
            :disabled="isLoading"
            rows="1"
            ref="messageInput"
          ></textarea>
          <button
            @click="sendMessage"
            :disabled="!userInput.trim() || isLoading"
            class="send-button"
          >
            <i class="fas fa-paper-plane"></i>
          </button>
        </div>
        <div class="input-hint">
          <i class="fas fa-lightbulb"></i>
          提示：可以询问饮食搭配、营养补充、健康食谱等问题
        </div>
      </div>
    </div>

    <!-- 简易设置 -->
    <div class="simple-settings" v-if="isApiConnected">
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
        <div class="setting-item">
          <button @click="clearChat" class="clear-btn">
            <i class="fas fa-trash"></i> 清空对话
          </button>
        </div>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="footer">
      <p>Powered by DeepSeek AI • 专业营养健康咨询</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

// 添加路由实例
const router = useRouter();
// 使用正确的DeepSeek API端点
const DEEPSEEK_API_URL = 'https://api.deepseek.com/chat/completions';

// 在这里硬编码API密钥
const API_KEY = 'sk-ea65bb27c3a642718ecbea5ea3980779';

// 响应式数据
const isApiConnected = ref(false);
const messages = ref([]);
const userInput = ref('');
const isLoading = ref(false);
const messagesContainer = ref(null);
const messageInput = ref(null);
const showSettings = ref(false);

// 模型参数
const temperature = ref(0.7);
const maxTokens = ref(1000);

// 营养专家系统提示词
const nutritionSystemPrompt = `你是一位专业、亲切的营养健康专家。请用温暖、易懂的语言回答用户的饮食健康问题。

请遵循以下原则：
1. 像朋友一样亲切交流，避免专业术语
2. 用解释性语言说明营养知识
3. 提供实用的饮食建议和具体示例
4. 关注日常生活中的实际应用
5. 语气温和鼓励，给予积极建议
6. 考虑中国人的饮食习惯和常见食材
7. 不要输出markdown格式，使用纯文本

请用中文回答，保持专业但亲切的风格。`;

// 检查 API 连接
const checkApiConnection = async () => {
  console.log("开始检查API连接...");
  try {
    isLoading.value = true;
    
    const testResponse = await axios.post(
      DEEPSEEK_API_URL,
      {
        model: 'deepseek-chat',
        messages: [
          {
            role: 'system',
            content: '请简单回复"连接测试成功"'
          },
          {
            role: 'user', 
            content: '测试连接'
          }
        ],
        max_tokens: 10,
        temperature: 0.1,
        stream: false
      },
      {
        headers: {
          'Authorization': `Bearer ${API_KEY}`,
          'Content-Type': 'application/json',
        },
        timeout: 10000 // 10秒超时
      }
    );

    if (testResponse.data && testResponse.data.choices) {
      isApiConnected.value = true;
      addSystemMessage('API连接成功！我是您的营养健康顾问，很高兴为您服务。请问有什么饮食健康方面的问题需要咨询吗？');
    } else {
      throw new Error('API响应格式异常');
    }
  } catch (error) {
    console.error('API 连接失败:', error);
    isApiConnected.value = false;
    
    let errorMessage = 'API连接失败：';
    if (error.response) {
      // 服务器响应错误
      if (error.response.status === 401) {
        errorMessage += 'API密钥无效或已过期';
      } else if (error.response.status === 429) {
        errorMessage += '请求频率过高，请稍后重试';
      } else {
        errorMessage += `服务器错误 (${error.response.status})`;
      }
    } else if (error.request) {
      // 网络错误
      errorMessage += '网络连接失败，请检查网络设置';
    } else {
      // 其他错误
      errorMessage += error.message;
    }
    
    addSystemMessage(errorMessage);
  } finally {
    isLoading.value = false;
  }
};

// 发送消息
const sendMessage = async () => {
  const inputText = userInput.value.trim();
  if (!inputText || !isApiConnected.value || isLoading.value) return;

  const userMessage = {
    role: 'user',
    content: inputText,
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
        stream: false
      },
      {
        headers: {
          'Authorization': `Bearer ${API_KEY}`,
          'Content-Type': 'application/json',
        },
        timeout: 30000 // 30秒超时
      }
    );

    if (response.data && response.data.choices && response.data.choices.length > 0) {
      const aiResponse = response.data.choices[0].message.content;
      messages.value.push({
        role: 'assistant',
        content: aiResponse,
        timestamp: new Date(),
      });
    } else {
      throw new Error('API响应格式异常');
    }
  } catch (error) {
    console.error('API 调用失败:', error);
    
    let errorMessage = '抱歉，请求处理失败：';
    if (error.response) {
      if (error.response.status === 429) {
        errorMessage += '请求过于频繁，请稍后重试';
      } else {
        errorMessage += `服务器错误 (${error.response.status})`;
      }
    } else if (error.code === 'ECONNABORTED') {
      errorMessage += '请求超时，请稍后重试';
    } else {
      errorMessage += error.message;
    }
    
    addSystemMessage(errorMessage);
  } finally {
    isLoading.value = false;
    scrollToBottom();
    nextTick(() => {
      if (messageInput.value) {
        messageInput.value.focus();
      }
    });
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

// 清空对话
const clearChat = () => {
  messages.value = [];
  addSystemMessage('对话已清空，请问有什么营养健康问题需要咨询吗？');
};

// 格式化时间
const formatTime = (date) => {
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  });
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
  checkApiConnection();
  
  // 自动聚焦输入框
  nextTick(() => {
    if (messageInput.value) {
      messageInput.value.focus();
    }
  });
});
</script>

<style scoped>
/* 修改后的返回按钮样式 */
.back-button {
  position: absolute;
  left: 20px;
  top: 20px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  padding: 8px 12px;
  border-radius: 20px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
}

/* 调整返回文本样式 */
.back-text {
  display: block;
  line-height: 1;
}

/* 调整头部内容位置 */
.header-content {
  position: relative;
  max-width: 500px;
  margin: 0 auto;
  padding: 0 40px;
}
/* 原有的样式保持不变 */
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

/* 连接状态 */
.connection-status {
  max-width: 600px;
  margin: 10px auto;
  padding: 10px;
  border-radius: 5px;
  background: #0097ff;
  border: 1px solid #f5c6cb;
}

.connection-status.connected {
  background: #d4edda;
  border-color: #c3e6cb;
}

.status-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #dc3545;
}

.status-indicator.online {
  background: #28a745;
}

.status-text {
  flex: 1;
  margin-left: 10px;
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

.clear-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
}

.clear-btn:hover {
  background: #c82333;
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