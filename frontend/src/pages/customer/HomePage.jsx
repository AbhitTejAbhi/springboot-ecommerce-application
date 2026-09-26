import { useState, useEffect, useCallback } from 'react';
import { getAllProducts, getProductsByCategory, searchProducts } from '../../api/productApi';
import { getAllCategories } from '../../api/categoryApi';
import ProductCard from '../../components/product/ProductCard';
import ProductGrid from '../../components/product/ProductGrid';
import { ProductCardSkeleton } from '../../components/common/Skeleton';
import EmptyState from '../../components/common/EmptyState';

export default function HomePage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  // Fetch categories
  useEffect(() => {
    getAllCategories(0, 100).then((res) => {
      setCategories(res.data.data.content || []);
    }).catch(() => {});
  }, []);

  // Fetch products
  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      let res;
      if (searchKeyword) {
        res = await searchProducts(searchKeyword, page, 12);
      } else if (selectedCategory) {
        res = await getProductsByCategory(selectedCategory, page, 12);
      } else {
        res = await getAllProducts(page, 12);
      }
      const data = res.data.data;
      setProducts((data.content || []).filter((p) => p.active));
      setTotalPages(data.totalPages || 0);
    } catch {
      setProducts([]);
    } finally {
      setLoading(false);
    }
  }, [page, selectedCategory, searchKeyword]);

  useEffect(() => { fetchProducts(); }, [fetchProducts]);

  const handleCategorySelect = (catId) => {
    setSelectedCategory(catId);
    setSearchKeyword('');
    setSearchInput('');
    setPage(0);
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setSelectedCategory(null);
    setSearchKeyword(searchInput.trim());
    setPage(0);
  };

  const pillStyle = (active) => ({
    padding: '8px 18px',
    borderRadius: '100px',
    fontSize: '13px',
    fontWeight: 500,
    border: `1.5px solid ${active ? '#7C3AED' : '#D6D0C8'}`,
    background: active ? '#7C3AED' : 'transparent',
    color: active ? '#FFFFFF' : '#1C1917',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    whiteSpace: 'nowrap',
    boxShadow: active ? '0 4px 10px rgba(124, 58, 237, 0.2)' : 'none',
  });

  return (
    <div style={{
      maxWidth: '1200px',
      margin: '0 auto',
      padding: '32px 24px 60px 24px',
      animation: 'fadeUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards',
    }}>
      {/* Search & Header Row */}
      <div style={{
        display: 'flex',
        flexWrap: 'wrap',
        alignItems: 'flex-end',
        justifyContent: 'space-between',
        gap: '24px',
        marginBottom: '36px',
      }}>
        <div>
          <h2 style={{ fontSize: '28px', fontWeight: 700, color: '#1C1917', letterSpacing: '-0.02em', marginBottom: '6px' }}>
            Curated Catalog
          </h2>
          <p style={{ fontSize: '14px', color: '#78716C' }}>Find the best products tailored for you</p>
        </div>

        {/* Search bar */}
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: '8px', width: '100%', maxWidth: '380px' }}>
          <input
            type="text"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            placeholder="Search products..."
            style={{
              flex: 1,
              padding: '11px 18px',
              fontSize: '14px',
              color: '#1C1917',
              background: '#FFFFFF',
              border: '1.5px solid #E5E5E5',
              borderRadius: '100px',
              transition: 'all 0.2s ease',
              outline: 'none',
            }}
            onFocus={(e) => {
              e.target.style.borderColor = '#7C3AED';
              e.target.style.boxShadow = '0 0 0 3px rgba(124, 58, 237, 0.15)';
            }}
            onBlur={(e) => {
              e.target.style.borderColor = '#E5E5E5';
              e.target.style.boxShadow = 'none';
            }}
          />
          <button type="submit" style={{
            padding: '11px 24px',
            fontSize: '13px',
            fontWeight: 600,
            borderRadius: '100px',
            background: '#7C3AED',
            color: '#FFFFFF',
            border: 'none',
            cursor: 'pointer',
            boxShadow: '0 4px 12px rgba(124, 58, 237, 0.25)',
            transition: 'all 0.2s ease',
          }}
            onMouseEnter={(e) => { e.currentTarget.style.background = '#6D28D9'; }}
            onMouseLeave={(e) => { e.currentTarget.style.background = '#7C3AED'; }}
            onMouseDown={(e) => { e.currentTarget.style.transform = 'scale(0.97)'; }}
            onMouseUp={(e) => { e.currentTarget.style.transform = 'scale(1)'; }}
          >
            Search
          </button>
        </form>
      </div>

      {/* Category Pills Filter */}
      <div style={{
        display: 'flex',
        flexWrap: 'wrap',
        gap: '10px',
        marginBottom: '40px',
        paddingBottom: '12px',
        borderBottom: '1px solid #E8E2D9',
      }}>
        <button onClick={() => handleCategorySelect(null)} style={pillStyle(!selectedCategory && !searchKeyword)}>All Products</button>
        {categories.map((cat) => (
          <button key={cat.id} onClick={() => handleCategorySelect(cat.id)} style={pillStyle(selectedCategory === cat.id)}>
            {cat.name}
          </button>
        ))}
      </div>

      {/* Search result indicator */}
      {searchKeyword && (
        <div style={{ marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '14px', color: '#78716C' }}>Results for "{searchKeyword}"</span>
          <button onClick={() => { setSearchKeyword(''); setSearchInput(''); setPage(0); }} style={{
            fontSize: '13px', color: '#EF4444', cursor: 'pointer', fontWeight: 600,
          }}>
            Clear
          </button>
        </div>
      )}

      {/* Products Grid */}
      {loading ? (
        <ProductGrid>
          {Array.from({ length: 8 }).map((_, i) => <ProductCardSkeleton key={i} />)}
        </ProductGrid>
      ) : products.length === 0 ? (
        <EmptyState
          icon="🔍"
          title="No products found"
          subtitle={searchKeyword ? `No results for "${searchKeyword}"` : 'Check back later for new arrivals'}
        />
      ) : (
        <ProductGrid>
          {products.map((p) => <ProductCard key={p.id} product={p} />)}
        </ProductGrid>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px', marginTop: '48px' }}>
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            style={{
              padding: '10px 20px',
              fontSize: '13px',
              fontWeight: 600,
              borderRadius: '100px',
              border: '1.5px solid #E5E5E5',
              background: page === 0 ? '#FAF8FD' : '#FFFFFF',
              color: page === 0 ? '#A8A29E' : '#1C1917',
              cursor: page === 0 ? 'not-allowed' : 'pointer',
              transition: 'all 0.2s ease',
            }}
          >
            Previous
          </button>
          <span style={{ fontSize: '14px', color: '#78716C', padding: '0 8px', fontWeight: 500 }}>
            Page {page + 1} of {totalPages}
          </span>
          <button
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            style={{
              padding: '10px 20px',
              fontSize: '13px',
              fontWeight: 600,
              borderRadius: '100px',
              border: '1.5px solid #E5E5E5',
              background: page >= totalPages - 1 ? '#FAF8FD' : '#FFFFFF',
              color: page >= totalPages - 1 ? '#A8A29E' : '#1C1917',
              cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer',
              transition: 'all 0.2s ease',
            }}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
