# CQU Express Locker System - Android App

## 简介 (Introduction)
本项目为 **CQU Express Locker System** 的移动端应用，服务于 **普通用户** 和 **快递员**。提供寄件、取件、身份认证、快递员投递等核心业务功能。

## 技术栈 (Tech Stack)
- **开发语言**: Kotlin
- **网络框架**: Retrofit 2 + OkHttp 3
- **异步处理**: Kotlin Coroutines (协程)
- **数据解析**: Gson
- **依赖注入**: (如 Hilt/Koin，视具体实现而定)

## 核心功能 (Features)
- **用户端**:
  - 扫码/输入取件码取件 (`pickupExpress`)
  - 寄件下单 (`sendExpress`)
  - 个人中心与历史记录
- **快递员端**:
  - 快递投递 (`deliverExpress`)
  - 滞留件清理/退回
  - 揽收寄件

## 接口集成 (API Integration)
- **网络层封装**: `me.wjz.cquexpresslocker.network.ApiService`
- **Base URL**: 配置在 `BuildConfig.BASE_URL`
- **对接后端**: `/api/v1` 下的业务接口（Auth, User, Courier, Express）。
- **认证方式**: Bearer Token (JWT)，自动在 `ApiClient`拦截器中注入。

## 开发环境 (Development)
- Android Studio Ladybug | 2024.2.1 或更高
- Gradle 8.x
- JDK 17
