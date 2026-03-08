package com.mortgage.service;

import com.mortgage.model.MortgageInput;
import com.mortgage.model.PaymentResult;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MortgageCalculatorTest {
    
    private final MortgageCalculator calculator = new MortgageCalculator();
    
    @Test
    void testStandard30YearMortgage() {
        MortgageInput input = new MortgageInput(
            new BigDecimal("300000"),
            new BigDecimal("3.5"),
            30
        );
        
        PaymentResult result = calculator.calculatePayment(input);
        
        // Expected monthly payment for $300k at 3.5% for 30 years is approximately $1347.13
        assertEquals(0, result.monthlyPayment().compareTo(new BigDecimal("1347.13")));
        assertTrue(result.totalInterest().compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    void testZeroInterestRate() {
        MortgageInput input = new MortgageInput(
            new BigDecimal("240000"),
            new BigDecimal("0"),
            20
        );
        
        PaymentResult result = calculator.calculatePayment(input);
        
        // With 0% interest, monthly payment should be principal / total months
        BigDecimal expected = new BigDecimal("240000").divide(new BigDecimal("240"), 2, java.math.RoundingMode.HALF_UP);
        assertEquals(0, result.monthlyPayment().compareTo(expected));
        assertEquals(0, result.totalInterest().compareTo(BigDecimal.ZERO));
    }
    
    @Test
    void test15YearMortgage() {
        MortgageInput input = new MortgageInput(
            new BigDecimal("300000"),
            new BigDecimal("3.0"),
            15
        );
        
        PaymentResult result = calculator.calculatePayment(input);
        
        // Expected monthly payment for $300k at 3.0% for 15 years is approximately $2071.74
        assertEquals(0, result.monthlyPayment().compareTo(new BigDecimal("2071.74")));
    }
    
    @Test
    void testHighInterestRate() {
        MortgageInput input = new MortgageInput(
            new BigDecimal("200000"),
            new BigDecimal("7.5"),
            30
        );
        
        PaymentResult result = calculator.calculatePayment(input);
        
        // Expected monthly payment for $200k at 7.5% for 30 years is approximately $1398.43
        assertEquals(0, result.monthlyPayment().compareTo(new BigDecimal("1398.43")));
    }
}
