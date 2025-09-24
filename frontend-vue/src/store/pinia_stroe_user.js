/** 共享仓库 */
// 1.引入pinia,定义共享仓库的方法
import { defineStore } from 'pinia'
import { computed,ref } from 'vue';
// 2.针对于登录用户的session保存，会话范围
export const sessionLoginUserStore = defineStore('sessionLoginId', () => {
    const sessionUser = ref({
      token:"XYSSS",
      userId:"15584123321",
      userName:"斯皮尔伯格",
      userSex: 2
    });
    //存储登录用户信息的数据
    const setSessionUser = (user) => {
      sessionUser.value = user;// token/userId/loginName
    }
    //获取登录用户信息的数据
    const getSessionUser = computed(() => {
      return sessionUser.value;
    })

    // 登出操作时候，清空用户信息
    const clearSessionUser = () => {
      sessionUser.value = {};
    }

  
    return { getSessionUser,setSessionUser,clearSessionUser }
})
/**
ref() 就是 state 属性
computed() 就是 getters
function() 就是 actions
 */