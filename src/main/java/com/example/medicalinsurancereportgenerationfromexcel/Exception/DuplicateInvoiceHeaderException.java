package com.example.medicalinsurancereportgenerationfromexcel.Exception;

public class DuplicateInvoiceHeaderException extends RuntimeException {

    public DuplicateInvoiceHeaderException() {
        super();
    }

    public DuplicateInvoiceHeaderException(String message) {
        super(message);
    }

    public DuplicateInvoiceHeaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateInvoiceHeaderException(Throwable cause) {
        super(cause);
    }

    protected DuplicateInvoiceHeaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
