# 🍽️ YumDrop – Backend API

YumDrop is a food delivery backend system inspired by platforms like Zomato and Swiggy.
Built as an **interview-ready, production-pattern** Spring Boot backend.

---

## ⚙️ Tech Stack

| Layer     | Technology                   |
|-----------|------------------------------|
| Language  | Java 25                      |
| Framework | Spring Boot 4.x              |
| Auth      | Spring Security + JWT        |
| Database  | MySQL 8                      |
| Cache     | Redis                        |
| Messaging | RabbitMQ                     |
| API Docs  | Swagger / OpenAPI (SpringDoc)|
| Container | Docker / Docker Compose      |

---

## 🧱 Architecture

**Style:** Modular Monolith

```
Request → JWT Filter → Rate Limiter (Redis) → Controller → Service → Repository (MySQL)
                                                         ↓
                                                   RabbitMQ (async)
                                                         ↓
                                              Notification Consumer
```

---

## 🚀 Running Locally

### 1. Start infrastructure (MySQL + Redis + RabbitMQ)

```bash
docker compose up -d
```

| Service   | Port  | Notes                                      |
|-----------|-------|--------------------------------------------|
| MySQL     | 3306  | database `yumdrop`, password `root`        |
| Redis     | 6379  |                                            |
| RabbitMQ  | 5672  | Management UI → http://localhost:15672     |

### 2. Run the application

```bash
./mvnw spring-boot:run
```

App starts on **http://localhost:8084**

On startup you will see:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Application  : YumDrop
  Profile      : dev
  Port         : 8084
  Swagger UI   : http://localhost:8084/swagger-ui/index.html
  API Base     : http://localhost:8084/api
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### 3. Swagger UI

```
http://localhost:8084/swagger-ui/index.html
```

Click **Authorize** and paste your `accessToken` to test secured endpoints.

---

## 🔐 Auth & User

**Base Path:** `/api/auth`

| Method | Endpoint              | Role          | Description                        |
|--------|-----------------------|---------------|------------------------------------|
| POST   | `/api/auth/register`  | Public        | Register a new user                |
| POST   | `/api/auth/login`     | Public        | Login — returns access + refresh token |
| POST   | `/api/auth/refresh`   | Public        | Rotate refresh token               |
| GET    | `/api/auth/users/{id}`| ADMIN or self | Get user profile (no passwordHash) |
| PUT    | `/api/auth/users/{id}`| ADMIN or self | Update user                        |

**Roles:** `USER` · `ADMIN` · `SUPPORT` · `DELIVERY_PARTNER` · `RESTAURANT_OWNER`

**Login response:**
```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<uuid>"
}
```

Refresh tokens expire in **7 days** and are **rotated on every use** (old token revoked immediately).

---

## 🏪 Restaurant & Menu

**Base Path:** `/api/restaurants`

| Method | Endpoint                     | Role   | Description              |
|--------|------------------------------|--------|--------------------------|
| POST   | `/api/restaurants`           | ADMIN  | Create restaurant        |
| PUT    | `/api/restaurants/{id}`      | ADMIN  | Update restaurant        |
| PATCH  | `/api/restaurants/{id}/status` | ADMIN | Toggle open/closed      |
| GET    | `/api/restaurants`           | Public | List all restaurants     |
| GET    | `/api/restaurants/{id}`      | Public | Get restaurant by ID     |
| DELETE | `/api/restaurants/{id}`      | ADMIN  | Delete restaurant        |
| POST   | `/api/restaurants/{id}/menu` | ADMIN  | Add menu items (bulk)    |
| GET    | `/api/restaurants/{id}/menu` | Public | Get menu                 |

> Restaurant **rating is computed automatically** from submitted feedback — it cannot be set manually.

---

## 🧾 Orders

**Base Path:** `/api/orders`

| Method | Endpoint                        | Role                      | Description              |
|--------|---------------------------------|---------------------------|--------------------------|
| POST   | `/api/orders`                   | USER                      | Place an order           |
| GET    | `/api/orders`                   | USER, ADMIN               | My orders (paginated)    |
| GET    | `/api/orders/{orderId}`         | USER, SUPPORT, ADMIN      | Get order by ID          |
| GET    | `/api/orders/{orderId}/history` | USER, SUPPORT, ADMIN      | Full status audit trail  |
| POST   | `/api/orders/{orderId}/accept`  | RESTAURANT_OWNER          | Accept order             |
| POST   | `/api/orders/{orderId}/reject`  | RESTAURANT_OWNER          | Reject order             |
| POST   | `/api/orders/{orderId}/cancel`  | USER                      | Cancel order             |

**Order status flow:**
```
PLACED → ACCEPTED → DISPATCHED → DELIVERED
       ↘ REJECTED
       ↘ CANCELLED  (from PLACED or ACCEPTED)
                     ↘ ACCEPTED  (if delivery FAILED — re-dispatchable)
```

Every transition is recorded in `order_status_history` with `fromStatus`, `toStatus`, `changedBy`, and `changedAt`.

---

## 🚚 Delivery

**Base Path:** `/api/deliveries`

| Method | Endpoint                              | Role                              | Description             |
|--------|---------------------------------------|-----------------------------------|-------------------------|
| POST   | `/api/deliveries/assign`              | ADMIN                             | Assign partner to order |
| POST   | `/api/deliveries/{deliveryId}/status` | DELIVERY_PARTNER                  | Update delivery status  |
| GET    | `/api/deliveries/order/{orderId}`     | USER, ADMIN, DELIVERY_PARTNER     | Track delivery          |

**Delivery status flow:**
```
ASSIGNED → PICKED_UP → DELIVERED
                     ↘ FAILED
```

**Automatic side effects:**

| Delivery event | Order synced to | Partner synced to |
|----------------|-----------------|-------------------|
| Assigned       | `DISPATCHED`    | `ON_DELIVERY`     |
| Delivered      | `DELIVERED`     | `AVAILABLE`       |
| Failed         | `ACCEPTED`      | `AVAILABLE`       |

---

## 👷 Delivery Partners

**Base Path:** `/api/partners` — all endpoints require `ADMIN`

| Method | Endpoint                    | Description                        |
|--------|-----------------------------|------------------------------------|
| POST   | `/api/partners`             | Register partner                   |
| PUT    | `/api/partners/{id}`        | Update partner details             |
| PUT    | `/api/partners/{id}/status` | Update partner status              |
| GET    | `/api/partners/available`   | List available partners (paginated)|
| GET    | `/api/partners/{id}`        | Get partner by ID                  |
| DELETE | `/api/partners/{id}`        | Delete partner                     |

**Partner statuses:** `AVAILABLE` · `ON_DELIVERY` · `OFFLINE`

---

## ⭐ Feedback

**Base Path:** `/api/feedback`

| Method | Endpoint                       | Description                    |
|--------|--------------------------------|--------------------------------|
| POST   | `/api/feedback`                | Submit feedback (one per order)|
| GET    | `/api/feedback/order/{orderId}`| Get feedback for an order      |

- Ratings: 1–5 for both restaurant and delivery partner
- Comments: 5–100 characters
- Submitting feedback **automatically recomputes** the restaurant's average rating

---

## 🔔 Notifications

Event-driven via RabbitMQ. When an order is accepted:
1. `OrderEventProducer` publishes to `order.exchange` with routing key `order.accepted`
2. `OrderEventListener` consumes from `order.accepted.queue`
3. `EmailNotificationService` handles the notification (pluggable with real email provider)

---

## 🛡️ Security

- **Stateless JWT** — no sessions, `SessionCreationPolicy.STATELESS`
- **Refresh token rotation** — UUID-based, 7-day expiry, revoked on use
- **Role-based access** — `@PreAuthorize` on every sensitive endpoint, `@EnableMethodSecurity`
- **Rate limiting** — sliding window via Redis, 100 requests / 60 seconds per user
- **BCrypt** password hashing
- **CSRF disabled** — stateless API, no session cookies

---

## 📁 Project Structure

```
src/main/java/com/deliveratdoor/yumdrop/
├── config/          # SecurityConfig, RabbitMQConfig, RedisConfig, SwaggerConfig, StartupListener
├── controler/       # REST controllers
├── dto/             # Request / Response DTOs
├── entity/          # JPA entities
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── messaging/       # RabbitMQ producer & consumer + events
├── model/           # Enums (OrderStatus, DeliveryStatus, PartnerStatus, UserRole, PaymentMethod)
├── rateLimit/       # Sliding window rate limiter (Redis)
├── repositories/    # Spring Data JPA repositories
├── security/        # JwtAuthenticationFilter
├── service/         # Business logic
└── util/            # JwtUtil, mappers, pagination helpers
```

---

## 🧠 Roadmap

- Payment service (mock → real gateway)
- Idempotency key on order placement
- `@Cacheable` on restaurant/menu reads (Redis)
- Soft delete for restaurants and partners
- RabbitMQ events for all order/delivery transitions
- OAuth2 login (Google)
- Kubernetes deployment
