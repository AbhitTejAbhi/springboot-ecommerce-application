import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProductById } from '../../api/productApi';
import { useCart } from '../../hooks/useCart';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../components/common/Toast';
import { formatPrice, getErrorMessage } from '../../utils/helpers';
import Loader from '../../components/common/Loader';

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const { isAuthenticated, isAdmin } = useAuth();
  const { addToast } = useToast();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [adding, setAdding] = useState(false);
  const [added, setAdded] = useState(false);
  const [activeThumb, setActiveThumb] = useState(0);
  const [imgAnimKey, setImgAnimKey] = useState(0);

  useEffect(() => {
    setLoading(true);
    getProductById(id)
      .then((res) => setProduct(res.data.data))
      .catch(() => navigate('/', { replace: true }))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      addToast('Please sign in to add items to your cart', 'error');
      navigate('/login');
      return;
    }
    if (adding) return;
    setAdding(true);
    try {
      await addToCart(product.id, quantity);
      setAdded(true);
      addToast(`${product.name} added to cart`);
      setTimeout(() => setAdded(false), 2000);
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    } finally {
      setAdding(false);
    }
  };

  // Generate thumbnail views from single image (simulated multi-angle)
  const getThumbnails = () => {
    if (!product?.imageUrl) return [];
    // We use the same image but the UI is ready for multiple images
    return [product.imageUrl, product.imageUrl, product.imageUrl, product.imageUrl];
  };

  const handleThumbClick = (index) => {
    if (index !== activeThumb) {
      setActiveThumb(index);
      setImgAnimKey((k) => k + 1);
    }
  };

  if (loading) return <Loader />;
  if (!product) return null;

  const thumbnails = getThumbnails();
  const hasImage = !!product.imageUrl;

  return (
    <div style={{
      maxWidth: '1200px',
      margin: '0 auto',
      padding: '32px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      {/* Back Button */}
      <button
        onClick={() => navigate(-1)}
        style={{
          fontSize: '13px',
          fontWeight: 500,
          color: '#78716C',
          marginBottom: '28px',
          display: 'flex',
          alignItems: 'center',
          gap: '6px',
          cursor: 'pointer',
          transition: 'color 0.2s',
        }}
        onMouseEnter={(e) => { e.currentTarget.style.color = '#1C1917'; }}
        onMouseLeave={(e) => { e.currentTarget.style.color = '#78716C'; }}
      >
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
        Back
      </button>

      {/* Main Product Layout */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '56px',
        alignItems: 'start',
      }}>
        {/* ===== LEFT: Image Gallery ===== */}
        <div style={{
          display: 'flex',
          gap: '16px',
          alignItems: 'flex-start',
        }}>
          {/* Vertical Thumbnail Strip */}
          {hasImage && (
            <div style={{
              display: 'flex',
              flexDirection: 'column',
              gap: '10px',
              flexShrink: 0,
            }}>
              {thumbnails.map((thumb, i) => (
                <button
                  key={i}
                  onClick={() => handleThumbClick(i)}
                  style={{
                    width: '64px',
                    height: '64px',
                    borderRadius: '10px',
                    border: activeThumb === i ? '2px solid #1C1917' : '1.5px solid #E8E2D9',
                    background: '#FAFAFA',
                    padding: '6px',
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                    opacity: activeThumb === i ? 1 : 0.65,
                    transform: activeThumb === i ? 'scale(1.05)' : 'scale(1)',
                    boxShadow: activeThumb === i ? '0 2px 8px rgba(28,25,23,0.1)' : 'none',
                  }}
                  onMouseEnter={(e) => {
                    if (activeThumb !== i) {
                      e.currentTarget.style.opacity = '0.9';
                      e.currentTarget.style.borderColor = '#C4B9A8';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (activeThumb !== i) {
                      e.currentTarget.style.opacity = '0.65';
                      e.currentTarget.style.borderColor = '#E8E2D9';
                    }
                  }}
                >
                  <img
                    src={thumb}
                    alt={`${product.name} view ${i + 1}`}
                    style={{
                      width: '100%',
                      height: '100%',
                      objectFit: 'contain',
                      borderRadius: '6px',
                    }}
                  />
                </button>
              ))}
            </div>
          )}

          {/* Main Image */}
          <div style={{
            flex: 1,
            background: '#FAFAFA',
            borderRadius: '16px',
            aspectRatio: '1 / 1',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '32px',
            position: 'relative',
            overflow: 'hidden',
            border: '1px solid #F0EBE3',
          }}>
            {hasImage ? (
              <img
                key={imgAnimKey}
                src={product.imageUrl}
                alt={product.name}
                style={{
                  width: '100%',
                  height: '100%',
                  objectFit: 'contain',
                  animation: 'imageFadeIn 0.4s ease-out',
                }}
              />
            ) : (
              <div style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                gap: '12px',
                color: '#C4B9A8',
              }}>
                <svg width="72" height="72" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                  <rect x="3" y="3" width="18" height="18" rx="2" />
                  <circle cx="8.5" cy="8.5" r="1.5" />
                  <path d="M21 15l-5-5L5 21" />
                </svg>
                <span style={{ fontSize: '13px', fontWeight: 500 }}>No image available</span>
              </div>
            )}
          </div>
        </div>

        {/* ===== RIGHT: Product Info ===== */}
        <div style={{
          animation: 'slideInRight 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards',
          paddingTop: '8px',
        }}>
          {/* Product Name */}
          <h1 style={{
            fontSize: '28px',
            fontWeight: 700,
            color: '#1C1917',
            letterSpacing: '-0.02em',
            lineHeight: 1.25,
            marginBottom: '6px',
          }}>
            {product.name}
          </h1>

          {/* Category (subtitle) */}
          <div style={{
            fontSize: '15px',
            fontWeight: 400,
            color: '#78716C',
            marginBottom: '24px',
          }}>
            {product.categoryName}
          </div>

          {product.active === false && (
            <div style={{
              marginBottom: '20px',
              padding: '12px 16px',
              borderRadius: '8px',
              background: '#FEF2F2',
              color: '#9B1D20',
              border: '1px solid #F8DCDC',
              fontSize: '14px',
              fontWeight: 500,
            }}>
              This product is currently discontinued / unavailable for purchase.
            </div>
          )}

          {/* Divider */}
          <div style={{ height: '1px', background: '#F0EBE3', marginBottom: '20px' }} />

          {/* Price */}
          <div style={{ marginBottom: '4px' }}>
            <span style={{
              fontSize: '14px',
              fontWeight: 500,
              color: '#78716C',
              marginRight: '8px',
            }}>
              MRP :
            </span>
            <span style={{
              fontSize: '22px',
              fontWeight: 700,
              color: '#1C1917',
              letterSpacing: '-0.01em',
            }}>
              {formatPrice(product.price)}
            </span>
          </div>

          {/* Tax note */}
          <div style={{
            fontSize: '12px',
            color: '#A8A29E',
            marginBottom: '6px',
          }}>
            incl. of taxes
          </div>
          <div style={{
            fontSize: '12px',
            color: '#A8A29E',
            marginBottom: '24px',
          }}>
            (also includes all applicable duties)
          </div>

          {/* Stock Status */}
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '6px',
            padding: '6px 14px',
            borderRadius: '100px',
            fontSize: '13px',
            fontWeight: 500,
            marginBottom: '24px',
            background: product.stock <= 0 ? '#FEF2F2' : product.stock <= 5 ? '#FEF3C7' : '#ECFDF5',
            color: product.stock <= 0 ? '#DC2626' : product.stock <= 5 ? '#92400E' : '#059669',
          }}>
            <span style={{
              width: '7px',
              height: '7px',
              borderRadius: '50%',
              background: product.stock <= 0 ? '#DC2626' : product.stock <= 5 ? '#D97706' : '#059669',
            }} />
            {product.stock <= 0 ? 'Out of Stock' : product.stock <= 5 ? `Only ${product.stock} left in stock` : `${product.stock} units in stock`}
          </div>

          {/* Description */}
          {product.description && (
            <>
              <div style={{ height: '1px', background: '#F0EBE3', marginBottom: '20px' }} />
              <p style={{
                fontSize: '14px',
                color: '#57534E',
                lineHeight: 1.7,
                marginBottom: '28px',
              }}>
                {product.description}
              </p>
            </>
          )}

          {/* Quantity + Add to Cart */}
          {!isAdmin && product.active !== false && product.stock > 0 && (
            <>
              {/* Quantity Selector */}
              <div style={{ marginBottom: '16px' }}>
                <div style={{
                  fontSize: '13px',
                  fontWeight: 600,
                  color: '#1C1917',
                  marginBottom: '10px',
                  textTransform: 'uppercase',
                  letterSpacing: '0.05em',
                }}>
                  Quantity
                </div>
                <div style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  border: '1.5px solid #E5E5E5',
                  borderRadius: '100px',
                  overflow: 'hidden',
                  background: '#FFFFFF',
                }}>
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    disabled={quantity <= 1}
                    style={{
                      width: '42px',
                      height: '42px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '18px',
                      fontWeight: 400,
                      color: quantity <= 1 ? '#D4D4D4' : '#1C1917',
                      cursor: quantity <= 1 ? 'not-allowed' : 'pointer',
                      transition: 'all 0.15s',
                      borderRadius: '50%',
                    }}
                    onMouseEnter={(e) => { if (quantity > 1) e.currentTarget.style.background = '#F5F5F5'; }}
                    onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
                  >−</button>
                  <span style={{
                    width: '44px',
                    textAlign: 'center',
                    fontSize: '15px',
                    fontWeight: 600,
                    color: '#1C1917',
                    userSelect: 'none',
                  }}>{quantity}</span>
                  <button
                    onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}
                    disabled={quantity >= product.stock}
                    style={{
                      width: '42px',
                      height: '42px',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '18px',
                      fontWeight: 400,
                      color: quantity >= product.stock ? '#D4D4D4' : '#1C1917',
                      cursor: quantity >= product.stock ? 'not-allowed' : 'pointer',
                      transition: 'all 0.15s',
                      borderRadius: '50%',
                    }}
                    onMouseEnter={(e) => { if (quantity < product.stock) e.currentTarget.style.background = '#F5F5F5'; }}
                    onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
                  >+</button>
                </div>
                {quantity >= product.stock && (
                  <div style={{ fontSize: '12px', color: '#D97706', marginTop: '6px' }}>
                    Maximum available stock reached ({product.stock})
                  </div>
                )}
              </div>

              {/* Add to Cart Button */}
              <button
                id="add-to-cart-btn"
                onClick={handleAddToCart}
                disabled={adding}
                style={{
                  width: '100%',
                  padding: '16px 32px',
                  fontSize: '16px',
                  fontWeight: 600,
                  borderRadius: '100px',
                  border: 'none',
                  cursor: 'pointer',
                  background: added ? '#059669' : '#1C1917',
                  color: '#FFFFFF',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '10px',
                  transition: 'all 0.25s cubic-bezier(0.16, 1, 0.3, 1)',
                  marginBottom: '12px',
                  boxShadow: '0 4px 14px rgba(28, 25, 23, 0.2)',
                }}
                onMouseEnter={(e) => {
                  if (!added) {
                    e.currentTarget.style.background = '#2D6A4F';
                    e.currentTarget.style.boxShadow = '0 6px 20px rgba(45, 106, 79, 0.3)';
                    e.currentTarget.style.transform = 'translateY(-1px)';
                  }
                }}
                onMouseLeave={(e) => {
                  if (!added) {
                    e.currentTarget.style.background = '#1C1917';
                    e.currentTarget.style.boxShadow = '0 4px 14px rgba(28, 25, 23, 0.2)';
                    e.currentTarget.style.transform = 'translateY(0)';
                  }
                }}
                onMouseDown={(e) => { e.currentTarget.style.transform = 'scale(0.98)'; }}
                onMouseUp={(e) => { e.currentTarget.style.transform = 'scale(1)'; }}
              >
                {added ? (
                  <>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M20 6L9 17l-5-5" />
                    </svg>
                    Added to Cart
                  </>
                ) : adding ? (
                  'Adding…'
                ) : (
                  <>
                    Add to Cart
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <circle cx="9" cy="21" r="1" />
                      <circle cx="20" cy="21" r="1" />
                      <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" />
                    </svg>
                  </>
                )}
              </button>
            </>
          )}

          {/* Out of stock message for customers */}
          {!isAdmin && product.stock <= 0 && (
            <div style={{
              padding: '16px 24px',
              borderRadius: '12px',
              background: '#FEF2F2',
              color: '#DC2626',
              fontSize: '14px',
              fontWeight: 500,
              textAlign: 'center',
              marginTop: '8px',
            }}>
              This item is currently out of stock
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
