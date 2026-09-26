import { useState, useEffect } from 'react';
import { getAllProducts, createProduct, updateProduct, deleteProduct, uploadProductImage } from '../../api/productApi';
import { getAllCategories } from '../../api/categoryApi';
import Modal from '../../components/common/Modal';
import Badge from '../../components/common/Badge';
import Loader from '../../components/common/Loader';
import ErrorMessage from '../../components/common/ErrorMessage';
import EmptyState from '../../components/common/EmptyState';
import { useToast } from '../../components/common/Toast';
import { formatPrice, getErrorMessage } from '../../utils/helpers';

const emptyForm = { name: '', description: '', price: '', stock: '', imageUrl: '', categoryId: '', active: true };

export default function AdminProductsPage() {
  const { addToast } = useToast();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);
  const [imageFile, setImageFile] = useState(null);

  const fetchProducts = () => {
    setLoading(true);
    getAllProducts(page, 10)
      .then((res) => { setProducts(res.data.data.content || []); setTotalPages(res.data.data.totalPages || 0); })
      .catch(() => setProducts([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchProducts(); }, [page]);
  useEffect(() => { getAllCategories(0, 100).then((res) => setCategories(res.data.data.content || [])).catch(() => {}); }, []);

  const openCreate = () => { setEditingProduct(null); setForm(emptyForm); setFormError(''); setImageFile(null); setModalOpen(true); };
  const openEdit = (p) => {
    setEditingProduct(p);
    setForm({ name: p.name, description: p.description || '', price: p.price, stock: p.stock, imageUrl: p.imageUrl || '', categoryId: p.categoryId, active: p.active });
    setFormError(''); setImageFile(null); setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true); setFormError('');
    try {
      const payload = { ...form, price: parseFloat(form.price), stock: parseInt(form.stock), categoryId: parseInt(form.categoryId) };
      let productId;
      if (editingProduct) {
        const res = await updateProduct(editingProduct.id, payload);
        productId = res.data.data.id;
        addToast('Product updated');
      } else {
        const res = await createProduct(payload);
        productId = res.data.data.id;
        addToast('Product created');
      }
      if (imageFile && productId) {
        try { await uploadProductImage(productId, imageFile); } catch { addToast('Product saved but image upload failed', 'error'); }
      }
      setModalOpen(false); fetchProducts();
    } catch (err) { setFormError(getErrorMessage(err)); } finally { setSaving(false); }
  };

  const handleToggleActive = async (p) => {
    const action = p.active ? 'deactivate' : 'activate';
    if (!window.confirm(`Are you sure you want to ${action} "${p.name}"?`)) return;
    try {
      await deleteProduct(p.id);
      addToast(`Product ${p.active ? 'deactivated' : 'activated'}`);
      fetchProducts();
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    }
  };

  const inputStyle = { width: '100%', padding: '10px 14px', fontSize: '14px', color: '#1C1917', background: '#FFFFFF', border: '1px solid #D6D0C8', borderRadius: '8px', outline: 'none', transition: 'border-color 0.2s ease' };
  const labelStyle = { display: 'block', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C', marginBottom: '4px' };

  return (
    <div style={{ animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em' }}>Products</h1>
        <button onClick={openCreate} style={{ padding: '10px 20px', fontSize: '13px', fontWeight: 500, background: '#1C1917', color: '#F5F0E8', borderRadius: '8px', cursor: 'pointer', transition: 'all 0.15s ease' }}>Add product</button>
      </div>

      {loading ? <Loader /> : products.length === 0 ? (
        <EmptyState icon="📦" title="No products yet" subtitle="Create your first product to get started" />
      ) : (
        <div style={{ background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', overflow: 'hidden' }}>
          <table>
            <thead>
              <tr style={{ background: '#FAF7F2', borderBottom: '2px solid #E8E2D9' }}>
                {['Product', 'Category', 'Price', 'Stock', 'Status', 'Actions'].map((h) => (
                  <th key={h} style={{ padding: '12px 16px', textAlign: 'left', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.06em', color: '#78716C' }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {products.map((p) => (
                <tr key={p.id} style={{ borderBottom: '1px solid #F0EBE3', transition: 'background 0.15s' }}
                  onMouseEnter={(e) => e.currentTarget.style.background = '#FAF7F2'}
                  onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                >
                  <td style={{ padding: '14px 16px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <div style={{ width: '36px', height: '36px', borderRadius: '6px', background: '#FAF7F2', flexShrink: 0, overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        {p.imageUrl ? <img src={p.imageUrl} alt="" style={{ width: '100%', height: '100%', objectFit: 'contain' }} /> : <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#C4B9A8" strokeWidth="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/></svg>}
                      </div>
                      <span style={{ fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>{p.name}</span>
                    </div>
                  </td>
                  <td style={{ padding: '14px 16px', fontSize: '13px', color: '#78716C' }}>{p.categoryName}</td>
                  <td style={{ padding: '14px 16px', fontSize: '14px', fontWeight: 500, color: '#1C1917' }}>{formatPrice(p.price)}</td>
                  <td style={{ padding: '14px 16px', fontSize: '14px', color: p.stock > 0 ? '#1C1917' : '#9B1D20' }}>{p.stock}</td>
                  <td style={{ padding: '14px 16px' }}>
                    <span style={{
                      fontSize: '11px',
                      fontWeight: 600,
                      padding: '4px 10px',
                      borderRadius: '100px',
                      background: p.active ? '#ECFDF5' : '#FEF2F2',
                      color: p.active ? '#059669' : '#DC2626',
                    }}>
                      {p.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td style={{ padding: '14px 16px' }}>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button onClick={() => openEdit(p)} style={{ fontSize: '12px', fontWeight: 500, color: '#2D6A4F', cursor: 'pointer', padding: '4px 8px', borderRadius: '4px' }}>Edit</button>
                      <button onClick={() => handleToggleActive(p)} style={{ fontSize: '12px', fontWeight: 500, color: p.active ? '#9B1D20' : '#059669', cursor: 'pointer', padding: '4px 8px', borderRadius: '4px' }}>
                        {p.active ? 'Deactivate' : 'Activate'}
                      </button>
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

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editingProduct ? 'Edit product' : 'New product'} maxWidth="560px">
        <form onSubmit={handleSubmit}>
          {formError && <div style={{ marginBottom: '12px' }}><ErrorMessage message={formError} /></div>}
          <div style={{ marginBottom: '12px' }}><label style={labelStyle}>Name</label><input style={inputStyle} value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          <div style={{ marginBottom: '12px' }}><label style={labelStyle}>Description</label><textarea style={{ ...inputStyle, minHeight: '80px', resize: 'vertical' }} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' }}>
            <div><label style={labelStyle}>Price</label><input type="number" step="0.01" min="0.01" style={inputStyle} value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
            <div><label style={labelStyle}>Stock</label><input type="number" min="0" style={inputStyle} value={form.stock} onChange={(e) => setForm({ ...form, stock: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          </div>
          <div style={{ marginBottom: '12px' }}><label style={labelStyle}>Category</label>
            <select style={inputStyle} value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'}>
              <option value="">Select category</option>
              {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div style={{ marginBottom: '12px' }}><label style={labelStyle}>Image</label><input type="file" accept="image/*" onChange={(e) => setImageFile(e.target.files[0])} style={{ fontSize: '13px', color: '#78716C' }} /></div>
          <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px', color: '#1C1917', marginBottom: '20px', cursor: 'pointer' }}>
            <input type="checkbox" checked={form.active} onChange={(e) => setForm({ ...form, active: e.target.checked })} /> Active (visible to customers)
          </label>
          <button type="submit" disabled={saving} style={{ width: '100%', padding: '12px', fontSize: '14px', fontWeight: 500, background: '#1C1917', color: '#F5F0E8', borderRadius: '8px', cursor: saving ? 'not-allowed' : 'pointer', opacity: saving ? 0.7 : 1 }}>
            {saving ? 'Saving…' : editingProduct ? 'Update product' : 'Create product'}
          </button>
        </form>
      </Modal>
    </div>
  );
}
