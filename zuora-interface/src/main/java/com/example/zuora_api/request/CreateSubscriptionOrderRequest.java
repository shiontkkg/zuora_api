package com.example.zuora_api.request;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateSubscriptionOrderRequest {
  
  private String accountNumber;
  
  private LocalDate orderDate;

  private String productId;

  private String productRatePlanId;
  
  private int discountType;

  private int price;
}
