package com.example.zuora_api.api;

import java.util.List;
import com.example.zuora_api.dto.OrderDto;

public interface OrderApi {
  
  List<OrderDto> getOrdersBySubscription(String subscriptionNumber);
  
  boolean activate(String orderNumber);
  
  boolean forceDelete(String orderNumber);
}
