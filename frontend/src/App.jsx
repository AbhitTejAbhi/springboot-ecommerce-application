import { Routes, Route, Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import Navbar from './components/common/Navbar';
import Footer from './components/common/Footer';
import ProtectedRoute from './components/common/ProtectedRoute';

// Pages - Auth
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';

// Pages - Customer
import LandingPage from './pages/customer/LandingPage';
import HomePage from './pages/customer/HomePage';
import ProductDetailPage from './pages/customer/ProductDetailPage';
import CartPage from './pages/customer/CartPage';
import CheckoutPage from './pages/customer/CheckoutPage';
import OrdersPage from './pages/customer/OrdersPage';
import OrderDetailPage from './pages/customer/OrderDetailPage';
import PaymentPage from './pages/customer/PaymentPage';
import AddressPage from './pages/customer/AddressPage';
import ProfilePage from './pages/customer/ProfilePage';

// Pages - Admin
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminProductsPage from './pages/admin/AdminProductsPage';
import AdminCategoriesPage from './pages/admin/AdminCategoriesPage';
import AdminOrdersPage from './pages/admin/AdminOrdersPage';
import AdminPaymentsPage from './pages/admin/AdminPaymentsPage';

function AdminLayout({ children }) {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItems = [
    { label: 'Dashboard', path: '/admin' },
    { label: 'Products', path: '/admin/products' },
    { label: 'Categories', path: '/admin/categories' },
    { label: 'Orders', path: '/admin/orders' },
    { label: 'Payments', path: '/admin/payments' },
  ];

  return (
    <div style={{ display: 'flex', minHeight: '100vh', background: '#F5F0E8' }}>
      {/* Admin Sidebar per spec: width: 220px, background: #1C1917, color: #F5F0E8 */}
      <aside style={{
        width: '220px',
        flexShrink: 0,
        background: '#1C1917',
        color: '#F5F0E8',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        padding: '24px 16px',
        position: 'sticky',
        top: 0,
        height: '100vh',
      }}>
        <div>
          {/* Brand */}
          <div style={{ marginBottom: '32px', padding: '0 8px' }}>
            <Link to="/admin" style={{ fontSize: '18px', fontWeight: 700, color: '#F5F0E8', letterSpacing: '-0.02em' }}>
              Store Admin
            </Link>
            <div style={{ fontSize: '11px', color: '#78716C', marginTop: '2px', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
              Management
            </div>
          </div>

          {/* Navigation Links */}
          <nav style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
            {navItems.map((item) => {
              const isActive = location.pathname === item.path;
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  style={{
                    display: 'block',
                    padding: '10px 12px',
                    borderRadius: '6px',
                    fontSize: '13px',
                    fontWeight: 500,
                    color: isActive ? '#F5F0E8' : '#78716C',
                    background: isActive ? 'rgba(245, 240, 232, 0.1)' : 'transparent',
                    transition: 'all 0.15s ease',
                  }}
                  onMouseEnter={(e) => {
                    if (!isActive) e.currentTarget.style.color = '#F5F0E8';
                  }}
                  onMouseLeave={(e) => {
                    if (!isActive) e.currentTarget.style.color = '#78716C';
                  }}
                >
                  {item.label}
                </Link>
              );
            })}
          </nav>
        </div>

        {/* User Info & Logout */}
        <div style={{ borderTop: '1px solid rgba(214, 208, 200, 0.15)', paddingTop: '16px', paddingLeft: '8px', paddingRight: '8px' }}>
          <div style={{ fontSize: '13px', fontWeight: 500, color: '#F5F0E8' }}>{user?.name}</div>
          <div style={{ fontSize: '11px', color: '#78716C', marginBottom: '12px' }}>{user?.email}</div>
          <button
            onClick={handleLogout}
            style={{
              fontSize: '12px',
              fontWeight: 500,
              color: '#F8DCDC',
              background: 'transparent',
              border: 'none',
              cursor: 'pointer',
              padding: 0,
            }}
          >
            Sign out
          </button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main style={{ flex: 1, padding: '32px 40px', maxWidth: '1200px' }}>
        {children}
      </main>
    </div>
  );
}

function CustomerLayout({ children }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Navbar />
      <main style={{ flex: 1 }}>
        {children}
      </main>
      <Footer />
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Shop is the opening page — accessible to everyone */}
      <Route path="/" element={
        <CustomerLayout>
          <HomePage />
        </CustomerLayout>
      } />
      <Route path="/products/:id" element={
        <CustomerLayout>
          <ProductDetailPage />
        </CustomerLayout>
      } />
      <Route path="/cart" element={
        <ProtectedRoute role="CUSTOMER">
          <CustomerLayout>
            <CartPage />
          </CustomerLayout>
        </ProtectedRoute>
      } />
      <Route path="/checkout" element={
        <ProtectedRoute role="CUSTOMER">
          <CustomerLayout>
            <CheckoutPage />
          </CustomerLayout>
        </ProtectedRoute>
      } />
      <Route path="/orders" element={
        <ProtectedRoute role="CUSTOMER">
          <CustomerLayout>
            <OrdersPage />
          </CustomerLayout>
        </ProtectedRoute>
      } />
      <Route path="/orders/:id" element={
        <ProtectedRoute role="CUSTOMER">
          <CustomerLayout>
            <OrderDetailPage />
          </CustomerLayout>
        </ProtectedRoute>
      } />
      <Route path="/payment/:orderId" element={
        <ProtectedRoute role="CUSTOMER">
          <CustomerLayout>
            <PaymentPage />
          </CustomerLayout>
        </ProtectedRoute>
      } />
      <Route path="/addresses" element={
        <ProtectedRoute role="CUSTOMER">
          <CustomerLayout>
            <AddressPage />
          </CustomerLayout>
        </ProtectedRoute>
      } />
      <Route path="/profile" element={
        <ProtectedRoute role="CUSTOMER">
          <CustomerLayout>
            <ProfilePage />
          </CustomerLayout>
        </ProtectedRoute>
      } />

      {/* Admin Routes (Protected: role ADMIN) */}
      <Route path="/admin" element={
        <ProtectedRoute role="ADMIN">
          <AdminLayout>
            <AdminDashboard />
          </AdminLayout>
        </ProtectedRoute>
      } />
      <Route path="/admin/products" element={
        <ProtectedRoute role="ADMIN">
          <AdminLayout>
            <AdminProductsPage />
          </AdminLayout>
        </ProtectedRoute>
      } />
      <Route path="/admin/categories" element={
        <ProtectedRoute role="ADMIN">
          <AdminLayout>
            <AdminCategoriesPage />
          </AdminLayout>
        </ProtectedRoute>
      } />
      <Route path="/admin/orders" element={
        <ProtectedRoute role="ADMIN">
          <AdminLayout>
            <AdminOrdersPage />
          </AdminLayout>
        </ProtectedRoute>
      } />
      <Route path="/admin/payments" element={
        <ProtectedRoute role="ADMIN">
          <AdminLayout>
            <AdminPaymentsPage />
          </AdminLayout>
        </ProtectedRoute>
      } />
    </Routes>
  );
}
