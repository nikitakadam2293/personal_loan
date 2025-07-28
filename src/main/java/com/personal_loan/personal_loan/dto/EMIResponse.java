package com.personal_loan.personal_loan.dto;
import lombok.Data;
import java.util.List;

@Data
public class EMIResponse {

    private double emi;
    private double principal;
    private double totalInterest;
    private double totalRepayment;

    // for Amortization
    private List<AmortizationEntryResponse> amortizationSchedule;
}
