import { useNavigate } from 'react-router-dom';
import { useState, useRef, useEffect, memo } from 'react';
import { showToast } from '../../utils/toast';
import { formatPrice, calculateDiscount, getExpiryStatus } from '../../utils/format';
import { getImageUrl } from '../../utils/imageHelper';
import { getCategoryName } from '../../utils/categoryHelper';
import { getOptimizedImageUrl } from '../../utils/imageOptimization';
import { addToCart, getCart } from '../../services/cartService';
import { CART_UPDATED_EVENT } from '../../utils/constants';
import { Clock, Plus, Minus, ShoppingCart } from 'lucide-react';

const ProductCard = memo(function ProductCard({ product }) {
    const navigate = useNavigate();
    const [isImageLoaded, setIsImageLoaded] = useState(false);
    const [isVisible, setIsVisible] = useState(false);
    const [cartQuantity, setCartQuantity] = useState(0);
    const [isAdding, setIsAdding] = useState(false);
    const cardRef = useRef(null);

    // Lazy load image with Intersection Observer
    useEffect(() => {
        if (!cardRef.current) return;

        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        setIsVisible(true);
                        observer.unobserve(entry.target);
                    }
                });
            },
            {
                rootMargin: '100px'
            }
        );

        observer.observe(cardRef.current);

        return () => {
            if (cardRef.current) {
                observer.unobserve(cardRef.current);
            }
        };
    }, []);

    // Load cart quantity on mount and listen for updates
    useEffect(() => {
        const updateCartQuantity = () => {
            const cart = getCart();
            const cartItem = cart.find(item => item.product.productId === product.productId);
            setCartQuantity(cartItem ? cartItem.quantity : 0);
        };

        // Initial load
        updateCartQuantity();

        // Listen for cart updates
        window.addEventListener(CART_UPDATED_EVENT, updateCartQuantity);
        return () => window.removeEventListener(CART_UPDATED_EVENT, updateCartQuantity);
    }, [product.productId]);

    const handleClick = () => {
        navigate(`/products/${product.productId}`);
    };

    const handleQuickAdd = (e) => {
        e.stopPropagation();
        if (product.quantity === 0) return;
        
        setIsAdding(true);
        try {
            addToCart(product, 1);
            showToast.success('Đã thêm vào giỏ hàng');
        } catch (error) {
            showToast.error('Không thể thêm vào giỏ hàng');
        } finally {
            setIsAdding(false);
        }
    };

    const handleIncrement = (e) => {
        e.stopPropagation();
        if (cartQuantity >= product.quantity) {
            showToast.warning('Đã đạt số lượng tối đa');
            return;
        }
        
        setIsAdding(true);
        try {
            addToCart(product, 1);
        } catch (error) {
            showToast.error('Không thể thêm vào giỏ hàng');
        } finally {
            setIsAdding(false);
        }
    };

    const handleDecrement = (e) => {
        e.stopPropagation();
        if (cartQuantity <= 0) return;
        
        setIsAdding(true);
        try {
            addToCart(product, -1);
        } catch (error) {
            showToast.error('Không thể cập nhật giỏ hàng');
        } finally {
            setIsAdding(false);
        }
    };

    const expiryStatus = getExpiryStatus(product.expirationDate);
    const discount = calculateDiscount(product.originalPrice, product.salePrice);
    const imageUrl = getImageUrl(product.imageUrl, product.productId, product.name);
    const optimizedImageUrl = getOptimizedImageUrl(imageUrl, 400, 80);

    // Delivery info - có thể lấy từ seller settings hoặc config
    const deliveryTime = '10-15 phút';

    return (
        <div
            ref={cardRef}
            className="group bg-white rounded overflow-hidden cursor-pointer 
                       shadow-sm hover:shadow-2xl
                       transition-all duration-300 ease-out
                       hover:-translate-y-2
                       flex flex-col"
            onClick={handleClick}
        >
            {/* IMAGE SECTION */}
            <div className="relative w-full p-2 aspect-[7/5] overflow-hidden bg-gray-100">
                {!isImageLoaded && (
                    <div className="absolute inset-0 bg-gradient-to-r from-gray-200 via-gray-100 to-gray-200 animate-pulse " />
                )}

                {isVisible && (
                    <img
                        src={optimizedImageUrl}
                        alt={product.name}
                        loading="lazy"
                        className={`w-full h-full object-cover transition-all duration-700 rounded group-hover:scale-90 ${
                            isImageLoaded ? 'opacity-100' : 'opacity-0'
                        }`}
                        onLoad={() => setIsImageLoaded(true)}
                        onError={(e) => {
                            e.target.style.display = 'none';
                            e.target.parentElement.style.background = 'linear-gradient(135deg, #FF8B67 0%, #FFB84D 100%)';
                            setIsImageLoaded(true);
                        }}
                    />
                )}

                {/* Discount Badge - Top Left */}
                {discount > 0 && (
                    <div className="absolute top-3 left-3 z-10">
                        <div className="bg-white text-orange-600 px-3 py-1.5 rounded text-sm font-bold shadow-lg">
                            -{discount}%
                        </div>
                    </div>
                )}

                {/* Out of Stock Overlay */}
                {product.quantity === 0 && (
                    <div className="absolute inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-20">
                        <span className="bg-white text-gray-900 px-6 py-3 rounded-full font-bold text-sm shadow-xl">
                            Hết hàng
                        </span>
                    </div>
                )}
            </div>

            {/* CONTENT SECTION */}
            <div className="p-4 flex flex-col gap-3">
                {/* Product Name & Price */}
                <div className="flex items-start justify-between gap-2">
                    <h3 className="text-gray-900 font-bold text-base leading-tight flex-1 line-clamp-1">
                        {product.name}
                    </h3>
                    <div className="flex items-center gap-1 flex-shrink-0">
                        {expiryStatus && (
                            <span className="w-2 h-2 rounded-full bg-green-500"></span>
                        )}
                        <span className="text-lg font-black text-gray-900">
                            {formatPrice(product.salePrice)}
                        </span>
                    </div>
                </div>

                {/* Delivery Info */}
                <div className="flex items-center gap-2 text-xs text-gray-500">
                    <Clock className="w-3.5 h-3.5 text-orange-500" />
                    <span className="font-medium">{deliveryTime}</span>
                </div>

                {/* Category Tags */}
                <div className="flex flex-wrap gap-2">
                    {product.category && (
                        <span className="px-3 py-1 bg-gray-100 text-gray-600 text-xs font-medium rounded-full">
                            {getCategoryName(product.category)}
                        </span>
                    )}
                    {expiryStatus && (
                        <span className={`px-3 py-1 text-xs font-medium rounded-full ${
                            expiryStatus.color.includes('red') 
                                ? 'bg-red-50 text-red-600' 
                                : expiryStatus.color.includes('orange')
                                ? 'bg-orange-50 text-orange-600'
                                : 'bg-green-50 text-green-600'
                        }`}>
                            {expiryStatus.text}
                        </span>
                    )}
                    {product.quantity > 0 && product.quantity < 10 && (
                        <span className="px-3 py-1 bg-red-50 text-red-600 text-xs font-medium rounded-full">
                            Sắp hết
                        </span>
                    )}
                </div>

                {/* QUICK ADD BUTTON */}
                <div className="mt-2">
                    {cartQuantity === 0 ? (
                        <button
                            onClick={handleQuickAdd}
                            disabled={product.quantity === 0 || isAdding}
                            className={`w-full py-3 rounded font-bold text-sm
                                       transition-all duration-300 transform
                                       flex items-center justify-center gap-2
                                       ${product.quantity === 0 
                                           ? 'bg-gray-100 text-gray-400 cursor-not-allowed' 
                                           : 'bg-gradient-to-r from-orange-500 to-orange-600 text-white hover:from-orange-600 hover:to-orange-700 hover:shadow-lg hover:scale-[1.02] active:scale-95'
                                       }
                                       ${isAdding ? 'opacity-50 cursor-wait' : ''}`}
                        >
                            <ShoppingCart className="w-4 h-4" />
                            <span>{isAdding ? 'Đang thêm...' : 'Thêm vào giỏ'}</span>
                        </button>
                    ) : (
                        <div className="flex items-center gap-2">
                            <button
                                onClick={handleDecrement}
                                disabled={isAdding}
                                className="w-10 h-10 rounded-xl bg-orange-100 text-orange-600 
                                         hover:bg-orange-200 active:scale-95
                                         transition-all duration-200 flex items-center justify-center
                                         disabled:opacity-50 disabled:cursor-wait"
                            >
                                <Minus className="w-4 h-4" />
                            </button>
                            
                            <div className="flex-1 h-10 rounded-xl bg-gradient-to-r from-orange-500 to-orange-600 
                                          text-white font-bold flex items-center justify-center gap-2
                                          shadow-md">
                                <ShoppingCart className="w-4 h-4" />
                                <span>{cartQuantity}</span>
                            </div>
                            
                            <button
                                onClick={handleIncrement}
                                disabled={isAdding || cartQuantity >= product.quantity}
                                className="w-10 h-10 rounded-xl bg-orange-100 text-orange-600 
                                         hover:bg-orange-200 active:scale-95
                                         transition-all duration-200 flex items-center justify-center
                                         disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                <Plus className="w-4 h-4" />
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
});

ProductCard.displayName = 'ProductCard';

export default ProductCard;
