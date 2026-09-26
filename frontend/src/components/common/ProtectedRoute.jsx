import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Loader from './Loader';

export default function ProtectedRoute({ children, role }) {
  const { isAuthenticated, user, loading } = useAuth();

  if (loading) {
    return <Loader />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (role && user?.role !== role) {
    // Wrong role — redirect to appropriate home
    if (user?.role === 'ADMIN') {
      return <Navigate to="/admin" replace />;
    }
    return <Navigate to="/" replace />;
  }

  return children;
}
