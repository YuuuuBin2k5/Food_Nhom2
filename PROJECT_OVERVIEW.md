# 📋 Food Rescue - Tổng quan Dự án

## 🎯 Mô tả Dự án

**Food Rescue** - Nền tảng thương mại điện tử giải cứu thực phẩm sắp hết hạn, kết nối người bán (sellers), người mua (buyers), và người giao hàng (shippers).

---

## 🏗️ Kiến trúc Tổng thể

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT (Frontend)                     │
│              React 19 + Vite + TailwindCSS              │
│                   Port: 5173 (Dev)                      │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP/REST API
                     │ WebSocket
┌────────────────────▼────────────────────────────────────┐
│                    SERVER (Backend)                      │
│         Jakarta EE 10 + Hibernate + PostgreSQL          │
│                   Port: 8080 (Tomcat)                   │
└────────────────────┬────────────────────────────────────┘
                     │ JDBC
┌────────────────────▼────────────────────────────────────┐
│                      DATABASE                            │
│              PostgreSQL (Supabase Cloud)                │
└─────────────────────────────────────────────────────────┘
```

---

## 🖥️ FRONTEND (Client)

### Tech Stack

| Công nghệ | Version | Mục đích |
|-----------|---------|----------|
| **React** | 19.2.0 | UI Framework |
| **Vite** | 7.2.4 | Build Tool & Dev Server |
| **TailwindCSS** | 4.1.17 | CSS Framework |
| **React Router** | 7.10.1 | Client-side Routing |
| **Axios** | 1.13.2 | HTTP Client |
| **Lucide React** | 0.562.0 | Icon Library |
| **React Toastify** | 11.0.5 | Toast Notifications |
| **Framer Motion** | Latest | Animations |

### Cấu trúc Thư mục

```
client/
├── src/
│   ├── components/          # React Components
│   │   ├── admin/          # Admin components
│   │   ├── buyer/          # Buyer components
│   │   ├── seller/         # Seller components
│   │   ├── shipper/        # Shipper components
│   │   ├── checkout/       # Checkout flow
│   │   ├── common/         # Shared components
│   │   └── layouts/        # Layout components
│   ├── context/            # React Context (State Management)
│   │   ├── AuthContext.jsx
│   │   └── CartContext.jsx
│   ├── hooks/              # Custom React Hooks
│   │   ├── useProducts.js
│   │   ├── useOrders.js
│   │   ├── useCart.js
│   │   ├── useDebounce.js
│   │   └── ...
│   ├── pages/              # Page Components
│   │   ├── Auth/           # Login, Register, Reset Password
│   │   ├── Buyer/          # Buyer pages
│   │   ├── seller/         # Seller pages
│   │   ├── shipper/        # Shipper pages
│   │   └── admin/          # Admin pages
│   ├── services/           # API Services
│   │   ├── api.js          # Axios instance
│   │   ├── authService.js
│   │   ├── productService.js
│   │   ├── orderService.js
│   │   └── ...
│   ├── utils/              # Utility Functions
│   │   ├── apiCache.js     # API caching
│   │   ├── imageOptimization.js
│   │   ├── performanceMonitor.js
│   │   ├── format.js
│   │   └── ...
│   ├── App.jsx             # Root Component
│   ├── main.jsx            # Entry Point
│   └── index.css           # Global Styles
├── public/                 # Static Assets
├── package.json
├── vite.config.js
└── tailwind.config.js
```

### Tính năng Frontend

#### 🔐 Authentication
- JWT-based authentication
- Role-based access control (Admin, Seller, Buyer, Shipper)
- Password reset via email
- Protected routes

#### 🛒 Buyer Features
- Browse products with filters (category, price, discount)
- Search products
- Product detail view
- Shopping cart
- Checkout process
- Order history
- Real-time notifications

#### 🏪 Seller Features
- Product management (CRUD)
- Order management
- Dashboard with statistics
- Product approval status

#### 🚚 Shipper Features
- Available orders view
- Order pickup & delivery
- Delivery status updates

#### 👨‍💼 Admin Features
- User management
- Product approval
- Seller approval
- System statistics

#### ⚡ Performance Optimizations
- **API Caching**: 2-tier cache (Memory + localStorage)
- **Image Lazy Loading**: Intersection Observer
- **Request Deduplication**: Prevent duplicate API calls
- **Prefetching**: Background data loading
- **Memoization**: React.memo, useMemo, useCallback

---

## 🔧 BACKEND (Server)

### Tech Stack

| Công nghệ | Version | Mục đích |
|-----------|---------|----------|
| **Java** | 17 | Programming Language |
| **Jakarta EE** | 10.0.0 | Enterprise Framework |
| **Hibernate** | 6.4.4 | JPA Implementation (ORM) |
| **PostgreSQL** | 42.7.3 | Database Driver |
| **Gson** | 2.10.1 | JSON Serialization |
| **JWT** | 0.11.5 | Authentication Tokens |
| **BCrypt** | 0.4 | Password Hashing |
| **Jakarta Mail** | 2.0.1 | Email Service |
| **Flyway** | 9.22.0 | Database Migrations |
| **Maven** | - | Build Tool |
| **Tomcat** | 10.x | Servlet Container |

### Cấu trúc Thư mục

```
server/
├── src/main/
│   ├── java/com/ecommerce/
│   │   ├── entity/              # JPA Entities
│   │   │   ├── User.java        # Base entity
│   │   │   ├── Buyer.java
│   │   │   ├── Seller.java
│   │   │   ├── Shipper.java
│   │   │   ├── Admin.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   ├── OrderDetail.java
│   │   │   ├── Payment.java
│   │   │   ├── Review.java
│   │   │   ├── Notification.java
│   │   │   └── PasswordResetToken.java
│   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── ProductDTO.java
│   │   │   ├── ProductFilter.java
│   │   │   ├── ProductPageResponse.java
│   │   │   └── SellerDTO.java
│   │   ├── service/             # Business Logic
│   │   │   ├── ProductService.java
│   │   │   ├── OrderService.java
│   │   │   ├── UserService.java
│   │   │   └── NotificationService.java
│   │   ├── servlet/             # REST API Endpoints
│   │   │   ├── AuthServlet.java
│   │   │   ├── ProductServlet.java
│   │   │   ├── SellerOrderServlet.java
│   │   │   ├── BuyerOrderServlet.java
│   │   │   ├── ShipperOrderServlet.java
│   │   │   ├── CategoryServlet.java
│   │   │   └── ...
│   │   ├── filter/              # Servlet Filters
│   │   │   ├── JwtAuthFilter.java
│   │   │   └── CorsFilter.java
│   │   ├── websocket/           # WebSocket Endpoints
│   │   │   └── NotificationWebSocket.java
│   │   └── util/                # Utilities
│   │       ├── DBUtil.java      # EntityManager Factory
│   │       ├── JwtUtil.java     # JWT Helper
│   │       └── MailUtil.java    # Email Helper
│   └── resources/
│       ├── META-INF/
│       │   └── persistence.xml  # JPA Configuration
│       └── db/migration/        # Flyway Migrations
├── pom.xml                      # Maven Configuration
└── Dockerfile                   # Docker Configuration
```

### API Architecture

#### REST API Pattern
```
/api/{role}/{resource}
```

Ví dụ:
- `/api/products` - Public product listing
- `/api/seller/products` - Seller's products
- `/api/buyer/orders` - Buyer's orders
- `/api/shipper/orders` - Shipper's orders
- `/api/admin/users` - Admin user management

#### Authentication Flow
```
1. Login → JWT Token
2. Store token in localStorage
3. Include token in Authorization header
4. JwtAuthFilter validates token
5. Extract userId and role
6. Pass to servlet via request attributes
```

#### WebSocket
```
ws://localhost:8080/server/ws/notifications/{userId}
```
- Real-time notifications
- Order status updates
- Product approval notifications

---

## 🗄️ DATABASE

### PostgreSQL (Supabase Cloud)

#### Connection
- **Host**: aws-1-ap-northeast-1.pooler.supabase.com
- **Port**: 6543
- **Database**: postgres
- **SSL**: Required

#### Schema Overview

```sql
-- Users (Inheritance: JOINED strategy)
users (base table)
├── buyers
├── sellers
├── shippers
└── admins

-- Products
products
├── seller_id (FK → sellers)
└── category (ENUM)

-- Orders
orders
├── buyer_id (FK → buyers)
├── shipper_id (FK → shippers)
└── status (ENUM)

-- Order Details
orderDetails
├── order_id (FK → orders)
└── product_id (FK → products)

-- Others
payments
reviews
notifications
password_reset_tokens
```

#### Key Entities

| Entity | Description |
|--------|-------------|
| **User** | Base class (JOINED inheritance) |
| **Buyer** | Customer role |
| **Seller** | Shop owner role |
| **Shipper** | Delivery person role |
| **Admin** | System administrator |
| **Product** | Food items for sale |
| **Order** | Purchase orders |
| **OrderDetail** | Order line items |
| **Payment** | Payment records |
| **Review** | Product reviews |
| **Notification** | User notifications |

#### Enums

```java
// Product Status
PENDING_APPROVAL, ACTIVE, HIDDEN, REJECTED, OUT_OF_STOCK

// Order Status
PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED

// Product Category
VEGETABLES, FRUITS, MEAT, SEAFOOD, DAIRY, BAKERY, 
SNACKS, BEVERAGES, FROZEN, CANNED, CONDIMENTS, OTHER

// Payment Method
CASH, CREDIT_CARD, BANK_TRANSFER, E_WALLET

// User Role
BUYER, SELLER, SHIPPER, ADMIN
```

---

## 🔄 Data Flow

### Product Purchase Flow

```
1. Buyer browses products
   ↓
2. Add to cart (localStorage)
   ↓
3. Checkout → Create Order
   ↓
4. Order status: PENDING
   ↓
5. Seller confirms → CONFIRMED
   ↓
6. Shipper picks up → SHIPPING
   ↓
7. Shipper delivers → DELIVERED
   ↓
8. Payment processed
```

### Product Approval Flow

```
1. Seller creates product
   ↓
2. Status: PENDING_APPROVAL
   ↓
3. Admin reviews
   ↓
4. Approve → ACTIVE (visible to buyers)
   OR
   Reject → REJECTED (not visible)
```

---

## 🔐 Security

### Authentication
- **JWT Tokens**: Stateless authentication
- **BCrypt**: Password hashing (cost factor: 10)
- **Token Expiry**: 24 hours
- **Refresh**: Manual re-login required

### Authorization
- **Role-based**: Admin, Seller, Buyer, Shipper
- **Filter**: JwtAuthFilter validates all protected routes
- **Attributes**: userId and role passed to servlets

### CORS
- **Allowed Origin**: http://localhost:5173
- **Credentials**: true
- **Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Headers**: Content-Type, Authorization

---

## 📦 Build & Deployment

### Frontend

```bash
# Development
cd client
npm install
npm run dev          # Port 5173

# Production
npm run build        # Output: dist/
npm run preview      # Preview production build
```

### Backend

```bash
# Development
cd server
mvn clean install
mvn tomcat:run       # Port 8080

# Production
mvn clean package    # Output: target/server-1.0-SNAPSHOT.war
# Deploy WAR to Tomcat
```

### Docker

```dockerfile
# Backend Dockerfile available
docker build -t food-rescue-backend .
docker run -p 8080:8080 food-rescue-backend
```

---

## 🚀 Performance Optimizations

### Frontend
- ✅ API Caching (2-tier: Memory + localStorage)
- ✅ Image Lazy Loading (Intersection Observer)
- ✅ Request Deduplication (fetchingRef)
- ✅ Prefetching (Background loading)
- ✅ Component Memoization (React.memo)
- ✅ Debounced Search
- ✅ Pagination
- ✅ Abort Controllers (Cancel old requests)

### Backend
- ✅ JOIN FETCH (Avoid N+1 queries)
- ✅ Connection Pooling
- ✅ Query Optimization
- ✅ Eager Loading for relationships
- ⚠️ TODO: Redis caching
- ⚠️ TODO: Database indexes

---

## 📊 Key Metrics

### Performance
- **Frontend Load**: ~2s (first load), ~0.1s (cached)
- **API Response**: ~100-300ms (optimized), ~9s (before optimization)
- **Cache Hit Rate**: ~90%
- **Bundle Size**: TBD

### Scale
- **Users**: Multi-role (4 types)
- **Products**: Unlimited
- **Orders**: Unlimited
- **Concurrent Users**: Limited by Tomcat config

---

## 🛠️ Development Tools

### Frontend
- **ESLint**: Code linting
- **Vite**: Fast HMR
- **React DevTools**: Debugging
- **Axios Interceptors**: Request/Response logging

### Backend
- **Maven**: Dependency management
- **Hibernate**: SQL logging (show_sql=true)
- **Flyway**: Database migrations
- **Tomcat**: Servlet container

---

## 📝 API Documentation

### Authentication
```
POST /api/auth/login
POST /api/auth/register
POST /api/auth/forgot-password
POST /api/auth/reset-password
```

### Products
```
GET    /api/products              # List products (public)
GET    /api/products/{id}         # Product detail
GET    /api/seller/products       # Seller's products
POST   /api/seller/products       # Create product
PUT    /api/seller/products       # Update product
DELETE /api/seller/products/{id}  # Delete product
```

### Orders
```
GET  /api/buyer/orders            # Buyer's orders
POST /api/checkout                # Create order
PUT  /api/buyer/orders/{id}/cancel

GET  /api/seller/orders           # Seller's orders
PUT  /api/seller/orders/{id}/status

GET  /api/shipper/orders          # Shipper's orders
PUT  /api/shipper/orders/{id}/status
```

### Categories
```
GET /api/categories               # List all categories
```

### Notifications
```
GET    /api/notifications         # List notifications
GET    /api/notifications/unread-count
PUT    /api/notifications/{id}/read
PUT    /api/notifications/mark-all-read
DELETE /api/notifications/{id}
```

---

## 🎨 UI/UX Features

### Design System
- **Colors**: Orange/Amber gradient theme
- **Typography**: System fonts
- **Icons**: Lucide React
- **Animations**: Framer Motion
- **Responsive**: Mobile-first design

### Key Pages
- 🏠 Home Page (Product showcase)
- 📦 Product List (Filters, search, pagination)
- 🔍 Product Detail (Reviews, seller info)
- 🛒 Shopping Cart
- 💳 Checkout
- 📋 Order History
- 🏪 Seller Dashboard
- 🚚 Shipper Dashboard
- 👨‍💼 Admin Panel

---

## 🐛 Known Issues & TODOs

### Frontend
- ⚠️ WebSocket connection fails sometimes
- ⚠️ StrictMode causes double mounting (dev only)
- 📝 TODO: Add unit tests
- 📝 TODO: Add E2E tests
- 📝 TODO: Optimize bundle size

### Backend
- ⚠️ API response slow (9s) - Fixed with JOIN FETCH
- 📝 TODO: Add database indexes
- 📝 TODO: Implement Redis caching
- 📝 TODO: Add API rate limiting
- 📝 TODO: Add request validation
- 📝 TODO: Add API documentation (Swagger)

---

## 📚 Documentation Files

- `client/OPTIMIZATION_SUMMARY.md` - Frontend optimization overview
- `client/SELLER_OPTIMIZATION.md` - Seller loading fix
- `client/ORDERS_OPTIMIZATION.md` - Orders loading fix
- `client/DUPLICATE_CALLS_FIX.md` - Duplicate API calls fix
- `server/BACKEND_OPTIMIZATION.md` - Backend N+1 query fix

---

## 👥 Team & Roles

- **Frontend**: React, TailwindCSS, Performance optimization
- **Backend**: Jakarta EE, Hibernate, REST API
- **Database**: PostgreSQL schema design
- **DevOps**: Docker, deployment

---

## 📄 License

[Add license information]

---

## 🎯 Project Goals

1. ✅ Reduce food waste by selling near-expiry products
2. ✅ Connect sellers, buyers, and shippers
3. ✅ Provide real-time notifications
4. ✅ Ensure fast and responsive user experience
5. ✅ Scalable architecture for future growth

---

**Last Updated**: December 2024
**Version**: 1.0.0
