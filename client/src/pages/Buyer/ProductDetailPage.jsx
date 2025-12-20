import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { showToast } from '../../utils/toast';
import { formatPrice, calculateDiscount, getExpiryStatus } from '../../utils/format';
import { getImageUrl } from '../../utils/imageHelper';
import { getCategoryName } from '../../utils/categoryHelper';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ReviewsSection from '../../components/buyer/ReviewsSection';
import RecommendedProducts from '../../components/buyer/RecommendedProducts';
import { getProductById } from '../../services/productService';
import { addToCart, getCart } from '../../services/cartService';
import { CART_UPDATED_EVENT } from '../../utils/constants';
import { ShoppingCart, MapPin, Clock, Phone, Store } from 'lucide-react';

function ProductDetailPage() {
    const { productId } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(true);
    const [addingToCart, setAddingToCart] = useState(false);
    const [cartQuantity, setCartQuantity] = useState(0);

    useEffect(() => {
        loadProduct();
        window.scrollTo(0, 0);
    }, [productId]);

    // Load cart quantity
    useEffect(() => {
        const updateCartQuantity = () => {
            const cart = getCart();
            const cartItem = cart.find(item => item.product.productId === parseInt(productId));
            setCartQuantity(cartItem ? cartItem.quantity : 0);
        };

        updateCartQuantity();
        window.addEventListener(CART_UPDATED_EVENT, updateCartQuantity);
        return () => window.removeEventListener(CART_UPDATED_EVENT, updateCartQuantity);
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

    const handleAddToCart = (isBuyNow = false) => {
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
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white sticky top-0 z-50 border-b border-gray-200 shadow-sm">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center">
                    <button
                        onClick={() => navigate(-1)}
                        className="flex items-center gap-2 text-gray-700 hover:text-orange-600 transition-colors font-medium"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                        </svg>
                        <span className="hidden sm:inline">Quay lại</span>
                    </button>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                    {/* Left: Image Section */}
                    <div className="space-y-4">
                        <div className="relative aspect-square w-full rounded-2xl overflow-hidden bg-white shadow-lg">
                            <img
                                src={imageUrl}
                                alt={product.name}
                                className="w-full h-full object-cover"
                            />
                            {discount > 0 && (
                                <div className="absolute top-4 left-4">
                                    <div className="bg-red-500 text-white px-4 py-2 rounded-lg text-sm font-bold shadow-lg">
                                        -{discount}%
                                    </div>
                                </div>
                            )}
                            {product.quantity === 0 && (
                                <div className="absolute inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center">
                                    <span className="bg-white text-gray-900 px-6 py-3 rounded-full font-bold shadow-xl">
                                        Hết hàng
                                    </span>
                                </div>
                            )}
                        </div>

                        {/* Shop Info Card - Desktop */}
                        <div className="hidden lg:block bg-white rounded-2xl p-6 shadow-sm border border-gray-200">
                            <div className="flex items-center gap-4 mb-4">
                                <div className="w-16 h-16 bg-gradient-to-br from-orange-400 to-red-500 rounded-xl flex items-center justify-center text-white text-2xl font-bold shadow-md">
                                    {product.seller?.shopName?.charAt(0) || 'S'}
                                </div>
                                <div className="flex-1 min-w-0">
                                    <h3 className="font-bold text-gray-900 text-lg truncate">
                                        {product.seller?.shopName || 'Cửa hàng'}
                                    </h3>
                                    <div className="flex items-center gap-2 text-sm text-gray-500">
                                        <Store className="w-4 h-4" />
                                        <span>Người bán</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div className="space-y-3 text-sm">
                                {product.seller?.address && (
                                    <div className="flex items-start gap-3 text-gray-600">
                                        <MapPin className="w-4 h-4 mt-0.5 flex-shrink-0 text-orange-500" />
                                        <span className="line-clamp-2">{product.seller.address}</span>
                                    </div>
                                )}
                                {product.seller?.phone && (
                                    <div className="flex items-center gap-3 text-gray-600">
                                        <Phone className="w-4 h-4 flex-shrink-0 text-orange-500" />
                                        <span>{product.seller.phone}</span>
                                    </div>
                                )}
                                <div className="flex items-center gap-3 text-gray-600">
                                    <Clock className="w-4 h-4 flex-shrink-0 text-orange-500" />
                                    <span>08:00 - 22:00</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Right: Product Info */}
                    <div className="space-y-6">
                        {/* Category Badge */}
                        <div>
                            <span className="inline-block bg-orange-100 text-orange-700 px-3 py-1 rounded-full text-sm font-medium">
                                {getCategoryName(product.category)}
                            </span>
                        </div>

                        {/* Product Name */}
                        <h1 className="text-3xl lg:text-4xl font-bold text-gray-900 leading-tight">
                            {product.name}
                        </h1>

                        {/* Price Section */}
                        <div className="bg-gradient-to-br from-orange-50 to-red-50 rounded-2xl p-6 border border-orange-200">
                            <div className="flex items-baseline gap-3 mb-3">
                                <span className="text-4xl font-bold text-red-600">
                                    {formatPrice(product.salePrice)}
                                </span>
                                {product.originalPrice > product.salePrice && (
                                    <span className="text-xl text-gray-400 line-through">
                                        {formatPrice(product.originalPrice)}
                                    </span>
                                )}
                            </div>
                            
                            <div className="flex flex-wrap gap-2">
                                {expiryStatus && (
                                    <span className={`px-3 py-1.5 rounded-lg text-sm font-medium ${
                                        expiryStatus.color.includes('red') 
                                            ? 'bg-red-100 text-red-700' 
                                            : expiryStatus.color.includes('orange')
                                            ? 'bg-orange-100 text-orange-700'
                                            : 'bg-green-100 text-green-700'
                                    }`}>
                                        {expiryStatus.text}
                                    </span>
                                )}
                                <span className={`px-3 py-1.5 rounded-lg text-sm font-medium ${
                                    product.quantity > 0 
                                        ? 'bg-green-100 text-green-700' 
                                        : 'bg-gray-100 text-gray-700'
                                }`}>
                                    {product.quantity > 0 ? `Còn ${product.quantity} sản phẩm` : 'Hết hàng'}
                                </span>
                                {cartQuantity > 0 && (
                                    <span className="px-3 py-1.5 rounded-lg text-sm font-medium bg-blue-100 text-blue-700">
                                        {cartQuantity} trong giỏ hàng
                                    </span>
                                )}
                            </div>
                        </div>

                        {/* Description */}
                        {product.description && (
                            <div className="bg-white rounded-2xl p-6 border border-gray-200">
                                <h3 className="font-bold text-gray-900 mb-3 flex items-center gap-2">
                                    <span className="text-lg">📝</span>
                                    Mô tả sản phẩm
                                </h3>
                                <p className="text-gray-600 leading-relaxed whitespace-pre-line">
                                    {product.description}
                                </p>
                            </div>
                        )}

                        {/* Quantity & Actions */}
                        <div className="bg-white rounded-2xl p-6 border border-gray-200 space-y-4">
                            <div className="flex items-center gap-4">
                                <span className="text-gray-700 font-medium">Số lượng:</span>
                                <div className="flex items-center border-2 border-gray-300 rounded-xl overflow-hidden">
                                    <button
                                        onClick={() => handleQuantityChange(-1)}
                                        disabled={quantity <= 1 || product.quantity === 0}
                                        className="w-10 h-10 flex items-center justify-center text-gray-600 hover:bg-gray-100 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
                                    >
                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
                                        </svg>
                                    </button>
                                    <input
                                        type="number"
                                        value={quantity}
                                        onChange={(e) => {
                                            const val = parseInt(e.target.value) || 1;
                                            if (val > 0 && val <= product.quantity) setQuantity(val);
                                        }}
                                        className="w-16 h-10 text-center border-x-2 border-gray-300 focus:outline-none font-bold text-gray-900"
                                    />
                                    <button
                                        onClick={() => handleQuantityChange(1)}
                                        disabled={quantity >= product.quantity || product.quantity === 0}
                                        className="w-10 h-10 flex items-center justify-center text-gray-600 hover:bg-gray-100 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
                                    >
                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                        </svg>
                                    </button>
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-3">
                                <button
                                    onClick={() => handleAddToCart(false)}
                                    disabled={product.quantity === 0 || addingToCart}
                                    className="py-3 px-4 bg-white border-2 border-orange-500 text-orange-600 font-bold rounded-xl hover:bg-orange-50 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                                >
                                    <ShoppingCart className="w-5 h-5" />
                                    <span className="hidden sm:inline">Thêm vào giỏ</span>
                                    <span className="sm:hidden">Giỏ hàng</span>
                                </button>
                                <button
                                    onClick={() => handleAddToCart(true)}
                                    disabled={product.quantity === 0 || addingToCart}
                                    className="py-3 px-4 bg-gradient-to-r from-orange-500 to-red-500 text-white font-bold rounded-xl hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                                >
                                    Mua ngay
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                                    </svg>
                                </button>
                            </div>
                        </div>

                        {/* Shop Info Card - Mobile */}
                        <div className="lg:hidden bg-white rounded-2xl p-6 border border-gray-200">
                            <div className="flex items-center gap-4 mb-4">
                                <div className="w-14 h-14 bg-gradient-to-br from-orange-400 to-red-500 rounded-xl flex items-center justify-center text-white text-xl font-bold shadow-md">
                                    {product.seller?.shopName?.charAt(0) || 'S'}
                                </div>
                                <div className="flex-1 min-w-0">
                                    <h3 className="font-bold text-gray-900 truncate">
                                        {product.seller?.shopName || 'Cửa hàng'}
                                    </h3>
                                    <div className="flex items-center gap-2 text-sm text-gray-500">
                                        <Store className="w-4 h-4" />
                                        <span>Người bán</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div className="space-y-2 text-sm">
                                {product.seller?.address && (
                                    <div className="flex items-start gap-2 text-gray-600">
                                        <MapPin className="w-4 h-4 mt-0.5 flex-shrink-0 text-orange-500" />
                                        <span className="line-clamp-2">{product.seller.address}</span>
                                    </div>
                                )}
                                {product.seller?.phone && (
                                    <div className="flex items-center gap-2 text-gray-600">
                                        <Phone className="w-4 h-4 flex-shrink-0 text-orange-500" />
                                        <span>{product.seller.phone}</span>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                {/* Reviews Section */}
                <div className="mt-12 bg-white rounded-2xl p-6 lg:p-8 border border-gray-200">
                    <ReviewsSection productId={productId} />
                </div>

                {/* Recommended Products */}
                <div className="mt-8">
                    <RecommendedProducts currentProductId={productId} />
                </div>
            </main>
        </div>
    );
}

export default ProductDetailPage;
