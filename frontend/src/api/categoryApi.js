import axiosInstance from '../utils/axiosInstance';

// ── Public ──

export const getAllCategories = (page = 0, size = 100, sort = 'name') =>
  axiosInstance.get('/api/categories', { params: { page, size, sort } });

export const getCategoryById = (id) =>
  axiosInstance.get(`/api/categories/${id}`);

// ── Admin ──

export const createCategory = (data) =>
  axiosInstance.post('/api/admin/categories', data);

export const updateCategory = (id, data) =>
  axiosInstance.put(`/api/admin/categories/${id}`, data);

export const deleteCategory = (id) =>
  axiosInstance.delete(`/api/admin/categories/${id}`);
