package com.example.zuora_api.product;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PlanService {

    private final PlanGateway planGateway;

    public List<PlanDto> getBasicPlansByProduct(String productId) {
        return planGateway.getBasicPlansByProduct(productId);
    }
}
