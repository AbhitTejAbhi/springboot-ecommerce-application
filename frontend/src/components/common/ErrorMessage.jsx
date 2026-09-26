export default function ErrorMessage({ message }) {
  if (!message) return null;

  return (
    <div style={{
      background: '#F8DCDC',
      color: '#9B1D20',
      padding: '10px 16px',
      borderRadius: '8px',
      fontSize: '13px',
      fontWeight: 500,
      border: '1px solid #F0C8C8',
      lineHeight: 1.5,
    }}>
      {message}
    </div>
  );
}
