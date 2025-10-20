package com.example.zuora_api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.zuora_api.dto.ProductDto;
import com.example.zuora_api.service.ProductService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/products")
@CrossOrigin
@AllArgsConstructor
public class ProductController {
  
  private final ProductService productService;
  
  @GetMapping
  public List<ProductDto> getProducts() {
    return productService.getProducts();
  }
}
