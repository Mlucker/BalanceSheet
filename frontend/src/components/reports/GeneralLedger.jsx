import { useEffect, useState } from 'react';
import api from '../../api/api';

const GeneralLedger = () => {
    const [accounts, setAccounts] = useState([]);
    const [selectedAccount, setSelectedAccount] = useState('');
    const [year, setYear] = useState(new Date().getFullYear());
    const [entries, setEntries] = useState([]);
    const [currency, setCurrency] = useState('USD');

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get(`/companies/${companyId}`).then(res => setCurrency(res.data.currency));
        api.get('/accounts', { headers: { 'X-Company-ID': companyId } })
            .then(res => setAccounts(res.data));
    }, []);

    useEffect(() => {
        if (!selectedAccount) return;

        const companyId = localStorage.getItem('companyId') || 1;
        api.get(`/reports/general-ledger/${selectedAccount}?year=${year}`, { headers: { 'X-Company-ID': companyId } })
            .then(res => setEntries(res.data))
            .catch(err => console.error(err));
    }, [selectedAccount, year]);

    return (
        <div className="glass-panel">
            <h2>General Ledger</h2>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '2rem' }}>
                <div>
                    <label className="stat-label">Select Account</label>
                    <select
                        value={selectedAccount} onChange={e => setSelectedAccount(e.target.value)}
                        style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                    >
                        <option value="">-- Choose Account --</option>
                        {accounts.map(acc => (
                            <option key={acc.id} value={acc.id}>{acc.name} ({acc.type})</option>
                        ))}
                    </select>
                </div>
                <div>
                    <label className="stat-label">Year</label>
                    <select
                        value={year} onChange={e => setYear(e.target.value)}
                        style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                    >
                        <option value="2023">2023</option>
                        <option value="2024">2024</option>
                        <option value="2025">2025</option>
                    </select>
                </div>
            </div>

            {selectedAccount && (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ borderBottom: '1px solid var(--glass-border)' }}>
                            <th style={{ textAlign: 'left', padding: '1rem' }}>Date</th>
                            <th style={{ textAlign: 'left', padding: '1rem' }}>Description</th>
                            <th style={{ textAlign: 'right', padding: '1rem' }}>Debit</th>
                            <th style={{ textAlign: 'right', padding: '1rem' }}>Credit</th>
                        </tr>
                    </thead>
                    <tbody>
                        {entries.length === 0 && (
                            <tr><td colSpan={4} style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-secondary)' }}>No transactions found for this period.</td></tr>
                        )}
                        {entries.map(entry => {
                            const isDebit = entry.amount > 0;
                            return (
                                <tr key={entry.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                                    <td style={{ padding: '0.75rem 1rem', color: 'var(--text-secondary)' }}>
                                        {entry.transaction ? new Date(entry.transaction.date).toLocaleDateString() : 'N/A'}
                                    </td>
                                    <td style={{ padding: '0.75rem 1rem' }}>
                                        {entry.transaction ? entry.transaction.description : 'N/A'}
                                    </td>
                                    <td style={{
                                        padding: '0.75rem 1rem',
                                        textAlign: 'right',
                                        color: isDebit ? '#ef4444' : 'var(--text-secondary)',
                                        fontWeight: isDebit ? '600' : 'normal'
                                    }}>
                                        {isDebit ? new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(entry.amount) : '-'}
                                    </td>
                                    <td style={{
                                        padding: '0.75rem 1rem',
                                        textAlign: 'right',
                                        color: !isDebit ? '#10b981' : 'var(--text-secondary)',
                                        fontWeight: !isDebit ? '600' : 'normal'
                                    }}>
                                        {!isDebit ? new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(Math.abs(entry.amount)) : '-'}
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default GeneralLedger;
