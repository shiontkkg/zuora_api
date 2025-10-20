package com.example.zuora_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.zuora_api.dto.PreviewChangePlanOrderDto;
import com.example.zuora_api.request.GenerateBillingDocumentsRequest;
import com.example.zuora_api.service.AccountService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/accounts")
@CrossOrigin
@AllArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping("/preview")
  public PreviewChangePlanOrderDto previewBillingDocumentsByAccount(
      @RequestBody GenerateBillingDocumentsRequest request) {
    return this.accountService.previewBillingDocumentsByAccount(
        request.getAccountNumber(), request.getTargetDate());
  }

  @PostMapping("/bill")
  public Integer generateBillingDocumentsByAccount(
      @RequestBody GenerateBillingDocumentsRequest request) {
    return this.accountService.generateBillingDocumentsByAccount(
        request.getAccountNumber(), request.getDocumentDate(), request.getTargetDate());
  }
}
