package com.mortgage.model;

import java.math.BigDecimal;

public record ComparisonResult(
    String label,
    BigDecimal monthlyPayment,
    BigDecimal totalInterest,
    BigDecimal totalCost
) {}
