export default function ProductGrid({ children }) {
  return (
    <div style={{
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
      gap: '24px',
    }}>
      {children}
    </div>
  );
}
