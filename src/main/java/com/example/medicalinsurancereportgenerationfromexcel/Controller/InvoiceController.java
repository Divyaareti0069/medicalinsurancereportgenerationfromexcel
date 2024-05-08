package com.example.medicalinsurancereportgenerationfromexcel.Controller;

import com.example.medicalinsurancereportgenerationfromexcel.Exception.EmptyInvoiceException;
import com.example.medicalinsurancereportgenerationfromexcel.Exception.ResourceNotFoundException;
import com.example.medicalinsurancereportgenerationfromexcel.Model.Invoice;
import com.example.medicalinsurancereportgenerationfromexcel.Model.InvoiceHeader;
import com.example.medicalinsurancereportgenerationfromexcel.Model.Providers;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceHeaderRepository;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceRepository;
import com.example.medicalinsurancereportgenerationfromexcel.Service.InvoiceService;
import com.example.medicalinsurancereportgenerationfromexcel.Service.ProviderService;
import org.apache.commons.lang3.tuple.Pair;


import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

@RestController
public class InvoiceController {

    @Autowired
    private InvoiceService service;

    @Autowired
    private InvoiceRepository repository;

    @Autowired
    private InvoiceHeaderRepository invoiceHeaderRepository;

    @Autowired
    private MongoTemplate template;

    @Autowired
    private ProviderService providerService;

    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file, @RequestParam String providerName) {
        try {
            if (file.isEmpty()) {
                throw new EmptyInvoiceException("Uploaded file is empty");
            }

            List<Invoice> savedInvoices = service.processExcel(file, providerName);

            if (savedInvoices.isEmpty()) {
                throw new EmptyInvoiceException("No Records found in the uploaded file");
            }

            return ResponseEntity.status(HttpStatus.OK).body(savedInvoices);
        } catch (EmptyInvoiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }


    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getInvoices() {
        Pair<List<Invoice>, List<InvoiceHeader>> invoicesWithHeaders = service.getInvoicesWithHeaders();
        List<Invoice> invoices = invoicesWithHeaders.getLeft();

        if (invoices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok().body(invoices);
        }
    }

    @GetMapping("/invoiceHeaders")
    public ResponseEntity<List<InvoiceHeader>> getInvoiceHeaders() {
        Pair<List<Invoice>, List<InvoiceHeader>> invoicesWithHeaders = service.getInvoicesWithHeaders();
        List<InvoiceHeader> invoiceHeaders = invoicesWithHeaders.getRight();

        if (invoiceHeaders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok().body(invoiceHeaders);
        }
    }


    @GetMapping("/invoices/{providerName}")
    public ResponseEntity<Map<String, Object>> getInvoicesByProviderName(@PathVariable String providerName) {
        Query invoiceQuery = new Query(Criteria.where("providerName").is(providerName));
        List<Invoice> invoices = template.find(invoiceQuery, Invoice.class);

        Query headerQuery = new Query(Criteria.where("providerName").is(providerName));
        InvoiceHeader invoiceHeader = template.findOne(headerQuery, InvoiceHeader.class);

        if (invoices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("invoiceHeader", invoiceHeader);
            response.put("invoices", invoices);

            return ResponseEntity.ok().body(response);
        }
    }



    @PostMapping("/providers")
        public ResponseEntity<?> addProvider(@RequestBody Providers provider) {
            try {
                Providers savedProvider = providerService.addProvider(provider);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedProvider);
            } catch (Exception e) {
                log.error("An error occurred while adding provider: {}", provider, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            }
        }

        @PutMapping("/providers/{providerName}")
        public ResponseEntity<?> updateProvider(@PathVariable String providerName, @RequestBody Providers updatedProvider) {
            try {
                Providers updatedProviderEntity = providerService.updateProvider(providerName, updatedProvider);
                return ResponseEntity.ok(updatedProviderEntity);
            } catch (Exception e) {
                log.error("An error occurred while updating provider: {}", providerName, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            }
        }

        @DeleteMapping("/providers/{providerName}")
        public ResponseEntity<?> deleteProvider(@PathVariable String providerName) {
            try {
                providerService.deleteProvider(providerName);
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                log.error("An error occurred while deleting provider: {}", providerName, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("provider with name "+providerName+" not found");
            }
        }

        @GetMapping("/providers")
        public ResponseEntity<?> getAllProviders() {
            try {
                List<Providers> providers = providerService.getAllProviders();
                if (providers.isEmpty()) {
                    throw new ResourceNotFoundException("No providers found");
                }
                return ResponseEntity.ok(providers);
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        @GetMapping("/providers/{providerName}")
        public ResponseEntity<?> getProviderByName(@PathVariable String providerName) {
            try {
                Optional<Providers> provider = providerService.getProviderByName(providerName);
                return provider.map(ResponseEntity::ok)
                        .orElseThrow(() -> new ResourceNotFoundException("Provider "+ providerName +" not found" ));
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }


    }





