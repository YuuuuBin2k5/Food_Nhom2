import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { cartService } from '../../services/cartService';
import { showToast } from '../../utils/toast';

function ProductCard({ product }) {
    const navigate = useNavigate();
    const [isImageLoaded, setIsImageLoaded] = useState(false);
    const [isAdding, setIsAdding] = useState(false);

    const handleClick = () => {
        navigate(`/products/${product.productId}`);
    };

    const handleAddToCart = async (e) => {
        e.stopPropagation();
        if (isAdding) return;

        setIsAdding(true);
        try {
            await cartService.addToCart(product);
            showToast.success('Đã thêm vào danh sách giải cứu!');
        } catch (error) {
            showToast.error(error.message || 'Không thể giải cứu món này');
        } finally {
            setIsAdding(false);
        }
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

    const getExpiryStatus = () => {
        if (!product.expirationDate) return null;
        const days = Math.ceil((new Date(product.expirationDate) - new Date()) / (1000 * 60 * 60 * 24));
        if (days <= 1) return { text: 'Hết hạn hôm nay', color: 'bg-red-100 text-red-700' };
        if (days <= 3) return { text: `Còn ${days} ngày`, color: 'bg-orange-100 text-orange-700' };
        if (days <= 7) return { text: `Còn ${days} ngày`, color: 'bg-amber-100 text-amber-700' };
        return { text: `HSD: ${new Date(product.expirationDate).toLocaleDateString('vi-VN')}`, color: 'bg-gray-100 text-gray-600' };
    };

    // Placeholder image nếu không có ảnh
    const getImageUrl = () => {
        if (product.imageUrl && !product.imageUrl.includes('placeholder')) {
            return product.imageUrl;
        }
        // Sử dụng placeholder với màu ngẫu nhiên
        const colors = ['2ECC71', '3498DB', 'E74C3C', 'F39C12', '9B59B6', '1ABC9C'];
        const randomColor = colors[product.productId % colors.length];
        return `https://via.placeholder.com/400x400/${randomColor}/FFFFFF?text=${encodeURIComponent(product.name?.substring(0, 10) || 'Food')}`;
    };

    const expiryStatus = getExpiryStatus();
    const discount = calculateDiscount();

    return (
        <div
            className="group bg-white rounded-2xl overflow-hidden cursor-pointer border border-gray-100 
                       shadow-md hover:shadow-2xl hover:shadow-emerald-200/40
                       transition-all duration-400 ease-out
                       hover:-translate-y-2 hover:border-emerald-200
                       flex flex-col h-full
                       transform animate-fadeIn"
            onClick={handleClick}
        >
            {/* ===== IMAGE SECTION ===== */}
            <div className="relative w-full aspect-square overflow-hidden bg-gradient-to-br from-gray-100 to-gray-200">
                {/* Loading skeleton */}
                {!isImageLoaded && (
                    <div className="absolute inset-0 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 animate-pulse" />
                )}

                <img
                    src={getImageUrl()}
                    alt={product.name}
                    className={`w-full h-full object-cover transition-all duration-700 group-hover:scale-125 ${isImageLoaded ? 'opacity-100' : 'opacity-0'
                        }`}
                    onLoad={() => setIsImageLoaded(true)}
                    onError={(e) => {
                        e.target.src = `https://via.placeholder.com/400x400/22C55E/FFFFFF?text=Food`;
                        setIsImageLoaded(true);
                    }}
                />

                {/* Discount Badge - animated */}
                {discount > 0 && (
                    <div className="absolute top-3 left-3 z-10 animate-scaleIn">
                        <div className="bg-gradient-to-r from-orange-500 via-red-500 to-rose-500 text-white 
                                        px-3 py-1.5 rounded-full text-sm font-bold 
                                        shadow-lg shadow-orange-300/50
                                        transform transition-transform group-hover:scale-110 group-hover:rotate-3">
                            -{discount}%
                        </div>
                    </div>
                )}

                {/* Out of Stock Overlay */}
                {product.quantity === 0 && (
                    <div className="absolute inset-0 bg-black/50 backdrop-blur-xs flex items-center justify-center z-20">
                        <span className="bg-white text-gray-800 px-5 py-2.5 rounded-full font-semibold shadow-xl">
                            Hết hàng
                        </span>
                    </div>
                )}

                {/* Quick Add Button on Hover - animated entrance */}
                {product.quantity > 0 && (
                    <div className="absolute bottom-0 left-0 right-0 p-4 
                                    opacity-0 translate-y-4 group-hover:opacity-100 group-hover:translate-y-0 
                                    transition-all duration-400 z-10">
                        <button
                            onClick={handleAddToCart}
                            disabled={isAdding}
                            className={`w-full py-3 bg-gradient-to-r ${isAdding ? 'from-gray-400 to-gray-500' : 'from-emerald-500 to-teal-500 hover:from-emerald-600 hover:to-teal-600'}
                                       text-white font-semibold rounded-xl shadow-lg hover:shadow-xl
                                       transition-all duration-300 flex items-center justify-center gap-2
                                       active:scale-95`}
                        >
                            {isAdding ? (
                                <span className="animate-spin h-5 w-5 border-2 border-white border-t-transparent rounded-full mr-2"></span>
                            ) : (
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                </svg>
                            )}
                            {isAdding ? 'Đang xử lý...' : 'Giải cứu ngay'}
                        </button>
                    </div>
                )}
            </div>

            {/* ===== CONTENT SECTION ===== */}
            <div className="p-5 flex flex-col gap-3 flex-1">

                {/* Expiry Status Badge - animated */}
                {expiryStatus && (
                    <div className="self-start animate-slideInLeft">
                        <span className={`inline-block px-3 py-1 rounded-full text-xs font-medium transition-all ${expiryStatus.color}`}>
                            ⏰ {expiryStatus.text}
                        </span>
                    </div>
                )}

                {/* Product Name */}
                <h3 className="text-gray-900 font-semibold text-base leading-snug
                               line-clamp-2 min-h-[2.75rem]
                               group-hover:text-emerald-600 transition-colors duration-300">
                    {product.name}
                </h3>

                {/* Shop Name with icon */}
                <p className="text-sm text-gray-500 flex items-center gap-1.5 hover:text-emerald-600 transition-colors">
                    <svg className="w-4 h-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" />
                    </svg>
                    <span className="truncate">{product.seller?.shopName || 'Cửa hàng'}</span>
                </p>

                {/* ===== PRICE SECTION ===== */}
                <div className="mt-auto pt-3 border-t border-gray-100">
                    <div className="flex items-baseline gap-2 flex-wrap">
                        <span className="text-xl font-bold bg-gradient-to-r from-emerald-600 to-teal-600 bg-clip-text text-transparent">
                            {formatPrice(product.salePrice)}
                        </span>
                        {product.originalPrice > product.salePrice && (
                            <span className="text-sm text-gray-400 line-through">
                                {formatPrice(product.originalPrice)}
                            </span>
                        )}
                    </div>

                    {/* Stock Info */}
                    <div className="flex items-center justify-between mt-2">
                        <span className="text-xs text-gray-500">
                            Còn {product.quantity} sản phẩm
                        </span>
                        {product.quantity > 0 && product.quantity < 10 && (
                            <span className="text-xs text-orange-600 font-medium bg-orange-50 px-2 py-0.5 rounded-full animate-pulse">
                                🔥 Sắp hết
                            </span>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ProductCard;

