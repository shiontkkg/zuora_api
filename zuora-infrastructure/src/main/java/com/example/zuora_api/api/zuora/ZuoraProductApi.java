package com.example.zuora_api.api.zuora;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.ProductApi;
import com.example.zuora_api.config.ZuoraV2Client;
import com.example.zuora_api.dto.ProductDto;
import com.example.zuora_api.dto.ProductRatePlanDto;
import com.zuora.ZuoraClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ZuoraProductApi implements ProductApi {
  
  private static final Set<String> PRODUCT_EXCLUSION_LIST = Set.of(
      "f9773ee0a58f63cb1fbe080094e76fad", // Cloud Storage - Personal
      "f9773ee026d8f81dca6d9791966178b4", // Cloud Storage - Standard
      "f9773ee0a72d26c62132e2734796b88f", // Cloud Storage Integration Services
      "f9773ee0507080ce75b788bf6cf2065e", // Cloud Storage API Access
      "8ad097b4917efc77019184c423e76f9c" // サンプルサプライ用品
      );

  private final ZuoraClient zuoraClient;
  
  private final ZuoraV2Client zuoraV2Client;

  public List<ProductDto> getProducts() {
    
    var productDtoList = new ArrayList<ProductDto>();
    
    try {
      var products = zuoraClient.productsApi().getProductsApi().execute();

      for (var product : products.getProducts()) {
        if (PRODUCT_EXCLUSION_LIST.contains(product.getId())) {
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
          productDtoList.add(productDto);
        }
      }
    } catch (Exception e) {
      log.error("Failed to retrieve products: {}", e.getMessage());
    }
    
    return productDtoList;
  }
}
