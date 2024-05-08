package com.example.medicalinsurancereportgenerationfromexcel.Service;

import com.example.medicalinsurancereportgenerationfromexcel.Exception.ResourceNotFoundException;
import com.example.medicalinsurancereportgenerationfromexcel.Model.Invoice;
import com.example.medicalinsurancereportgenerationfromexcel.Model.Providers;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.InvoiceRepository;
import com.example.medicalinsurancereportgenerationfromexcel.Repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providersRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    public Providers addProvider(Providers provider) {

        return providersRepository.save(provider);
    }

    public Providers updateProvider(String providerName, Providers updatedProvider) throws ResourceNotFoundException {

        Optional<Providers> existingProvider = providersRepository.findByProviderName(providerName);

        if (existingProvider.isPresent()) {
            Providers existingProviderValue = existingProvider.get();


            existingProviderValue.setHealth(updatedProvider.getHealth());
            existingProviderValue.setDental(updatedProvider.getDental());
            existingProviderValue.setVision(updatedProvider.getVision());
            existingProviderValue.setLife(updatedProvider.getLife());

            return providersRepository.save(existingProviderValue);
        } else {
            throw new ResourceNotFoundException("Provider with name " + providerName + " not found");
        }
    }

    public void deleteProvider(String providerName) {
        Providers existingProvider = providersRepository.findByProviderName(providerName)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with name: " + providerName));
        providersRepository.delete(existingProvider);
    }

    public List<Providers> getAllProviders() {
        return providersRepository.findAll();
    }

    public Optional<Providers> getProviderByName(String providerName) {
        return providersRepository.findByProviderName(providerName);
    }
}

