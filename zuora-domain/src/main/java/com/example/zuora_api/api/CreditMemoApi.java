package com.example.zuora_api.api;

import java.util.List;
import com.example.zuora_api.dto.CreditMemoDto;
import com.example.zuora_api.model.CreateCreditMemoRequest;

public interface CreditMemoApi {
  
  public List<CreditMemoDto> getCreditMemosByAccount(String accountKey);
  
  public CreditMemoDto get(String creditMemoKey);
  
  public boolean forceDeleteCreditMemo(String creditMemoKey);
  
  public String createFromInvoice(CreateCreditMemoRequest request);
}
