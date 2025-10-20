package com.example.zuora_api.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreviewChangePlanOrderDto {
  
  private Invoice invoice;
  
  private CreditMemo creditMemo;
  
  @Data
  @Builder
  public static class Invoice {
    private int amountWithoutTax;
    private int taxAmount;
    private int amount;
    private List<InvoiceItem> invoiceItems;
    
    @Data
    @Builder
    public static class InvoiceItem {
      private String chargeName;
      private LocalDate serviceStartDate;
      private LocalDate serviceEndDate;
      private int period;
      private int amountWithoutTax;
    }
  }
  
  @Data
  @Builder
  public static class CreditMemo {
    private int amountWithoutTax;
    private int taxAmount;
    private int amount;
    private List<CreditMemoItem> creditMemoItems;
    
    @Data
    @Builder
    public static class CreditMemoItem {
      private String chargeName;
      private LocalDate serviceStartDate;
      private LocalDate serviceEndDate;
      private int period;
      private int amountWithoutTax;
    }
  }

}
