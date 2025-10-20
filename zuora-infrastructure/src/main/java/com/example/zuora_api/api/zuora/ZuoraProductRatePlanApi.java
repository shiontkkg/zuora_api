package com.example.zuora_api.api.zuora;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.client.ApiException;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.ProductRatePlanApi;
import com.example.zuora_api.config.ZuoraV2Client;
import com.example.zuora_api.dto.ProductRatePlanDto;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ZuoraProductRatePlanApi implements ProductRatePlanApi {

  private final ZuoraV2Client zuoraV2Client;

  public ProductRatePlanDto getProductRatePlan(String id) {

    if (StringUtils.isEmpty(id)) {
      throw new IllegalArgumentException("Product Rate Plan ID cannot be null or empty");
    }

    try {
      var planResponse = zuoraV2Client.plans().getPlan(id, Arrays.asList("prices"));
      if (planResponse != null && planResponse.getPrices() != null) {
        for (var price : planResponse.getPrices().getData()) {
          if (StringUtils.equals(price.getChargeModel(), "flat_fee")) {
            return ProductRatePlanDto.builder()
                .id(planResponse.getId())
                .name(planResponse.getName())
                .chargeId(price.getId())
                .chargeName(price.getName())
                .price(price.getAmounts().get("JPY").intValue())
                .build();
          }
        }
      }
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
    return null;
  }
  
  public List<ProductRatePlanDto> getProductRatePlansByProduct(String productId) {
    if (StringUtils.isEmpty(productId)) {
      throw new IllegalArgumentException("Product ID cannot be null or empty");
    }

    try {
      var planListResponse =
          zuoraV2Client
              .plans()
              .getPlans(null, Arrays.asList("prices"), Arrays.asList("product_id.EQ:" + productId));

      return planListResponse.getData().stream()
          .map(
              plan -> {
                for (var price : plan.getPrices().getData()) {
                  if (StringUtils.equals(price.getChargeModel(), "flat_fee")) {
                    return ProductRatePlanDto.builder()
                        .id(plan.getId())
                        .name(plan.getName())
                        .chargeId(price.getId())
                        .chargeName(price.getName())
                        .price(price.getAmounts().get("JPY").intValue())
                        .build();
                  }
                }
                return null;
              })
          .filter(dto -> dto != null)
          .toList();
    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }
}
