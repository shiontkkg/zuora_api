package com.example.zuora_api.product;

import com.zuora.sdk.ZuoraClient;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Plan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

//@Configuration
public class ProductConfig {

//    @Bean
    public Map<String, List<PlanDto>> productToBasicPlansMap(ZuoraClient zuoraClient) {
        var productToBasicPlansMap = new HashMap<String, List<PlanDto>>();
        var productIds = Arrays.asList("8ad081dd917ed47501918819c3fe28c3");
        var plans = new ArrayList<PlanDto>();
        try {
            for (var productId: productIds) {
                var planListResponse = zuoraClient.plans().getPlans(
                        null,
                        Arrays.asList("prices"),
                        Arrays.asList("product_id.EQ:" + productId));
                for(var plan: planListResponse.getData()) {
                    for (var price: plan.getPrices().getData()) {
                        if (StringUtils.equals(price.getChargeModel(), "flat_fee")) {
                            var planDto = PlanDto.builder()
                                    .id(plan.getId())
                                    .name(plan.getName())
                                    .price(price.getAmounts().get("JPY").intValue())
                                    .build();
                            plans.add(planDto);
                            continue;
                        }
                    }
                }
                plans.sort((p1, p2) -> {
                    return p1.getPrice() - p2.getPrice();
                });
                productToBasicPlansMap.put(productId, plans);
            }
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return productToBasicPlansMap;
    }
}
