package com.example.zuora_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditMemoItemPartDto {
  
  private int amount;
}
