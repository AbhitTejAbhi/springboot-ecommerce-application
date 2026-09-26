export default function Footer() {
  return (
    <footer style={{
      borderTop: '1px solid #E8E2D9',
      padding: '24px',
      textAlign: 'center',
      fontSize: '12px',
      color: '#78716C',
      marginTop: '64px',
    }}>
      © {new Date().getFullYear()} Store. All rights reserved.
    </footer>
  );
}
