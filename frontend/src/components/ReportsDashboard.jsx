import { Link, Outlet } from 'react-router-dom';

const ReportsDashboard = () => {
    return (
        <div className="container">
            <h1>Financial Reports</h1>
            <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
                <Link to="/reports/pnl" className="btn" style={{ background: 'var(--bg-secondary)', border: '1px solid var(--glass-border)', color: 'var(--text-primary)' }}>Profit & Loss</Link>
                <Link to="/reports/trial-balance" className="btn" style={{ background: 'var(--bg-secondary)', border: '1px solid var(--glass-border)', color: 'var(--text-primary)' }}>Trial Balance</Link>
                <Link to="/reports/general-ledger" className="btn" style={{ background: 'var(--bg-secondary)', border: '1px solid var(--glass-border)', color: 'var(--text-primary)' }}>General Ledger</Link>
            </div>

            <Outlet />
        </div>
    );
};

export default ReportsDashboard;
