import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { logout } = useAuth();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <span className="navbar-logo-dot"></span>
        PrepIQ
      </div>
      <div className="navbar-links">
        <Link to="/dashboard" className={`navbar-link ${isActive('/dashboard') ? 'active' : ''}`}>Dashboard</Link>
        <Link to="/problems" className={`navbar-link ${isActive('/problems') ? 'active' : ''}`}>My Problems</Link>
        <Link to="/log-problem" className={`navbar-link ${isActive('/log-problem') ? 'active' : ''}`}>Log Problem</Link>
        <Link to="/interview" className={`navbar-link ${isActive('/interview') ? 'active' : ''}`}>Mock Interview</Link>
        <Link to="/history" className={`navbar-link ${isActive('/history') ? 'active' : ''}`}>History</Link>
      </div>
      <div className="navbar-spacer">
        <button className="btn-logout" onClick={logout}>Log Out</button>
      </div>
    </nav>
  );
}

export default Navbar;