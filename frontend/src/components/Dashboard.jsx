import { useEffect, useState } from 'react';
import api from '../api/api';

const Dashboard = () => {
    const [detailedPostion, setDetailedPosition] = useState({});
    const [year, setYear] = useState(new Date().getFullYear());
    const [currency, setCurrency] = useState('USD');
    const [companyId, setCompanyId] = useState(1); // Default to 1 (Demo) or dynamic

    useEffect(() => {
        // Fetch Company Details
        const currentCompanyId = localStorage.getItem('companyId') || 1;
        setCompanyId(currentCompanyId);

        api.get(`/companies/${currentCompanyId}`)
            .then(res => setCurrency(res.data.currency))
            .catch(err => console.error("Failed to fetch company", err));

        api.get(`/financial-position/detailed?year=${year}`)
            .then(res => setDetailedPosition(res.data))
            .catch(err => console.error(err));
    }, [year]);

    const handleCurrencyChange = (newCurrency) => {
        setCurrency(newCurrency);
        // Persist change
        api.put(`/companies/${companyId}`, { currency: newCurrency })
            .catch(err => {
                console.error("Failed to update currency", err);
                // Revert on failure if needed, or show error
            });
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: currency }).format(amount || 0);
    };

    // Calculate totals from detailed view
    const getTotal = (type) => {
        if (!detailedPostion[type]) return 0;
        return detailedPostion[type].reduce((sum, item) => sum + item.amount, 0);
    };

    const assets = getTotal('ASSET');
    const liabilities = getTotal('LIABILITY');
    const equity = getTotal('EQUITY');
    const revenue = getTotal('REVENUE');
    const expense = getTotal('EXPENSE');

    const netIncome = Math.abs(revenue) - expense;

    const getAccountColor = (type) => {
        switch (type) {
            case 'REVENUE':
            case 'ASSET':
            case 'EQUITY':
                return 'var(--success)';
            case 'EXPENSE':
            case 'LIABILITY':
                return 'var(--danger)';
            default:
                return 'var(--text-primary)';
        }
    };

    const renderAccountList = (type, title) => {
        const items = detailedPostion[type] || [];
        const sectionColor = getAccountColor(type);

        return (
            <div className="glass-panel" style={{ marginBottom: '1rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', borderBottom: '1px solid var(--glass-border)', paddingBottom: '0.5rem' }}>
                    <h3 style={{ margin: 0 }}>{title}</h3>
                    <span style={{ fontWeight: 'bold', fontSize: '1.1rem', color: sectionColor }}>{formatCurrency(Math.abs(getTotal(type)))}</span>
                </div>
                {items.length === 0 && <div style={{ color: 'var(--text-secondary)', fontStyle: 'italic' }}>No accounts</div>}
                {items.map(item => (
                    <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.25rem 0' }}>
                        <span>{item.name}</span>
                        <span style={{ color: sectionColor, fontWeight: '500' }}>{formatCurrency(Math.abs(item.amount))}</span>
                    </div>
                ))}
            </div>
        );
    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h1>Statement of Changes</h1>
                <div style={{ display: 'flex', gap: '1rem' }}>
                    <select
                        value={currency}
                        onChange={e => handleCurrencyChange(e.target.value)}
                        style={{ padding: '0.5rem', borderRadius: '4px', background: 'var(--glass-bg)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                    >
                        <option value="USD">USD ($)</option>
                        <option value="EUR">EUR (€)</option>
                    </select>
                    <select
                        value={year}
                        onChange={e => setYear(parseInt(e.target.value))}
                        style={{ padding: '0.5rem', borderRadius: '4px', background: 'var(--glass-bg)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                    >
                        <option value={2023}>2023</option>
                        <option value={2024}>2024</option>
                        <option value={2025}>2025</option>
                        <option value={2026}>2026</option>
                    </select>
                </div>
            </div>

            <div className="dashboard-grid">
                <div className="glass-panel stat-card">
                    <span className="stat-label">Net Income</span>
                    <span className="stat-value" style={{ color: netIncome >= 0 ? 'var(--success)' : 'var(--danger)' }}>
                        {formatCurrency(netIncome)}
                    </span>
                    <div style={{ marginTop: '0.5rem', fontSize: '0.9rem' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}><span>Revenue:</span> <span>{formatCurrency(Math.abs(revenue))}</span></div>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}><span>Expenses:</span> <span>{formatCurrency(expense)}</span></div>
                    </div>
                </div>
            </div>

            <h2 style={{ marginTop: '3rem', marginBottom: '1rem', borderBottom: '1px solid var(--accent-primary)', display: 'inline-block' }}>Statement of Financial Position</h2>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
                <div>
                    {renderAccountList('ASSET', 'Assets')}
                </div>
                <div>
                    {renderAccountList('LIABILITY', 'Liabilities')}
                    <div className="glass-panel" style={{ marginBottom: '1rem', background: 'rgba(56, 189, 248, 0.05)' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', borderBottom: '1px solid var(--glass-border)', paddingBottom: '0.5rem' }}>
                            <h3 style={{ margin: 0 }}>Equity</h3>
                            <span style={{ fontWeight: 'bold', fontSize: '1.1rem' }}>{formatCurrency(Math.abs(equity) + netIncome)}</span>
                        </div>
                        {/* Original Equity Items */}
                        {(detailedPostion['EQUITY'] || []).map(item => (
                            <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.25rem 0' }}>
                                <span>{item.name}</span>
                                <span>{formatCurrency(Math.abs(item.amount))}</span>
                            </div>
                        ))}
                        {/* Net Income Line */}
                        <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.25rem 0', fontWeight: 'bold', color: netIncome >= 0 ? 'var(--success)' : 'var(--danger)' }}>
                            <span>Retained Earnings (Net Income)</span>
                            <span>{formatCurrency(netIncome)}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
