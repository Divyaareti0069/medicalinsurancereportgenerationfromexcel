package com.example.medicalinsurancereportgenerationfromexcel.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {

    private InvoiceId id;
    private String policy;
    private String customerDefinedSort;
    private String subscriberName;
    private String coverageDates;
    private String status;
    private String volume;
    private float chargeAmount;
    private String adjCode;
    private String coverageType;
    private String benefitGroup1;
    private String benefitGroup2;
    private String benefitGroup3;
    private String providerName;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvoiceId implements Serializable {
        private String id;
        private String coverageDates;
        private String plan;
    }

}
