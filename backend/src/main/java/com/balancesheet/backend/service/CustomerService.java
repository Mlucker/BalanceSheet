package com.balancesheet.backend.service;

import com.balancesheet.backend.model.Company;
import com.balancesheet.backend.model.Customer;
import com.balancesheet.backend.repository.CompanyRepository;
import com.balancesheet.backend.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    public CustomerService(CustomerRepository customerRepository, CompanyRepository companyRepository) {
        this.customerRepository = customerRepository;
        this.companyRepository = companyRepository;
    }

    public List<Customer> getCustomers(Long companyId) {
        return customerRepository.findByCompanyId(companyId);
    }

    public Customer createCustomer(Customer customer, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        customer.setCompany(company);
        return customerRepository.save(customer);
    }
}
