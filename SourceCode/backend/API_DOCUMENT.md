# CQU Express Locker System API Documentation

## Base URL
`http://localhost:8080/api`

## Authentication

### Login
`POST /auth/login`
- Body: `{ username, password }`
- Response: `{ code: 200, data: { token, userInfo } }`

### Logout
`POST /auth/logout`
- Response: `{ code: 200, message: "Logged out" }`

### Get User Info
`GET /auth/userinfo`
- Headers: `Authorization: <token>`
- Response: `{ code: 200, data: { ...userInfo } }`

---

## Lockers

### Get List
`GET /locker/list`
- Query: `page`, `pageSize`, `id`, `location`
- Response: `{ code: 200, data: { list: [], total: 0 } }`

### Add Locker
`POST /locker`
- Body: `{ id, location, compartmentCount, remark }`

### Update Locker
`PUT /locker/:id`
- Body: `{ location, remark }`

### Delete Locker
`DELETE /locker/:id`

### Toggle Status
`PUT /locker/:id/status`
- Body: `{ status }`

---

## Express Orders

### Get List
`GET /express/list`
- Query: `page`, `pageSize`, `trackingNo`, `phone`, `status`

---

## Users

### Get List
`GET /user/list`
- Query: `page`, `pageSize`, `name`, `phone`

### Delete User
`DELETE /user/:id`

### Toggle Status
`PUT /user/:id/status`
- Body: `{ status }`

---

## Statistics

### Dashboard
`GET /statistics/dashboard`
- Response: `{ code: 200, data: { lockerCount, expressCount, userCount, courierCount } }`

### Trend
`GET /statistics/express-trend`

### Usage Rate
`GET /statistics/usage-rate`
