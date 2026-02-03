package com.balancesheet.backend.controller;

import com.balancesheet.backend.model.Customer;
import com.balancesheet.backend.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(customerService.getCustomers(companyId));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(
            @RequestBody Customer customer,
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(customerService.createCustomer(customer, companyId));
    }
}
