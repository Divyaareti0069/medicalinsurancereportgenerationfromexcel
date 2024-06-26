package com.example.medicalinsurancereportgenerationfromexcel.Controller;

import com.example.medicalinsurancereportgenerationfromexcel.Exception.EmptyInvoiceException;
import com.example.medicalinsurancereportgenerationfromexcel.Exception.IllegalAugmentException;
import com.example.medicalinsurancereportgenerationfromexcel.Exception.ResourceNotFoundException;
import com.example.medicalinsurancereportgenerationfromexcel.Model.Invoice;
import com.example.medicalinsurancereportgenerationfromexcel.Model.InvoiceHeader;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceHeaderRepository;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceRepository;
import com.example.medicalinsurancereportgenerationfromexcel.Service.InvoiceService;
import com.example.medicalinsurancereportgenerationfromexcel.Service.ProviderService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService service;

    @Autowired
    private InvoiceRepository invoiceRepository;

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

    @GetMapping
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

    @GetMapping("/{providerName}")
    public ResponseEntity<Map<String, Object>> getInvoicesByProviderName(@PathVariable String providerName) {
        Query invoiceQuery = new Query(Criteria.where("providerName").is(providerName));
        List<Invoice> invoices = template.find(invoiceQuery, Invoice.class);

        Query headerQuery = new Query(Criteria.where("providerName").is(providerName));
        InvoiceHeader invoiceHeader = template.findOne(headerQuery, InvoiceHeader.class);

        if (invoices.isEmpty()) {
            throw new ResourceNotFoundException("Provider with name " + providerName + " not found");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("invoiceHeader", invoiceHeader);
        response.put("invoices", invoices);

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/year/{year}")
    public ResponseEntity<?> filterInvoicesByYear(@PathVariable int year) {
        if (!isValidYear(year)) {
            throw new IllegalAugmentException("Invalid year provided: " + year);
        }
        List<Invoice> invoices = service.filterByYear(year);
        if (invoices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No records found for the year: " + year);
        }
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<?> filterInvoicesByYearAndMonth(@PathVariable int year, @PathVariable int month) {
        if (!isValidYear(year)) {
            throw new IllegalAugmentException("Records for the year " + year + " not found");
        }
        if (!isValidMonth(month)) {
            throw new IllegalAugmentException("Invalid month provided: " + month);
        }
        List<Invoice> invoices = service.filterByYearAndMonth(year, month);
        if (invoices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No records found for the provided month: " + month);
        }
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<Invoice>> filterInvoicesByMonth(@PathVariable int month) {
        if (!isValidMonth(month)) {
            throw new IllegalAugmentException("Invalid month entered: " + month);
        }
        List<Invoice> invoices = service.filterByMonth(month);
        return ResponseEntity.ok(invoices);
    }

    private boolean isValidYear(int year) {
        return year >= 2000 && year <= 2100;
    }

    private boolean isValidMonth(int month) {
        return month >= 1 && month <= 12;
    }



}
