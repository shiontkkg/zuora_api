package com.example.zuora_api.api;

import com.example.zuora_api.dto.PreviewChangePlanOrderDto;
import com.example.zuora_api.model.GenerateBillingDocumentsRequest;

public interface AccountApi {
  
  public PreviewChangePlanOrderDto previewBillingDocumentsByAccount(GenerateBillingDocumentsRequest request);
  
  public Integer generateBillingDocumentsByAccount(GenerateBillingDocumentsRequest request);
}
