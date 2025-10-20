package com.example.zuora_api.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.CreditMemoApi;
import com.example.zuora_api.dto.CreditMemoDto;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreditMemoService {
  
  private final CreditMemoApi creditMemoApi;

  public List<CreditMemoDto> getCreditMemosByAccount(String accountId) {
    return creditMemoApi.getCreditMemosByAccount(accountId);
  }

  public CreditMemoDto get(String creditMemoKey) {
    return creditMemoApi.get(creditMemoKey);
  }

  public boolean delete(String creditMemoKey) {
    return creditMemoApi.forceDeleteCreditMemo(creditMemoKey);
  }
}
