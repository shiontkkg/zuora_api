package com.example.zuora_api.api.zuora;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.InvoiceApi;
import com.example.zuora_api.api.zuora.mapper.ZuoraInvoiceMapper;
import com.example.zuora_api.config.ZuoraV2Client;
import com.example.zuora_api.dto.InvoiceDto;
import com.example.zuora_api.exception.ExternalApiException;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;
import com.zuora.model.BillingDocumentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ZuoraInvoiceApi implements InvoiceApi {

  private final ZuoraClient zuoraClient;

  private final ZuoraV2Client zuoraV2Client;

  private final ZuoraInvoiceMapper invoiceMapper;

  public List<InvoiceDto> getInvoicesByAccount(String accountKey) {

    var invoices = new ArrayList<InvoiceDto>();

    try {
      var accountId = accountKey;
      if (StringUtils.length(accountKey) != 32) {
        var account = zuoraClient.accountsApi().getAccountApi(accountKey).execute();
        accountId = account.getBasicInfo().getId();
      }

      var response =
          zuoraClient
              .objectQueriesApi()
              .queryInvoicesApi()
              .filter(List.of("accountid.EQ:" + accountId))
              .expand(List.of("invoiceitems"))
              .execute();

      for (var invoice : response.getData()) {
        var invoiceDto = invoiceMapper.toDto(invoice);
        invoices.add(invoiceDto);
      }

    } catch (ApiException e) {
      e.printStackTrace();
    }

    return invoices;
  }

  @Override
  public InvoiceDto getInvoice(String invoiceId) {

    try {
      var invoice = zuoraClient.invoicesApi().getInvoiceApi(invoiceId).execute();
      var invoiceItems = zuoraClient.invoicesApi().getInvoiceItemsApi(invoiceId).execute();
      
      return invoiceMapper.toDto(invoice, invoiceItems);

    } catch (ApiException e) {
      log.error(
          """
              請求書の取得に失敗しました。%s
              """
              .formatted(e.getMessage()));
      throw new ExternalApiException(e.getErrorObject().getReasons().get(0).getMessage(), e.getCode());
    }
  }

  public boolean forceDeleteInvoice(String invoiceId) {

    try {
      var invoice = zuoraClient.invoicesApi().getInvoiceApi(invoiceId).execute();

      if (BillingDocumentStatus.POSTED.equals(invoice.getStatus())) {
        // 確定済みをドラフトに戻すAPIがv1に見付からないので、Quickstart APIを用いる
        zuoraV2Client.invoices().unpostInvoice(invoiceId);
        zuoraClient.invoicesApi().cancelInvoiceApi(invoiceId).execute();
        zuoraClient.invoicesApi().deleteInvoiceApi(invoiceId).execute();
      } else if (BillingDocumentStatus.DRAFT.equals(invoice.getStatus())) {
        zuoraClient.invoicesApi().cancelInvoiceApi(invoiceId).execute();
        zuoraClient.invoicesApi().deleteInvoiceApi(invoiceId).execute();
      } else if (BillingDocumentStatus.CANCELED.equals(invoice.getStatus())) {
        zuoraClient.invoicesApi().deleteInvoiceApi(invoiceId).execute();
      }

    } catch (ApiException e) {
      log.error(
          """
              請求書の削除に失敗しました。%s
              """
              .formatted(e.getMessage()));
      throw new ExternalApiException(e.getErrorObject().getReasons().get(0).getMessage(), e.getCode());

    } catch (org.openapitools.client.ApiException e) {
      log.error(
          """
              請求書の削除に失敗しました。%s
              """
              .formatted(e.getMessage()));
      throw new ExternalApiException(e.getErrorObject().getErrors().get(0).getMessage(), e.getCode());
    }

    return true;
  }
}
