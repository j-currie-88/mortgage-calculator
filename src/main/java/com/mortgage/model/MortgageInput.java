package com.mortgage.model;

import java.math.BigDecimal;

public record MortgageInput(
    BigDecimal principal,
    BigDecimal annualRate,
    int termYears
) {
    public BigDecimal monthlyRate() {
        return annualRate.divide(new BigDecimal("100"), 10, java.math.RoundingMode.HALF_UP)
                         .divide(new BigDecimal("12"), 10, java.math.RoundingMode.HALF_UP);
    }
    
    public int totalMonths() {
        return termYears * 12;
    }
}
