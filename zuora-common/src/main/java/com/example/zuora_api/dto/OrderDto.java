package com.example.zuora_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDto {
  
  private String number;
  
  private String orderDate;
  
  private String description;
  
  private String status;
}
