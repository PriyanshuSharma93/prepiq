import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { logout } = useAuth();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  const isActive = (path) => location.pathname === path;

  const links = [
    { to: '/dashboard', label: 'Dashboard' },
    { to: '/problems', label: 'My Problems' },
    { to: '/log-problem', label: 'Log Problem' },
    { to: '/interview', label: 'Mock Interview' },
    { to: '/history', label: 'History' },
  ];

  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <span className="navbar-logo-dot"></span>
        PrepIQ
      </div>

      <button
        className="navbar-toggle"
        onClick={() => setMenuOpen(!menuOpen)}
        aria-label={menuOpen ? 'Close navigation menu' : 'Open navigation menu'}
        aria-expanded={menuOpen}
      >
        {menuOpen ? '✕' : '☰'}
      </button>

      <div className={`navbar-links ${menuOpen ? 'open' : ''}`}>
        {links.map((link) => (
          <Link
            key={link.to}
            to={link.to}
            className={`navbar-link ${isActive(link.to) ? 'active' : ''}`}
            onClick={() => setMenuOpen(false)}
          >
            {link.label}
          </Link>
        ))}
      </div>

      <div className="navbar-spacer">
        <button className="btn-logout" onClick={logout}>Log Out</button>
      </div>
    </nav>
  );
}

export default Navbar;