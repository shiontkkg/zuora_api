package com.example.zuora_api.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.SubscriptionApi;
import com.example.zuora_api.dto.SubscriptionDto;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubscriptionService {

  private final SubscriptionApi subscriptionApi;

  public List<SubscriptionDto> getActiveSubscriptionsByAccount(String accountKey) {
    return subscriptionApi.getActiveSubscriptionsByAccount(accountKey);
  }
  
  public SubscriptionDto getSubscription(String subscriptionNumber) {
    return subscriptionApi.getSubscriptionByNumber(subscriptionNumber);
  }
}
