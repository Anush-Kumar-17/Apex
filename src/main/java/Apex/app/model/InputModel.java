package Apex.app.model;

import lombok.Data;

@Data
public class InputModel {

    private Double income;                 // Applicant's income
    private Integer creditScore;           // Credit Score of the applicant (300-900 range)
    private Integer age;                   // Applicant's age (must be >= 18)
    private Integer loanTenurePrefMonths;  // Loan tenure preference in months
    private Integer existingLoans;         // Number of existing loans
    private Double currentEmiOutflow;      // Current EMI outflow per month
    private String occupation;             // Applicant's occupation (e.g., Salaried, Self-Employed)
    private Integer cityTier;              // City Tier (1, 2, or 3)
    private Double bankBalanceAvg;         // Average bank balance
    private Integer accountAgeMonths;      // Age of the bank account in months
    private Integer defaultsLast2y;        // Defaults in the past 2 years
}