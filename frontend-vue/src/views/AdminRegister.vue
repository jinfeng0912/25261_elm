<template>
    <div class="wrapper">
        <header>
            <i class="fa fa-arrow-left" @click="goBack"></i>
            <p>管理员注册</p>
        </header>
        
        <div class="form-box">
            <div class="input-group">
                <div class="title">管理员账号：</div>
                <div class="content">
                    <input
                        type="text"
                        v-model="admin.adminId"
                        placeholder="请输入管理员账号"
                    />
                </div>
            </div>
            <div class="input-group">
                <div class="title">密码：</div>
                <div class="content">
                    <input
                        type="password"
                        v-model="admin.password"
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
            admin: {
                adminId: '',       // 修正：使用 adminId
                password: ''
            },
            confirmPassword: ''
        }
    },
    computed: {
        canRegister() {
            return this.admin.adminId && 
                   this.admin.password && 
                   this.confirmPassword &&
                   this.admin.password === this.confirmPassword
        }
    },
    methods: {
        async register() {
            if (this.admin.password !== this.confirmPassword) {
                alert('两次输入的密码不一致')
                return
            }
            
            if (this.admin.password.length < 3) {
                alert('密码长度至少3位')
                return
            }
            
            try {
                console.log('=== 管理员注册 ===')
                console.log('注册信息:', this.admin)
                
                // ========== 修改：调用管理员注册接口 ==========
                const response = await this.$axios.post('/admin/saveAdmin', this.$qs.stringify({
                    adminId: this.admin.adminId,
                    password: this.admin.password
                }))
                
                console.log('注册响应:', response.data)
                
                if (response.data.code === 1) {
                    alert('管理员注册成功！请使用新账号登录')
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
    }

    .input-group {
        margin-bottom: 5vw;
    }

    .input-group .title {
        font-size: 3.5vw;
        color: #333;
        margin-bottom: 2vw;
    }

    .input-group input {
        width: 100%;
        height: 12vw;
        border: 1px solid #ddd;
        border-radius: 2vw;
        padding: 0 4vw;
        font-size: 4vw;
        box-sizing: border-box;
    }

    .input-group input:focus {
        border-color: #0097FF;
        outline: none;
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
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .btn-box button:hover:not(:disabled) {
        background-color: #007ACC;
    }

    .btn-box button:disabled {
        background-color: #ccc;
        cursor: not-allowed;
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
    }
</style>