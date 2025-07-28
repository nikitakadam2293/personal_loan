package com.personal_loan.personal_loan.dto;
import lombok.Data;

@Data
public class RateResponse {

    private double baseRate;
    private double employerAdjustment;
    private double referralAdjustment;
    private double incomeAdjustment;
    private double finalRate;
    private double processingFee;

}
