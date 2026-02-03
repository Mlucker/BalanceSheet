import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/api';

const InvoiceList = () => {
    const [invoices, setInvoices] = useState([]);

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get('/invoices', { headers: { 'X-Company-ID': companyId } })
            .then(res => setInvoices(res.data))
            .catch(err => console.error(err));
    }, []);

    const getStatusColor = (status) => {
        switch (status) {
            case 'DRAFT': return 'var(--text-secondary)';
            case 'POSTED': return 'var(--accent-primary)';
            case 'PAID': return 'var(--success)';
            default: return 'var(--text-primary)';
        }
    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <h1 style={{ margin: 0 }}>Invoices</h1>
                <Link to="/invoices/new" className="btn btn-primary">+ New Invoice</Link>
            </div>

            <div className="glass-panel">
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ borderBottom: '1px solid var(--glass-border)', textAlign: 'left' }}>
                            <th style={{ padding: '1rem' }}>Invoice #</th>
                            <th style={{ padding: '1rem' }}>Customer</th>
                            <th style={{ padding: '1rem' }}>Date</th>
                            <th style={{ padding: '1rem' }}>Due Date</th>
                            <th style={{ padding: '1rem' }}>Amount</th>
                            <th style={{ padding: '1rem' }}>Status</th>
                            <th style={{ padding: '1rem' }}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {invoices.map(inv => (
                            <tr key={inv.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                                <td style={{ padding: '1rem' }}>{inv.invoiceNumber}</td>
                                <td style={{ padding: '1rem' }}>{inv.customer.name}</td>
                                <td style={{ padding: '1rem', color: 'var(--text-secondary)' }}>{inv.date}</td>
                                <td style={{ padding: '1rem', color: 'var(--text-secondary)' }}>{inv.dueDate}</td>
                                <td style={{ padding: '1rem', fontWeight: 'bold' }}>
                                    {new Intl.NumberFormat('en-US', { style: 'currency', currency: inv.currency }).format(inv.totalAmount)}
                                </td>
                                <td style={{ padding: '1rem' }}>
                                    <span style={{
                                        padding: '0.25rem 0.5rem',
                                        borderRadius: '4px',
                                        background: 'rgba(255,255,255,0.1)',
                                        color: getStatusColor(inv.status),
                                        fontSize: '0.8rem',
                                        fontWeight: 'bold'
                                    }}>
                                        {inv.status}
                                    </span>
                                </td>
                                <td style={{ padding: '1rem' }}>
                                    {inv.status === 'DRAFT' && (
                                        <Link to={`/invoices/${inv.id}/edit`} style={{ color: 'var(--accent-primary)', textDecoration: 'none', marginRight: '1rem' }}>Edit/Post</Link>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                {invoices.length === 0 && <p style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-secondary)' }}>No invoices found.</p>}
            </div>
        </div>
    );
};

export default InvoiceList;
