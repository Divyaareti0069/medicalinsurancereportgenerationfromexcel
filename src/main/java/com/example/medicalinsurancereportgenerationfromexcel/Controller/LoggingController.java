package com.example.medicalinsurancereportgenerationfromexcel.Controller;

import com.example.medicalinsurancereportgenerationfromexcel.Model.ErrorData;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.ErrorDataRepository;
import com.example.medicalinsurancereportgenerationfromexcel.Service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LoggingController {

    private final ErrorDataRepository errorDataRepository;
    private final LoggingService loggingService;

    @Autowired
    public LoggingController(ErrorDataRepository errorDataRepository, LoggingService loggingService) {
        this.errorDataRepository = errorDataRepository;
        this.loggingService = loggingService;
    }

    @PostMapping("/errordata")
    public ResponseEntity<?> createErrorData(@RequestBody ErrorData errorData) {
        try {
            ErrorData savedErrorData = loggingService.saveErrorData(errorData);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedErrorData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<List<ErrorData>> getAllLogs() {
        List<ErrorData> getErrorData = errorDataRepository.findAll();
        return ResponseEntity.ok(getErrorData);
    }
}
