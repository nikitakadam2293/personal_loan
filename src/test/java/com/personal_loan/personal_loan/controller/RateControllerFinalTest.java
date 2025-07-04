package com.personal_loan.personal_loan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal_loan.personal_loan.dto.*;
import com.personal_loan.personal_loan.entity.Applicant;
import com.personal_loan.personal_loan.exception.ApplicantNotFoundException;
import com.personal_loan.personal_loan.exception.InvalidCreditScoreException;
import com.personal_loan.personal_loan.service.RateService;
import com.personal_loan.personal_loan.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(RateController.class)
public class RateControllerFinalTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RateService rateService;

  @Autowired
   private ObjectMapper objectMapper;

  @Test
    void testCalculateRate_Valid() throws Exception {
      RateRequest request = TestDataFactory.validRateRequest();
      RateResponse response = TestDataFactory.validRateResponse();

      Mockito.when(rateService.calculateAndSaveRate(any())).thenReturn(response);

      mockMvc.perform(post("/api/rate/calculate_rate")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.baseRate").value(response.getBaseRate()))
              .andExpect(jsonPath("$.employerAdjustment").value(response.getEmployerAdjustment()))
              .andExpect(jsonPath("$.referralAdjustment").value(response.getReferralAdjustment()))
              .andExpect(jsonPath("$.incomeAdjustment").value(response.getIncomeAdjustment()))
              .andExpect(jsonPath("$.finalRate").value(response.getFinalRate()))
              .andExpect(jsonPath("$.processingFee").value(response.getProcessingFee()));

  }

  @Test
  void testCalculateRate_InvalidCreditScore() throws  Exception {
      RateRequest request = TestDataFactory.invalidCreditScoreRequest();

      Mockito.when(rateService.calculateAndSaveRate(any()))
              .thenThrow(new InvalidCreditScoreException("Invalid credit score"));

      mockMvc.perform(post("/api/rate/calculate_rate")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isBadRequest())
              .andExpect(content().string("Invalid credit score"));
  }

  @Test
    void testCalculateEmi_Valid() throws Exception {
      EMIRequest request = TestDataFactory.validEmiRequest();
      EMIResponse response = TestDataFactory.validEmiResponse();

      Mockito.when(rateService.calculateAndSaveEMI(any())).thenReturn(response);

      mockMvc.perform(post("/api/rate/calculate_emi")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.emi").value(response.getEmi()))
              .andExpect(jsonPath("$.principal").value(response.getPrincipal()))
              .andExpect(jsonPath("$.totalInterest").value(response.getTotalInterest()))
              .andExpect(jsonPath("$.totalRepayment").value(response.getTotalRepayment()));
  }

  @Test
    void testCompareScenario_Valid() throws Exception {
      ScenarioRequest request = TestDataFactory.validScenarioRequest();
      ScenarioComparisonResponse response = TestDataFactory.validScenarioResponse();

      Mockito.when(rateService.compareScenarios(any())).thenReturn(response);

      mockMvc.perform(post("/api/rate/compare")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.result[0].emi").value(response.getResult().get(0).getEmi()))
              .andExpect(jsonPath("$.result[0].totalInterest").value(response.getResult().get(0).getTotalInterest()))
              .andExpect(jsonPath("$.result[0].totalRepayment").value(response.getResult().get(0).getTotalRepayment()))
              .andExpect(jsonPath("$.result[0].principal").value(response.getResult().get(0).getPrincipal()))
              .andExpect(jsonPath("$.result[0].annualRate").value(response.getResult().get(0).getAnnualRate()))
              .andExpect(jsonPath("$.result[0].tenureMonth").value(response.getResult().get(0).getTenureMonth()));
  }


  @Test
  void testGetAllApplicants() throws Exception{
    Applicant applicant = TestDataFactory.validApplicant();
    Mockito.when(rateService.getAllApplicants()).thenReturn(List.of(applicant));

    mockMvc.perform(get("/api/rate/getall"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$[0].id").value(applicant.getId()))
            .andExpect(jsonPath("$[0].creditScore").value(applicant.getCreditScore()))
            .andExpect(jsonPath("$[0].employerType").value(applicant.getEmployerType()));
  }

  @Test
  void testGetApplicantById_Found() throws Exception {
    Applicant applicant = TestDataFactory.validApplicant();
    Mockito.when(rateService.getApplicantById(applicant.getId())).thenReturn(applicant);

    mockMvc.perform(get("/api/rate/{id}",applicant.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(applicant.getId()))
            .andExpect(jsonPath("$.creditScore").value(applicant.getCreditScore()));
  }

  @Test
  void testGetApplicantById_NotFound() throws Exception {
    Mockito.when(rateService.getApplicantById(999L))
            .thenThrow(new ApplicantNotFoundException("Applicant not found"));

    mockMvc.perform(get("/api/rate/999"))
            .andExpect(status().isNotFound());
  }
}
