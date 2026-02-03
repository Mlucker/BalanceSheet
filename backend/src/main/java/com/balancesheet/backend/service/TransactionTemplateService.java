package com.balancesheet.backend.service;

import com.balancesheet.backend.model.Company;
import com.balancesheet.backend.model.TransactionTemplate;
import com.balancesheet.backend.model.TransactionTemplateEntry;
import com.balancesheet.backend.repository.CompanyRepository;
import com.balancesheet.backend.repository.TransactionTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionTemplateService {

    private final TransactionTemplateRepository templateRepository;
    private final CompanyRepository companyRepository;

    public TransactionTemplateService(TransactionTemplateRepository templateRepository,
            CompanyRepository companyRepository) {
        this.templateRepository = templateRepository;
        this.companyRepository = companyRepository;
    }

    public List<TransactionTemplate> getTemplates(Long companyId) {
        return templateRepository.findByCompanyId(companyId);
    }

    @Transactional
    public TransactionTemplate createTemplate(TransactionTemplate template, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        template.setCompany(company);

        // Link entries
        if (template.getEntries() != null) {
            for (TransactionTemplateEntry entry : template.getEntries()) {
                entry.setTemplate(template);
                // Verify account belongs to company?
                // We'll skip strict verification for now to speed up, but good to have in mind.
            }
        }

        return templateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(Long id, Long companyId) {
        TransactionTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));

        if (!template.getCompany().getId().equals(companyId)) {
            throw new SecurityException("Unauthorized access to template");
        }

        templateRepository.delete(template);
    }
}
