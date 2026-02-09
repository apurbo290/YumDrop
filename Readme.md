# 🍽️ YumDrop – Backend API Documentation

YumDrop is a food delivery backend system inspired by platforms like Zomato and Swiggy. This project is built **for learning purposes**, focusing on real-world backend architecture using **Java, Spring Boot, SQL, MongoDB, Redis, and Kafka**.

---

## 🧱 Architecture Overview

**Style:** Modular Monolith (can be split into microservices later)

**Databases Used:**

* PostgreSQL / MySQL – transactional data
* MongoDB – search, notifications, analytics
* Redis – caching & delivery partner availability

---

## 🔐 Authentication & User Service

**Base Path:** `/api/auth`

### Register User

```
POST /api/auth/register
```

**Request Body**

```json
{
  "name": "Apurba",
  "email": "apurba@gmail.com",
  "password": "secret",
  "role": "CUSTOMER"
}
```

### Login

```
POST /api/auth/login
```

**Response**

```json
{
  "accessToken": "jwt-token",
  "refreshToken": "jwt-token"
}
```

### Get Logged-in User

```
GET /api/users/me
```

---

## 🏪 Restaurant & Menu Service

**Base Path:** `/api/restaurants`

### Create Restaurant

```
POST /api/restaurants
```

```json
{
  "name": "Spice Villa",
  "address": "Bangalore",
  "isOpen": true,
  "rating": 4.5
}
```

### Get All Restaurants

```
GET /api/restaurants
```

### Get Restaurant By ID

```
GET /api/restaurants/{restaurantId}
```

### Add Menu Item

```
POST /api/restaurants/{restaurantId}/menu
```

```json
{
  "name": "Paneer Butter Masala",
  "price": 250,
  "available": true
}
```

### Get Restaurant Menu

```
GET /api/restaurants/{restaurantId}/menu
```

---

## 🔍 Search & Discovery Service (MongoDB)

**Base Path:** `/api/search`

### Search Restaurants

```
GET /api/search/restaurants
```

**Query Params (optional)**

* `keyword`
* `cuisine`
* `openOnly=true`

Example:

```
/api/search/restaurants?keyword=spice&openOnly=true
```

---

## 🧾 Order Service

**Base Path:** `/api/orders`

### Place Order

```
POST /api/orders
```

```json
{
  "restaurantId": 1,
  "items": [
    { "menuItemId": 10, "quantity": 2 },
    { "menuItemId": 12, "quantity": 1 }
  ]
}
```

### Get Order By ID

```
GET /api/orders/{orderId}
```

### Get Orders By User

```
GET /api/orders/user/{userId}
```

### Update Order Status

```
PATCH /api/orders/{orderId}/status
```

```json
{
  "status": "OUT_FOR_DELIVERY"
}
```

**Order Status Flow**

```
PLACED → ACCEPTED → PREPARING → OUT_FOR_DELIVERY → DELIVERED
```

---

## 💳 Payment Service (Mock)

**Base Path:** `/api/payments`

### Initiate Payment

```
POST /api/payments/initiate
```

```json
{
  "orderId": 101,
  "amount": 520,
  "method": "UPI",
  "idempotencyKey": "abc-123"
}
```

### Payment Callback (Mock)

```
POST /api/payments/callback
```

---

## 🚚 Dispatch & Logistics Service

**Base Path:** `/api/dispatch`

### Assign Delivery Partner

```
POST /api/dispatch/assign/{orderId}
```

### Track Delivery

```
GET /api/dispatch/track/{orderId}
```

---

## 🔔 Notification Service (Kafka + MongoDB)

**Base Path:** `/api/notifications`

### Get User Notifications

```
GET /api/notifications/{userId}
```

Notification types:

* ORDER_PLACED
* ORDER_STATUS_UPDATED
* PAYMENT_SUCCESS

---

## 📊 Recommendation & Analytics Service (Future)

**Base Path:** `/api/recommendations`

### Get Recommendations

```
GET /api/recommendations/{userId}
```

---

## ⚙️ Tech Stack Summary

| Layer     | Technology            |
| --------- | --------------------- |
| Language  | Java 17               |
| Framework | Spring Boot 3.x       |
| Auth      | Spring Security + JWT |
| DB        | PostgreSQL / MySQL    |
| NoSQL     | MongoDB               |
| Cache     | Redis                 |
| Messaging | Kafka                 |
| API Docs  | Swagger / OpenAPI     |
| Container | Docker                |

---

## 🚀 Learning Goals

* Clean REST API design
* Polyglot persistence (SQL + Mongo)
* Event-driven architecture
* Transaction & idempotency handling
* Scalable backend patterns

---

## 🧠 Roadmap

* OAuth2 login (Google)
* Elasticsearch integration
* Real payment gateway
* Kubernetes deployment

---

**Project Name:** YumDrop
**Purpose:** Backend learning + interview-ready system design

---
Docker command:

Pull MySQL Image: docker pull mysql:8.0
Container:
docker run -d \
--name YumDrop-MySQL \
-e MYSQL_ROOT_PASSWORD=root \
-e MYSQL_ROOT_HOST=% \
-e MYSQL_DATABASE=yumdrop \
-p 3306:3306 \
mysql:8.0

Pull Redis Image: docker pull redis:latest
Container:
docker run -d \
--name YumDrop-Redis \
-p 6379:6379 \
redis:latest





