package com.example.zuora_api.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.ProductRatePlanApi;
import com.example.zuora_api.dto.ProductRatePlanDto;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductRatePlanService {
  
  private final ProductRatePlanApi productRatePlanApi;
  
  public List<ProductRatePlanDto> getProductRatePlansByProduct(String productId) {
    return productRatePlanApi.getProductRatePlansByProduct(productId);
  }
}
