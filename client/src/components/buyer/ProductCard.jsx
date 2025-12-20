import { useNavigate } from 'react-router-dom';
import { useState, useRef, useEffect, memo } from 'react';
import { showToast } from '../../utils/toast';
import { formatPrice, calculateDiscount, getExpiryStatus } from '../../utils/format';
import { getImageUrl } from '../../utils/imageHelper';
import { getCategoryName } from '../../utils/categoryHelper';
import { addToCart } from '../../services/cartService';
import { getOptimizedImageUrl } from '../../utils/imageOptimization';

const ProductCard = memo(function ProductCard({ product }) {
    const navigate = useNavigate();
    const [isImageLoaded, setIsImageLoaded] = useState(false);
    const [isAdding, setIsAdding] = useState(false);
    const [isVisible, setIsVisible] = useState(false);
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
                rootMargin: '100px' // Load 100px before entering viewport
            }
        );

        observer.observe(cardRef.current);

        return () => {
            if (cardRef.current) {
                observer.unobserve(cardRef.current);
            }
        };
    }, []);

    const handleClick = () => {
        navigate(`/products/${product.productId}`);
    };

    const handleAddToCart = async (e) => {
        e.stopPropagation();
        if (isAdding) return;

        setIsAdding(true);
        try {
            addToCart(product, 1);
            showToast.success('Đã thêm vào danh sách giải cứu!');
        } catch (error) {
            showToast.error(error.message || 'Không thể giải cứu món này');
        } finally {
            setIsAdding(false);
        }
    };

    const expiryStatus = getExpiryStatus(product.expirationDate);
    const discount = calculateDiscount(product.originalPrice, product.salePrice);
    const imageUrl = getImageUrl(product.imageUrl, product.productId, product.name);
    const optimizedImageUrl = getOptimizedImageUrl(imageUrl, 400, 80);

    return (
        <div
            ref={cardRef}
            className="group bg-white rounded-xl overflow-hidden cursor-pointer 
                       border border-gray-200/60 hover:border-orange-300
                       shadow-sm hover:shadow-xl
                       transition-all duration-300 ease-out
                       hover:-translate-y-1
                       flex flex-col h-full"
            onClick={handleClick}
        >
            {/* IMAGE SECTION */}
            <div className="relative w-full aspect-square overflow-hidden bg-gradient-to-br from-orange-50 to-amber-50">
                {!isImageLoaded && (
                    <div className="absolute inset-0 bg-gradient-to-r from-orange-100 via-amber-50 to-orange-100 animate-pulse" />
                )}

                {isVisible && (
                    <img
                        src={optimizedImageUrl}
                        alt={product.name}
                        loading="lazy"
                        className={`w-full h-full object-cover transition-all duration-500 group-hover:scale-110 ${
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

                {/* Category Badge - Top Left */}
                {product.category && (
                    <div className="absolute top-2 left-2 z-10">
                        <div className="bg-white/95 backdrop-blur-sm text-gray-700 px-2 py-0.5 text-[10px] font-semibold border border-gray-200/50 rounded">
                            {getCategoryName(product.category)}
                        </div>
                    </div>
                )}

                {/* Discount Badge - Minimalist */}
                {discount > 0 && (
                    <div className="absolute top-2 right-2 z-10">
                        <div className="bg-red-600 text-white px-2.5 py-1 rounded-md text-xs font-bold shadow-lg">
                            -{discount}%
                        </div>
                    </div>
                )}

                {/* Expiry Badge - Below Category */}
                {expiryStatus && (
                    <div className={`absolute ${product.category ? 'top-9' : 'top-2'} left-2 z-10`}>
                        <div className={`px-2.5 py-1 rounded-md text-xs font-semibold backdrop-blur-sm ${expiryStatus.color}`}>
                            {expiryStatus.text}
                        </div>
                    </div>
                )}

                {/* Out of Stock Overlay */}
                {product.quantity === 0 && (
                    <div className="absolute inset-0 bg-gray-900/60 backdrop-blur-sm flex items-center justify-center z-20">
                        <span className="bg-white text-gray-900 px-4 py-2 rounded-lg font-bold text-sm shadow-xl">
                            Hết hàng
                        </span>
                    </div>
                )}

                {/* Quick Add Button - Cleaner */}
                {product.quantity > 0 && (
                    <div className="absolute bottom-3 left-3 right-3 
                                    opacity-0 translate-y-2 group-hover:opacity-100 group-hover:translate-y-0 
                                    transition-all duration-300 z-10">
                        <button
                            onClick={handleAddToCart}
                            disabled={isAdding}
                            className={`w-full py-2.5 ${
                                isAdding 
                                    ? 'bg-gray-400' 
                                    : 'bg-gradient-to-r from-orange-500 to-amber-500 hover:from-orange-600 hover:to-amber-600'
                            } text-white font-bold rounded-lg shadow-lg
                               transition-all duration-200 flex items-center justify-center gap-2
                               active:scale-95 text-sm`}
                        >
                            {isAdding ? (
                                <>
                                    <span className="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full"></span>
                                    Đang xử lý
                                </>
                            ) : (
                                <>
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                    </svg>
                                    Thêm vào giỏ
                                </>
                            )}
                        </button>
                    </div>
                )}
            </div>

            {/* CONTENT SECTION - Cleaner Layout */}
            <div className="p-4 flex flex-col gap-2.5 flex-1">
                {/* Product Name */}
                <h3 className="text-gray-900 font-bold text-sm leading-snug
                               line-clamp-2 min-h-[2.5rem]
                               group-hover:text-orange-600 transition-colors duration-200">
                    {product.name}
                </h3>

                {/* Shop Name - Subtle */}
                <p className="text-xs text-gray-500 flex items-center gap-1.5 truncate">
                    <svg className="w-3.5 h-3.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" />
                    </svg>
                    <span className="truncate">{product.seller?.shopName || 'Cửa hàng'}</span>
                </p>

                {/* PRICE SECTION - Prominent */}
                <div className="mt-auto pt-2.5 border-t border-gray-100">
                    <div className="flex items-center justify-between mb-2">
                        <div className="flex items-baseline gap-2">
                            <span className="text-lg font-black text-orange-600">
                                {formatPrice(product.salePrice)}
                            </span>
                            {product.originalPrice > product.salePrice && (
                                <span className="text-xs text-gray-400 line-through">
                                    {formatPrice(product.originalPrice)}
                                </span>
                            )}
                        </div>
                        {product.quantity > 0 && product.quantity < 10 && (
                            <span className="text-[10px] text-red-600 font-bold bg-red-50 px-2 py-0.5 rounded-full">
                                Sắp hết
                            </span>
                        )}
                    </div>

                    {/* Stock Bar - Visual Indicator */}
                    <div className="flex items-center gap-2">
                        <div className="flex-1 h-1.5 bg-gray-100 rounded-full overflow-hidden">
                            <div 
                                className={`h-full rounded-full transition-all ${
                                    product.quantity < 10 ? 'bg-red-500' : 
                                    product.quantity < 30 ? 'bg-amber-500' : 
                                    'bg-emerald-500'
                                }`}
                                style={{ width: `${Math.min((product.quantity / 50) * 100, 100)}%` }}
                            />
                        </div>
                        <span className="text-[10px] text-gray-500 font-medium whitespace-nowrap">
                            Còn {product.quantity}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    );
});

ProductCard.displayName = 'ProductCard';

export default ProductCard;
