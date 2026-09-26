import { useState, useEffect } from 'react';
import { getMyAddresses, createAddress, updateAddress, deleteAddress } from '../../api/addressApi';
import AddressCard from '../../components/address/AddressCard';
import Modal from '../../components/common/Modal';
import EmptyState from '../../components/common/EmptyState';
import Loader from '../../components/common/Loader';
import ErrorMessage from '../../components/common/ErrorMessage';
import { useToast } from '../../components/common/Toast';
import { getErrorMessage } from '../../utils/helpers';

const emptyForm = { houseNumber: '', street: '', city: '', state: '', country: '', pincode: '', phoneNumber: '', isDefault: false };

export default function AddressPage() {
  const { addToast } = useToast();
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);

  const fetchAddresses = () => {
    setLoading(true);
    getMyAddresses()
      .then((res) => setAddresses(res.data.data || []))
      .catch(() => setAddresses([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchAddresses(); }, []);

  const openCreate = () => { setEditingAddress(null); setForm(emptyForm); setFormError(''); setModalOpen(true); };
  const openEdit = (addr) => {
    setEditingAddress(addr);
    setForm({
      houseNumber: addr.houseNumber, street: addr.street, city: addr.city,
      state: addr.state, country: addr.country, pincode: addr.pincode,
      phoneNumber: addr.phoneNumber, isDefault: addr.isDefault,
    });
    setFormError('');
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError('');
    try {
      if (editingAddress) {
        await updateAddress(editingAddress.addressId, form);
        addToast('Address updated');
      } else {
        await createAddress(form);
        addToast('Address added');
      }
      setModalOpen(false);
      fetchAddresses();
    } catch (err) {
      setFormError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this address?')) return;
    try {
      await deleteAddress(id);
      addToast('Address deleted');
      fetchAddresses();
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    }
  };

  const inputStyle = {
    width: '100%', padding: '10px 14px', fontSize: '14px', color: '#1C1917',
    background: '#FFFFFF', border: '1px solid #D6D0C8', borderRadius: '8px',
    transition: 'border-color 0.2s ease', outline: 'none',
  };
  const labelStyle = { display: 'block', fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C', marginBottom: '4px' };

  return (
    <div style={{
      maxWidth: '800px', margin: '0 auto', padding: '32px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em' }}>Addresses</h1>
        <button onClick={openCreate} style={{
          padding: '10px 20px', fontSize: '13px', fontWeight: 500,
          background: '#1C1917', color: '#F5F0E8', borderRadius: '8px',
          cursor: 'pointer', transition: 'all 0.15s ease',
        }}
          onMouseDown={(e) => { e.currentTarget.style.transform = 'scale(0.97)'; }}
          onMouseUp={(e) => { e.currentTarget.style.transform = 'scale(1)'; }}
        >Add address</button>
      </div>

      {loading ? <Loader /> : addresses.length === 0 ? (
        <EmptyState icon="📍" title="No addresses saved" subtitle="Add your first delivery address" />
      ) : (
        <div style={{ display: 'grid', gap: '12px' }}>
          {addresses.map((addr) => (
            <AddressCard key={addr.addressId} address={addr} onEdit={openEdit} onDelete={handleDelete} />
          ))}
        </div>
      )}

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title={editingAddress ? 'Edit address' : 'New address'}>
        <form onSubmit={handleSubmit}>
          {formError && <div style={{ marginBottom: '12px' }}><ErrorMessage message={formError} /></div>}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' }}>
            <div><label style={labelStyle}>House no.</label><input style={inputStyle} value={form.houseNumber} onChange={(e) => setForm({ ...form, houseNumber: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
            <div><label style={labelStyle}>Street</label><input style={inputStyle} value={form.street} onChange={(e) => setForm({ ...form, street: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' }}>
            <div><label style={labelStyle}>City</label><input style={inputStyle} value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
            <div><label style={labelStyle}>State</label><input style={inputStyle} value={form.state} onChange={(e) => setForm({ ...form, state: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' }}>
            <div><label style={labelStyle}>Country</label><input style={inputStyle} value={form.country} onChange={(e) => setForm({ ...form, country: e.target.value })} required onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
            <div><label style={labelStyle}>Pincode</label><input style={inputStyle} value={form.pincode} onChange={(e) => setForm({ ...form, pincode: e.target.value })} required pattern="[0-9]{4,10}" onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          </div>
          <div style={{ marginBottom: '12px' }}><label style={labelStyle}>Phone</label><input style={inputStyle} value={form.phoneNumber} onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })} required pattern="[0-9]{10}" onFocus={(e) => e.target.style.borderColor = '#2D6A4F'} onBlur={(e) => e.target.style.borderColor = '#D6D0C8'} /></div>
          <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px', color: '#1C1917', marginBottom: '20px', cursor: 'pointer' }}>
            <input type="checkbox" checked={form.isDefault} onChange={(e) => setForm({ ...form, isDefault: e.target.checked })} />
            Set as default address
          </label>
          <button type="submit" disabled={saving} style={{
            width: '100%', padding: '12px', fontSize: '14px', fontWeight: 500,
            background: '#1C1917', color: '#F5F0E8', borderRadius: '8px',
            cursor: saving ? 'not-allowed' : 'pointer', opacity: saving ? 0.7 : 1,
          }}>
            {saving ? 'Saving…' : editingAddress ? 'Update address' : 'Save address'}
          </button>
        </form>
      </Modal>
    </div>
  );
}
