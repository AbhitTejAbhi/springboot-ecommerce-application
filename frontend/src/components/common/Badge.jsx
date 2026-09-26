import { getStatusColor } from '../../utils/helpers';

export default function Badge({ status }) {
  if (!status) return null;
  const { bg, color } = getStatusColor(status);

  return (
    <span style={{
      display: 'inline-block',
      fontSize: '11px',
      fontWeight: 500,
      textTransform: 'uppercase',
      letterSpacing: '0.06em',
      padding: '3px 10px',
      borderRadius: '4px',
      backgroundColor: bg,
      color: color,
      transition: 'background-color 0.3s ease, color 0.3s ease',
      lineHeight: 1.5,
    }}>
      {status}
    </span>
  );
}
