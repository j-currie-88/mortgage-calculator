package com.mortgage.model;

import java.util.List;

public record AmortizationSchedule(
    PaymentResult paymentResult,
    List<YearlyAmortization> yearlyBreakdown
) {}
