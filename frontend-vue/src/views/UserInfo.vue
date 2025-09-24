<template>
    <div class="wrapper">
        <!-- header -->
        <header>
            <p>用户中心</p>
        </header>

        <!-- 主要内容区域 -->
        <main class="content">
            <!-- 用户信息展示 -->
            <div class="user-info-container">
                <!-- 头像区域 -->
                <div class="avatar-section">
                    <div class="avatar-wrapper">
                        <img src="https://via.placeholder.com/150" alt="用户头像" class="user-avatar">
                    </div>
                    <div class="avatar-edit">
                        <i class="fa fa-camera"></i>
                    </div>
                </div>

                <!-- 基本信息 -->
                <div class="info-section">
                    <div class="info-item">
                        <span class="info-label">昵称</span>
                        <span class="info-value">美食达人</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">账号</span>
                        <span class="info-value">user123</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">手机号</span>
                        <span class="info-value">138****8888</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">会员等级</span>
                        <span class="info-value vip">黄金会员</span>
                    </div>
                </div>

                <!-- 其他信息卡片 -->
                <div class="card-section">
                    <div class="info-card">
                        <div class="card-icon">⚙️</div>
                        <div class="card-content">
                            <h4>设置</h4>
                            <p>账号与安全设置</p>
                        </div>
                        <i class="fa fa-chevron-right"></i>
                    </div>
                </div>
                
                <!-- 退出登录按钮 -->
                <div class="logout-btn-box">
                    <button class="logout-btn" @click="logout">退出登录</button>
                </div>
            </div>
        </main>

        <!-- 底部导航栏 -->
        <ul class="footer">
            <li @click="toIndex">
                <i class="fa fa-home"></i>
                <p>首页</p>
            </li>
            <li @click="toMessage">
                <i class="fa fa-compass"></i>
                <p>消息</p>
            </li>
            <li @click="toOrderList">
                <i class="fa fa-file-text-o"></i>
                <p>订单</p>
            </li>
            <li @click="toLogin">
                <i class="fa fa-user-o"></i>
                <p>我的</p>
            </li>
        </ul>
    </div>
</template>

<script>
import { useRouter } from "vue-router";

export default {
    setup() {
        const router = useRouter();

        const toIndex = () => {
            router.push({ path: "/index" });
        };

        const toMessage = () => {
            router.push({ path: "/message" });
        };

        const toOrderList = () => {
            router.push({ path: "/orderList" });
        };

        const toLogin = () => {
            router.push({ path: "/userInfo" });
        };

        return {
            toIndex,
            toMessage,
            toOrderList,
            toLogin
        };
    },
    data() {
        return {
            // 静态用户数据
            userInfo: {
                avatar: 'https://via.placeholder.com/150',
                nickname: '美食达人',
                username: 'user123',
                phone: '138****8888',
                vipLevel: '黄金会员'
            }
        }
    },
    methods: {
        logout() {
            console.log('用户点击退出登录')
            alert('您已退出登录')
        }
    }
}
</script>

<style scoped>
    /* 基础样式 */
    .wrapper {
        display: flex;
        flex-direction: column;
        min-height: 100vh;
        background-color: #f5f5f5;
        position: relative;
        padding-bottom: 14vw; /* 为footer留出空间 */
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

    .content {
        flex: 1;
        overflow-y: auto;
    }

    /* 用户信息容器 */
    .user-info-container {
        width: 100%;
        padding: 4vw 6vw;
        box-sizing: border-box;
    }

    /* 头像区域 */
    .avatar-section {
        position: relative;
        width: 100%;
        display: flex;
        justify-content: center;
        margin-bottom: 8vw;
    }

    .avatar-wrapper {
        width: 25vw;
        height: 25vw;
        border-radius: 50%;
        overflow: hidden;
        border: 2px solid #0097FF;
        box-shadow: 0 2px 10px rgba(0, 151, 255, 0.3);
    }

    .user-avatar {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .avatar-edit {
        position: absolute;
        right: calc(50% - 15vw);
        bottom: -2vw;
        width: 8vw;
        height: 8vw;
        background-color: #0097FF;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        color: white;
        font-size: 4vw;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
    }

    /* 基本信息区域 */
    .info-section {
        background-color: white;
        border-radius: 3vw;
        padding: 4vw;
        margin-bottom: 6vw;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .info-item {
        display: flex;
        justify-content: space-between;
        padding: 3vw 0;
        border-bottom: 1px solid #f0f0f0;
    }

    .info-item:last-child {
        border-bottom: none;
    }

    .info-label {
        font-size: 4vw;
        color: #666;
    }

    .info-value {
        font-size: 4vw;
        color: #333;
        font-weight: bold;
    }

    .info-value.vip {
        color: #FFA500;
    }

    /* 卡片区域 */
    .card-section {
        margin-bottom: 10vw;
    }

    .info-card {
        display: flex;
        align-items: center;
        background-color: white;
        border-radius: 3vw;
        padding: 4vw;
        margin-bottom: 3vw;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .card-icon {
        font-size: 6vw;
        margin-right: 4vw;
        width: 10vw;
        height: 10vw;
        background-color: #f0f8ff;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .card-content {
        flex: 1;
    }

    .card-content h4 {
        font-size: 4.2vw;
        color: #333;
        margin-bottom: 1vw;
    }

    .card-content p {
        font-size: 3.2vw;
        color: #999;
    }

    .info-card i {
        color: #999;
        font-size: 4vw;
    }

    /* 退出登录按钮 */
    .logout-btn-box {
        width: 100%;
        padding: 0 6vw;
        box-sizing: border-box;
        margin-bottom: 6vw;
    }

    .logout-btn {
        width: 100%;
        height: 12vw;
        background-color: #FF4444;
        color: white;
        border: none;
        border-radius: 2vw;
        font-size: 4.5vw;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .logout-btn:hover {
        background-color: #CC0000;
    }

    /* 底部导航栏样式 */
    .footer {
        width: 100%;
        height: 14vw;
        border-top: solid 1px #ddd;
        background-color: #fff;
        position: fixed;
        left: 0;
        bottom: 0;
        display: flex;
        justify-content: space-around;
        align-items: center;
        z-index: 100;
    }

    .footer li {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: #999;
        user-select: none;
        cursor: pointer;
    }

    .footer li p {
        font-size: 2.8vw;
    }

    .footer li i {
        font-size: 5vw;
    }
</style>