package com.personal_loan.personal_loan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ScenarioRequest {

    @NotNull(message = "Scenarios list cannot be null")
    @NotEmpty(message = "At least one scenario must be provided")
    @Valid
    private List<EMIRequest> scenario;

}
