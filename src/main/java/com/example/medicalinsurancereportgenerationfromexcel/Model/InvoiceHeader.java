package com.example.medicalinsurancereportgenerationfromexcel.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Document(value="invoice_headers")
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceHeader {
    public InvoiceHeaderId id;
    private int recordCount;
    private LocalDateTime uploaded_timeStamp;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvoiceHeaderId implements Serializable {
        private String invoiceDate;
        private String invoiceNumber;
        private String providerName;
    }

}
