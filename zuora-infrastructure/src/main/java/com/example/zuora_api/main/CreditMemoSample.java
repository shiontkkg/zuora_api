package com.example.zuora_api.main;

import java.util.ArrayList;
import java.util.List;
import com.example.zuora_api.dto.CreditMemoDto;
import com.example.zuora_api.dto.CreditMemoItemDto;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;

public class CreditMemoSample {

  public static void main(String[] args) throws ApiException {
    var zuoraClient =
        new ZuoraClient(
            "a2581d34-150e-400e-8f94-8e0f7f0925d0",
            "rx=xPiDt9kvxQV6CVLgsU9tpefR6BEn6cVbhIrxz",
            "https://rest.apisandbox.zuora.com");
    zuoraClient.initialize();

    var creditMemoKey = "CM00000026";

    var response =
        zuoraClient
            .objectQueriesApi()
            .queryCreditMemoByKeyApi(creditMemoKey)
            .expand(List.of("creditmemoitems.subscription"))
            .execute();

    var creditMemoItemDtoList = new ArrayList<CreditMemoItemDto>();

    // クレジットメモ
    System.out.println("""
            %s (%s)
            """.formatted(response.getMemoNumber(), response.getId())); 
    
    for (var creditMemoItem : response.getCreditMemoItems()) {
      var creditMemoItemDto =
          CreditMemoItemDto.builder()
              .id(creditMemoItem.getId())
              .chargeName(creditMemoItem.getChargeName())
              .amount(creditMemoItem.getAmount().intValue())
              .appliedAmount(creditMemoItem.getAppliedToOthersAmount().intValue())
              .refundAmount(creditMemoItem.getBeAppliedByOthersAmount().intValue())
              .unappliedAmount(creditMemoItem.getUnappliedAmount().intValue())
              .subscriptionNumber(creditMemoItem.getSubscription().getName())
              .build();
      creditMemoItemDtoList.add(creditMemoItemDto);

      // クレジットメモ項目
      System.out.println(
          """
              CreditMemoItem: %s (%s): %d /%d
              """
              .formatted(
                  creditMemoItem.getChargeName(),
                  creditMemoItem.getId(),
                  creditMemoItem.getUnappliedAmount().intValue(),
                  creditMemoItem.getAmount().intValue()));

      var creditFromItemId = creditMemoItem.getCreditFromItemId();

      var invoiceItem =
          zuoraClient
              .objectQueriesApi()
              .queryInvoiceItemByKeyApi(creditFromItemId)
              .expand(List.of("invoice"))
              .execute();
      var sourceInvoiceItemChargeAmount = invoiceItem.getChargeAmount().intValue();
      var sourceInvoiceItemChargeName = invoiceItem.getChargeName();
      var sourceInvoiceNumber = invoiceItem.getInvoice().getInvoiceNumber();
      System.out.println(
          """
              %s (%s) - %s (%s) - %d /%d
              """
              .formatted(
                  sourceInvoiceNumber,
                  invoiceItem.getInvoiceId(),
                  sourceInvoiceItemChargeName,
                  invoiceItem.getId(),
                  invoiceItem.getBalance().intValue(),
                  sourceInvoiceItemChargeAmount));

      System.out.println();
    }

    var creditMemoDto =
        CreditMemoDto.builder()
            .id(response.getId())
            .memoNumber(response.getMemoNumber())
            .memoDate(response.getMemoDate())
            .amount(response.getTotalAmount().intValue())
            .taxAmount(response.getTaxAmount().intValue())
            .unappliedAmount(response.getBalance().intValue())
            .refundAmount(response.getRefundAmount().intValue())
            .appliedAmount(response.getAppliedAmount().intValue())
            .creditMemoItems(creditMemoItemDtoList)
            .build();

    System.out.println(creditMemoDto);
  }
}
