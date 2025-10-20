package com.example.zuora_api.api;

import java.util.List;
import com.example.zuora_api.dto.ProductDto;

public interface ProductApi {
  
  public List<ProductDto> getProducts();
}
