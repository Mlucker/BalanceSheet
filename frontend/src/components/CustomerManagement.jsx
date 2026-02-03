import { useEffect, useState } from 'react';
import api from '../api/api';

const CustomerManagement = () => {
    const [customers, setCustomers] = useState([]);
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');

    useEffect(() => {
        fetchCustomers();
    }, []);

    const fetchCustomers = () => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get('/customers', { headers: { 'X-Company-ID': companyId } })
            .then(res => setCustomers(res.data))
            .catch(err => console.error(err));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const companyId = localStorage.getItem('companyId') || 1;

        try {
            await api.post('/customers', { name, email }, { headers: { 'X-Company-ID': companyId } });
            alert('Customer created!');
            setName('');
            setEmail('');
            fetchCustomers();
        } catch (err) {
            console.error(err);
            alert('Failed to create customer');
        }
    };

    return (
        <div className="container">
            <h1>Customer Management</h1>
            <div className="dashboard-grid" style={{ gridTemplateColumns: '1fr 2fr' }}>
                <div className="glass-panel">
                    <h3>Add New Customer</h3>
                    <form onSubmit={handleSubmit}>
                        <div style={{ marginBottom: '1rem' }}>
                            <label className="stat-label">Name</label>
                            <input
                                value={name} onChange={e => setName(e.target.value)} required
                                style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                            />
                        </div>
                        <div style={{ marginBottom: '1rem' }}>
                            <label className="stat-label">Email</label>
                            <input
                                value={email} onChange={e => setEmail(e.target.value)}
                                style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                            />
                        </div>
                        <button className="btn btn-primary" type="submit">Create Customer</button>
                    </form>
                </div>

                <div className="glass-panel">
                    <h3>Customer List</h3>
                    {customers.map(c => (
                        <div key={c.id} style={{ padding: '0.5rem', borderBottom: '1px solid var(--glass-border)', display: 'flex', justifyContent: 'space-between' }}>
                            <span style={{ fontWeight: 'bold' }}>{c.name}</span>
                            <span style={{ color: 'var(--text-secondary)' }}>{c.email}</span>
                        </div>
                    ))}
                    {customers.length === 0 && <p style={{ color: 'var(--text-secondary)' }}>No customers found.</p>}
                </div>
            </div>
        </div>
    );
};

export default CustomerManagement;
