package com.personal_loan.personal_loan.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RateRequest {

    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 950, message = "Credit score must be at most 950")
    private int creditScore;

    @NotBlank(message = "Employer type is required (GOVERNMENT, MNC, PRIVATE)")
    private String employerType;   //   GOVERNMENT, MNC, PRIVATE

    // boolean bydefault false
    private boolean referringSomeone;  //  me
    private boolean referredBySomeone;  // friend

    @Positive(message = "Monthly income must be greater than 0")
    private double monthlyIncome;

    @Min(value = 50000, message = "Loan amount greater than 50,000")
    @Max(value = 4000000, message = "Loan amount less than 40,00,000")
    private double loanAmount;

}
