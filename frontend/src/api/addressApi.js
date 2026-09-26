import axiosInstance from '../utils/axiosInstance';

export const createAddress = (data) =>
  axiosInstance.post('/api/customer/addresses', data);

export const getMyAddresses = () =>
  axiosInstance.get('/api/customer/addresses');

export const getAddressById = (id) =>
  axiosInstance.get(`/api/customer/addresses/${id}`);

export const updateAddress = (id, data) =>
  axiosInstance.put(`/api/customer/addresses/${id}`, data);

export const deleteAddress = (id) =>
  axiosInstance.delete(`/api/customer/addresses/${id}`);
