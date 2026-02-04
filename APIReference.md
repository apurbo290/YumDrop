1. Restaurants
=====================
POST - http://localhost:8084/api/restaurants
{
"name": "Spice Villa",
"address": "MG Road, Bangalore",
"isOpen": true
}
----
PATCH - http://localhost:8084/api/restaurants/{id}/status
{
"isOpen": false
}
----
PUT - http://localhost:8084/api/restaurants/{id}
{
"name": "Spice Villa",
"address": "MG Road, Pune",
"isOpen": true
}
----
GET - http://localhost:8084/api/restaurants
GET - http://localhost:8084/api/restaurants/{id}
----
POST - http://localhost:8084/api/restaurants/{id}/menu
[
    {
    "name": "Chicken",
    "price": 500,
    "isAvailable": true
    }
]
----
GET - http://localhost:8084/api/restaurants/{id}/menu
=====================


2. Orders
=====================
POST - http://localhost:8084/api/orders
{
    "restaurantId": 1,
    "userId": "user_123",
    "deliveryAddress": "Flat 302, Green View Apartments, Whitefield, Bangalore",
    "paymentMethod": "COD",
    "items": [
        {
            "menuItemId": 1,
            "quantity": 2
        }
    ]
}
----
POST - http://localhost:8084/api/orders/{orderId}/accept
POST - http://localhost:8084/api/orders/{orderId}/reject
POST - http://localhost:8084/api/orders/{orderId}/cancel
GET - http://localhost:8084/api/orders/{orderId}
=====================

3. Delivery
=====================
POST - http://localhost:8084/api/deliveries/assign?orderId={orderId}&deliveryPartnerId={partnerId}
POST - http://localhost:8084/api/deliveries/{deliveryId}/status?status=ASSIGNED
GET - http://localhost:8084/api/deliveries/order/{orderId}
=====================

4. Delivery Partners
=====================
POST - http://localhost:8084/api/partners
{
"name": "Rahul Sharma",
"phone": "98765432013",
"email": "rahul.sharma@example.com"
}
----
PUT - http://localhost:8084/api/partners/{partnerId}/status
{
"status": "ON_DELIVERY"
}
----
PUT - http://localhost:8084/api/partners/{partnerId}
{
"email" : "abc@gmail.com"
}
----
GET - http://localhost:8084/api/partners/available
GET - http://localhost:8084/api/partners/{partnerId}
=====================

5. Feedback
=====================
POST - http://localhost:8084/api/feedback
   {
   "orderId": 12345,
   "userId": 987,
   "restaurantId": 55,
   "deliveryPartnerId": 21,
   "restaurantRating": 5,
   "deliveryRating": 4,
   "comments": "Food was tasty and delivery was on time."
   }
GET - http://localhost:8084/api/feedback/order/{orderId}
=====================

6. User
=====================
POST - http://localhost:8084/api/auth/register
   {
   "firstName": "Ramesh4",
   "lastName": "Kumar",
   "username": "ramesh123",
   "phoneNumber": "9876543210",
   "email": "ramesh4@example.com",
   "password": "strongPass123",
   "role" : "USER"
   }
----
PUT - http://localhost:8084/api/auth/users/{userId}
{
"role" : "ADMIN"
}
----
POST - http://localhost:8084/api/auth/login
{
"email": "<email address>",
"password": "<your password>"
}


