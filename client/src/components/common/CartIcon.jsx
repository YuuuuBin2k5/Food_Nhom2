import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function CartIcon() {
    const navigate = useNavigate();
    const [cartCount, setCartCount] = useState(0);
    const [isAnimating, setIsAnimating] = useState(false);

    useEffect(() => {
        updateCartCount();

        const handleCartUpdate = (event) => {
            setCartCount(event.detail.count);
            // Trigger animation
            setIsAnimating(true);
            setTimeout(() => setIsAnimating(false), 300);
        };

        window.addEventListener('cartUpdated', handleCartUpdate);

        return () => {
            window.removeEventListener('cartUpdated', handleCartUpdate);
        };
    }, []);

    const updateCartCount = () => {
        try {
            const cart = JSON.parse(localStorage.getItem('cart') || '[]');
            const count = cart.reduce((acc, item) => acc + item.quantity, 0);
            setCartCount(count);
        } catch (error) {
            console.error('Error reading cart:', error);
            setCartCount(0);
        }
    };

    const handleClick = () => {
        navigate('/cart');
    };

    return (
        <button
            onClick={handleClick}
            className={`relative p-2 rounded-xl bg-white/10 hover:bg-white/20 transition-all ${isAnimating ? 'scale-110' : 'scale-100'}`}
        >
            <svg
                className="w-7 h-7 text-white"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
            >
                <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
                />
            </svg>
            {cartCount > 0 && (
                <span className={`
                    absolute -top-1 -right-1 
                    bg-gradient-to-r from-orange-500 to-red-500 
                    text-white text-xs font-bold 
                    rounded-full min-w-[20px] h-5 
                    flex items-center justify-center px-1
                    shadow-lg
                    ${isAnimating ? 'animate-bounce' : ''}
                `}>
                    {cartCount > 99 ? '99+' : cartCount}
                </span>
            )}
        </button>
    );
}

export default CartIcon;
