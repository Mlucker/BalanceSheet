package com.balancesheet.backend.controller;

import com.balancesheet.backend.model.Product;
import com.balancesheet.backend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(productService.getProducts(companyId));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId,
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(companyId, product));
    }
}
