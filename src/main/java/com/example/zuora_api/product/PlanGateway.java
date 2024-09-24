package com.example.zuora_api.product;

import com.zuora.model.ChargeModel;
import com.zuora.sdk.ZuoraClient;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Plan;
import org.openapitools.client.model.Price;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@AllArgsConstructor
public class PlanGateway {

    private final ZuoraClient zuoraClient;

    public List<PlanDto> getBasicPlansByProduct(String productId) {
        if (StringUtils.isEmpty(productId)) {
            productId = "8ad081dd917ed47501918819c3fe28c3";
        }
        var plans = new ArrayList<PlanDto>();
        try {
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
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return plans;
    }
}
