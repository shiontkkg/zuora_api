package com.example.zuora_api.model;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class GenerateBillingDocumentsRequest {

  private String accountNumber;

  private LocalDate documentDate;

  private LocalDate targetDate;

  public GenerateBillingDocumentsRequest(
      String accountNumber, LocalDate documentDate, LocalDate targetDate) {
    this.accountNumber = accountNumber;
    this.documentDate = documentDate;
    this.targetDate = targetDate;
  }
}
