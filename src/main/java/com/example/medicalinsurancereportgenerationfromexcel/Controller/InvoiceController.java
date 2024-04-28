package com.example.medicalinsurancereportgenerationfromexcel.Controller;

import com.example.medicalinsurancereportgenerationfromexcel.Model.Invoice;
import com.example.medicalinsurancereportgenerationfromexcel.Model.Providers;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceRepository;
import com.example.medicalinsurancereportgenerationfromexcel.Service.InvoiceService;
import com.example.medicalinsurancereportgenerationfromexcel.Service.ProviderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;

@RestController
public class InvoiceController {

@Autowired
private InvoiceService service;

@Autowired
private InvoiceRepository repository;

@Autowired
private MongoTemplate template;

@Autowired
private ProviderService providerService;

@PostMapping("/upload")
public ResponseEntity<List<Invoice>> uploadExcel(@RequestParam("file") MultipartFile file, @RequestParam String providerName) {
    try {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<Invoice> savedInvoices;
        savedInvoices = service.processExcel(file,providerName);

        if (savedInvoices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(savedInvoices);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}


@GetMapping("/invoices")
public ResponseEntity<List<Invoice>> getInvoices() {
    List<Invoice> invoices = repository.findAll();
    if (invoices.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } else {
        return ResponseEntity.ok().body(invoices);
    }
}

    @GetMapping("/invoices/{providerName}")
    public ResponseEntity<List<Invoice>> getInvoicesByProviderName(@PathVariable String providerName) {
        Query query = new Query(Criteria.where("providerName").is(providerName));
        List<Invoice> invoices = template.find(query, Invoice.class);
        if (invoices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok().body(invoices);
        }
    }


    @PostMapping("/providers")
    public ResponseEntity<Providers> addProvider(@RequestBody Providers provider) {
        Providers savedProvider = providerService.addProvider(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProvider);
    }

    @PutMapping("/providers/{providerName}")
    public ResponseEntity<Providers> updateProvider(@PathVariable String providerName, @RequestBody Providers updatedProvider) {
        Providers updatedProviderEntity = providerService.updateProvider(providerName, updatedProvider);
        return ResponseEntity.ok(updatedProviderEntity);
    }

    @DeleteMapping("/providers/{providerName}")
    public ResponseEntity<Void> deleteProvider(@PathVariable String providerName) {
        providerService.deleteProvider(providerName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/providers")
    public ResponseEntity<List<Providers>> getAllProviders() {
        List<Providers> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/providers/{providerName}")
    public ResponseEntity<Providers> getProviderByName(@PathVariable String providerName) {
        Optional<Providers> provider = providerService.getProviderByName(providerName);
        return provider.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}