package com.example.zuora_api.api.zuora;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.ChangePlanOrderApi;
import com.example.zuora_api.api.zuora.builder.CreateChangePlanOrderRequestBuilder;
import com.example.zuora_api.api.zuora.builder.PreviewChangePlanOrderRequestBuilder;
import com.example.zuora_api.dto.PreviewChangePlanOrderDto;
import com.example.zuora_api.model.ChangePlanDowngradeOrderRequest;
import com.example.zuora_api.model.ChangePlanUpgradeOrderRequest;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;
import com.zuora.model.ApplyCreditMemoItemToInvoiceItem;
import com.zuora.model.ApplyCreditMemoRequest;
import com.zuora.model.ApplyCreditMemoToInvoice;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ZuoraChangePlanOrderApi implements ChangePlanOrderApi {

  private final ZuoraClient zuoraClient;

  private final PreviewChangePlanOrderRequestBuilder previewChangePlanOrderRequestBuilder;

  private final CreateChangePlanOrderRequestBuilder createChangePlanOrderRequestBuilder;

  public PreviewChangePlanOrderDto previewChangePlanUpgrade(
      ChangePlanUpgradeOrderRequest domainRequest) {

    var request = previewChangePlanOrderRequestBuilder.createUpgrade(domainRequest);
    PreviewChangePlanOrderDto preview = null;

    try {
      var response = zuoraClient.ordersApi().previewOrderApi(request).execute();

      // 今のところ請求書は1つだけしか作成されないようリクエストしているので、最初だけ取得している。
      var invoice = response.getPreviewResult().getInvoices().get(0);

      var invoiceItems = new ArrayList<PreviewChangePlanOrderDto.Invoice.InvoiceItem>();
      invoice
          .getInvoiceItems()
          .forEach(
              i -> {
                var period =
                    Period.between(i.getServiceStartDate(), i.getServiceEndDate().plusDays(1));
                var invoiceItem =
                    PreviewChangePlanOrderDto.Invoice.InvoiceItem.builder()
                        .chargeName(i.getChargeName())
                        // .quantity(i.getAdditionalInfo().getQuantity().intValue())
                        .serviceStartDate(i.getServiceStartDate())
                        .serviceEndDate(i.getServiceEndDate())
                        .period(period.getMonths() + period.getYears() * 12)
                        .amountWithoutTax(i.getAmountWithoutTax().intValue())
                        .build();
                invoiceItems.add(invoiceItem);
              });
      var invoiceDto =
          PreviewChangePlanOrderDto.Invoice.builder()
              .amountWithoutTax(invoice.getAmountWithoutTax().intValue())
              .taxAmount(invoice.getTaxAmount().intValue())
              .amount(invoice.getAmount().intValue())
              .invoiceItems(invoiceItems)
              .build();

      preview = PreviewChangePlanOrderDto.builder().invoice(invoiceDto).build();

    } catch (ApiException e) {
      e.printStackTrace();
    }

    return preview;
  }

  public PreviewChangePlanOrderDto previewChangePlanDowngrade(
      ChangePlanDowngradeOrderRequest domainRequest) {

    var request = previewChangePlanOrderRequestBuilder.createDowngrade(domainRequest);
    var preview = PreviewChangePlanOrderDto.builder().build();

    try {
      var response = zuoraClient.ordersApi().previewOrderApi(request).execute();

      if (response.getPreviewResult().getInvoices() != null) {
        // 今のところ請求書は1つだけしか作成されないようリクエストしているので、最初だけ取得している。
        var invoice = response.getPreviewResult().getInvoices().get(0);
        
        var invoiceItems = new ArrayList<PreviewChangePlanOrderDto.Invoice.InvoiceItem>();
        invoice
        .getInvoiceItems()
        .forEach(
            i -> {
              var period =
                  Period.between(i.getServiceStartDate(), i.getServiceEndDate().plusDays(1));
              var invoiceItem =
                  PreviewChangePlanOrderDto.Invoice.InvoiceItem.builder()
                  .chargeName(i.getChargeName())
                  // .quantity(i.getAdditionalInfo().getQuantity().intValue())
                  .serviceStartDate(i.getServiceStartDate())
                  .serviceEndDate(i.getServiceEndDate())
                  .period(period.getMonths() + period.getYears() * 12)
                  .amountWithoutTax(i.getAmountWithoutTax().intValue())
                  .build();
              invoiceItems.add(invoiceItem);
            });
        var invoiceDto =
            PreviewChangePlanOrderDto.Invoice.builder()
            .amountWithoutTax(invoice.getAmountWithoutTax().intValue())
            .taxAmount(invoice.getTaxAmount().intValue())
            .amount(invoice.getAmount().intValue())
            .invoiceItems(invoiceItems)
            .build();
        preview.setInvoice(invoiceDto);
      }

      // クレジットメモ
      if (response.getPreviewResult().getCreditMemos() != null) {
        var creditMemo = response.getPreviewResult().getCreditMemos().get(0);
        var creditMemoItems = new ArrayList<PreviewChangePlanOrderDto.CreditMemo.CreditMemoItem>();
        creditMemo
            .getCreditMemoItems()
            .forEach(
                cm -> {
                  var period =
                      Period.between(cm.getServiceStartDate(), cm.getServiceEndDate().plusDays(1));
                  var creditMemoItem =
                      PreviewChangePlanOrderDto.CreditMemo.CreditMemoItem.builder()
                          .chargeName(cm.getChargeName())
                          .serviceStartDate(cm.getServiceStartDate())
                          .serviceEndDate(cm.getServiceEndDate())
                          .period(period.getMonths() + period.getYears() * 12)
                          .amountWithoutTax(cm.getAmountWithoutTax().intValue())
                          .build();
                  creditMemoItems.add(creditMemoItem);
                });
        var creditMemoDto =
            PreviewChangePlanOrderDto.CreditMemo.builder()
                .amountWithoutTax(creditMemo.getAmountWithoutTax().intValue())
                .taxAmount(creditMemo.getTaxAmount().intValue())
                .amount(creditMemo.getAmount().intValue())
                .creditMemoItems(creditMemoItems)
                .build();
        preview.setCreditMemo(creditMemoDto);
      }

    } catch (ApiException e) {
      e.printStackTrace();
    }

    return preview;
  }

  public String changePlanUpgrade(ChangePlanUpgradeOrderRequest domainRequest) {

    var request = createChangePlanOrderRequestBuilder.createUpgrade(domainRequest);
    var orderNumber = "";

    try {
      var response = zuoraClient.ordersApi().createOrderApi(request).execute();
      orderNumber = response.getOrderNumber();

    } catch (ApiException e) {
      e.printStackTrace();
    }

    return orderNumber;
  }

  public String changePlanDowngrade(ChangePlanDowngradeOrderRequest domainRequest) {

    var request = createChangePlanOrderRequestBuilder.createDowngrade(domainRequest);
    var orderNumber = "";

    try {
      var response = zuoraClient.ordersApi().createOrderApi(request).execute();
      orderNumber = response.getOrderNumber();

      if (domainRequest.isApplyCreditToSourceInvoice() && 0 < response.getCreditMemoNumbers().size()) {
        // 発生元の請求書にクレジットメモを適用する
        var creditMemoNumber = response.getCreditMemoNumbers().get(0);

        var creditMemoItems =
            zuoraClient.creditMemosApi().getCreditMemoItemsApi(creditMemoNumber).execute();

        for (var creditMemoItem : creditMemoItems.getItems()) {
          var creditTaxItem = creditMemoItem.getTaxationItems().getData().get(0);

          // クレジットメモ項目の発生元である請求項目を取得する
          var sourceInvoiceItemId = creditMemoItem.getCreditFromItemId();
          // InvoiceItem -> Invoiceを直接取得できないので、Object Queryを使用
          var sourceInvoiceItem =
              zuoraClient
                  .objectQueriesApi()
                  .queryInvoiceItemByKeyApi(sourceInvoiceItemId)
                  .expand(List.of("invoice"))
                  .execute();
          var sourceInvoiceId = sourceInvoiceItem.getInvoiceId();
          var sourceTaxationItems =
              zuoraClient
                  .invoicesApi()
                  .getTaxationItemsOfInvoiceItemApi(sourceInvoiceId, sourceInvoiceItemId)
                  .execute();
          var sourceTaxationItem = sourceTaxationItems.getData().get(0);

          // クレジットメモを発生元の請求書に適用するリクエストを作成する
          var applyCreditMemoItem =
              new ApplyCreditMemoItemToInvoiceItem()
                  .creditMemoItemId(creditMemoItem.getId())
                  .invoiceItemId(sourceInvoiceItemId)
                  .amount(creditMemoItem.getUnappliedAmount());
          var applyCreditTaxItem =
              new ApplyCreditMemoItemToInvoiceItem()
                  .creditTaxItemId(creditTaxItem.getId())
                  .taxItemId(sourceTaxationItem.getId())
                  .amount(creditTaxItem.getUnappliedAmount());
          var applyCreditMemoToInvoice =
              new ApplyCreditMemoToInvoice()
                  .invoiceId(sourceInvoiceId)
                  .amount(
                      creditMemoItem.getUnappliedAmount().add(creditTaxItem.getUnappliedAmount()))
                  .items(List.of(applyCreditMemoItem, applyCreditTaxItem));
          var applyCreditMemoRequest =
              new ApplyCreditMemoRequest()
                  .effectiveDate(domainRequest.getOrderDate())
                  .invoices(List.of(applyCreditMemoToInvoice));
          
          // リクエストを実行する
          zuoraClient
              .creditMemosApi()
              .applyCreditMemoApi(creditMemoNumber, applyCreditMemoRequest)
              .execute();
        }
      }

    } catch (ApiException e) {
      e.printStackTrace();
    }

    return orderNumber;
  }
}
