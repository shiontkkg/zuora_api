package com.example.zuora_api.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.InvoiceApi;
import com.example.zuora_api.dto.InvoiceDto;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InvoiceService {
  
  private final InvoiceApi invoiceApi;
  
  public List<InvoiceDto> getInvoicesByAccount(String accountKey) {
    return invoiceApi.getInvoicesByAccount(accountKey);
  }

  public InvoiceDto getInvoice(String invoiceNumber) {
    return invoiceApi.getInvoice(invoiceNumber);
  }

  public boolean delete(String invoiceId) {
    return invoiceApi.forceDeleteInvoice(invoiceId);
  }
}
