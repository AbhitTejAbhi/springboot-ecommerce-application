import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import ErrorMessage from '../../components/common/ErrorMessage';
import { getErrorMessage } from '../../utils/helpers';

export default function LoginPage() {
  const { login, isAuthenticated, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const isExpired = new URLSearchParams(location.search).get('expired') === 'true';
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Redirect if already logged in
  if (isAuthenticated) {
    navigate(isAdmin ? '/admin' : '/', { replace: true });
    return null;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await login(form);
      navigate(res.role === 'ADMIN' ? '/admin' : '/', { replace: true });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  const inputStyle = {
    width: '100%',
    padding: '12px 16px',
    fontSize: '14px',
    color: '#1C1917',
    background: '#FFFFFF',
    border: '1px solid #D6D0C8',
    borderRadius: '8px',
    transition: 'border-color 0.2s ease',
    outline: 'none',
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: '#F5F0E8',
      padding: '32px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      <div style={{ width: '100%', maxWidth: '400px' }}>
        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <Link to="/" style={{
            fontSize: '24px',
            fontWeight: 700,
            color: '#1C1917',
            letterSpacing: '-0.02em',
          }}>
            Store
          </Link>
        </div>

        {/* Login Card */}
        <div style={{
          background: '#FFFFFF',
          borderRadius: '12px',
          border: '1px solid #E8E2D9',
          padding: '36px 32px',
          boxShadow: '0 4px 16px rgba(28,25,23,0.04)',
        }}>
          <h2 style={{ fontSize: '21px', fontWeight: 600, color: '#1C1917', marginBottom: '4px', letterSpacing: '-0.02em' }}>
            Welcome
          </h2>
          <p style={{ fontSize: '14px', color: '#78716C', marginBottom: '28px' }}>
            Sign in to your account
          </p>

          {isExpired && !error && (
            <div style={{
              marginBottom: '16px',
              padding: '10px 14px',
              fontSize: '13px',
              borderRadius: '8px',
              background: '#FEF3C7',
              color: '#92400E',
              border: '1px solid #FCD34D',
            }}>
              Your session has expired. Please sign in again.
            </div>
          )}

          {error && <div style={{ marginBottom: '16px' }}><ErrorMessage message={error} /></div>}

          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: '16px' }}>
              <label style={{ display: 'block', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C', marginBottom: '6px' }}>
                Email
              </label>
              <input
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                placeholder="you@example.com"
                required
                style={inputStyle}
                onFocus={(e) => { e.target.style.borderColor = '#2D6A4F'; }}
                onBlur={(e) => { e.target.style.borderColor = '#D6D0C8'; }}
              />
            </div>

            <div style={{ marginBottom: '24px' }}>
              <label style={{ display: 'block', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C', marginBottom: '6px' }}>
                Password
              </label>
              <input
                type="password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                placeholder="Enter your password"
                required
                style={inputStyle}
                onFocus={(e) => { e.target.style.borderColor = '#2D6A4F'; }}
                onBlur={(e) => { e.target.style.borderColor = '#D6D0C8'; }}
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              style={{
                width: '100%',
                padding: '12px 20px',
                fontSize: '14px',
                fontWeight: 500,
                color: '#F5F0E8',
                background: '#1C1917',
                border: 'none',
                borderRadius: '8px',
                cursor: loading ? 'not-allowed' : 'pointer',
                opacity: loading ? 0.7 : 1,
                transition: 'all 0.15s ease',
              }}
              onMouseDown={(e) => { if (!loading) e.currentTarget.style.transform = 'scale(0.97)'; }}
              onMouseUp={(e) => { e.currentTarget.style.transform = 'scale(1)'; }}
            >
              {loading ? 'Signing in…' : 'Sign in'}
            </button>
          </form>
        </div>

        <p style={{ textAlign: 'center', marginTop: '20px', fontSize: '13px', color: '#78716C' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: '#2D6A4F', fontWeight: 500 }}>Create one</Link>
        </p>
      </div>
    </div>
  );
}
