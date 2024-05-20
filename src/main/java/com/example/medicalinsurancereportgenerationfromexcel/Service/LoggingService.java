package com.example.medicalinsurancereportgenerationfromexcel.Service;

import com.example.medicalinsurancereportgenerationfromexcel.Model.ErrorData;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.ErrorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoggingService {

    private final ErrorDataRepository errorDataRepository;

    @Autowired
    public LoggingService(ErrorDataRepository errorDataRepository) {
        this.errorDataRepository = errorDataRepository;
    }

    public ErrorData saveErrorData(ErrorData errorData) {
        ErrorData existingErrorData = errorDataRepository.findBydatetime(errorData.getDatetime());
        if (existingErrorData != null) {
            throw new IllegalArgumentException("ErrorData with timestamp " + errorData.getDatetime()+ " already exists");
        }
        return errorDataRepository.save(errorData);
    }

    public List<ErrorData> getAllErrorData() {
        return errorDataRepository.findAll();
    }
}
