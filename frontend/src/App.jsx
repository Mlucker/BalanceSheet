import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom';
import Dashboard from './components/Dashboard';
import TransactionForm from './components/TransactionForm';
import TemplateManagement from './components/TemplateManagement';
import CustomerManagement from './components/CustomerManagement';
import InvoiceList from './components/InvoiceList';
import InvoiceForm from './components/InvoiceForm';
import Accounts from './components/Accounts';
import AuditTrail from './components/AuditTrail';
import Automation from './components/Automation';
import Guide from './components/Guide';

import { CompanyProvider, useCompany } from './context/CompanyContext';

const NavLink = ({ to, children }) => {
  const location = useLocation();
  const active = location.pathname === to;
  return (
    <Link to={to} className={active ? 'active' : ''} style={{ padding: '0.5rem 1rem', borderRadius: '8px', background: active ? 'rgba(255,255,255,0.1)' : 'transparent' }}>{children}</Link>
  );
};

const Navigation = () => {
  const { companyName, switchCompany, companyId } = useCompany();

  return (
    <nav className="nav container" style={{ alignItems: 'center' }}>
      <div style={{ marginRight: 'auto', display: 'flex', flexDirection: 'column' }}>
        <h2 style={{ margin: 0, background: 'linear-gradient(to right, #38bdf8, #818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', cursor: 'pointer' }}>BalanceSheet</h2>
        <select
          value={companyId}
          onChange={(e) => switchCompany(parseInt(e.target.value), e.target.options[e.target.selectedIndex].text)}
          style={{
            marginTop: '0.5rem',
            background: 'transparent',
            border: 'none',
            color: 'var(--text-secondary)',
            fontSize: '0.8rem',
            cursor: 'pointer',
            padding: 0
          }}
        >
          <option value="1">Demo Inc.</option>
          <option value="2">My Company</option>
        </select>
      </div>

      <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
        <NavLink to="/">Dashboard</NavLink>
        <NavLink to="/transactions">Record Incident</NavLink>
        <NavLink to="/accounts">Accounts</NavLink>
        <NavLink to="/automation">Automation</NavLink>
        <NavLink to="/audit">Audit Trail</NavLink>
        <NavLink to="/templates">Templates</NavLink>
        <NavLink to="/customers">Customers</NavLink>
        <NavLink to="/invoices">Invoices</NavLink>
        <NavLink to="/guide">User Guide</NavLink>
      </div>
    </nav>
  );
};

function App() {
  return (
    <CompanyProvider>
      <Router>
        <Navigation />
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/transactions" element={<TransactionForm />} />
          <Route path="/accounts" element={<Accounts />} />
          <Route path="/automation" element={<Automation />} />
          <Route path="/audit" element={<AuditTrail />} />
          <Route path="/templates" element={<TemplateManagement />} />
          <Route path="/customers" element={<CustomerManagement />} />
          <Route path="/invoices" element={<InvoiceList />} />
          <Route path="/invoices/new" element={<InvoiceForm />} />
          <Route path="/invoices/:id/edit" element={<InvoiceForm />} />
          <Route path="/guide" element={<Guide />} />
        </Routes>
      </Router>
    </CompanyProvider>
  );
}

export default App;
