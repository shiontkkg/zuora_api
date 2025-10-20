package com.example.zuora_api.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductDto {
  
  String id;
  
  String name;
  
//  Map<String, ProductRatePlanDto> productRatePlanMap;
  List<ProductRatePlanDto> productRatePlans;
}
