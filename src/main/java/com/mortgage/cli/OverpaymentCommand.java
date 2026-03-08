package com.mortgage.cli;

import com.mortgage.formatter.TableFormatter;
import com.mortgage.model.MortgageInput;
import com.mortgage.service.MortgageCalculator;
import com.mortgage.service.ValidationService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

@Command(
    name = "overpayment",
    description = "Analyze the impact of extra monthly payments"
)
public class OverpaymentCommand implements Callable<Integer> {
    
    @ParentCommand
    private MortgageCommand parent;
    
    @Option(names = {"-p", "--principal"}, required = true, description = "Loan principal amount")
    private BigDecimal principal;
    
    @Option(names = {"-r", "--rate"}, required = true, description = "Annual interest rate (e.g., 3.5 for 3.5%%)")
    private BigDecimal rate;
    
    @Option(names = {"-t", "--term"}, required = true, description = "Loan term in years")
    private int termYears;
    
    @Option(names = {"-e", "--extra-payment"}, required = true, description = "Extra monthly payment amount")
    private BigDecimal extraPayment;
    
    @Override
    public Integer call() {
        ValidationService validator = new ValidationService();
        ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
        
        if (!validation.isValid()) {
            System.err.println("Validation errors:");
            validation.getErrors().forEach(err -> System.err.println("  - " + err));
            return 1;
        }
        
        if (extraPayment.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Extra payment must be greater than zero");
            return 1;
        }
        
        if (validation.hasWarnings()) {
            System.out.println("Warnings:");
            validation.getWarnings().forEach(warn -> System.out.println("  ⚠ " + warn));
        }
        
        MortgageInput input = new MortgageInput(principal, rate, termYears);
        MortgageCalculator calculator = new MortgageCalculator();
        var comparison = calculator.calculateOverpaymentImpact(input, extraPayment);
        
        TableFormatter.printOverpaymentComparison(comparison);
        
        return 0;
    }
}
