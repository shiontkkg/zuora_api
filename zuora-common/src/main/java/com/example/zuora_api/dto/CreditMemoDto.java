package com.example.zuora_api.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditMemoDto {
  
  private String id;
  
  private String memoNumber;
  
  private LocalDate memoDate;
  
  private int amount;
  
  private int taxAmount;
  
  private int unappliedAmount;
  
  private int refundAmount;
  
  private int appliedAmount;
  
  private List<CreditMemoItemDto> creditMemoItems;
}
