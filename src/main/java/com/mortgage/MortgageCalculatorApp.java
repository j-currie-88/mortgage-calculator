package com.mortgage;

import com.mortgage.cli.MortgageCommand;
import picocli.CommandLine;

public class MortgageCalculatorApp {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new MortgageCommand()).execute(args);
        System.exit(exitCode);
    }
}
