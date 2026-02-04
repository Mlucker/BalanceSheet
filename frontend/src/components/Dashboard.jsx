import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import api from '../api/api';

const Dashboard = () => {
    const [metrics, setMetrics] = useState({
        totalRevenue: 0,
        totalExpenses: 0,
        netIncome: 0,
        cashBalance: 0,
        accountsReceivable: 0
    });
    const [revenueData, setRevenueData] = useState([]);
    const [recentTransactions, setRecentTransactions] = useState([]);
    const [recentInvoices, setRecentInvoices] = useState([]);
    const [currency, setCurrency] = useState('USD');

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;
        const currentYear = new Date().getFullYear();

        // Fetch company details
        api.get(`/companies/${companyId}`)
            .then(res => setCurrency(res.data.currency))
            .catch(err => console.error(err));

        // Fetch financial position for metrics
        api.get(`/financial-position/detailed?year=${currentYear}`, { headers: { 'X-Company-ID': companyId } })
            .then(res => {
                const data = res.data;

                const getTotal = (type) => {
                    if (!data[type]) return 0;
                    return data[type].reduce((sum, item) => sum + item.amount, 0);
                };

                const revenue = Math.abs(getTotal('REVENUE'));
                const expenses = getTotal('EXPENSE');
                const assets = data['ASSET'] || [];
                const cashAccount = assets.find(a => a.name.toLowerCase().includes('cash'));
                const arAccount = assets.find(a => a.name.toLowerCase().includes('receivable'));

                setMetrics({
                    totalRevenue: revenue,
                    totalExpenses: expenses,
                    netIncome: revenue - expenses,
                    cashBalance: cashAccount ? Math.abs(cashAccount.amount) : 0,
                    accountsReceivable: arAccount ? Math.abs(arAccount.amount) : 0
                });
            })
            .catch(err => console.error(err));

        // Fetch revenue trend (last 6 months)
        const months = [];
        for (let i = 5; i >= 0; i--) {
            const date = new Date();
            date.setMonth(date.getMonth() - i);
            months.push({
                month: date.toLocaleDateString('en-US', { month: 'short' }),
                year: date.getFullYear(),
                monthNum: date.getMonth() + 1
            });
        }

        // Simulate revenue data (in production, fetch from backend)
        const mockRevenueData = months.map(m => ({
            name: m.month,
            revenue: Math.floor(Math.random() * 50000) + 30000
        }));
        setRevenueData(mockRevenueData);

        // Fetch recent transactions
        api.get('/transactions', { headers: { 'X-Company-ID': companyId } })
            .then(res => {
                const sorted = res.data.sort((a, b) => new Date(b.date) - new Date(a.date));
                setRecentTransactions(sorted.slice(0, 5));
            })
            .catch(err => console.error(err));

        // Fetch recent invoices
        api.get('/invoices', { headers: { 'X-Company-ID': companyId } })
            .then(res => {
                const sorted = res.data.sort((a, b) => new Date(b.date) - new Date(a.date));
                setRecentInvoices(sorted.slice(0, 5));
            })
            .catch(err => console.error(err));
    }, []);

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(amount || 0);
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    };

    return (
        <div className="container">
            <h1 style={{ marginBottom: '2rem' }}>Dashboard</h1>

            {/* Key Metrics */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
                <div className="glass-panel stat-card">
                    <span className="stat-label">Total Revenue</span>
                    <span className="stat-value" style={{ color: 'var(--success)' }}>
                        {formatCurrency(metrics.totalRevenue)}
                    </span>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Year to Date</span>
                </div>

                <div className="glass-panel stat-card">
                    <span className="stat-label">Total Expenses</span>
                    <span className="stat-value" style={{ color: 'var(--danger)' }}>
                        {formatCurrency(metrics.totalExpenses)}
                    </span>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Year to Date</span>
                </div>

                <div className="glass-panel stat-card">
                    <span className="stat-label">Net Income</span>
                    <span className="stat-value" style={{ color: metrics.netIncome >= 0 ? 'var(--success)' : 'var(--danger)' }}>
                        {formatCurrency(metrics.netIncome)}
                    </span>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Year to Date</span>
                </div>

                <div className="glass-panel stat-card">
                    <span className="stat-label">Cash Balance</span>
                    <span className="stat-value" style={{ color: 'var(--accent-primary)' }}>
                        {formatCurrency(metrics.cashBalance)}
                    </span>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Current</span>
                </div>

                <div className="glass-panel stat-card">
                    <span className="stat-label">Accounts Receivable</span>
                    <span className="stat-value" style={{ color: 'var(--accent-secondary)' }}>
                        {formatCurrency(metrics.accountsReceivable)}
                    </span>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Outstanding</span>
                </div>
            </div>

            {/* Revenue Trend Chart */}
            <div className="glass-panel" style={{ marginBottom: '2rem', padding: '1.5rem' }}>
                <h2 style={{ marginTop: 0, marginBottom: '1.5rem' }}>Revenue Trend</h2>
                <ResponsiveContainer width="100%" height={250}>
                    <LineChart data={revenueData}>
                        <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" />
                        <XAxis dataKey="name" stroke="var(--text-secondary)" />
                        <YAxis stroke="var(--text-secondary)" />
                        <Tooltip
                            contentStyle={{ background: 'var(--glass-bg)', border: '1px solid var(--glass-border)', borderRadius: '4px' }}
                            formatter={(value) => formatCurrency(value)}
                        />
                        <Line type="monotone" dataKey="revenue" stroke="var(--accent-primary)" strokeWidth={2} dot={{ fill: 'var(--accent-primary)' }} />
                    </LineChart>
                </ResponsiveContainer>
            </div>

            {/* Quick Actions */}
            <div className="glass-panel" style={{ marginBottom: '2rem', padding: '1.5rem' }}>
                <h2 style={{ marginTop: 0, marginBottom: '1rem' }}>Quick Actions</h2>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '1rem' }}>
                    <Link to="/invoices/new" className="btn btn-primary" style={{ textAlign: 'center', textDecoration: 'none' }}>
                        + New Invoice
                    </Link>
                    <Link to="/record-incident" className="btn" style={{ textAlign: 'center', textDecoration: 'none', background: 'var(--glass-bg)' }}>
                        Record Transaction
                    </Link>
                    <Link to="/customers" className="btn" style={{ textAlign: 'center', textDecoration: 'none', background: 'var(--glass-bg)' }}>
                        Manage Customers
                    </Link>
                    <Link to="/products" className="btn" style={{ textAlign: 'center', textDecoration: 'none', background: 'var(--glass-bg)' }}>
                        Manage Products
                    </Link>
                </div>
            </div>

            {/* Recent Activity */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
                {/* Recent Invoices */}
                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                        <h2 style={{ margin: 0 }}>Recent Invoices</h2>
                        <Link to="/invoices" style={{ color: 'var(--accent-primary)', textDecoration: 'none', fontSize: '0.9rem' }}>View All →</Link>
                    </div>
                    {recentInvoices.length === 0 ? (
                        <p style={{ color: 'var(--text-secondary)', fontStyle: 'italic' }}>No invoices yet</p>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                            {recentInvoices.map(inv => (
                                <div key={inv.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.5rem', background: 'rgba(255,255,255,0.02)', borderRadius: '4px' }}>
                                    <div>
                                        <div style={{ fontWeight: 'bold' }}>{inv.invoiceNumber}</div>
                                        <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{inv.customer?.name}</div>
                                    </div>
                                    <div style={{ textAlign: 'right' }}>
                                        <div style={{ fontWeight: 'bold' }}>{formatCurrency(inv.totalAmount)}</div>
                                        <div style={{ fontSize: '0.75rem', color: inv.status === 'PAID' ? 'var(--success)' : inv.status === 'POSTED' ? 'var(--accent-primary)' : 'var(--text-secondary)' }}>
                                            {inv.status}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                {/* Recent Transactions */}
                <div className="glass-panel" style={{ padding: '1.5rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                        <h2 style={{ margin: 0 }}>Recent Transactions</h2>
                        <Link to="/audit-trail" style={{ color: 'var(--accent-primary)', textDecoration: 'none', fontSize: '0.9rem' }}>View All →</Link>
                    </div>
                    {recentTransactions.length === 0 ? (
                        <p style={{ color: 'var(--text-secondary)', fontStyle: 'italic' }}>No transactions yet</p>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                            {recentTransactions.map(tx => (
                                <div key={tx.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.5rem', background: 'rgba(255,255,255,0.02)', borderRadius: '4px' }}>
                                    <div>
                                        <div style={{ fontWeight: '500' }}>{tx.description}</div>
                                        <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{formatDate(tx.date)}</div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
