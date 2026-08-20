import apiClient from './client';

export const getProblems = async () => {
  const response = await apiClient.get('/problems');
  return response.data;
};

export const createProblem = async (problem) => {
  const response = await apiClient.post('/problems', problem);
  return response.data;
};

export const updateProblem = async (id, problem) => {
  const response = await apiClient.put(`/problems/${id}`, problem);
  return response.data;
};

export const deleteProblem = async (id) => {
  await apiClient.delete(`/problems/${id}`);
};