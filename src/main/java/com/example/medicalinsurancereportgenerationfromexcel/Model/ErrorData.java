package com.example.medicalinsurancereportgenerationfromexcel.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(value="errordata")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorData {


    private String id;
    private int statusCode;
    private String errorMessage;
    private LocalDateTime datetime;
}
