# Mortgage Calculator CLI

A comprehensive command-line application for mortgage forecasting and analysis. Calculate payments, view amortization schedules, analyze overpayment impacts, and compare different mortgage scenarios.

## Features

- **Monthly Payment Calculation** - Calculate your monthly mortgage payment
- **Amortization Schedule** - View yearly breakdown of principal and interest
- **Overpayment Analysis** - See how extra payments save time and money
- **Interest Rate Comparison** - Compare multiple rates side-by-side
- **Term Length Comparison** - Evaluate different loan terms
- **Balance Queries** - Check remaining balance at any point in time

## Requirements

- Java 21 or higher
- Maven 3.6 or higher

## Building

```bash
mvn clean package
```

This creates an executable JAR: `target/mortgage-calculator-1.0.0.jar`

## Usage

### Interactive Mode

Launch without arguments to enter interactive menu:

```bash
java -jar target/mortgage-calculator-1.0.0.jar
```

### Command-Line Mode

Use subcommands for direct execution:

#### Calculate Monthly Payment

```bash
java -jar target/mortgage-calculator-1.0.0.jar payment \
  --principal 300000 \
  --rate 3.5 \
  --term 30
```

**Output:**
```
=== Monthly Payment Calculation ===
Principal:        $300,000.00
Interest Rate:    3.5%
Term:             30 years

--- Results ---
Monthly Payment:  $1,347.13
Total Payment:    $484,968.27
Total Interest:   $184,968.27
```

#### View Amortization Schedule

```bash
java -jar target/mortgage-calculator-1.0.0.jar amortization \
  --principal 300000 \
  --rate 3.5 \
  --term 15
```

Shows yearly breakdown of interest paid, principal paid, and ending balance.

#### Analyze Overpayment Impact

```bash
java -jar target/mortgage-calculator-1.0.0.jar overpayment \
  --principal 300000 \
  --rate 3.5 \
  --term 30 \
  --extra-payment 200
```

**Output:**
```
=== Overpayment Impact Analysis ===

--- Original Mortgage ---
Monthly Payment:  $1,347.13
Total Interest:   $184,968.27
Payoff Time:      30 years (360 months)

--- With Overpayment ---
Monthly Payment:  $1,547.13
Total Interest:   $142,878.74
Payoff Time:      23 years 11 months (287 months)

--- Savings ---
Time Saved:       6 years 1 months
Interest Saved:   $42,089.53
```

#### Compare Interest Rates

```bash
java -jar target/mortgage-calculator-1.0.0.jar compare-rates \
  --principal 300000 \
  --term 30 \
  --rates 3.0,3.5,4.0,4.5
```

**Output:**
```
=== Interest Rate Comparison ===
Option             Monthly Payment     Total Interest         Total Cost
---------------------------------------------------------------------------
3%                       $1,264.81        $155,332.36        $455,332.36
3.5%                     $1,347.13        $184,968.27        $484,968.27
4%                       $1,432.25        $215,608.52        $515,608.52
4.5%                     $1,520.06        $247,220.13        $547,220.13
```

#### Compare Term Lengths

```bash
java -jar target/mortgage-calculator-1.0.0.jar compare-terms \
  --principal 300000 \
  --rate 3.5 \
  --terms 15,20,30
```

**Output:**
```
=== Term Length Comparison ===
Option             Monthly Payment     Total Interest         Total Cost
---------------------------------------------------------------------------
15 years                 $2,144.65         $86,036.57        $386,036.57
20 years                 $1,739.88        $117,571.00        $417,571.00
30 years                 $1,347.13        $184,968.27        $484,968.27
```

#### Query Remaining Balance

```bash
java -jar target/mortgage-calculator-1.0.0.jar balance \
  --principal 300000 \
  --rate 3.5 \
  --term 30 \
  --after-years 5
```

**Output:**
```
=== Remaining Balance Query ===
After:              5 years 0 months

--- Status ---
Remaining Balance:  $269,091.53
Equity Built:       $30,908.47
Interest Paid:      $49,919.33
Principal Paid:     $30,908.47
Percent Paid Off:   10.30%
```

## Example Scenarios

### First-Time Home Buyer

Compare 15-year vs 30-year mortgage to understand payment vs total cost trade-offs:

```bash
java -jar target/mortgage-calculator-1.0.0.jar compare-terms \
  --principal 350000 \
  --rate 3.75 \
  --terms 15,30
```

### Refinance Analysis

Compare current rate vs potential new rates:

```bash
java -jar target/mortgage-calculator-1.0.0.jar compare-rates \
  --principal 280000 \
  --term 25 \
  --rates 4.5,3.5,3.0
```

### Overpayment Strategy

Evaluate the impact of making extra payments:

```bash
java -jar target/mortgage-calculator-1.0.0.jar overpayment \
  --principal 400000 \
  --rate 4.0 \
  --term 30 \
  --extra-payment 500
```

### Equity Check

See how much equity you've built after 10 years:

```bash
java -jar target/mortgage-calculator-1.0.0.jar balance \
  --principal 300000 \
  --rate 3.5 \
  --term 30 \
  --after-years 10
```

## Help

Get help for any command:

```bash
java -jar target/mortgage-calculator-1.0.0.jar --help
java -jar target/mortgage-calculator-1.0.0.jar payment --help
```

## Technical Details

- **Precision**: Uses `BigDecimal` for accurate financial calculations
- **Validation**: Permissive validation with warnings for unusual inputs
- **Rounding**: Standard rounding (HALF_UP) to 2 decimal places for currency
- **Framework**: Built with Picocli for robust CLI handling

## License

MIT License
