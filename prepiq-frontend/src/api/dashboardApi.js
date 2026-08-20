import apiClient from './client';

export const getWeakTopics = async () => {
  const response = await apiClient.get('/dashboard/weak-topics');
  return response.data;
};