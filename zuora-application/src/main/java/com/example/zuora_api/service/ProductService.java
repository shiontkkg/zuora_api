package com.example.zuora_api.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.ProductApi;
import com.example.zuora_api.dto.ProductDto;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
  
  private final ProductApi productApi;

  public List<ProductDto> getProducts() {
    return productApi.getProducts();
  }
}
