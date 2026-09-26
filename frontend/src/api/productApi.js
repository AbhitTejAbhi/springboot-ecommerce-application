import axiosInstance from '../utils/axiosInstance';

// ── Public (no auth required) ──

export const getAllProducts = (page = 0, size = 10, sort = 'name') =>
  axiosInstance.get('/api/products', { params: { page, size, sort } });

export const getProductById = (id) =>
  axiosInstance.get(`/api/products/${id}`);

export const getProductsByCategory = (categoryId, page = 0, size = 10, sort = 'name') =>
  axiosInstance.get(`/api/products/category/${categoryId}`, { params: { page, size, sort } });

export const searchProducts = (keyword, page = 0, size = 10, sort = 'name') =>
  axiosInstance.get('/api/products/search', { params: { keyword, page, size, sort } });

// ── Admin ──

export const createProduct = (data) =>
  axiosInstance.post('/api/admin/products', data);

export const updateProduct = (id, data) =>
  axiosInstance.put(`/api/admin/products/${id}`, data);

export const deleteProduct = (id) =>
  axiosInstance.delete(`/api/admin/products/${id}`);

export const uploadProductImage = (id, file) => {
  const formData = new FormData();
  formData.append('image', file);
  return axiosInstance.post(`/api/admin/products/${id}/image`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

export const updateProductImage = (id, file) => {
  const formData = new FormData();
  formData.append('image', file);
  return axiosInstance.put(`/api/admin/products/${id}/image`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

export const deleteProductImage = (id) =>
  axiosInstance.delete(`/api/admin/products/${id}/image`);
