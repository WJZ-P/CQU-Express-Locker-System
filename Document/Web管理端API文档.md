# 快递柜系统 Web 管理端 API 文档

> 本文档基于 Web 管理平台需求整理，定义管理后台所需的后端接口。

## 服务配置

| 配置项 | 值 |
|--------|-----|
| 本地端口号 | **6666** |
| 本地基础URL | `http://localhost:6666/api/v1/admin` |

---

## 目录

1. [管理员认证模块](#1-管理员认证模块)
2. [仪表盘模块](#2-仪表盘模块)
3. [快递柜管理模块](#3-快递柜管理模块)
4. [仓门管理模块](#4-仓门管理模块)
5. [快递管理模块](#5-快递管理模块)
6. [寄存记录模块](#6-寄存记录模块)
7. [用户管理模块](#7-用户管理模块)
8. [快递员管理模块](#8-快递员管理模块)
9. [统计分析模块](#9-统计分析模块)
10. [系统配置模块](#10-系统配置模块)

---

## 通用说明

### 基础路径
```
Base URL: http://{server}:{port}/api/v1/admin
```

### 通用响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 分页响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "list": []
  }
}
```

### 错误码说明
| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token失效 |
| 403 | 权限不足（非管理员） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 认证方式
除管理员登录外，其他接口需在 Header 中携带管理员 Token：
```
Authorization: Bearer {admin_token}
```

---

## 1. 管理员认证模块

### 1.1 管理员登录
**状态**: ❌ 待实现

```
POST /admin/auth/login
```

**请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "adminId": "1",
    "username": "admin",
    "nickname": "超级管理员",
    "role": "super_admin"
  }
}
```

> role 可选值: `super_admin`(超级管理员), `admin`(普通管理员)

---

### 1.2 管理员登出
**状态**: ❌ 待实现

```
POST /admin/auth/logout
```

**响应**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

---

### 1.3 获取当前管理员信息
**状态**: ❌ 待实现

```
GET /admin/auth/userinfo
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "adminId": "1",
    "username": "admin",
    "nickname": "超级管理员",
    "role": "super_admin",
    "lastLoginTime": "2026-01-25 10:00:00"
  }
}
```

---

## 2. 仪表盘模块

### 2.1 获取首页统计数据
**状态**: ❌ 待实现

```
GET /admin/dashboard/stats
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "lockerCount": 20,
    "onlineLockerCount": 18,
    "offlineLockerCount": 2,
    "todayExpressCount": 156,
    "pendingExpressCount": 42,
    "userCount": 5680,
    "courierCount": 35,
    "todayElectricity": 45.6,
    "faultCompartmentCount": 3
  }
}
```

---

### 2.2 获取近7日快递趋势
**状态**: ❌ 待实现

```
GET /admin/dashboard/express-trend
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| days | Integer | 否 | 天数，默认7 |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "dates": ["01-20", "01-21", "01-22", "01-23", "01-24", "01-25", "01-26"],
    "deliveryCount": [120, 145, 132, 156, 148, 167, 142],
    "pickupCount": [115, 138, 128, 150, 145, 160, 135]
  }
}
```

---

### 2.3 获取快递柜实时状态列表
**状态**: ❌ 待实现

```
GET /admin/dashboard/locker-status
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "lockerId": "L001",
        "lockerName": "A区1号柜",
        "location": "重庆大学A区第一教学楼",
        "totalCompartments": 8,
        "usedCompartments": 5,
        "availableCompartments": 3,
        "todayElectricity": 2.5,
        "status": "online",
        "faultCount": 0
      }
    ]
  }
}
```

---

## 3. 快递柜管理模块

### 3.1 获取快递柜列表（分页）
**状态**: ❌ 待实现

```
GET /admin/locker/list
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| lockerId | String | 否 | 柜号筛选 |
| location | String | 否 | 位置筛选（模糊匹配） |
| status | String | 否 | 状态筛选：online/offline/disabled |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 20,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "lockerId": "L001",
        "lockerName": "A区1号柜",
        "location": "重庆大学A区第一教学楼",
        "compartmentCount": 8,
        "smallCount": 3,
        "mediumCount": 3,
        "largeCount": 2,
        "todayElectricity": 2.5,
        "temperature": 25.5,
        "humidity": 45,
        "status": "online",
        "createTime": "2025-06-01 10:00:00"
      }
    ]
  }
}
```

---

### 3.2 获取快递柜详情
**状态**: ❌ 待实现

```
GET /admin/locker/{lockerId}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "lockerId": "L001",
    "lockerName": "A区1号柜",
    "location": "重庆大学A区第一教学楼",
    "compartmentCount": 8,
    "compartments": {
      "small": { "total": 3, "available": 1, "fault": 0 },
      "medium": { "total": 3, "available": 2, "fault": 1 },
      "large": { "total": 2, "available": 1, "fault": 0 }
    },
    "todayElectricity": 2.5,
    "monthElectricity": 68.5,
    "temperature": 25.5,
    "humidity": 45,
    "status": "online",
    "lastOnlineTime": "2026-01-26 10:00:00",
    "createTime": "2025-06-01 10:00:00",
    "remark": "备注信息"
  }
}
```

---

### 3.3 新增快递柜
**状态**: ❌ 待实现

```
POST /admin/locker
```

**请求体**:
```json
{
  "lockerName": "C区1号柜",
  "location": "重庆大学C区图书馆",
  "smallCount": 3,
  "mediumCount": 3,
  "largeCount": 2,
  "remark": "新安装快递柜"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "lockerId": "L021"
  }
}
```

---

### 3.4 更新快递柜信息
**状态**: ❌ 待实现

```
PUT /admin/locker/{lockerId}
```

**请求体**:
```json
{
  "lockerName": "A区1号柜（新）",
  "location": "重庆大学A区第一教学楼一楼",
  "remark": "已更换位置"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 3.5 删除快递柜
**状态**: ❌ 待实现

```
DELETE /admin/locker/{lockerId}
```

**响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

> **注意**: 仅允许删除没有正在使用中的格口的快递柜

---

### 3.6 启用/禁用快递柜
**状态**: ❌ 待实现

```
PUT /admin/locker/{lockerId}/status
```

**请求体**:
```json
{
  "status": "disabled"
}
```

> status 可选值: `online`(启用), `disabled`(禁用)

**响应**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

### 3.7 获取快递柜用电量统计
**状态**: ❌ 待实现

```
GET /admin/locker/{lockerId}/electricity
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | String | 否 | 开始日期，格式 YYYY-MM-DD |
| endDate | String | 否 | 结束日期，格式 YYYY-MM-DD |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "lockerId": "L001",
    "totalElectricity": 68.5,
    "dailyData": [
      { "date": "2026-01-20", "electricity": 2.3 },
      { "date": "2026-01-21", "electricity": 2.5 },
      { "date": "2026-01-22", "electricity": 2.1 }
    ]
  }
}
```

---

## 4. 仓门管理模块

### 4.1 获取仓门列表
**状态**: ❌ 待实现

```
GET /admin/locker/{lockerId}/compartments
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "lockerId": "L001",
    "lockerName": "A区1号柜",
    "list": [
      {
        "compartmentId": "C01",
        "compartmentNo": "A-01",
        "size": "small",
        "status": "normal",
        "hasItem": true,
        "pressure": 1.2,
        "temperature": 25,
        "humidity": 45,
        "currentExpressId": "EX20260125001",
        "currentStorageId": null
      },
      {
        "compartmentId": "C02",
        "compartmentNo": "A-02",
        "size": "medium",
        "status": "fault",
        "hasItem": false,
        "pressure": 0,
        "temperature": 0,
        "humidity": 0,
        "faultReason": "电磁锁故障",
        "faultTime": "2026-01-25 08:30:00"
      }
    ]
  }
}
```

> status 可选值: `normal`(正常), `fault`(故障), `disabled`(禁用), `occupied`(占用中)

---

### 4.2 远程开门
**状态**: ❌ 待实现

```
POST /admin/locker/{lockerId}/compartments/{compartmentId}/open
```

**响应**:
```json
{
  "code": 200,
  "message": "仓门已打开",
  "data": null
}
```

---

### 4.3 启用/禁用仓门
**状态**: ❌ 待实现

```
PUT /admin/locker/{lockerId}/compartments/{compartmentId}/status
```

**请求体**:
```json
{
  "status": "disabled"
}
```

> status 可选值: `normal`(启用), `disabled`(禁用)

**响应**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

### 4.4 标记仓门故障/恢复
**状态**: ❌ 待实现

```
PUT /admin/locker/{lockerId}/compartments/{compartmentId}/fault
```

**请求体**:
```json
{
  "isFault": true,
  "faultReason": "电磁锁无法正常开启"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

## 5. 快递管理模块

### 5.1 获取快递列表（分页）
**状态**: ❌ 待实现

```
GET /admin/express/list
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| trackingNo | String | 否 | 快递单号筛选 |
| receiverPhone | String | 否 | 收件人手机号筛选 |
| lockerId | String | 否 | 快递柜筛选 |
| status | String | 否 | 状态筛选：pending/picked/expired/returned |
| startDate | String | 否 | 入柜开始日期 |
| endDate | String | 否 | 入柜结束日期 |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1560,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "expressId": "EX20260125001",
        "trackingNo": "SF1234567890",
        "company": "顺丰速运",
        "courierName": "张三",
        "courierPhone": "137****7000",
        "receiverName": "李明",
        "receiverPhone": "138****8000",
        "lockerId": "L001",
        "lockerName": "A区1号柜",
        "compartmentNo": "A-01",
        "pickupCode": "123456",
        "status": "pending",
        "arrivalTime": "2026-01-25 10:30:00",
        "deadline": "2026-01-28 10:30:00",
        "pickupTime": null
      }
    ]
  }
}
```

> status 可选值: 
> - `pending`: 待取件
> - `picked`: 已取件
> - `expired`: 已超时
> - `returned`: 已退回

---

### 5.2 获取快递详情
**状态**: ❌ 待实现

```
GET /admin/express/{expressId}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "expressId": "EX20260125001",
    "trackingNo": "SF1234567890",
    "company": "顺丰速运",
    "courierName": "张三",
    "courierPhone": "13700007000",
    "receiverName": "李明",
    "receiverPhone": "13800008000",
    "senderName": "王五",
    "senderPhone": "13900009000",
    "lockerId": "L001",
    "lockerName": "A区1号柜",
    "lockerAddress": "重庆大学A区第一教学楼",
    "compartmentNo": "A-01",
    "compartmentSize": "medium",
    "pickupCode": "123456",
    "status": "pending",
    "arrivalTime": "2026-01-25 10:30:00",
    "deadline": "2026-01-28 10:30:00",
    "pickupTime": null,
    "notificationSent": true,
    "notificationTime": "2026-01-25 10:31:00"
  }
}
```

---

## 6. 寄存记录模块

### 6.1 获取寄存记录列表（分页）
**状态**: ❌ 待实现

```
GET /admin/storage/list
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| depositorPhone | String | 否 | 寄存人手机号筛选 |
| lockerId | String | 否 | 快递柜筛选 |
| status | String | 否 | 状态筛选：active/picked/expired |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 350,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "storageId": "ST20260125001",
        "depositorName": "张明",
        "depositorPhone": "138****1234",
        "lockerId": "L001",
        "lockerName": "A区1号柜",
        "compartmentNo": "A-02",
        "compartmentSize": "medium",
        "openCode": "998877",
        "itemDescription": "书包",
        "status": "active",
        "createTime": "2026-01-25 11:00:00",
        "expireTime": "2026-01-26 11:00:00",
        "pickupTime": null,
        "fee": 2.00
      }
    ]
  }
}
```

---

### 6.2 获取寄存详情
**状态**: ❌ 待实现

```
GET /admin/storage/{storageId}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "storageId": "ST20260125001",
    "depositorId": "10001",
    "depositorName": "张明",
    "depositorPhone": "13812341234",
    "lockerId": "L001",
    "lockerName": "A区1号柜",
    "lockerAddress": "重庆大学A区第一教学楼",
    "compartmentNo": "A-02",
    "compartmentSize": "medium",
    "openCode": "998877",
    "itemDescription": "书包",
    "status": "active",
    "createTime": "2026-01-25 11:00:00",
    "expireTime": "2026-01-26 11:00:00",
    "pickupTime": null,
    "pickerId": null,
    "pickerName": null,
    "fee": 2.00,
    "duration": 24
  }
}
```

---

## 7. 用户管理模块

### 7.1 获取用户列表（分页）
**状态**: ❌ 待实现

```
GET /admin/user/list
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| phone | String | 否 | 手机号筛选 |
| nickname | String | 否 | 昵称筛选（模糊匹配） |
| status | String | 否 | 状态筛选：normal/disabled |
| faceRegistered | Boolean | 否 | 是否已录入人脸 |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5680,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "userId": "10001",
        "nickname": "李明",
        "phone": "138****1234",
        "email": "liming@qq.com",
        "faceRegistered": true,
        "status": "normal",
        "createTime": "2025-12-01 10:00:00",
        "lastLoginTime": "2026-01-25 09:30:00",
        "expressCount": 25,
        "storageCount": 5
      }
    ]
  }
}
```

---

### 7.2 获取用户详情
**状态**: ❌ 待实现

```
GET /admin/user/{userId}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "10001",
    "nickname": "李明",
    "phone": "13812341234",
    "email": "liming@qq.com",
    "faceRegistered": true,
    "status": "normal",
    "createTime": "2025-12-01 10:00:00",
    "lastLoginTime": "2026-01-25 09:30:00",
    "bindLockers": [
      {
        "lockerId": "L001",
        "lockerName": "A区1号柜"
      }
    ],
    "statistics": {
      "totalExpressCount": 25,
      "totalStorageCount": 5,
      "pendingExpressCount": 2
    }
  }
}
```

---

### 7.3 新增用户
**状态**: ❌ 待实现

```
POST /admin/user
```

**请求体**:
```json
{
  "phone": "13800001111",
  "nickname": "新用户",
  "password": "123456",
  "email": "newuser@qq.com"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "userId": "10002"
  }
}
```

---

### 7.4 更新用户信息
**状态**: ❌ 待实现

```
PUT /admin/user/{userId}
```

**请求体**:
```json
{
  "nickname": "李明（新）",
  "email": "liming_new@qq.com"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 7.5 删除用户
**状态**: ❌ 待实现

```
DELETE /admin/user/{userId}
```

**响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 7.6 启用/禁用用户
**状态**: ❌ 待实现

```
PUT /admin/user/{userId}/status
```

**请求体**:
```json
{
  "status": "disabled"
}
```

> status 可选值: `normal`(启用), `disabled`(禁用)

**响应**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

### 7.7 重置用户密码
**状态**: ❌ 待实现

```
PUT /admin/user/{userId}/reset-password
```

**请求体**:
```json
{
  "newPassword": "123456"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
```

---

## 8. 快递员管理模块

### 8.1 获取快递员列表（分页）
**状态**: ❌ 待实现

```
GET /admin/courier/list
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| workNo | String | 否 | 工号筛选 |
| name | String | 否 | 姓名筛选 |
| company | String | 否 | 快递公司筛选 |
| status | String | 否 | 状态筛选：active/inactive |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 35,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "courierId": "C001",
        "workNo": "SF001",
        "name": "张三",
        "phone": "137****7000",
        "company": "顺丰速运",
        "todayDelivered": 25,
        "todayCollected": 10,
        "totalDelivered": 3560,
        "status": "active",
        "createTime": "2024-06-01 10:00:00"
      }
    ]
  }
}
```

---

### 8.2 获取快递员详情
**状态**: ❌ 待实现

```
GET /admin/courier/{courierId}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "courierId": "C001",
    "workNo": "SF001",
    "name": "张三",
    "phone": "13700007000",
    "company": "顺丰速运",
    "status": "active",
    "createTime": "2024-06-01 10:00:00",
    "bindLockers": [
      {
        "lockerId": "L001",
        "lockerName": "A区1号柜"
      },
      {
        "lockerId": "L002",
        "lockerName": "B区2号柜"
      }
    ],
    "statistics": {
      "todayDelivered": 25,
      "todayCollected": 10,
      "weekDelivered": 156,
      "monthDelivered": 680,
      "totalDelivered": 3560
    }
  }
}
```

---

### 8.3 新增快递员
**状态**: ❌ 待实现

```
POST /admin/courier
```

**请求体**:
```json
{
  "workNo": "YT001",
  "name": "李四",
  "phone": "13800002222",
  "password": "123456",
  "company": "圆通速递",
  "bindLockerIds": ["L001", "L002"]
}
```

**响应**:
```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "courierId": "C036"
  }
}
```

---

### 8.4 更新快递员信息
**状态**: ❌ 待实现

```
PUT /admin/courier/{courierId}
```

**请求体**:
```json
{
  "name": "张三（新）",
  "company": "顺丰速运",
  "bindLockerIds": ["L001", "L002", "L003"]
}
```

**响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 8.5 删除快递员
**状态**: ❌ 待实现

```
DELETE /admin/courier/{courierId}
```

**响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 8.6 启用/禁用快递员（在职/离职）
**状态**: ❌ 待实现

```
PUT /admin/courier/{courierId}/status
```

**请求体**:
```json
{
  "status": "inactive"
}
```

> status 可选值: `active`(在职), `inactive`(离职)

**响应**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

## 9. 统计分析模块

### 9.1 获取快递柜日用电量统计
**状态**: ❌ 待实现

```
GET /admin/statistics/electricity
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| date | String | 否 | 日期，默认今日，格式 YYYY-MM-DD |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "date": "2026-01-26",
    "totalElectricity": 45.6,
    "lockerData": [
      { "lockerId": "L001", "lockerName": "A区1号柜", "electricity": 2.5 },
      { "lockerId": "L002", "lockerName": "B区2号柜", "electricity": 2.8 },
      { "lockerId": "L003", "lockerName": "C区1号柜", "electricity": 1.9 }
    ]
  }
}
```

---

### 9.2 获取快递公司投递占比
**状态**: ❌ 待实现

```
GET /admin/statistics/company-ratio
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1560,
    "companyData": [
      { "company": "顺丰速运", "count": 546, "ratio": 35.0 },
      { "company": "圆通速递", "count": 437, "ratio": 28.0 },
      { "company": "中通快递", "count": 343, "ratio": 22.0 },
      { "company": "韵达快递", "count": 234, "ratio": 15.0 }
    ]
  }
}
```

---

### 9.3 获取月度快递量趋势
**状态**: ❌ 待实现

```
GET /admin/statistics/monthly-trend
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| year | Integer | 否 | 年份，默认当年 |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "year": 2026,
    "months": ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
    "deliveryCount": [1320, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    "pickupCount": [1280, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    "storageCount": [350, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
  }
}
```

---

### 9.4 获取仓门使用率统计
**状态**: ❌ 待实现

```
GET /admin/statistics/usage-rate
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| lockerId | String | 否 | 快递柜ID，不传则统计全部 |

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "overall": {
      "totalCompartments": 160,
      "usedCompartments": 98,
      "usageRate": 61.25
    },
    "bySize": [
      { "size": "small", "total": 60, "used": 51, "usageRate": 85.0 },
      { "size": "medium", "total": 60, "used": 43, "usageRate": 71.67 },
      { "size": "large", "total": 40, "used": 23, "usageRate": 57.5 }
    ]
  }
}
```

---

### 9.5 获取故障统计
**状态**: ❌ 待实现

```
GET /admin/statistics/fault
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalFaultCount": 5,
    "faultLockers": [
      {
        "lockerId": "L003",
        "lockerName": "C区1号柜",
        "faultCompartments": [
          {
            "compartmentNo": "C-04",
            "faultReason": "电磁锁故障",
            "faultTime": "2026-01-25 08:30:00"
          }
        ]
      }
    ],
    "recentFaults": [
      {
        "lockerId": "L003",
        "compartmentNo": "C-04",
        "faultReason": "电磁锁故障",
        "faultTime": "2026-01-25 08:30:00",
        "status": "pending"
      }
    ]
  }
}
```

---

## 10. 系统配置模块

### 10.1 获取系统配置
**状态**: ❌ 待实现

```
GET /admin/config
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "systemName": "快递柜综合应用系统",
    "pickupTimeout": 72,
    "storageTimeout": 24,
    "smsNotify": true,
    "timeoutReminder": true,
    "reminderHours": 6,
    "faceRecognition": false,
    "pricing": {
      "smallPerHour": 0.5,
      "mediumPerHour": 1.0,
      "largePerHour": 1.5
    }
  }
}
```

---

### 10.2 更新系统配置
**状态**: ❌ 待实现

```
PUT /admin/config
```

**请求体**:
```json
{
  "systemName": "快递柜综合应用系统",
  "pickupTimeout": 72,
  "storageTimeout": 24,
  "smsNotify": true,
  "timeoutReminder": true,
  "reminderHours": 6,
  "faceRecognition": true,
  "pricing": {
    "smallPerHour": 0.5,
    "mediumPerHour": 1.0,
    "largePerHour": 1.5
  }
}
```

**响应**:
```json
{
  "code": 200,
  "message": "配置更新成功",
  "data": null
}
```

---

## 接口实现进度

| 模块 | 接口 | 状态 |
|------|------|------|
| 管理员认证 | POST /admin/auth/login | ❌ 待实现 |
| 管理员认证 | POST /admin/auth/logout | ❌ 待实现 |
| 管理员认证 | GET /admin/auth/userinfo | ❌ 待实现 |
| 仪表盘 | GET /admin/dashboard/stats | ❌ 待实现 |
| 仪表盘 | GET /admin/dashboard/express-trend | ❌ 待实现 |
| 仪表盘 | GET /admin/dashboard/locker-status | ❌ 待实现 |
| 快递柜管理 | GET /admin/locker/list | ❌ 待实现 |
| 快递柜管理 | GET /admin/locker/{lockerId} | ❌ 待实现 |
| 快递柜管理 | POST /admin/locker | ❌ 待实现 |
| 快递柜管理 | PUT /admin/locker/{lockerId} | ❌ 待实现 |
| 快递柜管理 | DELETE /admin/locker/{lockerId} | ❌ 待实现 |
| 快递柜管理 | PUT /admin/locker/{lockerId}/status | ❌ 待实现 |
| 快递柜管理 | GET /admin/locker/{lockerId}/electricity | ❌ 待实现 |
| 仓门管理 | GET /admin/locker/{lockerId}/compartments | ❌ 待实现 |
| 仓门管理 | POST /admin/locker/{lockerId}/compartments/{compartmentId}/open | ❌ 待实现 |
| 仓门管理 | PUT /admin/locker/{lockerId}/compartments/{compartmentId}/status | ❌ 待实现 |
| 仓门管理 | PUT /admin/locker/{lockerId}/compartments/{compartmentId}/fault | ❌ 待实现 |
| 快递管理 | GET /admin/express/list | ❌ 待实现 |
| 快递管理 | GET /admin/express/{expressId} | ❌ 待实现 |
| 寄存记录 | GET /admin/storage/list | ❌ 待实现 |
| 寄存记录 | GET /admin/storage/{storageId} | ❌ 待实现 |
| 用户管理 | GET /admin/user/list | ❌ 待实现 |
| 用户管理 | GET /admin/user/{userId} | ❌ 待实现 |
| 用户管理 | POST /admin/user | ❌ 待实现 |
| 用户管理 | PUT /admin/user/{userId} | ❌ 待实现 |
| 用户管理 | DELETE /admin/user/{userId} | ❌ 待实现 |
| 用户管理 | PUT /admin/user/{userId}/status | ❌ 待实现 |
| 用户管理 | PUT /admin/user/{userId}/reset-password | ❌ 待实现 |
| 快递员管理 | GET /admin/courier/list | ❌ 待实现 |
| 快递员管理 | GET /admin/courier/{courierId} | ❌ 待实现 |
| 快递员管理 | POST /admin/courier | ❌ 待实现 |
| 快递员管理 | PUT /admin/courier/{courierId} | ❌ 待实现 |
| 快递员管理 | DELETE /admin/courier/{courierId} | ❌ 待实现 |
| 快递员管理 | PUT /admin/courier/{courierId}/status | ❌ 待实现 |
| 统计分析 | GET /admin/statistics/electricity | ❌ 待实现 |
| 统计分析 | GET /admin/statistics/company-ratio | ❌ 待实现 |
| 统计分析 | GET /admin/statistics/monthly-trend | ❌ 待实现 |
| 统计分析 | GET /admin/statistics/usage-rate | ❌ 待实现 |
| 统计分析 | GET /admin/statistics/fault | ❌ 待实现 |
| 系统配置 | GET /admin/config | ❌ 待实现 |
| 系统配置 | PUT /admin/config | ❌ 待实现 |

---

## 与移动端 API 的区别

Web 管理端 API 与移动端 API 的主要区别：

1. **路径前缀**: 管理端使用 `/api/v1/admin` 前缀，移动端使用 `/api/v1`
2. **认证方式**: 管理端需要管理员账号登录，权限更高
3. **数据范围**: 管理端可查看所有用户/快递员/快递数据，移动端只能查看当前用户相关数据
4. **操作权限**: 管理端可执行删除、禁用、远程开门等高权限操作
5. **分页查询**: 管理端支持分页和多条件筛选

---

## 硬件模拟说明

以下硬件功能通过软件模拟实现：

| 硬件功能 | 模拟方式 |
|----------|----------|
| 电表数据 | 随机生成用电量数据，存入数据库 |
| 摄像头 | 无需模拟，仅记录操作日志 |
| 数字键盘 | 前端UI模拟输入 |
| 仓门开关 | 数据库状态变更 + WebSocket 推送 |
| 4G通信 | 使用HTTP通信替代 |
| 显示屏 | 前端UI展示 |
| 压力传感器 | 根据存取操作更新物品状态 |
| 温湿度传感器 | 随机生成或固定值 |

---

## 更新日志

| 日期 | 版本 | 说明 |
|------|------|------|
| 2026-01-26 | v1.0.0 | 初始版本，基于 Web 管理平台需求整理 |
