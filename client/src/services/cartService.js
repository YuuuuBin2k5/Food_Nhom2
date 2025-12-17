/**
 * Shopping Cart Service
 * Manages cart operations with both localStorage (client-side) and backend API
 */

import { api } from './api';

const CART_KEY = 'food_rescue_cart';
// const API_BASE_URL = 'http://localhost:8080/server/api/cart'; // Removed, using api instance

class CartService {
    /**
     * Get all items in cart
     * @returns {Array} Array of cart items
     */
    getCart() {
        try {
            const cart = localStorage.getItem(CART_KEY);
            return cart ? JSON.parse(cart) : [];
        } catch (error) {
            console.error('Error reading cart from localStorage:', error);
            return [];
        }
    }

    /**
     * Add item to cart (sync with backend)
     * @param {Object} product - Product object
     * @param {number} quantity - Quantity to add
     * @returns {Promise<Array>} Updated cart
     */
    async addToCart(product, quantity = 1) {
        try {
            // Try to sync with backend
            try {
                const response = await api.post('/cart/add', {
                    productId: product.productId,
                    quantity: quantity
                });
                console.log('Backend cart sync:', response.data);
            } catch (backendError) {
                console.warn('Backend sync failed, using localStorage only:', backendError);
            }

            // Also update localStorage for offline capability
            const cart = this.getCart();
            const existingIndex = cart.findIndex(
                item => item.product.productId === product.productId
            );

            if (existingIndex >= 0) {
                const newQuantity = cart[existingIndex].quantity + quantity;
                if (newQuantity > product.quantity) {
                    throw new Error(`Chỉ còn ${product.quantity} sản phẩm trong kho`);
                }
                cart[existingIndex].quantity = newQuantity;
            } else {
                if (quantity > product.quantity) {
                    throw new Error(`Chỉ còn ${product.quantity} sản phẩm trong kho`);
                }
                cart.push({
                    product: product,
                    quantity: quantity,
                    addedAt: new Date().toISOString()
                });
            }

            this.saveCart(cart);
            return cart;
        } catch (error) {
            console.error('Error adding to cart:', error);
            throw error;
        }
    }

    /**
     * Update quantity of a specific product in cart
     * @param {number} productId - Product ID
     * @param {number} quantity - New quantity
     * @returns {Promise<Array>} Updated cart
     */
    async updateQuantity(productId, quantity) {
        try {
            // Try to sync with backend
            try {
                const response = await api.post('/cart/update', {
                    productId: productId,
                    quantity: quantity
                });
                console.log('Backend cart update:', response.data);
            } catch (backendError) {
                console.warn('Backend sync failed, using localStorage only:', backendError);
            }

            // Also update localStorage
            const cart = this.getCart();
            const index = cart.findIndex(
                item => item.product.productId === productId
            );

            if (index >= 0) {
                if (quantity <= 0) {
                    cart.splice(index, 1);
                } else {
                    if (quantity > cart[index].product.quantity) {
                        throw new Error(`Chỉ còn ${cart[index].product.quantity} sản phẩm trong kho`);
                    }
                    cart[index].quantity = quantity;
                }
                this.saveCart(cart);
            }

            return cart;
        } catch (error) {
            console.error('Error updating cart quantity:', error);
            throw error;
        }
    }
    /**
     * Remove item from cart (sync with backend)
     * @param {number} productId - Product ID to remove
     * @returns {Promise<Array>} Updated cart
     */
    async removeFromCart(productId) {
        try {
            // Try to sync with backend
            try {
                const response = await api.delete(`/cart/remove?productId=${productId}`);
                console.log('Backend cart removal:', response.data);
            } catch (backendError) {
                console.warn('Backend sync failed, using localStorage only:', backendError);
            }

            // Also update localStorage
            let cart = this.getCart();
            cart = cart.filter(item => item.product.productId !== productId);
            this.saveCart(cart);
            return cart;
        } catch (error) {
            console.error('Error removing from cart:', error);
            throw error;
        }
    }

    /**
     * Clear entire cart
     */
    clearCart() {
        try {
            localStorage.removeItem(CART_KEY);
            // Try to clear backend cart
            api.post('/cart/clear').catch(err => console.warn('Backend clear failed:', err));
        } catch (error) {
            console.error('Error clearing cart:', error);
        }
    }

    /**
     * Validate cart items with backend (check stock, existence)
     * @returns {Promise<Object>} Validation result { valid: boolean, errors: string[] }
     */
    async validateCart() {
        try {
            const cart = this.getCart();
            const cartItems = cart.map(item => ({
                productId: item.product.productId.toString(),
                quantity: item.quantity
            }));

            const response = await api.post('/cart/validate', { items: cartItems });
            return response.data;
        } catch (error) {
            console.error('Error validating cart:', error);
            return { valid: true, errors: [] }; // Assume valid if backend unreachable
        }
    }

    /**
     * Get total number of items in cart
     * @returns {number} Total item count
     */
    getCartCount() {
        const cart = this.getCart();
        return cart.reduce((total, item) => total + item.quantity, 0);
    }

    /**
     * Calculate cart total price
     * @returns {number} Total price
     */
    getCartTotal() {
        const cart = this.getCart();
        return cart.reduce((total, item) => {
            return total + (item.product.salePrice * item.quantity);
        }, 0);
    }

    /**
     * Check if product is in cart
     * @param {number} productId - Product ID
     * @returns {boolean} True if in cart
     */
    isInCart(productId) {
        const cart = this.getCart();
        return cart.some(item => item.product.productId === productId);
    }

    /**
     * Get quantity of specific product in cart
     * @param {number} productId - Product ID
     * @returns {number} Quantity in cart (0 if not in cart)
     */
    getProductQuantity(productId) {
        const cart = this.getCart();
        const item = cart.find(item => item.product.productId === productId);
        return item ? item.quantity : 0;
    }

    /**
     * Save cart to localStorage
     * @private
     * @param {Array} cart - Cart array to save
     */
    saveCart(cart) {
        try {
            localStorage.setItem(CART_KEY, JSON.stringify(cart));
            // Dispatch custom event for cart updates
            window.dispatchEvent(new CustomEvent('cartUpdated', {
                detail: {
                    cart,
                    count: this.getCartCount(),
                    total: this.getCartTotal()
                }
            }));
        } catch (error) {
            console.error('Error saving cart to localStorage:', error);
            throw new Error('Không thể lưu giỏ hàng. Vui lòng thử lại.');
        }
    }
}

// Export singleton instance
export const cartService = new CartService();
