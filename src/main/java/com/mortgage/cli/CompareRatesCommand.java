package com.mortgage.cli;

import com.mortgage.formatter.TableFormatter;
import com.mortgage.model.ComparisonResult;
import com.mortgage.model.MortgageInput;
import com.mortgage.model.PaymentResult;
import com.mortgage.service.MortgageCalculator;
import com.mortgage.service.ValidationService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(
    name = "compare-rates",
    description = "Compare different interest rates side-by-side"
)
public class CompareRatesCommand implements Callable<Integer> {
    
    @ParentCommand
    private MortgageCommand parent;
    
    @Option(names = {"-p", "--principal"}, required = true, description = "Loan principal amount")
    private BigDecimal principal;
    
    @Option(names = {"-r", "--rates"}, required = true, split = ",", description = "Interest rates to compare (comma or space separated)")
    private List<BigDecimal> rates;
    
    @Option(names = {"-t", "--term"}, required = true, description = "Loan term in years")
    private int termYears;
    
    @Override
    public Integer call() {
        ValidationService validator = new ValidationService();
        
        if (rates.isEmpty()) {
            System.err.println("At least one rate must be provided");
            return 1;
        }
        
        List<ComparisonResult> results = new ArrayList<>();
        MortgageCalculator calculator = new MortgageCalculator();
        
        for (BigDecimal rate : rates) {
            ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
            
            if (!validation.isValid()) {
                System.err.println("Validation errors for rate " + rate + "%:");
                validation.getErrors().forEach(err -> System.err.println("  - " + err));
                continue;
            }
            
            MortgageInput input = new MortgageInput(principal, rate, termYears);
            PaymentResult result = calculator.calculatePayment(input);
            
            results.add(new ComparisonResult(
                rate.stripTrailingZeros().toPlainString() + "%",
                result.monthlyPayment(),
                result.totalInterest(),
                result.totalPayment()
            ));
        }
        
        if (results.isEmpty()) {
            System.err.println("No valid rates to compare");
            return 1;
        }
        
        TableFormatter.printComparisonResults("Interest Rate Comparison", results);
        
        return 0;
    }
}
