import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useCart } from '../../hooks/useCart';
import { useState } from 'react';

export default function Navbar() {
  const { user, isAuthenticated, isAdmin, isCustomer, logout } = useAuth();
  const { cartCount, cartBounce } = useCart();
  const navigate = useNavigate();
  const location = useLocation();
  const [showDropdown, setShowDropdown] = useState(false);

  const handleLogout = () => {
    logout();
    setShowDropdown(false);
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  const linkStyle = (path) => ({
    fontSize: '14px',
    fontWeight: isActive(path) ? 500 : 400,
    color: isActive(path) ? '#1C1917' : '#78716C',
    transition: 'color 0.2s ease',
    padding: '4px 0',
    borderBottom: isActive(path) ? '1.5px solid #1C1917' : '1.5px solid transparent',
  });

  return (
    <nav style={{
      position: 'sticky',
      top: 0,
      zIndex: 1000,
      background: 'rgba(245, 240, 232, 0.92)',
      backdropFilter: 'blur(8px)',
      WebkitBackdropFilter: 'blur(8px)',
      borderBottom: '1px solid #E8E2D9',
      padding: '0 24px',
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        height: '56px',
      }}>
        {/* Logo */}
        <Link to={isAdmin ? '/admin' : '/'} style={{
          fontSize: '18px',
          fontWeight: 700,
          color: '#1C1917',
          letterSpacing: '-0.02em',
        }}>
          Store
        </Link>

        {/* Nav Links */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '24px' }}>
          {/* Shop link visible to everyone (except admins who have their own nav) */}
          {!isAdmin && (
            <>
              <Link to="/" style={linkStyle('/')}>Shop</Link>
              {isCustomer && (
                <>
                  <Link to="/orders" style={linkStyle('/orders')}>Orders</Link>
                  <Link to="/addresses" style={linkStyle('/addresses')}>Addresses</Link>
                </>
              )}
            </>
          )}

          {isAdmin && (
            <>
              <Link to="/admin" style={linkStyle('/admin')}>Dashboard</Link>
              <Link to="/admin/products" style={linkStyle('/admin/products')}>Products</Link>
              <Link to="/admin/categories" style={linkStyle('/admin/categories')}>Categories</Link>
              <Link to="/admin/orders" style={linkStyle('/admin/orders')}>Orders</Link>
              <Link to="/admin/payments" style={linkStyle('/admin/payments')}>Payments</Link>
            </>
          )}

          {/* Cart Icon — visible to everyone except admins; redirects to login if not authenticated */}
          {!isAdmin && (
            <Link to="/cart" style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#1C1917" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                <path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4z" />
                <line x1="3" y1="6" x2="21" y2="6" />
                <path d="M16 10a4 4 0 01-8 0" />
              </svg>
              {cartCount > 0 && (
                <span style={{
                  position: 'absolute',
                  top: '-6px',
                  right: '-10px',
                  background: '#1C1917',
                  color: '#F5F0E8',
                  fontSize: '10px',
                  fontWeight: 600,
                  minWidth: '18px',
                  height: '18px',
                  borderRadius: '100px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  padding: '0 5px',
                  animation: cartBounce ? 'popIn 0.3s ease' : 'none',
                }}>
                  {cartCount}
                </span>
              )}
            </Link>
          )}

          {/* User menu */}
          {isAuthenticated ? (
            <div style={{ position: 'relative' }}>
              <button
                onClick={() => setShowDropdown(!showDropdown)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  fontSize: '13px',
                  fontWeight: 500,
                  color: '#1C1917',
                  padding: '6px 12px',
                  borderRadius: '8px',
                  border: '1px solid #D6D0C8',
                  background: '#FFFFFF',
                  transition: 'all 0.15s ease',
                }}
              >
                {user?.name?.split(' ')[0]}
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#78716C" strokeWidth="2"><path d="M6 9l6 6 6-6"/></svg>
              </button>

              {showDropdown && (
                <div style={{
                  position: 'absolute',
                  top: 'calc(100% + 6px)',
                  right: 0,
                  background: '#FFFFFF',
                  border: '1px solid #E8E2D9',
                  borderRadius: '8px',
                  boxShadow: '0 4px 12px rgba(28,25,23,0.08)',
                  minWidth: '160px',
                  zIndex: 1001,
                  animation: 'scaleIn 0.15s ease forwards',
                  overflow: 'hidden',
                }}>
                  <div style={{ padding: '10px 14px', borderBottom: '1px solid #F0EBE3' }}>
                    <div style={{ fontSize: '13px', fontWeight: 500, color: '#1C1917' }}>{user?.name}</div>
                    <div style={{ fontSize: '11px', color: '#78716C' }}>{user?.email}</div>
                  </div>
                  {isCustomer && (
                    <Link
                      to="/profile"
                      onClick={() => setShowDropdown(false)}
                      style={{
                        display: 'block',
                        padding: '9px 14px',
                        fontSize: '13px',
                        color: '#1C1917',
                        transition: 'background 0.15s',
                      }}
                      onMouseEnter={(e) => e.target.style.background = '#FAF7F2'}
                      onMouseLeave={(e) => e.target.style.background = 'transparent'}
                    >
                      Profile
                    </Link>
                  )}
                  <button
                    onClick={handleLogout}
                    style={{
                      display: 'block',
                      width: '100%',
                      textAlign: 'left',
                      padding: '9px 14px',
                      fontSize: '13px',
                      color: '#9B1D20',
                      transition: 'background 0.15s',
                    }}
                    onMouseEnter={(e) => e.target.style.background = '#FEF2F2'}
                    onMouseLeave={(e) => e.target.style.background = 'transparent'}
                  >
                    Sign out
                  </button>
                </div>
              )}
            </div>
          ) : (
            <Link to="/login" style={{
              fontSize: '13px',
              fontWeight: 500,
              color: '#F5F0E8',
              background: '#1C1917',
              padding: '8px 18px',
              borderRadius: '8px',
              transition: 'all 0.15s ease',
            }}>
              Sign in
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
}
