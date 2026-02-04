import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/api';

const InvoiceList = () => {
    const [invoices, setInvoices] = useState([]);
    const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false);
    const [selectedInvoice, setSelectedInvoice] = useState(null);
    const [cashAccounts, setCashAccounts] = useState([]);
    const [refresh, setRefresh] = useState(0);

    // Payment Form State
    const [paymentAmount, setPaymentAmount] = useState('');
    const [paymentDate, setPaymentDate] = useState(new Date().toISOString().split('T')[0]);
    const [paymentMethod, setPaymentMethod] = useState('Cash');
    const [cashAccountId, setCashAccountId] = useState('');
    const [reference, setReference] = useState('');

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get('/invoices', { headers: { 'X-Company-ID': companyId } })
            .then(res => setInvoices(res.data))
            .catch(err => console.error(err));

        // Fetch Cash/Bank accounts
        api.get('/accounts', { headers: { 'X-Company-ID': companyId } })
            .then(res => {
                const cashAccts = res.data.filter(a => a.type === 'ASSET');
                setCashAccounts(cashAccts);
            });
    }, [refresh]);

    const getStatusColor = (status) => {
        switch (status) {
            case 'DRAFT': return 'var(--text-secondary)';
            case 'POSTED': return 'var(--accent-primary)';
            case 'PAID': return 'var(--success)';
            default: return 'var(--text-primary)';
        }
    };

    const openPaymentModal = (invoice) => {
        setSelectedInvoice(invoice);
        setPaymentAmount(invoice.totalAmount);
        setIsPaymentModalOpen(true);
    };

    const handleRecordPayment = async (e) => {
        e.preventDefault();
        const companyId = localStorage.getItem('companyId') || 1;

        const payload = {
            invoiceId: selectedInvoice.id,
            amount: parseFloat(paymentAmount),
            cashAccountId: parseInt(cashAccountId),
            paymentDate,
            paymentMethod,
            reference
        };

        try {
            await api.post('/payments', payload, { headers: { 'X-Company-ID': companyId } });
            alert('Payment recorded successfully!');
            setIsPaymentModalOpen(false);
            setRefresh(refresh + 1);
            resetPaymentForm();
        } catch (err) {
            console.error(err);
            alert('Failed to record payment');
        }
    };

    const resetPaymentForm = () => {
        setPaymentAmount('');
        setPaymentDate(new Date().toISOString().split('T')[0]);
        setPaymentMethod('Cash');
        setCashAccountId('');
        setReference('');
        setSelectedInvoice(null);
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
                                    {inv.status === 'POSTED' && (
                                        <button
                                            className="btn"
                                            style={{ background: 'var(--success)', color: 'white', padding: '0.5rem 1rem', fontSize: '0.8rem' }}
                                            onClick={() => openPaymentModal(inv)}
                                        >
                                            Record Payment
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                {invoices.length === 0 && <p style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-secondary)' }}>No invoices found.</p>}
            </div>

            {isPaymentModalOpen && selectedInvoice && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Record Payment - {selectedInvoice.invoiceNumber}</h2>
                        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
                            Customer: {selectedInvoice.customer.name} | Total: {new Intl.NumberFormat('en-US', { style: 'currency', currency: selectedInvoice.currency }).format(selectedInvoice.totalAmount)}
                        </p>
                        <form onSubmit={handleRecordPayment} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div>
                                <label className="stat-label">Payment Amount</label>
                                <input
                                    type="number"
                                    step="0.01"
                                    value={paymentAmount}
                                    onChange={e => setPaymentAmount(e.target.value)}
                                    required
                                    style={{ width: '100%', padding: '0.8rem', borderRadius: '4px' }}
                                />
                            </div>
                            <div>
                                <label className="stat-label">Payment Date</label>
                                <input
                                    type="date"
                                    value={paymentDate}
                                    onChange={e => setPaymentDate(e.target.value)}
                                    required
                                    style={{ width: '100%', padding: '0.8rem', borderRadius: '4px' }}
                                />
                            </div>
                            <div>
                                <label className="stat-label">Payment Method</label>
                                <select
                                    value={paymentMethod}
                                    onChange={e => setPaymentMethod(e.target.value)}
                                    style={{ width: '100%', padding: '0.8rem', borderRadius: '4px' }}
                                >
                                    <option value="Cash">Cash</option>
                                    <option value="Check">Check</option>
                                    <option value="Bank Transfer">Bank Transfer</option>
                                    <option value="Credit Card">Credit Card</option>
                                </select>
                            </div>
                            <div>
                                <label className="stat-label">Deposit to Account</label>
                                <select
                                    value={cashAccountId}
                                    onChange={e => setCashAccountId(e.target.value)}
                                    required
                                    style={{ width: '100%', padding: '0.8rem', borderRadius: '4px' }}
                                >
                                    <option value="">Select Cash/Bank Account</option>
                                    {cashAccounts.map(a => <option key={a.id} value={a.id}>{a.name}</option>)}
                                </select>
                            </div>
                            <div>
                                <label className="stat-label">Reference (Optional)</label>
                                <input
                                    value={reference}
                                    onChange={e => setReference(e.target.value)}
                                    placeholder="Check #, Transfer ID, etc."
                                    style={{ width: '100%', padding: '0.8rem', borderRadius: '4px' }}
                                />
                            </div>

                            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '1rem' }}>
                                <button
                                    type="button"
                                    className="btn"
                                    style={{ background: 'transparent' }}
                                    onClick={() => { setIsPaymentModalOpen(false); resetPaymentForm(); }}
                                >
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary">Record Payment</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default InvoiceList;
