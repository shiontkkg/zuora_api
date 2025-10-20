package com.example.zuora_api.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.ProductRatePlanApi;
import com.example.zuora_api.api.RenewSubscriptionOrderApi;
import com.example.zuora_api.api.SubscriptionApi;
import com.example.zuora_api.dto.PreviewOrderDto;
import com.example.zuora_api.model.DiscountOption;
import com.example.zuora_api.model.RenewSubscriptionOrderRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RenewSubscriptionOrderService {

  private final RenewSubscriptionOrderApi renewSubscriptionOrderApi;

  private final SubscriptionApi subscriptionApi;

  private final ProductRatePlanApi productRatePlanApi;

  public PreviewOrderDto previewRenewSubscription(
      String accountNumber,
      String subscriptionNumber,
      LocalDate orderDate,
      String planId,
      int discountType,
      int price) {
    // 現在のサブスクリプション情報を取得
    var subscriptionDto = subscriptionApi.getSubscriptionByNumber(subscriptionNumber);

    var productRatePlanDto =
        productRatePlanApi.getProductRatePlan(subscriptionDto.getProductRatePlanId());

    var request =
        new RenewSubscriptionOrderRequest(
            accountNumber,
            orderDate,
            subscriptionDto,
            productRatePlanDto,
            new DiscountOption(discountType, price));

    return renewSubscriptionOrderApi.previewRenewSubscription(request);
  }

  public String renewSubscription(
      String accountNumber,
      String subscriptionNumber,
      LocalDate orderDate,
      String planId,
      int discountType,
      int price) {
    // 現在のサブスクリプション情報を取得
    var subscriptionDto = subscriptionApi.getSubscriptionByNumber(subscriptionNumber);

    var productRatePlanDto =
        productRatePlanApi.getProductRatePlan(subscriptionDto.getProductRatePlanId());

    var request =
        new RenewSubscriptionOrderRequest(
            accountNumber,
            orderDate,
            subscriptionDto,
            productRatePlanDto,
            new DiscountOption(discountType, price));

    return renewSubscriptionOrderApi.renewSubscription(request);
  }
}
