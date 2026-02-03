package com.balancesheet.backend.controller;

import com.balancesheet.backend.model.TransactionTemplate;
import com.balancesheet.backend.service.TransactionTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class TransactionTemplateController {

    private final TransactionTemplateService templateService;

    public TransactionTemplateController(TransactionTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionTemplate>> getTemplates(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(templateService.getTemplates(companyId));
    }

    @PostMapping
    public ResponseEntity<TransactionTemplate> createTemplate(
            @RequestBody TransactionTemplate template,
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(templateService.createTemplate(template, companyId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long id,
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        templateService.deleteTemplate(id, companyId);
        return ResponseEntity.ok().build();
    }
}
