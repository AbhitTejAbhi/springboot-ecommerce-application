export default function EmptyState({ icon, title, subtitle, action }) {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '64px 24px',
      textAlign: 'center',
    }}>
      {icon && (
        <div style={{ fontSize: '40px', color: '#D6D0C8', marginBottom: '16px', lineHeight: 1 }}>
          {icon}
        </div>
      )}
      <h3 style={{
        fontSize: '16px',
        fontWeight: 500,
        color: '#1C1917',
        marginBottom: '6px',
      }}>
        {title}
      </h3>
      {subtitle && (
        <p style={{
          fontSize: '14px',
          color: '#78716C',
          maxWidth: '320px',
          lineHeight: 1.5,
        }}>
          {subtitle}
        </p>
      )}
      {action && (
        <div style={{ marginTop: '20px' }}>
          {action}
        </div>
      )}
    </div>
  );
}
