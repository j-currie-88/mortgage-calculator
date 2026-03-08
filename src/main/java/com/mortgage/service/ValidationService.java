package com.mortgage.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ValidationService {
    
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> warnings;
        private final List<String> errors;
        
        public ValidationResult() {
            this.valid = true;
            this.warnings = new ArrayList<>();
            this.errors = new ArrayList<>();
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
    }
    
    public ValidationResult validateMortgageInput(BigDecimal principal, BigDecimal rate, int termYears) {
        ValidationResult result = new ValidationResult();
        
        // Errors (invalid input)
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("Principal must be greater than zero");
        }
        
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0) {
            result.addError("Interest rate cannot be negative");
        }
        
        if (termYears <= 0) {
            result.addError("Term must be greater than zero years");
        }
        
        // Warnings (unusual but allowed)
        if (rate != null && rate.compareTo(BigDecimal.ZERO) == 0) {
            result.addWarning("Interest rate is 0% - this is unusual for a mortgage");
        }
        
        if (rate != null && rate.compareTo(new BigDecimal("10")) > 0) {
            result.addWarning("Interest rate above 10% is unusually high");
        }
        
        if (termYears < 5) {
            result.addWarning("Term less than 5 years is unusually short");
        }
        
        if (termYears > 40) {
            result.addWarning("Term greater than 40 years is unusually long");
        }
        
        if (principal != null && principal.compareTo(new BigDecimal("1000000")) > 0) {
            result.addWarning("Principal over $1,000,000 - ensure this is correct");
        }
        
        return result;
    }
}
