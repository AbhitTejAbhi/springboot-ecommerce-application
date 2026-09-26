import { formatPrice } from '../../utils/helpers';

export default function CartItem({ item, onUpdateQuantity, onRemove, loading }) {
  return (
    <div style={{
      display: 'flex',
      gap: '16px',
      padding: '20px 0',
      borderBottom: '1px solid #F0EBE3',
      alignItems: 'flex-start',
    }}>
      {/* Image */}
      <div style={{
        width: '80px',
        height: '80px',
        flexShrink: 0,
        borderRadius: '8px',
        background: '#FAF7F2',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        overflow: 'hidden',
      }}>
        {item.productImageUrl ? (
          <img src={item.productImageUrl} alt={item.productName} style={{ width: '100%', height: '100%', objectFit: 'contain', padding: '4px' }} />
        ) : (
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#C4B9A8" strokeWidth="1.5"><rect x="3" y="3" width="18" height="18" rx="2" /><circle cx="8.5" cy="8.5" r="1.5" /><path d="M21 15l-5-5L5 21" /></svg>
        )}
      </div>

      {/* Info */}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
          <span style={{ fontSize: '14px', fontWeight: 500, color: '#1C1917', lineHeight: 1.4 }}>
            {item.productName}
          </span>
          {item.productActive === false && (
            <span style={{
              fontSize: '11px',
              fontWeight: 600,
              padding: '2px 8px',
              borderRadius: '4px',
              background: '#FEF2F2',
              color: '#DC2626',
            }}>
              Unavailable
            </span>
          )}
          {item.productActive !== false && item.productStock !== undefined && item.productStock < item.quantity && (
            <span style={{
              fontSize: '11px',
              fontWeight: 600,
              padding: '2px 8px',
              borderRadius: '4px',
              background: '#FEF3C7',
              color: '#92400E',
              border: '1px solid #FCD34D',
            }}>
              {item.productStock <= 0 ? 'Out of stock' : `Only ${item.productStock} left in stock`}
            </span>
          )}
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '12px' }}>
          <span style={{ fontSize: '14px', color: '#78716C' }}>
            {formatPrice(item.productPrice)} each
          </span>
          {item.productActive !== false && item.productStock !== undefined && (
            <span style={{
              fontSize: '12px',
              fontWeight: 500,
              color: item.productStock <= 0 ? '#DC2626' : (item.productStock <= 5 || item.quantity > item.productStock) ? '#D97706' : '#059669',
            }}>
              • {item.productStock <= 0 ? 'Out of stock' : item.productStock <= 5 ? `Only ${item.productStock} left in stock` : `${item.productStock} in stock`}
            </span>
          )}
        </div>

        {/* Quantity controls */}
        {(() => {
          const isUnavailable = item.productActive === false;
          const isMaxStockReached = item.productStock !== undefined && item.quantity >= item.productStock;
          return (
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              {!isUnavailable && (
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  border: '1px solid #D6D0C8',
                  borderRadius: '8px',
                  overflow: 'hidden',
                }}>
                  <button
                    onClick={() => onUpdateQuantity(item.cartItemId, item.quantity - 1)}
                    disabled={item.quantity <= 1 || loading}
                    style={{
                      width: '32px', height: '32px',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      fontSize: '16px', color: item.quantity <= 1 ? '#D6D0C8' : '#1C1917',
                      cursor: item.quantity <= 1 ? 'not-allowed' : 'pointer',
                      transition: 'background 0.15s',
                    }}
                    onMouseEnter={(e) => { if (item.quantity > 1) e.currentTarget.style.background = '#FAF7F2'; }}
                    onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
                  >
                    −
                  </button>
                  <span style={{ width: '36px', textAlign: 'center', fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>
                    {item.quantity}
                  </span>
                  <button
                    onClick={() => onUpdateQuantity(item.cartItemId, item.quantity + 1)}
                    disabled={isMaxStockReached || loading}
                    style={{
                      width: '32px', height: '32px',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      fontSize: '16px', color: isMaxStockReached ? '#D6D0C8' : '#1C1917',
                      cursor: isMaxStockReached ? 'not-allowed' : 'pointer',
                      transition: 'background 0.15s',
                    }}
                    onMouseEnter={(e) => { if (!isMaxStockReached) e.currentTarget.style.background = '#FAF7F2'; }}
                    onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
                  >
                    +
                  </button>
                </div>
              )}

              <button
                onClick={() => onRemove(item.cartItemId)}
                disabled={loading}
                style={{
                  fontSize: '12px', fontWeight: 500, color: '#9B1D20',
                  padding: '6px 10px', borderRadius: '6px',
                  transition: 'background 0.15s', cursor: 'pointer',
                }}
                onMouseEnter={(e) => { e.currentTarget.style.background = '#F8DCDC'; }}
                onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
              >
                Remove
              </button>
            </div>
          );
        })()}
      </div>

      {/* Line total */}
      <div style={{
        fontSize: '15px', fontWeight: 600, color: '#1C1917',
        whiteSpace: 'nowrap', paddingTop: '2px',
      }}>
        {formatPrice(item.itemTotal)}
      </div>
    </div>
  );
}
