import { useEffect, useState } from 'react';
import api from '../api/api';

const Accounts = () => {
    const [accounts, setAccounts] = useState([]);
    const [newAccount, setNewAccount] = useState({ name: '', type: 'ASSET' });

    useEffect(() => {
        fetchAccounts();
    }, []);

    const fetchAccounts = () => {
        api.get('/accounts').then(res => setAccounts(res.data));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        api.post('/accounts', newAccount).then(() => {
            fetchAccounts();
            setNewAccount({ name: '', type: 'ASSET' });
        });
    };

    return (
        <div className="container">
            <div className="glass-panel">
                <h2>Chart of Accounts</h2>
                <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: '1rem', marginBottom: '2rem' }}>
                    <input
                        placeholder="Account Name"
                        value={newAccount.name}
                        onChange={e => setNewAccount({ ...newAccount, name: e.target.value })}
                        required
                    />
                    <select
                        value={newAccount.type}
                        onChange={e => setNewAccount({ ...newAccount, type: e.target.value })}
                    >
                        {['ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE'].map(t => (
                            <option key={t} value={t}>{t}</option>
                        ))}
                    </select>
                    <button type="submit" className="btn btn-primary">Add Account</button>
                </form>

                <div className="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Type</th>
                            </tr>
                        </thead>
                        <tbody>
                            {accounts.map(acc => (
                                <tr key={acc.id}>
                                    <td>{acc.name}</td>
                                    <td><span style={{
                                        padding: '0.25rem 0.5rem',
                                        borderRadius: '4px',
                                        background: 'rgba(255,255,255,0.1)',
                                        fontSize: '0.8rem'
                                    }}>{acc.type}</span></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default Accounts;
