import { useNavigate } from 'react-router-dom';
import heroIllustration from '../../assets/hero_illustration.png';

export default function LandingPage() {
  const navigate = useNavigate();

  const handleGetStarted = () => {
    navigate('/shop');
  };

  return (
    <div style={{
      background: 'linear-gradient(135deg, #F0EBFC 0%, #E3E7FD 50%, #F6EBFE 100%)',
      minHeight: '100vh',
      position: 'relative',
      overflow: 'hidden',
      padding: '40px 40px 80px 40px',
    }}>
      {/* Wavy Background Blobs */}
      <div style={{
        position: 'absolute',
        top: '-10%',
        right: '-5%',
        width: '600px',
        height: '600px',
        borderRadius: '50%',
        background: 'radial-gradient(circle, rgba(124,58,237,0.08) 0%, rgba(255,255,255,0) 70%)',
        pointerEvents: 'none',
      }} />
      <div style={{
        position: 'absolute',
        bottom: '-10%',
        left: '10%',
        width: '500px',
        height: '500px',
        borderRadius: '50%',
        background: 'radial-gradient(circle, rgba(236,72,153,0.05) 0%, rgba(255,255,255,0) 70%)',
        pointerEvents: 'none',
      }} />

      {/* Main Container */}
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
        position: 'relative',
        zIndex: 2,
      }}>
        
        {/* Header row (Logo + Navigation) */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '80px',
        }}>
          {/* Logo */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div style={{
              width: '32px',
              height: '32px',
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #F59E0B 0%, #D97706 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#FFFFFF',
              fontWeight: 800,
              fontSize: '18px',
              boxShadow: '0 4px 8px rgba(217, 119, 6, 0.25)',
            }}>
              e
            </div>
            <span style={{
              fontSize: '20px',
              fontWeight: 800,
              background: 'linear-gradient(135deg, #7C3AED 0%, #EC4899 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}>
              Your Logo
            </span>
          </div>

          {/* Pill Navigation Bar */}
          <div style={{
            display: 'flex',
            alignItems: 'center',
            background: '#2A2659',
            borderRadius: '100px',
            padding: '6px 24px',
            boxShadow: '0 8px 24px rgba(42, 38, 89, 0.15)',
          }}>
            {['Home', 'About', 'Project', 'Service', 'Contact'].map((item, idx) => (
              <button
                key={item}
                onClick={() => {
                  if (item === 'Home') {
                    handleGetStarted();
                  }
                }}
                style={{
                  color: idx === 0 ? '#FFFFFF' : '#B5B1E2',
                  fontSize: '14px',
                  fontWeight: 500,
                  padding: '8px 16px',
                  cursor: 'pointer',
                  transition: 'color 0.2s',
                  background: 'none',
                  border: 'none',
                }}
                onMouseEnter={(e) => { e.currentTarget.style.color = '#FFFFFF'; }}
                onMouseLeave={(e) => { if (idx !== 0) e.currentTarget.style.color = '#B5B1E2'; }}
              >
                {item}
              </button>
            ))}
          </div>
        </div>

        {/* Hero Banner Layout */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1.1fr',
          gap: '50px',
          alignItems: 'center',
          marginTop: '40px',
        }}>
          {/* Left Column info */}
          <div style={{
            animation: 'fadeUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards',
          }}>
            <h1 style={{
              fontSize: '52px',
              fontWeight: 800,
              color: '#7C3AED',
              lineHeight: 1.1,
              letterSpacing: '-0.02em',
              marginBottom: '8px',
            }}>
              E-COMMERCE
            </h1>
            <h2 style={{
              fontSize: '36px',
              fontWeight: 700,
              color: '#A855F7',
              lineHeight: 1.2,
              letterSpacing: '-0.01em',
              marginBottom: '24px',
            }}>
              LANDING PAGE
            </h2>
            <p style={{
              fontSize: '16px',
              color: '#4B5563',
              lineHeight: 1.8,
              marginBottom: '40px',
              maxWidth: '480px',
            }}>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
            </p>

            {/* Get Started Button */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <button
                onClick={handleGetStarted}
                style={{
                  alignSelf: 'flex-start',
                  background: 'linear-gradient(135deg, #4F46E5 0%, #3B82F6 100%)',
                  color: '#FFFFFF',
                  padding: '14px 44px',
                  borderRadius: '100px',
                  fontSize: '16px',
                  fontWeight: 600,
                  boxShadow: '0 8px 24px rgba(59, 130, 246, 0.35)',
                  cursor: 'pointer',
                  transition: 'all 0.25s ease-out',
                  border: 'none',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = 'translateY(-2px)';
                  e.currentTarget.style.boxShadow = '0 12px 28px rgba(59, 130, 246, 0.45)';
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = 'translateY(0)';
                  e.currentTarget.style.boxShadow = '0 8px 24px rgba(59, 130, 246, 0.35)';
                }}
              >
                Get Started
              </button>

              {/* Bullet Dots */}
              <div style={{ display: 'flex', gap: '8px', paddingLeft: '12px' }}>
                {['#F59E0B', '#EF4444', '#EC4899', '#8B5CF6', '#6366F1'].map((color, i) => (
                  <span
                    key={i}
                    style={{
                      width: '8px',
                      height: '8px',
                      borderRadius: '50%',
                      backgroundColor: color,
                      display: 'inline-block',
                    }}
                  />
                ))}
              </div>
            </div>
          </div>

          {/* Right Column illustration */}
          <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            position: 'relative',
            animation: 'fadeUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards',
          }}>
            {/* Glass panel wrapper */}
            <div style={{
              background: 'rgba(255, 255, 255, 0.22)',
              backdropFilter: 'blur(12px)',
              WebkitBackdropFilter: 'blur(12px)',
              borderRadius: '28px',
              padding: '20px',
              border: '1px solid rgba(255, 255, 255, 0.4)',
              boxShadow: '0 25px 50px rgba(42, 38, 89, 0.1)',
            }}>
              <img
                src={heroIllustration}
                alt="E-Commerce isometric scene"
                style={{
                  maxWidth: '100%',
                  height: 'auto',
                  borderRadius: '20px',
                  display: 'block',
                }}
              />
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
