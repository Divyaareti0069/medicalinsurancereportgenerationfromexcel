package com.example.medicalinsurancereportgenerationfromexcel.Repository;

import com.example.medicalinsurancereportgenerationfromexcel.Model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice,String> {
    Optional<Invoice> findFirstByOrderByIdAsc();




    }

