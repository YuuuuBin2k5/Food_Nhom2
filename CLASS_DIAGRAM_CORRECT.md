# CLASS DIAGRAM - ĐÚNG VỚI CODE THỰC TẾ

## THIẾT KẾ LẠI BẢNG SHIPPER

### So sánh Diagram cũ vs Code thực tế:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           DIAGRAM CŨ (SAI)                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│  Shipper                                                                    │
│  ─────────────────                                                          │
│  -isAvailable: boolean                                                      │
│  ─────────────────                                                          │
│  +receiveOrder(): boolean        ❌ KHÔNG CÓ TRONG CODE                     │
│  +updateDeliveryStatus(): void   ❌ KHÔNG CÓ TRONG CODE                     │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                           DIAGRAM MỚI (ĐÚNG)                                │
├─────────────────────────────────────────────────────────────────────────────┤
│  Shipper                                                                    │
│  ─────────────────                                                          │
│  -isAvailable: boolean                                                      │
│  -assignedOrders: List<Order>                                               │
│  ─────────────────                                                          │
│  +isAvailable(): boolean                                                    │
│  +setAvailable(boolean): void                                               │
│  +getAssignedOrders(): List<Order>                                          │
│  +setAssignedOrders(List<Order>): void                                      │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## TOÀN BỘ CLASS DIAGRAM ĐÚNG VỚI CODE

### 1. USER (MappedSuperclass - Abstract)

```
┌─────────────────────────────────────────────┐
│              <<abstract>>                   │
│                 User                        │
├─────────────────────────────────────────────┤
│ #userId: String                             │
│ #fullName: String                           │
│ #email: String                              │
│ #password: String                           │
│ #phoneNumber: String                        │
│ #address: String                            │
│ #role: Role                                 │
│ #isBanned: boolean                          │
│ #createdDate: Date                          │
├─────────────────────────────────────────────┤
│ +getUserId(): String                        │
│ +setUserId(String): void                    │
│ +getFullName(): String                      │
│ +setFullName(String): void                  │
│ +getEmail(): String                         │
│ +setEmail(String): void                     │
│ +getPassword(): String                      │
│ +setPassword(String): void                  │
│ +getPhoneNumber(): String                   │
│ +setPhoneNumber(String): void               │
│ +getAddress(): String                       │
│ +setAddress(String): void                   │
│ +getRole(): Role                            │
│ +setRole(Role): void                        │
│ +isBanned(): boolean                        │
│ +setBanned(boolean): void                   │
│ +getCreatedDate(): Date                     │
│ +setCreatedDate(Date): void                 │
└─────────────────────────────────────────────┘
              △
              │ extends
    ┌─────────┼─────────┬─────────┐
    │         │         │         │
┌───┴───┐ ┌───┴───┐ ┌───┴───┐ ┌───┴───┐
│ Admin │ │Seller │ │ Buyer │ │Shipper│
└───────┘ └───────┘ └───────┘ └───────┘
```

---

### 2. ADMIN

```
┌─────────────────────────────────────────────┐
│                  Admin                      │
│              extends User                   │
├─────────────────────────────────────────────┤
│ (Không có attribute riêng)                  │
├─────────────────────────────────────────────┤
│ (Chỉ có constructors)                       │
└─────────────────────────────────────────────┘
```

---

### 3. SELLER

```
┌─────────────────────────────────────────────┐
│                  Seller                     │
│              extends User                   │
├─────────────────────────────────────────────┤
│ -shopName: String                           │
│ -rating: float                              │
│ -revenue: double                            │
│ -businessLicenseUrl: String                 │
│ -licenseSubmittedDate: Date                 │
│ -licenseApprovedDate: Date                  │
│ -verificationStatus: SellerStatus           │
│ -products: List<Product>                    │
├─────────────────────────────────────────────┤
│ +getShopName(): String                      │
│ +setShopName(String): void                  │
│ +getRating(): float                         │
│ +setRating(float): void                     │
│ +getRevenue(): double                       │
│ +setRevenue(double): void                   │
│ +getBusinessLicenseUrl(): String            │
│ +setBusinessLicenseUrl(String): void        │
│ +getLicenseSubmittedDate(): Date            │
│ +setLicenseSubmittedDate(Date): void        │
│ +getLicenseApprovedDate(): Date             │
│ +setLicenseApprovedDate(Date): void         │
│ +getVerificationStatus(): SellerStatus      │
│ +setVerificationStatus(SellerStatus): void  │
│ +getProducts(): List<Product>               │
│ +setProducts(List<Product>): void           │
└─────────────────────────────────────────────┘
```

---

### 4. BUYER

```
┌─────────────────────────────────────────────┐
│                  Buyer                      │
│              extends User                   │
├─────────────────────────────────────────────┤
│ -savedAddresses: List<String>               │
│ -orders: List<Order>                        │
│ -reviews: List<Review>                      │
├─────────────────────────────────────────────┤
│ +getSavedAddresses(): List<String>          │
│ +setSavedAddresses(List<String>): void      │
│ +getOrders(): List<Order>                   │
│ +setOrders(List<Order>): void               │
│ +getReviews(): List<Review>                 │
│ +setReviews(List<Review>): void             │
└─────────────────────────────────────────────┘
```

---

### 5. SHIPPER ⭐ (ĐÃ SỬA)

```
┌─────────────────────────────────────────────┐
│                 Shipper                     │
│              extends User                   │
├─────────────────────────────────────────────┤
│ -isAvailable: boolean                       │
│ -assignedOrders: List<Order>                │
├─────────────────────────────────────────────┤
│ +isAvailable(): boolean                     │
│ +setAvailable(boolean): void                │
│ +getAssignedOrders(): List<Order>           │
│ +setAssignedOrders(List<Order>): void       │
└─────────────────────────────────────────────┘
```

**LƯU Ý:** Shipper KHÔNG CÓ methods `receiveOrder()` hay `updateDeliveryStatus()` trong entity. Logic này nằm trong:
- `ShipperActionServlet.java` (Controller)
- `OrderService.java` (Service)

---

### 6. ORDER

```
┌─────────────────────────────────────────────┐
│                  Order                      │
├─────────────────────────────────────────────┤
│ -orderId: Long                              │
│ -orderDate: Date                            │
│ -shippingAddress: String                    │
│ -status: OrderStatus                        │
│ -buyer: Buyer                               │
│ -shipper: Shipper                           │
│ -orderDetails: List<OrderDetail>            │
│ -payment: Payment                           │
├─────────────────────────────────────────────┤
│ +getOrderId(): Long                         │
│ +setOrderId(Long): void                     │
│ +getOrderDate(): Date                       │
│ +setOrderDate(Date): void                   │
│ +getShippingAddress(): String               │
│ +setShippingAddress(String): void           │
│ +getStatus(): OrderStatus                   │
│ +setStatus(OrderStatus): void               │
│ +getBuyer(): Buyer                          │
│ +setBuyer(Buyer): void                      │
│ +getShipper(): Shipper                      │
│ +setShipper(Shipper): void                  │
│ +getOrderDetails(): List<OrderDetail>       │
│ +setOrderDetails(List<OrderDetail>): void   │
│ +getPayment(): Payment                      │
│ +setPayment(Payment): void                  │
└─────────────────────────────────────────────┘
```

---

### 7. ORDER DETAIL

```
┌─────────────────────────────────────────────┐
│               OrderDetail                   │
├─────────────────────────────────────────────┤
│ -orderDetailId: Long                        │
│ -quantity: int                              │
│ -priceAtPurchase: double                    │
│ -order: Order                               │
│ -product: Product                           │
├─────────────────────────────────────────────┤
│ +getOrderDetailId(): Long                   │
│ +setOrderDetailId(Long): void               │
│ +getQuantity(): int                         │
│ +setQuantity(int): void                     │
│ +getPriceAtPurchase(): double               │
│ +setPriceAtPurchase(double): void           │
│ +getOrder(): Order                          │
│ +setOrder(Order): void                      │
│ +getProduct(): Product                      │
│ +setProduct(Product): void                  │
└─────────────────────────────────────────────┘
```

---

### 8. PRODUCT

```
┌─────────────────────────────────────────────┐
│                 Product                     │
├─────────────────────────────────────────────┤
│ -productId: Long                            │
│ -name: String                               │
│ -description: String                        │
│ -originalPrice: double                      │
│ -salePrice: double                          │
│ -expirationDate: Date                       │
│ -manufactureDate: Date                      │
│ -quantity: int                              │
│ -imageUrl: String                           │
│ -createdDate: Date                          │
│ -approvedDate: Date                         │
│ -status: ProductStatus                      │
│ -category: ProductCategory                  │
│ -seller: Seller                             │
├─────────────────────────────────────────────┤
│ +getProductId(): Long                       │
│ +setProductId(Long): void                   │
│ +getName(): String                          │
│ +setName(String): void                      │
│ +getDescription(): String                   │
│ +setDescription(String): void               │
│ +getOriginalPrice(): double                 │
│ +setOriginalPrice(double): void             │
│ +getSalePrice(): double                     │
│ +setSalePrice(double): void                 │
│ +getExpirationDate(): Date                  │
│ +setExpirationDate(Date): void              │
│ +getManufactureDate(): Date                 │
│ +setManufactureDate(Date): void             │
│ +getQuantity(): int                         │
│ +setQuantity(int): void                     │
│ +getImageUrl(): String                      │
│ +setImageUrl(String): void                  │
│ +getCreatedDate(): Date                     │
│ +setCreatedDate(Date): void                 │
│ +getApprovedDate(): Date                    │
│ +setApprovedDate(Date): void                │
│ +getStatus(): ProductStatus                 │
│ +setStatus(ProductStatus): void             │
│ +getCategory(): ProductCategory             │
│ +setCategory(ProductCategory): void         │
│ +getSeller(): Seller                        │
│ +setSeller(Seller): void                    │
└─────────────────────────────────────────────┘
```

---

### 9. PAYMENT

```
┌─────────────────────────────────────────────┐
│                 Payment                     │
├─────────────────────────────────────────────┤
│ -paymentId: Long                            │
│ -paymentDate: Date                          │
│ -amount: double                             │
│ -method: PaymentMethod                      │
│ -order: Order                               │
├─────────────────────────────────────────────┤
│ +getPaymentId(): Long                       │
│ +setPaymentId(Long): void                   │
│ +getPaymentDate(): Date                     │
│ +setPaymentDate(Date): void                 │
│ +getAmount(): double                        │
│ +setAmount(double): void                    │
│ +getMethod(): PaymentMethod                 │
│ +setMethod(PaymentMethod): void             │
│ +getOrder(): Order                          │
│ +setOrder(Order): void                      │
└─────────────────────────────────────────────┘
```

---

### 10. REVIEW

```
┌─────────────────────────────────────────────┐
│                  Review                     │
├─────────────────────────────────────────────┤
│ -reviewId: Long                             │
│ -rating: int                                │
│ -comment: String                            │
│ -reviewDate: Date                           │
│ -product: Product                           │
│ -buyer: Buyer                               │
├─────────────────────────────────────────────┤
│ +getReviewId(): Long                        │
│ +setReviewId(Long): void                    │
│ +getRating(): int                           │
│ +setRating(int): void                       │
│ +getComment(): String                       │
│ +setComment(String): void                   │
│ +getReviewDate(): Date                      │
│ +setReviewDate(Date): void                  │
│ +getProduct(): Product                      │
│ +setProduct(Product): void                  │
│ +getBuyer(): Buyer                          │
│ +setBuyer(Buyer): void                      │
└─────────────────────────────────────────────┘
```

---

## ENUMERATIONS

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ <<enumeration>> │  │ <<enumeration>> │  │ <<enumeration>> │
│      Role       │  │  OrderStatus    │  │ PaymentMethod   │
├─────────────────┤  ├─────────────────┤  ├─────────────────┤
│ ADMIN           │  │ PENDING         │  │ COD             │
│ SELLER          │  │ CONFIRMED       │  │ BANKING         │
│ BUYER           │  │ SHIPPING        │  └─────────────────┘
│ SHIPPER         │  │ DELIVERED       │
└─────────────────┘  │ CANCELLED       │
                     └─────────────────┘

┌─────────────────┐  ┌─────────────────┐
│ <<enumeration>> │  │ <<enumeration>> │
│ ProductStatus   │  │  SellerStatus   │
├─────────────────┤  ├─────────────────┤
│ PENDING_APPROVAL│  │ UNVERIFIED      │
│ REJECTED        │  │ PENDING         │
│ ACTIVE          │  │ VERIFIED        │
│ SOLD_OUT        │  │ REJECTED        │
│ EXPIRED         │  └─────────────────┘
│ HIDDEN          │
└─────────────────┘
```

---

## RELATIONSHIPS (QUAN HỆ)

```
User (abstract)
    △
    │ extends
    ├── Admin
    ├── Seller ──────────────── 1 ──────── * ──── Product
    ├── Buyer ───────────────── 1 ──────── * ──── Order
    │                           1 ──────── * ──── Review
    └── Shipper ─────────────── 1 ──────── * ──── Order (assignedOrders)

Order ──────────────────────── 1 ──────── * ──── OrderDetail
Order ──────────────────────── 1 ──────── 1 ──── Payment
Order ──────────────────────── * ──────── 1 ──── Buyer
Order ──────────────────────── * ──────── 0..1 ─ Shipper

OrderDetail ─────────────────── * ──────── 1 ──── Product

Product ─────────────────────── * ──────── 1 ──── Seller
Product ─────────────────────── 1 ──────── * ──── Review

Review ──────────────────────── * ──────── 1 ──── Buyer
Review ──────────────────────── * ──────── 1 ──── Product
```

---

## TÓM TẮT THAY ĐỔI SO VỚI DIAGRAM CŨ

### SHIPPER:
| Diagram cũ | Code thực tế | Hành động |
|------------|--------------|-----------|
| `-isAvailable` | ✅ Có | Giữ nguyên |
| `+receiveOrder(): boolean` | ❌ Không có | **XÓA** |
| `+updateDeliveryStatus(): void` | ❌ Không có | **XÓA** |
| (thiếu) | `-assignedOrders: List<Order>` | **THÊM** |
| (thiếu) | `+isAvailable(): boolean` | **THÊM** |
| (thiếu) | `+setAvailable(boolean): void` | **THÊM** |
| (thiếu) | `+getAssignedOrders(): List<Order>` | **THÊM** |
| (thiếu) | `+setAssignedOrders(List<Order>): void` | **THÊM** |

### SELLER (Diagram cũ thiếu nhiều):
| Thêm mới |
|----------|
| `-businessLicenseUrl: String` |
| `-licenseSubmittedDate: Date` |
| `-licenseApprovedDate: Date` |
| `-verificationStatus: SellerStatus` |

### PRODUCT (Diagram cũ thiếu):
| Thêm mới |
|----------|
| `-imageUrl: String` |
| `-createdDate: Date` |
| `-approvedDate: Date` |
| `-category: ProductCategory` |

### BUYER (Diagram cũ thiếu):
| Thêm mới |
|----------|
| `-reviews: List<Review>` |
