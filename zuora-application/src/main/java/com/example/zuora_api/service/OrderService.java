package com.example.zuora_api.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.OrderApi;
import com.example.zuora_api.dto.OrderDto;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderService {

  private final OrderApi orderApi;

  public List<OrderDto> getOrdersBySubscription(String subscriptionNumber) {
    return orderApi.getOrdersBySubscription(subscriptionNumber);
  }

  public boolean activate(String orderNumber) {
    return orderApi.activate(orderNumber);
  }

  public boolean forceDelete(String orderNumber) {
    return orderApi.forceDelete(orderNumber);
  }
}
