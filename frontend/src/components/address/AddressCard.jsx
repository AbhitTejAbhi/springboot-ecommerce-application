export default function AddressCard({ address, onEdit, onDelete, selectable, selected, onSelect }) {
  const isInteractive = !!onSelect;

  return (
    <div
      onClick={isInteractive ? () => onSelect(address.addressId) : undefined}
      style={{
        border: `1px solid ${selected ? '#2D6A4F' : '#E8E2D9'}`,
        borderRadius: '12px',
        background: selected ? '#FAF7F2' : '#FFFFFF',
        padding: '20px',
        transition: 'border-color 0.2s ease, background 0.2s ease',
        cursor: isInteractive ? 'pointer' : 'default',
        position: 'relative',
      }}
    >
      {/* Default badge */}
      {address.isDefault && (
        <span style={{
          position: 'absolute',
          top: '12px',
          right: '12px',
          fontSize: '10px',
          fontWeight: 500,
          textTransform: 'uppercase',
          letterSpacing: '0.06em',
          padding: '2px 8px',
          borderRadius: '4px',
          background: '#D8F3DC',
          color: '#1B4332',
        }}>
          Default
        </span>
      )}

      <div style={{ fontSize: '14px', fontWeight: 500, color: '#1C1917', marginBottom: '6px' }}>
        {address.houseNumber}, {address.street}
      </div>
      <div style={{ fontSize: '13px', color: '#78716C', lineHeight: 1.6 }}>
        {address.city}, {address.state}, {address.pincode}<br />
        {address.country}<br />
        Phone: {address.phoneNumber}
      </div>

      {/* Actions */}
      {(onEdit || onDelete) && (
        <div style={{ display: 'flex', gap: '12px', marginTop: '14px' }}>
          {onEdit && (
            <button
              onClick={(e) => { e.stopPropagation(); onEdit(address); }}
              style={{
                fontSize: '12px', fontWeight: 500, color: '#1C1917',
                padding: '5px 12px', borderRadius: '6px',
                border: '1px solid #D6D0C8', transition: 'all 0.15s', cursor: 'pointer',
                background: 'transparent',
              }}
              onMouseEnter={(e) => { e.currentTarget.style.background = '#FAF7F2'; }}
              onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
            >
              Edit
            </button>
          )}
          {onDelete && (
            <button
              onClick={(e) => { e.stopPropagation(); onDelete(address.addressId); }}
              style={{
                fontSize: '12px', fontWeight: 500, color: '#9B1D20',
                padding: '5px 12px', borderRadius: '6px',
                transition: 'all 0.15s', cursor: 'pointer',
                background: 'transparent',
              }}
              onMouseEnter={(e) => { e.currentTarget.style.background = '#F8DCDC'; }}
              onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
            >
              Delete
            </button>
          )}
        </div>
      )}

      {/* Radio indicator for selectable mode */}
      {isInteractive && (
        <div style={{
          position: 'absolute',
          top: '20px',
          left: '20px',
          width: '16px', height: '16px',
          borderRadius: '50%',
          border: `2px solid ${selected ? '#2D6A4F' : '#D6D0C8'}`,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          {selected && <div style={{ width: '8px', height: '8px', borderRadius: '50%', background: '#2D6A4F' }} />}
        </div>
      )}
    </div>
  );
}
