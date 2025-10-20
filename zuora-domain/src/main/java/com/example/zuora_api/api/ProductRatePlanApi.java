package com.example.zuora_api.api;

import java.util.List;
import com.example.zuora_api.dto.ProductRatePlanDto;

public interface ProductRatePlanApi {
  
  public ProductRatePlanDto getProductRatePlan(String id);
  
  public List<ProductRatePlanDto> getProductRatePlansByProduct(String productId);
}
