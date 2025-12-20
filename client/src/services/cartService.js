import { CART_UPDATED_EVENT } from '../utils/constants';

const CART_STORAGE_KEY = 'cart';

/**
 * Get cart from localStorage
 * @returns {Array} Cart items
 */
export const getCart = () => {
    try {
        const cart = localStorage.getItem(CART_STORAGE_KEY);
        return cart ? JSON.parse(cart) : [];
    } catch (error) {
        console.error('Error getting cart:', error);
        return [];
    }
};

/**
 * Save cart to localStorage
 * @param {Array} cart - Cart items
 */
export const saveCart = (cart) => {
    try {
        localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
        dispatchCartUpdate(cart);
    } catch (error) {
        console.error('Error saving cart:', error);
        throw error;
    }
};

/**
 * Add product to cart
 * @param {Object} product - Product to add
 * @param {number} quantity - Quantity to add (can be negative to decrease)
 * @returns {Array} Updated cart
 */
export const addToCart = (product, quantity = 1) => {
    const cart = getCart();
    const existingIndex = cart.findIndex(
        item => item.product.productId === product.productId
    );

    if (existingIndex >= 0) {
        // Update existing item
        cart[existingIndex].quantity += quantity;
        
        // Remove item if quantity becomes 0 or negative
        if (cart[existingIndex].quantity <= 0) {
            cart.splice(existingIndex, 1);
        }
    } else if (quantity > 0) {
        // Only add new item if quantity is positive
        cart.push({
            product,
            quantity,
            addedAt: new Date().toISOString()
        });
    }

    saveCart(cart);
    return cart;
};

/**
 * Update cart item quantity
 * @param {number} productId - Product ID
 * @param {number} newQuantity - New quantity
 * @returns {Array} Updated cart
 */
export const updateCartQuantity = (productId, newQuantity) => {
    const cart = getCart();
    const index = cart.findIndex(item => item.product.productId === productId);

    if (index >= 0) {
        if (newQuantity <= 0) {
            cart.splice(index, 1);
        } else {
            cart[index].quantity = newQuantity;
        }
    }

    saveCart(cart);
    return cart;
};

/**
 * Remove item from cart
 * @param {number} productId - Product ID to remove
 * @returns {Array} Updated cart
 */
export const removeFromCart = (productId) => {
    const cart = getCart();
    const filtered = cart.filter(item => item.product.productId !== productId);
    saveCart(filtered);
    return filtered;
};

/**
 * Clear entire cart
 */
export const clearCart = () => {
    localStorage.removeItem(CART_STORAGE_KEY);
    dispatchCartUpdate([]);
};

/**
 * Get cart item count
 * @returns {number} Total items in cart
 */
export const getCartCount = () => {
    const cart = getCart();
    return cart.reduce((acc, item) => acc + item.quantity, 0);
};

/**
 * Calculate cart total
 * @returns {number} Total price
 */
export const getCartTotal = () => {
    const cart = getCart();
    return cart.reduce((total, item) => {
        return total + (item.product.salePrice * item.quantity);
    }, 0);
};

/**
 * Dispatch cart update event
 * @param {Array} cart - Cart items
 */
const dispatchCartUpdate = (cart) => {
    window.dispatchEvent(new CustomEvent(CART_UPDATED_EVENT, {
        detail: {
            cart,
            count: cart.reduce((acc, item) => acc + item.quantity, 0)
        }
    }));
};
