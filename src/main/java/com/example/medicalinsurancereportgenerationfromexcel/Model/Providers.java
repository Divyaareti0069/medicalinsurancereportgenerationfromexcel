package com.example.medicalinsurancereportgenerationfromexcel.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="providers")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Providers{

    @Id
    private String providerName;
    private String health;
    private String dental;
    private String vision;
    private String life;


}
