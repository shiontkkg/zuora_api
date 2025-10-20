package com.example.zuora_api.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceDto {
  
  private String id;
  
  private String invoiceNumber;
  
  private LocalDate invoiceDate;
  
  private int amount;
  
  private int amountWithoutTax;
  
  private int taxAmount;
  
  private int paymentAmount;
  
  private int creditMemoAmount;
  
  private int balance;
  
  private List<InvoiceItemDto> invoiceItems;
}
