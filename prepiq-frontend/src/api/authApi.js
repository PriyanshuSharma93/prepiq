import apiClient from './client';

export const signup = async (name, email, password) => {
  const response = await apiClient.post('/auth/signup', { name, email, password });
  return response.data;
};

export const login = async (email, password) => {
  const response = await apiClient.post('/auth/login', { email, password });
  return response.data;
};