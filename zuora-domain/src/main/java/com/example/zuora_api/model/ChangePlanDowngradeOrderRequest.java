package com.example.zuora_api.model;

import java.time.LocalDate;
import com.example.zuora_api.dto.ProductRatePlanDto;
import com.example.zuora_api.dto.SubscriptionDto;
import lombok.Getter;

@Getter
public class ChangePlanDowngradeOrderRequest {

  private String accountNumber;

  private LocalDate orderDate;

  private SubscriptionDto subscriptionDto;

  private ProductRatePlanDto productRatePlanDto;

  private LocalDate triggerDate;

  private DiscountOption discountOption;

  private boolean downgradeImmediately;

  private boolean applyCreditToGeneratedInvoice;

  private boolean applyCreditToSourceInvoice;

  public ChangePlanDowngradeOrderRequest(
      String accountNumber,
      LocalDate orderDate,
      SubscriptionDto subscriptionDto,
      ProductRatePlanDto productRatePlanDto,
      DiscountOption discountOption,
      boolean downgradeImmediately,
      boolean applyCreditToGeneratedInvoice,
      boolean applyCreditToSourceInvoice) {
    this.accountNumber = accountNumber;
    this.orderDate = orderDate;
    this.subscriptionDto = subscriptionDto;
    this.productRatePlanDto = productRatePlanDto;
    this.discountOption = discountOption;
    this.downgradeImmediately = downgradeImmediately;
    this.applyCreditToGeneratedInvoice = applyCreditToGeneratedInvoice;
    this.applyCreditToSourceInvoice = applyCreditToSourceInvoice;

    this.triggerDate =
        downgradeImmediately
            ? orderDate.plusMonths(1).withDayOfMonth(1)
            : subscriptionDto.getSubscriptionEndDate();
  }
}
