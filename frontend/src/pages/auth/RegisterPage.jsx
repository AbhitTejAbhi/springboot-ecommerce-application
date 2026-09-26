import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import ErrorMessage from '../../components/common/ErrorMessage';
import { getErrorMessage } from '../../utils/helpers';

export default function RegisterPage() {
  const { register, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) {
    navigate('/', { replace: true });
    return null;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(form);
      navigate('/', { replace: true });
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

  const labelStyle = {
    display: 'block',
    fontSize: '11px',
    fontWeight: 500,
    textTransform: 'uppercase',
    letterSpacing: '0.08em',
    color: '#78716C',
    marginBottom: '6px',
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

        {/* Register Card */}
        <div style={{
          background: '#FFFFFF',
          borderRadius: '12px',
          border: '1px solid #E8E2D9',
          padding: '36px 32px',
          boxShadow: '0 4px 16px rgba(28,25,23,0.04)',
        }}>
          <h2 style={{ fontSize: '21px', fontWeight: 600, color: '#1C1917', marginBottom: '4px', letterSpacing: '-0.02em' }}>
            Create your account
          </h2>
          <p style={{ fontSize: '14px', color: '#78716C', marginBottom: '28px' }}>
            Start shopping in seconds
          </p>

          {error && <div style={{ marginBottom: '16px' }}><ErrorMessage message={error} /></div>}

          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: '16px' }}>
              <label style={labelStyle}>Full name</label>
              <input type="text" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="John Doe" required style={inputStyle}
                onFocus={(e) => { e.target.style.borderColor = '#2D6A4F'; }}
                onBlur={(e) => { e.target.style.borderColor = '#D6D0C8'; }}
              />
            </div>
            <div style={{ marginBottom: '16px' }}>
              <label style={labelStyle}>Email</label>
              <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="you@example.com" required style={inputStyle}
                onFocus={(e) => { e.target.style.borderColor = '#2D6A4F'; }}
                onBlur={(e) => { e.target.style.borderColor = '#D6D0C8'; }}
              />
            </div>
            <div style={{ marginBottom: '24px' }}>
              <label style={labelStyle}>Password</label>
              <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="Min 8 characters" required minLength={8} style={inputStyle}
                onFocus={(e) => { e.target.style.borderColor = '#2D6A4F'; }}
                onBlur={(e) => { e.target.style.borderColor = '#D6D0C8'; }}
              />
            </div>

            <button type="submit" disabled={loading} style={{
              width: '100%', padding: '12px 20px', fontSize: '14px', fontWeight: 500,
              color: '#F5F0E8', background: '#1C1917', border: 'none', borderRadius: '8px',
              cursor: loading ? 'not-allowed' : 'pointer', opacity: loading ? 0.7 : 1,
              transition: 'all 0.15s ease',
            }}
              onMouseDown={(e) => { if (!loading) e.currentTarget.style.transform = 'scale(0.97)'; }}
              onMouseUp={(e) => { e.currentTarget.style.transform = 'scale(1)'; }}
            >
              {loading ? 'Creating account…' : 'Create account'}
            </button>
          </form>
        </div>

        <p style={{ textAlign: 'center', marginTop: '20px', fontSize: '13px', color: '#78716C' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ color: '#2D6A4F', fontWeight: 500 }}>Sign in</Link>
        </p>
      </div>
    </div>
  );
}
