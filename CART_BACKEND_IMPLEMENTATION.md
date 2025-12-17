# 🛒 CART SYSTEM - BACKEND IMPLEMENTATION

## ✅ Implemented Features

### Backend Components

#### 1. **CartService.java** - Business Logic
- ✅ Add item to cart with stock validation
- ✅ Update item quantity with availability check
- ✅ Remove item from cart
- ✅ Calculate cart total price
- ✅ Validate entire cart (check stock & existence)
- ✅ Get cart with product details

#### 2. **CartServlet.java** - REST API
Endpoints:
- `POST /api/cart/add` - Add item to cart
- `POST /api/cart/update` - Update item quantity
- `DELETE /api/cart/remove` - Remove item from cart
- `POST /api/cart/validate` - Validate cart items
- `GET /api/cart/total` - Calculate total price

#### 3. **CartItemDTO.java** - Data Transfer Object
- `productId` - Product identifier
- `quantity` - Item quantity
- `unitPrice` - Price at time of adding
- `productName` - Product reference

#### 4. **Client-side cartService.js** - Hybrid Approach
- Uses **localStorage** for offline support
- Syncs with **backend API** for validation & persistence
- Fallback to localStorage if backend unavailable

---

## API Endpoints Documentation

### 1. POST /api/cart/add
**Add item to cart**

**Request:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Đã thêm vào giỏ hàng",
  "cart": [
    {
      "productId": "1",
      "quantity": 2
    }
  ]
}
```

**Error Responses:**
- 400: Invalid quantity or insufficient stock
- 500: Server error

---

### 2. POST /api/cart/update
**Update item quantity**

**Request:**
```json
{
  "productId": 1,
  "quantity": 5
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Đã cập nhật giỏ hàng",
  "cart": [...]
}
```

**Response (Removal when quantity=0):**
- Item is automatically removed from cart

---

### 3. DELETE /api/cart/remove
**Remove item from cart**

**Request:**
```
DELETE /api/cart/remove?productId=1
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Đã xóa khỏi giỏ hàng",
  "cart": [...]
}
```

---

### 4. POST /api/cart/validate
**Validate cart items (check stock & existence)**

**Request:**
```json
{
  "items": [
    {
      "productId": "1",
      "quantity": 2
    },
    {
      "productId": "2",
      "quantity": 1
    }
  ]
}
```

**Response (All valid):**
```json
{
  "valid": true,
  "errors": []
}
```

**Response (With errors):**
```json
{
  "valid": false,
  "errors": [
    "Sản phẩm không tồn tại",
    "Sản phẩm X chỉ còn 3 cái"
  ]
}
```

---

### 5. GET /api/cart/total
**Calculate total price**

**Request:**
```
GET /api/cart/total?items=[{"productId":"1","quantity":2}]
```

**Response:**
```json
{
  "total": 250000
}
```

---

## Error Handling

### Common Error Messages:
- `"Số lượng phải lớn hơn 0"` - Invalid quantity
- `"Chỉ còn X sản phẩm trong kho"` - Insufficient stock
- `"Sản phẩm không tồn tại"` - Product not found
- `"Sản phẩm không có trong giỏ"` - Item not in cart
- `"Missing productId parameter"` - Missing required parameter

---

## Session Management

Cart is stored in **HttpSession** on server:
```java
request.getSession().setAttribute("cart", cartList);
```

- Each user has their own session cart
- Session persists for the duration of user's browser session
- Alternative: Store in database for persistence across sessions

---

## Stock Validation Flow

1. **Client adds item** → cartService.addToCart()
2. **Frontend validation** → Check quantity > 0
3. **localStorage update** → Save for offline use
4. **Backend sync** → POST /api/cart/add
5. **Server validation** → CartService validates stock
6. **DB check** → ProductService.getProductAndValidate()
7. **Session update** → Save to HttpSession
8. **Response** → Success or error

---

## Data Flow Diagram

```
Frontend (React)
     ↓
localStorage (offline cache)
     ↓
cartService.js (API calls + localStorage sync)
     ↓
Backend CartServlet
     ↓
CartService (business logic)
     ↓
ProductService (stock check)
     ↓
Database (Product table)
     ↓
HttpSession (cart storage)
```

---

## Testing the Cart System

### 1. Add item to cart:
```bash
curl -X POST http://localhost:8080/server/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'
```

### 2. Validate cart:
```bash
curl -X POST http://localhost:8080/server/api/cart/validate \
  -H "Content-Type: application/json" \
  -d '{"items": [{"productId": "1", "quantity": 2}]}'
```

### 3. Update quantity:
```bash
curl -X POST http://localhost:8080/server/api/cart/update \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 5}'
```

### 4. Remove from cart:
```bash
curl -X DELETE "http://localhost:8080/server/api/cart/remove?productId=1"
```

---

## Future Enhancements

- [ ] Persist cart to database for recovery after logout
- [ ] Cart expiration (e.g., remove items after 30 days)
- [ ] Cart sharing between devices
- [ ] Abandoned cart recovery emails
- [ ] Loyalty points calculation during checkout
- [ ] Bulk operations (add multiple items at once)
- [ ] Cart recommendations based on items

---

## Files Modified/Created

| File | Status | Changes |
|------|--------|---------|
| CartService.java | ✅ Created | New service layer |
| CartServlet.java | ✅ Created | REST API endpoints |
| CartItemDTO.java | ✅ Updated | Added unitPrice, productName |
| cartService.js | ✅ Updated | Backend API sync |

---

## CORS Configuration

✅ Configured for frontend access:
- Origin: `http://localhost:5173`
- Methods: `GET, POST, DELETE, OPTIONS`
- Credentials: Allowed

---

**Status: READY FOR TESTING** ✅
