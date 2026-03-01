# API Documentation

Complete REST API reference for the TrendyolClone marketplace platform.

---

## Table of Contents

1. [Base URL](#base-url)
2. [Authentication](#authentication)
3. [Common Response Format](#common-response-format)
4. [Pagination](#pagination)
5. [Error Handling](#error-handling)
6. [Auth Service](#auth-service)
7. [User Service](#user-service)
8. [Address Service](#address-service)
9. [Product Service](#product-service)
10. [Category Service](#category-service)
11. [Review Service](#review-service)
12. [Seller Product Service](#seller-product-service)
13. [Cart Service](#cart-service)
14. [Order Service](#order-service)
15. [Seller Order Service](#seller-order-service)
16. [Payment Service](#payment-service)
17. [Notification Service](#notification-service)

---

## Base URL

When running via Docker Compose, all API requests go through the NGINX gateway:

```
http://localhost/api/v1
```

When running services individually for development, call each service directly:

| Service | Base URL |
|---|---|
| User Service | `http://localhost:8081/api/v1` |
| Product Service | `http://localhost:8082/api/v1` |
| Cart Service | `http://localhost:8083/api/v1` |
| Order Service | `http://localhost:8084/api/v1` |
| Payment Service | `http://localhost:8085/api/v1` |
| Notification Service | `http://localhost:8086/api/v1` |

---

## Authentication

The API uses **JWT Bearer tokens** for authentication. Include the access token in the `Authorization` header:

```
Authorization: Bearer <accessToken>
```

### Token Lifecycle

1. **Register** or **Login** to receive an `accessToken` and `refreshToken`.
2. The `accessToken` expires after **15 minutes** (configurable).
3. Use the `refreshToken` to obtain a new token pair via the `/auth/refresh` endpoint.
4. The `refreshToken` expires after **7 days** (configurable).

### Roles

| Role | Description |
|---|---|
| `BUYER` | Default customer role. Can browse, cart, order, review. |
| `SELLER` | Can manage products and fulfill orders. Has all BUYER permissions. |
| `ADMIN` | Full system access. |

Endpoints marked with **Auth: SELLER** require the authenticated user to have the `SELLER` role.

---

## Common Response Format

All API responses are wrapped in a standard `ApiResponse` envelope:

### Success Response

```json
{
  "success": true,
  "data": { ... },
  "message": "Optional success message",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

### Error Response

```json
{
  "success": false,
  "data": null,
  "message": "Description of what went wrong",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

### Paginated Response

For endpoints returning lists, `data` contains a `PagedResponse`:

```json
{
  "success": true,
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "last": false,
    "first": true
  },
  "message": null,
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

## Pagination

Paginated endpoints accept the following query parameters:

| Parameter | Default | Description |
|---|---|---|
| `page` | `0` | Zero-based page index |
| `size` | `20` | Number of items per page |
| `sort` | `createdAt,desc` | Sort field and direction (e.g., `price,asc`) |

Example:

```
GET /api/v1/products?page=0&size=10&sort=price,asc
```

---

## Error Handling

### HTTP Status Codes

| Code | Meaning |
|---|---|
| `200` | OK |
| `201` | Created |
| `400` | Bad Request (validation errors) |
| `401` | Unauthorized (missing/invalid token) |
| `403` | Forbidden (insufficient role) |
| `404` | Not Found |
| `409` | Conflict (duplicate resource) |
| `429` | Too Many Requests (rate limited) |
| `500` | Internal Server Error |

### Validation Error Response

```json
{
  "success": false,
  "data": null,
  "message": "Email must be a valid email address",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

## Auth Service

Base path: `/api/v1/auth`

### POST /auth/register

Register a new user account.

**Auth Required**: No

**Request Body**:

```json
{
  "email": "john@example.com",
  "password": "SecurePass123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+905551234567",
  "role": "BUYER"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `email` | string | Yes | Valid email format |
| `password` | string | Yes | 8-100 characters |
| `firstName` | string | Yes | Not blank |
| `lastName` | string | Yes | Not blank |
| `phone` | string | No | -- |
| `role` | string | Yes | One of: `BUYER`, `SELLER`, `ADMIN` |

**Response** (`201 Created`):

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900000
  },
  "message": "User registered successfully",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### POST /auth/login

Authenticate with email and password.

**Auth Required**: No

**Request Body**:

```json
{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `email` | string | Yes | Valid email format |
| `password` | string | Yes | Not blank |

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900000
  },
  "message": "Login successful",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### POST /auth/refresh

Refresh an expired access token using a valid refresh token.

**Auth Required**: No

**Request Body**:

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900000
  },
  "message": "Token refreshed successfully",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

## User Service

Base path: `/api/v1/users`

### GET /users/me

Get the authenticated user's profile.

**Auth Required**: Yes

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+905551234567",
    "role": "BUYER",
    "status": "ACTIVE",
    "addresses": [
      {
        "id": "660e8400-e29b-41d4-a716-446655440001",
        "title": "Home",
        "fullName": "John Doe",
        "phone": "+905551234567",
        "city": "Istanbul",
        "district": "Kadikoy",
        "fullAddress": "123 Main Street, Apt 4B",
        "postalCode": "34710",
        "isDefault": true
      }
    ],
    "createdAt": "2025-01-10T08:00:00.000"
  },
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### PUT /users/me

Update the authenticated user's profile.

**Auth Required**: Yes

**Request Body**:

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+905559876543"
}
```

**Response** (`200 OK`): Same format as `GET /users/me`.

---

### GET /users/{id}

Get a user by UUID. Primarily used for internal service-to-service calls.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | User ID |

**Response** (`200 OK`): Same format as `GET /users/me`.

---

## Address Service

Base path: `/api/v1/users/me/addresses`

### GET /users/me/addresses

List all addresses for the authenticated user.

**Auth Required**: Yes

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "title": "Home",
      "fullName": "John Doe",
      "phone": "+905551234567",
      "city": "Istanbul",
      "district": "Kadikoy",
      "fullAddress": "123 Main Street, Apt 4B",
      "postalCode": "34710",
      "isDefault": true
    }
  ],
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### POST /users/me/addresses

Add a new address.

**Auth Required**: Yes

**Request Body**:

```json
{
  "title": "Office",
  "fullName": "John Doe",
  "phone": "+905551234567",
  "city": "Istanbul",
  "district": "Sisli",
  "fullAddress": "456 Business Avenue, Floor 3",
  "postalCode": "34394",
  "isDefault": false
}
```

**Response** (`201 Created`): Returns the created `AddressResponse`.

---

### PUT /users/me/addresses/{id}

Update an existing address.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Address ID |

**Request Body**: Same as POST.

**Response** (`200 OK`): Returns the updated `AddressResponse`.

---

### DELETE /users/me/addresses/{id}

Delete an address.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Address ID |

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": null,
  "message": "Address deleted successfully",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

## Product Service

Base path: `/api/v1/products`

### GET /products

Get products with optional filters and pagination.

**Auth Required**: No

**Query Parameters**:

| Parameter | Type | Required | Description |
|---|---|---|---|
| `categoryId` | UUID | No | Filter by category |
| `brand` | string | No | Filter by brand name |
| `minPrice` | decimal | No | Minimum price |
| `maxPrice` | decimal | No | Maximum price |
| `status` | string | No | Product status (`ACTIVE`, `INACTIVE`, `DELETED`) |
| `search` | string | No | Text search query |
| `page` | int | No | Page number (default: 0) |
| `size` | int | No | Page size (default: 20) |
| `sort` | string | No | Sort field and direction (default: `createdAt,desc`) |

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "770e8400-e29b-41d4-a716-446655440010",
        "sellerId": "550e8400-e29b-41d4-a716-446655440000",
        "name": "Wireless Bluetooth Headphones",
        "description": "High-quality noise-cancelling headphones",
        "price": 299.99,
        "discountedPrice": 249.99,
        "stockQuantity": 150,
        "sku": "WBH-001",
        "status": "ACTIVE",
        "category": {
          "id": "880e8400-e29b-41d4-a716-446655440020",
          "name": "Electronics",
          "slug": "electronics",
          "children": []
        },
        "brand": "TechBrand",
        "images": [
          {
            "id": "990e8400-e29b-41d4-a716-446655440030",
            "url": "https://example.com/images/headphones-1.jpg",
            "altText": "Headphones front view",
            "sortOrder": 1
          }
        ],
        "averageRating": 4.5,
        "reviewCount": 23,
        "createdAt": "2025-01-12T14:00:00.000"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 45,
    "totalPages": 3,
    "last": false,
    "first": true
  },
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### GET /products/{id}

Get a single product by ID.

**Auth Required**: No

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Product ID |

**Response** (`200 OK`): Returns a single `ProductResponse` in the `data` field.

---

### GET /products/search

Search products using Elasticsearch full-text search.

**Auth Required**: No

**Query Parameters**:

| Parameter | Type | Required | Description |
|---|---|---|---|
| `q` | string | Yes | Search query |
| `categoryId` | UUID | No | Filter by category |
| `brand` | string | No | Filter by brand |
| `minPrice` | decimal | No | Minimum price |
| `maxPrice` | decimal | No | Maximum price |
| `page` | int | No | Page number (default: 0) |
| `size` | int | No | Page size (default: 20) |

**Response** (`200 OK`): Same paginated `ProductResponse` format as `GET /products`.

---

## Category Service

Base path: `/api/v1/categories`

### GET /categories

Get the full category tree (hierarchical structure).

**Auth Required**: No

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440020",
      "name": "Electronics",
      "slug": "electronics",
      "children": [
        {
          "id": "880e8400-e29b-41d4-a716-446655440021",
          "name": "Headphones",
          "slug": "headphones",
          "children": []
        },
        {
          "id": "880e8400-e29b-41d4-a716-446655440022",
          "name": "Smartphones",
          "slug": "smartphones",
          "children": []
        }
      ]
    },
    {
      "id": "880e8400-e29b-41d4-a716-446655440030",
      "name": "Fashion",
      "slug": "fashion",
      "children": []
    }
  ],
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### GET /categories/{slug}/products

Get products belonging to a specific category.

**Auth Required**: No

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `slug` | string | Category URL slug (e.g., `electronics`) |

**Query Parameters**: Standard pagination (`page`, `size`, `sort`).

**Response** (`200 OK`): Paginated `ProductResponse` format.

---

## Review Service

Base path: `/api/v1/products/{productId}/reviews`

### GET /products/{productId}/reviews

Get reviews for a product.

**Auth Required**: No

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `productId` | UUID | Product ID |

**Query Parameters**: Standard pagination (`page`, `size`, `sort`).

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "aa0e8400-e29b-41d4-a716-446655440040",
        "productId": "770e8400-e29b-41d4-a716-446655440010",
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "rating": 5,
        "comment": "Excellent sound quality, very comfortable!",
        "status": "APPROVED",
        "createdAt": "2025-01-14T16:00:00.000"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 23,
    "totalPages": 2,
    "last": false,
    "first": true
  },
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### POST /products/{productId}/reviews

Create a review for a product.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `productId` | UUID | Product ID |

**Request Body**:

```json
{
  "rating": 5,
  "comment": "Excellent sound quality, very comfortable!"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `rating` | integer | Yes | 1-5 |
| `comment` | string | No | -- |

**Response** (`201 Created`): Returns the created `ReviewResponse`.

---

## Seller Product Service

Base path: `/api/v1/seller/products`

All endpoints in this section require the `SELLER` role.

### GET /seller/products

Get the authenticated seller's product listing.

**Auth Required**: Yes (SELLER)

**Query Parameters**: Standard pagination (`page`, `size`, `sort`).

**Response** (`200 OK`): Paginated `ProductResponse` format.

---

### POST /seller/products

Create a new product.

**Auth Required**: Yes (SELLER)

**Request Body**:

```json
{
  "name": "Wireless Bluetooth Headphones",
  "description": "High-quality noise-cancelling headphones with 30-hour battery life",
  "price": 299.99,
  "discountedPrice": 249.99,
  "stockQuantity": 150,
  "sku": "WBH-001",
  "categoryId": "880e8400-e29b-41d4-a716-446655440021",
  "brand": "TechBrand",
  "images": [
    {
      "url": "https://example.com/images/headphones-1.jpg",
      "altText": "Headphones front view",
      "sortOrder": 1
    }
  ]
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `name` | string | Yes | Not blank |
| `description` | string | No | -- |
| `price` | decimal | Yes | Min 0.01 |
| `discountedPrice` | decimal | No | -- |
| `stockQuantity` | integer | Yes | Min 0 |
| `sku` | string | Yes | Not blank |
| `categoryId` | UUID | Yes | Valid category ID |
| `brand` | string | No | -- |
| `images` | array | No | List of image objects |
| `images[].url` | string | Yes | Image URL |
| `images[].altText` | string | No | Alt text |
| `images[].sortOrder` | integer | No | Display order |

**Response** (`201 Created`): Returns the created `ProductResponse`.

---

### PUT /seller/products/{id}

Update an existing product.

**Auth Required**: Yes (SELLER)

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Product ID (must belong to the authenticated seller) |

**Request Body**: Same fields as the create request.

**Response** (`200 OK`): Returns the updated `ProductResponse`.

---

### PATCH /seller/products/{id}/stock

Update a product's stock quantity.

**Auth Required**: Yes (SELLER)

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Product ID |

**Request Body**:

```json
{
  "quantity": 200
}
```

**Response** (`200 OK`): Returns the updated `ProductResponse`.

---

### DELETE /seller/products/{id}

Soft-delete a product (sets status to `DELETED`).

**Auth Required**: Yes (SELLER)

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Product ID |

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": null,
  "message": "Product deleted successfully",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

## Cart Service

Base path: `/api/v1/cart`

All cart endpoints require authentication. The cart is stored in Redis, keyed by the authenticated user's ID.

### GET /cart

Get the current user's cart.

**Auth Required**: Yes

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "productId": "770e8400-e29b-41d4-a716-446655440010",
        "productName": "Wireless Bluetooth Headphones",
        "productImage": "https://example.com/images/headphones-1.jpg",
        "price": 249.99,
        "quantity": 2,
        "subtotal": 499.98
      }
    ],
    "totalAmount": 499.98,
    "itemCount": 2
  },
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### POST /cart/items

Add an item to the cart.

**Auth Required**: Yes

**Request Body**:

```json
{
  "productId": "770e8400-e29b-41d4-a716-446655440010",
  "quantity": 2
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `productId` | string | Yes | Not blank |
| `quantity` | integer | Yes | Min 1, Max 10 |

**Response** (`201 Created`): Returns the updated `CartResponse`.

---

### PUT /cart/items/{productId}

Update the quantity of an item in the cart.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `productId` | string | Product ID |

**Request Body**:

```json
{
  "quantity": 3
}
```

**Response** (`200 OK`): Returns the updated `CartResponse`.

---

### DELETE /cart/items/{productId}

Remove an item from the cart.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `productId` | string | Product ID |

**Response** (`200 OK`): Returns the updated `CartResponse`.

---

### DELETE /cart

Clear the entire cart.

**Auth Required**: Yes

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": null,
  "message": "Cart cleared",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### GET /cart/summary

Get a summary of the cart (total, count, estimated delivery).

**Auth Required**: Yes

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "totalAmount": 499.98,
    "itemCount": 2,
    "estimatedDelivery": "3-5 business days"
  },
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

## Order Service

Base path: `/api/v1/orders`

### POST /orders

Create a new order from the current cart contents.

**Auth Required**: Yes

**Request Body**:

```json
{
  "shippingAddressId": "660e8400-e29b-41d4-a716-446655440001",
  "notes": "Please leave at the door"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `shippingAddressId` | UUID | Yes | Valid address ID belonging to the user |
| `notes` | string | No | -- |

**Response** (`201 Created`):

```json
{
  "success": true,
  "data": {
    "id": "bb0e8400-e29b-41d4-a716-446655440050",
    "orderNumber": "ORD-20250115-ABC123",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "CREATED",
    "items": [
      {
        "id": "cc0e8400-e29b-41d4-a716-446655440060",
        "productId": "770e8400-e29b-41d4-a716-446655440010",
        "productName": "Wireless Bluetooth Headphones",
        "sellerId": "550e8400-e29b-41d4-a716-446655440099",
        "quantity": 2,
        "unitPrice": 249.99,
        "totalPrice": 499.98,
        "status": "PENDING"
      }
    ],
    "shippingAddress": {
      "fullName": "John Doe",
      "phone": "+905551234567",
      "city": "Istanbul",
      "district": "Kadikoy",
      "fullAddress": "123 Main Street, Apt 4B",
      "postalCode": "34710"
    },
    "totalAmount": 499.98,
    "discountAmount": 0.00,
    "shippingCost": 0.00,
    "finalAmount": 499.98,
    "createdAt": "2025-01-15T10:30:00.000"
  },
  "message": "Order created successfully",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### GET /orders

List the authenticated user's orders.

**Auth Required**: Yes

**Query Parameters**: Standard pagination (`page`, `size`, `sort`). Default size is 10.

**Response** (`200 OK`): Paginated `OrderResponse` format.

---

### GET /orders/{orderNumber}

Get a specific order by its order number.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `orderNumber` | string | Order number (e.g., `ORD-20250115-ABC123`) |

**Response** (`200 OK`): Returns a single `OrderResponse`.

---

### PATCH /orders/{orderNumber}/cancel

Cancel an order. Only allowed from certain statuses (CREATED, PAYMENT_PENDING, PAYMENT_COMPLETED, PROCESSING).

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `orderNumber` | string | Order number |

**Request Body**:

```json
{
  "reason": "Changed my mind"
}
```

**Response** (`200 OK`): Returns the updated `OrderResponse` with status `CANCELLED`.

---

## Seller Order Service

Base path: `/api/v1/seller/orders`

All endpoints require the `SELLER` role.

### GET /seller/orders

Get all orders containing items from the authenticated seller.

**Auth Required**: Yes (SELLER)

**Query Parameters**: Standard pagination (`page`, `size`, `sort`). Default size is 10.

**Response** (`200 OK`): Paginated `OrderResponse` format.

---

### PATCH /seller/orders/{orderNumber}/items/{itemId}/status

Update the fulfillment status of a specific order item.

**Auth Required**: Yes (SELLER)

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `orderNumber` | string | Order number |
| `itemId` | UUID | Order item ID |

**Request Body**:

```json
{
  "status": "CONFIRMED"
}
```

| Field | Type | Required | Valid Values |
|---|---|---|---|
| `status` | string | Yes | `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED` |

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "id": "cc0e8400-e29b-41d4-a716-446655440060",
    "productId": "770e8400-e29b-41d4-a716-446655440010",
    "productName": "Wireless Bluetooth Headphones",
    "sellerId": "550e8400-e29b-41d4-a716-446655440099",
    "quantity": 2,
    "unitPrice": 249.99,
    "totalPrice": 499.98,
    "status": "CONFIRMED"
  },
  "message": "Order item status updated",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

## Payment Service

Base path: `/api/v1/payments`

### POST /payments

Initiate a new payment. Typically triggered automatically by the Payment Service when it consumes an `order.created` Kafka event, but can also be called directly.

**Auth Required**: Yes

**Request Body**:

```json
{
  "orderNumber": "ORD-20250115-ABC123",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 499.98,
  "method": "CREDIT_CARD"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `orderNumber` | string | Yes | Not blank |
| `userId` | UUID | Yes | Not null |
| `amount` | decimal | Yes | Min 0.01 |
| `method` | string | Yes | One of: `CREDIT_CARD`, `DEBIT_CARD`, `BANK_TRANSFER` |

**Response** (`201 Created`):

```json
{
  "success": true,
  "data": {
    "id": "dd0e8400-e29b-41d4-a716-446655440070",
    "orderNumber": "ORD-20250115-ABC123",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 499.98,
    "currency": "TRY",
    "method": "CREDIT_CARD",
    "status": "COMPLETED",
    "transactionId": "TXN-1705312200-XYZ789",
    "failureReason": null,
    "createdAt": "2025-01-15T10:30:00.000",
    "completedAt": "2025-01-15T10:30:01.000"
  },
  "message": "Payment initiated",
  "timestamp": "2025-01-15T10:30:01.000"
}
```

---

### GET /payments/{id}

Get a payment by its ID.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Payment ID |

**Response** (`200 OK`): Returns a single `PaymentResponse`.

---

### GET /payments/order/{orderNumber}

Get the payment associated with a specific order.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `orderNumber` | string | Order number |

**Response** (`200 OK`): Returns a single `PaymentResponse`.

---

### POST /payments/{id}/refund

Refund a completed payment. This is also triggered automatically when a `order.cancelled` Kafka event is consumed for an order that has a completed payment.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Payment ID |

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "id": "dd0e8400-e29b-41d4-a716-446655440070",
    "orderNumber": "ORD-20250115-ABC123",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "amount": 499.98,
    "currency": "TRY",
    "method": "CREDIT_CARD",
    "status": "REFUNDED",
    "transactionId": "TXN-1705312200-XYZ789",
    "failureReason": null,
    "createdAt": "2025-01-15T10:30:00.000",
    "completedAt": "2025-01-15T10:35:00.000"
  },
  "message": "Payment refunded successfully",
  "timestamp": "2025-01-15T10:35:00.000"
}
```

### Payment Status Values

| Status | Description |
|---|---|
| `PENDING` | Payment created, awaiting processing |
| `PROCESSING` | Payment is being processed |
| `COMPLETED` | Payment succeeded |
| `FAILED` | Payment failed |
| `REFUNDED` | Payment was refunded |

---

## Notification Service

Base path: `/api/v1/notifications`

Notifications are created automatically by the Notification Service when it consumes Kafka events. Users can only read and manage their own notifications through this API.

### GET /notifications

Get notifications for the authenticated user.

**Auth Required**: Yes

**Query Parameters**: Standard pagination (`page`, `size`). Default size is 20.

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "ee0e8400-e29b-41d4-a716-446655440080",
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "type": "TRANSACTIONAL",
        "channel": "ORDER_CONFIRMATION",
        "title": "Order Confirmed",
        "content": "Your order ORD-20250115-ABC123 has been placed successfully!",
        "status": "SENT",
        "isRead": false,
        "createdAt": "2025-01-15T10:30:05.000",
        "sentAt": "2025-01-15T10:30:05.000"
      },
      {
        "id": "ee0e8400-e29b-41d4-a716-446655440081",
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "type": "TRANSACTIONAL",
        "channel": "WELCOME",
        "title": "Welcome!",
        "content": "Welcome to TrendyolClone, John! Start exploring amazing products.",
        "status": "SENT",
        "isRead": true,
        "createdAt": "2025-01-10T08:00:01.000",
        "sentAt": "2025-01-10T08:00:01.000"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "last": true,
    "first": true
  },
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### PATCH /notifications/{id}/read

Mark a single notification as read.

**Auth Required**: Yes

**Path Parameters**:

| Parameter | Type | Description |
|---|---|---|
| `id` | UUID | Notification ID |

**Response** (`200 OK`): Returns the updated `NotificationResponse` with `isRead: true`.

---

### PATCH /notifications/read-all

Mark all of the authenticated user's notifications as read.

**Auth Required**: Yes

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": null,
  "message": "All notifications marked as read",
  "timestamp": "2025-01-15T10:30:00.000"
}
```

---

### GET /notifications/unread-count

Get the count of unread notifications.

**Auth Required**: Yes

**Response** (`200 OK`):

```json
{
  "success": true,
  "data": 3,
  "timestamp": "2025-01-15T10:30:00.000"
}
```

### Notification Channels

Notifications are created automatically based on Kafka events:

| Channel | Trigger Event | Description |
|---|---|---|
| `WELCOME` | `user.registered` | Welcome message for new users |
| `ORDER_CONFIRMATION` | `order.created` | Order placement confirmation |
| `ORDER_SHIPPED` | `order.shipped` | Order has been shipped |
| `ORDER_DELIVERED` | `order.delivered` | Order has been delivered |
| `PAYMENT_SUCCESS` | `payment.completed` | Payment confirmed |
| `PAYMENT_FAILED` | `payment.failed` | Payment failed |
| `STOCK_LOW` | `product.stock.low` | Low stock alert for sellers |
| `PRICE_DROP` | -- | Reserved for future use |
