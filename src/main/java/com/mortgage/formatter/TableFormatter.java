package com.mortgage.formatter;

import com.mortgage.model.*;
import com.mortgage.service.MortgageCalculator;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TableFormatter {
    
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.UK);
    private static final NumberFormat PERCENT = NumberFormat.getPercentInstance(Locale.UK);
    
    static {
        PERCENT.setMinimumFractionDigits(2);
        PERCENT.setMaximumFractionDigits(2);
    }
    
    public static void printPaymentResult(PaymentResult result) {
        System.out.println("\n=== Monthly Payment Calculation ===");
        System.out.println("Principal:        " + CURRENCY.format(result.input().principal()));
        System.out.println("Interest Rate:    " + formatRate(result.input().annualRate()) + "%");
        System.out.println("Term:             " + result.input().termYears() + " years");
        System.out.println("\n--- Results ---");
        System.out.println("Monthly Payment:  " + CURRENCY.format(result.monthlyPayment()));
        System.out.println("Total Payment:    " + CURRENCY.format(result.totalPayment()));
        System.out.println("Total Interest:   " + CURRENCY.format(result.totalInterest()));
    }
    
    public static void printAmortizationSchedule(AmortizationSchedule schedule) {
        System.out.println("\n=== Amortization Schedule (Yearly Summary) ===");
        printPaymentResult(schedule.paymentResult());
        
        System.out.println("\n--- Yearly Breakdown ---");
        System.out.printf("%-6s %15s %15s %18s%n", "Year", "Interest Paid", "Principal Paid", "Ending Balance");
        System.out.println("-".repeat(60));
        
        for (YearlyAmortization year : schedule.yearlyBreakdown()) {
            System.out.printf("%-6d %15s %15s %18s%n",
                year.year(),
                CURRENCY.format(year.totalInterest()),
                CURRENCY.format(year.totalPrincipal()),
                CURRENCY.format(year.endingBalance())
            );
        }
    }
    
    public static void printComparisonResults(String title, List<ComparisonResult> results) {
        System.out.println("\n=== " + title + " ===");
        System.out.printf("%-15s %18s %18s %18s%n", "Option", "Monthly Payment", "Total Interest", "Total Cost");
        System.out.println("-".repeat(75));
        
        for (ComparisonResult result : results) {
            System.out.printf("%-15s %18s %18s %18s%n",
                result.label(),
                CURRENCY.format(result.monthlyPayment()),
                CURRENCY.format(result.totalInterest()),
                CURRENCY.format(result.totalCost())
            );
        }
    }
    
    private static String formatRate(BigDecimal rate) {
        return rate.stripTrailingZeros().toPlainString();
    }
    
    public static void printOverpaymentComparison(MortgageCalculator.OverpaymentComparison comparison) {
        System.out.println("\n=== Overpayment Impact Analysis ===");
        
        System.out.println("\n--- Original Mortgage ---");
        PaymentResult original = comparison.original().paymentResult();
        System.out.println("Monthly Payment:  " + CURRENCY.format(original.monthlyPayment()));
        System.out.println("Total Interest:   " + CURRENCY.format(original.totalInterest()));
        System.out.println("Payoff Time:      " + original.input().termYears() + " years (" + original.input().totalMonths() + " months)");
        
        System.out.println("\n--- With Overpayment ---");
        PaymentResult withOver = comparison.withOverpayment().paymentResult();
        System.out.println("Monthly Payment:  " + CURRENCY.format(withOver.monthlyPayment()));
        System.out.println("Total Interest:   " + CURRENCY.format(withOver.totalInterest()));
        int monthsPaid = comparison.original().paymentResult().input().totalMonths() - comparison.monthsSaved();
        int yearsPaid = monthsPaid / 12;
        int remainingMonths = monthsPaid % 12;
        System.out.println("Payoff Time:      " + yearsPaid + " years " + remainingMonths + " months (" + monthsPaid + " months)");
        
        System.out.println("\n--- Savings ---");
        System.out.println("Time Saved:       " + (comparison.monthsSaved() / 12) + " years " + (comparison.monthsSaved() % 12) + " months");
        System.out.println("Interest Saved:   " + CURRENCY.format(comparison.interestSaved()));
    }
}
