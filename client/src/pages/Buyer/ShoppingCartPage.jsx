import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartService } from '../../services/cartService';
import { showToast } from '../../utils/toast';
import CartItem from './CartItem';
import LoadingSpinner from '../../components/common/LoadingSpinner';

import { formatPrice } from '../../utils/format';

function ShoppingCartPage() {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = () => {
        try {
            const items = cartService.getCart();
            setCartItems(items);
        } catch (error) {
            console.error('Error loading cart:', error);
            showToast.error('Lỗi khi tải giỏ hàng');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateQuantity = async (productId, newQuantity) => {
        try {
            await cartService.updateQuantity(productId, newQuantity);
            loadCart();
        } catch (error) {
            throw error;
        }
    };

    const handleRemoveItem = async (productId) => {
        try {
            await cartService.removeFromCart(productId);
            loadCart();
        } catch (error) {
            throw error;
        }
    };

    const calculateSubtotal = () => {
        return cartItems.reduce((total, item) => {
            return total + (item.product.salePrice * item.quantity);
        }, 0);
    };

    const handleContinueShopping = () => {
        navigate('/products');
    };

    const handleClearCart = () => {
        if (window.confirm('Bạn có chắc muốn xóa toàn bộ giỏ hàng?')) {
            cartService.clearCart();
            loadCart();
            showToast.success('Đã xóa giỏ hàng');
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-amber-50 flex items-center justify-center">
                <LoadingSpinner />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-amber-50">
            {/* Header */}
            <header className="bg-gradient-to-r from-emerald-600 via-green-500 to-teal-500 shadow-lg">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex items-center justify-between h-16">
                        <button
                            onClick={handleContinueShopping}
                            className="flex items-center gap-2 text-white hover:text-emerald-100 transition-colors"
                        >
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                            </svg>
                            Tiếp tục mua sắm
                        </button>
                        <h1 className="text-xl font-bold text-white">🛒 Giỏ hàng</h1>
                        <div className="w-32"></div>
                    </div>
                </div>
            </header>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {cartItems.length === 0 ? (
                    /* Empty Cart */
                    <div className="bg-white rounded-3xl shadow-xl p-12 text-center max-w-lg mx-auto">
                        <div className="w-32 h-32 mx-auto mb-6 bg-gradient-to-br from-emerald-100 to-teal-100 rounded-full flex items-center justify-center">
                            <span className="text-6xl">🛒</span>
                        </div>
                        <h2 className="text-2xl font-bold text-gray-800 mb-2">Giỏ hàng trống</h2>
                        <p className="text-gray-500 mb-8">
                            Bạn chưa có sản phẩm nào trong giỏ hàng.<br />
                            Hãy khám phá các sản phẩm tươi ngon!
                        </p>
                        <button
                            onClick={handleContinueShopping}
                            className="px-8 py-4 bg-gradient-to-r from-emerald-500 to-teal-500 text-white font-semibold rounded-xl hover:from-emerald-600 hover:to-teal-600 transition-all shadow-lg hover:shadow-xl"
                        >
                            🛍️ Khám phá sản phẩm
                        </button>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                        {/* Cart Items */}
                        <div className="lg:col-span-2 space-y-4">
                            {/* Cart Header */}
                            <div className="bg-white rounded-2xl shadow-sm p-4 flex items-center justify-between">
                                <h2 className="text-lg font-semibold text-gray-800">
                                    {cartItems.length} sản phẩm
                                </h2>
                                <button
                                    onClick={handleClearCart}
                                    className="text-red-500 hover:text-red-600 text-sm font-medium flex items-center gap-1 transition-colors"
                                >
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                    </svg>
                                    Xóa tất cả
                                </button>
                            </div>

                            {/* Items List */}
                            <div className="space-y-4">
                                {cartItems.map(item => (
                                    <CartItem
                                        key={item.product.productId}
                                        item={item}
                                        onUpdateQuantity={handleUpdateQuantity}
                                        onRemove={handleRemoveItem}
                                    />
                                ))}
                            </div>
                        </div>

                        {/* Order Summary */}
                        <div className="lg:col-span-1">
                            <div className="bg-white rounded-2xl shadow-sm p-6 sticky top-4">
                                <h2 className="text-xl font-bold text-gray-800 mb-6 pb-4 border-b">
                                    Tóm tắt đơn hàng
                                </h2>

                                <div className="space-y-4 mb-6">
                                    <div className="flex justify-between text-gray-600">
                                        <span>Số món</span>
                                        <span className="font-medium">{cartItems.reduce((acc, item) => acc + item.quantity, 0)} món</span>
                                    </div>

                                    <div className="flex justify-between text-gray-600">
                                        <span>Tạm tính</span>
                                        <span className="font-medium">{formatPrice(cartItems.reduce((total, item) => total + (item.product.originalPrice * item.quantity), 0))}</span>
                                    </div>

                                    <div className="flex justify-between text-emerald-600">
                                        <span>Tiết kiệm được</span>
                                        <span className="font-medium">-{formatPrice(cartItems.reduce((total, item) => total + ((item.product.originalPrice - item.product.salePrice) * item.quantity), 0))}</span>
                                    </div>
                                </div>

                                <div className="border-t pt-4 mb-6">
                                    <div className="flex justify-between items-center">
                                        <span className="text-lg font-semibold text-gray-800">Tổng cộng</span>
                                        <span className="text-2xl font-bold text-emerald-600">
                                            {formatPrice(calculateSubtotal())}
                                        </span>
                                    </div>
                                </div>

                                <button
                                    onClick={() => {
                                        if (cartItems.length === 0) {
                                            showToast.warning('Giỏ hàng trống!');
                                            return;
                                        }
                                        // Simulate checkout
                                        const currentItems = [...cartItems];
                                        cartService.clearCart();
                                        navigate('/order-success', {
                                            state: {
                                                order: {
                                                    orderId: `FS-${Math.random().toString(36).substr(2, 6).toUpperCase()}`,
                                                    items: currentItems.map(item => ({
                                                        name: item.product.name,
                                                        quantity: item.quantity,
                                                        shopName: item.product.seller?.shopName || 'Cửa hàng',
                                                        image: item.product.imageUrl
                                                    }))
                                                }
                                            }
                                        });
                                    }}
                                    className="w-full py-4 bg-gradient-to-r from-emerald-500 to-teal-500 text-white font-semibold rounded-xl hover:from-emerald-600 hover:to-teal-600 transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
                                >
                                    Xác nhận đặt hàng
                                </button>

                                <p className="text-center text-xs text-gray-400 mt-4">
                                    Bạn có 30 phút để đến lấy đồ sau khi đặt
                                </p>

                                <button
                                    onClick={handleContinueShopping}
                                    className="w-full mt-3 py-3 bg-white text-emerald-600 font-medium rounded-xl border-2 border-emerald-200 hover:bg-emerald-50 transition-colors"
                                >
                                    Tiếp tục mua sắm
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
}

export default ShoppingCartPage;
