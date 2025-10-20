package com.example.zuora_api.command;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSubscriptionOrderCommand {

  private String accountNumber;
  
  private LocalDate orderDate;

//  private String productId;

  private String productRatePlanId;
  
  private int discountType;

  private int price;
}
