package com.personal_loan.personal_loan.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class EMIRequest {

    @NotNull(message = "Loan amount is required")
    @Min(value = 50000, message ="Loan amount must be at least 50000")
    private double loanAmount;

    @NotNull(message = "Tenure is required")
    @Min(value = 3, message = "Tenure must be at least 3 months")
    @Max(value = 60, message = "Tenure must be at most 60 months")
    private int tenureAmount;

    @NotNull(message = "Annual rate is required")
    @Min(value = 10, message = "Annual rate not less than 10")
    private  double  annualRate;

}
