import React, { createContext, useContext, useEffect, useState } from "react";

const CartContext = createContext(null);

export const useCart = () => useContext(CartContext);

export const CartProvider = ({ children }) => {
  const [items, setItems] = useState(() => {
    try {
      const raw = localStorage.getItem("cart_items");
      return raw ? JSON.parse(raw) : [];
    } catch (e) {
      return [];
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem("cart_items", JSON.stringify(items));
    } catch (e) {}
  }, [items]);

  const addItem = (product, quantity = 1) => {
    setItems((prev) => {
      const idx = prev.findIndex((p) => p.productId === product.productId);
      if (idx >= 0) {
        const copy = [...prev];
        copy[idx] = { ...copy[idx], quantity: copy[idx].quantity + quantity };
        return copy;
      }
      return [...prev, { ...product, quantity }];
    });
  };

  const updateQuantity = (productId, quantity) => {
    setItems((prev) =>
      prev.map((p) => (p.productId === productId ? { ...p, quantity } : p))
    );
  };

  const removeItem = (productId) => {
    setItems((prev) => prev.filter((p) => p.productId !== productId));
  };

  const clearCart = () => setItems([]);

  const getTotals = () => {
    const subtotal = items.reduce(
      (s, it) =>
        s + parseFloat(it.salePrice || it.price || 0) * (it.quantity || 0),
      0
    );
    const shipping = subtotal > 0 ? 30000 : 0;
    const total = subtotal + shipping;
    return { subtotal, shipping, total };
  };

  return (
    <CartContext.Provider
      value={{
        items,
        addItem,
        updateQuantity,
        removeItem,
        clearCart,
        getTotals,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export default CartContext;
