import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { showToast } from '../../utils/toast';
import { formatPrice, calculateDiscount, getExpiryStatus } from '../../utils/format';
import { getImageUrl } from '../../utils/imageHelper';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ReviewsSection from '../../components/buyer/ReviewsSection';
import RecommendedProducts from '../../components/buyer/RecommendedProducts';
import CartIcon from '../../components/common/CartIcon';
import { getProductById } from '../../services/productService';
import { addToCart } from '../../services/cartService';

function ProductDetailPage() {
    const { productId } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(true);
    const [addingToCart, setAddingToCart] = useState(false);

    useEffect(() => {
        loadProduct();
        window.scrollTo(0, 0);
    }, [productId]);

    const loadProduct = async () => {
        try {
            const data = await getProductById(productId);
            setProduct(data);
        } catch (error) {
            console.error('Error loading product:', error);
            showToast.error('Không thể tải thông tin sản phẩm');
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCart = async (isBuyNow = false) => {
        if (!product) return;

        setAddingToCart(true);
        try {
            addToCart(product, quantity);

            if (isBuyNow) {
                navigate('/cart');
            } else {
                showToast.success(`Đã thêm ${quantity} sản phẩm vào giỏ hàng!`);
                setQuantity(1);
            }
        } catch (error) {
            showToast.error(error.message);
        } finally {
            setAddingToCart(false);
        }
    };

    const handleQuantityChange = (delta) => {
        const newQuantity = quantity + delta;
        if (newQuantity >= 1 && newQuantity <= product.quantity) {
            setQuantity(newQuantity);
        }
    };

    // Use utility functions
    const discount = calculateDiscount(product?.originalPrice, product?.salePrice);
    const expiryStatus = getExpiryStatus(product?.expirationDate);
    const imageUrl = getImageUrl(product?.imageUrl, product?.productId, product?.name);

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50 flex items-center justify-center">
                <LoadingSpinner />
            </div>
        );
    }

    if (!product) {
        return (
            <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
                <div className="text-6xl mb-4">🔍</div>
                <h2 className="text-2xl font-bold text-gray-800 mb-2">Không tìm thấy sản phẩm</h2>
                <button
                    onClick={() => navigate('/products')}
                    className="mt-4 px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
                >
                    Quay lại danh sách
                </button>
            </div>
        );
    }



    return (
        <div className="min-h-screen bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50">
            {/* Header */}
            <header className="bg-white/80 backdrop-blur-md sticky top-0 z-50 border-b border-orange-100">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
                    <button
                        onClick={() => navigate('/products')}
                        className="flex items-center gap-2 text-gray-600 hover:text-[#FF6B6B] transition-colors font-medium"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                        </svg>
                        Quay lại
                    </button>
                    <CartIcon />
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="bg-white rounded-3xl shadow-xl overflow-hidden border border-gray-100">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-0 md:gap-8">
                        {/* Product Image Section */}
                        <div className="p-8 bg-gray-50 flex flex-col items-center justify-center">
                            <div className="relative aspect-square w-full max-w-lg mx-auto rounded-2xl overflow-hidden shadow-lg bg-white">
                                <img
                                    src={imageUrl}
                                    alt={product.name}
                                    className="w-full h-full object-cover"
                                />
                                {discount > 0 && (
                                    <div className="absolute top-4 left-4">
                                        <div className="bg-gradient-to-r from-orange-500 to-red-500 text-white px-4 py-2 rounded text-lg font-bold shadow-lg">
                                            -{discount}%
                                        </div>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Product Info Section */}
                        <div className="p-8 md:pr-12 flex flex-col">
                            {/* Breadcrumb & Shop Info */}
                            <div className="flex items-center gap-2 text-sm text-gray-500 mb-4">
                                <span className="bg-orange-100 text-[#FF6B6B] px-3 py-1 rounded font-medium">
                                    {product.category || 'Thực phẩm'}
                                </span>
                                <span>•</span>
                                <div className="flex items-center gap-1 font-medium text-gray-700">
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                                    </svg>
                                    {product.seller?.shopName}
                                </div>
                            </div>

                            <h1 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4 leading-tight">
                                {product.name}
                            </h1>

                            {/* Ratings */}
                            <div className="flex items-center gap-4 mb-6">
                                <div className="flex text-amber-400">
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <svg key={star} className="w-5 h-5 fill-current" viewBox="0 0 20 20">
                                            <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                                        </svg>
                                    ))}
                                </div>
                                <span className="text-gray-500 text-sm">(12 đánh giá)</span>
                            </div>

                            {/* Price Block */}
                            <div className="bg-gradient-to-br from-orange-50 to-amber-50 rounded-2xl p-6 mb-8 border border-orange-100">
                                <div className="flex items-end gap-3 mb-2">
                                    <span className="text-4xl font-bold text-[#FF6B6B]">
                                        {formatPrice(product.salePrice)}
                                    </span>
                                    {product.originalPrice > product.salePrice && (
                                        <span className="text-xl text-gray-400 line-through mb-1">
                                            {formatPrice(product.originalPrice)}
                                        </span>
                                    )}
                                </div>
                                <div className="flex items-center gap-4 text-sm mt-2">
                                    {expiryStatus && (
                                        <span className={`px-3 py-1 rounded font-medium ${expiryStatus.color}`}>
                                            ⏰ {expiryStatus.text}
                                        </span>
                                    )}
                                    <span className={`px-3 py-1 rounded font-medium ${product.quantity > 0 ? 'bg-orange-100 text-[#FF8E53]' : 'bg-red-100 text-red-700'
                                        }`}>
                                        {product.quantity > 0 ? `Còn ${product.quantity} sản phẩm` : 'Hết hàng'}
                                    </span>
                                </div>
                            </div>

                            {/* Description */}
                            <div className="mb-8">
                                <h3 className="font-semibold text-gray-900 mb-2">Mô tả sản phẩm</h3>
                                <div className="text-gray-600 leading-relaxed whitespace-pre-line">
                                    {product.description || 'Chưa có mô tả cho sản phẩm này.'}
                                </div>
                            </div>

                            {/* Actions Area */}
                            <div className="mt-auto pt-6 border-t border-gray-100">
                                <div className="flex flex-col sm:flex-row gap-4">
                                    {/* Quantity */}
                                    <div className="flex items-center gap-0 border border-gray-200 rounded-xl bg-white w-fit">
                                        <button
                                            onClick={() => handleQuantityChange(-1)}
                                            disabled={quantity <= 1 || product.quantity === 0}
                                            className="w-12 h-12 flex items-center justify-center text-gray-500 hover:bg-gray-50 rounded-l-xl transition-colors disabled:opacity-50"
                                        >
                                            -
                                        </button>
                                        <input
                                            type="number"
                                            value={quantity}
                                            onChange={(e) => {
                                                const val = parseInt(e.target.value) || 1;
                                                if (val > 0 && val <= product.quantity) setQuantity(val);
                                            }}
                                            className="w-14 h-12 text-center border-x border-gray-200 focus:outline-none font-medium text-gray-900"
                                        />
                                        <button
                                            onClick={() => handleQuantityChange(1)}
                                            disabled={quantity >= product.quantity || product.quantity === 0}
                                            className="w-12 h-12 flex items-center justify-center text-gray-500 hover:bg-gray-50 rounded-r-xl transition-colors disabled:opacity-50"
                                        >
                                            +
                                        </button>
                                    </div>

                                    {/* Buttons */}
                                    <button
                                        onClick={() => handleAddToCart(false)}
                                        disabled={product.quantity === 0 || addingToCart}
                                        className="flex-1 px-8 py-3 bg-white border-2 border-[#FF6B6B] text-[#FF6B6B] font-bold rounded-xl hover:bg-orange-50 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                                    >
                                        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                                        </svg>
                                        Thêm vào giỏ
                                    </button>
                                    <button
                                        onClick={() => handleAddToCart(true)}
                                        disabled={product.quantity === 0 || addingToCart}
                                        className="flex-1 px-8 py-3 bg-gradient-to-r from-[#FF6B6B] via-[#FF8E53] to-[#FFC75F] text-white font-bold rounded-xl hover:opacity-90 shadow-lg hover:shadow-xl transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                                    >
                                        Mua ngay
                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                                        </svg>
                                    </button>
                                </div>
                            </div>

                            {/* Shop Info Card */}
                            <div className="mt-6 bg-orange-50 rounded-2xl p-5 border border-orange-100">
                                <div className="flex items-start gap-4">
                                    <div className="w-16 h-16 bg-white rounded-xl shadow-sm overflow-hidden flex-shrink-0">
                                        <img
                                            src={product.seller?.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(product.seller?.shopName || 'Shop')}&background=random`}
                                            alt={product.seller?.shopName}
                                            className="w-full h-full object-cover"
                                        />
                                    </div>
                                    <div className="flex-1 min-w-0">
                                        <h3 className="font-bold text-gray-900 text-lg truncate">
                                            {product.seller?.shopName || 'Quán Cơm Nhà Làm'}
                                        </h3>
                                        <div className="flex items-center gap-1 text-amber-500 text-sm mb-2">
                                            <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
                                                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                                            </svg>
                                            <span className="font-medium">4.8</span>
                                        </div>

                                        <div className="space-y-2 text-sm text-gray-600">
                                            <div className="flex items-start gap-2">
                                                <span className="flex-shrink-0 mt-0.5">📍</span>
                                                <span className="line-clamp-2">{product.seller?.address || '456 Lê Lợi, Quận 3, TP.HCM'}</span>
                                            </div>
                                            <div className="flex items-center gap-2">
                                                <span>📞</span>
                                                <span>{product.seller?.phone || '0912345678'}</span>
                                            </div>
                                            <div className="flex items-center gap-2">
                                                <span>⏰</span>
                                                <span>08:00 - 22:00</span>
                                            </div>
                                        </div>

                                        <button
                                            onClick={() => window.open(`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(product.seller?.address || '456 Lê Lợi, Quận 3, TP.HCM')}`, '_blank')}
                                            className="w-full mt-4 py-2.5 bg-white border border-gray-300 hover:border-[#FF6B6B] hover:text-[#FF6B6B] rounded-xl text-gray-700 font-medium flex items-center justify-center gap-2 transition-all shadow-sm hover:shadow-md active:scale-95"
                                        >
                                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                                            </svg>
                                            Xem trên bản đồ
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Additional Sections */}
                    <div className="bg-gray-50 border-t border-gray-100 p-8">
                        <ReviewsSection productId={productId} />
                    </div>
                    {/* Recommended Products */}
                    <div className="border-t border-gray-100 p-8">
                        <RecommendedProducts currentProductId={productId} />
                    </div>
                </div>
            </main>
        </div>
    );
}

export default ProductDetailPage;
