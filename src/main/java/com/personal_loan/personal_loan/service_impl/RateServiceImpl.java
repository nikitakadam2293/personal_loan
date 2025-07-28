package com.personal_loan.personal_loan.service_impl;
import com.personal_loan.personal_loan.dto.RateRequest;
import com.personal_loan.personal_loan.dto.RateResponse;
import com.personal_loan.personal_loan.dto.EMIResponse;
import com.personal_loan.personal_loan.dto.EMIRequest;
import com.personal_loan.personal_loan.dto.AmortizationEntryResponse;
import com.personal_loan.personal_loan.dto.ScenarioComparisonResponse;
import com.personal_loan.personal_loan.dto.ScenarioRequest;
import com.personal_loan.personal_loan.dto.ScenarioResult;
import com.personal_loan.personal_loan.entity.Applicant;
import com.personal_loan.personal_loan.exception.ApplicantNotFoundException;
import com.personal_loan.personal_loan.exception.InvalidCreditScoreException;
import com.personal_loan.personal_loan.repository.ApplicantRepository;
import com.personal_loan.personal_loan.service.RateService;
import com.personal_loan.personal_loan.util.RateCalculatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.personal_loan.personal_loan.util.RateCalculatorUtil.calculatedoubleProcessingFee;
import static java.lang.Math.round;

@Service
public class RateServiceImpl implements RateService {

    @Autowired
    private ApplicantRepository repository;

    Applicant applicant = new Applicant();

    @Override
    public RateResponse calculateAndSaveRate(RateRequest request) {

       //throw exception if credit score is not  valid
        int score = request.getCreditScore();
        if (score < 300 || score > 950) {
            throw new InvalidCreditScoreException("Credit score is not valid. Must between 300  and 950");
        }

        double baseRate = RateCalculatorUtil.calculateBaseRate(score);
        double employerAdj = RateCalculatorUtil.getEmployerAdjustment(request.getEmployerType());
        double referralAdj = RateCalculatorUtil.getReferralAdjustment(request.isReferredBySomeone(), request.isReferringSomeone());
        double incomeAdj = RateCalculatorUtil.getIncomeAdjustment(request.getMonthlyIncome());

        double finalRate = baseRate + employerAdj + referralAdj + incomeAdj;
        if (finalRate < 10.0) {
            finalRate = 10.0;
        }

        double processingFee = calculatedoubleProcessingFee(request.getLoanAmount());

        // save to  db
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
        RateResponse response = new RateResponse();
        response.setBaseRate(baseRate);
        response.setEmployerAdjustment(employerAdj);
        response.setReferralAdjustment(referralAdj);
        response.setIncomeAdjustment(incomeAdj);
        response.setFinalRate(finalRate);
        response.setProcessingFee(processingFee);

        return response;
    }

    //CALCULATE   EMI
    @Override
    public EMIResponse calculateAndSaveEMI(EMIRequest request) {

        double principal = request.getLoanAmount();
        double annualRate = request.getAnnualRate();
        int tenureMonths = request.getTenureAmount();

        double emi = RateCalculatorUtil.calculateEMI(principal, annualRate, tenureMonths);
        double totalRepayment = emi * tenureMonths;
        double totalInterest = totalRepayment - principal;

        // Save the database
        applicant.setLoanAmount(principal);
        applicant.setTenureMonths(tenureMonths);
        applicant.setFinalRate(annualRate);
        applicant.setEmi(emi);
        applicant.setTotalRepayment(totalRepayment);
        applicant.setTotalInterest(totalInterest);

        repository.save(applicant);

        // Preparation amortization scedule
        List<AmortizationEntryResponse> schedule = new ArrayList<>();
        double remainingPrincipal = principal;
        double monthlyRate = annualRate / 12 / 100;

        for (int month = 1; month <= tenureMonths; month++) {
            double interestForMonth = remainingPrincipal * monthlyRate;
            double principalForMonth = emi - interestForMonth;
            remainingPrincipal -= principalForMonth;

            if (month == tenureMonths) {
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
        EMIResponse response = new EMIResponse();
        response.setEmi(emi);
        response.setPrincipal(round(principal));
        response.setTotalInterest(round(totalInterest));
        response.setTotalRepayment(round(totalRepayment));
        response.setAmortizationSchedule(schedule);
        return response;

    }

    @Override
    public ScenarioComparisonResponse compareScenarios(ScenarioRequest request) {
        List<ScenarioResult> results = RateCalculatorUtil.calculateScenarioResult(request.getScenario());
        ScenarioComparisonResponse response = new ScenarioComparisonResponse();
        response.setResult(results);
        return response;
    }

    @Override
    public List<Applicant> getAllApplicants() {
        return repository.findAll();
    }

    @Override
    public Applicant getApplicantById(Long id) {
        Optional<Applicant> optionalApplicant = repository.findById(id);
        return optionalApplicant.orElseThrow(() -> new ApplicantNotFoundException("Applicant not found with id : " + id));

    }
}

