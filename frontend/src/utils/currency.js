// Currency formatting utility
export const formatCurrency = (amount, currency = 'USD') => {
    const value = parseFloat(amount);
    if (isNaN(value)) return '$0.00';

    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(value);
};

// Format currency without symbol (for inputs)
export const formatNumber = (amount) => {
    const value = parseFloat(amount);
    if (isNaN(value)) return '0.00';
    return value.toFixed(2);
};
