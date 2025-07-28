package com.personal_loan.personal_loan.controller;
import com.personal_loan.personal_loan.dto.EMIRequest;
import com.personal_loan.personal_loan.dto.EMIResponse;
import com.personal_loan.personal_loan.dto.RateRequest;
import com.personal_loan.personal_loan.dto.RateResponse;
import com.personal_loan.personal_loan.entity.Applicant;
import com.personal_loan.personal_loan.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import com.personal_loan.personal_loan.dto.ScenarioComparisonResponse;
import com.personal_loan.personal_loan.dto.ScenarioRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/rate")
public class RateController {
    @Autowired
    private RateService rateService;

    @PostMapping("/calculate_rate")
    public RateResponse calculateRate(@RequestBody RateRequest request) {
        return rateService.calculateAndSaveRate(request);
    }

    @PostMapping("/calculate_emi")
    public EMIResponse calculateEmi(@RequestBody EMIRequest request) {
        return rateService.calculateAndSaveEMI(request);
    }

    @PostMapping("/compare")
    public ScenarioComparisonResponse compareScenario(@RequestBody ScenarioRequest request) {
        return rateService.compareScenarios(request);
    }

    // get all
    @GetMapping("/getall")
    public List<Applicant> getAllApplicants() {
        return rateService.getAllApplicants();
    }

    // get by id
    @GetMapping("/{id}")
    public Applicant getApplicantById(@PathVariable Long id) {
        return rateService.getApplicantById(id);
    }
}





































/*
after replace this:
req.getCreditScore()
with:
creditScoreService.getCreditScore(applicantId)
* */