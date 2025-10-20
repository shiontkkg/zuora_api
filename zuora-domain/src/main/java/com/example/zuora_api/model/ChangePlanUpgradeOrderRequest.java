package com.example.zuora_api.model;

import java.time.LocalDate;
import com.example.zuora_api.dto.ProductRatePlanDto;
import com.example.zuora_api.dto.SubscriptionDto;
import lombok.Getter;

@Getter
public class ChangePlanUpgradeOrderRequest {
  
  private String accountNumber;
  
  private LocalDate orderDate;
  
  private SubscriptionDto subscriptionDto;
  
  private ProductRatePlanDto productRatePlanDto;
  
  private LocalDate triggerDate;
  
  private DiscountOption discountOption;

  public ChangePlanUpgradeOrderRequest(
      String accountNumber,
      LocalDate orderDate,
      SubscriptionDto subscriptionDto,
      ProductRatePlanDto productRatePlanDto,
      DiscountOption discountOption) {
    this.accountNumber = accountNumber;
    this.orderDate = orderDate;
    this.subscriptionDto = subscriptionDto;
    this.productRatePlanDto = productRatePlanDto;
    this.discountOption = discountOption;
    
    this.triggerDate = orderDate.plusMonths(1).withDayOfMonth(1);
  }
}
