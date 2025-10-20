package com.example.zuora_api.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.AccountApi;
import com.example.zuora_api.dto.PreviewChangePlanOrderDto;
import com.example.zuora_api.model.GenerateBillingDocumentsRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {
  
  private final AccountApi accountApi;
  
  public PreviewChangePlanOrderDto previewBillingDocumentsByAccount(String accountNumber, LocalDate targetDate) {
    var request = new GenerateBillingDocumentsRequest(accountNumber, null, targetDate);
    return this.accountApi.previewBillingDocumentsByAccount(request);
  }
  
  public Integer generateBillingDocumentsByAccount(String accountNumber, LocalDate documentDate, LocalDate targetDate) {
    var request = new GenerateBillingDocumentsRequest(accountNumber, documentDate, targetDate);
    return this.accountApi.generateBillingDocumentsByAccount(request);
  }
}
