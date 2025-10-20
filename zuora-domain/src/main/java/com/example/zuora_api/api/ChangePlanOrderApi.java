package com.example.zuora_api.api;

import com.example.zuora_api.dto.PreviewChangePlanOrderDto;
import com.example.zuora_api.model.ChangePlanDowngradeOrderRequest;
import com.example.zuora_api.model.ChangePlanUpgradeOrderRequest;

public interface ChangePlanOrderApi {

  public PreviewChangePlanOrderDto previewChangePlanUpgrade(ChangePlanUpgradeOrderRequest request);

  public PreviewChangePlanOrderDto previewChangePlanDowngrade(
      ChangePlanDowngradeOrderRequest request);

  public String changePlanUpgrade(ChangePlanUpgradeOrderRequest request);

  public String changePlanDowngrade(ChangePlanDowngradeOrderRequest request);
}
