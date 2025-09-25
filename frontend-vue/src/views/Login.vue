<template>
    <div class="wrapper">
        <!-- header -->
        <header>
            <p>登录</p>
        </header>

        <!-- 身份选择模态框 -->
        <div class="modal-overlay" v-if="showRoleModal" @click="closeRoleModal">
            <div class="role-modal" @click.stop>
                <h3>请选择登录身份</h3>
                <div class="role-options">
                    <div class="role-option" @click="selectRole('user')">
                        <div class="role-icon">👤</div>
                        <div class="role-info">
                            <h4>用户登录</h4>
                            <p>点餐、下单、查看订单</p>
                        </div>
                    </div>
                    <div class="role-option" @click="selectRole('admin')">
                        <div class="role-icon">👨‍💼</div>
                        <div class="role-info">
                            <h4>管理员登录</h4>
                            <p>商家管理、食品管理</p>
                        </div>
                    </div>
                    <div class="role-option" @click="selectRole('business')">
                        <div class="role-icon">🏪</div>
                        <div class="role-info">
                            <h4>商家登录</h4>
                            <p>管理店铺、查看订单</p>
                        </div>
                    </div>
                    <div class="role-option" @click="selectRole('developer')">
                        <div class="role-icon">👨‍💻</div>
                        <div class="role-info">
                            <h4>开发者登录</h4>
                            <p>系统维护、功能开发</p>
                        </div>
                    </div>
                </div>
                <button class="close-btn" @click="closeRoleModal">取消</button>
            </div>
        </div>

        <!-- 登录表单 -->
        <div class="login-form">
            <!-- 当前选择的身份显示 -->
            <div class="current-role" @click="showRoleModal = true">
                <span v-if="currentRole === 'user'">👤 用户登录</span>
                <span v-if="currentRole === 'admin'">👨‍💼 管理员登录</span>
                <span v-if="currentRole === 'business'">🏪 商家登录</span>
                <span v-if="currentRole === 'developer'">👨‍💻 开发者登录</span>
                <i class="fa fa-caret-down"></i>
            </div>

            <!-- 账号输入 -->
            <div class="login-form-username">
                <input 
                    type="text" 
                    v-model="user.userName" 
                    :placeholder="getUsernamePlaceholder()"
                />
            </div>

            <!-- 密码输入 -->
            <div class="login-form-password">
                <input 
                    type="password" 
                    v-model="user.password" 
                    placeholder="请输入密码"
                />
            </div>

            <!-- 登录按钮 -->
            <div class="btn-box">
                <button @click="login">登录</button>
            </div>

            <!-- 注册链接 -->
            <div class="register-link">
                <p @click="goToRegister">
                    {{ getRegisterText() }}
                </p>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            showRoleModal: false,
            currentRole: 'user',
            user: {
                userName: '',
                password: ''
            },
            loading: false,
            error: null,
            apiResponse: null
        }
    },
    mounted() {
        this.showRoleModal = true
    },
    methods: {
        getUsernamePlaceholder() {
            switch(this.currentRole) {
                case 'user': return '请输入用户名';
                case 'admin': return '请输入管理员账号';
                case 'business': return '请输入商家账号';
                default: return '请输入账号';
            }
        },
        
        getRegisterText() {
            switch(this.currentRole) {
                case 'user': return '还没有账号？点击注册';
                case 'admin': return '注册管理员账号';
                case 'business': return '注册商家账号';
                default: return '注册账号';
            }
        },
        
        selectRole(role) {
            console.log('选择登录身份:', role)
            this.currentRole = role
            this.showRoleModal = false
            this.user.userName = ''
            this.user.password = ''
        },

        closeRoleModal() {
            this.showRoleModal = false
        },

        async unifiedLogin(userId) {
            try {
                // 清理旧token
                localStorage.removeItem('token');
                
                // 发起认证请求
                const response = await axios.post('/api/auth', {
                    username: this.user.userName,
                    password: this.user.password,
                }, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                const token = response.data?.id_token;
                console.log('id_token : ' + token);
                console.log('当前登录身份 : ' +  this.currentRole);
                if (!token) {
                    throw new Error('未获取到有效令牌');
                }

                // 存储token
                localStorage.setItem('token', token);
                
                // 获取用户信息
                const userInfo = await this.fetchUserInfo(token);

                // 存储用户信息和登录状态
                localStorage.setItem('userInfo', JSON.stringify(userInfo));
                localStorage.setItem('loginStatus', 'true');
                localStorage.setItem('role', this.currentRole);
                localStorage.setItem('userId', "not knowing");
                localStorage.setItem('token', token);
                localStorage.setItem('userId', userId);
                
                console.log("已保存token" + localStorage.getItem('token'));

                // 根据角色跳转
                this.redirectByRole(this.currentRole);
                
            } catch (error) {
                console.error('登录错误:', error);
                this.handleError(error);
                alert(error.response?.data?.message || error.message || '登录失败');
            }
        },

        async fetchUserInfo(token) {
            const response = await axios.get('/api/user', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            return response.data;
        },

        redirectByRole(role) {
            const routes = {
                'admin': '/adminPanel',
                'business': '/businessPanel',
                'developer': '/testConn',
                'user': '/'
            };
            
            const redirectPath = routes[role] || '/';
            this.$router.push(redirectPath);
            alert(`${this.getRoleName(role)}登录成功！`);
        },

        getRoleName(role) {
            const names = {
                'admin': '管理员',
                'business': '商家',
                'developer': '开发者',
                'user': '用户'
            };
            return names[role] || '用户';
        },

        handleError(err) {
            this.error = `Error ${err.response?.status}: ${
                err.response?.data?.message || err.message
            }`;
            
            if (err.response) {
                console.error('API Error Status:', err.response.status);
                console.error('API Error Data:', err.response.data);
                this.apiResponse = err.response;
            } else {
                console.error('API Error:', err.message);
            }
        },

        async login() {
            if (!this.user.userName || !this.user.password) {
                alert('请输入完整的登录信息');
                return;
            }

            this.loading = true;
            try {
                await this.unifiedLogin(this.user.userName);
            } finally {
                this.loading = false;
            }
        },

        goToRegister() {
            const routes = {
                'user': '/register',
                'admin': '/adminRegister',
                'business': '/businessRegister',
                'developer': '/testConn'
            };
            this.$router.push(routes[this.currentRole] || '/register');
        }
    }
}
</script>

<style scoped>
    /* 基础样式 */
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
        justify-content: center;
        align-items: center;
    }

    /* 身份选择模态框 */
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

    .role-modal {
        width: 80%;
        max-width: 400px;
        background-color: #fff;
        border-radius: 3vw;
        padding: 6vw;
        text-align: center;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    }

    .role-modal h3 {
        font-size: 4.5vw;
        color: #333;
        margin-bottom: 5vw;
    }

    .role-options {
        display: flex;
        flex-direction: column;
        gap: 3vw;
        margin-bottom: 5vw;
    }

    .role-option {
        display: flex;
        align-items: center;
        padding: 4vw;
        border: 2px solid #e0e0e0;
        border-radius: 2vw;
        cursor: pointer;
        transition: all 0.3s ease;
    }

    .role-option:hover {
        border-color: #0097FF;
        background-color: #f0f8ff;
    }

    .role-icon {
        font-size: 8vw;
        margin-right: 4vw;
    }

    .role-info {
        text-align: left;
        flex: 1;
    }

    .role-info h4 {
        font-size: 4vw;
        color: #333;
        margin-bottom: 1vw;
    }

    .role-info p {
        font-size: 3vw;
        color: #666;
    }

    .close-btn {
        width: 100%;
        height: 10vw;
        background-color: #ddd;
        color: #666;
        border: none;
        border-radius: 2vw;
        font-size: 3.5vw;
        cursor: pointer;
    }

    /* 当前身份显示 */
    .current-role {
        width: 80%;
        margin: 5vw auto 4vw;
        padding: 3vw;
        background-color: #fff;
        border: 2px solid #0097FF;
        border-radius: 2vw;
        display: flex;
        justify-content: space-between;
        align-items: center;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .current-role:hover {
        background-color: #f0f8ff;
    }

    .current-role span {
        font-size: 4vw;
        color: #0097FF;
        font-weight: bold;
    }

    .current-role i {
        color: #0097FF;
        font-size: 3.5vw;
    }

    /* 登录表单 */
    .login-form {
        width: 100%;
        padding: 2vw 0;
    }

    .login-form-username,
    .login-form-password {
        width: 80%;
        margin: 4vw auto;
    }

    .login-form-username input,
    .login-form-password input {
        width: 100%;
        height: 12vw;
        border: 1px solid #ddd;
        border-radius: 2vw;
        padding: 0 4vw;
        font-size: 4vw;
        box-sizing: border-box;
    }

    .login-form-username input:focus,
    .login-form-password input:focus {
        border-color: #0097FF;
        outline: none;
    }

    /* 按钮 */
    .btn-box {
        width: 80%;
        margin: 6vw auto;
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

    .btn-box button:hover {
        background-color: #007ACC;
    }

    /* 注册链接 */
    .register-link {
        text-align: center;
        margin-top: 4vw;
    }

    .register-link p {
        color: #0097FF;
        font-size: 3.5vw;
        cursor: pointer;
        text-decoration: underline;
    }

    .register-link p:hover {
        color: #007ACC;
    }
</style>