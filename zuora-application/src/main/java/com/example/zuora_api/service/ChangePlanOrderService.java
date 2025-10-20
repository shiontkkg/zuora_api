package com.example.zuora_api.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.ProductRatePlanApi;
import com.example.zuora_api.api.SubscriptionApi;
import com.example.zuora_api.api.ChangePlanOrderApi;
import com.example.zuora_api.dto.PreviewChangePlanOrderDto;
import com.example.zuora_api.model.ChangePlanDowngradeOrderRequest;
import com.example.zuora_api.model.ChangePlanUpgradeOrderRequest;
import com.example.zuora_api.model.DiscountOption;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ChangePlanOrderService {

  private final ChangePlanOrderApi changePlanOrderApi;

  private final SubscriptionApi subscriptionApi;

  private final ProductRatePlanApi productRatePlanApi;

  public PreviewChangePlanOrderDto previewChangePlan(
      String accountNumber,
      String subscriptionNumber,
      LocalDate orderDate,
      String ratePlanId,
      String currentProductRatePlanId,
      String newProductRatePlanId,
      int discountType,
      int price,
      int downgradeOption) {
    // 本来であればここでサブスクリプション情報が更新されていないことを確認する

    var subscriptionDto = subscriptionApi.getSubscriptionByNumber(subscriptionNumber);
    var currentProductRatePlan =
        productRatePlanApi.getProductRatePlan(subscriptionDto.getProductRatePlanId());
    var newProductRatePlan = productRatePlanApi.getProductRatePlan(newProductRatePlanId);

    if (currentProductRatePlan.getPrice() < newProductRatePlan.getPrice()) {
      // アップグレード
      var request =
          new ChangePlanUpgradeOrderRequest(
              accountNumber,
              orderDate,
              subscriptionDto,
              newProductRatePlan,
              new DiscountOption(discountType, price));
      return changePlanOrderApi.previewChangePlanUpgrade(request);

    } else if (currentProductRatePlan.getPrice() > newProductRatePlan.getPrice()) {
      // ダウングレード
      var request =
          new ChangePlanDowngradeOrderRequest(
              accountNumber,
              orderDate,
              subscriptionDto,
              newProductRatePlan,
              new DiscountOption(discountType, price),
              (0 < downgradeOption) ? true : false,
              (1 < downgradeOption) ? true : false,
              (2 < downgradeOption) ? true : false);
      return changePlanOrderApi.previewChangePlanDowngrade(request);

    } else {
      throw new IllegalArgumentException("同じプランへの契約変更はできません。");
    }
  }

  public String changePlan(
      String accountNumber,
      String subscriptionNumber,
      LocalDate orderDate,
      String ratePlanId,
      String currentProductRatePlanId,
      String newProductRatePlanId,
      int discountType,
      int price,
      int downgradeOption) {

    // 本来であればここでサブスクリプション情報が更新されていないことを確認する

    var subscriptionDto = subscriptionApi.getSubscriptionByNumber(subscriptionNumber);
    var currentProductRatePlan =
        productRatePlanApi.getProductRatePlan(subscriptionDto.getProductRatePlanId());
    var newProductRatePlan = productRatePlanApi.getProductRatePlan(newProductRatePlanId);

    if (currentProductRatePlan.getPrice() < newProductRatePlan.getPrice()) {
      var request =
          new ChangePlanUpgradeOrderRequest(
              accountNumber,
              orderDate,
              subscriptionDto,
              newProductRatePlan,
              new DiscountOption(discountType, price));
      return changePlanOrderApi.changePlanUpgrade(request);

    } else if (currentProductRatePlan.getPrice() > newProductRatePlan.getPrice()) {
      var request =
          new ChangePlanDowngradeOrderRequest(
              accountNumber,
              orderDate,
              subscriptionDto,
              newProductRatePlan,
              new DiscountOption(discountType, price),
              (0 < downgradeOption) ? true : false,
              (1 < downgradeOption) ? true : false,
              (2 < downgradeOption) ? true : false);
      return changePlanOrderApi.changePlanDowngrade(request);

    } else {
      throw new IllegalArgumentException("同じプランへの契約変更はできません。");
    }
  }
}
