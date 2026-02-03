import { useEffect, useState } from 'react';
import api from '../api/api';

const Automation = () => {
    const [recurringItems, setRecurringItems] = useState([]);
    const [accounts, setAccounts] = useState([]);

    // Form State
    const [showWizard, setShowWizard] = useState(false);
    const [wizardType, setWizardType] = useState('TEAM'); // TEAM, BUILDING, MACHINE
    const [formData, setFormData] = useState({
        name: '',
        amount: '',
        dayOfMonth: 1,
        expenseAccountId: ''
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = () => {
        api.get('/recurring').then(res => setRecurringItems(res.data));
        api.get('/accounts').then(res => setAccounts(res.data));
    };

    const handleCreate = async (e) => {
        e.preventDefault();

        let debitAccount = null;
        let creditAccount = accounts.find(a => a.name.toLowerCase().includes('cash') || a.type === 'ASSET');

        if (wizardType === 'TEAM') {
            debitAccount = accounts.find(a => a.name === 'Salaries Expense');
        } else if (wizardType === 'BUILDING') {
            debitAccount = accounts.find(a => a.name === 'Rent Expense');
        } else {
            // MACHINE logic
            debitAccount = accounts.find(a => a.name === 'Maintenance Expense');
        }

        if (!debitAccount || !creditAccount) {
            alert('Please create necessary expense accounts (Salaries, Rent, Maintenance) and "Cash" first!');
            return;
        }

        const payload = {
            name: formData.name,
            description: wizardType === 'TEAM' ? `Monthly Salary for ${formData.name}` : (wizardType === 'BUILDING' ? `Monthly Rent for ${formData.name}` : `Maintenance for ${formData.name}`),
            amount: formData.amount,
            dayOfMonth: formData.dayOfMonth,
            category: wizardType,
            debitAccountId: debitAccount.id,
            creditAccountId: creditAccount.id,
            startDate: formData.startDate ? formData.startDate : null,
            endDate: formData.endDate ? formData.endDate : null
        };

        try {
            await api.post('/recurring', payload);
            setShowWizard(false);
            setFormData({ name: '', amount: '', dayOfMonth: 1 });
            fetchData();
        } catch (err) {
            console.error(err);
            alert('Failed to create automation.');
        }
    };

    const handleDelete = async (id) => {
        if (confirm('Stop this automation?')) {
            await api.delete(`/recurring/${id}`);
            fetchData();
        }
    };

    return (
        <div className="container">
            <div className="glass-panel">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                    <h2>Automation Center</h2>
                    <div style={{ display: 'flex', gap: '1rem' }}>
                        <button className="btn btn-primary" onClick={() => { setShowWizard(true); setWizardType('TEAM'); }}>
                            + Add Team Member
                        </button>
                        <button className="btn btn-primary" style={{ background: 'linear-gradient(135deg, #10b981, #059669)' }} onClick={() => { setShowWizard(true); setWizardType('BUILDING'); }}>
                            + Add Building
                        </button>
                        <button className="btn btn-primary" style={{ background: 'linear-gradient(135deg, #fbbf24, #d97706)' }} onClick={() => { setShowWizard(true); setWizardType('MACHINE'); }}>
                            + Add Machine
                        </button>
                    </div>
                </div>

                <div className="dashboard-grid">
                    {/* Team Section */}
                    <div className="glass-panel" style={{ background: 'rgba(0,0,0,0.2)' }}>
                        <h3 style={{ color: '#38bdf8' }}>Team Members (Salaries)</h3>
                        {recurringItems.filter(i => i.category === 'TEAM').map(item => (
                            <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', borderBottom: '1px solid var(--glass-border)' }}>
                                <div>
                                    <div style={{ fontWeight: 'bold' }}>{item.name}</div>
                                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Pays ${item.amount} on day {item.dayOfMonth}</div>
                                </div>
                                <button className="btn" style={{ padding: '0.25rem 0.5rem', background: 'var(--danger)', fontSize: '0.8rem', color: 'white' }} onClick={() => handleDelete(item.id)}>Remove</button>
                            </div>
                        ))}
                        {recurringItems.filter(i => i.category === 'TEAM').length === 0 && <p style={{ fontStyle: 'italic', color: 'var(--text-secondary)' }}>No team members added.</p>}
                    </div>

                    {/* Building Section */}
                    <div className="glass-panel" style={{ background: 'rgba(0,0,0,0.2)' }}>
                        <h3 style={{ color: '#10b981' }}>Buildings (Rent)</h3>
                        {recurringItems.filter(i => i.category === 'BUILDING').map(item => (
                            <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', borderBottom: '1px solid var(--glass-border)' }}>
                                <div>
                                    <div style={{ fontWeight: 'bold' }}>{item.name}</div>
                                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Pays ${item.amount} on day {item.dayOfMonth}</div>
                                </div>
                                <button className="btn" style={{ padding: '0.25rem 0.5rem', background: 'var(--danger)', fontSize: '0.8rem', color: 'white' }} onClick={() => handleDelete(item.id)}>Remove</button>
                            </div>
                        ))}
                        {recurringItems.filter(i => i.category === 'BUILDING').length === 0 && <p style={{ fontStyle: 'italic', color: 'var(--text-secondary)' }}>No buildings added.</p>}
                    </div>

                    {/* Machine Section */}
                    <div className="glass-panel" style={{ background: 'rgba(0,0,0,0.2)' }}>
                        <h3 style={{ color: '#fbbf24' }}>Machines</h3>
                        {recurringItems.filter(i => i.category === 'MACHINE').map(item => (
                            <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', borderBottom: '1px solid var(--glass-border)' }}>
                                <div>
                                    <div style={{ fontWeight: 'bold' }}>{item.name}</div>
                                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Pays ${item.amount} on day {item.dayOfMonth}</div>
                                </div>
                                <button className="btn" style={{ padding: '0.25rem 0.5rem', background: 'var(--danger)', fontSize: '0.8rem', color: 'white' }} onClick={() => handleDelete(item.id)}>Remove</button>
                            </div>
                        ))}
                        {recurringItems.filter(i => i.category === 'MACHINE').length === 0 && <p style={{ fontStyle: 'italic', color: 'var(--text-secondary)' }}>No machines added.</p>}
                    </div>
                </div>
            </div>

            {/* Wizard Modal */}
            {showWizard && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.8)', display: 'flex', justifyContent: 'center', alignItems: 'center', backdropFilter: 'blur(5px)' }}>
                    <div className="glass-panel" style={{ width: '400px', maxWidth: '90%' }}>
                        <h3>Add {wizardType === 'TEAM' ? 'Team Member' : (wizardType === 'BUILDING' ? 'Building' : 'Machine')}</h3>
                        <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                            <div>
                                <label className="stat-label">Name</label>
                                <input placeholder={wizardType === 'TEAM' ? "e.g. Alice Smith" : (wizardType === 'BUILDING' ? "e.g. Headquarters" : "e.g. Forklift")} value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} required />
                            </div>
                            <div>
                                <label className="stat-label">{wizardType === 'TEAM' ? 'Monthly Salary' : (wizardType === 'BUILDING' ? 'Monthly Rent' : 'Monthly Maintenance')} ($)</label>
                                <input type="number" placeholder="0.00" value={formData.amount} onChange={e => setFormData({ ...formData, amount: e.target.value })} required />
                            </div>
                            <div>
                                <label className="stat-label">Day of Month to Pay</label>
                                <input type="number" min="1" max="28" value={formData.dayOfMonth} onChange={e => setFormData({ ...formData, dayOfMonth: e.target.value })} required />
                            </div>
                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                                <div>
                                    <label className="stat-label">Start Date (Optional)</label>
                                    <input type="date" value={formData.startDate || ''} onChange={e => setFormData({ ...formData, startDate: e.target.value })} />
                                </div>
                                <div>
                                    <label className="stat-label">End Date (Optional)</label>
                                    <input type="date" value={formData.endDate || ''} onChange={e => setFormData({ ...formData, endDate: e.target.value })} />
                                </div>
                            </div>
                            <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem' }}>
                                <button type="button" className="btn" style={{ flex: 1, background: 'var(--bg-secondary)' }} onClick={() => setShowWizard(false)}>Cancel</button>
                                <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>Confirm</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Automation;
