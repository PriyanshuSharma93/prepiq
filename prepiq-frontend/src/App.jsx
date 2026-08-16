import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { useState, useEffect } from 'react';
import apiClient from './api/client';

function Home() {
  const [status, setStatus] = useState('Checking backend...');

  useEffect(() => {
    apiClient
      .get('/health')
      .then((response) => setStatus(`Backend says: ${response.data.status}`))
      .catch(() => setStatus('Backend not reachable'));
  }, []);

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>PrepIQ</h1>
      <p>{status}</p>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        {/* Day 4 onward: /login, /signup, /dashboard, etc. will be added here */}
      </Routes>
    </BrowserRouter>
  );
}

export default App;