package com.personal_loan.personal_loan.util;

import com.personal_loan.personal_loan.dto.*;
import com.personal_loan.personal_loan.entity.Applicant;

import java.util.List;

public class TestDataFactory {

    public static RateRequest validRateRequest() {

        RateRequest request = new RateRequest();
        request.setCreditScore(750);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(true);
        request.setReferringSomeone(false);
        return request;
    }

    public static RateRequest invalidCreditScoreRequest() {
        RateRequest request = validRateRequest();
        request.setCreditScore(100);  // Invalid
        return request;
    }


    public static RateResponse validRateResponse() {

        RateResponse response = new RateResponse();
        response.setBaseRate(12.0);
        response.setEmployerAdjustment(-0.25);
        response.setReferralAdjustment(-0.25);
        response.setIncomeAdjustment(-0.25);
        response.setFinalRate(11.25);
        response.setProcessingFee(2000);
        return response;
    }

    public static EMIRequest validEmiRequest() {
        EMIRequest request = new EMIRequest();
        request.setLoanAmount(100000);
        request.setAnnualRate(12);
        request.setTenureAmount(12);
        return request;
    }

    public static EMIResponse validEmiResponse() {
        EMIResponse response = new EMIResponse();
        response.setEmi(8888.88);
        response.setPrincipal(100000);
        response.setTotalInterest(6666.66);
        response.setTotalRepayment(106666.66);
        response.setAmortizationSchedule(List.of());
        return  response;
    }

    public static ScenarioRequest validScenarioRequest() {
        ScenarioRequest request = new ScenarioRequest();
        request.setScenario(List.of(validEmiRequest()));
        return request;
    }

    public static ScenarioComparisonResponse validScenarioResponse() {
        ScenarioResult result = new ScenarioResult();
        result.setEmi(5000);
        result.setTotalInterest(1000);
        result.setTotalRepayment(60000);
        result.setPrincipal(50000);
        result.setAnnualRate(10);
        result.setTenureMonth(12);

        ScenarioComparisonResponse response = new ScenarioComparisonResponse();
        response.setResult(List.of(result));
        return response;

    }

    public static Applicant validApplicant() {
        Applicant applicant = new Applicant();
        applicant.setId(1L);
        applicant.setCreditScore(750);
        applicant.setEmployerType("MNC");
        applicant.setMonthlyIncome(60000);
        applicant.setLoanAmount(200000);
        applicant.setBaseRate(12.0);
        applicant.setFinalRate(11.25);
        applicant.setProcessingFee(2000);
        return applicant;
    }

}
