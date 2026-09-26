import { useState, useEffect } from 'react';
import { getAllPayments, updatePaymentStatus } from '../../api/paymentApi';
import Badge from '../../components/common/Badge';
import Loader from '../../components/common/Loader';
import EmptyState from '../../components/common/EmptyState';
import { useToast } from '../../components/common/Toast';
import { formatPrice, formatDateShort, getErrorMessage } from '../../utils/helpers';

export default function AdminPaymentsPage() {
  const { addToast } = useToast();
  const [payments, setPayments] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(null);

  const fetchPayments = () => {
    setLoading(true);
    getAllPayments(page, 10)
      .then((res) => { setPayments(res.data.data.content || []); setTotalPages(res.data.data.totalPages || 0); })
      .catch(() => setPayments([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchPayments(); }, [page]);

  const handleStatusUpdate = async (paymentId, newStatus) => {
    setUpdating(paymentId);
    try {
      await updatePaymentStatus(paymentId, { paymentStatus: newStatus });
      addToast(`Payment #${paymentId} updated to ${newStatus}`);
      fetchPayments();
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    } finally {
      setUpdating(null);
    }
  };

  return (
    <div style={{ animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards' }}>
      <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em', marginBottom: '24px' }}>Payments</h1>

      {loading ? <Loader /> : payments.length === 0 ? (
        <EmptyState icon="💳" title="No payments yet" subtitle="Payments will appear here when customers make them" />
      ) : (
        <div style={{ background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', overflow: 'hidden' }}>
          <table>
            <thead>
              <tr style={{ background: '#FAF7F2', borderBottom: '2px solid #E8E2D9' }}>
                {['ID', 'Order', 'Customer', 'Amount', 'Method', 'Status', 'Date', 'Actions'].map((h) => (
                  <th key={h} style={{ padding: '12px 16px', textAlign: 'left', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {payments.map((p) => {
                const isPending = p.paymentStatus === 'PENDING';
                return (
                  <tr key={p.paymentId} style={{ borderBottom: '1px solid #F0EBE3', transition: 'background 0.15s' }}
                    onMouseEnter={(e) => e.currentTarget.style.background = '#FAF7F2'}
                    onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                  >
                    <td style={{ padding: '14px 16px', fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>#{p.paymentId}</td>
                    <td style={{ padding: '14px 16px', fontSize: '14px', color: '#1C1917' }}>#{p.orderId}</td>
                    <td style={{ padding: '14px 16px' }}>
                      <div style={{ fontSize: '14px', color: '#1C1917' }}>{p.customerName}</div>
                      <div style={{ fontSize: '11px', color: '#78716C' }}>{p.customerEmail}</div>
                    </td>
                    <td style={{ padding: '14px 16px', fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>{formatPrice(p.amount)}</td>
                    <td style={{ padding: '14px 16px', fontSize: '13px', color: '#78716C' }}>{p.paymentMethod}</td>
                    <td style={{ padding: '14px 16px' }}><Badge status={p.paymentStatus} /></td>
                    <td style={{ padding: '14px 16px', fontSize: '13px', color: '#78716C' }}>{formatDateShort(p.createdAt)}</td>
                    <td style={{ padding: '14px 16px' }}>
                      {isPending ? (
                        <select
                          disabled={updating === p.paymentId}
                          onChange={(e) => { if (e.target.value) handleStatusUpdate(p.paymentId, e.target.value); e.target.value = ''; }}
                          defaultValue=""
                          style={{ fontSize: '12px', padding: '5px 8px', borderRadius: '6px', border: '1px solid #D6D0C8', color: '#1C1917', cursor: 'pointer', background: '#FFFFFF' }}
                        >
                          <option value="" disabled>Update…</option>
                          <option value="SUCCESS">SUCCESS</option>
                          <option value="FAILED">FAILED</option>
                        </select>
                      ) : (
                        <span style={{ fontSize: '12px', color: '#A8A29E' }}>Final</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {totalPages > 1 && (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginTop: '24px' }}>
          <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0} style={{ padding: '8px 16px', fontSize: '13px', fontWeight: 500, borderRadius: '8px', border: '1px solid #D6D0C8', background: page === 0 ? '#F5F0E8' : '#FFFFFF', color: page === 0 ? '#A8A29E' : '#1C1917', cursor: page === 0 ? 'not-allowed' : 'pointer' }}>Previous</button>
          <span style={{ fontSize: '13px', color: '#78716C' }}>{page + 1} of {totalPages}</span>
          <button onClick={() => setPage(p => p + 1)} disabled={page >= totalPages - 1} style={{ padding: '8px 16px', fontSize: '13px', fontWeight: 500, borderRadius: '8px', border: '1px solid #D6D0C8', background: page >= totalPages - 1 ? '#F5F0E8' : '#FFFFFF', color: page >= totalPages - 1 ? '#A8A29E' : '#1C1917', cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer' }}>Next</button>
        </div>
      )}
    </div>
  );
}
