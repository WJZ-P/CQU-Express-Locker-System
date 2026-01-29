# CQU Express Locker System - Admin Frontend

## 简介 (Introduction)
本项目为 **CQU Express Locker System** 的 Web 管理端前端，基于 **Vue 3** + **Vite** 构建。为管理员提供用户管理、快递柜监控、业务数据统计等功能。

## 技术栈 (Tech Stack)
- **核心框架**: Vue 3
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **状态管理**: Pinia (或 Vuex)

## 项目结构 (Project Structure)
```
src
├── api             // API 接口定义 (admin/, user.js, etc.)
├── assets          // 静态资源
├── components      // 公共组件
├── layout          // 布局组件
├── router          // 路由配置
├── utils           // 工具函数 (request.js 封装)
├── views           // 页面试图
└── App.vue         // 根组件
```

## 接口集成 (API Integration)
- **配置位置**: `src/utils/request.js`
- **Base URL**: `/api/v1`
- **对接服务**: 后端 AdminController 系列接口 (`/api/v1/admin/**`)。
- **主要功能对接**:
  - 用户/快递员管理 (CRUD) -> `AdminUserController`, `AdminCourierController`
  - 业务数据展示

## 快速开始 (Setup)
1. **安装依赖**:
   ```bash
   npm install
   ```
2. **启动开发服务器**:
   ```bash
   npm run dev
   ```
3. **构建生产版本**:
   ```bash
   npm run build
   ```
