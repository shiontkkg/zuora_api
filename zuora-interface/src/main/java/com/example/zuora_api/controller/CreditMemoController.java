package com.example.zuora_api.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.zuora_api.command.CreateCreditMemoCommand;
import com.example.zuora_api.dto.CreditMemoDto;
import com.example.zuora_api.request.CreateCreditMemoRequest;
import com.example.zuora_api.service.CreditMemoService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("credit-memos")
@CrossOrigin
@AllArgsConstructor
public class CreditMemoController {
  
  private final CreditMemoService creditMemoService;

  @GetMapping
  public List<CreditMemoDto> list(@RequestParam("accountKey") String accountKey) {
    return creditMemoService.getCreditMemosByAccount(accountKey);
  }

  @GetMapping("/{creditMemoKey}")
  public CreditMemoDto get(@PathVariable("creditMemoKey") String creditMemoKey) {
    return creditMemoService.get(creditMemoKey);
  }

  @DeleteMapping("/{creditMemoKey}")
  public String delete(@PathVariable("creditMemoKey") String creditMemoKey) {
    creditMemoService.delete(creditMemoKey);
    return "";
  }
  
  @PostMapping("/invoices/{invoiceKey}")
  public String createFromInvoice(@PathVariable("invoiceKey") String invoiceKey, @RequestBody CreateCreditMemoRequest request) {
    
    var items = new ArrayList<CreateCreditMemoCommand.Item>();
    Arrays.stream(request.getItems()).forEach(i -> {
      items.add(CreateCreditMemoCommand.Item.builder()
          .amount(i.getAmount()).
          invoiceItemId(i.getInvoiceItemId()).
          skuName(i.getSkuName()).
          build());
    });
    
    var command = CreateCreditMemoCommand.builder()
        .invoiceId(request.getInvoiceId())
        .invoiceNumber(invoiceKey)
        .effectiveDate(LocalDate.parse(request.getEffectiveDate()))
        .items(items).build();
    
    return creditMemoService.createFromInvoice(command);
  }
}
