#!/bin/bash
# Demonstration of all mortgage calculator features

JAR="target/mortgage-calculator-1.0.0.jar"
JAVA="/opt/homebrew/Cellar/openjdk/23.0.2/libexec/openjdk.jdk/Contents/Home/bin/java"

echo "========================================="
echo "Mortgage Calculator CLI - Full Demo"
echo "========================================="
echo

echo "1. Monthly Payment Calculation"
echo "   Command: payment -p 300000 -r 3.5 -t 30"
$JAVA -jar $JAR payment -p 300000 -r 3.5 -t 30
echo
echo "Press Enter to continue..."; read

echo "2. Amortization Schedule (15-year mortgage)"
echo "   Command: amortization -p 300000 -r 3.5 -t 15"
$JAVA -jar $JAR amortization -p 300000 -r 3.5 -t 15 | head -30
echo "   ... (truncated for demo)"
echo
echo "Press Enter to continue..."; read

echo "3. Overpayment Impact ($200 extra/month)"
echo "   Command: overpayment -p 300000 -r 3.5 -t 30 -e 200"
$JAVA -jar $JAR overpayment -p 300000 -r 3.5 -t 30 -e 200
echo
echo "Press Enter to continue..."; read

echo "4. Interest Rate Comparison"
echo "   Command: compare-rates -p 300000 -t 30 -r 3.0,3.5,4.0,4.5"
$JAVA -jar $JAR compare-rates -p 300000 -t 30 -r 3.0,3.5,4.0,4.5
echo
echo "Press Enter to continue..."; read

echo "5. Term Length Comparison"
echo "   Command: compare-terms -p 300000 -r 3.5 -t 15,20,30"
$JAVA -jar $JAR compare-terms -p 300000 -r 3.5 -t 15,20,30
echo
echo "Press Enter to continue..."; read

echo "6. Remaining Balance Query (after 5 years)"
echo "   Command: balance -p 300000 -r 3.5 -t 30 --after-years 5"
$JAVA -jar $JAR balance -p 300000 -r 3.5 -t 30 --after-years 5
echo

echo "========================================="
echo "Demo Complete!"
echo "========================================="
