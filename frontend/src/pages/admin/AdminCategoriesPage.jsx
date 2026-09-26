import { useState, useEffect } from 'react';
import { getAllCategories, createCategory, updateCategory, deleteCategory } from '../../api/categoryApi';
import Modal from '../../components/common/Modal';
import Loader from '../../components/common/Loader';
import ErrorMessage from '../../components/common/ErrorMessage';
import EmptyState from '../../components/common/EmptyState';
import { useToast } from '../../components/common/Toast';
import { getErrorMessage, formatDateShort } from '../../utils/helpers';

export default function AdminCategoriesPage() {
  const { addToast } = useToast();
  const [categories, setCategories] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ name: '', description: '' });
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);

  const fetch = () => {
    setLoading(true);
    getAllCategories(page, 10)
      .then((res) => { setCategories(res.data.data.content || []); setTotalPages(res.data.data.totalPages || 0); })
      .catch(() => setCategories([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetch(); }, [page]);

  const openCreate = () => { setEditing(null); setForm({ name: '', description: '' }); setFormError(''); setModalOpen(true); };
  const openEdit = (c) => { setEditing(c); setForm({ name: c.name, description: c.description || '' }); setFormError(''); setModalOpen(true); };

  const handleSubmit = async (e) => {
    e.preventDefault(); setSaving(true); setFormError('');
    try {
      if (editing) { await updateCategory(editing.id, form); addToast('Category updated'); }
      else { await createCategory(form); addToast('Category created'); }
      setModalOpen(false); fetch();
    } catch (err) { setFormError(getErrorMessage(err)); } finally { setSaving(false); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this category?')) return;
    try { await deleteCategory(id); addToast('Category deleted'); fetch(); } catch (err) { addToast(getErrorMessage(err), 'error'); }
  };

  const inputStyle = { width: '100%', padding: '10px 14px', fontSize: '14px', color: '#1C1917', background: '#FFFFFF', border: '1px solid #D6D0C8', borderRadius: '8px', outline: 'none', transition: 'border-color 0.2s ease' };
  const labelStyle = { display: 'block', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C', marginBottom: '4px' };

  return (
    <div style={{ animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em' }}>Categories</h1>
        <button onClick={openCreate} style={{ padding: '10px 20px', fontSize: '13px', fontWeight: 500, background: '#1C1917', color: '#F5F0E8', borderRadius: '8px', cursor: 'pointer' }}>Add category</button>
      </div>

      {loading ? <Loader /> : categories.length === 0 ? (
        <EmptyState icon="📁" title="No categories yet" subtitle="Create your first category" />
      ) : (
        <div style={{ background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', overflow: 'hidden' }}>
          <table>
            <thead>
              <tr style={{ background: '#FAF7F2', borderBottom: '2px solid #E8E2D9' }}>
                {['ID', 'Name', 'Description', 'Created', 'Actions'].map((h) => (
                  <th key={h} style={{ padding: '12px 16px', textAlign: 'left', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {categories.map((c) => (
                <tr key={c.id} style={{ borderBottom: '1px solid #F0EBE3', transition: 'background 0.15s' }}
                  onMouseEnter={(e) => e.currentTarget.style.background = '#FAF7F2'}
                  onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                >
                  <td style={{ padding: '14px 16px', fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>{c.id}</td>
                  <td style={{ padding: '14px 16px', fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>{c.name}</td>
                  <td style={{ padding: '14px 16px', fontSize: '13px', color: '#78716C', maxWidth: '300px' }}>{c.description || '—'}</td>
                  <td style={{ padding: '14px 16px', fontSize: '13px', color: '#78716C' }}>{formatDateShort(c.createdAt)}</td>
                  <td style={{ padding: '14px 16px' }}>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button onClick={() => openEdit(c)} style={{ fontSize: '12px', fontWeight: 500, color: '#2D6A4F', cursor: 'pointer', padding: '4px 8px' }}>Edit</button>
                      <button onClick={() => handleDelete(c.id)} style={{ fontSize: '12px', fontWeight: 500, color: '#9B1D20', cursor: 'pointer', padding: '4px 8px' }}>Delete</button>
                    </div>
                  </td>
                </tr>
              ))}
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

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit category' : 'New category'}>
        <form onSubmit={handleSubmit}>
          {formError && <div style={{ marginBottom: '12px' }}><ErrorMessage message={formError} /></div>}
          <div style={{ marginBottom: '12px' }}><label style={labelStyle}>Name</label><input style={inputStyle} value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          <div style={{ marginBottom: '20px' }}><label style={labelStyle}>Description</label><textarea style={{ ...inputStyle, minHeight: '80px', resize: 'vertical' }} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          <button type="submit" disabled={saving} style={{ width: '100%', padding: '12px', fontSize: '14px', fontWeight: 500, background: '#1C1917', color: '#F5F0E8', borderRadius: '8px', cursor: saving ? 'not-allowed' : 'pointer', opacity: saving ? 0.7 : 1 }}>
            {saving ? 'Saving…' : editing ? 'Update category' : 'Create category'}
          </button>
        </form>
      </Modal>
    </div>
  );
}
