import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getOrderDetails, cancelOrder } from '../../api/orderApi';
import { useToast } from '../../components/common/Toast';
import Badge from '../../components/common/Badge';
import Loader from '../../components/common/Loader';
import { formatPrice, formatDate, getErrorMessage } from '../../utils/helpers';

export default function OrderDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToast } = useToast();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [cancelling, setCancelling] = useState(false);

  useEffect(() => {
    setLoading(true);
    getOrderDetails(id)
      .then((res) => setOrder(res.data.data))
      .catch(() => navigate('/orders', { replace: true }))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleCancel = async () => {
    if (!window.confirm('Are you sure you want to cancel this order?')) return;
    setCancelling(true);
    try {
      const res = await cancelOrder(id);
      setOrder(res.data.data);
      addToast('Order cancelled successfully');
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    } finally {
      setCancelling(false);
    }
  };

  if (loading) return <Loader />;
  if (!order) return null;

  const canCancel = ['PENDING', 'CONFIRMED'].includes(order.orderStatus);
  const needsPayment = !order.paymentStatus && order.orderStatus !== 'CANCELLED';

  return (
    <div style={{
      maxWidth: '800px', margin: '0 auto', padding: '32px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      <button onClick={() => navigate('/orders')} style={{ fontSize: '13px', fontWeight: 500, color: '#78716C', marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '4px', cursor: 'pointer' }}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        Back to orders
      </button>

      {/* Header */}
      <div style={{ background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', padding: '24px', marginBottom: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <div style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C', marginBottom: '4px' }}>Order #{order.orderId}</div>
            <div style={{ fontSize: '21px', fontWeight: 600, color: '#1C1917' }}>{formatPrice(order.totalAmount)}</div>
            <div style={{ fontSize: '12px', color: '#78716C', marginTop: '4px' }}>{formatDate(order.createdAt)}</div>
          </div>
          <div style={{ display: 'flex', gap: '20px', flexWrap: 'wrap', alignItems: 'center' }}>
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '4px' }}>
              <span style={{ fontSize: '10px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C' }}>
                Order Status
              </span>
              <Badge status={order.orderStatus} />
            </div>
            {order.paymentStatus && (
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '4px' }}>
                <span style={{ fontSize: '10px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C' }}>
                  Payment Status
                </span>
                <Badge status={order.paymentStatus} />
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Shipping Address */}
      {order.shippingAddress && (
        <div style={{ background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', padding: '24px', marginBottom: '20px' }}>
          <h3 style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C', marginBottom: '8px' }}>Shipping address</h3>
          <p style={{ fontSize: '14px', color: '#1C1917', lineHeight: 1.5 }}>{order.shippingAddress}</p>
        </div>
      )}

      {/* Items */}
      <div style={{ background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', overflow: 'hidden', marginBottom: '20px' }}>
        <div style={{ padding: '16px 24px', borderBottom: '1px solid #E8E2D9' }}>
          <h3 style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C' }}>Items</h3>
        </div>
        {order.orderItems?.map((item) => (
          <div key={item.orderItemId} style={{ display: 'flex', alignItems: 'center', gap: '16px', padding: '16px 24px', borderBottom: '1px solid #F0EBE3' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '8px', background: '#FAF7F2', flexShrink: 0, display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden' }}>
              {item.productImageUrl ? (
                <img src={item.productImageUrl} alt={item.productName} style={{ width: '100%', height: '100%', objectFit: 'contain', padding: '4px' }} />
              ) : (
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C4B9A8" strokeWidth="1.5"><rect x="3" y="3" width="18" height="18" rx="2" /></svg>
              )}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>{item.productName}</div>
              <div style={{ fontSize: '12px', color: '#78716C' }}>{formatPrice(item.priceAtPurchase)} × {item.quantity}</div>
            </div>
            <div style={{ fontSize: '14px', fontWeight: 600, color: '#1C1917' }}>{formatPrice(item.subtotal)}</div>
          </div>
        ))}
      </div>

      {/* Actions */}
      <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
        {needsPayment && (
          <Link to={`/payment/${order.orderId}`} style={{
            padding: '10px 24px', fontSize: '13px', fontWeight: 500,
            background: '#1C1917', color: '#F5F0E8', borderRadius: '8px',
            transition: 'all 0.15s ease', display: 'inline-block',
          }}>
            Make payment
          </Link>
        )}
        {canCancel && (
          <button onClick={handleCancel} disabled={cancelling} style={{
            padding: '10px 24px', fontSize: '13px', fontWeight: 500,
            background: '#F8DCDC', color: '#9B1D20', borderRadius: '8px',
            border: '1px solid #F0C8C8', cursor: 'pointer', transition: 'all 0.15s ease',
          }}>
            {cancelling ? 'Cancelling…' : 'Cancel order'}
          </button>
        )}
      </div>
    </div>
  );
}
