import { useEffect, useState } from 'react';
import api from '../api/api';

const ProductManagement = () => {
    const [products, setProducts] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [refresh, setRefresh] = useState(0);

    // Form State
    const [name, setName] = useState('');
    const [sku, setSku] = useState('');
    const [description, setDescription] = useState('');
    const [sellingPrice, setSellingPrice] = useState('');
    const [costPrice, setCostPrice] = useState('');
    const [quantityOnHand, setQuantityOnHand] = useState('');
    const [currency, setCurrency] = useState('USD');

    useEffect(() => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get(`/companies/${companyId}`).then(res => setCurrency(res.data.currency));
        api.get('/products', { headers: { 'X-Company-ID': companyId } })
            .then(res => setProducts(res.data))
            .catch(err => console.error(err));
    }, [refresh]);

    const formatCurrency = (val) => new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(val || 0);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const companyId = localStorage.getItem('companyId') || 1;
        const payload = {
            name,
            sku,
            description,
            sellingPrice: parseFloat(sellingPrice),
            costPrice: costPrice ? parseFloat(costPrice) : null,
            quantityOnHand: parseInt(quantityOnHand || 0)
        };

        try {
            await api.post('/products', payload, { headers: { 'X-Company-ID': companyId } });
            setRefresh(refresh + 1);
            setIsModalOpen(false);
            resetForm();
        } catch (err) {
            console.error(err);
            alert('Failed to save product');
        }
    };

    const resetForm = () => {
        setName('');
        setSku('');
        setDescription('');
        setSellingPrice('');
        setCostPrice('');
        setQuantityOnHand('');
    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <h1>Products / Inventory</h1>
                <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>+ New Product</button>
            </div>

            <div className="glass-panel">
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                        <tr style={{ borderBottom: '1px solid var(--glass-border)' }}>
                            <th style={{ textAlign: 'left', padding: '1rem' }}>Name</th>
                            <th style={{ textAlign: 'left', padding: '1rem' }}>SKU</th>
                            <th style={{ textAlign: 'right', padding: '1rem' }}>Price</th>
                            <th style={{ textAlign: 'right', padding: '1rem' }}>Stock</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map(p => (
                            <tr key={p.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                                <td style={{ padding: '0.75rem 1rem' }}>
                                    <div style={{ fontWeight: 'bold' }}>{p.name}</div>
                                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{p.description}</div>
                                </td>
                                <td style={{ padding: '0.75rem 1rem' }}>{p.sku}</td>
                                <td style={{ padding: '0.75rem 1rem', textAlign: 'right' }}>{formatCurrency(p.sellingPrice)}</td>
                                <td style={{ padding: '0.75rem 1rem', textAlign: 'right', color: p.quantityOnHand < 10 ? 'var(--danger)' : 'inherit' }}>
                                    {p.quantityOnHand}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>New Product</h2>
                        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <input value={name} onChange={e => setName(e.target.value)} placeholder="Product Name" required style={{ padding: '0.8rem', borderRadius: '4px' }} />
                            <input value={sku} onChange={e => setSku(e.target.value)} placeholder="SKU (e.g. WIDGET-001)" style={{ padding: '0.8rem', borderRadius: '4px' }} />
                            <input value={description} onChange={e => setDescription(e.target.value)} placeholder="Description" style={{ padding: '0.8rem', borderRadius: '4px' }} />
                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                                <input type="number" step="0.01" value={sellingPrice} onChange={e => setSellingPrice(e.target.value)} placeholder="Selling Price" required style={{ padding: '0.8rem', borderRadius: '4px' }} />
                                <input type="number" step="0.01" value={costPrice} onChange={e => setCostPrice(e.target.value)} placeholder="Cost Price (Optional)" style={{ padding: '0.8rem', borderRadius: '4px' }} />
                            </div>
                            <input type="number" value={quantityOnHand} onChange={e => setQuantityOnHand(e.target.value)} placeholder="Initial Stock Quantity" required style={{ padding: '0.8rem', borderRadius: '4px' }} />

                            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '1rem' }}>
                                <button type="button" className="btn" style={{ background: 'transparent' }} onClick={() => setIsModalOpen(false)}>Cancel</button>
                                <button type="submit" className="btn btn-primary">Save Product</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ProductManagement;
