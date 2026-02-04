import { useEffect, useState } from 'react';
import api from '../../api/api';

const ProfitAndLoss = () => {
    const [data, setData] = useState(null);
    const [year, setYear] = useState(new Date().getFullYear());
    const [currency, setCurrency] = useState('USD');

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get(`/companies/${companyId}`).then(res => setCurrency(res.data.currency));

        api.get(`/reports/pnl?year=${year}`, { headers: { 'X-Company-ID': companyId } })
            .then(res => setData(res.data))
            .catch(err => console.error(err));
    }, [year]);

    const formatCurrency = (val) => new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(val || 0);

    if (!data) return <p>Loading...</p>;

    const { current, previous } = data;

    return (
        <div className="glass-panel">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                <h2>Profit & Loss (P&L)</h2>
                <select value={year} onChange={e => setYear(e.target.value)} style={{ padding: '0.5rem', borderRadius: '4px' }}>
                    <option value="2023">2023</option>
                    <option value="2024">2024</option>
                    <option value="2025">2025</option>
                </select>
            </div>

            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr style={{ borderBottom: '1px solid var(--glass-border)' }}>
                        <th style={{ textAlign: 'left', padding: '1rem' }}>Category</th>
                        <th style={{ textAlign: 'right', padding: '1rem' }}>{year}</th>
                        <th style={{ textAlign: 'right', padding: '1rem', color: 'var(--text-secondary)' }}>{year - 1}</th>
                        <th style={{ textAlign: 'right', padding: '1rem' }}>Change</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td style={{ padding: '1rem' }}>Revenue</td>
                        <td style={{ padding: '1rem', textAlign: 'right', color: 'var(--success)' }}>{formatCurrency(current.revenue)}</td>
                        <td style={{ padding: '1rem', textAlign: 'right', color: 'var(--text-secondary)' }}>{formatCurrency(previous.revenue)}</td>
                        <td style={{ padding: '1rem', textAlign: 'right' }}>
                            {current.revenue - previous.revenue > 0 ? '+' : ''}{formatCurrency(current.revenue - previous.revenue)}
                        </td>
                    </tr>
                    <tr>
                        <td style={{ padding: '1rem' }}>Expense</td>
                        <td style={{ padding: '1rem', textAlign: 'right', color: 'var(--danger)' }}>{formatCurrency(current.expense)}</td>
                        <td style={{ padding: '1rem', textAlign: 'right', color: 'var(--text-secondary)' }}>{formatCurrency(previous.expense)}</td>
                        <td style={{ padding: '1rem', textAlign: 'right' }}>
                            {formatCurrency(current.expense - previous.expense)}
                        </td>
                    </tr>
                    <tr style={{ borderTop: '1px solid var(--glass-border)', fontWeight: 'bold' }}>
                        <td style={{ padding: '1rem' }}>Net Income</td>
                        <td style={{ padding: '1rem', textAlign: 'right' }}>{formatCurrency(current.netIncome)}</td>
                        <td style={{ padding: '1rem', textAlign: 'right', color: 'var(--text-secondary)' }}>{formatCurrency(previous.netIncome)}</td>
                        <td style={{ padding: '1rem', textAlign: 'right' }}>
                            {formatCurrency(current.netIncome - previous.netIncome)}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    );
};

export default ProfitAndLoss;
