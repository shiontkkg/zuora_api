package com.example.zuora_api.api.zuora;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.CreditMemoApi;
import com.example.zuora_api.api.zuora.mapper.ZuoraCreditMemoMapper;
import com.example.zuora_api.dto.CreditMemoDto;
import com.example.zuora_api.exception.ExternalApiException;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;
import com.zuora.model.BillingDocumentStatus;
import com.zuora.model.UnapplyCreditMemoRequest;
import com.zuora.model.UnapplyCreditMemoToInvoice;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ZuoraCreditMemoApi implements CreditMemoApi {

  private final ZuoraClient zuoraClient;
  
  private final ZuoraCreditMemoMapper creditMemoMapper;

  public List<CreditMemoDto> getCreditMemosByAccount(String accountKey) {

    List<CreditMemoDto> creditMemos = new ArrayList<>();

    try {
      var accountId = accountKey;
      if (StringUtils.length(accountKey) != 32) {
        var account = zuoraClient.accountsApi().getAccountApi(accountKey).execute();
        accountId = account.getBasicInfo().getId();
      }

      var response =
          zuoraClient
              .objectQueriesApi()
              .queryCreditMemosApi()
              .filter(List.of("accountid.EQ:" + accountId))
              .expand(List.of("creditmemoitems.subscription"))
              .execute();

      for (var creditMemo : response.getData()) {
        var creditMemoDto = creditMemoMapper.toDto(creditMemo);
        creditMemos.add(creditMemoDto);
      }

    } catch (Exception e) {
      log.error("Error fetching credit memos for account: {}", accountKey, e);
      throw new RuntimeException("Failed to fetch credit memos", e);
    }

    return creditMemos;
  }

  public CreditMemoDto get(String creditMemoKey) {

    CreditMemoDto creditMemoDto = null;

    try {
      var response =
          zuoraClient
              .objectQueriesApi()
              .queryCreditMemoByKeyApi(creditMemoKey)
              .expand(List.of("creditmemoitems.subscription"))
              .execute();
      creditMemoDto = creditMemoMapper.toDto(response);

    } catch (Exception e) {
      log.error("Error fetching credit memo with key: {}", creditMemoKey, e);
      throw new RuntimeException("Failed to fetch credit memo", e);
    }

    return creditMemoDto;
  }

  public boolean forceDeleteCreditMemo(String creditMemoKey) {

    try {
      var creditMemo = zuoraClient.creditMemosApi().getCreditMemoApi(creditMemoKey).execute();

      // クレジットメモの適用を全て解除する
      var creditMemoParts =
          zuoraClient.creditMemosApi().getCreditMemoPartsApi(creditMemoKey).execute();

      var unapplyCreditMemoToInvoiceList = new ArrayList<UnapplyCreditMemoToInvoice>();
      for (var creditMemoPart : creditMemoParts.getParts()) {
        if (creditMemoPart.getInvoiceId() != null) {
          unapplyCreditMemoToInvoiceList.add(
              new UnapplyCreditMemoToInvoice()
              .invoiceId(creditMemoPart.getInvoiceId())
              .amount(creditMemoPart.getAmount()));
        }
      }

      if (0 < unapplyCreditMemoToInvoiceList.size()) {
        var unapplyCreditMemoRequest =
            new UnapplyCreditMemoRequest()
                .effectiveDate(creditMemo.getCreditMemoDate())
                .invoices(unapplyCreditMemoToInvoiceList);
        zuoraClient
            .creditMemosApi()
            .unapplyCreditMemoApi(creditMemoKey, unapplyCreditMemoRequest)
            .execute();
      }

      if (BillingDocumentStatus.POSTED.equals(creditMemo.getStatus())) {
        zuoraClient.creditMemosApi().unpostCreditMemoApi(creditMemoKey).execute();
        zuoraClient.creditMemosApi().cancelCreditMemoApi(creditMemoKey).execute();
        zuoraClient.creditMemosApi().deleteCreditMemoApi(creditMemoKey).execute();
      } else if (BillingDocumentStatus.DRAFT.equals(creditMemo.getStatus())) {
        zuoraClient.creditMemosApi().cancelCreditMemoApi(creditMemoKey).execute();
        zuoraClient.creditMemosApi().deleteCreditMemoApi(creditMemoKey).execute();
      } else if (BillingDocumentStatus.CANCELED.equals(creditMemo.getStatus())) {
        zuoraClient.creditMemosApi().deleteCreditMemoApi(creditMemoKey).execute();
      }

    } catch (ApiException e) {
      log.error(
          """
              クレジットメモの削除に失敗しました。%s
              """
              .formatted(e.getMessage()));
      throw new ExternalApiException(e.getErrorObject().getReasons().get(0).getMessage(), e.getCode());
    }

    return true;
  }
}
