Restaurants
-----------------
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
-----------------