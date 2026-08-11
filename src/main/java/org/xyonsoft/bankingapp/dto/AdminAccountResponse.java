package org.xyonsoft.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data @AllArgsConstructor
public class AdminAccountResponse {
    private String username;
    private String accountNumber;
    private BigDecimal balance;
}
