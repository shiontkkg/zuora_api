package com.example.zuora_api.api;

import com.example.zuora_api.dto.PreviewOrderDto;
import com.example.zuora_api.model.CreateSubscriptionOrderRequest;

public interface CreateSubscriptionOrderApi {

  public PreviewOrderDto previewSubscription(CreateSubscriptionOrderRequest domainRequest);

  public String createSubscription(CreateSubscriptionOrderRequest domainRequest);
}
