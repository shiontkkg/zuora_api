package com.example.zuora_api.api;

import java.util.List;
import com.example.zuora_api.dto.InvoiceDto;

public interface InvoiceApi {
  
  public List<InvoiceDto> getInvoicesByAccount(String accountKey);
  
  public InvoiceDto getInvoice(String invoiceNumber);
  
  public boolean forceDeleteInvoice(String invoiceId);
}
