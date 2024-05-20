package com.example.medicalinsurancereportgenerationfromexcel.Repository;

import com.example.medicalinsurancereportgenerationfromexcel.Model.InvoiceHeader;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceHeaderRepository extends MongoRepository<InvoiceHeader,String> {
    InvoiceHeader findByInvoiceDateAndInvoiceNumberAndProviderName(String invoiceDate, String invoiceNumber, String providerName);
}