export default function Skeleton({ width = '100%', height = '16px', borderRadius = '4px', style = {} }) {
  return (
    <div style={{
      width,
      height,
      borderRadius,
      background: 'linear-gradient(90deg, #EDE8E0 25%, #F5F0E8 50%, #EDE8E0 75%)',
      backgroundSize: '800px 100%',
      animation: 'shimmer 1.4s infinite linear',
      ...style,
    }} />
  );
}

export function ProductCardSkeleton() {
  return (
    <div style={{
      border: '1px solid #E8E2D9',
      borderRadius: '12px',
      background: '#FFFFFF',
      overflow: 'hidden',
    }}>
      <Skeleton height="260px" borderRadius="0" />
      <div style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
        <Skeleton width="50%" height="11px" />
        <Skeleton width="80%" height="14px" />
        <Skeleton width="30%" height="16px" />
        <Skeleton height="38px" borderRadius="8px" style={{ marginTop: '8px' }} />
      </div>
    </div>
  );
}

export function TableRowSkeleton({ cols = 5 }) {
  return (
    <tr>
      {Array.from({ length: cols }).map((_, i) => (
        <td key={i} style={{ padding: '14px 16px' }}>
          <Skeleton width={i === 0 ? '40px' : '80%'} height="14px" />
        </td>
      ))}
    </tr>
  );
}
