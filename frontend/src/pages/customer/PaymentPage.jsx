import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getOrderDetails } from '../../api/orderApi';
import { createPayment } from '../../api/paymentApi';
import { useToast } from '../../components/common/Toast';
import Loader from '../../components/common/Loader';
import { formatPrice, getErrorMessage } from '../../utils/helpers';

const PAYMENT_METHODS = [
  { value: 'UPI', label: 'UPI' },
  { value: 'CARD', label: 'Card' },
  { value: 'NET_BANKING', label: 'Net Banking' },
  { value: 'COD', label: 'Cash on Delivery' },
];

export default function PaymentPage() {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const { addToast } = useToast();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedMethod, setSelectedMethod] = useState('UPI');
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    getOrderDetails(orderId)
      .then((res) => setOrder(res.data.data))
      .catch(() => navigate('/orders', { replace: true }))
      .finally(() => setLoading(false));
  }, [orderId, navigate]);

  const handlePayment = async () => {
    setProcessing(true);
    try {
      await createPayment({ orderId: Number(orderId), paymentMethod: selectedMethod });
      addToast('Payment created successfully!');
      navigate(`/orders/${orderId}`);
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    } finally {
      setProcessing(false);
    }
  };

  if (loading) return <Loader />;
  if (!order) return null;

  return (
    <div style={{
      maxWidth: '560px', margin: '0 auto', padding: '32px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      <button onClick={() => navigate(`/orders/${orderId}`)} style={{ fontSize: '13px', fontWeight: 500, color: '#78716C', marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '4px', cursor: 'pointer' }}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        Back to order
      </button>

      <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em', marginBottom: '32px' }}>
        Payment
      </h1>

      {/* Order summary */}
      <div style={{ background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', padding: '24px', marginBottom: '24px' }}>
        <div style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C', marginBottom: '8px' }}>Order #{order.orderId}</div>
        <div style={{ fontSize: '21px', fontWeight: 600, color: '#1C1917' }}>{formatPrice(order.totalAmount)}</div>
      </div>

      {/* Payment method */}
      <div style={{ marginBottom: '32px' }}>
        <h3 style={{ fontSize: '14px', fontWeight: 500, color: '#78716C', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '12px' }}>
          Payment method
        </h3>
        <div style={{ display: 'grid', gap: '8px' }}>
          {PAYMENT_METHODS.map((method) => (
            <button
              key={method.value}
              onClick={() => setSelectedMethod(method.value)}
              style={{
                display: 'flex', alignItems: 'center', gap: '12px',
                padding: '14px 16px', borderRadius: '8px',
                border: `1px solid ${selectedMethod === method.value ? '#2D6A4F' : '#D6D0C8'}`,
                background: selectedMethod === method.value ? '#FAF7F2' : '#FFFFFF',
                cursor: 'pointer', transition: 'all 0.15s ease', textAlign: 'left',
              }}
            >
              <div style={{
                width: '16px', height: '16px', borderRadius: '50%',
                border: `2px solid ${selectedMethod === method.value ? '#2D6A4F' : '#D6D0C8'}`,
                display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
              }}>
                {selectedMethod === method.value && <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: '#2D6A4F' }} />}
              </div>
              <span style={{ fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>{method.label}</span>
            </button>
          ))}
        </div>
      </div>

      <button onClick={handlePayment} disabled={processing} style={{
        width: '100%', padding: '14px 20px', fontSize: '15px', fontWeight: 500,
        background: '#1C1917', color: '#F5F0E8', borderRadius: '8px', border: 'none',
        cursor: processing ? 'not-allowed' : 'pointer', opacity: processing ? 0.7 : 1,
        transition: 'all 0.15s ease',
      }}
        onMouseDown={(e) => { if (!processing) e.currentTarget.style.transform = 'scale(0.97)'; }}
        onMouseUp={(e) => { e.currentTarget.style.transform = 'scale(1)'; }}
      >
        {processing ? 'Processing…' : `Pay ${formatPrice(order.totalAmount)}`}
      </button>
    </div>
  );
}
