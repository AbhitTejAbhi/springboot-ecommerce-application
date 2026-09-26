import { useState, useEffect, createContext, useContext, useCallback } from 'react';

const ToastContext = createContext(null);

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) throw new Error('useToast must be used within ToastProvider');
  return context;
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = 'success') => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 3000);
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  return (
    <ToastContext.Provider value={{ addToast }}>
      {children}
      <div style={{
        position: 'fixed',
        bottom: '24px',
        right: '24px',
        zIndex: 9999,
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
      }}>
        {toasts.map((toast) => (
          <div
            key={toast.id}
            onClick={() => removeToast(toast.id)}
            style={{
              background: toast.type === 'error' ? '#9B1D20' : '#1C1917',
              color: '#F5F0E8',
              padding: '12px 20px',
              borderRadius: '8px',
              fontSize: '13px',
              fontWeight: 500,
              maxWidth: '320px',
              boxShadow: '0 4px 16px rgba(0,0,0,0.15)',
              cursor: 'pointer',
              animation: 'slideUp 0.25s cubic-bezier(0.16, 1, 0.3, 1) forwards',
              lineHeight: 1.5,
            }}
          >
            {toast.message}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}
