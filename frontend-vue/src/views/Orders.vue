<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>确认订单</p >
    </header>

    <!-- 订单信息部分 -->
    <div class="order-info">
      <h5>订单配送至：</h5>
      <div class="order-info-address" @click="toUserAddress">
        <p>
          {{ deliveryaddress?.address || "请选择送货地址" }}
        </p >
        <i class="fa fa-angle-right"></i>
      </div>
      <p v-if="deliveryaddress">
        {{ deliveryaddress.contactName }}{{ deliveryaddress.contactSex == 1 ? "先生" : "女士" }} {{ deliveryaddress.contactTel }}
      </p >
    </div>

    <h3>{{ business.businessName }}</h3>

    <!-- 订单明细部分 -->
    <ul class="order-detailed">
      <li v-for="item in cartArr" :key="item.cartId">
        <div class="order-detailed-left">
          <!-- < img :src="getFoodImage(item.food.foodId)" /> -->
           <img :src="item.food.foodImg">
          <p>{{ item.food.foodName }} x {{ item.quantity }}</p >
        </div>
        <p>&#165;{{ (item.food.foodPrice * item.quantity).toFixed(2) }}</p >
      </li>
    </ul>
    <div class="order-deliveryfee">
      <p>配送费</p >
      <p>&#165;{{ business.deliveryPrice }}</p >
    </div>

    <!-- 合计部分 -->
    <div class="total">
      <div class="total-left">&#165;{{ totalPrice }}</div>
      <div class="total-right" @click="toPayment">去支付</div>
    </div>

    <!-- 修改/选择地址弹窗 -->
    <div v-if="showAddressModal" class="addr-modal-mask" @click.self="showAddressModal=false">
      <div class="addr-modal">
        <h4>修改配送地址</h4>
        <!-- 已有地址选择 -->
        <div class="addr-field" v-if="addressList.length">
          <label>选择已有</label>
          <select v-model="selectedAddressId">
            <option v-for="a in addressList" :key="a.id || a.daId" :value="a.id || a.daId">
              {{ a.contactName }}（{{ a.contactSex==1?'先生':'女士' }}） - {{ a.contactTel }} - {{ a.address }}
            </option>
          </select>
          <button class="addr-save" style="margin-left:2vw" @click="pickAddress">使用</button>
        </div>
        <div class="addr-field">
          <label>收货人</label>
          <input v-model="addressForm.contactName" placeholder="请输入收货人" />
        </div>
        <div class="addr-field">
          <label>性别</label>
          <select v-model.number="addressForm.contactSex">
            <option :value="1">先生</option>
            <option :value="0">女士</option>
          </select>
        </div>
        <div class="addr-field">
          <label>电话</label>
          <input v-model="addressForm.contactTel" placeholder="请输入联系电话" />
        </div>
        <div class="addr-field">
          <label>地址</label>
          <input v-model="addressForm.address" placeholder="请输入详细地址" />
        </div>
        <div class="addr-actions">
          <button class="addr-cancel" @click="showAddressModal=false">取消</button>
          <button class="addr-save" @click="saveAddress">保存新地址</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import qs from "qs";
import { useRouter, useRoute } from "vue-router";
import { ref, computed, inject, onMounted } from "vue";
import { getSessionStorage, getLocalStorage, setLocalStorage } from "../common.js";

const axios = inject("axios");
const router = useRouter();
const route = useRoute();

// 获取用户信息 - 支持多种方式
const getUserInfo = () => {
  const sessionUser = getSessionStorage("user");
  const localUser = getLocalStorage("userInfo");
  return sessionUser || localUser || null;
};

const user = ref(getUserInfo());
const businessId = route.query.businessId;
const business = ref({});
const cartArr = ref([]);

console.log('Orders页面用户信息:', user.value);


// 修改 getFoodImage 函数 - 使用店家图片作为默认图片
const getFoodImage = (foodId) => {
  console.log(`Orders页面获取商品图片 - foodId: ${foodId}`)
  
  // 首先检查是否有专用商品图片
  const image = foodImages[foodId]
  if (image) {
    console.log(`✅ 找到专用商品图片: foodId ${foodId}`)
    return image
  }
  
  
  // 获取对应的店家图片
  const businessImage = getBusinessImage(businessImageId)
  console.log(`⚠️ 使用店家图片: foodId ${foodId} -> 商家 ${businessImageId}`)
  
  return businessImage
}

// 默认地址数据（如果没有选择地址）
const defaultAddress = {
  daId: 1,
  contactName: '张三',
  contactSex: 1,
  contactTel: '13800138000',
  address: '辽宁省沈阳市浑南区软件园E18号楼'
}

// 获取用户地址，如果没有则使用默认地址
const deliveryaddress = ref(getLocalStorage(user.value?.userId) || defaultAddress);

// 修改/选择地址弹窗状态
const showAddressModal = ref(false);
// 地址列表（来自后端）
const addressList = ref([]);
// 当前选择的地址ID
const selectedAddressId = ref(deliveryaddress.value?.daId || null);
// 新建地址表单
const addressForm = ref({ contactName: '', contactSex: 1, contactTel: '', address: '' });

const openAddressModal = async () => {
  await loadAddressList();
  selectedAddressId.value = deliveryaddress.value?.daId || null;
  addressForm.value = { contactName: '', contactSex: 1, contactTel: '', address: '' };
  showAddressModal.value = true;
};

// 从后端加载地址列表
const loadAddressList = async () => {
  try {
    const headers = {};
    if (user.value?.token) headers['Authorization'] = `Bearer ${user.value.token}`;
    const resp = await axios.get('/api/addresses', { headers, params: { userId: user.value?.userId || user.value?.id } });
    addressList.value = resp?.data?.data || [];
  } catch (e) {
    addressList.value = [];
  }
};

// 选择已有地址
const pickAddress = () => {
  const found = addressList.value.find(a => String(a.id || a.daId) === String(selectedAddressId.value));
  if (found) {
    const normalized = {
      daId: found.id || found.daId,
      contactName: found.contactName,
      contactSex: found.contactSex,
      contactTel: found.contactTel,
      address: found.address
    };
    deliveryaddress.value = normalized;
    try { setLocalStorage(user.value?.userId, normalized); } catch (e) {}
    showAddressModal.value = false;
  }
};

// 新建并保存地址到后端
const saveAddress = async () => {
  const { contactName, contactSex, contactTel, address } = addressForm.value;
  if (!contactName || !contactTel || !address) { alert('请填写完整的收货人、电话和地址'); return; }
  try {
    const headers = {};
    if (user.value?.token) headers['Authorization'] = `Bearer ${user.value.token}`;
    const payload = { contactName, contactSex, contactTel, address };
    const resp = await axios.post('/api/addresses', payload, { headers });
    const saved = resp?.data?.data || null;
    if (saved) {
      deliveryaddress.value = { daId: saved.id, contactName, contactSex, contactTel, address };
      try { setLocalStorage(user.value?.userId, deliveryaddress.value); } catch (e) {}
      showAddressModal.value = false;
    } else {
      alert('保存地址失败');
    }
  } catch (e) {
    console.error(e);
    alert('保存地址失败');
  }
};

const totalPrice = computed(() => {
  let totalPrice = 0;
  for (let cartItem of cartArr.value) {
    totalPrice += cartItem.food.foodPrice * cartItem.quantity;
  }
  totalPrice += business.value.deliveryPrice;
  return totalPrice.toFixed(2);
});

// 初始化方法 - 匹配你的后端接口
const init = async () => {
  console.log('=== 订单页面初始化 ===')
  console.log('用户:', user.value?.userId)
  console.log('商家ID:', businessId)
  console.log('路由参数:', route.query)
  
  // 检查是否从购物车页面跳转过来
  const fromCart = route.query.fromCart === 'true'
  console.log('是否从购物车跳转:', fromCart)
  
  if (fromCart) {
    // 从localStorage获取购物车数据
    const cartData = localStorage.getItem('currentCart')
    if (cartData) {
      try {
        const parsedCartData = JSON.parse(cartData)
        console.log('从localStorage获取购物车数据:', parsedCartData)
        
        // 设置商家信息
        business.value = parsedCartData.business || {
          businessId: businessId,
          businessName: '默认商家',
          businessExplain: '默认说明',
          deliveryPrice: 3
        }
        
        // 设置购物车数据
        cartArr.value = parsedCartData.items.map(item => ({
          cartId: item.foodId, // 使用foodId作为cartId
          food: {
            foodId: item.foodId,
            foodName: item.foodName,
            foodPrice: item.foodPrice,
            foodImg: item.foodImg
          },
          quantity: item.quantity
        }))
        
        console.log('购物车数据设置成功，商品数量:', cartArr.value.length)
        
        // 清除localStorage中的购物车数据
        localStorage.removeItem('currentCart')
        
        return // 直接返回，不需要调用后端API
      } catch (e) {
        console.error('解析购物车数据失败:', e)
      }
    }
  }
  
  // 如果没有从购物车跳转或解析失败，使用后端API加载数据
  console.log('使用后端API加载数据')
  
  try {
    // 添加认证头信息
    const headers = {};
    if (user.value?.token) {
      headers['Authorization'] = `Bearer ${user.value.token}`;
    }

    // 1. 获取商家信息
    const businessResponse = await axios.get(`/api/businesses/${businessId}`, { headers })
    if (businessResponse.data && businessResponse.data.code === 'OK') {
      business.value = businessResponse.data.data
      console.log('商家信息加载成功:', business.value.businessName)
    } else {
      console.error('获取商家信息失败:', businessResponse.data?.message)
      // 使用本地默认数据
      business.value = {
        businessId: businessId,
        businessName: '默认商家',
        businessExplain: '默认说明',
        deliveryPrice: 3
      }
    }

    // 2. 获取购物车数据
    const cartResponse = await axios.get('/api/carts', { headers })
    if (cartResponse.data && cartResponse.data.code === 'OK') {
      // 过滤出当前商家的购物车商品
      const allCartItems = cartResponse.data.data || []
      cartArr.value = allCartItems.filter(item => 
        item.business && item.business.id === businessId
      )
      console.log('购物车数据加载成功，商品数量:', cartArr.value.length)
      
      if (cartArr.value.length === 0) {
        console.warn('购物车为空')
        alert('购物车为空，请先添加商品')
        router.go(-1)
        return
      }
      
      // 打印购物车数据结构用于调试
      if (cartArr.value.length > 0) {
        console.log('购物车第一个商品:', cartArr.value[0])
      }
    } else {
      console.error('获取购物车失败:', cartResponse.data?.message)
      alert('获取购物车失败，请重试')
      router.go(-1)
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    // 使用本地默认数据作为兜底
    business.value = {
      businessId: businessId,
      businessName: '默认商家',
      businessExplain: '默认说明',
      deliveryPrice: 3
    }
    cartArr.value = []
    alert('加载数据失败，请重试')
    router.go(-1)
  }
};

// 页面初始化
init();

const toUserAddress = () => {
  openAddressModal();
};

// 支付方法 - 简化版本，直接跳转到支付页面
const toPayment = async () => {
if (!deliveryaddress.value || !deliveryaddress.value.daId) {
  alert("请选择送货地址！");
  return;
}

if (cartArr.value.length === 0) {
  alert("购物车为空！");
  return;
}

console.log('=== 准备创建订单 ===');
console.log('用户信息:', user.value);
console.log('商家ID:', businessId);
  console.log('地址ID:', deliveryaddress.value?.daId);
console.log('订单总价:', totalPrice.value);
console.log('购物车商品数量:', cartArr.value.length);

try {
  // 调用后端API创建订单
  const orderData = {
    business: {
      id: businessId
    },
    deliveryAddress: {
      id: deliveryaddress.value?.daId
    },
    orderTotal: totalPrice.value
  };

  // 添加认证头信息
  const headers = {};
  if (user.value?.token) {
    headers['Authorization'] = `Bearer ${user.value.token}`;
  }

  console.log('发送订单数据到后端:', orderData);
  console.log('请求头信息:', headers);
  const response = await axios.post('/api/orders', orderData, { headers, withCredentials: true });
  
  if (response.data && response.data.code === 'OK') {
    const createdOrder = response.data.data;
    console.log('订单创建成功:', createdOrder);
    
    // 将订单数据保存到localStorage，供支付页面使用
    const paymentData = {
      orderId: createdOrder.id,
      business: business.value,
      items: cartArr.value,
      totalPrice: totalPrice.value,
      deliveryAddress: deliveryaddress.value,
      userId: user.value?.id || user.value?.userId || user.value?.username || 'unknown'
    };

    localStorage.setItem('currentOrder', JSON.stringify(paymentData));
    // 标记为未支付（用于订单页展示）
    try {
      const unKey = 'unpaid_orders_demo';
      const raw = localStorage.getItem(unKey);
      const arr = raw ? JSON.parse(raw) : [];
      const oid = String(createdOrder.id);
      if (!arr.includes(oid)) arr.unshift(oid);
      localStorage.setItem(unKey, JSON.stringify(arr));
      // 同步写入订单简要信息到本地历史，便于订单页显示到“未支付”
      const histKey = 'orders_history_demo';
      const histRaw = localStorage.getItem(histKey);
      const histArr = histRaw ? JSON.parse(histRaw) : [];
      const brief = {
        id: String(createdOrder.id),
        orderId: String(createdOrder.id),
        orderTotal: totalPrice.value,
        orderState: 0,
        orderTime: new Date().toISOString(),
        business: business.value,
        deliveryAddress: deliveryaddress.value,
        list: cartArr.value.map(ci => ({
          odId: ci.cartId,
          food: { foodName: ci.food.foodName, foodPrice: ci.food.foodPrice },
          quantity: ci.quantity
        }))
      };
      // 去重并前插
      const exists = histArr.some(x => String(x.id ?? x.orderId) === String(createdOrder.id));
      if (!exists) histArr.unshift(brief);
      localStorage.setItem(histKey, JSON.stringify(histArr));
    } catch (e) {}
    console.log('订单数据已保存:', paymentData);

    // 跳转到支付页面
    router.push({
      path: "/payment",
      query: { orderId: createdOrder.id }
    });
  } else {
    console.error('创建订单失败:', response.data?.message);
    // 后端未通过，走本地兜底订单
    const fallbackOrderId = Date.now();
    const paymentData = {
      orderId: fallbackOrderId,
      business: business.value,
      items: cartArr.value,
      totalPrice: totalPrice.value,
      deliveryAddress: deliveryaddress.value,
      userId: user.value?.id || user.value?.userId || user.value?.username || 'unknown'
    };
    localStorage.setItem('currentOrder', JSON.stringify(paymentData));
    // 标记为未支付（本地兜底）
    try {
      const unKey = 'unpaid_orders_demo';
      const raw = localStorage.getItem(unKey);
      const arr = raw ? JSON.parse(raw) : [];
      const oid = String(fallbackOrderId);
      if (!arr.includes(oid)) arr.unshift(oid);
      localStorage.setItem(unKey, JSON.stringify(arr));
      // 同步写入本地历史，便于订单页显示
      const histKey = 'orders_history_demo';
      const histRaw = localStorage.getItem(histKey);
      const histArr = histRaw ? JSON.parse(histRaw) : [];
      const brief = {
        id: String(fallbackOrderId),
        orderId: String(fallbackOrderId),
        orderTotal: totalPrice.value,
        orderState: 0,
        orderTime: new Date().toISOString(),
        business: business.value,
        deliveryAddress: deliveryaddress.value,
        list: cartArr.value.map(ci => ({
          odId: ci.cartId,
          food: { foodName: ci.food.foodName, foodPrice: ci.food.foodPrice },
          quantity: ci.quantity
        }))
      };
      const exists = histArr.some(x => String(x.id ?? x.orderId) === String(fallbackOrderId));
      if (!exists) histArr.unshift(brief);
      localStorage.setItem(histKey, JSON.stringify(histArr));
    } catch (e) {}
    console.warn('后端创建失败，使用本地订单号:', fallbackOrderId);
    router.push({ path: '/payment', query: { orderId: fallbackOrderId } });
  }
} catch (error) {
  console.error('创建订单失败:', error);
  // 如果是未认证或403，直接使用本地兜底订单流程
  const status = error?.response?.status;
  if (status === 401 || status === 403) {
    console.warn('检测到未登录/无权限，使用本地订单兜底');
  } else {
    console.warn('接口异常，使用本地订单兜底');
  }
  const fallbackOrderId = Date.now();
  const paymentData = {
    orderId: fallbackOrderId,
    business: business.value,
    items: cartArr.value,
    totalPrice: totalPrice.value,
    deliveryAddress: deliveryaddress.value,
    userId: user.value?.id || user.value?.userId || user.value?.username || 'unknown'
  };
  localStorage.setItem('currentOrder', JSON.stringify(paymentData));
  // 标记为未支付（本地兜底）
  try {
    const unKey = 'unpaid_orders_demo';
    const raw = localStorage.getItem(unKey);
    const arr = raw ? JSON.parse(raw) : [];
    const oid = String(fallbackOrderId);
    if (!arr.includes(oid)) arr.unshift(oid);
    localStorage.setItem(unKey, JSON.stringify(arr));
    // 同步写入本地历史
    const histKey = 'orders_history_demo';
    const histRaw = localStorage.getItem(histKey);
    const histArr = histRaw ? JSON.parse(histRaw) : [];
  const brief = {
      id: String(fallbackOrderId),
      orderId: String(fallbackOrderId),
      orderTotal: totalPrice.value,
      orderState: 0,
    orderTime: new Date().toISOString(),
      business: business.value,
    deliveryAddress: deliveryaddress.value,
      list: cartArr.value.map(ci => ({
        odId: ci.cartId,
        food: { foodName: ci.food.foodName, foodPrice: ci.food.foodPrice },
        quantity: ci.quantity
      }))
    };
    const exists = histArr.some(x => String(x.id ?? x.orderId) === String(fallbackOrderId));
    if (!exists) histArr.unshift(brief);
    localStorage.setItem(histKey, JSON.stringify(histArr));
  } catch (e) {}
  router.push({ path: '/payment', query: { orderId: fallbackOrderId } });
}
};
</script>

<style scoped>
/* 保持你原有的样式 */
/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  height: 100%;
}

/****************** header部分 ******************/
.wrapper header {
  width: 100%;
  height: 12vw;
  background-color: #0097ff;
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

/****************** 订单信息部分 ******************/
.wrapper .order-info {
  width: 100%;
  margin-top: 12vw;
  background-color: #0097ef;
  box-sizing: border-box;
  padding: 2vw;
  color: #fff;
}

.wrapper .order-info h5 {
  font-size: 3vw;
  font-weight: 300;
}

.wrapper .order-info .order-info-address {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;

  font-weight: 700;
  user-select: none;
  cursor: pointer;
  margin: 1vw 0;
}

.wrapper .order-info .order-info-address p {
  width: 90%;
  font-size: 5vw;
}

.wrapper .order-info .order-info-address i {
  font-size: 6vw;
}

.wrapper .order-info p {
  font-size: 3vw;
}

.wrapper h3 {
  box-sizing: border-box;
  padding: 3vw;
  font-size: 4vw;
  color: #666;
  border-bottom: solid 1px #ddd;
}

/****************** 订单明细部分 ******************/
.wrapper .order-detailed {
  width: 100%;
}

.wrapper .order-detailed li {
  width: 100%;
  height: 16vw;
  box-sizing: border-box;
  padding: 3vw;
  color: #666;

  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wrapper .order-detailed li .order-detailed-left {
  display: flex;
  align-items: center;
}

.wrapper .order-detailed li .order-detailed-left img {
  width: 10vw;
  height: 10vw;
}

.wrapper .order-detailed li .order-detailed-left p {
  font-size: 3.5vw;
  margin-left: 3vw;
}

.wrapper .order-detailed li p {
  font-size: 3.5vw;
}

.wrapper .order-deliveryfee {
  width: 100%;
  height: 16vw;
  box-sizing: border-box;
  padding: 3vw;
  color: #666;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 3.5vw;
}

/****************** 订单合计部分 ******************/
.wrapper .total {
  width: 100%;
  height: 14vw;

  position: fixed;
  left: 0;
  bottom: 0;

  display: flex;
}

.wrapper .total .total-left {
  flex: 2;
  background-color: #505051;
  color: #fff;
  font-size: 4.5vw;
  font-weight: 700;
  user-select: none;

  display: flex;
  justify-content: center;
  align-items: center;
}

.wrapper .total .total-right {
  flex: 1;
  background-color: #38ca73;
  color: #fff;
  font-size: 4.5vw;
  font-weight: 700;
  user-select: none;
  cursor: pointer;

  display: flex;
  justify-content: center;
  align-items: center;
}

/* 地址弹窗样式 */
.addr-modal-mask {
  position: fixed;
  left: 0; right: 0; top: 0; bottom: 0;
  background: rgba(0,0,0,0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.addr-modal {
  width: 86vw;
  background: #fff;
  border-radius: 3vw;
  padding: 4vw;
}
.addr-modal h4 {
  margin: 0 0 2vw 0;
  font-size: 4.2vw;
  color: #333;
}
.addr-field {
  margin: 2vw 0;
  display: flex;
  align-items: center;
}
.addr-field label {
  width: 18vw;
  color: #666;
  font-size: 3.6vw;
}
.addr-field input, .addr-field select {
  flex: 1;
  height: 10vw;
  border: 1px solid #ddd;
  border-radius: 2vw;
  padding: 0 3vw;
  font-size: 3.6vw;
}
.addr-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 3vw;
}
.addr-actions .addr-cancel,
.addr-actions .addr-save {
  height: 10vw;
  padding: 0 4vw;
  border: none;
  border-radius: 2vw;
  font-size: 3.8vw;
  color: #fff;
}
.addr-actions .addr-cancel { background: #bbb; margin-right: 2vw; }
.addr-actions .addr-save { background: #38ca73; }
</style>