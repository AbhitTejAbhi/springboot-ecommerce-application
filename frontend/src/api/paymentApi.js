import axiosInstance from '../utils/axiosInstance';

// ── Customer ──

export const createPayment = (data, headers = {}) =>
  axiosInstance.post('/api/customer/payments', data, { headers });

export const getMyPayments = (page = 0, size = 10) =>
  axiosInstance.get('/api/customer/payments', { params: { page, size } });

export const getMyPayment = (paymentId) =>
  axiosInstance.get(`/api/customer/payments/${paymentId}`);

// ── Admin ──

export const getAllPayments = (page = 0, size = 10) =>
  axiosInstance.get('/api/admin/payments', { params: { page, size } });

export const getPaymentById = (paymentId) =>
  axiosInstance.get(`/api/admin/payments/${paymentId}`);

export const updatePaymentStatus = (paymentId, data) =>
  axiosInstance.patch(`/api/admin/payments/${paymentId}/status`, data);
