import { createRouter, createWebHashHistory } from 'vue-router'
import Index from '../views/index.vue'
import BusinessList from '../views/BusinessList.vue'
import BusinessInfo from '../views/BusinessInfo.vue'
import Login from '../views/Login.vue'
import Orders from '../views/Orders.vue'
import UserAddress from '../views/UserAddress.vue'
import Payment from '../views/Payment.vue'
import OrderList from '../views/OrderList.vue'
import AddUserAddress from '../views/AddUserAddress.vue'
import EditUserAddress from '../views/EditUserAddress.vue'
import Register from '../views/Register.vue'
import UserInfo from '../views/UserInfo.vue'
import alterUserInfo from '../views/AlterUserInfo.vue'
import superMember from '../views/SuperMember.vue'
import PackageDeals from '../views/PackageDeals.vue'
import PackagePayment from '../views/PackagePayment.vue'
import Filter from '../views/Filter.vue'
import { name } from 'file-loader'



const routes = [{
    path: '/',
    name: 'Home',
    component: Index
}, {
    path: '/index',
    name: 'Index',
    component: Index
}, {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue')
}, {
    path: '/businessList',
    name: 'BusinessList',
    component: BusinessList
}, {
    path: '/businessInfo',
    name: 'BusinessInfo',
    component: BusinessInfo
}, {
    path: '/login',
    name: 'Login',
    component: Login
}, {
    path: '/orders',
    name: 'Orders',
    component: Orders
}, {
    path: '/userAddress',
    name: 'UserAddress',
    component: UserAddress
}, {
    path: '/payment',
    name: 'Payment',
    component: Payment
}, {
    path: '/orderList',
    name: 'OrderList',
    component: OrderList
}, {
    path: '/addUserAddress',
    name: 'AddUserAddress',
    component: AddUserAddress
}, {
    path: '/editUserAddress',
    name: 'EditUserAddress',
    component: EditUserAddress
}, {
    path: '/register',
    name: 'Register',
    component: Register
}, {
    path: '/userInfo',
    name: 'UserInfo',
    component: UserInfo
}, {
    path: '/alterUserInfo',
    name: 'AlterUserInfo',
    component: alterUserInfo
}, {
    path: '/superMember',
    name: 'SuperMember',
    component: superMember
}, {
    path: '/packageDeals',
    name: 'PackageDeals',
    component: PackageDeals
}, {
    path: '/packagePayment',
    name: 'PackagePayment',
    component: PackagePayment
}, {
    path: '/filter',
    name: 'Filter',
    component: Filter
    },
    {
        path: '/adminRegister',
        name: 'AdminRegister',
        component: () => import('../views/AdminRegister.vue')
    }, {
        path: '/businessPanel',
        name: 'BusinessPanel',
        component: () => import('../views/BusinessPanel.vue')
    },{
        path: '/adminPanel',
        name: 'AdminPanel',
        component: () => import('../views/AdminPanel.vue')
    },{
        path: '/testConn',
        name: 'TestConn',
        component: () => import('@/views/TestConn.vue')
    },{
  path: '/message',
  name: 'Message',
  component: () => import('@/views/Message.vue')
}, {
  path: '/chatDetail',
  name: 'ChatDetail',
  component: () => import('@/views/ChatDetail.vue')
}
]


const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router