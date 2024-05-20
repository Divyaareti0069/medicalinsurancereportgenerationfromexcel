package com.example.medicalinsurancereportgenerationfromexcel.Repository;

import com.example.medicalinsurancereportgenerationfromexcel.Model.Providers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository

public interface ProviderRepository extends MongoRepository<Providers,String> {
    Optional<Providers> findById(String providerName);
}

