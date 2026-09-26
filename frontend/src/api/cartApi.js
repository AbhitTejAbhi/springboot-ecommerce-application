import axiosInstance from '../utils/axiosInstance';

export const addToCart = (data) =>
  axiosInstance.post('/api/customer/cart', data);

export const getMyCart = () =>
  axiosInstance.get('/api/customer/cart');

export const updateCartItemQuantity = (cartItemId, data) =>
  axiosInstance.put(`/api/customer/cart/${cartItemId}`, data);

export const removeCartItem = (cartItemId) =>
  axiosInstance.delete(`/api/customer/cart/${cartItemId}`);

export const clearCart = () =>
  axiosInstance.delete('/api/customer/cart');
