package com.example.zuora_api.main;

import java.util.List;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;

public class CreditMemoItemSample {

  public static void main(String[] args) throws ApiException {
    var zuoraClient =
        new ZuoraClient(
            "a2581d34-150e-400e-8f94-8e0f7f0925d0",
            "rx=xPiDt9kvxQV6CVLgsU9tpefR6BEn6cVbhIrxz",
            "https://rest.apisandbox.zuora.com");
    zuoraClient.initialize();

    var creditMemoKey = "CM00000027";

    var creditMemoItems =
        zuoraClient.creditMemosApi().getCreditMemoItemsApi(creditMemoKey).execute();

    for (var creditMemoItem : creditMemoItems.getItems()) {
      // InvoiceItem -> Invoiceを直接取得できないので、Object Queryを使用
      var invoiceItemId = creditMemoItem.getCreditFromItemId(); // InvoiceItem
      var invoiceItem =
          zuoraClient
              .objectQueriesApi()
              .queryInvoiceItemByKeyApi(invoiceItemId)
              .expand(List.of("invoice"))
              .execute();
      var invoiceId = invoiceItem.getInvoiceId();

      var taxationItems =
          zuoraClient
              .invoicesApi()
              .getTaxationItemsOfInvoiceItemApi(invoiceId, invoiceItemId)
              .execute();
      var taxationItem = taxationItems.getData().get(0);

      System.out.println(
          """
              Invoice: %s"""
              .formatted(invoiceId));

      System.out.println(
          """
          CreditMemoItem: %s (%s): %d /%d"""
              .formatted(
                  creditMemoItem.getSkuName(),
                  creditMemoItem.getId(),
                  creditMemoItem.getUnappliedAmount().intValue(),
                  creditMemoItem.getAmount().intValue()));
      System.out.println(
          """
          InvoiceItem: %s"""
              .formatted(invoiceItemId));

      var cmtaxitem = creditMemoItem.getTaxationItems().getData().get(0);
      System.out.println(
          """
          CreditTaxItem: %s (%s): %d /%d"""
              .formatted(
                  cmtaxitem.getTaxCode(),
                  cmtaxitem.getId(),
                  cmtaxitem.getUnappliedAmount().intValue(),
                  cmtaxitem.getTaxAmount().intValue()));
      System.out.println(
          """
          TaxItem: %s"""
              .formatted(taxationItem.getId()));
    }
  }
}
