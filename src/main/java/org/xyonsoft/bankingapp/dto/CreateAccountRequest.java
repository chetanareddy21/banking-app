package org.xyonsoft.bankingapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "\\d{4,6}", message = "PIN must be 4 to 6 digits")
    private String pin;
}