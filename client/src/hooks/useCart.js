import { useState, useEffect } from 'react';
import * as cartService from '../services/cartService';
import { CART_UPDATED_EVENT } from '../utils/constants';

/**
 * Custom hook for cart management
 * @returns {Object} Cart state and methods
 */
export const useCart = () => {
    const [cart, setCart] = useState([]);
    const [count, setCount] = useState(0);
    const [total, setTotal] = useState(0);

    // Load cart on mount
    useEffect(() => {
        loadCart();
    }, []);

    // Listen for cart updates
    useEffect(() => {
        const handleCartUpdate = (event) => {
            setCart(event.detail.cart);
            setCount(event.detail.count);
            setTotal(cartService.getCartTotal());
        };

        window.addEventListener(CART_UPDATED_EVENT, handleCartUpdate);
        return () => window.removeEventListener(CART_UPDATED_EVENT, handleCartUpdate);
    }, []);

    const loadCart = () => {
        const cartData = cartService.getCart();
        setCart(cartData);
        setCount(cartService.getCartCount());
        setTotal(cartService.getCartTotal());
    };

    const addToCart = (product, quantity = 1) => {
        try {
            cartService.addToCart(product, quantity);
            loadCart();
            return true;
        } catch (error) {
            console.error('Error adding to cart:', error);
            return false;
        }
    };

    const updateQuantity = (productId, newQuantity) => {
        try {
            cartService.updateCartQuantity(productId, newQuantity);
            loadCart();
            return true;
        } catch (error) {
            console.error('Error updating quantity:', error);
            return false;
        }
    };

    const removeItem = (productId) => {
        try {
            cartService.removeFromCart(productId);
            loadCart();
            return true;
        } catch (error) {
            console.error('Error removing item:', error);
            return false;
        }
    };

    const clearCart = () => {
        try {
            cartService.clearCart();
            loadCart();
            return true;
        } catch (error) {
            console.error('Error clearing cart:', error);
            return false;
        }
    };

    return {
        cart,
        count,
        total,
        addToCart,
        updateQuantity,
        removeItem,
        clearCart,
        loadCart
    };
};
