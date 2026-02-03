import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../api/api';

const CompanyContext = createContext();

export const useCompany = () => useContext(CompanyContext);

export const CompanyProvider = ({ children }) => {
    const [companyId, setCompanyId] = useState(parseInt(localStorage.getItem('companyId')) || 1);
    const [companyName, setCompanyName] = useState(companyId === 1 ? 'Demo Inc.' : 'My Company');

    const switchCompany = (id, name) => {
        setCompanyId(id);
        setCompanyName(name);
        localStorage.setItem('companyId', id);
        // Reload page to force refresh all data
        window.location.reload();
    };

    // Update axios default header whenever companyId changes
    useEffect(() => {
        api.defaults.headers.common['X-Company-ID'] = companyId;
    }, [companyId]);

    return (
        <CompanyContext.Provider value={{ companyId, companyName, switchCompany }}>
            {children}
        </CompanyContext.Provider>
    );
};
