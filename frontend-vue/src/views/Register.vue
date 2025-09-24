<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-arrow-left" @click="goBack"></i>
            <p>用户注册</p>
        </header>
        
        <div class="form-box">
            <div class="input-group">
                <div class="title">用户账号：</div>
                <div class="content">
                    <input
                        type="text"
                        v-model="user.userId"
                        placeholder="请输入用户账号"
                    />
                </div>
            </div>
            
            <div class="input-group">
                <div class="title">用户名：</div>
                <div class="content">
                    <input
                        type="text"
                        v-model="user.userName"
                        placeholder="请输入用户名"
                    />
                </div>
            </div>
            
            <div class="input-group">
                <div class="title">密码：</div>
                <div class="content">
                    <input
                        type="password"
                        v-model="user.password"
                        placeholder="请输入密码"
                    />
                </div>
            </div>
            
            <div class="input-group">
                <div class="title">确认密码：</div>
                <div class="content">
                    <input
                        type="password"
                        v-model="confirmPassword"
                        placeholder="请再次输入密码"
                    />
                </div>
            </div>
            
            <div class="input-group">
                <div class="title">性别：</div>
                <div class="content">
                    <div class="gender-options">
                        <label class="gender-option" :class="{ active: user.userSex === 1 }">
                            <input 
                                type="radio" 
                                v-model="user.userSex" 
                                :value="1"
                                hidden
                            />
                            <span>👨 男</span>
                        </label>
                        <label class="gender-option" :class="{ active: user.userSex === 0 }">
                            <input 
                                type="radio" 
                                v-model="user.userSex" 
                                :value="0"
                                hidden
                            />
                            <span>👩 女</span>
                        </label>
                    </div>
                </div>
            </div>
            
            <div class="btn-box">
                <button @click="register" :disabled="!canRegister">
                    注册
                </button>
            </div>
            
            <div class="link-box">
                <p @click="goToLogin">已有账号？点击登录</p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            user: {
                userId: '',        // 修正：使用 userId
                userName: '',      // 用户名
                password: '',
                userSex: 1        // 性别：1-男，0-女
            },
            confirmPassword: ''
        }
    },
    computed: {
        canRegister() {
            return this.user.userId && 
                   this.user.userName && 
                   this.user.password && 
                   this.confirmPassword &&
                   this.user.password === this.confirmPassword
        }
    },
    methods: {
        async register() {
            if (this.user.password !== this.confirmPassword) {
                alert('两次输入的密码不一致')
                return
            }
            
            if (this.user.password.length < 3) {
                alert('密码长度至少3位')
                return
            }
            
            try {
                console.log('=== 用户注册 ===')
                console.log('注册信息:', this.user)
                
                // ========== 修改：调用用户注册接口 ==========
                const response = await this.$axios.post('/user/saveUser', this.$qs.stringify({
                    userId: this.user.userId,
                    userName: this.user.userName,
                    password: this.user.password,
                    userSex: this.user.userSex
                }))
                
                console.log('注册响应:', response.data)
                
                if (response.data.code === 1) {
                    alert('注册成功！请使用新账号登录')
                    this.$router.push('/login')
                } else {
                    alert(response.data.msg || '注册失败')
                }
                // ========== 注册接口修改结束 ==========
                
            } catch (error) {
                console.error('注册错误:', error)
                alert('注册失败，请稍后重试')
            }
        },
        
        goToLogin() {
            this.$router.push('/login')
        },
        
        goBack() {
            this.$router.go(-1)
        }
    }
}
</script>

<style scoped>
    .wrapper {
        width: 100%;
        height: 100vh;
        background-color: #f5f5f5;
    }

    .wrapper header {
        width: 100%;
        height: 12vw;
        background-color: #0097FF;
        color: #fff;
        font-size: 4.8vw;
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;
    }

    .wrapper header .fa-arrow-left {
        position: absolute;
        left: 4vw;
        font-size: 5vw;
        cursor: pointer;
    }

    .form-box {
        padding: 6vw 4vw;
        max-height: calc(100vh - 12vw);
        overflow-y: auto;
    }

    .input-group {
        margin-bottom: 5vw;
    }

    .input-group .title {
        font-size: 3.5vw;
        color: #333;
        margin-bottom: 2vw;
        font-weight: 500;
    }

    .input-group input {
        width: 100%;
        height: 12vw;
        border: 1px solid #ddd;
        border-radius: 2vw;
        padding: 0 4vw;
        font-size: 4vw;
        box-sizing: border-box;
        transition: border-color 0.3s ease;
    }

    .input-group input:focus {
        border-color: #0097FF;
        outline: none;
        box-shadow: 0 0 0 2px rgba(0, 151, 255, 0.1);
    }

    /* 性别选择样式 */
    .gender-options {
        display: flex;
        gap: 4vw;
    }

    .gender-option {
        flex: 1;
        height: 12vw;
        border: 2px solid #ddd;
        border-radius: 2vw;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.3s ease;
        background-color: #fff;
    }

    .gender-option:hover {
        border-color: #0097FF;
        background-color: #f0f8ff;
    }

    .gender-option.active {
        border-color: #0097FF;
        background-color: #0097FF;
        color: #fff;
    }

    .gender-option span {
        font-size: 4vw;
        font-weight: 500;
    }

    .btn-box {
        margin: 6vw 0;
    }

    .btn-box button {
        width: 100%;
        height: 12vw;
        background-color: #0097FF;
        color: #fff;
        border: none;
        border-radius: 2vw;
        font-size: 4.5vw;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.3s ease;
    }

    .btn-box button:hover:not(:disabled) {
        background-color: #007ACC;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 151, 255, 0.3);
    }

    .btn-box button:disabled {
        background-color: #ccc;
        cursor: not-allowed;
        transform: none;
        box-shadow: none;
    }

    .link-box {
        text-align: center;
        margin-top: 4vw;
    }

    .link-box p {
        color: #0097FF;
        font-size: 3.5vw;
        cursor: pointer;
        text-decoration: underline;
        transition: color 0.3s ease;
    }

    .link-box p:hover {
        color: #007ACC;
    }

    /* 响应式设计 */
    @media (max-width: 480px) {
        .form-box {
            padding: 4vw 3vw;
        }
        
        .gender-options {
            gap: 3vw;
        }
        
        .input-group .title {
            font-size: 4vw;
        }
        
        .input-group input {
            font-size: 4.2vw;
        }
    }

    /* 表单验证状态 */
    .input-group.error input {
        border-color: #dc3545;
        background-color: #fff5f5;
    }

    .input-group.success input {
        border-color: #28a745;
        background-color: #f8fff8;
    }

    /* 动画效果 */
    .wrapper {
        animation: fadeIn 0.3s ease-in-out;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .input-group {
        animation: slideInUp 0.4s ease-out forwards;
        opacity: 0;
        transform: translateY(20px);
    }

    .input-group:nth-child(1) { animation-delay: 0.1s; }
    .input-group:nth-child(2) { animation-delay: 0.2s; }
    .input-group:nth-child(3) { animation-delay: 0.3s; }
    .input-group:nth-child(4) { animation-delay: 0.4s; }
    .input-group:nth-child(5) { animation-delay: 0.5s; }

    @keyframes slideInUp {
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .btn-box {
        animation: slideInUp 0.4s ease-out 0.6s forwards;
        opacity: 0;
        transform: translateY(20px);
    }

    .link-box {
        animation: slideInUp 0.4s ease-out 0.7s forwards;
        opacity: 0;
        transform: translateY(20px);
    }
</style>