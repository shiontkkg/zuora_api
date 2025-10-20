package com.example.zuora_api.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import com.example.zuora_api.config.ZuoraV2Client;
import com.example.zuora_api.dto.ProductDto;
import com.example.zuora_api.dto.ProductRatePlanDto;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;

public class Main {
  public static void main(String[] args) throws ApiException, org.openapitools.client.ApiException {

    var zuoraClient =
        new ZuoraClient(
            "a2581d34-150e-400e-8f94-8e0f7f0925d0",
            "rx=xPiDt9kvxQV6CVLgsU9tpefR6BEn6cVbhIrxz",
            "https://rest.apisandbox.zuora.com");
    zuoraClient.initialize();

    var zuoraV2Client =
        new ZuoraV2Client(
            "a2581d34-150e-400e-8f94-8e0f7f0925d0",
            "rx=xPiDt9kvxQV6CVLgsU9tpefR6BEn6cVbhIrxz",
            "https://rest.apisandbox.zuora.com");
    zuoraV2Client.initialize();

    var products = zuoraClient.productsApi().getProductsApi().execute();

    var productExclusionList =
        Set.of(
            "f9773ee0a58f63cb1fbe080094e76fad", // Cloud Storage - Personal
            "f9773ee026d8f81dca6d9791966178b4", // Cloud Storage - Standard
            "f9773ee0a72d26c62132e2734796b88f", // Cloud Storage Integration Services
            "f9773ee0507080ce75b788bf6cf2065e", // Cloud Storage API Access
            "8ad097b4917efc77019184c423e76f9c" // サンプルサプライ用品
            );

    var productMap = new HashMap<String, ProductDto>();
    var productRatePlanMap = new HashMap<String, ProductRatePlanDto>();

    for (var product : products.getProducts()) {
      if (productExclusionList.contains(product.getId())) {
        continue;
      }

      // 料金プランの取得はv1 APIだと呼び出しが多くなるので、v2 APIを使用
      var productRatePlans = new ArrayList<ProductRatePlanDto>();
      var planListResponse =
          zuoraV2Client
              .plans()
              .getPlans(
                  null, Arrays.asList("prices"), Arrays.asList("product_id.EQ:" + product.getId()));
      for (var plan : planListResponse.getData()) {
        for (var price : plan.getPrices().getData()) {
          if (StringUtils.equals(price.getChargeModel(), "flat_fee")) {
            var productRatePlanDto =
                ProductRatePlanDto.builder()
                    .id(plan.getId())
                    .name(plan.getName())
                    .chargeId(price.getId())
                    .chargeName(price.getName())
                    .price(price.getAmounts().get("JPY").intValue())
                    .build();
            productRatePlans.add(productRatePlanDto);
            productRatePlanMap.put(productRatePlanDto.getId(), productRatePlanDto);
            continue;
          }
        }
      }
      if (productRatePlans.size() > 0) {
        productRatePlans.sort((p1, p2) -> p1.getPrice() - p2.getPrice());

        var productDto =
            ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .productRatePlans(productRatePlans)
                .build();
        productMap.put(productDto.getId(), productDto);
      }
    }
    
    System.out.println(productMap);
  }
}
