package com.example.zuora_api.api;

import com.example.zuora_api.dto.PreviewOrderDto;
import com.example.zuora_api.model.RenewSubscriptionOrderRequest;

public interface RenewSubscriptionOrderApi {
  
  PreviewOrderDto previewRenewSubscription(RenewSubscriptionOrderRequest domainRequest);
  
  String renewSubscription(RenewSubscriptionOrderRequest domainRequest);
}
