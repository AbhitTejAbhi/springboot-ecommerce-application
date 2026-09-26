import { useAuth } from '../../hooks/useAuth';

export default function ProfilePage() {
  const { user } = useAuth();

  return (
    <div style={{
      maxWidth: '560px', margin: '0 auto', padding: '32px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      <h1 style={{ fontSize: '27px', fontWeight: 600, color: '#1C1917', letterSpacing: '-0.02em', marginBottom: '32px' }}>
        Profile
      </h1>

      <div style={{
        background: '#FFFFFF', border: '1px solid #E8E2D9', borderRadius: '12px', padding: '24px',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '24px', paddingBottom: '24px', borderBottom: '1px solid #F0EBE3' }}>
          <div style={{
            width: '56px', height: '56px', borderRadius: '50%', background: '#FAF7F2',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: '20px', fontWeight: 600, color: '#1C1917', border: '1px solid #E8E2D9',
          }}>
            {user?.name?.charAt(0)?.toUpperCase()}
          </div>
          <div>
            <div style={{ fontSize: '17px', fontWeight: 600, color: '#1C1917' }}>{user?.name}</div>
            <div style={{ fontSize: '13px', color: '#78716C' }}>{user?.role}</div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <div style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C', marginBottom: '4px' }}>Email</div>
            <div style={{ fontSize: '14px', color: '#1C1917' }}>{user?.email}</div>
          </div>
          <div>
            <div style={{ fontSize: '11px', fontWeight: 500, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#78716C', marginBottom: '4px' }}>Account ID</div>
            <div style={{ fontSize: '14px', color: '#1C1917' }}>{user?.userId}</div>
          </div>
        </div>
      </div>
    </div>
  );
}
