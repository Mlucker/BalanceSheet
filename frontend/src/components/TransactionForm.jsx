import { useEffect, useState } from 'react';
import api from '../api/api';

const TransactionForm = () => {
    const [accounts, setAccounts] = useState([]);
    const [description, setDescription] = useState('');
    const [date, setDate] = useState(new Date().toISOString().split('T')[0]); // Default to today
    const [currency, setCurrency] = useState('USD');
    const [entries, setEntries] = useState([
        { accountId: '', debit: '', credit: '' },
        { accountId: '', debit: '', credit: '' }
    ]);

    useEffect(() => {
        api.get('/accounts').then(res => setAccounts(res.data));
        // Fetch company default currency
        const companyId = localStorage.getItem('companyId') || 1;
        api.get(`/companies/${companyId}`)
            .then(res => setCurrency(res.data.currency))
            .catch(err => console.error(err));
    }, []);

    const handleEntryChange = (index, field, value) => {
        const newEntries = [...entries];
        newEntries[index][field] = value;
        // If users types in debit, clear credit and vice versa
        if (field === 'debit' && value) newEntries[index].credit = '';
        if (field === 'credit' && value) newEntries[index].debit = '';
        setEntries(newEntries);
    };

    const addEntry = () => {
        setEntries([...entries, { accountId: '', debit: '', credit: '' }]);
    };

    const removeEntry = (index) => {
        setEntries(entries.filter((_, i) => i !== index));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Convert to signed amounts
        const processedEntries = entries.map(e => {
            let amount = 0;
            if (e.debit) amount = parseFloat(e.debit);
            if (e.credit) amount = -parseFloat(e.credit);
            return {
                accountId: e.accountId,
                amount: amount
            };
        });

        const payload = {
            description,
            date: date ? new Date(date).toISOString() : null,
            currency,
            entries: processedEntries
        };

        try {
            await api.post('/transactions', payload);
            setDescription('');
            setDate(new Date().toISOString().split('T')[0]);
            setEntries([
                { accountId: '', debit: '', credit: '' },
                { accountId: '', debit: '', credit: '' }
            ]);
            alert('Transaction recorded successfully!');
        } catch (err) {
            console.error(err); // Check response for message
            if (err.response && err.response.data && err.response.data.message) {
                alert('Error: ' + err.response.data.message);
            } else {
                alert('Error recording transaction. Ensure Debits = Credits.');
            }
        }
    };

    return (
        <div className="container">
            <div className="glass-panel">
                <h2>Record Business Incident</h2>
                <form onSubmit={handleSubmit}>
                    <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr', gap: '1rem', marginBottom: '1.5rem' }}>
                        <div>
                            <label className="stat-label">Description</label>
                            <input
                                value={description}
                                onChange={e => setDescription(e.target.value)}
                                placeholder="e.g. Sale of Goods to Client X"
                                required
                            />
                        </div>
                        <div>
                            <label className="stat-label">Date</label>
                            <input
                                type="date"
                                value={date}
                                onChange={e => setDate(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <label className="stat-label">Currency</label>
                            <select
                                value={currency}
                                onChange={e => setCurrency(e.target.value)}
                                style={{ padding: '0.8rem', width: '100%', borderRadius: '8px', background: 'var(--bg-secondary)', color: 'var(--text-primary)', border: '1px solid var(--glass-border)' }}
                            >
                                <option value="USD">USD ($)</option>
                                <option value="EUR">EUR (€)</option>
                            </select>
                        </div>
                    </div>

                    <h3>Journal Entries</h3>
                    {entries.map((entry, index) => (
                        <div key={index} style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr auto', gap: '1rem', marginBottom: '1rem', alignItems: 'end' }}>
                            <div>
                                <label className="stat-label">Account</label>
                                <select
                                    value={entry.accountId}
                                    onChange={e => handleEntryChange(index, 'accountId', e.target.value)}
                                    required
                                >
                                    <option value="">Select Account</option>
                                    {accounts.map(acc => (
                                        <option key={acc.id} value={acc.id}>{acc.name} ({acc.type})</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <label className="stat-label">Debit</label>
                                <input
                                    type="number"
                                    step="0.01"
                                    value={entry.debit}
                                    onChange={e => handleEntryChange(index, 'debit', e.target.value)}
                                    placeholder="0.00"
                                    disabled={!!entry.credit}
                                />
                            </div>
                            <div>
                                <label className="stat-label">Credit</label>
                                <input
                                    type="number"
                                    step="0.01"
                                    value={entry.credit}
                                    onChange={e => handleEntryChange(index, 'credit', e.target.value)}
                                    placeholder="0.00"
                                    disabled={!!entry.debit}
                                />
                            </div>
                            <button type="button" className="btn" style={{ background: 'var(--danger)', color: 'white', height: 'fit-content' }} onClick={() => removeEntry(index)}>X</button>
                        </div>
                    ))}

                    <button type="button" className="btn" style={{ background: 'var(--bg-secondary)', border: '1px solid var(--accent-primary)', color: 'var(--accent-primary)', marginRight: '1rem' }} onClick={addEntry}>+ Add Entry</button>
                    <button type="submit" className="btn btn-primary">Post Transaction</button>
                </form>
            </div>
        </div>
    );
};

export default TransactionForm;
