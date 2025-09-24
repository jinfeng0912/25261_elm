<template>
  <div class="user-edit-container">
    <!-- 顶部导航栏 -->
    <header>
      <button class="back-button" @click="goBack">
        <i class="fa fa-arrow-left"></i>
        <span>返回</span>
      </button>
      <h2>修改账户信息</h2>
    </header>

    <!-- 主要内容区域 -->
    <div class="edit-content">
      <!-- 表单编辑区域 -->
      <div class="form-section">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="form.userName" 
            placeholder="请输入新用户名"
          >
        </div>

        <div class="form-group">
          <label for="currentPassword">当前密码</label>
          <input 
            type="password" 
            id="currentPassword" 
            v-model="form.currentPassword" 
            placeholder="请输入当前密码"
          >
        </div>

        <div class="form-group">
          <label for="newPassword">新密码</label>
          <input 
            type="password" 
            id="newPassword" 
            v-model="form.newPassword" 
            placeholder="请输入新密码"
          >
        </div>

        <div class="form-group">
          <label for="confirmPassword">确认新密码</label>
          <input 
            type="password" 
            id="confirmPassword" 
            v-model="form.confirmPassword" 
            placeholder="请再次输入新密码"
          >
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="save-btn" @click="handleSubmit" :disabled="isSubmitting">
          {{ isSubmitting ? '保存中...' : '保存修改' }}
        </button>
        <button class="cancel-btn" @click="goBack">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const route = useRoute();

// 表单数据
const form = ref({
  userName: '',
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const isSubmitting = ref(false);

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 提交表单
const handleSubmit = async () => {
  // 验证表单
  if (!form.value.userName) {
    alert('用户名不能为空');
    return;
  }
  
  if (!form.value.currentPassword) {
    alert('当前密码不能为空');
    return;
  }
  
  if (!form.value.newPassword) {
    alert('新密码不能为空');
    return;
  }
  
  if (form.value.newPassword !== form.value.confirmPassword) {
    alert('两次输入的新密码不一致，请重新输入');
    return;
  }

  try {
    isSubmitting.value = true;
    
    // 准备请求参数
    const params = new URLSearchParams();
    params.append('userId', route.query.userId); // 从路由参数获取userId
    params.append('password', form.value.userName);
    params.append('userName', form.value.newPassword);
    
    // 调用后端接口
    const response = await axios.post(
      'UserController/updateUser', 
      params,
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    );

    if (response.data.success) {
      alert('账户信息更新成功');
      router.push('/user'); // 跳转到用户信息页
    } else {
      throw new Error(response.data.message || '');
    }
  } catch (error) {
    console.error('更新用户信息失败:', error);
    alert(`更新成功 ${error.response?.data?.message || error.message}`);
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
/* 原有样式保持不变 */
.user-edit-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  min-height: 100vh;
  font-family: Arial, sans-serif;
}

header {
  display: flex;
  align-items: center;
  padding: 15px 0;
  margin-bottom: 20px;
  position: relative;
  border-bottom: 1px solid #f0f0f0;
}

header h2 {
  margin: 0 auto;
  font-size: 1.2rem;
  color: #333;
}

.back-button {
  position: absolute;
  left: 0;
  background: none;
  border: none;
  display: flex;
  align-items: center;
  color: #0097ff;
  font-size: 0.9rem;
  cursor: pointer;
  padding: 5px;
}

.back-button i {
  margin-right: 5px;
}

.edit-content {
  padding: 0 15px;
}

.form-section {
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.save-btn, .cancel-btn {
  flex: 1;
  padding: 12px;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
}

.save-btn {
  background-color: #0097ff;
  color: white;
  border: none;
}

.save-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #666;
  border: 1px solid #ddd;
}

@media (max-width: 480px) {
  .user-edit-container {
    padding: 15px;
  }
}
</style>