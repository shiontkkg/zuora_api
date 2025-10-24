package com.example.zuora_api.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.zuora_api.api.CreditMemoApi;
import com.example.zuora_api.command.CreateCreditMemoCommand;
import com.example.zuora_api.dto.CreditMemoDto;
import com.example.zuora_api.model.CreateCreditMemoRequest;
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

  public String createFromInvoice(CreateCreditMemoCommand command) {

    var items = new ArrayList<CreateCreditMemoRequest.Item>();
    command
        .getItems()
        .forEach(
            i -> {
              var item =
                  new CreateCreditMemoRequest.Item(
                      i.getAmount(), i.getInvoiceItemId(), i.getSkuName());
              items.add(item);
            });

    var request =
        new CreateCreditMemoRequest(
            command.getInvoiceId(), command.getInvoiceNumber(), command.getEffectiveDate(), items);

    return creditMemoApi.createFromInvoice(request);
  }
}
