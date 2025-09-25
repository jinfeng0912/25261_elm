import { createApp } from 'vue';  
import App from './App.vue';
import router from './router';
import 'font-awesome/css/font-awesome.min.css';
import axios from 'axios';
import qs from 'qs';
import {
    getCurDate,
    setSessionStorage,
    getSessionStorage,
    removeSessionStorage,
    setLocalStorage,
    getLocalStorage,
    removeLocalStorage
} from './common.js';


const app = createApp(App);
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$qs = qs;
app.config.globalProperties.$getCurDate = getCurDate;
app.config.globalProperties.$setSessionStorage = setSessionStorage;
app.config.globalProperties.$getSessionStorage = getSessionStorage;
app.config.globalProperties.$removeSessionStorage = removeSessionStorage;
app.config.globalProperties.$setLocalStorage = setLocalStorage;
app.config.globalProperties.$getLocalStorage = getLocalStorage;
app.config.globalProperties.$removeLocalStorage = removeLocalStorage;

// ========== 修改：完善路由导航守卫 ==========
router.beforeEach(function(to, from, next) {
    let user = sessionStorage.getItem('user');
    
    // 白名单路由（不需要登录的页面）
    const whiteList = [
        '/', 
        '/index', 
        '/testConn',
        '/businessList', 
        '/businessInfo', 
        '/login', 
        '/message',
        '/chatDetail',
        '/register',
        '/search',          // 搜索页面
        '/filter',          // 筛选页面
        '/superMember',     // 会员页面
        '/packageDeals',    // 套餐页面
        '/packagePayment',  // 套餐支付页面
        '/adminLogin',      // 管理员登录页面
        '/adminRegister',   // 管理员注册页面
        '/adminPanel',       // 管理员面板页面
        '/businessPanel'    // 商家面板页面
    ];
    
    // 检查当前路由是否在白名单中
    if (!whiteList.includes(to.path)) {
        if (user == null) {
            router.push('/login');
            location.reload();
        }
    }
    next();
});
// ========== 路由守卫修改结束 ==========

app.use(router).mount('#app');