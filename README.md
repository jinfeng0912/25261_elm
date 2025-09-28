# 项目部署指南

## 项目简介
本项目是天津大学软件工程综合实践17组开发的elm智能外卖平台项目。本项目后端使用 Java 开发，使用 Spring Boot 框架，前端基于 Vue3 框架开发，数据库使用 MySQL，并调用 DeepseekAPI 提供智能问答和智能数据分析服务，调用 百度地图API 提供定位和距离计算服务，提供如下功能：
- 用户功能
    - 商家搜索、排序和浏览
    - 用户自动定位和商家距离分析
    - 智能搜索
    - 购物车管理
    - 红包刮奖和红包管理
    - 支付和订单状态跟踪
    - AI私人健康助手
- 商家登录
    - 菜品信息维护（增删改查）
    - 订单自动接收和管理
    - 数据管理，查看总订单，总营收等统计数据
- 管理员登录
    - 商家账户管理
    - 全局菜品管理
    - AI智能数据分析
- 开发者登录
    - 测试商家功能
    - 测试管理员登录
    - AI助手测试
    - 接口测试

## 项目结构

```
SEP-MASTER
│  README.md
│  
├─backend-spring
│  │  pom.xml
│  │  README.md
│  ├─src
│  │  └─main
│  │      ├─java
│  │      │  └─cn
│  │      │      └─edu
│  │      │          └─tju
│  │      │              │  MyApplication.java
│  │      │              ├─core
│  │      │              │  ├─config
│  │      │              │  │      CorsConfig.java
│  │      │              │  │      MethodSecurityConfig.java
│  │      │              │  │      SwaggerConfig.java
│  │      │              │  │      WebSecurityConfig.java 
│  │      │              │  ├─model
│  │      │              │  │      Authority.java
│  │      │              │  │      BaseEntity.java
│  │      │              │  │      HttpResult.java
│  │      │              │  │      Person.java
│  │      │              │  │      ResultCodeEnum.java
│  │      │              │  │      User.java    
│  │      │              │  ├─security
│  │      │              │  │  │  JwtAccessDeniedHandler.java
│  │      │              │  │  │  JwtAuthenticationEntryPoint.java
│  │      │              │  │  │  SecurityUtils.java
│  │      │              │  │  │  UserModelDetailsService.java
│  │      │              │  │  │  UserNotActivatedException.java
│  │      │              │  │  ├─jwt
│  │      │              │  │  │      JWTConfigurer.java
│  │      │              │  │  │      JWTFilter.java
│  │      │              │  │  │      TokenProvider.java   
│  │      │              │  │  ├─repository
│  │      │              │  │  │      AuthorityRepository.java
│  │      │              │  │  │      PersonRepository.java
│  │      │              │  │  │      UserRepository.java
│  │      │              │  │  ├─rest
│  │      │              │  │  │  │  AuthenticationRestController.java
│  │      │              │  │  │  │  UserRestController.java
│  │      │              │  │  │  └─dto
│  │      │              │  │  │          LoginDto.java
│  │      │              │  │  │          PasswordChangeDTO.java       
│  │      │              │  │  └─service
│  │      │              │  │          UserService.java       
│  │      │              │  ├─util
│  │      │              │  │      ObjectUtils.java
│  │      │              │  │      
│  │      │              │  └─utils
│  │      │              │          ObjectUtils.java        
│  │      │              └─elm
│  │      │                  ├─controller
│  │      │                  │      AddressController.java
│  │      │                  │      BusinessController.java
│  │      │                  │      CartController.java
│  │      │                  │      FoodController.java
│  │      │                  │      HttpRestController.java
│  │      │                  │      OrderController.java   
│  │      │                  ├─model
│  │      │                  │  │  Business.java
│  │      │                  │  │  Cart.java
│  │      │                  │  │  DeliveryAddress.java
│  │      │                  │  │  Food.java
│  │      │                  │  │  Order.java
│  │      │                  │  │  OrderDetailet.java
│  │      │                  │  │  
│  │      │                  │  └─dto
│  │      │                  │          BusinessDTO.java       
│  │      │                  ├─repository
│  │      │                  │      BusinessRepository.java
│  │      │                  │      CartRepository.java
│  │      │                  │      DeliveryAddressRepository.java
│  │      │                  │      FoodRepository.java
│  │      │                  │      OrderRepository.java   
│  │      │                  └─service
│  │      │                          BusinessService.java
│  │      │                          CartService.java
│  │      │                          DeliveryAddressService.java
│  │      │                          FoodService.java
│  │      │                          OrderService.java
│  │      │                          
│  │      └─resources
│  │          │  application.properties
│  │          │  banner.txt
│  │          │  data.sql
│  │          │  schema.sql
│  │          │  
│  │          └─static
│  │                  index.html
│  │                  
│  └─target
│              
├─frontend-vue
│  │  .vue.config.js.swp
│  │  babel.config.js
│  │  jsconfig.json
│  │  package-lock.json
│  │  package.json
│  │  README.md
│  │  vue.config.js
│  │  
│  ├─public
│  │  │  business_info.json
│  │  │  business_list.json
│  │  │  favicon.ico
│  │  │  foodtype.json
│  │  │  index.html
│  │  │  index.json
│  │  │  user.json
│  │  │  
│  │  └─images   
│  └─src
│      │  App.vue
│      │  common.js
│      │  main.js 
│      ├─assets 
│      ├─components
│      │      Footer.vue   
│      ├─router
│      │      index.js
│      ├─store
│      │      sessionStore.js
│      ├─utils
│      │      storage.js
│      └─views
│              AddUserAddress.vue
│              AdminPanel.vue
│              AdminRegister.vue
│              Ai.vue
│              AlterUserInfo.vue
│              BusinessInfo.vue
│              BusinessList.vue
│              BusinessPanel.vue
│              ChatDetail.vue
│              DylLogin.vue
│              EditUserAddress.vue
│              Filter.vue
│              index.vue
│              Login.vue
│              Message.vue
│              MyLLM.vue
│              MyLLMBusinessHelper.vue
│              OrderList.vue
│              Orders.vue
│              PackageDeals.vue
│              PackagePayment.vue
│              Payment.vue
│              RedPacket.vue
│              Register.vue
│              Search.vue
│              Shiwuyou.vue
│              SuperMember.vue
│              TestConn.vue
│              TestPage.vue
│              UserAddress.vue
│              UserInfo.vue
│              
└─sql
        business(1).sql
        elb(1).sql
        food.sql
        init_database.sql
```



## 环境要求

在开始之前，请确保您的系统满足以下要求：
- Node.js：版本 14 或更高
- npm 包管理工具
- Maven：版本 3.6 或更高
- Git：已安装并配置

## 安装步骤

### 安装相关环境
1. **检查 Node.js 和 npm版本**
   ```bash
   node -v
   npm -v
   ```

2. **检查 Java 版本**
   ```bash
   java -version
   ```
3. **下载项目代码库**
   ```bash
   git clone https://gitee.com/ysq233/sep.git
   ```

## 运行项目
1. 登录mysql数据库：

```
mysql -u root -p
# 随后输入密码
```
2. 创建饿了不数据库：

```sql
CREATE DATABASE elb;
```
3. 使用数据库：

```sql
USE elb;
```
4. 运行数据库初始化脚本：

```
SOURCE sql/init_database.sql
```
5. 运行前端项目：

```
cd fronted-vue
npm build
npm run serve
```
6. 运行后端项目：

```
cd backend-spring
mvn package 
java -jar target/myapp-1.0.jar
```

7. 打开浏览器，访问

```
http://localhost:8081/#/
```

如遇无法访问登录状态，请先于`http://localhost:8081/#/login`页面选择登录身份，共用户、商家、管理员、开发者四种登录身份.

用户admin（密码为admin）拥有全部权限，用户user（密码为password）拥有用户权限，后续功能均展现在页面上，汇报展示时数据库硬删除问题已解决，删除功能全部改为数据库中字段的修改。