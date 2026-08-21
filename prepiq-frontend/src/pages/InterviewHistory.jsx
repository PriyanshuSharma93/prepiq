import { useEffect, useState } from 'react';
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
        {error && <div className="alert alert-error" style={{ marginTop: '1rem' }}>{error}</div>}
        {loading ? (
          <p className="link-muted" style={{ marginTop: '1.5rem' }}>Loading...</p>
        ) : sessions.length === 0 && !error ? (
          <div className="empty-state">
            <p>No completed interviews yet.</p>
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
                  <strong style={{ color: '#2ed9a0' }}>{s.score}/100</strong>
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