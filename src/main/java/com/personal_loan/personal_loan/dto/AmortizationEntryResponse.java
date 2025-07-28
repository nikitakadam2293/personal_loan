package com.personal_loan.personal_loan.dto;

import lombok.Data;

// display month wise  principalPaid, interestPaid  and  remainingBalance
@Data
public class AmortizationEntryResponse {

    private int month;
    private double principalPaid;
    private double interestPaid;
    private double remainingBalance;
}
