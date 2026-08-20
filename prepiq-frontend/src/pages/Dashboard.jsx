import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { getWeakTopics } from '../api/dashboardApi';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import { Link } from 'react-router-dom';

function Dashboard() {
  const { user } = useAuth();
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getWeakTopics()
      .then(setTopics)
      .finally(() => setLoading(false));
  }, []);

  const weakTopics = topics.filter((t) => t.classification === 'Weak');

  const cardStyle = (classification) => {
    if (classification === 'Weak') return { borderLeft: '3px solid #ff6b6b' };
    if (classification === 'Strong') return { borderLeft: '3px solid #2ed9a0' };
    return { borderLeft: '3px solid #f2994a' };
  };

  const badgeClass = (classification) => {
    if (classification === 'Weak') return 'chip chip-failed';
    if (classification === 'Strong') return 'chip chip-solved';
    return 'chip chip-attempted';
  };

  return (
    <div className="page-flex">
      <Navbar />
      <div className="page-content-wide page-flex-body">
        <h1>Welcome back, {user?.name?.split(' ')[0]}</h1>
        <p className="link-muted" style={{ marginTop: '0.4rem' }}>{user?.email}</p>

        {loading ? (
          <p className="link-muted" style={{ marginTop: '2rem' }}>Loading your progress...</p>
        ) : topics.length === 0 ? (
          <div className="card" style={{ marginTop: '2rem' }}>
            <h3 style={{ marginBottom: '0.5rem' }}>No problems logged yet</h3>
            <p className="link-muted">Log a few problems and your weak-topic breakdown will appear here.</p>
            <Link to="/log-problem" style={{ display: 'inline-block', marginTop: '1rem' }}>Log a problem →</Link>
          </div>
        ) : (
          <>
            {weakTopics.length > 0 && (
              <div className="card" style={{ marginTop: '2rem', borderLeft: '3px solid #ff6b6b' }}>
                <h3 style={{ marginBottom: '0.75rem' }}>Weak Topics — Focus Here</h3>
                <div style={{ display: 'flex', gap: '0.6rem', flexWrap: 'wrap' }}>
                  {weakTopics.map((t) => (
                    <span key={t.topic} className="chip chip-failed">{t.topic}</span>
                  ))}
                </div>
              </div>
            )}

            <h3 style={{ marginTop: '2rem', marginBottom: '1rem' }}>Topic Breakdown</h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: '1rem' }}>
              {topics.map((t) => (
                <div key={t.topic} className="card" style={{ ...cardStyle(t.classification), padding: '1.2rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.6rem' }}>
                    <strong>{t.topic}</strong>
                    <span className={badgeClass(t.classification)}>{t.classification}</span>
                  </div>
                  <p className="link-muted" style={{ fontSize: '0.85rem', margin: 0 }}>
                    {t.solvedCount}/{t.totalAttempts} solved · {Math.round(t.solveRate * 100)}% rate
                  </p>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
      <Footer />
    </div>
  );
}

export default Dashboard;