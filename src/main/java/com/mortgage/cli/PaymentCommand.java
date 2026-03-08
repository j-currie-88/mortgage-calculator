package com.mortgage.cli;

import com.mortgage.formatter.TableFormatter;
import com.mortgage.model.MortgageInput;
import com.mortgage.model.PaymentResult;
import com.mortgage.service.MortgageCalculator;
import com.mortgage.service.ValidationService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

@Command(
    name = "payment",
    description = {
        "Calculate monthly mortgage payment",
        "",
        "This command calculates the fixed monthly payment required for a mortgage",
        "based on the principal amount, interest rate, and loan term. It also shows",
        "the total amount paid over the life of the loan and total interest paid."
    }
)
public class PaymentCommand implements Callable<Integer> {
    
    @ParentCommand
    private MortgageCommand parent;
    
    @Option(names = {"-p", "--principal"}, required = true, description = "Loan principal amount")
    private BigDecimal principal;
    
    @Option(names = {"-r", "--rate"}, required = true, description = "Annual interest rate (e.g., 3.5 for 3.5%%)")
    private BigDecimal rate;
    
    @Option(names = {"-t", "--term"}, required = true, description = "Loan term in years")
    private int termYears;
    
    @Override
    public Integer call() {
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
        
        MortgageInput input = new MortgageInput(principal, rate, termYears);
        MortgageCalculator calculator = new MortgageCalculator();
        PaymentResult result = calculator.calculatePayment(input);
        
        TableFormatter.printPaymentResult(result);
        
        return 0;
    }
}
