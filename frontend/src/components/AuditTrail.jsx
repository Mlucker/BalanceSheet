import { useEffect, useState } from 'react';
import api from '../api/api';

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
                                        color: entry.amount > 0 ? 'var(--text-primary)' : 'var(--text-secondary)',
                                        background: entry.amount > 0 ? 'rgba(56, 189, 248, 0.1)' : 'transparent',
                                        padding: '0 0.5rem',
                                        borderRadius: '4px'
                                    }}>
                                        {entry.amount > 0 ? `Dr ${parseFloat(entry.amount).toFixed(2)}` : `Cr ${Math.abs(entry.amount).toFixed(2)}`}
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
