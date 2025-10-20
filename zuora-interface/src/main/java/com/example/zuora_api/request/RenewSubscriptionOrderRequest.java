package com.example.zuora_api.request;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RenewSubscriptionOrderRequest {
  
  private String accountNumber;
  
  private String subscriptionNumber;
  
  private LocalDate orderDate;

  private String currentProductRatePlanId;
  
  private int discountType;
  
  private int price;
}
