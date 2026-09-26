import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllProducts } from '../../api/productApi';
import { getAllCategories } from '../../api/categoryApi';
import { getAllOrders } from '../../api/orderApi';
import { getAllPayments } from '../../api/paymentApi';
import Badge from '../../components/common/Badge';
import Loader from '../../components/common/Loader';
import { formatPrice, formatDateShort } from '../../utils/helpers';

export default function AdminDashboard() {
  const [stats, setStats] = useState({ products: 0, categories: 0, orders: 0, payments: 0 });
  const [recentOrders, setRecentOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      getAllProducts(0, 1).catch(() => ({ data: { data: { totalElements: 0 } } })),
      getAllCategories(0, 1).catch(() => ({ data: { data: { totalElements: 0 } } })),
      getAllOrders(0, 5).catch(() => ({ data: { data: { content: [], totalElements: 0 } } })),
      getAllPayments(0, 1).catch(() => ({ data: { data: { totalElements: 0 } } })),
    ]).then(([prodRes, catRes, orderRes, payRes]) => {
      setStats({
        products: prodRes.data.data.totalElements || 0,
        categories: catRes.data.data.totalElements || 0,
        orders: orderRes.data.data.totalElements || 0,
        payments: payRes.data.data.totalElements || 0,
      });
      setRecentOrders(orderRes.data.data.content || []);
    }).finally(() => setLoading(false));
  }, []);

  const StatCard = ({ label, value, to }) => (
    <Link to={to} style={{
      background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px',
      padding: '24px', transition: 'border-color 0.2s ease, box-shadow 0.2s ease',
    }}
      onMouseEnter={(e) => { e.currentTarget.style.borderColor = '#C4B9A8'; e.currentTarget.style.boxShadow = '0 4px 12px rgba(28,25,23,0.06)'; }}
      onMouseLeave={(e) => { e.currentTarget.style.borderColor = '#E8E2D9'; e.currentTarget.style.boxShadow = 'none'; }}
    >
      <div style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C', marginBottom: '8px' }}>{label}</div>
      <div style={{ fontSize: '34px', fontWeight: 700, color: '#1C1917', letterSpacing: '-0.02em' }}>{value}</div>
    </Link>
  );

  if (loading) return <Loader />;

  return (
    <div style={{ animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards' }}>
      <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em', marginBottom: '32px' }}>Dashboard</h1>

      {/* Stats */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px', marginBottom: '48px' }}>
        <StatCard label="Products" value={stats.products} to="/admin/products" />
        <StatCard label="Categories" value={stats.categories} to="/admin/categories" />
        <StatCard label="Orders" value={stats.orders} to="/admin/orders" />
        <StatCard label="Payments" value={stats.payments} to="/admin/payments" />
      </div>

      {/* Recent Orders */}
      <h2 style={{ fontSize: '17px', fontWeight: 600, color: '#1C1917', marginBottom: '16px' }}>Recent orders</h2>
      {recentOrders.length === 0 ? (
        <p style={{ fontSize: '14px', color: '#78716C' }}>No orders yet</p>
      ) : (
        <div style={{ background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', overflow: 'hidden' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ background: '#FAF7F2', borderBottom: '2px solid #E8E2D9' }}>
                {['ID', 'Customer', 'Total', 'Status', 'Date'].map((h) => (
                  <th key={h} style={{ padding: '12px 16px', textAlign: 'left', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {recentOrders.map((order) => (
                <tr key={order.orderId} style={{ borderBottom: '1px solid #F0EBE3', transition: 'background 0.15s' }}
                  onMouseEnter={(e) => { e.currentTarget.style.background = '#FAF7F2'; }}
                  onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
                >
                  <td style={{ padding: '14px 16px', fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>#{order.orderId}</td>
                  <td style={{ padding: '14px 16px', fontSize: '14px', color: '#1C1917' }}>{order.customerName}</td>
                  <td style={{ padding: '14px 16px', fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>{formatPrice(order.totalAmount)}</td>
                  <td style={{ padding: '14px 16px' }}><Badge status={order.orderStatus} /></td>
                  <td style={{ padding: '14px 16px', fontSize: '13px', color: '#78716C' }}>{formatDateShort(order.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
