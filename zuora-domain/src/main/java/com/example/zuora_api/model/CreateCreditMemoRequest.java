package com.example.zuora_api.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
public class CreateCreditMemoRequest {

  private String invoiceId;

  private String invoiceNumber;

  private boolean autoApplyToInvoiceUponPosting;

  private boolean autoPost;

  private LocalDate effectiveDate;

  private List<Item> items;

  @Data
  @AllArgsConstructor
  public static class Item {

    private int amount;

    private String invoiceItemId;

    private String skuName;
  }

  public CreateCreditMemoRequest(
      String invoiceId, String invoiceNumber, LocalDate effectiveDate, List<Item> items) {
    this.invoiceId = invoiceId;
    this.invoiceNumber = invoiceNumber;
    this.autoApplyToInvoiceUponPosting = true;
    this.autoPost = true;
    this.effectiveDate = effectiveDate;
    this.items = items;
  }
}
