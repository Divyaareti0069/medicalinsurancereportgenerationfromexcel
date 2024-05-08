package com.example.medicalinsurancereportgenerationfromexcel.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceHeader {
    private String invoiceDate;
    private String invoiceNumber;
    private int recordCount;
    private String providerName;
}
