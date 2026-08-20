import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getProblems, deleteProblem } from '../api/problemsApi';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

function ProblemList() {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadProblems = async () => {
    setLoading(true);
    try {
      setProblems(await getProblems());
    } catch (err) {
      setError('Failed to load problems.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadProblems(); }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this problem?')) return;
    try {
      await deleteProblem(id);
      setProblems(problems.filter((p) => p.id !== id));
    } catch (err) {
      setError('Failed to delete problem.');
    }
  };

  const chipClass = (status) => {
    if (status === 'Solved') return 'chip chip-solved';
    if (status === 'Attempted') return 'chip chip-attempted';
    return 'chip chip-failed';
  };

  const borderColor = (status) => {
    if (status === 'Solved') return '#2ed9a0';
    if (status === 'Attempted') return '#f2994a';
    return '#ff6b6b';
  };

  return (
    <div className="page-flex">
      <Navbar />
      <div className="page-content-wide page-flex-body">
        <h1>My Problems</h1>
        {error && <div className="alert alert-error" style={{ marginTop: '1rem' }}>{error}</div>}
        {loading ? (
          <p className="link-muted" style={{ marginTop: '1.5rem' }}>Loading...</p>
        ) : problems.length === 0 ? (
          <div className="empty-state">
            <p>No problems logged yet.</p>
            <Link to="/log-problem">Log your first one →</Link>
          </div>
        ) : (
          <div style={{ marginTop: '1.5rem' }}>
            {problems.map((p) => (
              <div key={p.id} className="problem-row" style={{ borderLeftColor: borderColor(p.status) }}>
                <div className="problem-main">
                  <span className="problem-name">{p.name}</span>
                  <span className="chip">{p.topic}</span>
                  <span className="chip">{p.difficulty}</span>
                  <span className={chipClass(p.status)}>{p.status}</span>
                </div>
                <div className="problem-meta">
                  <span>{p.solvedDate}</span>
                  <button className="btn-ghost" onClick={() => handleDelete(p.id)}>Delete</button>
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

export default ProblemList;