import React from 'react';

const Guide = () => {
    return (
        <div className="container">
            <h1>User Guide</h1>
            <div className="glass-panel">
                <p>Welcome to <strong>BalanceSheet</strong>, your automated double-entry accounting system. Here is how to get started:</p>

                <hr style={{ borderColor: 'var(--glass-border)', margin: '1.5rem 0' }} />

                <div style={{ marginBottom: '2rem' }}>
                    <h3 style={{ color: 'var(--accent-primary)' }}>1. Accounts Setup</h3>
                    <p>Before recording transactions, you need **Accounts**.</p>
                    <ul>
                        <li>Go to the <strong style={{ color: 'var(--accent-secondary)' }}>Accounts</strong> tab.</li>
                        <li>Create a <strong>Cash</strong> account (Type: ASSET).</li>
                        <li>Create operational accounts like <strong>Salaries Expense</strong> (EXPENSE), <strong>Sales Revenue</strong> (REVENUE), etc.</li>
                    </ul>
                </div>

                <div style={{ marginBottom: '2rem' }}>
                    <h3 style={{ color: 'var(--accent-primary)' }}>2. Manual Transactions</h3>
                    <p>Record one-off business events like a sale or an investment.</p>
                    <ul>
                        <li>Go to <strong style={{ color: 'var(--accent-secondary)' }}>Record Incident</strong>.</li>
                        <li>Ensure your Debits and Credits equal each other.</li>
                        <li>Example: Debit <strong>Cash</strong> $500, Credit <strong>Sales Revenue</strong> $500.</li>
                    </ul>
                </div>

                <div style={{ marginBottom: '2rem' }}>
                    <h3 style={{ color: 'var(--accent-primary)' }}>3. Automation</h3>
                    <p>Manage recurring monthly payments automatically.</p>
                    <ul>
                        <li>Go to <strong style={{ color: 'var(--accent-secondary)' }}>Automation</strong>.</li>
                        <li><strong>Team Members</strong>: Pays salaries (Requires "Salaries Expense" & "Cash").</li>
                        <li><strong>Buildings</strong>: Pays rent (Requires "Rent Expense" & "Cash").</li>
                        <li><strong>Machines</strong>: Pays maintenance (Requires "Maintenance Expense" & "Cash").</li>
                    </ul>
                </div>

                <div style={{ marginBottom: '2rem' }}>
                    <h3 style={{ color: 'var(--accent-primary)' }}>4. Reporting</h3>
                    <p>View your real-time financial health.</p>
                    <ul>
                        <li><strong>Dashboard</strong>: Shows a detailed Statement of Financial Position and calculates Net Income.</li>
                        <li><strong>Audit Trail</strong>: Shows a complete history of every transaction and journal entry.</li>
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default Guide;
