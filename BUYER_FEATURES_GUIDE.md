# 🛒 BUYER FEATURES - TÌM KIẾM & GIỎ HÀNG

**Phần đảm nhiệm**: Buyer - Tìm kiếm & Giỏ hàng  
**Bao gồm**: Homepage, Product Detail Page, Shopping Cart UI + Backend Logic

---

## 📋 TỔNG QUAN CHỨC NĂNG

### 1. Homepage (Trang chủ)
- Hiển thị danh sách sản phẩm
- Tìm kiếm sản phẩm
- Lọc & sắp xếp sản phẩm
- Banner/Promotions

### 2. Product Detail Page (Chi tiết sản phẩm)
- Thông tin chi tiết sản phẩm
- Thêm vào giỏ hàng
- Xem đánh giá
- Sản phẩm liên quan

### 3. Shopping Cart (Giỏ hàng)
- Xem giỏ hàng
- Cập nhật số lượng
- Xóa sản phẩm
- Tính tổng tiền
- Checkout

---

## 🎯 CHỨC NĂNG CHI TIẾT

## 1️⃣ HOMEPAGE - DANH SÁCH SẢN PHẨM

### Frontend Components

#### `HomePage.jsx`
```jsx
import { useState, useEffect } from 'react';
import ProductCard from '../components/ProductCard';
import SearchBar from '../components/SearchBar';
import FilterPanel from '../components/FilterPanel';
import { productService } from '../services/productService';

function HomePage() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({
        search: '',
        category: '',
        minPrice: 0,
        maxPrice: 1000000,
        sortBy: 'newest'
    });
    const [pagination, setPagination] = useState({
        page: 0,
        size: 12,
        totalPages: 0
    });

    useEffect(() => {
        loadProducts();
    }, [filters, pagination.page]);

    const loadProducts = async () => {
        setLoading(true);
        try {
            const response = await productService.getProducts({
                ...filters,
                page: pagination.page,
                size: pagination.size
            });
            setProducts(response.data);
            setPagination(prev => ({
                ...prev,
                totalPages: response.totalPages
            }));
        } catch (error) {
            console.error('Error loading products:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (searchTerm) => {
        setFilters(prev => ({ ...prev, search: searchTerm }));
        setPagination(prev => ({ ...prev, page: 0 }));
    };

    const handleFilterChange = (newFilters) => {
        setFilters(prev => ({ ...prev, ...newFilters }));
        setPagination(prev => ({ ...prev, page: 0 }));
    };

    return (
        <div className="homepage">
            <SearchBar onSearch={handleSearch} />
            
            <div className="content-wrapper">
                <FilterPanel 
                    filters={filters}
                    onChange={handleFilterChange}
                />
                
                <div className="product-grid">
                    {loading ? (
                        <LoadingSpinner />
                    ) : (
                        products.map(product => (
                            <ProductCard 
                                key={product.productId}
                                product={product}
                            />
                        ))
                    )}
                </div>
            </div>
            
            <Pagination 
                currentPage={pagination.page}
                totalPages={pagination.totalPages}
                onPageChange={(page) => setPagination(prev => ({ ...prev, page }))}
            />
        </div>
    );
}
```

#### `ProductCard.jsx`
```jsx
import { useNavigate } from 'react-router-dom';

function ProductCard({ product }) {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/products/${product.productId}`);
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    };

    const calculateDiscount = () => {
        if (product.originalPrice && product.salePrice) {
            return Math.round(
                ((product.originalPrice - product.salePrice) / product.originalPrice) * 100
            );
        }
        return 0;
    };

    return (
        <div className="product-card" onClick={handleClick}>
            <div className="product-image">
                <img src={product.imageUrl || '/placeholder.png'} alt={product.name} />
                {calculateDiscount() > 0 && (
                    <span className="discount-badge">-{calculateDiscount()}%</span>
                )}
                {product.quantity === 0 && (
                    <div className="out-of-stock">Hết hàng</div>
                )}
            </div>
            
            <div className="product-info">
                <h3 className="product-name">{product.name}</h3>
                
                <div className="product-price">
                    <span className="sale-price">{formatPrice(product.salePrice)}</span>
                    {product.originalPrice > product.salePrice && (
                        <span className="original-price">{formatPrice(product.originalPrice)}</span>
                    )}
                </div>
                
                <div className="product-meta">
                    <span className="seller">{product.seller?.shopName}</span>
                    <span className="stock">Còn {product.quantity} sản phẩm</span>
                </div>
                
                {product.expirationDate && (
                    <div className="expiration">
                        HSD: {new Date(product.expirationDate).toLocaleDateString('vi-VN')}
                    </div>
                )}
            </div>
        </div>
    );
}
```

#### `SearchBar.jsx`
```jsx
import { useState } from 'react';

function SearchBar({ onSearch }) {
    const [searchTerm, setSearchTerm] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        onSearch(searchTerm);
    };

    return (
        <form className="search-bar" onSubmit={handleSubmit}>
            <input
                type="text"
                placeholder="Tìm kiếm sản phẩm..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button type="submit">
                <SearchIcon />
            </button>
        </form>
    );
}
```

#### `FilterPanel.jsx`
```jsx
function FilterPanel({ filters, onChange }) {
    const handlePriceChange = (min, max) => {
        onChange({ minPrice: min, maxPrice: max });
    };

    const handleSortChange = (sortBy) => {
        onChange({ sortBy });
    };

    return (
        <div className="filter-panel">
            <h3>Bộ lọc</h3>
            
            {/* Price Range */}
            <div className="filter-section">
                <h4>Khoảng giá</h4>
                <div className="price-range">
                    <input
                        type="number"
                        placeholder="Từ"
                        value={filters.minPrice}
                        onChange={(e) => handlePriceChange(e.target.value, filters.maxPrice)}
                    />
                    <span>-</span>
                    <input
                        type="number"
                        placeholder="Đến"
                        value={filters.maxPrice}
                        onChange={(e) => handlePriceChange(filters.minPrice, e.target.value)}
                    />
                </div>
            </div>
            
            {/* Sort Options */}
            <div className="filter-section">
                <h4>Sắp xếp</h4>
                <select 
                    value={filters.sortBy}
                    onChange={(e) => handleSortChange(e.target.value)}
                >
                    <option value="newest">Mới nhất</option>
                    <option value="price_asc">Giá thấp đến cao</option>
                    <option value="price_desc">Giá cao đến thấp</option>
                    <option value="name_asc">Tên A-Z</option>
                    <option value="expiration">Gần hết hạn</option>
                </select>
            </div>
        </div>
    );
}
```

### Backend API

#### `ProductServlet.java`
```java
@WebServlet("/api/products")
public class ProductServlet extends HttpServlet {
    
    private final ProductService productService = new ProductService();
    private final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create();
    
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) 
            throws ServletException, IOException {
        
        setAccessControlHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            // Get query parameters
            String search = request.getParameter("search");
            String minPriceStr = request.getParameter("minPrice");
            String maxPriceStr = request.getParameter("maxPrice");
            String sortBy = request.getParameter("sortBy");
            String pageStr = request.getParameter("page");
            String sizeStr = request.getParameter("size");
            
            // Parse parameters
            Double minPrice = minPriceStr != null ? Double.parseDouble(minPriceStr) : null;
            Double maxPrice = maxPriceStr != null ? Double.parseDouble(maxPriceStr) : null;
            int page = pageStr != null ? Integer.parseInt(pageStr) : 0;
            int size = sizeStr != null ? Integer.parseInt(sizeStr) : 12;
            
            // Create filter object
            ProductFilter filter = new ProductFilter();
            filter.setSearch(search);
            filter.setMinPrice(minPrice);
            filter.setMaxPrice(maxPrice);
            filter.setSortBy(sortBy);
            filter.setPage(page);
            filter.setSize(size);
            
            // Get products
            ProductPageResponse pageResponse = productService.getProducts(filter);
            
            // Return response
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(gson.toJson(pageResponse));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("success", false);
            error.addProperty("message", e.getMessage());
            response.getWriter().print(gson.toJson(error));
        }
    }
    
    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
```

#### `ProductService.java`
```java
public class ProductService {
    
    public ProductPageResponse getProducts(ProductFilter filter) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            // Build query
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
            Root<Product> product = cq.from(Product.class);
            
            // Join with Seller to get shop name
            product.fetch("seller", JoinType.LEFT);
            
            List<Predicate> predicates = new ArrayList<>();
            
            // Only show active and verified products
            predicates.add(cb.equal(product.get("status"), ProductStatus.ACTIVE));
            predicates.add(cb.equal(product.get("isVerified"), true));
            
            // Search filter
            if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
                String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
                Predicate namePredicate = cb.like(
                    cb.lower(product.get("name")), 
                    searchPattern
                );
                Predicate descPredicate = cb.like(
                    cb.lower(product.get("description")), 
                    searchPattern
                );
                predicates.add(cb.or(namePredicate, descPredicate));
            }
            
            // Price range filter
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    product.get("salePrice"), 
                    filter.getMinPrice()
                ));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    product.get("salePrice"), 
                    filter.getMaxPrice()
                ));
            }
            
            // Apply predicates
            cq.where(predicates.toArray(new Predicate[0]));
            
            // Sorting
            if (filter.getSortBy() != null) {
                switch (filter.getSortBy()) {
                    case "price_asc":
                        cq.orderBy(cb.asc(product.get("salePrice")));
                        break;
                    case "price_desc":
                        cq.orderBy(cb.desc(product.get("salePrice")));
                        break;
                    case "name_asc":
                        cq.orderBy(cb.asc(product.get("name")));
                        break;
                    case "expiration":
                        cq.orderBy(cb.asc(product.get("expirationDate")));
                        break;
                    default: // newest
                        cq.orderBy(cb.desc(product.get("productId")));
                }
            }
            
            // Execute query with pagination
            TypedQuery<Product> query = em.createQuery(cq);
            
            // Count total
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(Product.class)));
            countQuery.where(predicates.toArray(new Predicate[0]));
            Long total = em.createQuery(countQuery).getSingleResult();
            
            // Pagination
            query.setFirstResult(filter.getPage() * filter.getSize());
            query.setMaxResults(filter.getSize());
            
            List<Product> products = query.getResultList();
            
            // Convert to DTOs
            List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            
            // Build response
            ProductPageResponse response = new ProductPageResponse();
            response.setData(productDTOs);
            response.setCurrentPage(filter.getPage());
            response.setPageSize(filter.getSize());
            response.setTotalElements(total);
            response.setTotalPages((int) Math.ceil((double) total / filter.getSize()));
            
            return response;
            
        } finally {
            em.close();
        }
    }
    
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setQuantity(product.getQuantity());
        dto.setExpirationDate(product.getExpirationDate());
        dto.setManufactureDate(product.getManufactureDate());
        dto.setStatus(product.getStatus().toString());
        
        // Seller info
        if (product.getSeller() != null) {
            SellerDTO sellerDTO = new SellerDTO();
            sellerDTO.setShopName(product.getSeller().getShopName());
            sellerDTO.setRating(product.getSeller().getRating());
            dto.setSeller(sellerDTO);
        }
        
        return dto;
    }
}
```

#### DTOs

**`ProductFilter.java`**:
```java
public class ProductFilter {
    private String search;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy;
    private int page = 0;
    private int size = 12;
    
    // Getters and setters
}
```

**`ProductDTO.java`**:
```java
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private double originalPrice;
    private double salePrice;
    private int quantity;
    private Date expirationDate;
    private Date manufactureDate;
    private String status;
    private SellerDTO seller;
    
    // Getters and setters
}
```

**`ProductPageResponse.java`**:
```java
public class ProductPageResponse {
    private List<ProductDTO> data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    
    // Getters and setters
}
```

---

## 2️⃣ PRODUCT DETAIL PAGE

### Frontend Component

#### `ProductDetailPage.jsx`
```jsx
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { productService } from '../services/productService';
import { cartService } from '../services/cartService';

function ProductDetailPage() {
    const { productId } = useParams();
    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadProduct();
    }, [productId]);

    const loadProduct = async () => {
        try {
            const response = await productService.getProductById(productId);
            setProduct(response.data);
        } catch (error) {
            console.error('Error loading product:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCart = async () => {
        try {
            await cartService.addToCart({
                productId: product.productId,
                quantity: quantity
            });
            
            // Show success notification
            alert('Đã thêm vào giỏ hàng!');
        } catch (error) {
            alert('Lỗi: ' + error.message);
        }
    };

    const handleQuantityChange = (delta) => {
        const newQuantity = quantity + delta;
        if (newQuantity >= 1 && newQuantity <= product.quantity) {
            setQuantity(newQuantity);
        }
    };

    if (loading) return <LoadingSpinner />;
    if (!product) return <div>Không tìm thấy sản phẩm</div>;

    return (
        <div className="product-detail-page">
            <div className="product-images">
                <img src={product.imageUrl || '/placeholder.png'} alt={product.name} />
            </div>
            
            <div className="product-info">
                <h1>{product.name}</h1>
                
                <div className="seller-info">
                    <span>Cửa hàng: {product.seller?.shopName}</span>
                    <span>⭐ {product.seller?.rating}/5</span>
                </div>
                
                <div className="price-section">
                    <div className="sale-price">
                        {formatPrice(product.salePrice)}
                    </div>
                    {product.originalPrice > product.salePrice && (
                        <>
                            <div className="original-price">
                                {formatPrice(product.originalPrice)}
                            </div>
                            <div className="discount-badge">
                                -{calculateDiscount(product)}%
                            </div>
                        </>
                    )}
                </div>
                
                <div className="product-meta">
                    <div className="meta-item">
                        <strong>Tình trạng:</strong>
                        {product.quantity > 0 ? (
                            <span className="in-stock">Còn {product.quantity} sản phẩm</span>
                        ) : (
                            <span className="out-of-stock">Hết hàng</span>
                        )}
                    </div>
                    
                    {product.expirationDate && (
                        <div className="meta-item">
                            <strong>Hạn sử dụng:</strong>
                            <span>{formatDate(product.expirationDate)}</span>
                        </div>
                    )}
                    
                    {product.manufactureDate && (
                        <div className="meta-item">
                            <strong>Ngày sản xuất:</strong>
                            <span>{formatDate(product.manufactureDate)}</span>
                        </div>
                    )}
                </div>
                
                <div className="quantity-selector">
                    <label>Số lượng:</label>
                    <div className="quantity-controls">
                        <button onClick={() => handleQuantityChange(-1)}>-</button>
                        <input 
                            type="number" 
                            value={quantity}
                            onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
                            min="1"
                            max={product.quantity}
                        />
                        <button onClick={() => handleQuantityChange(1)}>+</button>
                    </div>
                </div>
                
                <div className="action-buttons">
                    <button 
                        className="add-to-cart-btn"
                        onClick={handleAddToCart}
                        disabled={product.quantity === 0}
                    >
                        Thêm vào giỏ hàng
                    </button>
                    <button className="buy-now-btn">
                        Mua ngay
                    </button>
                </div>
            </div>
            
            <div className="product-description">
                <h2>Mô tả sản phẩm</h2>
                <p>{product.description}</p>
            </div>
        </div>
    );
}
```

### Backend API

#### `ProductServlet.java` (thêm method)
```java
@Override
protected void doGet(HttpServletRequest request, 
                    HttpServletResponse response) 
        throws ServletException, IOException {
    
    String pathInfo = request.getPathInfo();
    
    if (pathInfo != null && pathInfo.length() > 1) {
        // GET /api/products/{id}
        String productId = pathInfo.substring(1);
        getProductById(productId, response);
    } else {
        // GET /api/products (list with filters)
        getProducts(request, response);
    }
}

private void getProductById(String productId, HttpServletResponse response) 
        throws IOException {
    
    setAccessControlHeaders(response);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    try (PrintWriter out = response.getWriter()) {
        Long id = Long.parseLong(productId);
        ProductDTO product = productService.getProductById(id);
        
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", true);
        jsonResponse.add("data", gson.toJsonTree(product));
        
        response.setStatus(HttpServletResponse.SC_OK);
        out.print(gson.toJson(jsonResponse));
        
    } catch (NumberFormatException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        // Error response
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        // Error response
    }
}
```

#### `ProductService.java` (thêm method)
```java
public ProductDTO getProductById(Long productId) throws Exception {
    EntityManager em = DBUtil.getEmFactory().createEntityManager();
    
    try {
        Product product = em.find(Product.class, productId);
        
        if (product == null) {
            throw new Exception("Product not found: " + productId);
        }
        
        // Initialize lazy-loaded seller
        product.getSeller().getShopName();
        
        return convertToDTO(product);
        
    } finally {
        em.close();
    }
}
```

---

## 3️⃣ SHOPPING CART

### Frontend Components

#### `ShoppingCartPage.jsx`
```jsx
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartService } from '../services/cartService';

function ShoppingCartPage() {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = async () => {
        try {
            const items = await cartService.getCart();
            setCartItems(items);
        } catch (error) {
            console.error('Error loading cart:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateQuantity = async (productId, newQuantity) => {
        try {
            await cartService.updateQuantity(productId, newQuantity);
            loadCart(); // Reload cart
        } catch (error) {
            alert('Lỗi: ' + error.message);
        }
    };

    const handleRemoveItem = async (productId) => {
        try {
            await cartService.removeFromCart(productId);
            loadCart(); // Reload cart
        } catch (error) {
            alert('Lỗi: ' + error.message);
        }
    };

    const calculateTotal = () => {
        return cartItems.reduce((total, item) => {
            return total + (item.product.salePrice * item.quantity);
        }, 0);
    };

    const handleCheckout = () => {
        if (cartItems.length === 0) {
            alert('Giỏ hàng trống!');
            return;
        }
        navigate('/checkout');
    };

    if (loading) return <LoadingSpinner />;

    return (
        <div className="shopping-cart-page">
            <h1>Giỏ hàng của bạn</h1>
            
            {cartItems.length === 0 ? (
                <div className="empty-cart">
                    <p>Giỏ hàng trống</p>
                    <button onClick={() => navigate('/')}>
                        Tiếp tục mua sắm
                    </button>
                </div>
            ) : (
                <>
                    <div className="cart-items">
                        {cartItems.map(item => (
                            <CartItem
                                key={item.product.productId}
                                item={item}
                                onUpdateQuantity={handleUpdateQuantity}
                                onRemove={handleRemoveItem}
                            />
                        ))}
                    </div>
                    
                    <div className="cart-summary">
                        <div className="summary-row">
                            <span>Tạm tính:</span>
                            <span>{formatPrice(calculateTotal())}</span>
                        </div>
                        <div className="summary-row total">
                            <span>Tổng cộng:</span>
                            <span>{formatPrice(calculateTotal())}</span>
                        </div>
                        
                        <button 
                            className="checkout-btn"
                            onClick={handleCheckout}
                        >
                            Tiến hành thanh toán
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}
```

#### `CartItem.jsx`
```jsx
function CartItem({ item, onUpdateQuantity, onRemove }) {
    const { product, quantity } = item;

    const handleQuantityChange = (delta) => {
        const newQuantity = quantity + delta;
        if (newQuantity >= 1 && newQuantity <= product.quantity) {
            onUpdateQuantity(product.productId, newQuantity);
        }
    };

    return (
        <div className="cart-item">
            <img src={product.imageUrl || '/placeholder.png'} alt={product.name} />
            
            <div className="item-info">
                <h3>{product.name}</h3>
                <p className="seller">{product.seller?.shopName}</p>
            </div>
            
            <div className="item-price">
                {formatPrice(product.salePrice)}
            </div>
            
            <div className="item-quantity">
                <button onClick={() => handleQuantityChange(-1)}>-</button>
                <span>{quantity}</span>
                <button onClick={() => handleQuantityChange(1)}>+</button>
            </div>
            
            <div className="item-total">
                {formatPrice(product.salePrice * quantity)}
            </div>
            
            <button 
                className="remove-btn"
                onClick={() => onRemove(product.productId)}
            >
                ×
            </button>
        </div>
    );
}
```

### Backend - Cart Management

**Có 2 cách implement giỏ hàng:**

#### **Cách 1: Client-side Cart (Lưu trong localStorage)**

**Ưu điểm:**
- Đơn giản, không cần backend API
- Nhanh, không cần gọi server
- Không cần đăng nhập

**Nhược điểm:**
- Mất khi clear browser
- Không đồng bộ giữa các thiết bị

**Implementation:**

```javascript
// cartService.js
class CartService {
    constructor() {
        this.CART_KEY = 'shopping_cart';
    }

    getCart() {
        const cart = localStorage.getItem(this.CART_KEY);
        return cart ? JSON.parse(cart) : [];
    }

    addToCart(item) {
        const cart = this.getCart();
        const existingIndex = cart.findIndex(
            i => i.product.productId === item.productId
        );

        if (existingIndex >= 0) {
            cart[existingIndex].quantity += item.quantity;
        } else {
            cart.push({
                product: item,
                quantity: item.quantity
            });
        }

        localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
        return cart;
    }

    updateQuantity(productId, quantity) {
        const cart = this.getCart();
        const index = cart.findIndex(
            i => i.product.productId === productId
        );

        if (index >= 0) {
            cart[index].quantity = quantity;
            localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
        }

        return cart;
    }

    removeFromCart(productId) {
        let cart = this.getCart();
        cart = cart.filter(i => i.product.productId !== productId);
        localStorage.setItem(this.CART_KEY, JSON.stringify(cart));
        return cart;
    }

    clearCart() {
        localStorage.removeItem(this.CART_KEY);
    }
}

export const cartService = new CartService();
```

#### **Cách 2: Server-side Cart (Lưu trong Database)**

**Ưu điểm:**
- Đồng bộ giữa các thiết bị
- Persistent, không mất dữ liệu
- Có thể phân tích hành vi người dùng

**Nhược điểm:**
- Phức tạp hơn
- Cần đăng nhập
- Cần thêm API

**Database Schema:**

```java
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;
    
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private int quantity;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedDate;
    
    // Getters and setters
}
```

**Backend API:**

```java
@WebServlet("/api/cart")
public class CartServlet extends HttpServlet {
    
    private final CartService cartService = new CartService();
    
    // GET /api/cart - Get cart items
    @Override
    protected void doGet(HttpServletRequest request, 
                        HttpServletResponse response) {
        String userId = getUserIdFromToken(request);
        List<CartItemDTO> items = cartService.getCartItems(userId);
        // Return JSON
    }
    
    // POST /api/cart - Add to cart
    @Override
    protected void doPost(HttpServletRequest request, 
                         HttpServletResponse response) {
        String userId = getUserIdFromToken(request);
        AddToCartRequest req = gson.fromJson(reader, AddToCartRequest.class);
        cartService.addToCart(userId, req.getProductId(), req.getQuantity());
        // Return success
    }
    
    // PUT /api/cart/{productId} - Update quantity
    @Override
    protected void doPut(HttpServletRequest request, 
                        HttpServletResponse response) {
        String userId = getUserIdFromToken(request);
        Long productId = getProductIdFromPath(request);
        UpdateQuantityRequest req = gson.fromJson(reader, UpdateQuantityRequest.class);
        cartService.updateQuantity(userId, productId, req.getQuantity());
        // Return success
    }
    
    // DELETE /api/cart/{productId} - Remove from cart
    @Override
    protected void doDelete(HttpServletRequest request, 
                           HttpServletResponse response) {
        String userId = getUserIdFromToken(request);
        Long productId = getProductIdFromPath(request);
        cartService.removeFromCart(userId, productId);
        // Return success
    }
}
```

**CartService.java:**

```java
public class CartService {
    
    public List<CartItemDTO> getCartItems(String buyerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            TypedQuery<CartItem> query = em.createQuery(
                "SELECT c FROM CartItem c " +
                "JOIN FETCH c.product p " +
                "JOIN FETCH p.seller " +
                "WHERE c.buyer.userId = :buyerId",
                CartItem.class
            );
            query.setParameter("buyerId", buyerId);
            
            List<CartItem> items = query.getResultList();
            
            return items.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        } finally {
            em.close();
        }
    }
    
    public void addToCart(String buyerId, Long productId, int quantity) 
            throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Buyer buyer = em.find(Buyer.class, buyerId);
            Product product = em.find(Product.class, productId);
            
            if (product == null) {
                throw new Exception("Product not found");
            }
            
            if (product.getQuantity() < quantity) {
                throw new Exception("Not enough stock");
            }
            
            // Check if already in cart
            TypedQuery<CartItem> query = em.createQuery(
                "SELECT c FROM CartItem c " +
                "WHERE c.buyer.userId = :buyerId " +
                "AND c.product.productId = :productId",
                CartItem.class
            );
            query.setParameter("buyerId", buyerId);
            query.setParameter("productId", productId);
            
            try {
                CartItem existing = query.getSingleResult();
                existing.setQuantity(existing.getQuantity() + quantity);
                em.merge(existing);
            } catch (NoResultException e) {
                CartItem newItem = new CartItem();
                newItem.setBuyer(buyer);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                newItem.setAddedDate(new Date());
                em.persist(newItem);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void updateQuantity(String buyerId, Long productId, int quantity) 
            throws Exception {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            TypedQuery<CartItem> query = em.createQuery(
                "SELECT c FROM CartItem c " +
                "WHERE c.buyer.userId = :buyerId " +
                "AND c.product.productId = :productId",
                CartItem.class
            );
            query.setParameter("buyerId", buyerId);
            query.setParameter("productId", productId);
            
            CartItem item = query.getSingleResult();
            
            if (quantity <= 0) {
                em.remove(item);
            } else {
                item.setQuantity(quantity);
                em.merge(item);
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void removeFromCart(String buyerId, Long productId) 
            throws Exception {
        updateQuantity(buyerId, productId, 0);
    }
    
    public void clearCart(String buyerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            em.createQuery(
                "DELETE FROM CartItem c WHERE c.buyer.userId = :buyerId"
            )
            .setParameter("buyerId", buyerId)
            .executeUpdate();
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }
}
```

---

## 📊 LUỒNG XỬ LÝ TỔNG QUAN

### 1. Luồng tìm kiếm & lọc sản phẩm

```
User nhập từ khóa tìm kiếm
    ↓
SearchBar component
    ↓
HomePage setState (filters.search)
    ↓
useEffect trigger
    ↓
productService.getProducts(filters)
    ↓
axios.get('/api/products?search=...&page=0&size=12')
    ↓
ProductServlet.doGet()
    ↓
ProductService.getProducts(filter)
    ↓
Build CriteriaQuery với predicates
    ↓
Execute query với pagination
    ↓
Convert entities to DTOs
    ↓
Return ProductPageResponse
    ↓
Frontend nhận data
    ↓
Render ProductCard components
```

### 2. Luồng xem chi tiết sản phẩm

```
User click vào ProductCard
    ↓
navigate('/products/:id')
    ↓
ProductDetailPage mount
    ↓
useEffect → loadProduct()
    ↓
productService.getProductById(id)
    ↓
axios.get('/api/products/:id')
    ↓
ProductServlet.doGet() with pathInfo
    ↓
ProductService.getProductById(id)
    ↓
em.find(Product.class, id)
    ↓
Initialize lazy-loaded seller
    ↓
Convert to DTO
    ↓
Return product data
    ↓
Frontend render product details
```

### 3. Luồng thêm vào giỏ hàng

**Client-side Cart:**
```
User click "Thêm vào giỏ hàng"
    ↓
handleAddToCart()
    ↓
cartService.addToCart(product, quantity)
    ↓
Get cart from localStorage
    ↓
Check if product exists
    ↓
Update quantity or add new item
    ↓
Save to localStorage
    ↓
Show success notification
```

**Server-side Cart:**
```
User click "Thêm vào giỏ hàng"
    ↓
handleAddToCart()
    ↓
cartService.addToCart(productId, quantity)
    ↓
axios.post('/api/cart', { productId, quantity })
    ↓
CartServlet.doPost()
    ↓
Extract userId from JWT token
    ↓
CartService.addToCart(userId, productId, quantity)
    ↓
Begin transaction
    ↓
Find Buyer and Product
    ↓
Check stock availability
    ↓
Check if already in cart
    ↓
Update existing or create new CartItem
    ↓
Commit transaction
    ↓
Return success
    ↓
Frontend show notification
```

### 4. Luồng xem giỏ hàng

**Client-side:**
```
Navigate to /cart
    ↓
ShoppingCartPage mount
    ↓
loadCart()
    ↓
cartService.getCart()
    ↓
Get from localStorage
    ↓
Parse JSON
    ↓
Render cart items
```

**Server-side:**
```
Navigate to /cart
    ↓
ShoppingCartPage mount
    ↓
loadCart()
    ↓
cartService.getCart()
    ↓
axios.get('/api/cart')
    ↓
CartServlet.doGet()
    ↓
Extract userId from JWT
    ↓
CartService.getCartItems(userId)
    ↓
Query CartItem with JOIN FETCH
    ↓
Convert to DTOs
    ↓
Return cart items
    ↓
Frontend render
```

### 5. Luồng checkout

```
User click "Tiến hành thanh toán"
    ↓
navigate('/checkout')
    ↓
CheckoutPage
    ↓
Load cart items
    ↓
User nhập địa chỉ, chọn payment method
    ↓
Submit checkout form
    ↓
axios.post('/api/checkout', checkoutData)
    ↓
CheckoutServlet.doPost()
    ↓
OrderService.placeOrder()
    ↓
[Đã có sẵn - xem code_analysis_report.md]
    ↓
Clear cart (localStorage hoặc database)
    ↓
Navigate to order success page
```

---

## 📝 CHECKLIST IMPLEMENTATION

### Backend

- [ ] **Entity CartItem** (nếu dùng server-side cart)
  - [ ] Create CartItem.java
  - [ ] Add relationship với Buyer và Product
  
- [ ] **ProductServlet**
  - [ ] GET /api/products (list with filters)
  - [ ] GET /api/products/:id (detail)
  
- [ ] **ProductService**
  - [ ] getProducts(filter) với CriteriaBuilder
  - [ ] getProductById(id)
  - [ ] convertToDTO()
  
- [ ] **DTOs**
  - [ ] ProductFilter.java
  - [ ] ProductDTO.java
  - [ ] ProductPageResponse.java
  - [ ] SellerDTO.java
  
- [ ] **CartServlet** (nếu dùng server-side)
  - [ ] GET /api/cart
  - [ ] POST /api/cart
  - [ ] PUT /api/cart/:productId
  - [ ] DELETE /api/cart/:productId
  
- [ ] **CartService** (nếu dùng server-side)
  - [ ] getCartItems(userId)
  - [ ] addToCart(userId, productId, quantity)
  - [ ] updateQuantity(userId, productId, quantity)
  - [ ] removeFromCart(userId, productId)
  - [ ] clearCart(userId)

### Frontend

- [ ] **Services**
  - [ ] productService.js
  - [ ] cartService.js (client-side hoặc server-side)
  
- [ ] **Pages**
  - [ ] HomePage.jsx
  - [ ] ProductDetailPage.jsx
  - [ ] ShoppingCartPage.jsx
  
- [ ] **Components**
  - [ ] ProductCard.jsx
  - [ ] SearchBar.jsx
  - [ ] FilterPanel.jsx
  - [ ] CartItem.jsx
  - [ ] Pagination.jsx
  - [ ] LoadingSpinner.jsx
  
- [ ] **Styling**
  - [ ] homepage.css
  - [ ] product-detail.css
  - [ ] shopping-cart.css
  - [ ] product-card.css

### Testing

- [ ] Test tìm kiếm sản phẩm
- [ ] Test lọc theo giá
- [ ] Test sắp xếp
- [ ] Test pagination
- [ ] Test xem chi tiết sản phẩm
- [ ] Test thêm vào giỏ hàng
- [ ] Test cập nhật số lượng
- [ ] Test xóa khỏi giỏ hàng
- [ ] Test checkout

---

## 🎯 KHUYẾN NGHỊ

### 1. Chọn Cart Implementation

**Đề xuất: Client-side Cart (localStorage)**

**Lý do:**
- Đơn giản, dễ implement
- Không cần authentication cho việc browse
- Phù hợp với scope hiện tại
- Có thể migrate sang server-side sau

**Khi nào nên dùng Server-side:**
- Cần đồng bộ giữa devices
- Cần phân tích user behavior
- Có nhiều thời gian development

### 2. Pagination

- Dùng page-based pagination (đơn giản)
- Page size: 12 hoặc 16 sản phẩm
- Có thể thêm "Load more" sau

### 3. Search & Filter

**Priority:**
1. ✅ Search by name (cao nhất)
2. ✅ Filter by price range
3. ✅ Sort by price, name, date
4. ⚠️ Filter by category (nếu có thời gian)
5. ⚠️ Advanced filters (sau)

### 4. Performance

- Dùng `JOIN FETCH` để tránh N+1 queries
- Enable lazy loading cho relationships
- Cache product list ở client (React Query)
- Debounce search input

### 5. UX Improvements

- Loading states
- Empty states
- Error handling
- Success notifications
- Responsive design
- Image lazy loading

---

## 📚 TÀI LIỆU THAM KHẢO

### JPA Criteria API
- [Jakarta Persistence Criteria API](https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#criteria-api)

### React Best Practices
- [React Hooks](https://react.dev/reference/react)
- [React Router](https://reactrouter.com/)

### UI/UX References
- [Shopee](https://shopee.vn) - Tham khảo product card, filters
- [Tiki](https://tiki.vn) - Tham khảo search, cart UI
- [Lazada](https://lazada.vn) - Tham khảo pagination

---

**Good luck với implementation! 🚀**
