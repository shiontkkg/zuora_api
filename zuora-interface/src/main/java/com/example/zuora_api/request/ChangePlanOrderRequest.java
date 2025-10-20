package com.example.zuora_api.request;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePlanOrderRequest {
  
  private String accountNumber;
  
  private String subscriptionNumber;
  
  private LocalDate orderDate;

  private String ratePlanId;
  
  private String currentProductRatePlanId;
  
  private String newProductRatePlanId;
  
  private int discountType;
  
  private int price;
  
  private int downgradeOption;
}
