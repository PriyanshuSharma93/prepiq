import { useState, useEffect } from 'react';

function App() {
  const [status, setStatus] = useState('Checking backend...');

  useEffect(() => {
    fetch('http://localhost:8080/api/health')
      .then((response) => response.json())
      .then((data) => setStatus(`Backend says: ${data.status}`))
      .catch((error) => setStatus('Backend not reachable'));
  }, []);

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>PrepIQ</h1>
      <p>{status}</p>
    </div>
  );
}

export default App;