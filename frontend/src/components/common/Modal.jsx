export default function Modal({ isOpen, onClose, title, children, maxWidth = '480px' }) {
  if (!isOpen) return null;

  return (
    <div
      onClick={onClose}
      style={{
        position: 'fixed',
        inset: 0,
        zIndex: 2000,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'rgba(28, 25, 23, 0.4)',
        animation: 'fadeIn 0.2s ease',
        padding: '24px',
      }}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        style={{
          background: '#FFFFFF',
          borderRadius: '12px',
          width: '100%',
          maxWidth,
          maxHeight: '90vh',
          overflowY: 'auto',
          animation: 'scaleIn 0.25s cubic-bezier(0.16, 1, 0.3, 1)',
          boxShadow: '0 8px 24px rgba(28,25,23,0.12)',
        }}
      >
        {/* Header */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '20px 24px',
          borderBottom: '1px solid #E8E2D9',
        }}>
          <h3 style={{
            fontSize: '17px',
            fontWeight: 600,
            color: '#1C1917',
            letterSpacing: '-0.02em',
          }}>
            {title}
          </h3>
          <button
            onClick={onClose}
            style={{
              width: '28px',
              height: '28px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              borderRadius: '6px',
              transition: 'background 0.15s',
              color: '#78716C',
            }}
            onMouseEnter={(e) => e.currentTarget.style.background = '#FAF7F2'}
            onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
        </div>

        {/* Body */}
        <div style={{ padding: '24px' }}>
          {children}
        </div>
      </div>
    </div>
  );
}
