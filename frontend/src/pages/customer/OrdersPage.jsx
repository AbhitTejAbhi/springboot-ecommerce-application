import { useState, useEffect } from 'react';
import { getMyOrders } from '../../api/orderApi';
import OrderCard from '../../components/order/OrderCard';
import EmptyState from '../../components/common/EmptyState';
import Loader from '../../components/common/Loader';
import { Link } from 'react-router-dom';

import ErrorMessage from '../../components/common/ErrorMessage';
import { getErrorMessage } from '../../utils/helpers';

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    setError('');
    getMyOrders(page, 10)
      .then((res) => {
        const data = res.data.data;
        setOrders(data.content || []);
        setTotalPages(data.totalPages || 0);
      })
      .catch((err) => {
        console.error('Failed to load orders:', err);
        setError(getErrorMessage(err));
        setOrders([]);
      })
      .finally(() => setLoading(false));
  }, [page]);

  return (
    <div style={{
      maxWidth: '800px', margin: '0 auto', padding: '32px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em', marginBottom: '32px' }}>
        My Orders
      </h1>

      {error && <div style={{ marginBottom: '24px' }}><ErrorMessage message={error} /></div>}

      {loading ? <Loader /> : orders.length === 0 ? (
        <EmptyState
          icon="📦"
          title="No orders yet"
          subtitle="Place your first order to see it here"
          action={<Link to="/" style={{ display: 'inline-block', padding: '10px 24px', fontSize: '13px', fontWeight: 500, background: '#1C1917', color: '#F5F0E8', borderRadius: '8px' }}>Start shopping</Link>}
        />
      ) : (
        <>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {orders.map((order) => <OrderCard key={order.orderId} order={order} />)}
          </div>

          {totalPages > 1 && (
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginTop: '32px' }}>
              <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0} style={{ padding: '8px 16px', fontSize: '13px', fontWeight: 500, borderRadius: '8px', border: '1px solid #D6D0C8', background: page === 0 ? '#F5F0E8' : '#FFFFFF', color: page === 0 ? '#A8A29E' : '#1C1917', cursor: page === 0 ? 'not-allowed' : 'pointer' }}>Previous</button>
              <span style={{ fontSize: '13px', color: '#78716C' }}>{page + 1} of {totalPages}</span>
              <button onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page >= totalPages - 1} style={{ padding: '8px 16px', fontSize: '13px', fontWeight: 500, borderRadius: '8px', border: '1px solid #D6D0C8', background: page >= totalPages - 1 ? '#F5F0E8' : '#FFFFFF', color: page >= totalPages - 1 ? '#A8A29E' : '#1C1917', cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer' }}>Next</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
