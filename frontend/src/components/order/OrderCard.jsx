import { Link } from 'react-router-dom';
import Badge from '../common/Badge';
import { formatPrice, formatDateShort } from '../../utils/helpers';

export default function OrderCard({ order, linkPrefix = '/orders' }) {
  return (
    <Link
      to={`${linkPrefix}/${order.orderId}`}
      style={{
        display: 'block',
        border: '1px solid #E8E2D9',
        borderRadius: '12px',
        background: '#FFFFFF',
        padding: '20px 24px',
        transition: 'border-color 0.2s ease, box-shadow 0.2s ease',
      }}
      onMouseEnter={(e) => { e.currentTarget.style.borderColor = '#C4B9A8'; e.currentTarget.style.boxShadow = '0 4px 12px rgba(28,25,23,0.06)'; }}
      onMouseLeave={(e) => { e.currentTarget.style.borderColor = '#E8E2D9'; e.currentTarget.style.boxShadow = 'none'; }}
    >
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <div style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C', marginBottom: '4px' }}>
            Order #{order.orderId}
          </div>
          <div style={{ fontSize: '15px', fontWeight: 600, color: '#1C1917' }}>
            {formatPrice(order.totalAmount)}
          </div>
          <div style={{ fontSize: '12px', color: '#78716C', marginTop: '4px' }}>
            {formatDateShort(order.createdAt)} · {order.orderItems?.length || 0} item{(order.orderItems?.length || 0) !== 1 ? 's' : ''}
          </div>
        </div>
        <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
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
    </Link>
  );
}
