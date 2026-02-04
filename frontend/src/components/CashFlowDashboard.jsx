import { useEffect, useState } from 'react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import api from '../api/api';

const CashFlowDashboard = () => {
    const [history, setHistory] = useState([]);
    const [forecast, setForecast] = useState(null);
    const [currency, setCurrency] = useState('USD');

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get(`/companies/${companyId}`).then(res => setCurrency(res.data.currency));

        api.get('/cash-flow/history', { headers: { 'X-Company-ID': companyId } })
            .then(res => {
                // Parse dates for chart
                const formatted = res.data.map(d => ({
                    ...d,
                    dateStr: new Date(d.date).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })
                }));
                setHistory(formatted);
            });

        api.get('/cash-flow/forecast', { headers: { 'X-Company-ID': companyId } })
            .then(res => setForecast(res.data));
    }, []);

    const formatCurrency = (val) => new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(val || 0);

    return (
        <div className="container">
            <h1>Cash Flow Management</h1>

            <div className="dashboard-grid">
                {/* Stats Widgets */}
                <div className="glass-panel" style={{ borderLeft: '4px solid #38bdf8' }}>
                    <div className="stat-label">Current Cash on Hand</div>
                    <div className="stat-value">
                        {history.length > 0 ? formatCurrency(history[history.length - 1].balance) : '-'}
                    </div>
                </div>

                <div className="glass-panel" style={{ borderLeft: '4px solid var(--success)' }}>
                    <div className="stat-label">Projected Inflow (30 Days)</div>
                    <div className="stat-value" style={{ color: 'var(--success)' }}>
                        {forecast ? formatCurrency(forecast.totalInflow) : '-'}
                    </div>
                </div>

                <div className="glass-panel" style={{ borderLeft: '4px solid var(--danger)' }}>
                    <div className="stat-label">Projected Outflow (30 Days)</div>
                    <div className="stat-value" style={{ color: 'var(--danger)' }}>
                        {forecast ? formatCurrency(forecast.totalOutflow) : '-'}
                    </div>
                </div>
            </div>

            <div className="dashboard-grid" style={{ gridTemplateColumns: '2fr 1fr', marginTop: '2rem' }}>
                {/* Main Chart */}
                <div className="glass-panel" style={{ minHeight: '400px' }}>
                    <h3>Cash Balance Trend (Last 30 Days)</h3>
                    {history.length > 0 ? (
                        <ResponsiveContainer width="100%" height={300}>
                            <AreaChart data={history}>
                                <defs>
                                    <linearGradient id="colorBalance" x1="0" y1="0" x2="0" y2="1">
                                        <stop offset="5%" stopColor="#38bdf8" stopOpacity={0.8} />
                                        <stop offset="95%" stopColor="#38bdf8" stopOpacity={0} />
                                    </linearGradient>
                                </defs>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(255,255,255,0.1)" />
                                <XAxis dataKey="dateStr" stroke="rgba(255,255,255,0.5)" tick={{ fontSize: 12 }} />
                                <YAxis stroke="rgba(255,255,255,0.5)" tick={{ fontSize: 12 }} />
                                <Tooltip
                                    contentStyle={{ backgroundColor: 'rgba(0,0,0,0.8)', border: 'none', borderRadius: '8px' }}
                                    itemStyle={{ color: '#38bdf8' }}
                                />
                                <Area type="monotone" dataKey="balance" stroke="#38bdf8" fillOpacity={1} fill="url(#colorBalance)" />
                            </AreaChart>
                        </ResponsiveContainer>
                    ) : <p>Loading data...</p>}
                </div>

                {/* Forecast Details */}
                <div className="glass-panel">
                    <h3>Forecast Details</h3>
                    <div style={{ marginBottom: '1.5rem' }}>
                        <h4 style={{ color: 'var(--success)', marginBottom: '0.5rem', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '0.25rem' }}>Incoming</h4>
                        <div style={{ maxHeight: '150px', overflowY: 'auto' }}>
                            {forecast && forecast.invoices.length > 0 ? (
                                forecast.invoices.map((inv, i) => (
                                    <div key={i} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem', marginBottom: '0.5rem' }}>
                                        <span>{inv.customer}</span>
                                        <span style={{ fontWeight: 'bold' }}>{formatCurrency(inv.amount)}</span>
                                    </div>
                                ))
                            ) : <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>No incoming payments</span>}
                        </div>
                    </div>

                    <div>
                        <h4 style={{ color: 'var(--danger)', marginBottom: '0.5rem', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '0.25rem' }}>Outgoing</h4>
                        <div style={{ maxHeight: '150px', overflowY: 'auto' }}>
                            {forecast && forecast.recurringExpenses.length > 0 ? (
                                forecast.recurringExpenses.map((exp, i) => (
                                    <div key={i} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem', marginBottom: '0.5rem' }}>
                                        <span>{exp.name}</span>
                                        <span style={{ fontWeight: 'bold' }}>{formatCurrency(exp.amount)}</span>
                                    </div>
                                ))
                            ) : <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>No scheduled expenses</span>}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CashFlowDashboard;
