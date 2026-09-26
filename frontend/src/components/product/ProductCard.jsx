import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { formatPrice } from '../../utils/helpers';
import { useCart } from '../../hooks/useCart';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../common/Toast';
import { getErrorMessage } from '../../utils/helpers';

export default function ProductCard({ product }) {
  const { addToCart } = useCart();
  const { isAuthenticated, isAdmin } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();
  const [adding, setAdding] = useState(false);
  const [added, setAdded] = useState(false);
  const [hovered, setHovered] = useState(false);

  const handleAddToCart = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!isAuthenticated) {
      addToast('Please sign in to add items to your cart', 'error');
      navigate('/login');
      return;
    }
    if (adding || added) return;
    setAdding(true);
    try {
      await addToCart(product.id, 1);
      setAdded(true);
      addToast(`${product.name} added to cart`);
      setTimeout(() => setAdded(false), 2000);
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    } finally {
      setAdding(false);
    }
  };

  return (
    <Link
      to={`/products/${product.id}`}
      style={{
        display: 'block',
        border: '1px solid #E8E2D9',
        borderRadius: '12px',
        background: '#FFFFFF',
        overflow: 'hidden',
        transition: 'transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.25s ease, border-color 0.2s ease',
        transform: hovered ? 'translateY(-3px)' : 'translateY(0)',
        boxShadow: hovered ? '0 8px 24px rgba(28,25,23,0.10)' : '0 1px 3px rgba(28,25,23,0.04)',
        borderColor: hovered ? '#C4B9A8' : '#E8E2D9',
      }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
    >
      {/* Image */}
      <div style={{
        background: '#FAF7F2',
        aspectRatio: '1 / 1',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '12px',
        overflow: 'hidden',
      }}>
        {product.imageUrl ? (
          <img
            src={product.imageUrl}
            alt={product.name}
            style={{
              width: '100%',
              height: '100%',
              objectFit: 'contain',
            }}
          />
        ) : (
          <div style={{
            width: '100%',
            height: '100%',
            background: '#EDE8E0',
            borderRadius: '8px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}>
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#C4B9A8" strokeWidth="1.5">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
              <circle cx="8.5" cy="8.5" r="1.5" />
              <path d="M21 15l-5-5L5 21" />
            </svg>
          </div>
        )}
      </div>

      {/* Info */}
      <div style={{ padding: '16px' }}>
        <div style={{
          fontSize: '11px',
          fontWeight: 500,
          textTransform: 'uppercase',
          letterSpacing: '0.08em',
          color: '#78716C',
          marginBottom: '4px',
        }}>
          {product.categoryName}
        </div>
        <div style={{
          fontSize: '14px',
          fontWeight: 500,
          color: '#1C1917',
          marginBottom: '8px',
          lineHeight: 1.4,
          display: '-webkit-box',
          WebkitLineClamp: 2,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden',
        }}>
          {product.name}
        </div>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '12px',
        }}>
          <span style={{ fontSize: '16px', fontWeight: 600, color: '#1C1917' }}>
            {formatPrice(product.price)}
          </span>
          {product.stock !== undefined && (
            <span style={{
              fontSize: '11px',
              fontWeight: 500,
              color: product.stock <= 0 ? '#DC2626' : product.stock <= 5 ? '#D97706' : '#059669',
            }}>
              {product.stock <= 0 ? 'Out of stock' : product.stock <= 5 ? `Only ${product.stock} left` : `${product.stock} in stock`}
            </span>
          )}
        </div>

        {!isAdmin && (
          <button
            onClick={handleAddToCart}
            disabled={adding || product.stock === 0}
            style={{
              width: '100%',
              padding: '10px 20px',
              borderRadius: '8px',
              fontSize: '13px',
              fontWeight: 500,
              border: 'none',
              cursor: product.stock === 0 ? 'not-allowed' : 'pointer',
              transition: 'all 0.15s ease',
              background: added ? '#2D6A4F' : product.stock === 0 ? '#E8E2D9' : '#1C1917',
              color: product.stock === 0 ? '#78716C' : '#F5F0E8',
              transform: 'scale(1)',
            }}
            onMouseEnter={(e) => {
              if (!added && product.stock > 0) e.currentTarget.style.background = '#2D6A4F';
            }}
            onMouseLeave={(e) => {
              if (!added && product.stock > 0) e.currentTarget.style.background = '#1C1917';
            }}
            onMouseDown={(e) => { if (product.stock > 0) e.currentTarget.style.transform = 'scale(0.97)'; }}
            onMouseUp={(e) => { e.currentTarget.style.transform = 'scale(1)'; }}
          >
            {product.stock === 0 ? 'Out of stock' : added ? '✓ Added' : adding ? 'Adding…' : 'Add to cart'}
          </button>
        )}
      </div>
    </Link>
  );
}
