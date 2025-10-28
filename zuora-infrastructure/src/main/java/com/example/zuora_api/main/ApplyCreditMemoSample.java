package com.example.zuora_api.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.internal.LinkedTreeMap;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;
import com.zuora.model.ApplyCreditMemoItemToInvoiceItem;
import com.zuora.model.ApplyCreditMemoRequest;
import com.zuora.model.ApplyCreditMemoToInvoice;
import io.github.cdimascio.dotenv.Dotenv;

public class ApplyCreditMemoSample {

  public static void main(String[] args) throws ApiException {
    var dotenv = Dotenv.load();
    var zuoraClient =
        new ZuoraClient(
            dotenv.get("ZUORA_CLIENT_ID"),
            dotenv.get("ZUORA_CLIENT_SECRET"),
            dotenv.get("ZUORA_BASE_URL"));
    zuoraClient.initialize();

    // 適用対象のクレジットメモ
    var creditMemoNumber = "CM00000103";
    // var creditMemoId = "fa60bfb508c99f46ee6a16d8c85eabe6";

    /*
     * 「Retrieve a credit memo」でクレジットメモを取得する。
     * https://developer.zuora.com/v1-api-reference/api/operation/queryCreditMemoByKey/
     */
    var creditMemo = zuoraClient.creditMemosApi().getCreditMemoApi(creditMemoNumber).execute();
    var creditMemoId = creditMemo.getId();
    var referredInvoiceId = creditMemo.getReferredInvoiceId();

    /*
     * 「List credit memo items」で関連データを取得する。
     * https://developer.zuora.com/v1-api-reference/api/operation/queryCreditMemoItems/
     */
    var creditMemoItemsResponse =
        zuoraClient
            .objectQueriesApi()
            .queryCreditMemoItemsApi()
            .filter(List.of("creditmemoid.EQ:" + creditMemoId))
            .expand(List.of("credittaxationitems"))
            .execute();

    // 「Apply a credit memo」用のデータ
    var applyCreditMemoToInvoiceList = new ArrayList<ApplyCreditMemoToInvoice>();

    for (var creditMemoItem : creditMemoItemsResponse.getData()) {
      /*
       * amount: 残高。amountWithoutTaxもあるが、外税の場合は同じ。
       * invoiceItemId: おそらくsourceItemTypeがInvoiceDetailの場合はcreditFromItemIdと同じ
       */
      System.out.println(
          """
              id: %s
              amount: %d
              unappliedAmount: %d
              invoiceItemId: %s
              """
              .formatted(
                  creditMemoItem.getId(),
                  creditMemoItem.getAmount().intValue(),
                  creditMemoItem.getUnappliedAmount().intValue(),
                  creditMemoItem.getInvoiceItemId()));
      var applyCreditMemoItem =
          new ApplyCreditMemoItemToInvoiceItem()
              .creditMemoItemId(creditMemoItem.getId())
              .invoiceItemId(creditMemoItem.getInvoiceItemId())
              .amount(creditMemoItem.getUnappliedAmount());

      /*
       * 本来はcreditMemoItem.getCreditTaxationItems()でCreditTaxationItemsを取得できるはずだが、
       * おそらくSDKのバグ（ExpandedCreditMemoItemにおいて、@SerializedNameが
       * "creditTaxationItems"ではなく"creditTaxationItems"）でnullになってしまう。
       * 代わりにaddtionalPropertyに格納されているので、こちらから取得する。
       */
      var obj = creditMemoItem.getAdditionalProperty("credittaxationitems");
      if (obj instanceof List creditTaxationItems
          && creditTaxationItems.get(0) instanceof LinkedTreeMap creditTaxationItem) {
        System.out.println(
            """
            id: %s
            taxAmount: %d
            unappliedAmount: %d
            taxationItemId: %s
            """
                .formatted(
                    creditTaxationItem.get("id"),
                    ((Double) creditTaxationItem.get("taxAmount")).intValue(),
                    ((Double) creditTaxationItem.get("unappliedAmount")).intValue(),
                    creditTaxationItem.get("taxationItemId")));
        var applyCreditTaxItem =
            new ApplyCreditMemoItemToInvoiceItem()
                .creditTaxItemId(creditTaxationItem.get("id").toString())
                .taxItemId(creditTaxationItem.get("taxationItemId").toString())
                .amount(new BigDecimal(creditTaxationItem.get("unappliedAmount").toString()));

        applyCreditMemoToInvoiceList.add(
            new ApplyCreditMemoToInvoice()
                .invoiceId(referredInvoiceId)
                .amount(applyCreditMemoItem.getAmount().add(applyCreditTaxItem.getAmount()))
                .items(List.of(applyCreditMemoItem, applyCreditTaxItem)));
      }
    }

    var applyCreditMemoRequest =
        new ApplyCreditMemoRequest()
            // .effectiveDate(LocalDate.now())
            .invoices(applyCreditMemoToInvoiceList);
    /*
     * 「Apply a credit memo」でクレジットメモを適用する。
     * https://developer.zuora.com/v1-api-reference/api/operation/PUT_ApplyCreditMemo/
     * 適用後のクレジットメモが返却される。
     */
    var response =
        zuoraClient
            .creditMemosApi()
            .applyCreditMemoApi(creditMemoNumber, applyCreditMemoRequest)
            .execute();

    System.out.println(response);
  }
}
