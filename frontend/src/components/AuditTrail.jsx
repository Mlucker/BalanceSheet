import { useEffect, useState } from 'react';
import api from '../api/api';
import { formatCurrency } from '../utils/currency';

const AuditTrail = () => {
    const [transactions, setTransactions] = useState([]);

    useEffect(() => {
        api.get('/transactions').then(res => setTransactions(res.data));
    }, []);

    return (
        <div className="container">
            <div className="glass-panel">
                <h2>Audit Trail</h2>
                {transactions.length === 0 && <p style={{ color: 'var(--text-secondary)' }}>No transactions recorded.</p>}
                {transactions.map(tx => (
                    <div key={tx.id} style={{ marginBottom: '1.5rem', borderBottom: '1px solid var(--glass-border)', paddingBottom: '1rem' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                            <span style={{ fontWeight: 'bold', fontSize: '1.1rem' }}>{tx.description}</span>
                            <span style={{ color: 'var(--text-secondary)' }}>{new Date(tx.date).toLocaleString()}</span>
                        </div>
                        <div style={{ background: 'rgba(0,0,0,0.2)', padding: '0.5rem', borderRadius: '8px' }}>
                            {tx.entries.map(entry => (
                                <div key={entry.id} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem', padding: '0.25rem 0' }}>
                                    <span>{entry.account ? entry.account.name : 'Unknown Account'}</span>
                                    <span style={{
                                        color: entry.amount > 0 ? '#ef4444' : '#10b981', // Red for debit, green for credit
                                        fontWeight: '600',
                                        padding: '0.25rem 0.75rem',
                                        borderRadius: '4px',
                                        background: entry.amount > 0 ? 'rgba(239, 68, 68, 0.1)' : 'rgba(16, 185, 129, 0.1)'
                                    }}>
                                        {entry.amount > 0
                                            ? `Dr ${formatCurrency(entry.amount)}`
                                            : `Cr ${formatCurrency(Math.abs(entry.amount))}`}
                                    </span>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default AuditTrail;
