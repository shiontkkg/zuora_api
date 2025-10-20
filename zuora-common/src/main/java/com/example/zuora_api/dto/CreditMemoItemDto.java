package com.example.zuora_api.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditMemoItemDto {
  
  private String id;
  
  private String chargeName;
  
  private int amount;
  
  private int appliedAmount;
  
  private int refundAmount;
  
  private int unappliedAmount;
  
  private String subscriptionNumber;
}
