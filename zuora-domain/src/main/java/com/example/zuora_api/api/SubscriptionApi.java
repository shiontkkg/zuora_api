package com.example.zuora_api.api;

import java.util.List;
import com.example.zuora_api.dto.SubscriptionDto;

public interface SubscriptionApi {

  public List<SubscriptionDto> getActiveSubscriptionsByAccount(String accountKey);

  public SubscriptionDto getSubscriptionByNumber(String subscriptionNumber);
}
