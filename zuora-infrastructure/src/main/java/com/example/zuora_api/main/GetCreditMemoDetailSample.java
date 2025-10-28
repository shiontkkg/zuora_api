package com.example.zuora_api.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.internal.LinkedTreeMap;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;
import io.github.cdimascio.dotenv.Dotenv;

public class GetCreditMemoDetailSample {

  record CreditMemoItemPartDto(String id, String name, int amount, int taxAmount) {
    @Override
    public String toString() {
      return "CreditMemoItemPart: %s 充当金額（税）: %d(%d)".formatted(name, amount, taxAmount);
    }
  }

  record InvoiceItemDto(
      String id,
      String taxId,
      String name,
      int amount,
      int taxAmount,
      int balance,
      int taxBalance,
      List<CreditMemoItemPartDto> creditMemoItemParts) {
    @Override
    public String toString() {
      var stringList = new ArrayList<String>();
      stringList.add(
          "InvoiceItem: %s 金額（税）: %d(%d) 残高（税）: %d(%d)"
              .formatted(name, amount, taxAmount, balance, taxBalance));
      creditMemoItemParts.forEach(p -> stringList.add("    " + p.toString()));
      return String.join("\n", stringList);
    }
  }

  record CreditMemoPartDto(
      String id, String invoiceNumber, int amount, List<InvoiceItemDto> invoiceItems) {
    @Override
    public String toString() {
      var stringList = new ArrayList<String>();
      stringList.add("CreditMemoPart: %d -> %s".formatted(amount, invoiceNumber));
      invoiceItems.forEach(i -> stringList.add("  " + i.toString()));
      return String.join("\n", stringList);
    }
  }

  public static void main(String[] args) throws ApiException {
    var dotenv = Dotenv.load();
    var zuoraClient =
        new ZuoraClient(
            dotenv.get("ZUORA_CLIENT_ID"),
            dotenv.get("ZUORA_CLIENT_SECRET"),
            dotenv.get("ZUORA_BASE_URL"));
    zuoraClient.initialize();

    // 確認対象のクレジットメモ
    var creditMemoNumber = "CM00000105";

    var creditMemoPartDtoList = new ArrayList<CreditMemoPartDto>();

    /*
     * 確認対象のクレジットメモを取得し、次で必要なクレジットメモのIDを得る。
     *
     * Retrieve a credit memo
     * https://developer.zuora.com/v1-api-reference/api/operation/queryCreditMemoByKey/
     */
    var creditMemo = zuoraClient.creditMemosApi().getCreditMemoApi(creditMemoNumber).execute();
    var creditMemoId = creditMemo.getId();

    /*
     * クレジットメモの項目および税項目を取得する。
     *
     * Object Queries: List credit memo items
     * https://developer.zuora.com/v1-api-reference/api/operation/queryCreditMemoItems/
     */
    var creditMemoItemsResponse =
        zuoraClient
            .objectQueriesApi()
            .queryCreditMemoItemsApi()
            .filter(List.of("creditmemoid.EQ:" + creditMemoId))
            .expand(List.of("credittaxationitems"))
            .execute();

    /*
     * クレジットメモの項目および税項目のIDから名前を取得できるよう、Mapを作成する。
     * ※Credit Taxation ItemはSDKのバグのためaddtionalPropertyから取得する必要がある。
     */
    var creditMemoItemIdToNameMap = new HashMap<String, String>();

    for (var creditMemoItem : creditMemoItemsResponse.getData()) {
      if (creditMemoItem.getAdditionalProperty("credittaxationitems")
              instanceof List creditTaxationItems
          && creditTaxationItems.get(0) instanceof LinkedTreeMap creditTaxationItem) {
        creditMemoItemIdToNameMap.put(creditMemoItem.getId(), creditMemoItem.getChargeName());
        creditMemoItemIdToNameMap.put(
            creditTaxationItem.get("id").toString(), creditTaxationItem.get("name").toString());
      }
    }

    /*
     * 確認対象のクレジットメモの要素（part）を取得する。
     *
     * List all parts of credit memo
     * https://developer.zuora.com/v1-api-reference/api/operation/GET_CreditMemoParts/
     */
    var creditMemoParts =
        zuoraClient.creditMemosApi().getCreditMemoPartsApi(creditMemoNumber).execute();

    /*
     * 各要素について、適用先の請求書とその適用詳細を作成する。
     */
    for (var creditMemoPart : creditMemoParts.getParts()) {

      /*
       * 適用先の請求書とその項目および税項目を取得する。
       *
       * Object Queries: List invoice items
       * https://developer.zuora.com/v1-api-reference/api/operation/queryInvoiceItems/
       */
      var invoiceItemsResponse =
          zuoraClient
              .objectQueriesApi()
              .queryInvoiceItemsApi()
              .filter(List.of("invoiceid.EQ:" + creditMemoPart.getInvoiceId()))
              .expand(List.of("invoice", "taxationitems"))
              .execute();

      /*
       * 未充当分もCredit Memo Partとして存在する模様。
       */
      if (invoiceItemsResponse.getData().isEmpty()) {
        var creditMemoPartDto =
            new CreditMemoPartDto(
                creditMemoPart.getId(),
                "未適用",
                creditMemoPart.getAmount().intValue(),
                new ArrayList<InvoiceItemDto>());
        System.out.println(creditMemoPartDto);
        continue;
      }

      var invoiceNumber = invoiceItemsResponse.getData().get(0).getInvoice().getInvoiceNumber();

      /*
       * クレジットメモ要素に、適用先の請求項目情報を追加する。
       */
      var creditMemoPartDto =
          new CreditMemoPartDto(
              creditMemoPart.getId(),
              invoiceNumber,
              creditMemoPart.getAmount().intValue(),
              new ArrayList<InvoiceItemDto>());

      for (var invoiceItem : invoiceItemsResponse.getData()) {
        var taxationItem = invoiceItem.getTaxationItems().get(0);
        creditMemoPartDto
            .invoiceItems()
            .add(
                new InvoiceItemDto(
                    invoiceItem.getId(),
                    taxationItem.getId(),
                    invoiceItem.getChargeName(),
                    invoiceItem.getChargeAmount().intValue(),
                    taxationItem.getTaxAmount().intValue(),
                    invoiceItem.getBalance().intValue(),
                    taxationItem.getBalance().intValue(),
                    new ArrayList<CreditMemoItemPartDto>()));
      }

      /*
       * クレジットメモ項目の要素（item part）を取得し、適用先の請求項目情報に紐づける。
       */

      /*
       * クレジットメモ項目の要素を取得する。
       * 他の項目と異なり、本体と税が分かれていない。
       *
       * List all credit memo part items
       * ※API Referenceに載っていない。
       *
       * Java client library referenceには載っている。
       * https://developer.zuora.com/sdk-references/java-sdk-reference/
       * GET /v1/credit-memos/{creditMemoKey}/parts/{partId}/item-parts
       *
       * オブジェクトモデルでは「Credit memo part item」と書かれているが、
       * 他オブジェクトの命名を見ると「Credit memo item part」が適切であると考えられる。
       */
      var creditMemoItemParts =
          zuoraClient
              .creditMemosApi()
              .getCreditMemoItemPartsApi(creditMemoPart.getId(), creditMemoNumber)
              .execute();

      for (var creditMemoItemPart : creditMemoItemParts.getItemParts()) {
        CreditMemoItemPartDto creditMemoItemPartDto = null;

        if (creditMemoItemPart.getCreditMemoItemId() != null) {
          creditMemoItemPartDto =
              new CreditMemoItemPartDto(
                  creditMemoItemPart.getId(),
                  creditMemoItemIdToNameMap.get(creditMemoItemPart.getCreditMemoItemId()),
                  creditMemoItemPart.getAmount().intValue(),
                  0);
        } else {
          creditMemoItemPartDto =
              new CreditMemoItemPartDto(
                  creditMemoItemPart.getId(),
                  creditMemoItemIdToNameMap.get(creditMemoItemPart.getCreditTaxItemId()),
                  0,
                  creditMemoItemPart.getAmount().intValue());
        }

        /*
         * 適用先の請求項目に、クレジットメモ項目の要素を追加する。
         */
        if (creditMemoItemPart.getInvoiceItemId() != null) {
          for (var invoiceItemDto : creditMemoPartDto.invoiceItems()) {
            if (creditMemoItemPart.getInvoiceItemId().equals(invoiceItemDto.id())) {
              invoiceItemDto.creditMemoItemParts().add(creditMemoItemPartDto);
              break;
            }
          }
        } else {
          for (var invoiceItemDto : creditMemoPartDto.invoiceItems()) {
            if (creditMemoItemPart.getTaxItemId().equals(invoiceItemDto.taxId())) {
              invoiceItemDto.creditMemoItemParts().add(creditMemoItemPartDto);
              break;
            }
          }
        }
      }
      System.out.println(creditMemoPartDto);
    }
  }
}
