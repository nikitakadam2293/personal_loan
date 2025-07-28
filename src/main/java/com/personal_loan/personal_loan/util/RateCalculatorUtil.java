package com.personal_loan.personal_loan.util;
import com.personal_loan.personal_loan.dto.EMIRequest;
import com.personal_loan.personal_loan.dto.ScenarioResult;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.pow;

public class RateCalculatorUtil {
    public static double calculateBaseRate(int score) {

        // Linear Interpolation Formula
        if (score >= 750 && score <= 900) {
            return 10 + ((12 - 10) * (score - 750) / (900 - 750));
        } else if (score >= 650 && score < 750) {
            return 13 + ((16 - 13) * (score - 650) / (749 - 650));
        } else if (score < 650)  {  // (score >= 300   &&  score < 650)
            return 17 + ((24 - 17) * (score - 300) / (649 - 300));  //  India (CIBIL) or most systems credit scores  start at 300
        } else {
            return 24;
        }
    }

    public static double getEmployerAdjustment(String employerType) {
        if ("GOVERNMENT".equalsIgnoreCase(employerType)) {
            return -0.5;
        } else if ("MNC".equalsIgnoreCase(employerType)) {
            return -0.25;
        } else {
            return 0.0;
        }
    }

    public static double getReferralAdjustment(boolean referred, boolean referring) {
        if (referred && referring) {
            return -0.5;
        } else if (referred || referring) {
            return -0.25;
        } else {
            return 0.0;
        }
    }

    public static double getIncomeAdjustment(double income) {
        if (income > 100000) {
            return -0.5;
        } else if (income >= 50000) {
            return -0.25;
        } else {
            return 0.0;
        }
    }

    public static double calculatedoubleProcessingFee(double amount) {
        double fee = amount * 0.01;

        if (fee < 1250) {
            return 1250;
        } else if (fee > 5000) {
            return 5000;
        } else {
            return fee;
        }
    }

    public static double calculateEMI(double principal, double annualRate, int tenureMonths) {
        double monthlyRate = annualRate / 12 / 100;
        return (principal * monthlyRate * pow(1 + monthlyRate, tenureMonths)) / (pow(1 + monthlyRate, tenureMonths) - 1);
    }

    public static List<ScenarioResult> calculateScenarioResult(List<EMIRequest> scenarios) {

        List<ScenarioResult> results = new ArrayList<>();
        for (EMIRequest scenario : scenarios) {

            double P = scenario.getLoanAmount();
            double emi = calculateEMI(P, scenario.getAnnualRate(), scenario.getTenureAmount());
            double totalRepayment = emi * scenario.getTenureAmount();
            double totalInterest = totalRepayment - P;

            ScenarioResult result = new ScenarioResult();
            result.setEmi(emi);
            result.setTotalInterest(totalInterest);
            result.setTotalRepayment(totalRepayment);
            result.setPrincipal(P);
            result.setAnnualRate(scenario.getAnnualRate());
            result.setTenureMonth(scenario.getTenureAmount());

            results.add(result);
        }
        return results;
    }
}
