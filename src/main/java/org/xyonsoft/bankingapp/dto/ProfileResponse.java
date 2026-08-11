package org.xyonsoft.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data @AllArgsConstructor
public class ProfileResponse {
    private String username;
    private List<AccountResponse> accounts;
}