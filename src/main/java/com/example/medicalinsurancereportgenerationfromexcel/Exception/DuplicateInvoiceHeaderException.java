package com.example.medicalinsurancereportgenerationfromexcel.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateInvoiceHeaderException extends RuntimeException {
    public DuplicateInvoiceHeaderException(String message) {
        super(message);
    }

}
