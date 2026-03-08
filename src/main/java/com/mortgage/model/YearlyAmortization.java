package com.mortgage.model;

import java.math.BigDecimal;

public record YearlyAmortization(
    int year,
    BigDecimal totalInterest,
    BigDecimal totalPrincipal,
    BigDecimal endingBalance
) {}
