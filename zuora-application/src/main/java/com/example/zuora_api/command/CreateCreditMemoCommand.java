package com.example.zuora_api.command;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCreditMemoCommand {
  
  private String invoiceId;
  
  private String invoiceNumber;

  private LocalDate effectiveDate;

  private List<Item> items;

  @Data
  @Builder
  public static class Item {

    private int amount;

    private String invoiceItemId;

    private String skuName;
  }
}
