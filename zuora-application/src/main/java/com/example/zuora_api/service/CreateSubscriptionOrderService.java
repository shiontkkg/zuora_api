package com.example.zuora_api.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.CreateSubscriptionOrderApi;
import com.example.zuora_api.api.ProductRatePlanApi;
import com.example.zuora_api.dto.PreviewOrderDto;
import com.example.zuora_api.model.CreateSubscriptionOrderRequest;
import com.example.zuora_api.model.DiscountOption;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreateSubscriptionOrderService {

  private final CreateSubscriptionOrderApi orderApi;
  
  private final ProductRatePlanApi productRatePlanApi;
  
  public PreviewOrderDto previewSubscription(
      String accountNumber, LocalDate orderDate, String productRatePlanId, int discountType, int price) {
    var productRatePlanDto = productRatePlanApi.getProductRatePlan(productRatePlanId);
    var request = new CreateSubscriptionOrderRequest(
        accountNumber, orderDate, productRatePlanDto, new DiscountOption(discountType, price));
    return orderApi.previewSubscription(request);
  }
  
  public String createSubscription(
      String accountNumber, LocalDate orderDate, String productRatePlanId, int discountType, int price) {
    var productRatePlanDto = productRatePlanApi.getProductRatePlan(productRatePlanId);
    var request = new CreateSubscriptionOrderRequest(
        accountNumber, orderDate, productRatePlanDto, new DiscountOption(discountType, price));
    return orderApi.createSubscription(request);
  }
}
