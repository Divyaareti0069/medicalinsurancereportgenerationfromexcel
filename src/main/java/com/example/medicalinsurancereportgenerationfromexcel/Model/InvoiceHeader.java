package com.example.medicalinsurancereportgenerationfromexcel.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(value="invoice_headers")
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(def = "{'invoiceDate': 1, 'invoiceNumber': 1, 'providerName': 1}", unique = true)
})
public class InvoiceHeader {
    private String invoiceDate;
    private String invoiceNumber;
    private int recordCount;
    private String providerName;
    private LocalDateTime uploaded_timeStamp;
}
