package com.balancesheet.backend.service;

import com.balancesheet.backend.model.Company;
import com.balancesheet.backend.model.Product;
import com.balancesheet.backend.repository.CompanyRepository;
import com.balancesheet.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;

    public ProductService(ProductRepository productRepository, CompanyRepository companyRepository) {
        this.productRepository = productRepository;
        this.companyRepository = companyRepository;
    }

    public List<Product> getProducts(Long companyId) {
        return productRepository.findByCompanyId(companyId);
    }

    public Product createProduct(Long companyId, Product product) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        product.setCompany(company);
        return productRepository.save(product);
    }

    public Product updateStock(Long productId, int quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        int newQuantity = product.getQuantityOnHand() + quantityChange;
        product.setQuantityOnHand(newQuantity);

        return productRepository.save(product);
    }
}
