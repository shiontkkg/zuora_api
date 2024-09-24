package com.example.zuora_api.product;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plans")
@CrossOrigin
@AllArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public List<PlanDto> getBasicPlans(@RequestParam("productId") String productId) {
        return planService.getBasicPlansByProduct(productId);
    }
}
