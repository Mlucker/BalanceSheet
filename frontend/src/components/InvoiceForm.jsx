import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/api';

const InvoiceForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;

    const [customers, setCustomers] = useState([]);
    const [revenueAccounts, setRevenueAccounts] = useState([]);
    const [arAccounts, setArAccounts] = useState([]);

    // Form State
    const [customerId, setCustomerId] = useState('');
    const [invoiceNumber, setInvoiceNumber] = useState('');
    const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
    const [dueDate, setDueDate] = useState(new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]);
    const [items, setItems] = useState([
        { description: 'Consulting Services', quantity: 1, unitPrice: 0, revenueAccountId: '', productId: '' }
    ]);
    const [selectedArAccount, setSelectedArAccount] = useState('');
    const [status, setStatus] = useState('DRAFT');
    const [products, setProducts] = useState([]);

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;

        // Fetch Customers
        api.get('/customers', { headers: { 'X-Company-ID': companyId } })
            .then(res => setCustomers(res.data));

        // Fetch Products
        api.get('/products', { headers: { 'X-Company-ID': companyId } })
            .then(res => setProducts(res.data));

        // Fetch Accounts
        api.get('/accounts', { headers: { 'X-Company-ID': companyId } })
            .then(res => {
                const accts = res.data;
                // Filter Revenue & AR Accounts
                setRevenueAccounts(accts.filter(a => a.type === 'REVENUE'));

                // For AR, typically it's an ASSET. Ideally we'd have a subtype, but for now filtering by name or type is okay.
                // Assuming user has an account named "Accounts Receivable" or similar ASSET.
                setArAccounts(accts.filter(a => a.type === 'ASSET'));
            });

    }, []);

    const handleItemChange = (index, field, value) => {
        const newItems = [...items];
        newItems[index][field] = value;

        // Auto-fill details if product selected
        if (field === 'productId') {
            const product = products.find(p => p.id == value);
            if (product) {
                newItems[index].description = product.name;
                newItems[index].unitPrice = product.sellingPrice;
                // Note: Revenue Account still needs to be selected manually or defaulted in product in future features
            }
        }

        setItems(newItems);
    };

    const addItem = () => {
        setItems([...items, { description: '', quantity: 1, unitPrice: 0, revenueAccountId: '', productId: '' }]);
    };

    const removeItem = (index) => {
        setItems(items.filter((_, i) => i !== index));
    };

    const calculateTotal = () => {
        return items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
    };

    const handleSaveDraft = async () => {
        const companyId = localStorage.getItem('companyId') || 1;
        const payload = {
            customer: { id: parseInt(customerId) },
            date,
            dueDate,
            invoiceNumber,
            currency: 'USD', // Default for now
            items: items.map(item => ({
                description: item.description,
                quantity: parseFloat(item.quantity),
                unitPrice: parseFloat(item.unitPrice),
                revenueAccount: { id: parseInt(item.revenueAccountId) },
                product: item.productId ? { id: parseInt(item.productId) } : null
            }))
        };

        try {
            const res = await api.post('/invoices', payload, { headers: { 'X-Company-ID': companyId } });
            alert('Draft Saved!');
            if (!isEdit) {
                navigate(`/invoices/${res.data.id}/edit`);
            }
        } catch (err) {
            console.error(err);
            alert('Failed to save draft');
        }
    };

    const handleApprove = async () => {
        const companyId = localStorage.getItem('companyId') || 1;
        if (!selectedArAccount) {
            alert('Please select an Accounts Receivable account to post to.');
            return;
        }

        // Ensure user saves changes if any (basic check, usually we'd save first automatically)
        // For simplicity, assuming user saved draft or we just proceed with approve call on existing ID.
        if (!isEdit) {
            alert("Please save draft first.");
            return;
        }

        try {
            await api.post(`/invoices/${id}/approve`, { arAccountId: parseInt(selectedArAccount) }, { headers: { 'X-Company-ID': companyId } });
            alert('Invoice Approved & Posted!');
            navigate('/invoices');
        } catch (err) {
            console.error(err);
            alert('Failed to approve invoice');
        }
    };

    return (
        <div className="container">
            <div className="glass-panel">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                    <h2>{isEdit ? 'Edit Invoice' : 'New Invoice'} <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>({status})</span></h2>
                    <div>
                        <button className="btn" style={{ marginRight: '1rem', background: 'transparent', border: '1px solid var(--text-secondary)', color: 'var(--text-secondary)' }} onClick={handleSaveDraft}>Save Draft</button>
                        {isEdit && (
                            <button className="btn btn-primary" onClick={handleApprove}>Approve & Post</button>
                        )}
                    </div>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', marginBottom: '2rem' }}>
                    <div>
                        <label className="stat-label">Customer</label>
                        <select
                            value={customerId} onChange={e => setCustomerId(e.target.value)} required
                            style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                        >
                            <option value="">Select Customer</option>
                            {customers.map(c => (
                                <option key={c.id} value={c.id}>{c.name}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="stat-label">Invoice # (Optional)</label>
                        <input
                            value={invoiceNumber} onChange={e => setInvoiceNumber(e.target.value)} placeholder="Auto-generated if empty"
                            style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                        />
                    </div>
                    <div>
                        <label className="stat-label">Date</label>
                        <input type="date" value={date} onChange={e => setDate(e.target.value)}
                            style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                        />
                    </div>
                    <div>
                        <label className="stat-label">Due Date</label>
                        <input type="date" value={dueDate} onChange={e => setDueDate(e.target.value)}
                            style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                        />
                    </div>
                </div>

                <h3>Line Items</h3>
                <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1rem', borderRadius: '8px', marginBottom: '2rem' }}>
                    {items.map((item, index) => (
                        <div key={index} style={{ display: 'grid', gridTemplateColumns: '2fr 3fr 1fr 1fr 2fr auto', gap: '1rem', marginBottom: '1rem', alignItems: 'end' }}>
                            <div>
                                <label className="stat-label">Product</label>
                                <select value={item.productId} onChange={e => handleItemChange(index, 'productId', e.target.value)} style={{ width: '100%', padding: '0.5rem', borderRadius: '4px' }}>
                                    <option value="">-- Custom --</option>
                                    {products.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                                </select>
                            </div>
                            <div>
                                <label className="stat-label">Description</label>
                                <input value={item.description} onChange={e => handleItemChange(index, 'description', e.target.value)} style={{ width: '100%', padding: '0.5rem', borderRadius: '4px' }} />
                            </div>
                            <div>
                                <label className="stat-label">Qty</label>
                                <input type="number" value={item.quantity} onChange={e => handleItemChange(index, 'quantity', e.target.value)} style={{ width: '100%', padding: '0.5rem', borderRadius: '4px' }} />
                            </div>
                            <div>
                                <label className="stat-label">UnitPrice</label>
                                <input type="number" step="0.01" value={item.unitPrice} onChange={e => handleItemChange(index, 'unitPrice', e.target.value)} style={{ width: '100%', padding: '0.5rem', borderRadius: '4px' }} />
                            </div>
                            <div>
                                <label className="stat-label">Revenue Account</label>
                                <select value={item.revenueAccountId} onChange={e => handleItemChange(index, 'revenueAccountId', e.target.value)} style={{ width: '100%', padding: '0.5rem', borderRadius: '4px' }}>
                                    <option value="">Select Account</option>
                                    {revenueAccounts.map(a => <option key={a.id} value={a.id}>{a.name}</option>)}
                                </select>
                            </div>
                            <button className="btn" style={{ background: 'var(--danger)', color: 'white', padding: '0.5rem' }} onClick={() => removeItem(index)}>X</button>
                        </div>
                    ))}
                    <button className="btn" style={{ background: 'var(--bg-secondary)', color: 'var(--accent-primary)', border: '1px solid var(--accent-primary)' }} onClick={addItem}>+ Add Item</button>
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: '2rem' }}>
                    {isEdit && (
                        <div>
                            <label className="stat-label" style={{ marginRight: '0.5rem' }}>Post to A/R Account:</label>
                            <select value={selectedArAccount} onChange={e => setSelectedArAccount(e.target.value)} style={{ padding: '0.5rem', borderRadius: '4px' }}>
                                <option value="">Select AR Account</option>
                                {arAccounts.map(a => <option key={a.id} value={a.id}>{a.name}</option>)}
                            </select>
                        </div>
                    )}
                    <div style={{ textAlign: 'right', marginTop: '2rem', padding: '1rem', background: 'rgba(56, 189, 248, 0.1)', borderRadius: '8px' }}>
                        <h2 style={{ margin: 0 }}>Total: ${calculateTotal().toFixed(2)}</h2>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default InvoiceForm;
