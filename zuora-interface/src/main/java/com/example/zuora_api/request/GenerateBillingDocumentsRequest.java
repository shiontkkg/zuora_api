package com.example.zuora_api.request;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenerateBillingDocumentsRequest {
  
  private String accountNumber;
  
  private LocalDate documentDate;
  
  private LocalDate targetDate;
}
