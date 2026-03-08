# Quick Start Guide

## Installation

1. **Clone or navigate to the project directory:**
   ```bash
   cd ~/mortgage-calculator
   ```

2. **Build the project:**
   ```bash
   mvn clean package
   ```

3. **Run the application:**
   ```bash
   java -jar target/mortgage-calculator-1.0.0.jar
   ```

   Or use the wrapper script (requires Java 21+):
   ```bash
   ./mortgage-calc
   ```

## Quick Examples

### Calculate a monthly payment
```bash
java -jar target/mortgage-calculator-1.0.0.jar payment -p 300000 -r 3.5 -t 30
```

### See how $200 extra per month helps
```bash
java -jar target/mortgage-calculator-1.0.0.jar overpayment -p 300000 -r 3.5 -t 30 -e 200
```

### Compare 15 vs 30 year mortgages
```bash
java -jar target/mortgage-calculator-1.0.0.jar compare-terms -p 300000 -r 3.5 -t 15,30
```

### Check balance after 5 years
```bash
java -jar target/mortgage-calculator-1.0.0.jar balance -p 300000 -r 3.5 -t 30 --after-years 5
```

## Interactive Mode

Simply run without arguments to enter the interactive menu:

```bash
java -jar target/mortgage-calculator-1.0.0.jar
```

Then select from 6 calculation options with guided prompts.

## Getting Help

```bash
java -jar target/mortgage-calculator-1.0.0.jar --help
java -jar target/mortgage-calculator-1.0.0.jar payment --help
```

## Requirements

- Java 21 or higher (compiled with Java 21)
- Maven 3.6+ (for building)

## Project Structure

```
mortgage-calculator/
├── src/main/java/com/mortgage/
│   ├── MortgageCalculatorApp.java    # Main entry point
│   ├── cli/                           # Command implementations
│   ├── service/                       # Business logic
│   ├── model/                         # Data models (Java records)
│   └── formatter/                     # Output formatting
├── src/test/java/                     # Unit tests
├── pom.xml                            # Maven configuration
├── README.md                          # Full documentation
├── QUICKSTART.md                      # This file
└── mortgage-calc                      # Shell wrapper script
```

## Running Tests

```bash
mvn test
```

All tests use standard mortgage scenarios to verify calculation accuracy.
