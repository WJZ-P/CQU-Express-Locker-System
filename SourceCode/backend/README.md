# CQU Express Locker System - Backend

## 简介 (Introduction)
本项目为 **CQU Express Locker System** 的后端服务模块，基于 **Spring Boot** 框架开发。提供RESTful API接口，支持移动端（用户/快递员）和Web管理端的数据交互。

## 技术栈 (Tech Stack)
- **核心框架**: Spring Boot 3.x
- **ORM框架**: MyBatis-Plus
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **安全认证**: JWT (JSON Web Token)
- **工具库**: Lombok, Huetool

## 项目结构 (Project Structure)
```
com.cqu.locker
├── controller      // 控制层，API接口定义
│   ├── admin       // 管理端专用接口 (/api/v1/admin/**)
│   └── ...         // 移动端/通用接口 (/api/v1/**)
├── service         // 业务逻辑层
├── mapper          // 数据访问层
├── entity          // 实体类
│   └── dto         // 数据传输对象
└── utils           // 工具类 (Result封装, Token工具等)
```

## API 接口规范 (API Specification)
- **Base URL**: `/api/v1`
- **响应格式**: 统一使用 `Result<T>` 封装。
  ```json
  {
    "code": 200,
    "msg": "success",
    "data": { ... }
  }
  ```

### 核心模块
1. **认证模块 (`/auth`)**: 登录、注册、验证码、Token校验。
2. **用户模块 (`/user`)**: 用户个人信息管理。
3. **快递员模块 (`/courier`)**: 快递员业务（投递、揽收）。
4. **快递业务 (`/express`)**: 取件、寄件、开柜。
5. **管理后台 (`/admin`)**: 用户管理、设备管理、数据统计。

## 快速开始 (Quick Start)
1. **配置数据库**: 导入 `schema.sql` 到 MySQL 数据库。
2. **修改配置**: 在 `application.yml` 中配置数据库连接和 Redis 连接。
3. **运行项目**:
   ```bash
   mvn spring-boot:run
   ```
