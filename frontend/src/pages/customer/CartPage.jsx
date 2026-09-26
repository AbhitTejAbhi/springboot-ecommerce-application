import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';
import { useToast } from '../../components/common/Toast';
import CartItem from '../../components/cart/CartItem';
import EmptyState from '../../components/common/EmptyState';
import Loader from '../../components/common/Loader';
import { formatPrice, getErrorMessage } from '../../utils/helpers';

export default function CartPage() {
  const { cart, updateQuantity, removeItem, refreshCart } = useCart();
  const { addToast } = useToast();
  const [actionLoading, setActionLoading] = useState(false);
  const [loadingCart, setLoadingCart] = useState(!cart);

  useState(() => {
    if (!cart) {
      refreshCart().finally(() => setLoadingCart(false));
    }
  });

  const handleUpdateQuantity = async (cartItemId, newQty) => {
    if (newQty < 1) return;
    setActionLoading(true);
    try {
      await updateQuantity(cartItemId, newQty);
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    } finally {
      setActionLoading(false);
    }
  };

  const handleRemove = async (cartItemId) => {
    setActionLoading(true);
    try {
      await removeItem(cartItemId);
      addToast('Item removed from cart');
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    } finally {
      setActionLoading(false);
    }
  };

  if (loadingCart) return <Loader />;

  const items = cart?.items || [];
  const isEmpty = items.length === 0;

  return (
    <div style={{
      maxWidth: '800px', margin: '0 auto', padding: '32px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em', marginBottom: '32px' }}>
        Cart
      </h1>

      {isEmpty ? (
        <EmptyState
          icon="🛒"
          title="Your cart is empty"
          subtitle="Browse our collection and add items to get started"
          action={
            <Link to="/" style={{
              display: 'inline-block', padding: '10px 24px', fontSize: '13px', fontWeight: 500,
              background: '#1C1917', color: '#F5F0E8', borderRadius: '8px',
              transition: 'all 0.15s ease',
            }}>
              Start shopping
            </Link>
          }
        />
      ) : (
        <>
          {/* Items */}
          <div style={{ marginBottom: '32px' }}>
            {items.map((item) => (
              <CartItem
                key={item.cartItemId}
                item={item}
                onUpdateQuantity={handleUpdateQuantity}
                onRemove={handleRemove}
                loading={actionLoading}
              />
            ))}
          </div>

          {/* Summary */}
          {items.some((item) => item.productActive === false) && (
            <div style={{
              marginBottom: '16px',
              padding: '12px 16px',
              fontSize: '13px',
              borderRadius: '8px',
              background: '#FEF2F2',
              color: '#9B1D20',
              border: '1px solid #F8DCDC',
            }}>
              Some items in your cart are currently unavailable and have been excluded from your total. Please remove them to proceed.
            </div>
          )}

          {items.some((item) => item.productActive !== false && item.productStock !== undefined && item.productStock < item.quantity) && (
            <div style={{
              marginBottom: '20px',
              padding: '12px 16px',
              fontSize: '13px',
              borderRadius: '8px',
              background: '#FEF3C7',
              color: '#92400E',
              border: '1px solid #FCD34D',
            }}>
              Some items in your cart exceed available stock. Please lower their quantities using the − button to proceed to checkout.
            </div>
          )}

          {(() => {
            const hasInvalidItems = items.some(
              (item) => item.productActive === false || (item.productStock !== undefined && item.productStock < item.quantity)
            );

            return (
              <div style={{
                background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px',
                padding: '24px',
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <span style={{ fontSize: '14px', color: '#78716C' }}>Subtotal ({cart.totalItems} active {cart.totalItems === 1 ? 'item' : 'items'})</span>
                  <span style={{ fontSize: '17px', fontWeight: 600, color: '#1C1917' }}>{formatPrice(cart.grandTotal)}</span>
                </div>
                <div style={{ fontSize: '12px', color: '#78716C', marginBottom: '20px' }}>
                  Shipping calculated at checkout
                </div>
                {hasInvalidItems ? (
                  <button
                    disabled
                    style={{
                      width: '100%', padding: '12px 20px', fontSize: '14px', fontWeight: 500,
                      background: '#A8A29E', color: '#F5F0E8', borderRadius: '8px',
                      cursor: 'not-allowed', border: 'none',
                    }}
                  >
                    Proceed to checkout
                  </button>
                ) : (
                  <Link to="/checkout" style={{
                    display: 'block', width: '100%', textAlign: 'center',
                    padding: '12px 20px', fontSize: '14px', fontWeight: 500,
                    background: '#1C1917', color: '#F5F0E8', borderRadius: '8px',
                    transition: 'all 0.15s ease',
                  }}>
                    Proceed to checkout
                  </Link>
                )}
              </div>
            );
          })()}
        </>
      )}
    </div>
  );
}
