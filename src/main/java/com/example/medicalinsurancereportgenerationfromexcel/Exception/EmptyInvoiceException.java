package com.example.medicalinsurancereportgenerationfromexcel.Exception;

public class EmptyInvoiceException extends RuntimeException {
    public EmptyInvoiceException(String message) {
        super(message);
    }
}
