import { createContext, useState, useEffect, useCallback } from 'react';
import { loginUser, registerUser } from '../api/authApi';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // On mount, restore session from localStorage
  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');
    if (savedToken && savedUser) {
      try {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      } catch {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    setLoading(false);
  }, []);

  const saveSession = useCallback((authResponse) => {
    const userData = {
      userId: authResponse.userId,
      name: authResponse.name,
      email: authResponse.email,
      role: authResponse.role,
    };
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('user', JSON.stringify(userData));
    setToken(authResponse.token);
    setUser(userData);
  }, []);

  const login = useCallback(async (credentials) => {
    const res = await loginUser(credentials);
    const authResponse = res.data.data;
    saveSession(authResponse);
    return authResponse;
  }, [saveSession]);

  const register = useCallback(async (data) => {
    const res = await registerUser(data);
    const authResponse = res.data.data;
    saveSession(authResponse);
    return authResponse;
  }, [saveSession]);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  }, []);

  const value = {
    user,
    token,
    isAuthenticated: !!token,
    isAdmin: user?.role === 'ADMIN',
    isCustomer: user?.role === 'CUSTOMER',
    loading,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}
