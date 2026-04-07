# Cafeteria Management System

A backend application that manages daily menus, food items, and orders for a cafeteria system.

---

## Description

This project is developed as a part of backend learning to implement real-world concepts such as layered architecture, RESTful API design, and database integration using Java and Spring Boot with PostgreSQL.

The application simulates a cafeteria system where menus, food items, and orders are managed and exposed through role-based REST APIs supporting four user roles: **Admin**, **Staff**, **Customer**, and **Delivery Staff**.

---

## Getting Started

### Dependencies And Requirements

* Java 21

* Maven

* Docker & Docker Compose (for PostgreSQL)

* Spring Boot 4.0.3

* Spring Data JPA

* PostgreSQL

* Flyway (database migrations)

* Lombok

* SpringDoc OpenAPI 3.0.2

### Installing

* Clone the repository to your local machine using :

   ```bash
   git clone https://github.com/sivasbk140/Spring_Boot_Cafteria.git
   ```

* Navigate into the project directory :

   ```bash
   cd Spring_Boot_Cafteria
   ```

* Start the PostgreSQL database using Docker Compose :

   ```bash
   docker compose up -d
   ```

* Build and run the application :

   ```bash
   ./mvnw spring-boot:run
   ```

The app starts on **`http://localhost:8080`**

### Swagger UI

Access the interactive API documentation at :

```
http://localhost:8080/swagger-ui/index.html
```

---

## Tech Stack

| Technology        | Version   |
|-------------------|-----------|
| Java              | 21        |
| Spring Boot       | 4.0.3     |
| Spring Data JPA   | —         |
| PostgreSQL        | Latest    |
| Flyway            | —         |
| Lombok            | —         |
| SpringDoc OpenAPI | 3.0.2     |
| Docker Compose    | —         |
| Maven             | —         |

---

## Project Structure

```
src/main/java/net/breezeware/SpringBootCafeteria/
├── food/
│   ├── controller/        # AdminFoodController, CustomerFoodController
│   ├── dto/               # Request & Response DTOs
│   ├── entity/            # FoodItem, FoodMenu, FoodMenuItemMap
│   ├── enumeration/       # MenuDay
│   ├── repo/              # JPA Repositories
│   └── service/           # AdminFoodService, CustomerFoodService
│
├── order/
│   ├── controller/        # AdminOrderController, CustomerOrderController,
│   │                      # StaffOrderController, DeliveryStaffOrderController
│   ├── dto/               # Request & Response DTOs
│   ├── entity/            # Order, OrderItem, OrderDeliveryMap
│   ├── enumeration/       # OrderStatus
│   ├── repo/              # JPA Repositories
│   └── service/           # AdminOrderService, CustomerOrderService,
│                          # StaffOrderService, DeliveryStaffOrderService
│
├── user/
│   ├── controller/        # AdminUserController, CustomerUserController,
│   │                      # StaffUserController, DeliveryStaffUserController
│   ├── dto/               # Request & Response DTOs
│   ├── entity/            # User, DeliveryDetail
│   ├── enumeration/       # Role
│   ├── repo/              # JPA Repositories
│   └── service/           # AdminUserService, CustomerUserService,
│                          # StaffUserService, DeliveryStaffService
│
└── exception/
    ├── AppCustomException.java
    ├── GlobalExceptionHandler.java
    └── ErrorResponse.java
```

---

## User Roles

| Role             | Description                                       |
|------------------|---------------------------------------------------|
| `ADMIN`          | Full system access — users, food, orders          |
| `STAFF`          | View and manage orders, assign delivery staff     |
| `CUSTOMER`       | Browse menus, manage cart, place and track orders |
| `DELIVERY_STAFF` | View assigned orders, mark orders as delivered    |

---

## API Overview

### User Endpoints

| Role           | Method | Endpoint                       | Description             |
|----------------|--------|--------------------------------|-------------------------|
| Admin          | POST   | `/api/admin/users/register`    | Register admin user     |
| Admin          | POST   | `/api/admin/users/login`       | Admin login             |
| Admin          | GET    | `/api/admin/users`             | Get all users           |
| Admin          | GET    | `/api/admin/users/{id}`        | Get user by ID          |
| Customer       | POST   | `/api/customer/users/register` | Register customer       |
| Customer       | POST   | `/api/customer/users/login`    | Customer login          |
| Staff          | POST   | `/api/staff/users/register`    | Register staff          |
| Staff          | POST   | `/api/staff/users/login`       | Staff login             |
| Delivery Staff | POST   | `/api/delivery/users/register` | Register delivery staff |
| Delivery Staff | POST   | `/api/delivery/users/login`    | Delivery staff login    |

---

### Food Endpoints (Admin)

| Method | Endpoint                                        | Description                  |
|--------|-------------------------------------------------|------------------------------|
| POST   | `/api/admin/food/items`                         | Create food item             |
| GET    | `/api/admin/food/items`                         | Get all food items           |
| GET    | `/api/admin/food/items/{id}`                    | Get food item by ID          |
| GET    | `/api/admin/food/items/category/{category}`     | Get food items by category   |
| GET    | `/api/admin/food/items/low-stock/{threshold}`   | Get low stock items          |
| GET    | `/api/admin/food/items/search`                  | Search food items by keyword |
| PUT    | `/api/admin/food/items/{id}`                    | Update food item             |
| DELETE | `/api/admin/food/items/{id}`                    | Delete food item             |
| POST   | `/api/admin/food/menus`                         | Create food menu             |
| GET    | `/api/admin/food/menus`                         | Get all food menus           |
| GET    | `/api/admin/food/menus/{id}`                    | Get menu by ID               |
| GET    | `/api/admin/food/menus/day/{day}`               | Get menus by day             |
| PUT    | `/api/admin/food/menus/{id}`                    | Update food menu             |
| DELETE | `/api/admin/food/menus/{id}`                    | Delete food menu             |
| POST   | `/api/admin/food/menus/{menuId}/items/{itemId}` | Add item to menu             |
| DELETE | `/api/admin/food/menus/{menuId}/items/{itemId}` | Remove item from menu        |

---

### Food Endpoints (Customer)

| Method | Endpoint                                       | Description                  |
|--------|------------------------------------------------|------------------------------|
| GET    | `/api/customer/food/menus`                     | Get all available menus      |
| GET    | `/api/customer/food/menus/day/{day}`           | Get menus for a specific day |
| GET    | `/api/customer/food/items`                     | Get available food items     |
| GET    | `/api/customer/food/items/category/{category}` | Get items by category        |
| GET    | `/api/customer/food/items/search`              | Search food items            |

---

### Order Endpoints (Customer)

| Method | Endpoint                                    | Description          |
|--------|---------------------------------------------|----------------------|
| POST   | `/api/customer/orders`                      | Place a direct order |
| GET    | `/api/customer/orders`                      | View my orders       |
| GET    | `/api/customer/orders/{orderId}`            | View order detail    |
| PUT    | `/api/customer/orders/{orderId}/cancel`     | Cancel order         |
| POST   | `/api/customer/orders/cart/add`             | Add item to cart     |
| GET    | `/api/customer/orders/cart`                 | View cart            |
| DELETE | `/api/customer/orders/cart/remove`          | Remove item from cart|
| POST   | `/api/customer/orders/cart/checkout`        | Checkout cart        |
| POST   | `/api/customer/orders/{orderId}/delivery`   | Add delivery details |

---

### Order Endpoints (Admin)

| Method | Endpoint                                           | Description           |
|--------|----------------------------------------------------|-----------------------|
| GET    | `/api/admin/orders`                                | Get all orders        |
| GET    | `/api/admin/orders/{orderId}`                      | Get order by ID       |
| GET    | `/api/admin/orders/status/{status}`                | Get orders by status  |
| GET    | `/api/admin/orders/user/{userId}`                  | Get orders by user    |
| PUT    | `/api/admin/orders/{orderId}/status`               | Update order status   |
| PUT    | `/api/admin/orders/{orderId}/assign/{staffId}`     | Assign delivery staff |
| PUT    | `/api/admin/orders/{orderId}/cancel`               | Force cancel order    |

---

### Order Endpoints (Staff)

| Method | Endpoint                                          | Description           |
|--------|---------------------------------------------------|-----------------------|
| GET    | `/api/staff/orders`                               | Get all orders        |
| GET    | `/api/staff/orders/{orderId}`                     | Get order by ID       |
| GET    | `/api/staff/orders/status/{status}`               | Get orders by status  |
| GET    | `/api/staff/orders/user/{userId}`                 | Get orders by user    |
| PUT    | `/api/staff/orders/{orderId}/status`              | Update order status   |
| PUT    | `/api/staff/orders/{orderId}/assign/{staffId}`    | Assign delivery staff |
| PUT    | `/api/staff/orders/{orderId}/cancel`              | Cancel order          |

---

### Order Endpoints (Delivery Staff)

| Method | Endpoint                                 | Description             |
|--------|------------------------------------------|-------------------------|
| GET    | `/api/delivery/orders/assigned`          | Get assigned orders     |
| PUT    | `/api/delivery/orders/{orderId}/deliver` | Mark order as delivered |

---

## Order Status Flow

```
PLACED_ORDER → ORDER_RECEIVED → ORDER_PREPARING → ASSIGNED_DELIVERY_STAFF → ORDER_DELIVERED
                                                                   ↓
                                                          ORDER_CANCELLED (at any stage)
```

---

## Database Migrations (Flyway)

| Version | Description                          |
|---------|--------------------------------------|
| V1      | Create users table                   |
| V2      | Create delivery details table        |
| V3      | Create food menu table               |
| V4      | Create availability map table        |
| V5      | Create order table                   |
| V6      | Create food item table               |
| V7      | Create order items table             |
| V8      | Create food menu items map table     |
| V10     | Insert seed values                   |
| V11     | Alter food menu — add menu_day       |
| V12     | Create order delivery map table      |
| V13     | Update order status constraint       |
| V14     | Add delivery_staff_id column         |

---

## Author

**Sivabalakrishnan**