package com.mortgage.service;

import com.mortgage.model.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MortgageCalculator {
    
    public PaymentResult calculatePayment(MortgageInput input) {
        BigDecimal monthlyPayment = calculateMonthlyPayment(input);
        BigDecimal totalPayment = monthlyPayment.multiply(new BigDecimal(input.totalMonths()));
        BigDecimal totalInterest = totalPayment.subtract(input.principal());
        
        return new PaymentResult(
            monthlyPayment.setScale(2, RoundingMode.HALF_UP),
            totalPayment.setScale(2, RoundingMode.HALF_UP),
            totalInterest.setScale(2, RoundingMode.HALF_UP),
            input
        );
    }
    
    private BigDecimal calculateMonthlyPayment(MortgageInput input) {
        BigDecimal monthlyRate = input.monthlyRate();
        int totalMonths = input.totalMonths();
        
        // Handle 0% interest rate case
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return input.principal().divide(new BigDecimal(totalMonths), 10, RoundingMode.HALF_UP);
        }
        
        // M = P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(totalMonths);
        
        BigDecimal numerator = input.principal().multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, 10, RoundingMode.HALF_UP);
    }
    
    public AmortizationSchedule calculateAmortizationSchedule(MortgageInput input) {
        PaymentResult paymentResult = calculatePayment(input);
        List<YearlyAmortization> yearlyBreakdown = new ArrayList<>();
        
        BigDecimal balance = input.principal();
        BigDecimal monthlyPayment = paymentResult.monthlyPayment();
        BigDecimal monthlyRate = input.monthlyRate();
        
        for (int year = 1; year <= input.termYears(); year++) {
            BigDecimal yearInterest = BigDecimal.ZERO;
            BigDecimal yearPrincipal = BigDecimal.ZERO;
            
            for (int month = 1; month <= 12; month++) {
                if (balance.compareTo(BigDecimal.ZERO) <= 0) break;
                
                BigDecimal interestPayment = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal principalPayment = monthlyPayment.subtract(interestPayment);
                
                // Handle final payment
                if (principalPayment.compareTo(balance) > 0) {
                    principalPayment = balance;
                }
                
                yearInterest = yearInterest.add(interestPayment);
                yearPrincipal = yearPrincipal.add(principalPayment);
                balance = balance.subtract(principalPayment);
            }
            
            yearlyBreakdown.add(new YearlyAmortization(
                year,
                yearInterest.setScale(2, RoundingMode.HALF_UP),
                yearPrincipal.setScale(2, RoundingMode.HALF_UP),
                balance.setScale(2, RoundingMode.HALF_UP)
            ));
            
            if (balance.compareTo(BigDecimal.ZERO) <= 0) break;
        }
        
        return new AmortizationSchedule(paymentResult, yearlyBreakdown);
    }
    
    public record OverpaymentComparison(
        AmortizationSchedule original,
        AmortizationSchedule withOverpayment,
        int monthsSaved,
        BigDecimal interestSaved
    ) {}
    
    public OverpaymentComparison calculateOverpaymentImpact(MortgageInput input, BigDecimal extraPayment) {
        AmortizationSchedule original = calculateAmortizationSchedule(input);
        
        // Calculate with overpayment
        BigDecimal balance = input.principal();
        BigDecimal monthlyPayment = calculateMonthlyPayment(input);
        BigDecimal totalPayment = monthlyPayment.add(extraPayment);
        BigDecimal monthlyRate = input.monthlyRate();
        
        List<YearlyAmortization> yearlyBreakdown = new ArrayList<>();
        int monthsPaid = 0;
        BigDecimal totalInterestPaid = BigDecimal.ZERO;
        
        for (int year = 1; year <= input.termYears() + 1; year++) {
            BigDecimal yearInterest = BigDecimal.ZERO;
            BigDecimal yearPrincipal = BigDecimal.ZERO;
            
            for (int month = 1; month <= 12; month++) {
                if (balance.compareTo(BigDecimal.ZERO) <= 0) break;
                
                BigDecimal interestPayment = balance.multiply(monthlyRate).setScale(2, java.math.RoundingMode.HALF_UP);
                BigDecimal principalPayment = totalPayment.subtract(interestPayment);
                
                if (principalPayment.compareTo(balance) > 0) {
                    principalPayment = balance;
                }
                
                yearInterest = yearInterest.add(interestPayment);
                yearPrincipal = yearPrincipal.add(principalPayment);
                totalInterestPaid = totalInterestPaid.add(interestPayment);
                balance = balance.subtract(principalPayment);
                monthsPaid++;
            }
            
            if (yearPrincipal.compareTo(BigDecimal.ZERO) > 0) {
                yearlyBreakdown.add(new YearlyAmortization(
                    year,
                    yearInterest.setScale(2, java.math.RoundingMode.HALF_UP),
                    yearPrincipal.setScale(2, java.math.RoundingMode.HALF_UP),
                    balance.setScale(2, java.math.RoundingMode.HALF_UP)
                ));
            }
            
            if (balance.compareTo(BigDecimal.ZERO) <= 0) break;
        }
        
        BigDecimal totalPaid = input.principal().add(totalInterestPaid);
        PaymentResult overpaymentResult = new PaymentResult(
            totalPayment.setScale(2, java.math.RoundingMode.HALF_UP),
            totalPaid.setScale(2, java.math.RoundingMode.HALF_UP),
            totalInterestPaid.setScale(2, java.math.RoundingMode.HALF_UP),
            input
        );
        
        AmortizationSchedule withOverpayment = new AmortizationSchedule(overpaymentResult, yearlyBreakdown);
        
        int monthsSaved = input.totalMonths() - monthsPaid;
        BigDecimal interestSaved = original.paymentResult().totalInterest().subtract(totalInterestPaid);
        
        return new OverpaymentComparison(original, withOverpayment, monthsSaved, interestSaved);
    }
}
