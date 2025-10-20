package com.example.zuora_api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.zuora_api.dto.ProductRatePlanDto;
import com.example.zuora_api.service.ProductRatePlanService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/product-rate-plans")
@CrossOrigin
@AllArgsConstructor
public class ProductRatePlanController {
  
  private final ProductRatePlanService productRatePlanService;

  @GetMapping
  public List<ProductRatePlanDto> getProductRatePlansByProduct(@RequestParam("productId") String productId) {
    return productRatePlanService.getProductRatePlansByProduct(productId);
  }
}
