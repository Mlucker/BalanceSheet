import { useEffect, useState } from 'react';
import api from '../api/api';

const TemplateManagement = () => {
    const [templates, setTemplates] = useState([]);
    const [accounts, setAccounts] = useState([]);

    // Form State
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [entries, setEntries] = useState([
        { accountId: '', description: '', type: 'DEBIT' },
        { accountId: '', description: '', type: 'CREDIT' }
    ]);
    const [showForm, setShowForm] = useState(false);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = () => {
        const companyId = localStorage.getItem('companyId') || 1;
        api.get('/templates', { headers: { 'X-Company-ID': companyId } })
            .then(res => setTemplates(res.data))
            .catch(err => console.error("Failed to fetch templates", err));

        api.get('/accounts', { headers: { 'X-Company-ID': companyId } })
            .then(res => setAccounts(res.data))
            .catch(err => console.error("Failed to fetch accounts", err));
    };

    const handleEntryChange = (index, field, value) => {
        const newEntries = [...entries];
        newEntries[index][field] = value;
        setEntries(newEntries);
    };

    const addEntry = () => {
        setEntries([...entries, { accountId: '', description: '', type: 'DEBIT' }]);
    };

    const removeEntry = (index) => {
        setEntries(entries.filter((_, i) => i !== index));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const companyId = localStorage.getItem('companyId') || 1;

        const payload = {
            name,
            description,
            entries: entries.map(e => ({
                account: { id: parseInt(e.accountId) },
                description: e.description,
                type: e.type
            }))
        };

        try {
            await api.post('/templates', payload, { headers: { 'X-Company-ID': companyId } });
            alert('Template created successfully!');
            setName('');
            setDescription('');
            setEntries([
                { accountId: '', description: '', type: 'DEBIT' },
                { accountId: '', description: '', type: 'CREDIT' }
            ]);
            setShowForm(false);
            fetchData(); // Refresh list
        } catch (err) {
            console.error(err);
            alert('Failed to create template');
        }
    };

    const handleDelete = async (id) => {
        if (!confirm('Are you sure you want to delete this template?')) return;
        const companyId = localStorage.getItem('companyId') || 1;
        try {
            await api.delete(`/templates/${id}`, { headers: { 'X-Company-ID': companyId } });
            fetchData();
        } catch (err) {
            console.error(err);
            alert('Failed to delete template');
        }
    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <h1 style={{ margin: 0 }}>Business Event Templates</h1>
                <button
                    className="btn btn-primary"
                    onClick={() => setShowForm(!showForm)}
                >
                    {showForm ? 'Cancel' : '+ New Template'}
                </button>
            </div>

            {showForm && (
                <div className="glass-panel" style={{ marginBottom: '2rem' }}>
                    <h3>Create New Template</h3>
                    <form onSubmit={handleSubmit}>
                        <div style={{ marginBottom: '1rem' }}>
                            <label className="stat-label">Template Name</label>
                            <input
                                value={name}
                                onChange={e => setName(e.target.value)}
                                placeholder="e.g. Monthly Rent, Client Lunch"
                                required
                                style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                            />
                        </div>
                        <div style={{ marginBottom: '1rem' }}>
                            <label className="stat-label">Description</label>
                            <input
                                value={description}
                                onChange={e => setDescription(e.target.value)}
                                placeholder="Describe this event type..."
                                style={{ width: '100%', padding: '0.8rem', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                            />
                        </div>

                        <h4 style={{ marginTop: '1.5rem' }}>Default Entries</h4>
                        <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1rem', borderRadius: '8px' }}>
                            {entries.map((entry, index) => (
                                <div key={index} style={{ display: 'grid', gridTemplateColumns: '2fr 2fr 1fr auto', gap: '1rem', marginBottom: '1rem', alignItems: 'end' }}>
                                    <div>
                                        <label className="stat-label">Account</label>
                                        <select
                                            value={entry.accountId}
                                            onChange={e => handleEntryChange(index, 'accountId', e.target.value)}
                                            required
                                            style={{ width: '100%', padding: '0.6rem', borderRadius: '6px' }}
                                        >
                                            <option value="">Select Account</option>
                                            {accounts.map(acc => (
                                                <option key={acc.id} value={acc.id}>{acc.name} ({acc.type})</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div>
                                        <label className="stat-label">Default Line Description</label>
                                        <input
                                            value={entry.description}
                                            onChange={e => handleEntryChange(index, 'description', e.target.value)}
                                            placeholder="Optional override..."
                                            style={{ width: '100%', padding: '0.6rem', borderRadius: '6px' }}
                                        />
                                    </div>
                                    <div>
                                        <label className="stat-label">Type Hint</label>
                                        <select
                                            value={entry.type}
                                            onChange={e => handleEntryChange(index, 'type', e.target.value)}
                                            style={{ width: '100%', padding: '0.6rem', borderRadius: '6px' }}
                                        >
                                            <option value="DEBIT">Debit</option>
                                            <option value="CREDIT">Credit</option>
                                        </select>
                                    </div>
                                    <button type="button" className="btn" style={{ background: 'var(--danger)', color: 'white', height: 'fit-content' }} onClick={() => removeEntry(index)}>X</button>
                                </div>
                            ))}
                            <button type="button" className="btn" style={{ background: 'var(--bg-secondary)', border: '1px solid var(--accent-primary)', color: 'var(--accent-primary)' }} onClick={addEntry}>+ Add Line Item</button>
                        </div>

                        <div style={{ marginTop: '2rem', display: 'flex', justifyContent: 'flex-end' }}>
                            <button type="submit" className="btn btn-primary">Save Template</button>
                        </div>
                    </form>
                </div>
            )}

            <div className="dashboard-grid">
                {templates.map(template => (
                    <div key={template.id} className="glass-panel" style={{ position: 'relative' }}>
                        <button
                            onClick={() => handleDelete(template.id)}
                            style={{ position: 'absolute', top: '1rem', right: '1rem', background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
                        >
                            🗑️
                        </button>
                        <h3>{template.name}</h3>
                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{template.description}</p>
                        <hr style={{ borderColor: 'var(--glass-border)', margin: '1rem 0' }} />
                        <div style={{ fontSize: '0.85rem' }}>
                            {template.entries.map((entry, idx) => (
                                <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                                    <span>{entry.account ? entry.account.name : 'Unknown'}</span>
                                    <span style={{
                                        color: entry.type === 'DEBIT' ? 'var(--text-primary)' : 'var(--success)',
                                        fontWeight: 'bold'
                                    }}>
                                        {entry.type}
                                    </span>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
                {templates.length === 0 && !showForm && (
                    <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                        <p>No templates defined yet. Define common business events to speed up data entry.</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default TemplateManagement;
