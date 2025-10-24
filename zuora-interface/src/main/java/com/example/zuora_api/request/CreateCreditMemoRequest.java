package com.example.zuora_api.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCreditMemoRequest {
  
  private String invoiceId;
  
  private String effectiveDate;
  
  private Item[] items;
  
  @Data
  @NoArgsConstructor
  public static class Item {
    
    private int amount;
    
    private String invoiceItemId;
    
    private String skuName;
  }
}
