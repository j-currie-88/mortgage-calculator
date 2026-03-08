package com.mortgage.cli;

import com.mortgage.formatter.TableFormatter;
import com.mortgage.model.ComparisonResult;
import com.mortgage.model.MortgageInput;
import com.mortgage.model.PaymentResult;
import com.mortgage.model.YearlyAmortization;
import com.mortgage.service.MortgageCalculator;
import com.mortgage.service.ValidationService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Callable;

@Command(
    name = "mortgage-calc",
    version = "1.0.0",
    description = "Mortgage calculator for forecasting and answering questions about your personal mortgage",
    mixinStandardHelpOptions = true,
    subcommands = {
        PaymentCommand.class,
        AmortizationCommand.class,
        OverpaymentCommand.class,
        CompareRatesCommand.class,
        CompareTermsCommand.class,
        BalanceCommand.class
    }
)
public class MortgageCommand implements Callable<Integer> {
    
    @Override
    public Integer call() {
        // No subcommand provided, launch interactive menu
        showInteractiveMenu();
        return 0;
    }
    
    private void showInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== Mortgage Calculator ===");
            System.out.println("1. Calculate Monthly Payment");
            System.out.println("2. View Amortization Schedule");
            System.out.println("3. Analyze Overpayment Impact");
            System.out.println("4. Compare Interest Rates");
            System.out.println("5. Compare Term Lengths");
            System.out.println("6. Query Remaining Balance");
            System.out.println("0. Exit");
            System.out.print("\nSelect an option: ");
            
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("0")) {
                System.out.println("Goodbye!");
                break;
            }
            
            switch (choice) {
                case "1" -> calculatePaymentInteractive(scanner);
                case "2" -> calculateAmortizationInteractive(scanner);
                case "3" -> calculateOverpaymentInteractive(scanner);
                case "4" -> compareRatesInteractive(scanner);
                case "5" -> compareTermsInteractive(scanner);
                case "6" -> queryBalanceInteractive(scanner);
                default -> System.out.println("\nInvalid option. Please try again.");
            }
        }
    }
    
    private void calculatePaymentInteractive(Scanner scanner) {
        System.out.println("\n=== Calculate Monthly Payment ===");
        
        BigDecimal principal = promptBigDecimal(scanner, "Enter loan principal (e.g., 300000): ");
        BigDecimal rate = promptBigDecimal(scanner, "Enter annual interest rate % (e.g., 3.5): ");
        int termYears = promptInt(scanner, "Enter loan term in years (e.g., 30): ");
        
        ValidationService validator = new ValidationService();
        ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
        
        if (!validation.isValid()) {
            System.err.println("\nValidation errors:");
            validation.getErrors().forEach(err -> System.err.println("  - " + err));
            return;
        }
        
        if (validation.hasWarnings()) {
            System.out.println("\nWarnings:");
            validation.getWarnings().forEach(warn -> System.out.println("  ⚠ " + warn));
        }
        
        MortgageInput input = new MortgageInput(principal, rate, termYears);
        MortgageCalculator calculator = new MortgageCalculator();
        PaymentResult result = calculator.calculatePayment(input);
        
        TableFormatter.printPaymentResult(result);
    }
    
    private void calculateAmortizationInteractive(Scanner scanner) {
        System.out.println("\n=== View Amortization Schedule ===");
        
        BigDecimal principal = promptBigDecimal(scanner, "Enter loan principal (e.g., 300000): ");
        BigDecimal rate = promptBigDecimal(scanner, "Enter annual interest rate % (e.g., 3.5): ");
        int termYears = promptInt(scanner, "Enter loan term in years (e.g., 30): ");
        
        ValidationService validator = new ValidationService();
        ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
        
        if (!validation.isValid()) {
            System.err.println("\nValidation errors:");
            validation.getErrors().forEach(err -> System.err.println("  - " + err));
            return;
        }
        
        if (validation.hasWarnings()) {
            System.out.println("\nWarnings:");
            validation.getWarnings().forEach(warn -> System.out.println("  ⚠ " + warn));
        }
        
        MortgageInput input = new MortgageInput(principal, rate, termYears);
        MortgageCalculator calculator = new MortgageCalculator();
        var schedule = calculator.calculateAmortizationSchedule(input);
        
        TableFormatter.printAmortizationSchedule(schedule);
    }
    
    private void calculateOverpaymentInteractive(Scanner scanner) {
        System.out.println("\n=== Analyze Overpayment Impact ===");
        
        BigDecimal principal = promptBigDecimal(scanner, "Enter loan principal (e.g., 300000): ");
        BigDecimal rate = promptBigDecimal(scanner, "Enter annual interest rate % (e.g., 3.5): ");
        int termYears = promptInt(scanner, "Enter loan term in years (e.g., 30): ");
        BigDecimal extraPayment = promptBigDecimal(scanner, "Enter extra monthly payment (e.g., 200): ");
        
        ValidationService validator = new ValidationService();
        ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
        
        if (!validation.isValid()) {
            System.err.println("\nValidation errors:");
            validation.getErrors().forEach(err -> System.err.println("  - " + err));
            return;
        }
        
        if (extraPayment.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("\nExtra payment must be greater than zero");
            return;
        }
        
        if (validation.hasWarnings()) {
            System.out.println("\nWarnings:");
            validation.getWarnings().forEach(warn -> System.out.println("  ⚠ " + warn));
        }
        
        MortgageInput input = new MortgageInput(principal, rate, termYears);
        MortgageCalculator calculator = new MortgageCalculator();
        var comparison = calculator.calculateOverpaymentImpact(input, extraPayment);
        
        TableFormatter.printOverpaymentComparison(comparison);
    }
    
    private void compareRatesInteractive(Scanner scanner) {
        System.out.println("\n=== Compare Interest Rates ===");
        
        BigDecimal principal = promptBigDecimal(scanner, "Enter loan principal (e.g., 300000): ");
        int termYears = promptInt(scanner, "Enter loan term in years (e.g., 30): ");
        System.out.print("Enter interest rates to compare (comma-separated, e.g., 3.0,3.5,4.0): ");
        String ratesInput = scanner.nextLine().trim();
        
        List<BigDecimal> rates = new ArrayList<>();
        for (String rateStr : ratesInput.split("[,\\s]+")) {
            try {
                rates.add(new BigDecimal(rateStr.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Skipping invalid rate: " + rateStr);
            }
        }
        
        if (rates.isEmpty()) {
            System.err.println("\nNo valid rates provided");
            return;
        }
        
        List<ComparisonResult> results = new ArrayList<>();
        MortgageCalculator calculator = new MortgageCalculator();
        ValidationService validator = new ValidationService();
        
        for (BigDecimal rate : rates) {
            ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
            if (!validation.isValid()) continue;
            
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
            System.err.println("\nNo valid rates to compare");
            return;
        }
        
        TableFormatter.printComparisonResults("Interest Rate Comparison", results);
    }
    
    private void compareTermsInteractive(Scanner scanner) {
        System.out.println("\n=== Compare Term Lengths ===");
        
        BigDecimal principal = promptBigDecimal(scanner, "Enter loan principal (e.g., 300000): ");
        BigDecimal rate = promptBigDecimal(scanner, "Enter annual interest rate % (e.g., 3.5): ");
        System.out.print("Enter term lengths to compare in years (comma-separated, e.g., 15,20,30): ");
        String termsInput = scanner.nextLine().trim();
        
        List<Integer> terms = new ArrayList<>();
        for (String termStr : termsInput.split("[,\\s]+")) {
            try {
                terms.add(Integer.parseInt(termStr.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Skipping invalid term: " + termStr);
            }
        }
        
        if (terms.isEmpty()) {
            System.err.println("\nNo valid terms provided");
            return;
        }
        
        List<ComparisonResult> results = new ArrayList<>();
        MortgageCalculator calculator = new MortgageCalculator();
        ValidationService validator = new ValidationService();
        
        for (int termYears : terms) {
            ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
            if (!validation.isValid()) continue;
            
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
            System.err.println("\nNo valid terms to compare");
            return;
        }
        
        TableFormatter.printComparisonResults("Term Length Comparison", results);
    }
    
    private void queryBalanceInteractive(Scanner scanner) {
        System.out.println("\n=== Query Remaining Balance ===");
        
        BigDecimal principal = promptBigDecimal(scanner, "Enter loan principal (e.g., 300000): ");
        BigDecimal rate = promptBigDecimal(scanner, "Enter annual interest rate % (e.g., 3.5): ");
        int termYears = promptInt(scanner, "Enter loan term in years (e.g., 30): ");
        int afterYears = promptInt(scanner, "Query balance after how many years? (e.g., 5): ");
        
        ValidationService validator = new ValidationService();
        ValidationService.ValidationResult validation = validator.validateMortgageInput(principal, rate, termYears);
        
        if (!validation.isValid()) {
            System.err.println("\nValidation errors:");
            validation.getErrors().forEach(err -> System.err.println("  - " + err));
            return;
        }
        
        int queryMonths = afterYears * 12;
        if (queryMonths <= 0 || queryMonths > termYears * 12) {
            System.err.println("\nQuery time must be between 1 and " + termYears + " years");
            return;
        }
        
        if (validation.hasWarnings()) {
            System.out.println("\nWarnings:");
            validation.getWarnings().forEach(warn -> System.out.println("  ⚠ " + warn));
        }
        
        MortgageInput input = new MortgageInput(principal, rate, termYears);
        MortgageCalculator calculator = new MortgageCalculator();
        var schedule = calculator.calculateAmortizationSchedule(input);
        
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
        System.out.println("After:              " + afterYears + " years");
        System.out.println("\n--- Status ---");
        System.out.println("Remaining Balance:  " + currency.format(remainingBalance));
        System.out.println("Equity Built:       " + currency.format(principalPaidToDate));
        System.out.println("Interest Paid:      " + currency.format(interestPaidToDate));
        System.out.println("Principal Paid:     " + currency.format(principalPaidToDate));
        
        BigDecimal percentPaid = principalPaidToDate.divide(principal, 4, java.math.RoundingMode.HALF_UP)
                                                     .multiply(new BigDecimal("100"));
        System.out.println("Percent Paid Off:   " + percentPaid.setScale(2, java.math.RoundingMode.HALF_UP) + "%");
    }
    
    private BigDecimal promptBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }
    
    private int promptInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }
}
