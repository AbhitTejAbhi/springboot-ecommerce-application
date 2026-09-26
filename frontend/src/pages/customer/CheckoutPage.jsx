import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../../hooks/useCart';
import { useToast } from '../../components/common/Toast';
import { getMyAddresses } from '../../api/addressApi';
import { placeOrder } from '../../api/orderApi';
import AddressCard from '../../components/address/AddressCard';
import EmptyState from '../../components/common/EmptyState';
import Loader from '../../components/common/Loader';
import { formatPrice, getErrorMessage } from '../../utils/helpers';

export default function CheckoutPage() {
  const { cart, refreshCart } = useCart();
  const { addToast } = useToast();
  const navigate = useNavigate();
  const [addresses, setAddresses] = useState([]);
  const [selectedAddress, setSelectedAddress] = useState(null);
  const [loadingAddresses, setLoadingAddresses] = useState(true);
  const [placing, setPlacing] = useState(false);
  const [idempotencyKey] = useState(() => crypto.randomUUID());

  useEffect(() => {
    refreshCart();
    getMyAddresses()
      .then((res) => {
        const addrs = res.data.data || [];
        setAddresses(addrs);
        const defaultAddr = addrs.find((a) => a.isDefault);
        if (defaultAddr) setSelectedAddress(defaultAddr.addressId);
        else if (addrs.length > 0) setSelectedAddress(addrs[0].addressId);
      })
      .catch(() => {})
      .finally(() => setLoadingAddresses(false));
  }, [refreshCart]);

  const handlePlaceOrder = async () => {
    if (!selectedAddress) {
      addToast('Please select a delivery address', 'error');
      return;
    }
    setPlacing(true);
    try {
      const res = await placeOrder(
        { addressId: selectedAddress },
        { 'Idempotency-Key': idempotencyKey }
      );
      const order = res.data.data;
      await refreshCart();
      addToast('Order placed successfully!');
      navigate(`/orders/${order.orderId}`);
    } catch (err) {
      addToast(getErrorMessage(err), 'error');
    } finally {
      setPlacing(false);
    }
  };

  const items = cart?.items || [];
  if (!cart || items.length === 0) {
    return (
      <div style={{ maxWidth: '800px', margin: '0 auto', padding: '32px 24px' }}>
        <EmptyState
          icon="🛒"
          title="Your cart is empty"
          subtitle="Add items to your cart before checking out"
          action={<button onClick={() => navigate('/')} style={{ padding: '10px 24px', fontSize: '13px', fontWeight: 500, background: '#1C1917', color: '#F5F0E8', borderRadius: '8px', cursor: 'pointer' }}>Browse products</button>}
        />
      </div>
    );
  }

  return (
    <div style={{
      maxWidth: '800px', margin: '0 auto', padding: '32px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em', marginBottom: '32px' }}>
        Checkout
      </h1>

      {/* Order Summary */}
      <div style={{
        background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px',
        padding: '24px', marginBottom: '32px',
      }}>
        <h3 style={{ fontSize: '14px', fontWeight: 500, color: '#78716C', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '16px' }}>
          Order summary
        </h3>
        {items.map((item) => (
          <div key={item.cartItemId} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', fontSize: '14px', borderBottom: '1px solid #F0EBE3' }}>
            <span style={{ color: '#1C1917' }}>{item.productName} × {item.quantity}</span>
            <span style={{ fontWeight: 500, color: '#1C1917' }}>{formatPrice(item.itemTotal)}</span>
          </div>
        ))}
        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '14px 0 0', fontSize: '16px', fontWeight: 600, color: '#1C1917' }}>
          <span>Total</span>
          <span>{formatPrice(cart.grandTotal)}</span>
        </div>
      </div>

      {/* Address Selection */}
      <div style={{ marginBottom: '32px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
          <h3 style={{ fontSize: '14px', fontWeight: 500, color: '#78716C', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
            Delivery address
          </h3>
          <button onClick={() => navigate('/addresses')} style={{ fontSize: '12px', fontWeight: 500, color: '#2D6A4F', cursor: 'pointer' }}>
            Manage addresses
          </button>
        </div>

        {loadingAddresses ? <Loader /> : addresses.length === 0 ? (
          <EmptyState
            icon="📍"
            title="No addresses saved"
            subtitle="Add a delivery address to continue"
            action={<button onClick={() => navigate('/addresses')} style={{ padding: '10px 20px', fontSize: '13px', fontWeight: 500, background: '#1C1917', color: '#F5F0E8', borderRadius: '8px', cursor: 'pointer' }}>Add address</button>}
          />
        ) : (
          <div style={{ display: 'grid', gap: '12px' }}>
            {addresses.map((addr) => (
              <AddressCard
                key={addr.addressId}
                address={addr}
                selectable
                selected={selectedAddress === addr.addressId}
                onSelect={setSelectedAddress}
              />
            ))}
          </div>
        )}
      </div>

      {/* Place Order */}
      <button
        onClick={handlePlaceOrder}
        disabled={placing || !selectedAddress}
        style={{
          width: '100%', padding: '14px 20px', fontSize: '15px', fontWeight: 500,
          background: !selectedAddress ? '#E8E2D9' : '#1C1917',
          color: !selectedAddress ? '#78716C' : '#F5F0E8',
          borderRadius: '8px', border: 'none',
          cursor: placing || !selectedAddress ? 'not-allowed' : 'pointer',
          transition: 'all 0.15s ease',
        }}
        onMouseDown={(e) => { if (selectedAddress && !placing) e.currentTarget.style.transform = 'scale(0.97)'; }}
        onMouseUp={(e) => { e.currentTarget.style.transform = 'scale(1)'; }}
      >
        {placing ? 'Placing order…' : 'Place order'}
      </button>
    </div>
  );
}
