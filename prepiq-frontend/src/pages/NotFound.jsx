import { Link } from 'react-router-dom';
import Footer from '../components/Footer';

function NotFound() {
  return (
    <div className="page-flex">
      <div className="auth-shell page-flex-body">
        <div style={{ textAlign: 'center' }}>
          <h1 style={{ fontSize: '4rem', marginBottom: '0.5rem' }}>404</h1>
          <p className="link-muted" style={{ marginBottom: '1.5rem' }}>This page doesn't exist.</p>
          <Link to="/dashboard" className="btn btn-primary btn-auto" style={{ display: 'inline-flex' }}>
            Back to Dashboard
          </Link>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default NotFound;