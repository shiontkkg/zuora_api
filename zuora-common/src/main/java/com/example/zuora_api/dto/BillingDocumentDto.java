package com.example.zuora_api.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingDocumentDto {
  
  private String documentType;
  
  private String documentNumber;
  
  private LocalDate documentDate;
}
