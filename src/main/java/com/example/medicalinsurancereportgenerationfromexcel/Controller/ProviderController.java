package com.example.medicalinsurancereportgenerationfromexcel.Controller;

import com.example.medicalinsurancereportgenerationfromexcel.Exception.ResourceNotFoundException;
import com.example.medicalinsurancereportgenerationfromexcel.Model.Providers;
import com.example.medicalinsurancereportgenerationfromexcel.Service.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/providers")
public class ProviderController {
    @Autowired
    private ProviderService providerService;
    private static final Logger log = LoggerFactory.getLogger(ProviderController.class);

    @PostMapping
    public ResponseEntity<?> addProvider(@RequestBody Providers provider) {
        try {
            Providers savedProvider = providerService.addProvider(provider);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProvider);
        } catch (Exception e) {
            log.error("An error occurred while adding provider: {}", provider, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/{providerName}")
    public ResponseEntity<?> updateProvider(@PathVariable String providerName, @RequestBody Providers updatedProvider) {
        try {
            Providers updatedProviderEntity = providerService.updateProvider(providerName, updatedProvider);
            return ResponseEntity.ok(updatedProviderEntity);
        } catch (Exception e) {
            log.error("An error occurred while updating provider: {}", providerName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/{providerName}")
    public ResponseEntity<?> deleteProvider(@PathVariable String providerName) {
        try {
            providerService.deleteProvider(providerName);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("An error occurred while deleting provider: {}", providerName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("provider with name " + providerName + " not found");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProviders() {
        try {
            List<Providers> providers = providerService.getAllProviders();
            if (providers.isEmpty()) {
                throw new ResourceNotFoundException("No providers found");
            }
            return ResponseEntity.ok(providers);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{providerName}")
    public ResponseEntity<?> getProviderByName(@PathVariable String providerName) {
        try {
            Optional<Providers> provider = providerService.getProviderByName(providerName);
            return provider.map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResourceNotFoundException("Provider " + providerName + " not found"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
