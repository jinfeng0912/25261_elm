<template>
	<div class="wrapper">
		<!-- header部分 -->
		<header>
			<p>用户登录</p>
		</header>
		
		<!-- 用户类型选择 -->
		<div class="user-type-selector">
			<div class="selector-label">登录类型：</div>
			<div class="selector-options">
				<label class="option-item">
					<input 
						type="radio" 
						v-model="userType" 
						value="CUSTOMER"
						name="userType"
					>
					<span class="option-text">普通用户</span>
				</label>
				<label class="option-item">
					<input 
						type="radio" 
						v-model="userType" 
						value="BUSINESS"
						name="userType"
					>
					<span class="option-text">商家用户</span>
				</label>
			</div>
		</div>
		
		<!-- 表单部分 -->
		<ul class="form-box">
			<li>
				<div class="title">
					用户名：
				</div>
				<div class="content">
					<input 
						type="text" 
						v-model="loginId" 
						placeholder="请输入用户名"
					>
				</div>
			</li>
			<li>
				<div class="title">
					密码：
				</div>
				<div class="content">
					<input type="password" v-model="password" placeholder="请输入密码">
				</div>
			</li>
		</ul>
		<div class="button-login">
			<button @click="login">登录</button>
		</div>
		<div class="button-register">
			<button @click="register">{{ userType === 'BUSINESS' ? '商家注册' : '去注册' }}</button>
		</div>

		<!-- 底部菜单部分 -->
		<Footer></Footer>
	</div>
</template>
<script>
import Footer from '../components/Footer.vue';
import axios from 'axios';

export default {
  name: 'Login',
  components: {
    Footer
  },
  data() {
    return {
      loginId: '',
      password: '',
      userType: 'CUSTOMER',  // 用户类型：CUSTOMER 或 BUSINESS
      loading: false,
      error: null
    }
  },
  mounted() {
    // 支持从路由参数初始化类型
    const type = (this.$route.query?.type || '').toString().toUpperCase();
    if (type === 'ADMIN' || type === 'BUSINESS' || type === 'CUSTOMER') {
      this.userType = type;
    }
  },
  methods: {
    login() {
      if (!this.loginId.trim()) {
        alert('用户名不能为空！');
        return;
      }
      if (!this.password) {
        alert('密码不能为空！');
        return;
      }
      
      this.unifiedLogin();
    },
    
    async unifiedLogin() {
      this.loading = true;
      this.error = null;
      
      try {
        // 清理旧令牌
        localStorage.removeItem('token');
        
        // 1. 获取Token
        const authResponse = await axios.post('/api/auth', 
          {
            username: this.loginId.trim(),
            password: this.password,
            rememberMe: false
          },
          {
            headers: { 'Content-Type': 'application/json' }
          }
        );

        const token = authResponse.data?.id_token || authResponse.data?.token;
        if (!token) {
          throw new Error('未获取到有效令牌');
        }
        localStorage.setItem('token', token);

        // 2. 获取用户信息
        const userResponse = await axios.get('/api/user', {
          headers: { Authorization: `Bearer ${token}` }
        });
        
        console.log('用户信息响应码:', userResponse.status);
        const user = userResponse.data;
        if (!user) {
          throw new Error('无效的用户数据');
        }

        // 3. 验证用户类型
        this.validateUserType(user.userType);

        // 4. 存储用户数据并跳转
        this.handleLoginSuccess(user);

      } catch (error) {
        this.handleLoginError(error);
      } finally {
        this.loading = false;
      }
    },
    
    validateUserType(actualType) {
      actualType = 'ADMIN'
      if (actualType !== 'ADMIN' && actualType !== this.userType) {
        localStorage.removeItem('token');
        throw new Error(`请使用${actualType}类型的登录入口`);
      }
    },

    handleLoginSuccess(user) {
      const storageKey = "ADMIN";
      const storageData = {
        ...user,
        timestamp: new Date().getTime()
      };

      // 存储用户数据
      localStorage.setItem(storageKey, JSON.stringify(storageData));
      localStorage.setItem('currentRole', user.userType);

      // 跳转逻辑
      const routes = {
        'ADMIN': '/adminPanel',
        'BUSINESS': '/businessManager',
        'CUSTOMER': this.$route.query.redirect 
                  ? decodeURIComponent(this.$route.query.redirect) 
                  : '/'
      };

      this.$router.push(routes[user.userType] || '/');
      alert(`${this.getUserTypeName(user.userType)}登录成功！`);
    },

    handleLoginError(error) {
      console.error('完整错误对象:', error);
      
      let errorMessage = '登录失败，请稍后重试';
      if (error.response) {
        switch(error.response.status) {
          case 401:
            errorMessage = '用户名或密码错误';
            break;
          case 403:
            errorMessage = '没有访问权限';
            break;
          case 500:
            errorMessage = '服务器内部错误';
            break;
        }
      } else if (error.request) {
        errorMessage = '网络连接异常，请检查网络';
      } else if (error.message) {
        errorMessage = error.message;
      }

      this.error = errorMessage;
      alert(errorMessage);
    },

    getUserTypeName(type) {
      const names = {
        'ADMIN': '管理员',
        'BUSINESS': '商家',
        'CUSTOMER': '用户'
      };
      return names[type] || '用户';
    },

    register() {
      const route = this.userType === 'BUSINESS' 
                  ? 'businessRegister' 
                  : 'register';
      this.$router.push({ path: route });
    }
  }
}
</script>
<style scoped>
	/****************** 总容器 ******************/
	.wrapper {
		width: 100%;
		height: 100vh;
		overflow: hidden;
	}
		/****************** header部分 ******************/
	.wrapper header {
		width: 100%;
		height: 12vw;
		background-color: #0097FF;
		color: #fff;
		font-size: 4.8vw;
		position: fixed;
		left: 0;
		top: 0;
		z-index: 1000;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	/****************** 用户类型选择部分 ******************/
	.user-type-selector {
		width: 100%;
		margin-top: 12vw;
		padding: 3vw 3vw;
		background-color: #f8f8f8;
		border-bottom: 1px solid #ddd;
	}

	.selector-label {
		font-size: 3.5vw;
		font-weight: 700;
		color: #666;
		margin-bottom: 2vw;
	}

	.selector-options {
		display: flex;
		gap: 6vw;
	}

	.option-item {
		display: flex;
		align-items: center;
		cursor: pointer;
		font-size: 3.2vw;
		color: #666;
	}

	.option-item input[type="radio"] {
		margin-right: 2vw;
		width: 4vw;
		height: 4vw;
		accent-color: #0097FF;
	}

	.option-item input[type="radio"]:checked + .option-text {
		color: #0097FF;
		font-weight: 700;
	}

	.option-text {
		transition: all 0.3s ease;
	}

	/****************** 表单部分 ******************/
	.wrapper .form-box {
		width: 100%;
	}

	.wrapper .form-box li {
		box-sizing: border-box;
		padding: 3vw 3vw 0 3vw;
		display: flex;
		align-items: center;
	}

	.wrapper .form-box li .title {
		flex: 0 0 18vw;
		font-size: 3vw;
		font-weight: 700;
		color: #666;
	}

	.wrapper .form-box li .content {
		flex: 1;
	}

	.wrapper .form-box li .content input {
		box-sizing: border-box;
		width: 100%;
		height: 8vw;
		border: 1px solid #DDD;
		font-size: 3vw;
		padding: 0 2vw;
	}

	/****************** 按钮部分 ******************/
	.wrapper .button-login {
		box-sizing: border-box;
		padding: 2vw 3vw 0 3vw;
	}

	.wrapper .button-login button {
		width: 100%;
		height: 10vw;
		background-color: #0097FF;
		border: none;
		font-size: 3.8vw;
		font-weight: 700;
		color: #fff;
		border-radius: 4px;
	}

	.wrapper .button-register {
		box-sizing: border-box;
		padding: 2vw 3vw 0 3vw;
	}

	.wrapper .button-register button {
		width: 100%;
		height: 10vw;
		background-color: #fff;
		border: 1px solid #0097FF;
		font-size: 3.8vw;
		font-weight: 700;
		color: #0097FF;
		border-radius: 4px;
	}
</style>