import axiosInstance from '../utils/axiosInstance';

// ── Customer ──

export const placeOrder = (data, headers = {}) =>
  axiosInstance.post('/api/customer/orders', data, { headers });

export const getMyOrders = (page = 0, size = 10) =>
  axiosInstance.get('/api/customer/orders', { params: { page, size } });

export const getOrderDetails = (orderId) =>
  axiosInstance.get(`/api/customer/orders/${orderId}`);

export const cancelOrder = (orderId) =>
  axiosInstance.patch(`/api/customer/orders/${orderId}/cancel`);

// ── Admin ──

export const getAllOrders = (page = 0, size = 10) =>
  axiosInstance.get('/api/admin/orders', { params: { page, size } });

export const getOrderById = (orderId) =>
  axiosInstance.get(`/api/admin/orders/${orderId}`);

export const updateOrderStatus = (orderId, status) =>
  axiosInstance.patch(`/api/admin/orders/${orderId}/status`, null, { params: { status } });
