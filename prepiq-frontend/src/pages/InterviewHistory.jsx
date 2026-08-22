import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getInterviewHistory } from '../api/interviewApi';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

function InterviewHistory() {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getInterviewHistory()
      .then(setSessions)
      .catch(() => setError('Could not load your interview history. Please refresh the page.'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="page-flex">
      <Navbar />
      <div className="page-content-wide page-flex-body">
        <h1>Interview History</h1>
        {error && <div className="alert alert-error" style={{ marginTop: '1rem' }} role="alert">{error}</div>}
        {loading ? (
          <div style={{ marginTop: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.7rem' }}>
            {[1, 2].map((i) => (
              <div key={i} className="skeleton" style={{ height: '58px' }} />
            ))}
          </div>
        ) : sessions.length === 0 && !error ? (
          <div className="empty-state">
            <div className="empty-state-icon">🎯</div>
            <p>No completed interviews yet.</p>
            <Link to="/interview">Start your first mock interview →</Link>
          </div>
        ) : (
          <div style={{ marginTop: '1.5rem' }}>
            {sessions.map((s) => (
              <div key={s.sessionId} className="problem-row" style={{ borderLeftColor: '#5b8def' }}>
                <div className="problem-main">
                  <span className="problem-name">{new Date(s.startedAt).toLocaleDateString()}</span>
                  <span className="chip">{s.topicsCovered.join(', ')}</span>
                </div>
                <div className="problem-meta">
                  <strong style={{ color: s.score >= 70 ? '#2ed9a0' : s.score >= 50 ? '#f2994a' : '#ff6b6b' }}>{s.score}/100</strong>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      <Footer />
    </div>
  );
}

export default InterviewHistory;