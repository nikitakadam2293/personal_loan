package com.personal_loan.personal_loan.service_impl;

import com.personal_loan.personal_loan.dto.*;
import com.personal_loan.personal_loan.entity.Applicant;
import com.personal_loan.personal_loan.exception.InvalidCreditScoreException;
import com.personal_loan.personal_loan.repository.ApplicantRepository;
import com.personal_loan.personal_loan.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

@Service
public class RateServiceImpl implements RateService {

    @Autowired
    private ApplicantRepository repository;

    Applicant  applicant  =  new Applicant();


    @Override
    public RateResponse calculateAndSaveRate(RateRequest request) {

        //   Calculate Base  Rate
        double baseRate =  calculateBaseRate(request.getCreditScore());

        //  throw exception if credit score is not  valid
        int score  =  request.getCreditScore();
        if(score < 300  ||  score>950)
        {
            throw  new InvalidCreditScoreException("Credit score is not valid. Must between 300  and 950");
        }


        // Adjustment
        // Employer
        double  employerAdj = getEmployerAdjustment(request.getEmployerType());

        // Referal
        double referralAdj  = getReferralAdjustment( request.isReferredBySomeone(),request.isReferringSomeone() );

        // Income
        double  incomeAdj   = getIncomeAdjustment(request.getMonthlyIncome());

        double  finalRate  =    baseRate +  employerAdj  +  referralAdj  +  incomeAdj;


        if(finalRate  <  10.0)
        {
            finalRate  =  10.0;
        }

        //  Processing  Fee
        double  processingFee =  calculatedoubleProcessingFee(request.getLoanAmount());


        // save to  db
//        Applicant  applicant  =  new Applicant();

        applicant.setCreditScore(request.getCreditScore());
        applicant.setEmployerType(request.getEmployerType());
        applicant.setReferringSomeone(request.isReferringSomeone());
        applicant.setReferredBySomeone(request.isReferredBySomeone());
        applicant.setMonthlyIncome(request.getMonthlyIncome());
        applicant.setLoanAmount(request.getLoanAmount());


        applicant.setBaseRate(baseRate);
        applicant.setEmployerAdjustment(employerAdj);
        applicant.setReferralAdjustment(referralAdj);
        applicant.setIncomeAdjustment(incomeAdj);
        applicant.setFinalRate(finalRate);
        applicant.setProcessingFee(processingFee);

        repository.save(applicant);



        //  create response
        RateResponse  response  =  new RateResponse();
        response.setBaseRate(baseRate);
        response.setEmployerAdjustment(employerAdj);
        response.setReferralAdjustment(referralAdj);
        response.setIncomeAdjustment(incomeAdj);
        response.setFinalRate(finalRate);
        response.setProcessingFee(processingFee);

        return response;
    }


    public double  calculateBaseRate(int score)
    {

        // Linear Interpolation Formula
        if(score >= 750  &&  score <= 900)
        {
            return  10 + ((12-10)  *  (score - 750) / (900 - 750));
        }
        else if (score >= 650  &&  score < 750)
        {
            return  13 + ((16 - 13)  *  (score - 650)  / (749 - 650 ));

        }
        else if (score < 650)     // (score >= 300   &&  score < 650)
        {
            return  17 + ((24 - 17)  *  (score - 300)  / (649 -  300 ));  //  India (CIBIL) or most systems credit scores  start at 300

        }
        else
        {
            return  24;
        }
    }

    public  double  getEmployerAdjustment(String employerType)
    {
        if ("GOVERNMENT".equalsIgnoreCase(employerType))
        {
            return  -0.5;
        }
        else if ("MNC".equalsIgnoreCase(employerType))
        {
            return  -0.25;
        }
        else
        {
            return 0.0;

        }
    }


    public  double getReferralAdjustment(boolean referred, boolean referring)
    {
        if (referred  &&  referring)
        {
            return  -0.5;
        }
        else if (referred  ||  referring) {

            return   -0.25;
        }
        else
        {
            return  0.0;
        }

    }

    public double getIncomeAdjustment(double income)
    {
        if(income >  100000)
        {
            return  -0.5;
        }
        else if (income  >=  50000) {

            return   -0.25;
        }
        else
        {
            return   0.0;
        }
    }


    public  double calculatedoubleProcessingFee(double amount)
    {
        double  fee  =   amount  *  0.01;

        if(fee  <  1250)
        {
            return 1250;
        }
        else if(fee  >  5000)
        {
            return  5000;
        }
        else
        {
            return  fee;
        }
    }

    //   CALCULATE   EMI
    @Override
    public EMIResponse calculateAndSaveEMI(EMIRequest request) {

        double  P  =   request.getLoanAmount();
        double  R  =   request.getAnnualRate()  / 12 / 100;
        int     N  =   request.getTenureAmount();

        double emi =    (P * R  *  Math.pow( 1 +  R, N))  /  (Math.pow(1 + R , N) - 1);

        double  totalRepayment  =  emi * N;
        double  totalInterest   =   totalRepayment - P;

        // Save the database
       // Applicant applicant = new Applicant();

        applicant.setLoanAmount(P);
        applicant.setTenureMonths(N);
        applicant.setFinalRate(request.getAnnualRate());
        applicant.setEmi(emi);
        applicant.setTotalRepayment(totalRepayment);
        applicant.setTotalInterest(totalInterest);

        repository.save(applicant);


        //  Prepare  Amortization Schedule
        List<AmortizationEntryResponse>  schedule  =  new ArrayList<>();
        double remainingPrincipal  =  P;

        for(int month = 1; month <= N ; month++)
        {
            double  interestForMonth = remainingPrincipal * R;
            double  principalForMonth =  emi -  interestForMonth;

            remainingPrincipal  -=  principalForMonth;

            if(month == N)
            {
                remainingPrincipal = 0;
            }

            AmortizationEntryResponse entry = new AmortizationEntryResponse();
            entry.setMonth(month);
            entry.setPrincipalPaid(round(principalForMonth));
            entry.setInterestPaid(round(interestForMonth));
            entry.setRemainingBalance(round(remainingPrincipal));

            schedule.add(entry);


        }


        //  Prepare Response
        EMIResponse  response  = new EMIResponse();
        response.setEmi(emi);
        response.setPrincipal(round(P));
        response.setTotalInterest(round(totalInterest));
        response.setTotalRepayment(round(totalRepayment));

        response.setAmortizationSchedule(schedule);
        return response;
    }

    @Override
    public ScenarioComparisonResponse compareScenarios(ScenarioRequest request) {

       List<ScenarioResult>  results = new ArrayList<>();

       for(EMIRequest scenario : request.getScenario())
       {
           double P = scenario.getLoanAmount();
           double R = scenario.getAnnualRate() / 12/ 100;
           int N    = scenario.getTenureAmount();

           double emi =    (P * R  *  Math.pow( 1 +  R, N))  /  (Math.pow(1 + R , N) - 1);
           double totalRepayment = emi * N;
           double totalInterest  =  totalRepayment - P;

           ScenarioResult  result = new ScenarioResult();
           result.setEmi(emi);
           result.setTotalInterest(totalInterest);
           result.setTotalRepayment(totalRepayment);
           result.setPrincipal(P);
           result.setAnnualRate(scenario.getAnnualRate());
           result.setTenureMonth(N);

           results.add(result);

       }

       ScenarioComparisonResponse  response = new ScenarioComparisonResponse();
       response.setResult(results);

        return response;
    }


}
