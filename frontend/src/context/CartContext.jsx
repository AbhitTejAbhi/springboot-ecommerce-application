import { createContext, useState, useCallback, useEffect, useRef } from 'react';
import * as cartApi from '../api/cartApi';
import { useAuth } from '../hooks/useAuth';

export const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { isAuthenticated, isCustomer } = useAuth();
  const [cart, setCart] = useState(null);
  const [cartCount, setCartCount] = useState(0);
  const [cartBounce, setCartBounce] = useState(false);
  const prevCountRef = useRef(0);

  const refreshCart = useCallback(async () => {
    if (!isAuthenticated || !isCustomer) {
      setCart(null);
      setCartCount(0);
      return;
    }
    try {
      const res = await cartApi.getMyCart();
      const cartData = res.data.data;
      setCart(cartData);
      const newCount = cartData?.totalItems || 0;

      // Trigger bounce animation only when count increases
      if (newCount > prevCountRef.current) {
        setCartBounce(true);
        setTimeout(() => setCartBounce(false), 300);
      }
      prevCountRef.current = newCount;
      setCartCount(newCount);
    } catch {
      setCart(null);
      setCartCount(0);
    }
  }, [isAuthenticated, isCustomer]);

  useEffect(() => {
    refreshCart();
  }, [refreshCart]);

  const addToCart = useCallback(async (productId, quantity = 1) => {
    const res = await cartApi.addToCart({ productId, quantity });
    const cartData = res.data.data;
    setCart(cartData);
    const newCount = cartData?.totalItems || 0;
    if (newCount > prevCountRef.current) {
      setCartBounce(true);
      setTimeout(() => setCartBounce(false), 300);
    }
    prevCountRef.current = newCount;
    setCartCount(newCount);
    return cartData;
  }, []);

  const updateQuantity = useCallback(async (cartItemId, quantity) => {
    const res = await cartApi.updateCartItemQuantity(cartItemId, { quantity });
    const cartData = res.data.data;
    setCart(cartData);
    setCartCount(cartData?.totalItems || 0);
    prevCountRef.current = cartData?.totalItems || 0;
    return cartData;
  }, []);

  const removeItem = useCallback(async (cartItemId) => {
    const res = await cartApi.removeCartItem(cartItemId);
    const cartData = res.data.data;
    setCart(cartData);
    setCartCount(cartData?.totalItems || 0);
    prevCountRef.current = cartData?.totalItems || 0;
    return cartData;
  }, []);

  const clearCartItems = useCallback(async () => {
    await cartApi.clearCart();
    setCart(null);
    setCartCount(0);
    prevCountRef.current = 0;
  }, []);

  const value = {
    cart,
    cartCount,
    cartBounce,
    refreshCart,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart: clearCartItems,
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
}
