import { useState } from 'react';
import { createProblem } from '../api/problemsApi';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const TOPICS = ['Arrays', 'Strings', 'LinkedList', 'Trees', 'Graphs', 'DP', 'Recursion', 'SlidingWindow', 'Stack', 'Queue', 'HashMap', 'Greedy', 'Backtracking', 'Other'];
const DIFFICULTIES = ['Easy', 'Medium', 'Hard'];
const STATUSES = ['Solved', 'Attempted', 'Failed'];

function LogProblem() {
  const [form, setForm] = useState({
    name: '',
    topic: 'Arrays',
    difficulty: 'Easy',
    status: 'Solved',
    mistakeNote: '',
    solvedDate: new Date().toISOString().split('T')[0],
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await createProblem(form);
      setSuccess(true);
      setForm({ ...form, name: '', mistakeNote: '' });
      setTimeout(() => setSuccess(false), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save problem.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-flex">
      <Navbar />
      <div className="page-content page-flex-body">
        <h1>Log a Problem</h1>
        <div className="card" style={{ marginTop: '1.5rem', maxWidth: '480px' }}>
          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">Problem saved!</div>}
          <form onSubmit={handleSubmit}>
            <label className="field-label">Problem Name</label>
            <input className="field-input" name="name" value={form.name} onChange={handleChange} placeholder="e.g. Two Sum" required />

            <label className="field-label">Topic</label>
            <select className="field-input" name="topic" value={form.topic} onChange={handleChange}>
              {TOPICS.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>

            <label className="field-label">Difficulty</label>
            <select className="field-input" name="difficulty" value={form.difficulty} onChange={handleChange}>
              {DIFFICULTIES.map((d) => <option key={d} value={d}>{d}</option>)}
            </select>

            <label className="field-label">Status</label>
            <select className="field-input" name="status" value={form.status} onChange={handleChange}>
              {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>

            <label className="field-label">Mistake Note (optional)</label>
            <textarea className="field-input" name="mistakeNote" value={form.mistakeNote} onChange={handleChange} placeholder="What tripped you up?" />

            <label className="field-label">Date</label>
            <input className="field-input" type="date" name="solvedDate" value={form.solvedDate} onChange={handleChange} required />

            <button className="btn btn-primary" type="submit" disabled={loading}>
              {loading ? 'Saving...' : 'Save Problem'}
            </button>
          </form>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default LogProblem;