export default function Loader({ size = 24 }) {
  return (
    <div style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '48px 0',
    }}>
      <div style={{
        width: `${size}px`,
        height: `${size}px`,
        border: '2px solid #E8E2D9',
        borderTopColor: '#1C1917',
        borderRadius: '50%',
        animation: 'spin 0.6s linear infinite',
      }} />
    </div>
  );
}
