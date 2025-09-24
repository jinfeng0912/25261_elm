<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>确认订单</p>
    </header>

    <!-- 订单信息部分 -->
    <div class="order-info">
      <h5>订单配送至：</h5>
      <div class="order-info-address" @click="toUserAddress">
        <p>
          {{ deliveryaddress != null ? deliveryaddress.address : "请选择送货地址" }}
        </p>
        <i class="fa fa-angle-right"></i>
      </div>
      <p v-if="deliveryaddress">
        {{ deliveryaddress.contactName
        }}{{ deliveryaddress.contactSex == 1 ? "先生" : "女士" }}
        {{ deliveryaddress.contactTel }}
      </p>
    </div>

    <h3>{{ business.businessName }}</h3>

    <!-- 订单明细部分 -->
    <ul class="order-detailed">
      <li v-for="item in cartArr" :key="item.cartId">
        <div class="order-detailed-left">
          <img :src="getFoodImage(item.food.foodId)" />
          <p>{{ item.food.foodName }} x {{ item.quantity }}</p>
        </div>
        <p>&#165;{{ (item.food.foodPrice * item.quantity).toFixed(2) }}</p>
      </li>
    </ul>
    <div class="order-deliveryfee">
      <p>配送费</p>
      <p>&#165;{{ business.deliveryPrice }}</p>
    </div>

    <!-- 合计部分 -->
    <div class="total">
      <div class="total-left">&#165;{{ totalPrice }}</div>
      <div class="total-right" @click="toPayment">去支付</div>
    </div>
  </div>
</template>

<script setup>
import qs from "qs";
import { useRouter, useRoute } from "vue-router";
import { ref, computed, inject } from "vue";
import { getSessionStorage, getLocalStorage } from "../common.js";
import sj01 from '@/assets/sj01.png'
import sj02 from '@/assets/sj02.png'
import sj03 from '@/assets/sj03.png'
import sj04 from '@/assets/sj04.png'
import sj05 from '@/assets/sj05.png'
import sj06 from '@/assets/sj06.png'
import sj07 from '@/assets/sj07.png'
// 导入图片
import sp01 from '@/assets/sp01.png'
import sp02 from '@/assets/sp02.png'
import sp03 from '@/assets/sp03.png'
import sp04 from '@/assets/sp04.png'
import sp05 from '@/assets/sp05.png'
import sp06 from '@/assets/sp06.png'
import sp07 from '@/assets/sp07.png'
import sp08 from '@/assets/sp08.png'
import sp09 from '@/assets/sp09.png'
import sp10 from '@/assets/sp10.png'
import sp11 from '@/assets/sp11.png'
import sp12 from '@/assets/sp12.png'

const axios = inject("axios");
const router = useRouter();
const route = useRoute();

const user = getSessionStorage("user");
const businessId = route.query.businessId;
const business = ref({});
const cartArr = ref([]);

// 商品图片映射
const foodImages = {
  // sj01 万家饺子（软件园E18店） - 使用专用商品图片
  1: sp01, 2: sp02, 3: sp03, 4: sp04, 5: sp05, 6: sp06,
  7: sp07, 8: sp08, 9: sp09,
  
  // sj02 小锅饭豆腐馆（全运店） - 使用专用商品图片
  10: sp10, 11: sp11, 12: sp12,
  
  // 其他商品将使用店家图片
}
// 店家图片映射函数
const getBusinessImage = (businessId) => {
  const imageMap = {
    10001: sj01, 10002: sj02, 10003: sj03, 10004: sj04,
    10005: sj05, 10006: sj06, 10007: sj07
  }
  return imageMap[businessId] || sj01
}

// 修改 getFoodImage 函数 - 使用店家图片作为默认图片
const getFoodImage = (foodId) => {
  console.log(`Orders页面获取商品图片 - foodId: ${foodId}`)
  
  // 首先检查是否有专用商品图片
  const image = foodImages[foodId]
  if (image) {
    console.log(`✅ 找到专用商品图片: foodId ${foodId}`)
    return image
  }
  
  // 如果没有专用图片，根据 foodId 确定所属商家，使用店家图片
  let businessImageId = null
  
  if (foodId >= 1 && foodId <= 9) {
    businessImageId = 10001  // 万家饺子
  } else if (foodId >= 10 && foodId <= 12) {
    businessImageId = 10002  // 小锅饭豆腐馆
  } else if (foodId >= 13 && foodId <= 18) {
    businessImageId = 10003  // 麦当劳麦乐送
  } else if (foodId >= 19 && foodId <= 24) {
    businessImageId = 10004  // 米村拌饭
  } else if (foodId >= 25 && foodId <= 30) {
    businessImageId = 10005  // 申记串道
  } else if (foodId >= 31 && foodId <= 36) {
    businessImageId = 10006  // 半亩良田排骨米饭
  } else if (foodId >= 37 && foodId <= 42) {
    businessImageId = 10007  // 茶兮鲜果饮品
  } else {
    businessImageId = 10001  // 默认使用万家饺子图片
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
const deliveryaddress = getLocalStorage(user.userId) || defaultAddress;

const totalPrice = computed(() => {
  let totalPrice = 0;
  for (let cartItem of cartArr.value) {
    totalPrice += cartItem.food.foodPrice * cartItem.quantity;
  }
  totalPrice += business.value.deliveryPrice;
  return totalPrice.toFixed(2);
});

// 初始化方法 - 匹配你的后端接口
const init = () => {
  console.log('=== 订单页面初始化 ===')
  console.log('用户:', user.userId)
  console.log('商家ID:', businessId)
  
  // 1. 获取商家信息 - 使用你后端的 BusinessController/getBusinessById
  axios
    .post(
      "BusinessController/getBusinessById",
      qs.stringify({
        businessId: businessId,
      })
    )
    .then((response) => {
      if (response.data) {
        business.value = response.data;
        console.log('商家信息加载成功:', business.value.businessName);
      } else {
        // 使用本地默认数据
        business.value = {
          businessId: businessId,
          businessName: '默认商家',
          businessExplain: '默认说明',
          deliveryPrice: 3
        };
        console.log('使用默认商家数据');
      }
    })
    .catch((error) => {
      console.error('加载商家信息失败:', error);
      // 使用本地默认数据
      business.value = {
        businessId: businessId,
        businessName: '默认商家',
        businessExplain: '默认说明',
        deliveryPrice: 3
      };
    });

  // 2. 获取购物车数据 - 使用你后端的 CartController/listCart
  axios
    .post(
      "CartController/listCart",
      qs.stringify({
        userId: user.userId,
        businessId: businessId,
      })
    )
    .then((response) => {
      if (response.data && response.data.length > 0) {
        cartArr.value = response.data;
        console.log('购物车数据加载成功，商品数量:', cartArr.value.length);
        
        // 打印购物车数据结构用于调试
        if (cartArr.value.length > 0) {
          console.log('购物车第一个商品:', cartArr.value[0]);
        }
      } else {
        console.warn('购物车为空');
        alert('购物车为空，请先添加商品');
        router.go(-1);
      }
    })
    .catch((error) => {
      console.error('加载购物车数据失败:', error);
      alert('加载购物车数据失败，请重试');
      router.go(-1);
    });
};

// 页面初始化
init();

const toUserAddress = () => {
  // 如果你有地址选择页面，取消注释下面的代码
  // router.push({
  //   path: "/userAddress",
  //   query: { businessId: businessId },
  // });
  
  // 临时解决方案：直接选择默认地址
  alert('当前使用默认地址，如需修改请在个人中心设置');
};

// 支付方法 - 使用你后端的 OrdersController/createOrders
const toPayment = () => {
  if (!deliveryaddress || !deliveryaddress.daId) {
    alert("请选择送货地址！");
    return;
  }

  if (cartArr.value.length === 0) {
    alert("购物车为空！");
    return;
  }

  console.log('=== 创建订单 ===');
  console.log('用户ID:', user.userId);
  console.log('商家ID:', businessId);
  console.log('地址ID:', deliveryaddress.daId);
  console.log('订单总价:', totalPrice.value);

  // 调用你后端的创建订单接口
  axios
    .post(
      "OrdersController/createOrders",  // 匹配你的后端接口名
      qs.stringify({
        userId: user.userId,
        businessId: businessId,
        daId: deliveryaddress.daId,
      })
    )
    .then((response) => {
      let orderId = response.data;
      console.log('订单创建成功，订单ID:', orderId);
      
      if (orderId > 0) {
        // 跳转到支付页面
        router.push({ 
          path: "/payment", 
          query: { orderId: orderId } 
        });
      } else {
        alert("创建订单失败！");
      }
    })
    .catch((error) => {
      console.error('创建订单失败:', error);
      alert('创建订单失败，请重试');
    });
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
</style>