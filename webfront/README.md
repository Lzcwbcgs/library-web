# Vue 3 + Vite

This template should help get you started developing with Vue 3 in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about IDE Support for Vue in the [Vue Docs Scaling up Guide](https://vuejs.org/guide/scaling-up/tooling.html#ide-support).

## 前端部分（webfront）
~~~
webfront/
├── src/
│   ├── api/                # API 接口定义
│   │   ├── auth.js         # 认证相关接口(登录/注册/登出)
│   │   ├── user.js         # 用户相关接口(个人信息/密码修改)
│   │   ├── book.js         # 图书相关接口
│   │   └── request.js      # Axios 请求配置
│   │
│   ├── assets/            # 静态资源(图片/字体等)
│   │
│   ├── components/        # 通用组件
│   │
│   ├── layout/           # 布局组件
│   │   └── Layout.vue    # 主布局(包含侧边栏/顶栏)
│   │
│   ├── router/           # 路由配置
│   │   └── index.js      # 路由定义
│   │
│   ├── stores/           # Pinia 状态管理
│   │   ├── user.js       # 用户状态管理
│   │   └── book.js       # 图书状态管理
│   │
│   ├── views/            # 页面组件
│   │   ├── Login.vue     # 登录页
│   │   ├── Register.vue  # 注册页
│   │   ├── Dashboard.vue # 首页仪表盘
│   │   └── Profile.vue   # 个人中心
│   │
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
~~~

## 后端部分（webback）
~~~
webback/
├── src/main/
│   ├── java/com/xiazihan/webback/
│   │   ├── config/           # 配置类
│   │   │   ├── SecurityConfig.java    # Spring Security 配置
│   │   │   └── SwaggerConfig.java     # Swagger 文档配置
│   │   │
│   │   ├── controller/       # 控制器
│   │   │   ├── AuthController.java    # 认证控制器
│   │   │   ├── UserController.java    # 用户控制器
│   │   │   └── BookController.java    # 图书控制器
│   │   │
│   │   ├── model/           # 数据模型
│   │   │   ├── entity/      # 实体类
│   │   │   ├── dto/         # 数据传输对象
│   │   │   └── vo/          # 视图对象
│   │   │
│   │   ├── service/         # 业务逻辑
│   │   │   ├── impl/       # 服务实现类
│   │   │   └── UserService.java  # 用户服务接口
│   │   │
│   │   └── security/        # 安全相关
│   │       └── JwtAuthenticationFilter.java  # JWT 认证过滤器
│   │
│   └── resources/
│       ├── mapper/         # MyBatis映射文件
│       └── application.yml # 应用配置文件
~~~

## 主要功能模块

#### 1、认证模块：

- 登录/注册/登出
- JWT token 认证
- 权限控制

#### 2、用户模块：

- 个人信息管理
- 密码修改
- 角色权限管理

#### 3、图书模块：

图书列表/详情
- 图书借阅/归还
- 分类管理

#### 4、系统模块：

- 权限管理
- 日志记录
- 系统配置
