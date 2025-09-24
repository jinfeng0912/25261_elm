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
export default {
    data() {
        return {
            showRoleModal: false,
            currentRole: 'user',
            user: {
                userName: '',
                password: ''
            }
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

        async login() {
            if (!this.user.userName || !this.user.password) {
                alert('请输入完整的登录信息')
                return
            }

            // 特例检查1：用户名和密码都为ysq时直接登录管理员
            if (this.user.userName === 'ysq' && this.user.password === 'ysq') {
                try {
                    console.log('=== 使用特例登录管理员 ===')
                    const mockAdminData = {
                        adminId: 'ysq',
                        adminName: '特殊管理员',
                        role: 'superAdmin'
                    }
                    
                    this.$setSessionStorage('admin', mockAdminData)
                    alert('特殊管理员登录成功！')
                    this.$router.push('/adminPanel')
                    return
                } catch (error) {
                    console.error('特例登录错误:', error)
                    alert('特例登录失败')
                    return
                }
            }

            // 特例检查2：用户名和密码都为tju时直接登录商家
            if (this.user.userName === 'tju' && this.user.password === 'tju') {
                try {
                    console.log('=== 使用特例登录商家 ===')
                    const mockAdminData = {
                        adminId: 'tju',
                        adminName: '特殊商家',
                        role: 'superBusiness'
                    }   
                    
                    this.$setSessionStorage('admin', mockAdminData)
                    alert('特殊商家登录成功！')
                    this.$router.push('/businessPanel')
                    return
                } catch (error) {
                    console.error('特例登录错误:', error)
                    alert('特例登录失败')
                    return
                }
            }


            // 特例检查：用户名和密码都为ligang时直接登录用户
            if (this.user.userName === 'ligang' && this.user.password === 'ligang') {
                try {
                    console.log('=== 使用特例登录用户 ===')
                    const mockUserData = {
                        userId: 'ligang',
                        userName: '李刚',
                        role: 'vipUser'
                    }
                    
                    this.$setSessionStorage('user', mockUserData)
                    alert('VIP用户登录成功！')
                    this.$router.push('/')
                    return
                } catch (error) {
                    console.error('特例登录错误:', error)
                    alert('特例登录失败')
                    return
                }
            }

            // 特例检查4：用户名和密码都为dev时直接登录开发者
            if (this.user.userName === 'dev' && this.user.password === 'dev') {
                try {
                    console.log('=== 使用特例登录开发者 ===')
                    const mockDeveloperData = {
                        developerId: 'dev',
                        developerName: '测试开发者',
                        role: 'developer'
                    }
                    
                    this.$setSessionStorage('developer', mockDeveloperData)
                    alert('开发者登录成功！')
                    this.$router.push('/testConn')
                    return
                } catch (error) {
                    console.error('特例登录错误:', error)
                    alert('特例登录失败')
                    return
                }
            }

            try {
                console.log('=== 开始登录 ===')
                console.log('登录身份:', this.currentRole)
                console.log('登录信息:', this.user)

                let response
                
                if (this.currentRole === 'user') {
                    // 用户登录接口
                    response = await this.$axios.post('/user/getUserByIdByPass', this.$qs.stringify({
                        userId: this.user.userName,
                        password: this.user.password
                    }))
                    
                    console.log('用户登录响应:', response.data)
                    
                    if (response.data.code === 1) {
                        this.$setSessionStorage('user', response.data.data)
                        alert('用户登录成功！')
                        this.$router.push('/')
                    } else {
                        alert(response.data.msg || '用户登录失败')
                    }
                    
                } else if (this.currentRole === 'admin') {
                    // 管理员登录接口
                    response = await this.$axios.post('/admin/getAdminByIdByPass', this.$qs.stringify({
                        adminId: this.user.userName,
                        password: this.user.password
                    }))
                    
                    console.log('管理员登录响应:', response.data)
                    
                    if (response.data.code === 1) {
                        this.$setSessionStorage('admin', response.data.data)
                        alert('管理员登录成功！')
                        this.$router.push('/adminPanel')
                    } else {
                        alert(response.data.msg || '管理员登录失败')
                    }
                } else if (this.currentRole === 'business') {
                    // 商家登录接口
                    response = await this.$axios.post('/business/getBusinessByIdByPass', this.$qs.stringify({
                        businessId: this.user.userName,
                        password: this.user.password
                    }))
                    
                    console.log('商家登录响应:', response.data)
                    
                    if (response.data.code === 1) {
                        this.$setSessionStorage('business', response.data.data)
                        alert('商家登录成功！')
                        this.$router.push('/businessPanel')
                    } else {
                        alert(response.data.msg || '商家登录失败')
                    }
                } else if (this.currentRole === 'developer') {
                    // 开发者登录接口
                    response = await this.$axios.post('/business/getBusinessByIdByPass', this.$qs.stringify({
                        businessId: this.user.userName,
                        password: this.user.password
                    }))
                    
                    console.log('开发者登录响应:', response.data)
                    
                    if (response.data.code === 1) {
                        this.$setSessionStorage('developer', response.data.data)
                        alert('开发者登录成功！')
                        this.$router.push('/testConn')
                    } else {
                        alert(response.data.msg || '开发者登录失败')
                    }
                }

            } catch (error) {
                console.error('登录错误:', error)
                alert('登录失败，请稍后重试')
            }
        },

        goToRegister() {
            if (this.currentRole === 'user') {
                this.$router.push('/register')
            } else if (this.currentRole === 'admin') {
                this.$router.push('/adminRegister')
            } else if (this.currentRole === 'business') {
                this.$router.push('/businessRegister')
            } else if(this.currentRole === 'developer') {
                // 这里需要修改
                this.$router.push('/testConn')
            }
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