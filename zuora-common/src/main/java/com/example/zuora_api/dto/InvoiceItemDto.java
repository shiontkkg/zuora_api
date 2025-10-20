package com.example.zuora_api.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceItemDto {
  
  private String id;
  
  private String chargeName;
  
  private int chargeAmount;
  
  private int taxAmount;
  
  private int balance;
  
  private LocalDate serviceStartDate;
  
  private LocalDate serviceEndDate;
}
