import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { showToast } from '../../utils/toast';
import { formatPrice, calculateDiscount, getExpiryStatus } from '../../utils/format';
import { getImageUrl } from '../../utils/imageHelper';
import { addToCart } from '../../services/cartService';

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

    return (
        <div
            className="group bg-white rounded-2xl overflow-hidden cursor-pointer border border-gray-100 
                       shadow-md hover:shadow-2xl hover:shadow-orange-200/40
                       transition-all duration-400 ease-out
                       hover:-translate-y-2 hover:border-orange-200
                       flex flex-col h-full
                       transform animate-fadeIn"
            onClick={handleClick}
        >
            {/* IMAGE SECTION */}
            <div className="relative w-full aspect-square overflow-hidden bg-gradient-to-br from-gray-100 to-gray-200">
                {!isImageLoaded && (
                    <div className="absolute inset-0 bg-gradient-to-r from-gray-100 via-gray-50 to-gray-100 animate-pulse" />
                )}

                <img
                    src={imageUrl}
                    alt={product.name}
                    className={`w-full h-full object-cover transition-all duration-700 group-hover:scale-125 ${
                        isImageLoaded ? 'opacity-100' : 'opacity-0'
                    }`}
                    onLoad={() => setIsImageLoaded(true)}
                    onError={(e) => {
                        e.target.style.display = 'none';
                        e.target.parentElement.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
                        setIsImageLoaded(true);
                    }}
                />

                {/* Discount Badge */}
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

                {/* Quick Add Button */}
                {product.quantity > 0 && (
                    <div className="absolute bottom-0 left-0 right-0 p-4 
                                    opacity-0 translate-y-4 group-hover:opacity-100 group-hover:translate-y-0 
                                    transition-all duration-400 z-10">
                        <button
                            onClick={handleAddToCart}
                            disabled={isAdding}
                            className={`w-full py-3 bg-gradient-to-r ${
                                isAdding ? 'from-gray-400 to-gray-500' : 'from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] hover:opacity-90'
                            } text-white font-semibold rounded-xl shadow-lg hover:shadow-xl
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

            {/* CONTENT SECTION */}
            <div className="p-5 flex flex-col gap-3 flex-1">
                {/* Expiry Status Badge */}
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
                               group-hover:text-[#FF6B6B] transition-colors duration-300">
                    {product.name}
                </h3>

                {/* Shop Name */}
                <p className="text-sm text-gray-500 flex items-center gap-1.5 hover:text-[#FF8E53] transition-colors">
                    <svg className="w-4 h-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" />
                    </svg>
                    <span className="truncate">{product.seller?.shopName || 'Cửa hàng'}</span>
                </p>

                {/* PRICE SECTION */}
                <div className="mt-auto pt-3 border-t border-gray-100">
                    <div className="flex items-baseline gap-2 flex-wrap">
                        <span className="text-xl font-bold bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] bg-clip-text text-transparent">
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
