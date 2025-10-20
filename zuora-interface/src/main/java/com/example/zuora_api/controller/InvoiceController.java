package com.example.zuora_api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.zuora_api.dto.InvoiceDto;
import com.example.zuora_api.service.InvoiceService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("invoices")
@CrossOrigin
@AllArgsConstructor
public class InvoiceController {
  
  private final InvoiceService invoiceService;
  
  @GetMapping
  public List<InvoiceDto> list(@RequestParam("accountKey") String accountKey) {
    return invoiceService.getInvoicesByAccount(accountKey);
  }

  @GetMapping("/{invoiceNumber}")
  public InvoiceDto get(@PathVariable("invoiceNumber") String invoiceNumber) {
    return invoiceService.getInvoice(invoiceNumber);
  }

  @DeleteMapping("/{invoiceNumber}")
  public String delete(@PathVariable("invoiceNumber") String invoiceNumber) {
    
      invoiceService.delete(invoiceNumber);
      return "";
  }
}
