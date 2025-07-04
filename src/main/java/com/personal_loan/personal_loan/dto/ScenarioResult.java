package com.personal_loan.personal_loan.dto;

import lombok.Data;

@Data
public class ScenarioResult {

    private double emi;
    private double totalInterest;
    private double totalRepayment;
    private double principal;
    private double annualRate;
    private int tenureMonth;

}
