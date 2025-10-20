package com.example.zuora_api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.zuora_api.dto.CreditMemoDto;
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
}
