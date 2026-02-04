import { useEffect, useState } from 'react';
import api from '../../api/api';

const TrialBalance = () => {
    const [data, setData] = useState([]);
    const [currency, setCurrency] = useState('USD');

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get(`/companies/${companyId}`).then(res => setCurrency(res.data.currency));

        api.get('/reports/trial-balance', { headers: { 'X-Company-ID': companyId } })
            .then(res => {
                // Sort by account type then name
                const sorted = res.data.sort((a, b) => a.account.localeCompare(b.account));
                setData(sorted);
            })
            .catch(err => console.error(err));
    }, []);

    const formatCurrency = (val) => new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(val || 0);

    const totalDebit = data.reduce((sum, item) => sum + item.debit, 0);
    const totalCredit = data.reduce((sum, item) => sum + item.credit, 0);

    return (
        <div className="glass-panel">
            <h2>Trial Balance</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr style={{ borderBottom: '1px solid var(--glass-border)' }}>
                        <th style={{ textAlign: 'left', padding: '1rem' }}>Account</th>
                        <th style={{ textAlign: 'left', padding: '1rem' }}>Type</th>
                        <th style={{ textAlign: 'right', padding: '1rem' }}>Debit</th>
                        <th style={{ textAlign: 'right', padding: '1rem' }}>Credit</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((item, idx) => (
                        <tr key={idx} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                            <td style={{ padding: '0.75rem 1rem' }}>{item.account}</td>
                            <td style={{ padding: '0.75rem 1rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{item.type}</td>
                            <td style={{ padding: '0.75rem 1rem', textAlign: 'right' }}>{item.debit > 0 ? formatCurrency(item.debit) : '-'}</td>
                            <td style={{ padding: '0.75rem 1rem', textAlign: 'right' }}>{item.credit > 0 ? formatCurrency(item.credit) : '-'}</td>
                        </tr>
                    ))}
                    <tr style={{ borderTop: '2px solid var(--text-primary)', fontWeight: 'bold' }}>
                        <td style={{ padding: '1rem' }} colSpan={2}>Total</td>
                        <td style={{ padding: '1rem', textAlign: 'right' }}>{formatCurrency(totalDebit)}</td>
                        <td style={{ padding: '1rem', textAlign: 'right' }}>{formatCurrency(totalCredit)}</td>
                    </tr>
                </tbody>
            </table>
            {totalDebit !== totalCredit && (
                <div style={{ marginTop: '1rem', color: 'var(--danger)', fontWeight: 'bold' }}>
                    Warning: Trial Balance is out of balance! Variance: {formatCurrency(Math.abs(totalDebit - totalCredit))}
                </div>
            )}
        </div>
    );
};

export default TrialBalance;
