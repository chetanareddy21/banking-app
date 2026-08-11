package org.xyonsoft.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data @AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;      // short category, e.g. "Insufficient Balance"
    private String message;    // human-readable detail
    private String path;       // which endpoint failed
    private Map<String, String> fieldErrors; // only populated for validation failures, otherwise null
}