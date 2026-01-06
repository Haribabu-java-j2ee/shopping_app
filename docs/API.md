# API Documentation

## Base URLs
- Auth Service: `http://localhost:8081/api/v1` (local) or `https://<your-domain>/api/v1` (production)
- Order Service: `http://localhost:8082/api/v1`
- Report Service: `http://localhost:8083/api/v1`

---

## Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

---

## Auth Service API

### POST /auth/register
Register a new user.

**Request:**
```json
{
  "username": "john.doe",
  "email": "john@example.com",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "john.doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "CUSTOMER",
    "status": "ACTIVE",
    "emailVerified": false,
    "createdAt": "2024-10-16T10:30:00"
  },
  "timestamp": "2024-10-16T10:30:00"
}
```

### POST /auth/login
Authenticate and get JWT tokens.

**Request:**
```json
{
  "usernameOrEmail": "john.doe",
  "password": "Password123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "john.doe",
      "email": "john@example.com",
      "role": "CUSTOMER"
    }
  }
}
```

---

## Order Service API

### POST /orders
Create a new order (requires authentication).

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Request:**
```json
{
  "customerId": 1,
  "items": [
    {
      "productId": "PROD-001",
      "productName": "Wireless Mouse",
      "sku": "WM-2024-BLK",
      "quantity": 2,
      "unitPrice": 25.00
    }
  ],
  "shippingAddress": {
    "street": "123 Main St",
    "city": "Mumbai",
    "state": "Maharashtra",
    "zipCode": "400001",
    "country": "India"
  },
  "paymentMethod": "CREDIT_CARD",
  "notes": "Please deliver after 6 PM"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": 1,
    "orderNumber": "ORD-1729069800-ABC123",
    "customerId": 1,
    "status": "PENDING",
    "subtotal": 50.00,
    "taxAmount": 5.00,
    "shippingCost": 5.00,
    "totalAmount": 60.00,
    "items": [...],
    "createdAt": "2024-10-16T10:30:00"
  }
}
```

### GET /orders/{orderId}
Get order details.

**Response:**
```json
{
  "success": true,
  "message": "Order retrieved successfully",
  "data": {
    "id": 1,
    "orderNumber": "ORD-1729069800-ABC123",
    "status": "DELIVERED",
    ...
  }
}
```

---

## Report Service API

### GET /reports/inventory
Get inventory report.

**Response:**
```json
{
  "success": true,
  "message": "Inventory report generated",
  "data": {
    "totalProducts": 1000,
    "lowStockItems": 50,
    "outOfStock": 10,
    "reportDate": "2024-10-16"
  }
}
```

### GET /reports/sales
Get sales report.

**Query Parameters:**
- `period` (optional): "today", "week", "month", "year", "all-time"

**Response:**
```json
{
  "success": true,
  "message": "Sales report generated",
  "data": {
    "totalRevenue": 125000.50,
    "totalOrders": 2500,
    "averageOrderValue": 50.00,
    "period": "month"
  }
}
```

---

For complete API documentation with Swagger/OpenAPI, visit:
- Auth Service: http://localhost:8081/swagger-ui.html
- Order Service: http://localhost:8082/swagger-ui.html
- Report Service: http://localhost:8083/swagger-ui.html








