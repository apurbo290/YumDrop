# YumDrop – API Reference

Base URL: `http://localhost:8084`
All secured endpoints require: `Authorization: Bearer <accessToken>`

---

## 1. Auth & User

### Register
```
POST /api/auth/register
```
```json
{
  "firstName": "Ramesh",
  "lastName": "Kumar",
  "username": "ramesh123",
  "phoneNumber": "9876543210",
  "email": "ramesh@example.com",
  "password": "strongPass123",
  "role": "USER"
}
```
Roles: `USER` | `ADMIN` | `SUPPORT` | `DELIVERY_PARTNER` | `RESTAURANT_OWNER`

---

### Login
```
POST /api/auth/login
```
```json
{
  "email": "ramesh@example.com",
  "password": "strongPass123"
}
```
Response:
```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<uuid>"
}
```

---

### Refresh Token
```
POST /api/auth/refresh
```
```json
{
  "refreshToken": "<uuid>"
}
```
Returns a new `accessToken` + rotated `refreshToken`. Old refresh token is revoked.

---

### Get User  `🔒 ADMIN or self`
```
GET /api/auth/users/{id}
```

### Update User  `🔒 ADMIN or self`
```
PUT /api/auth/users/{id}
```
```json
{
  "firstName": "Ramesh",
  "email": "new@example.com",
  "password": "newPass123",
  "phoneNumber": "9876543210"
}
```
All fields optional — only provided fields are updated.

---

## 2. Restaurants  `🔒 ADMIN for writes, Public for reads`

### Create Restaurant
```
POST /api/restaurants
```
```json
{
  "name": "Spice Villa",
  "address": "MG Road, Bangalore",
  "isOpen": true
}
```

### Update Restaurant
```
PUT /api/restaurants/{id}
```
```json
{
  "name": "Spice Villa",
  "address": "MG Road, Pune",
  "isOpen": true
}
```

### Toggle Open/Closed
```
PATCH /api/restaurants/{id}/status
```
```json
{
  "isOpen": false
}
```

### Get All Restaurants
```
GET /api/restaurants
```

### Get Restaurant By ID
```
GET /api/restaurants/{id}
```

### Delete Restaurant
```
DELETE /api/restaurants/{id}
```

### Add Menu Items (bulk)
```
POST /api/restaurants/{id}/menu
```
```json
[
  { "name": "Paneer Butter Masala", "price": 250, "isAvailable": true },
  { "name": "Garlic Naan", "price": 50, "isAvailable": true }
]
```

### Get Menu
```
GET /api/restaurants/{id}/menu
```

---

## 3. Orders

### Place Order  `🔒 USER`
```
POST /api/orders
```
```json
{
  "restaurantId": 1,
  "deliveryAddress": "Flat 302, Green View Apartments, Whitefield, Bangalore",
  "paymentMethod": "COD",
  "items": [
    { "menuItemId": 1, "quantity": 2 },
    { "menuItemId": 3, "quantity": 1 }
  ]
}
```
Payment methods: `CARD` | `UPI` | `COD`

### Get My Orders (paginated)  `🔒 USER, ADMIN`
```
GET /api/orders?page=0&size=10
```

### Get Order By ID  `🔒 USER, SUPPORT, ADMIN`
```
GET /api/orders/{orderId}
```

### Get Order Status History  `🔒 USER, SUPPORT, ADMIN`
```
GET /api/orders/{orderId}/history
```
Response:
```json
[
  { "fromStatus": null, "toStatus": "PLACED", "changedBy": "user_1", "changedAt": "..." },
  { "fromStatus": "PLACED", "toStatus": "ACCEPTED", "changedBy": "SYSTEM", "changedAt": "..." },
  { "fromStatus": "ACCEPTED", "toStatus": "DISPATCHED", "changedBy": "SYSTEM", "changedAt": "..." }
]
```

### Accept Order  `🔒 RESTAURANT_OWNER`
```
POST /api/orders/{orderId}/accept
```

### Reject Order  `🔒 RESTAURANT_OWNER`
```
POST /api/orders/{orderId}/reject
```

### Cancel Order  `🔒 USER`
```
POST /api/orders/{orderId}/cancel
```

---

## 4. Delivery

### Assign Delivery Partner  `🔒 ADMIN`
```
POST /api/deliveries/assign?orderId={orderId}&deliveryPartnerId={partnerId}
```
- Order must be in `ACCEPTED` status
- Partner must be `AVAILABLE`
- On success: order → `DISPATCHED`, partner → `ON_DELIVERY`

### Update Delivery Status  `🔒 DELIVERY_PARTNER`
```
POST /api/deliveries/{deliveryId}/status?status={status}
```
Statuses: `PICKED_UP` | `DELIVERED` | `FAILED`

Side effects:
- `DELIVERED` → order moves to `DELIVERED`, partner moves to `AVAILABLE`
- `FAILED` → order moves back to `ACCEPTED`, partner moves to `AVAILABLE`

### Track Delivery  `🔒 USER, ADMIN, DELIVERY_PARTNER`
```
GET /api/deliveries/order/{orderId}
```

---

## 5. Delivery Partners  `🔒 ADMIN`

### Register Partner
```
POST /api/partners
```
```json
{
  "name": "Rahul Sharma",
  "phone": "9876543210",
  "email": "rahul@example.com"
}
```

### Update Partner
```
PUT /api/partners/{id}
```
```json
{
  "email": "newemail@example.com",
  "phone": "9999999999"
}
```

### Update Partner Status
```
PUT /api/partners/{id}/status
```
```json
{
  "status": "OFFLINE"
}
```
Statuses: `AVAILABLE` | `ON_DELIVERY` | `OFFLINE`

### Get Available Partners (paginated)
```
GET /api/partners/available?page=0&size=10
```

### Get Partner By ID
```
GET /api/partners/{id}
```

### Delete Partner
```
DELETE /api/partners/{id}
```

---

## 6. Feedback  `🔒 Authenticated`

### Submit Feedback (one per order)
```
POST /api/feedback
```
```json
{
  "orderId": 1,
  "userId": 2,
  "restaurantId": 1,
  "deliveryPartnerId": 3,
  "restaurantRating": 5,
  "deliveryRating": 4,
  "comments": "Food was great and delivery was on time."
}
```
- Ratings: 1–5
- Comments: 5–100 characters
- Submitting feedback automatically recomputes the restaurant's average rating

### Get Feedback By Order
```
GET /api/feedback/order/{orderId}
```

---

## 7. Swagger UI

```
GET http://localhost:8084/swagger-ui/index.html
```
All endpoints are documented with JWT bearer auth support. Click **Authorize** and paste your `accessToken`.
