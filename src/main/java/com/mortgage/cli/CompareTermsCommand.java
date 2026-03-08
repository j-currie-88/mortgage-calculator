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
    name = "compare-terms",
    description = "Compare different loan term lengths side-by-side"
)
public class CompareTermsCommand implements Callable<Integer> {
    
    @ParentCommand
    private MortgageCommand parent;
    
    @Option(names = {"-p", "--principal"}, required = true, description = "Loan principal amount")
    private BigDecimal principal;
    
    @Option(names = {"-r", "--rate"}, required = true, description = "Annual interest rate (e.g., 3.5 for 3.5%%)")
    private BigDecimal rate;
    
    @Option(names = {"-t", "--terms"}, required = true, split = ",", description = "Term lengths in years to compare (comma or space separated)")
    private List<Integer> terms;
    
    @Override
    public Integer call() {
        ValidationService validator = new ValidationService();
        
        if (terms.isEmpty()) {
            System.err.println("At least one term must be provided");
            return 1;
        }
        
        List<ComparisonResult> results = new ArrayList<>();
        MortgageCalculator calculator = new MortgageCalculator();
        
        for (int termYears : terms) {
            ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
            
            if (!validation.isValid()) {
                System.err.println("Validation errors for " + termYears + " year term:");
                validation.getErrors().forEach(err -> System.err.println("  - " + err));
                continue;
            }
            
            MortgageInput input = new MortgageInput(principal, rate, termYears);
            PaymentResult result = calculator.calculatePayment(input);
            
            results.add(new ComparisonResult(
                termYears + " years",
                result.monthlyPayment(),
                result.totalInterest(),
                result.totalPayment()
            ));
        }
        
        if (results.isEmpty()) {
            System.err.println("No valid terms to compare");
            return 1;
        }
        
        TableFormatter.printComparisonResults("Term Length Comparison", results);
        
        return 0;
    }
}
