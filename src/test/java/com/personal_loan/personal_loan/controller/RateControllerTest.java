package com.personal_loan.personal_loan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal_loan.personal_loan.dto.*;
import com.personal_loan.personal_loan.repository.ApplicantRepository;
import com.personal_loan.personal_loan.service.RateService;
import com.personal_loan.personal_loan.service_impl.RateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RateController.class)
public class RateControllerTest {

   @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateService rateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testcalculateRate() throws Exception{

        RateRequest request = new RateRequest();
        request.setCreditScore(750);
        request.setEmployerType("MNC");
        request.setMonthlyIncome(60000);
        request.setLoanAmount(100000);
        request.setReferredBySomeone(true);
        request.setReferringSomeone(false);

        RateResponse response = new RateResponse();
        response.setBaseRate(12.0);
        response.setFinalRate(12.5);
        response.setProcessingFee(2000);

        Mockito.when(rateService.calculateAndSaveRate(any())).thenReturn(response);

        mockMvc.perform(post("/api/rate/calculate_rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseRate").value(12.0))
                .andExpect(jsonPath("$.finalRate").value(12.5))
                .andExpect(jsonPath("$.processingFee").value(2000));
    }

    @Test
    void testcalculateEmi() throws Exception{
        EMIRequest request = new EMIRequest();
        request.setLoanAmount(100000);
        request.setAnnualRate(12);
        request.setTenureAmount(12);

        EMIResponse response = new EMIResponse();
        response.setEmi(8888.88);
        response.setTotalInterest(6666.66);
        response.setTotalRepayment(106666.66);

        Mockito.when(rateService.calculateAndSaveEMI(any())).thenReturn(response);

        mockMvc.perform(post("/api/rate/calculate_emi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emi").value(8888.88))
                .andExpect(jsonPath("$.totalInterest").value(6666.66))
                .andExpect(jsonPath("$.totalRepayment").value(106666.66));
    }

    @Test
    void testcompareScenario() throws Exception{
        ScenarioRequest request = new ScenarioRequest();
        EMIRequest scenario1 = new EMIRequest();
        scenario1.setLoanAmount(50000);
        scenario1.setAnnualRate(10);
        scenario1.setTenureAmount(12);
        request.setScenario(java.util.List.of(scenario1));

        ScenarioResult result = new ScenarioResult();
        result.setEmi(5000);
        result.setTotalInterest(1000);
        result.setTotalRepayment(60000);
        result.setPrincipal(50000);
        result.setAnnualRate(10);
        result.setTenureMonth(12);

        ScenarioComparisonResponse response = new ScenarioComparisonResponse();
        response.setResult(java.util.List.of(result));

        Mockito.when(rateService.compareScenarios(any())).thenReturn(response);

        mockMvc.perform(post("/api/rate/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].emi").value(5000))
                .andExpect(jsonPath("$.result[0].totalInterest").value(1000))
                .andExpect(jsonPath("$.result[0].totalRepayment").value(60000))
                .andExpect(jsonPath("$.result[0].principal").value(50000))
                .andExpect(jsonPath("$.result[0].annualRate").value(10))
                .andExpect(jsonPath("$.result[0].tenureMonth").value(12));



    }
}
