package com.personal_loan.personal_loan.service_impl;

import com.personal_loan.personal_loan.dto.*;
import com.personal_loan.personal_loan.entity.Applicant;
import com.personal_loan.personal_loan.exception.InvalidCreditScoreException;
import com.personal_loan.personal_loan.repository.ApplicantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;


public class RateServiceImplTest {

    @Mock
    private ApplicantRepository repository;

    @InjectMocks
    private RateServiceImpl rateService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateAndSaveRate_shouldCalculate_andSaveApplicant() {
        RateRequest request = new RateRequest();
        request.setCreditScore(800);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(true);
        request.setReferringSomeone(false);

        RateResponse response = rateService.calculateAndSaveRate(request);

        assertThat(response).isNotNull();
        assertThat(response.getBaseRate()).isBetween(10.0,12.0);
        assertThat(response.getEmployerAdjustment()).isEqualTo(-0.25);
        assertThat(response.getReferralAdjustment()).isEqualTo(-0.25);
        assertThat(response.getIncomeAdjustment()).isEqualTo(-0.25);
        assertThat(response.getFinalRate()).isGreaterThanOrEqualTo(10.0);
        assertThat(response.getProcessingFee()).isBetween(1250.0,5000.0);

        verify(repository, times(1)).save(any(Applicant.class));

    }


   /*  negative testing or defensive testing.
    It proves your code:
    Protects itself against bad inputs
    Provides clear error feedback*/
    @Test
    void calculateAndSaveRate_shouldThrowException_whenCreditScoreInvalid() {
        RateRequest request = new RateRequest();
        request.setCreditScore(100);  // invalid
        request.setEmployerType("MNC");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(100000);
        request.setReferredBySomeone(false);
        request.setReferringSomeone(false);

        assertThatThrownBy(() -> rateService.calculateAndSaveRate(request))
                .isInstanceOf(InvalidCreditScoreException.class)
                .hasMessageContaining("Credit score is not valid");
    }

    //  writing additional test cases

    // Credit score is low
    @Test
    void calculateAndSaveRate_shouldCalculate_whenCreditScoreLowRange() {
        RateRequest request = new RateRequest();
        request.setCreditScore(500);   // <650
        request.setEmployerType("GOVERNMENT");
        request.setMonthlyIncome(40000);   //  <50000
        request.setLoanAmount(50000);   // process fee <1250
        request.setReferredBySomeone(false);
        request.setReferringSomeone(false);

        RateResponse response = rateService.calculateAndSaveRate(request);

        assertThat(response).isNotNull();
        assertThat(response.getBaseRate()).isBetween(17.0,24.0);
        assertThat(response.getEmployerAdjustment()).isEqualTo(-0.5);
        assertThat(response.getIncomeAdjustment()).isEqualTo(0.0);
        assertThat(response.getReferralAdjustment()).isEqualTo(0.0);
        assertThat(response.getProcessingFee()).isEqualTo(1250.0);

        verify(repository, times(1)).save(any(Applicant.class));

    }

    //  CREADIT SCORE  650-749 range

    @Test
    void calculateAndSaveRate_shouldCalculate_whenCreditScoreMiddleRange() {
        RateRequest request = new RateRequest();
        request.setCreditScore(700);  // between 650 and 749
        request.setEmployerType("PRIVATE");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(false);
        request.setReferringSomeone(true);     // false NEW

        RateResponse response = rateService.calculateAndSaveRate(request);
        assertThat(response.getBaseRate()).isBetween(13.0,16.0);

    }



    // high credit score (>950)
    @Test
    void calculateAndSaveRate_shouldCalculate_whenCreditScoreAbove950() {
        RateRequest request = new RateRequest();
        request.setCreditScore(970);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(80000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(false);
        request.setReferringSomeone(true);

/*
         RateResponse response = rateService.calculateAndSaveRate(request);
        assertThat(response).isNotNull();
        assertThat(response.getBaseRate()).isEqualTo(24.0);  // 24
      // this throw exception credit score invalid
*/

        assertThatThrownBy(() -> rateService.calculateAndSaveRate(request))
                .isInstanceOf(InvalidCreditScoreException.class)
                .hasMessageContaining("Credit score is not valid");    // this pass test case if credit score greater than 950
    }


//  REFERRAL ONLY REFERRED
    @Test
    void calculateAndSaveRate_shouldCalculate_whenOnlyReferred() {
        RateRequest request = new RateRequest();
        request.setCreditScore(800);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(true);
        request.setReferringSomeone(false);

        RateResponse response = rateService.calculateAndSaveRate(request);
        assertThat(response.getReferralAdjustment()).isEqualTo(-0.25);
    }

    //  REFERRAL  ADJUSTMENT : REFERRED = TRUE , REFERRING = TRUE
    @Test
    void calculateAndSaveRate_shouldCalculate_whenBothReferredAndReferring() {
        RateRequest request = new RateRequest();
        request.setCreditScore(800);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(true);
        request.setReferringSomeone(true);

        RateResponse response = rateService.calculateAndSaveRate(request);
        assertThat(response.getReferralAdjustment()).isEqualTo(-0.5);
    }

    // REFERRAL ADJUSTMENT : REFERRED = FALSE, REFERRING = TRUE
    @Test
    void calculateAndSaveRate_shouldCalculate_whenOnlyReferringTrue() {
        RateRequest request = new RateRequest();
        request.setCreditScore(800);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(false);
        request.setReferringSomeone(true);

        RateResponse response = rateService.calculateAndSaveRate(request);
        assertThat(response.getReferralAdjustment()).isEqualTo(-0.25);
    }


    // EMPLOYER  ADJUSTMENT = 0.0 (PRIVATE)
    @Test
    void calculateAndSaveRate_shouldCalculate_whenEmployerIsPrivate() {
        RateRequest request = new RateRequest();
        request.setCreditScore(800);
        request.setEmployerType("PRIVATE");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(false);
        request.setReferringSomeone(false);

        RateResponse response = rateService.calculateAndSaveRate(request);
        assertThat(response.getEmployerAdjustment()).isEqualTo(0.0);
    }

    @Test
    void calculateAndSaveRate_shouldCapProcessingFeeAt5000() {
        RateRequest request = new RateRequest();
        request.setCreditScore(800);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(80000);
        request.setLoanAmount(1000000);
        request.setReferredBySomeone(false);
        request.setReferringSomeone(false);

        RateResponse response = rateService.calculateAndSaveRate(request);

        assertThat(response.getProcessingFee()).isEqualTo(5000.0);
    }

    @Test
    void calculateAndSaveRate_shouldCalculate_whenIncomeAbove100000() {
        RateRequest request = new RateRequest();
        request.setCreditScore(800);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(150000);
        request.setLoanAmount(200000);
        request.setReferredBySomeone(false);
        request.setReferringSomeone(false);

        RateResponse response = rateService.calculateAndSaveRate(request);

        assertThat(response).isNotNull();
        assertThat(response.getIncomeAdjustment()).isEqualTo(-0.5);
        assertThat(response.getEmployerAdjustment()).isEqualTo(-0.25);
        assertThat(response.getReferralAdjustment()).isEqualTo(0.0);

        verify(repository, times(1)).save(any(Applicant.class));
    }





    @Test
    void calculateAndSaveEMI_shouldCalculate_andSaveApplicant() {
        EMIRequest request = new EMIRequest();
        request.setLoanAmount(100000);
        request.setAnnualRate(12);
        request.setTenureAmount(12);

        EMIResponse response = rateService.calculateAndSaveEMI(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmi()).isGreaterThan(0);
        assertThat(response.getTotalInterest()).isGreaterThan(0);
        assertThat(response.getTotalRepayment()).isGreaterThan(response.getPrincipal());
        assertThat(response.getAmortizationSchedule()).hasSize(12);

        verify(repository, times(1)).save(any(Applicant.class));
    }

    @Test
    void compareScenarios_shouldReturnCorrectResults() {
        EMIRequest scenario1 = new EMIRequest();
        scenario1.setLoanAmount(50000);
        scenario1.setAnnualRate(10);
        scenario1.setTenureAmount(12);

        EMIRequest scenario2 = new EMIRequest();
        scenario2.setLoanAmount(100000);
        scenario2.setAnnualRate(12);
        scenario2.setTenureAmount(24);

        ScenarioRequest request = new ScenarioRequest();
        request.setScenario(List.of(scenario1, scenario2));

        ScenarioComparisonResponse response = rateService.compareScenarios(request);

        assertThat(response).isNotNull();
        assertThat(response.getResult()).hasSize(2);

        ScenarioResult r1 = response.getResult().get(0);
        ScenarioResult r2 = response.getResult().get(1);

        assertThat(r1.getEmi()).isGreaterThan(0);
        assertThat(r2.getEmi()).isGreaterThan(0);


    }

}
