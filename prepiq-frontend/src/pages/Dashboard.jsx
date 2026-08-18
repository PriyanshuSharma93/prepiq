import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>PrepIQ Dashboard</h1>
      <p>Welcome, {user?.name}! ({user?.email})</p>
      <p style={{ color: '#666' }}>Full dashboard with weak-topic tracking coming Day 7.</p>
      <button onClick={handleLogout} style={{ padding: '0.5rem 1rem', cursor: 'pointer' }}>
        Log Out
      </button>
    </div>
  );
}

export default Dashboard;