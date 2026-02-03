Restaurants
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


Orders
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

