import { useState } from 'react';
import { showToast } from '../../utils/toast';
import { formatPrice } from '../../utils/format';

function CartItem({ item, onUpdateQuantity, onRemove }) {
    const { product, quantity } = item;
    const [isUpdating, setIsUpdating] = useState(false);

    const handleQuantityChange = async (delta) => {
        const newQuantity = quantity + delta;

        if (newQuantity < 1) return;
        if (newQuantity > product.quantity) {
            showToast.warning(`Chỉ còn ${product.quantity} sản phẩm trong kho`);
            return;
        }

        setIsUpdating(true);
        try {
            await onUpdateQuantity(product.productId, newQuantity);
        } catch (error) {
            showToast.error(error.message);
        } finally {
            setIsUpdating(false);
        }
    };

    const handleRemove = async () => {
        if (window.confirm(`Xóa "${product.name}" khỏi giỏ hàng?`)) {
            try {
                await onRemove(product.productId);
                showToast.success('Đã xóa sản phẩm');
            } catch (error) {
                showToast.error(error.message);
            }
        }
    };

    const discount = product.originalPrice > product.salePrice
        ? Math.round(((product.originalPrice - product.salePrice) / product.originalPrice) * 100)
        : 0;

    return (
        <div className={`bg-white rounded-2xl shadow-sm hover:shadow-lg border border-gray-100 hover:border-emerald-200 p-4 transition-all duration-300 ${isUpdating ? 'opacity-60' : ''}`}>
            <div className="flex gap-4">
                {/* Product Image - với hover effect */}
                <div className="relative w-24 h-24 flex-shrink-0 rounded-xl overflow-hidden group">
                    <img
                        src={product.imageUrl || '/placeholder.png'}
                        alt={product.name}
                        className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
                    />
                    {discount > 0 && (
                        <div className="absolute -top-2 -left-2 bg-gradient-to-r from-orange-500 to-red-500 text-white text-xs font-bold px-2 py-1 rounded-full shadow-lg">
                            -{discount}%
                        </div>
                    )}
                </div>

                {/* Product Info */}
                <div className="flex-1 min-w-0">
                    <div className="flex justify-between items-start gap-2">
                        <div className="min-w-0 flex-1">
                            <h3 className="font-semibold text-gray-800 line-clamp-2 hover:text-emerald-600 transition-colors">
                                {product.name}
                            </h3>
                            <p className="text-sm text-gray-500 mt-1 flex items-center gap-1">
                                <svg className="w-4 h-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                                    <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" />
                                </svg>
                                <span className="truncate">{product.seller?.shopName || 'Cửa hàng'}</span>
                            </p>
                            {product.expirationDate && (
                                <p className="text-xs text-amber-600 mt-1 font-medium">
                                    ⏰ HSD: {new Date(product.expirationDate).toLocaleDateString('vi-VN')}
                                </p>
                            )}
                        </div>

                        {/* Remove Button - với animation */}
                        <button
                            onClick={handleRemove}
                            disabled={isUpdating}
                            className="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all hover:scale-110 active:scale-95"
                        >
                            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                        </button>
                    </div>

                    {/* Price & Quantity */}
                    <div className="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
                        {/* Quantity Controls - improved design */}
                        <div className="flex items-center gap-1 bg-gray-100 rounded-xl p-1">
                            <button
                                onClick={() => handleQuantityChange(-1)}
                                disabled={isUpdating || quantity <= 1}
                                className="w-8 h-8 flex items-center justify-center rounded-lg bg-white shadow-sm text-gray-600 hover:bg-emerald-500 hover:text-white hover:shadow-md disabled:opacity-40 disabled:hover:bg-white disabled:hover:text-gray-600 transition-all active:scale-95"
                            >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
                                </svg>
                            </button>
                            <span className="w-10 text-center font-semibold text-gray-800">
                                {quantity}
                            </span>
                            <button
                                onClick={() => handleQuantityChange(1)}
                                disabled={isUpdating || quantity >= product.quantity}
                                className="w-8 h-8 flex items-center justify-center rounded-lg bg-white shadow-sm text-gray-600 hover:bg-emerald-500 hover:text-white hover:shadow-md disabled:opacity-40 disabled:hover:bg-white disabled:hover:text-gray-600 transition-all active:scale-95"
                            >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                </svg>
                            </button>
                        </div>

                        {/* Price - highlighted */}
                        <div className="text-right">
                            <div className="text-lg font-bold bg-gradient-to-r from-emerald-600 to-teal-600 bg-clip-text text-transparent">
                                {formatPrice(product.salePrice * quantity)}
                            </div>
                            {quantity > 1 && (
                                <div className="text-sm text-gray-400">
                                    {formatPrice(product.salePrice)} × {quantity}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default CartItem;
