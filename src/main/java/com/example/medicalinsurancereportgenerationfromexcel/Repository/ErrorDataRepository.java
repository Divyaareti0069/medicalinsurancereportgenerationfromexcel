package com.example.medicalinsurancereportgenerationfromexcel.Repository;

import com.example.medicalinsurancereportgenerationfromexcel.Model.ErrorData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ErrorDataRepository extends MongoRepository<ErrorData,String> {
 ErrorData findBydatetime(LocalDateTime datetime);
}
