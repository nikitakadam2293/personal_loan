package com.personal_loan.personal_loan.service;
import com.personal_loan.personal_loan.dto.EMIRequest;
import com.personal_loan.personal_loan.dto.EMIResponse;
import com.personal_loan.personal_loan.dto.RateRequest;
import com.personal_loan.personal_loan.dto.RateResponse;
import com.personal_loan.personal_loan.dto.ScenarioComparisonResponse;
import com.personal_loan.personal_loan.dto.ScenarioRequest;
import com.personal_loan.personal_loan.entity.Applicant;
import java.util.List;

public interface RateService {

    RateResponse calculateAndSaveRate(RateRequest request);

    EMIResponse calculateAndSaveEMI(EMIRequest request);

    ScenarioComparisonResponse compareScenarios(ScenarioRequest request);

    List<Applicant> getAllApplicants();

    Applicant getApplicantById(Long id);

}