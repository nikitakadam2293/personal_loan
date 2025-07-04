package com.personal_loan.personal_loan.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//@Getter
//@Setter
public class RateResponse {

    private double baseRate;
    private double employerAdjustment;
    private double referralAdjustment;
    private double incomeAdjustment;
    private double finalRate;
    private double processingFee;

}
