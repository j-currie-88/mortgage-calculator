package com.mortgage.model;

import java.math.BigDecimal;

public record PaymentResult(
    BigDecimal monthlyPayment,
    BigDecimal totalPayment,
    BigDecimal totalInterest,
    MortgageInput input
) {}
