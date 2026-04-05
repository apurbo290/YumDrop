# 🍽️ YumDrop – Backend API

YumDrop is a food delivery backend system inspired by platforms like Zomato and Swiggy. Built as an **interview-ready, production-pattern** backend using Java and Spring Boot.

---

## ⚙️ Tech Stack

| Layer     | Technology                  |
|-----------|-----------------------------|
| Language  | Java 25                     |
| Framework | Spring Boot 4.x             |
| Auth      | Spring Security + JWT       |
| DB        | MySQL 8                     |
| Cache     | Redis                       |
| Messaging | RabbitMQ                    |
| API Docs  | Swagger / OpenAPI (SpringDoc)|
| Container | Docker / Docker Compose     |

---

## 🧱 Architecture

**Style:** Modular Monolith

```
User → JWT Auth → Rate Limiter (Redis) → Controller → Service → Repository (MySQL)
                                                    ↓
                                              RabbitMQ (async events)
                                                    ↓
                                           Notification Consumer
```

---

## 🚀 Running Locally

### 1. Start infrastructure

```bash
docker compose up -d
```

This starts MySQL (3306), Redis (6379), and RabbitMQ (5672 / management UI at 15672).

### 2. Run the app

```bash
./mvnw spring-boot:run
```

App starts on **http://localhost:8084**

### 3. Swagger UI

```
http://localhost:8084/swagger-ui/index.html
```

---

## 🔐 Auth & User Service

**Base Path:** `/api/auth`

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login, returns access + refresh token |
| POST | `/api/auth/refresh` | Public | Rotate refresh token |
| GET | `/api/auth/users/{id}` | ADMIN or self | Get user profile |
| PUT | `/api/auth/users/{id}` | ADMIN or self | Update user |

**Roles:** `USER`, `ADMIN`, `SUPPORT`, `DELIVERY_PARTNER`, `RESTAURANT_OWNER`

**Login response:**
```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<uuid>"
}
```

**Refresh token:** 7-day expiry, rotated on every use (old token is revoked).

---

## 🏪 Restaurant & Menu Service

**Base Path:** `/api/restaurants`

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/restaurants` | ADMIN | Create restaurant |
| PUT | `/api/restaurants/{id}` | ADMIN | Update restaurant |
| PATCH | `/api/restaurants/{id}/status` | ADMIN | Toggle open/closed |
| GET | `/api/restaurants` | Public | List all restaurants |
| GET | `/api/restaurants/{id}` | Public | Get restaurant by ID |
| DELETE | `/api/restaurants/{id}` | ADMIN | Delete restaurant |
| POST | `/api/restaurants/{id}/menu` | ADMIN | Add menu items (bulk) |
| GET | `/api/restaurants/{id}/menu` | Public | Get menu |

> Rating is **computed automatically** from feedback — it cannot be set manually.

---

## 🧾 Order Service

**Base Path:** `/api/orders`

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/orders` | USER | Place an order |
| GET | `/api/orders` | USER, ADMIN | Get current user's orders (paginated) |
| GET | `/api/orders/{orderId}` | USER, SUPPORT, ADMIN | Get order by ID |
| GET | `/api/orders/{orderId}/history` | USER, SUPPORT, ADMIN | Full status audit trail |
| POST | `/api/orders/{orderId}/accept` | RESTAURANT_OWNER | Accept order |
| POST | `/api/orders/{orderId}/reject` | RESTAURANT_OWNER | Reject order |
| POST | `/api/orders/{orderId}/cancel` | USER | Cancel order |

**Order status flow:**
```
PLACED → ACCEPTED → DISPATCHED → DELIVERED
       ↘ REJECTED
       ↘ CANCELLED (from PLACED or ACCEPTED)
                    ↘ ACCEPTED (if delivery FAILED, re-dispatchable)
```

Every transition is recorded in `order_status_history` with `fromStatus`, `toStatus`, `changedBy`, and `changedAt`.

---

## 🚚 Delivery Service

**Base Path:** `/api/deliveries`

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/deliveries/assign?orderId=&deliveryPartnerId=` | ADMIN | Assign partner to order |
| POST | `/api/deliveries/{deliveryId}/status?status=` | DELIVERY_PARTNER | Update delivery status |
| GET | `/api/deliveries/order/{orderId}` | USER, ADMIN, DELIVERY_PARTNER | Track delivery |

**Delivery status flow:**
```
ASSIGNED → PICKED_UP → DELIVERED
                     ↘ FAILED
```

**Auto side-effects on status change:**

| Delivery event | Order status synced to | Partner status synced to |
|---|---|---|
| Assigned | `DISPATCHED` | `ON_DELIVERY` |
| Delivered | `DELIVERED` | `AVAILABLE` |
| Failed | `ACCEPTED` | `AVAILABLE` |

---

## 👷 Delivery Partner Service

**Base Path:** `/api/partners` — all endpoints require `ADMIN`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/partners` | Register partner |
| PUT | `/api/partners/{id}` | Update partner details |
| PUT | `/api/partners/{id}/status` | Update partner status |
| GET | `/api/partners/available` | List available partners (paginated) |
| GET | `/api/partners/{id}` | Get partner by ID |
| DELETE | `/api/partners/{id}` | Delete partner |

**Partner statuses:** `AVAILABLE`, `ON_DELIVERY`, `OFFLINE`

---

## ⭐ Feedback Service

**Base Path:** `/api/feedback`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/feedback` | Submit feedback (one per order) |
| GET | `/api/feedback/order/{orderId}` | Get feedback for an order |

- Restaurant and delivery partner rated 1–5
- Submitting feedback **automatically recomputes** the restaurant's average rating

---

## 🔔 Notification Service

Event-driven via RabbitMQ. When an order is accepted, a message is published to `order.exchange` and consumed by `OrderEventListener`, which triggers an email notification (currently logged — pluggable with real email provider).

---

## 🛡️ Security

- Stateless JWT authentication (no sessions)
- Sliding window rate limiter via Redis — 100 requests / 60 seconds per user
- `@PreAuthorize` role-based access on all sensitive endpoints
- Passwords hashed with BCrypt
- Refresh token rotation — old token revoked on every refresh

---

## 🧠 Roadmap

- Payment service (mock → real gateway)
- Idempotency key on order placement
- `@Cacheable` on restaurant/menu reads (Redis)
- Soft delete for restaurants and partners
- Order status RabbitMQ events for all transitions
- OAuth2 login (Google)
- Kubernetes deployment

---

## 📁 Project Structure

```
src/main/java/com/deliveratdoor/yumdrop/
├── config/          # Security, RabbitMQ, Redis, Swagger
├── controler/       # REST controllers
├── dto/             # Request / Response DTOs
├── entity/          # JPA entities
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── messaging/       # RabbitMQ producer & consumer
├── model/           # Enums (OrderStatus, DeliveryStatus, etc.)
├── rateLimit/       # Sliding window rate limiter
├── repositories/    # Spring Data JPA repositories
├── security/        # JwtAuthenticationFilter
├── service/         # Business logic
└── util/            # JwtUtil, mappers, pagination
```
