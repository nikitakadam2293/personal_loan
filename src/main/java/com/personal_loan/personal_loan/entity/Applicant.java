package com.personal_loan.personal_loan.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Data
public class Applicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Request
    private int creditScore;
    private String employerType;
    private boolean referringSomeone;  //  me
    private boolean referredBySomeone;  // friend
    private double monthlyIncome;
    private double loanAmount;

    //  Response
    private double baseRate;
    private double employerAdjustment;
    private double referralAdjustment;
    private double incomeAdjustment;
    private double finalRate;
    private double processingFee;

    //  EMI  Detail
    private int tenureMonths;
    private double emi;
    private double totalInterest;
    private double totalRepayment;

}
