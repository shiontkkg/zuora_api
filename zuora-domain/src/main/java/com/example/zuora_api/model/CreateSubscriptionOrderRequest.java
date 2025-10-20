package com.example.zuora_api.model;

import java.time.LocalDate;
import com.example.zuora_api.dto.ProductRatePlanDto;
import lombok.Getter;

@Getter
public class CreateSubscriptionOrderRequest {
  
  private String accountNumber;
  
  private LocalDate orderDate;
  
  private ProductRatePlanDto productRatePlanDto;
  
  private LocalDate triggerDate;
  
  private DiscountOption discountOption;

  public CreateSubscriptionOrderRequest(
      String accountNumber,
      LocalDate orderDate,
      ProductRatePlanDto productRatePlanDto,
      DiscountOption discountOption) {
    this.accountNumber = accountNumber;
    this.orderDate = orderDate;
    this.productRatePlanDto = productRatePlanDto;
    this.discountOption = discountOption;

    // Trigger date is set to the first day of the next month
    this.triggerDate = orderDate.plusMonths(1).withDayOfMonth(1);
  }
}
