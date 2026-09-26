/**
 * Format a number as Indian Rupees (₹)
 */
export function formatPrice(amount) {
  if (amount == null) return '₹0.00';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);
}

/**
 * Format an ISO date string to a human-readable format
 */
export function formatDate(isoString) {
  if (!isoString) return '—';
  const date = new Date(isoString);
  return new Intl.DateTimeFormat('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}

/**
 * Format an ISO date string to just the date (no time)
 */
export function formatDateShort(isoString) {
  if (!isoString) return '—';
  const date = new Date(isoString);
  return new Intl.DateTimeFormat('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  }).format(date);
}

/**
 * Returns { bg, color } for a status badge
 */
export function getStatusColor(status) {
  const map = {
    PENDING:   { bg: '#FEF3C7', color: '#92400E' },
    CONFIRMED: { bg: '#D8F3DC', color: '#1B4332' },
    SHIPPED:   { bg: '#E0F0FF', color: '#1E3A5F' },
    DELIVERED: { bg: '#D8F3DC', color: '#1B4332' },
    CANCELLED: { bg: '#F8DCDC', color: '#9B1D20' },
    FAILED:    { bg: '#F8DCDC', color: '#9B1D20' },
    SUCCESS:   { bg: '#D8F3DC', color: '#1B4332' },
    REFUNDED:  { bg: '#FEF3C7', color: '#92400E' },
  };
  return map[status] || { bg: '#F0EBE3', color: '#78716C' };
}

/**
 * Extract the error message from an API error response
 */
export function getErrorMessage(error) {
  if (error?.response?.data?.message) {
    return error.response.data.message;
  }
  if (error?.message) {
    return error.message;
  }
  return 'Something went wrong. Please try again.';
}

/**
 * Truncate text to a max length
 */
export function truncate(str, maxLength = 60) {
  if (!str) return '';
  if (str.length <= maxLength) return str;
  return str.slice(0, maxLength) + '…';
}
