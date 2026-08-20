import apiClient from './client';

export const startInterview = async () => {
  const response = await apiClient.post('/interview/start');
  return response.data;
};

export const submitAnswer = async (sessionId, answer) => {
  const response = await apiClient.post(`/interview/${sessionId}/answer`, { answer });
  return response.data;
};

export const endInterview = async (sessionId) => {
  const response = await apiClient.post(`/interview/${sessionId}/end`);
  return response.data;
};

export const getInterviewHistory = async () => {
  const response = await apiClient.get('/interview/history');
  return response.data;
};