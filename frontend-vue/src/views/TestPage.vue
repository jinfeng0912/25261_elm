<template>
  <div class="nutrition-expert-container">
    <!-- 顶部标题栏 -->
     <header>
        <div class="header-left">
            <p>智能数据分析</p>
        </div>
        <div class="header-right">
            <span>{{ adminName }}</span>
            <button @click="logout">退出</button>
        </div>
    </header>

    <!-- 主功能区 -->
    <div class="main-container">
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button @click="fetchData" class="action-button fetch-btn">
          <i class="fas fa-cloud-download-alt"></i> 获取数据
        </button>
        <button 
          @click="analyzeData" 
          class="action-button analyze-btn"
          :disabled="!jsonData || isLoading"
        >
          <i class="fas fa-chart-line"></i> 分析数据
        </button>
      </div>

      <!-- 调试信息 -->
      <div v-if="debugInfo" class="debug-info">
        <div v-for="(info, index) in debugInfo" :key="index" class="debug-message">
          <span class="debug-time">{{ info.time }}</span>
          <span class="debug-status" :class="info.status">{{ info.message }}</span>
        </div>
      </div>

      <!-- 数据显示区 -->
      <div class="data-display">
        <div v-if="jsonData" class="data-section">
          <h3><i class="fas fa-code"></i> 原始JSON数据：</h3>
          <pre>{{ jsonData }}</pre>
        </div>
        
        <div v-if="analysisResult" class="data-section">
          <h3><i class="fas fa-lightbulb"></i> AI分析结果：</h3>
          <p>{{ analysisResult }}</p>
        </div>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="footer">
      <p>TJU | Powered by Deepseek-v3.1</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

// 响应式数据
const jsonData = ref(null);
const analysisResult = ref(null);
const isLoading = ref(false);
const debugInfo = ref([]);

// 添加调试信息
const addDebugInfo = (message, status = 'info') => {
  const now = new Date();
  const timeString = `${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;
  debugInfo.value.push({
    time: timeString,
    message,
    status
  });
};

// 获取数据
const fetchData = async () => {
  isLoading.value = true;
  debugInfo.value = []; // 清除旧的调试信息
  addDebugInfo('开始获取数据...');
  
  try {
    addDebugInfo('正在连接数据服务器...');
    const response = await axios.get('/api/businesses');
    addDebugInfo('数据服务器连接成功', 'success');
    
    jsonData.value = response.data;
    analysisResult.value = null;
    addDebugInfo('数据获取完成', 'success');
  } catch (error) {
    console.error('获取数据失败:', error);
    addDebugInfo('数据获取失败: ' + error.message, 'error');
    jsonData.value = { error: '获取数据失败' };
  } finally {
    isLoading.value = false;
  }
};

// 分析数据
const analyzeData = async () => {
  if (!jsonData.value) return;
  
  isLoading.value = true;
  debugInfo.value = []; // 清除旧的调试信息
  addDebugInfo('开始分析数据...');
  
  try {
    addDebugInfo('正在连接AI分析服务...');
    
    // 模拟API调用过程的不同阶段
    setTimeout(() => {
      addDebugInfo('AI服务已连接', 'success');
      addDebugInfo('正在发送数据...');
    }, 500);
    
    setTimeout(() => {
      addDebugInfo('数据发送完成');
      addDebugInfo('等待AI分析结果...');
    }, 1000);

    // 这里假设有一个AI分析API
    const response = await axios.post(
      'https://api.deepseek.com/v1/chat/completions',
      {
        model: 'deepseek-chat',
        messages: [
          {
            role: 'system',
            content: '你是一位数据分析师，请根据提供的数据进行分析并给出专业见解 千万要输出纯文本 不能使用markdown格式如* 用一些解释性的语言 而不是概念性过强的语言 你的回答应该面向整个外卖平台的经营者 并为他们给出经营建议 不使用长句 多分段 你的建议不要涉及信息缺失 指出当前平台的短板，比如缺乏什么类型的店铺 然后给出建议'
          },
          {
            role: 'user',
            content: `请分析以下数据：${JSON.stringify(jsonData.value)} `
          }
        ],
        temperature: 0.7,
        max_tokens: 1000,
      },
      {
        headers: {
          'Authorization': 'Bearer sk-ea65bb27c3a642718ecbea5ea3980779',
          'Content-Type': 'application/json',
        },
      }
    );

    addDebugInfo('收到AI分析结果', 'success');
    analysisResult.value = response.data.choices[0].message.content;
  } catch (error) {
    console.error('分析数据失败:', error);
    addDebugInfo('分析数据失败: ' + error.message, 'error');
    analysisResult.value = '分析数据时出错';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.nutrition-expert-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
  color: #333;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 151, 255, 0.1);
}

.header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 151, 255, 0.1);
}

.header h1 {
  color: #0097FF;
  margin: 15px 0 5px;
  font-size: 28px;
  font-weight: 600;
}

.header-icon {
  color: #0097FF;
  font-size: 32px;
}

.main-container {
  background: #fff;
  border-radius: 8px;
  padding: 25px;
  border: 1px solid rgba(0, 151, 255, 0.2);
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.action-button {
  padding: 12px 25px;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-button i {
  font-size: 14px;
}

.fetch-btn {
  background: #0097FF;
}

.fetch-btn:hover {
  background: #007acc;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 151, 255, 0.2);
}

.analyze-btn {
  background: #4CAF50;
}

.analyze-btn:hover:not(:disabled) {
  background: #3d8b40;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(76, 175, 80, 0.2);
}

.action-button:disabled {
  background: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}

.debug-info {
  margin: 20px 0;
  padding: 15px;
  background: #f8fafc;
  border-radius: 6px;
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid rgba(0, 151, 255, 0.1);
}

.debug-message {
  margin: 8px 0;
  font-size: 14px;
  display: flex;
  align-items: center;
  line-height: 1.5;
}

.debug-time {
  color: #666;
  margin-right: 12px;
  font-family: monospace;
  min-width: 70px;
}

.debug-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.debug-status.info {
  color: #0097FF;
  background-color: rgba(0, 151, 255, 0.1);
}

.debug-status.success {
  color: #4CAF50;
  background-color: rgba(76, 175, 80, 0.1);
}

.debug-status.error {
  color: #f44336;
  background-color: rgba(244, 67, 54, 0.1);
}

.data-display {
  margin-top: 25px;
}

.data-section {
  background: #f8fafc;
  padding: 18px;
  border-radius: 6px;
  margin-bottom: 20px;
  border: 1px solid rgba(0, 151, 255, 0.1);
}

.data-section h3 {
  margin-top: 0;
  color: #0097FF;
  border-bottom: 1px solid rgba(0, 151, 255, 0.1);
  padding-bottom: 12px;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  background: #fff;
  padding: 12px;
  border-radius: 6px;
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid rgba(0, 151, 255, 0.1);
  font-family: 'Consolas', 'Monaco', monospace;
  line-height: 1.5;
}

.footer {
  text-align: center;
  margin-top: 40px;
  color: #777;
  font-size: 14px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 151, 255, 0.1);
}

/* 加载动画 */
@keyframes blink {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}

.loading-text {
  animation: blink 1.5s infinite;
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

</style>