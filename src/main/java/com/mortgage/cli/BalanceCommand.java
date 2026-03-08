package com.mortgage.cli;

import com.mortgage.model.MortgageInput;
import com.mortgage.model.YearlyAmortization;
import com.mortgage.service.MortgageCalculator;
import com.mortgage.service.ValidationService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

@Command(
    name = "balance",
    description = "Query remaining balance after a specified time"
)
public class BalanceCommand implements Callable<Integer> {
    
    @ParentCommand
    private MortgageCommand parent;
    
    @Option(names = {"-p", "--principal"}, required = true, description = "Loan principal amount")
    private BigDecimal principal;
    
    @Option(names = {"-r", "--rate"}, required = true, description = "Annual interest rate (e.g., 3.5 for 3.5%%)")
    private BigDecimal rate;
    
    @Option(names = {"-t", "--term"}, required = true, description = "Loan term in years")
    private int termYears;
    
    @Option(names = {"--after-years"}, description = "Query balance after this many years")
    private Integer afterYears;
    
    @Option(names = {"--after-months"}, description = "Query balance after this many months")
    private Integer afterMonths;
    
    @Override
    public Integer call() {
        if (afterYears == null && afterMonths == null) {
            System.err.println("Must specify either --after-years or --after-months");
            return 1;
        }
        
        ValidationService validator = new ValidationService();
        ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
        
        if (!validation.isValid()) {
            System.err.println("Validation errors:");
            validation.getErrors().forEach(err -> System.err.println("  - " + err));
            return 1;
        }
        
        if (validation.hasWarnings()) {
            System.out.println("Warnings:");
            validation.getWarnings().forEach(warn -> System.out.println("  ⚠ " + warn));
        }
        
        int queryMonths = afterMonths != null ? afterMonths : afterYears * 12;
        
        if (queryMonths <= 0 || queryMonths > termYears * 12) {
            System.err.println("Query time must be between 1 month and " + (termYears * 12) + " months");
            return 1;
        }
        
        MortgageInput input = new MortgageInput(principal, rate, termYears);
        MortgageCalculator calculator = new MortgageCalculator();
        var schedule = calculator.calculateAmortizationSchedule(input);
        
        // Calculate cumulative values up to query point
        int queryYears = (queryMonths - 1) / 12 + 1;
        BigDecimal interestPaidToDate = BigDecimal.ZERO;
        BigDecimal principalPaidToDate = BigDecimal.ZERO;
        BigDecimal remainingBalance = principal;
        
        List<YearlyAmortization> breakdown = schedule.yearlyBreakdown();
        for (int i = 0; i < Math.min(queryYears, breakdown.size()); i++) {
            YearlyAmortization year = breakdown.get(i);
            interestPaidToDate = interestPaidToDate.add(year.totalInterest());
            principalPaidToDate = principalPaidToDate.add(year.totalPrincipal());
            remainingBalance = year.endingBalance();
        }
        
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);
        
        System.out.println("\n=== Remaining Balance Query ===");
        System.out.println("After:              " + (queryMonths / 12) + " years " + (queryMonths % 12) + " months");
        System.out.println("\n--- Status ---");
        System.out.println("Remaining Balance:  " + currency.format(remainingBalance));
        System.out.println("Equity Built:       " + currency.format(principalPaidToDate));
        System.out.println("Interest Paid:      " + currency.format(interestPaidToDate));
        System.out.println("Principal Paid:     " + currency.format(principalPaidToDate));
        
        BigDecimal percentPaid = principalPaidToDate.divide(principal, 4, java.math.RoundingMode.HALF_UP)
                                                     .multiply(new BigDecimal("100"));
        System.out.println("Percent Paid Off:   " + percentPaid.setScale(2, java.math.RoundingMode.HALF_UP) + "%");
        
        return 0;
    }
}
