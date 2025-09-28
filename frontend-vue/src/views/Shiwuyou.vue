<template>
  <div class="wrapper">
    <!-- 头部 -->
    <header>
      <i class="fa fa-arrow-left" @click="goBack"></i>
      <p>食无忧保障详情</p>
      <div class="header-right">
        <i class="fa fa-question-circle"></i>
      </div>
    </header>

    <div class="content">
      <!-- 保障卡片 -->
      <div class="insurance-card">
        <div class="card-header">
          <div class="card-icon">
            <i class="fa fa-shield-alt"></i>
          </div>
          <div class="card-title">
            <h2>食无忧保障</h2>
            <p>已生效 · 全方位食品安全保障</p>
          </div>
        </div>
        
        <div class="card-status">
          <div class="status-badge active">
            <i class="fa fa-check-circle"></i>
            <span>保障中</span>
          </div>
          <div class="valid-date">
            有效期至：2025年12月31日
          </div>
        </div>
      </div>

      <!-- 保障内容 -->
      <div class="insurance-content">
        <h3 class="section-title">保障内容</h3>
        
        <div class="benefit-item">
          <div class="benefit-icon">
            <i class="fa fa-heart"></i>
          </div>
          <div class="benefit-info">
            <h4>食品安全问题赔付</h4>
            <p>如遇食物变质、异物等问题，最高赔付1000元</p>
          </div>
        </div>

        <div class="benefit-item">
          <div class="benefit-icon">
            <i class="fa fa-truck"></i>
          </div>
          <div class="benefit-info">
            <h4>配送超时补偿</h4>
            <p>订单超时30分钟以上，可获得5元优惠券补偿</p>
          </div>
        </div>

        <div class="benefit-item">
          <div class="benefit-icon">
            <i class="fa fa-utensils"></i>
          </div>
          <div class="benefit-info">
            <h4>品质商家优选</h4>
            <p>优先推荐通过食品安全认证的优质商家</p>
          </div>
        </div>

        <div class="benefit-item">
          <div class="benefit-icon">
            <i class="fa fa-headset"></i>
          </div>
          <div class="benefit-info">
            <h4>专属客服通道</h4>
            <p>享受优先客服接待，问题处理更快速</p>
          </div>
        </div>
      </div>

      <!-- 使用说明 -->
      <div class="usage-guide">
        <h3 class="section-title">如何使用</h3>
        
        <div class="guide-step">
          <div class="step-number">1</div>
          <div class="step-content">
            <h4>发现问题</h4>
            <p>在订单完成后24小时内发现食品安全问题</p>
          </div>
        </div>

        <div class="guide-step">
          <div class="step-number">2</div>
          <div class="step-content">
            <h4>保留证据</h4>
            <p>拍摄清晰的食品问题照片或视频作为证据</p>
          </div>
        </div>

        <div class="guide-step">
          <div class="step-number">3</div>
          <div class="step-content">
            <h4>申请理赔</h4>
            <p>在订单详情页点击"申请食无忧理赔"</p>
          </div>
        </div>

        <div class="guide-step">
          <div class="step-number">4</div>
          <div class="step-content">
            <h4>审核赔付</h4>
            <p>客服将在24小时内审核并处理您的申请</p>
          </div>
        </div>
      </div>



      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="primary-btn" @click="goToOrders">
          查看我的订单
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.go(-1) // 返回上一页
}

const goToOrders = () => {
  router.push('/orderList')
}

const contactService = () => {
  // 这里可以集成客服系统
  alert('即将联系客服，请稍候...')
}
</script>

<style scoped>
.wrapper {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 头部样式 */
header {
  width: 100%;
  height: 12vw;
  background-color: #fff;
  color: #333;
  font-size: 4.8vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4vw;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

header .fa-arrow-left {
  font-size: 5vw;
  cursor: pointer;
  color: #666;
}

header p {
  font-weight: bold;
  color: #333;
}

.header-right i {
  font-size: 5vw;
  cursor: pointer;
  color: #666;
}

/* 内容区域 */
.content {
  margin-top: 12vw;
  padding: 4vw;
  padding-bottom: 20vw;
}

/* 保障卡片 */
.insurance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3vw;
  padding: 6vw 4vw;
  color: white;
  margin-bottom: 4vw;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 4vw;
}

.card-icon {
  width: 12vw;
  height: 12vw;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 3vw;
}

.card-icon i {
  font-size: 6vw;
  color: white;
}

.card-title h2 {
  font-size: 5vw;
  margin: 0 0 1vw 0;
  font-weight: bold;
}

.card-title p {
  font-size: 3.2vw;
  margin: 0;
  opacity: 0.9;
}

.card-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-badge {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.2);
  padding: 1.5vw 3vw;
  border-radius: 2vw;
  font-size: 3.2vw;
}

.status-badge i {
  margin-right: 1.5vw;
  font-size: 3.6vw;
}

.valid-date {
  font-size: 3.2vw;
  opacity: 0.9;
}

/* 章节标题 */
.section-title {
  font-size: 4.5vw;
  color: #333;
  margin: 6vw 0 4vw 0;
  font-weight: bold;
  padding-left: 2vw;
  border-left: 1vw solid #667eea;
}

/* 保障内容 */
.benefit-item {
  display: flex;
  align-items: center;
  padding: 4vw;
  background: white;
  border-radius: 2vw;
  margin-bottom: 3vw;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.benefit-icon {
  width: 10vw;
  height: 10vw;
  background: #667eea;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 4vw;
  flex-shrink: 0;
}

.benefit-icon i {
  font-size: 4.5vw;
  color: white;
}

.benefit-info h4 {
  font-size: 4vw;
  color: #333;
  margin: 0 0 1vw 0;
  font-weight: bold;
}

.benefit-info p {
  font-size: 3.2vw;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

/* 使用指南 */
.guide-step {
  display: flex;
  align-items: flex-start;
  padding: 3vw;
  background: white;
  border-radius: 2vw;
  margin-bottom: 3vw;
}

.step-number {
  width: 8vw;
  height: 8vw;
  background: #667eea;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4vw;
  font-weight: bold;
  margin-right: 4vw;
  flex-shrink: 0;
}

.step-content h4 {
  font-size: 3.8vw;
  color: #333;
  margin: 0 0 1vw 0;
  font-weight: bold;
}

.step-content p {
  font-size: 3.2vw;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

/* 常见问题 */
.faq-item {
  background: white;
  border-radius: 2vw;
  margin-bottom: 3vw;
  overflow: hidden;
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4vw;
  cursor: pointer;
  background: #f8f9fa;
}

.faq-question span {
  font-size: 3.8vw;
  color: #333;
  font-weight: 500;
}

.faq-question i {
  font-size: 3.5vw;
  color: #999;
}

.faq-answer {
  padding: 0 4vw;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}

.faq-answer p {
  font-size: 3.2vw;
  color: #666;
  line-height: 1.5;
  padding: 2vw 0 4vw 0;
}

.faq-item.active .faq-answer {
  max-height: 20vw;
}

/* 操作按钮 */
.action-buttons {
  margin-top: 8vw;
  display: flex;
  flex-direction: column;
  gap: 3vw;
}

.primary-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 4vw;
  border-radius: 2vw;
  font-size: 4vw;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s;
}

.primary-btn:hover {
  background: #5a6fd8;
}

.secondary-btn {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
  padding: 4vw;
  border-radius: 2vw;
  font-size: 4vw;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}

.secondary-btn:hover {
  background: #f0f3ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content {
    padding: 3vw;
  }
  
  .insurance-card {
    padding: 5vw 4vw;
  }
  
  .benefit-item, .guide-step, .faq-question {
    padding: 3vw;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.benefit-item, .guide-step, .faq-item {
  animation: fadeIn 0.4s ease-out;
}
</style>