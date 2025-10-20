package com.example.zuora_api.api.zuora;

import java.time.Period;
import java.util.ArrayList;
import java.util.Optional;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.GenerateBillingDocumentsAccountRequest;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.AccountApi;
import com.example.zuora_api.config.ZuoraV2Client;
import com.example.zuora_api.dto.PreviewChangePlanOrderDto;
import com.example.zuora_api.model.GenerateBillingDocumentsRequest;
import com.zuora.ZuoraClient;
import com.zuora.model.CreateBillingPreviewRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ZuoraAccountApi implements AccountApi {

  private final ZuoraClient zuoraClient;

  private final ZuoraV2Client zuoraV2Client;

  @Override
  public PreviewChangePlanOrderDto previewBillingDocumentsByAccount(
      GenerateBillingDocumentsRequest domainRequest) {

    PreviewChangePlanOrderDto preview = PreviewChangePlanOrderDto.builder().build();

    try {
      var request =
          new CreateBillingPreviewRequest()
              .accountNumber(domainRequest.getAccountNumber())
              .targetDate(domainRequest.getTargetDate());
      var response = this.zuoraClient.operationsApi().createBillingPreviewApi(request).execute();

      // 請求書
      if (response.getInvoiceItems().size() > 0) {
        var invoiceItems = new ArrayList<PreviewChangePlanOrderDto.Invoice.InvoiceItem>();
        response
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
                          .amountWithoutTax(i.getChargeAmount().intValue())
                          .build();
                  invoiceItems.add(invoiceItem);
                });
        var amountWithoutTax = invoiceItems.stream().mapToInt(i -> i.getAmountWithoutTax()).sum();
        var invoiceDto =
            PreviewChangePlanOrderDto.Invoice.builder()
                .amountWithoutTax(amountWithoutTax)
                .taxAmount((int) (amountWithoutTax * 0.1))
                .amount((int) (amountWithoutTax * 1.1))
                .invoiceItems(invoiceItems)
                .build();
        preview.setInvoice(invoiceDto);
      }

      // クレジットメモ
      if (response.getCreditMemoItems().size() > 0) {
        var creditMemoItems = new ArrayList<PreviewChangePlanOrderDto.CreditMemo.CreditMemoItem>();
        response
            .getCreditMemoItems()
            .forEach(
                cm -> {
                  var period =
                      Period.between(cm.getServiceStartDate(), cm.getServiceEndDate().plusDays(1));
                  var creditMemoItem =
                      PreviewChangePlanOrderDto.CreditMemo.CreditMemoItem.builder()
                          .chargeName(cm.getSkuName())
                          .serviceStartDate(cm.getServiceStartDate())
                          .serviceEndDate(cm.getServiceEndDate())
                          .period(period.getMonths() + period.getYears() * 12)
                          .amountWithoutTax(cm.getAmountWithoutTax().intValue())
                          .build();
                  creditMemoItems.add(creditMemoItem);
                });
        var amountWithoutTax = creditMemoItems.stream().mapToInt(c -> c.getAmountWithoutTax()).sum();
        var creditMemoDto =
            PreviewChangePlanOrderDto.CreditMemo.builder()
                .amountWithoutTax(amountWithoutTax)
                .taxAmount((int) (amountWithoutTax * 0.1))
                .amount((int) (amountWithoutTax * 1.1))
                .creditMemoItems(creditMemoItems)
                .build();
        preview.setCreditMemo(creditMemoDto);
      }

    } catch (com.zuora.ApiException e) {
      e.printStackTrace();
    }

    //    try {
    //      var targetDate =
    //          org.threeten.bp.LocalDate.of(
    //              domainRequest.getTargetDate().getYear(),
    //              domainRequest.getTargetDate().getMonthValue(),
    //              domainRequest.getTargetDate().getDayOfMonth());
    //      var request = new AccountPreviewRequest().targetDate(targetDate);
    //      var response =
    //          this.zuoraV2Client.accounts().previewAccount(domainRequest.getAccountNumber(),
    // request);
    //
    //      var invoiceItems = new ArrayList<PreviewChangePlanOrderDto.Invoice.InvoiceItem>();
    //      response
    //          .getInvoiceItems()
    //          .forEach(
    //              i -> {
    //                var serviceStartDate = LocalDate.parse(i.getServiceStartDate());
    //                var serviceEndDate = LocalDate.parse(i.getServiceEndDate());
    //                var amountWithoutTax = i.getAmount().intValue();
    //                var period = Period.between(serviceStartDate, serviceEndDate);
    //                var invoiceItem =
    //                    PreviewChangePlanOrderDto.Invoice.InvoiceItem.builder()
    //                        .chargeName(i.getSubscriptionItemName())
    //                        .serviceStartDate(serviceStartDate)
    //                        .serviceEndDate(serviceEndDate)
    //                        .period(period.getMonths() + period.getYears() * 12)
    //                        .amountWithoutTax(amountWithoutTax)
    //                        .build();
    //                invoiceItems.add(invoiceItem);
    //              });
    //      var amountWithoutTax = invoiceItems.stream().mapToInt(i ->
    // i.getAmountWithoutTax()).sum();
    //      var invoiceDto =
    //          PreviewChangePlanOrderDto.Invoice.builder()
    //              .amountWithoutTax(amountWithoutTax)
    //              .taxAmount((int) (amountWithoutTax * 0.1))
    //              .amount((int) (amountWithoutTax * 1.1))
    //              .invoiceItems(invoiceItems)
    //              .build();
    //      preview = PreviewChangePlanOrderDto.builder().invoice(invoiceDto).build();
    //
    //    } catch (ApiException e) {
    //      e.printStackTrace();
    //    }

    return preview;
  }

  @Override
  public Integer generateBillingDocumentsByAccount(GenerateBillingDocumentsRequest domainRequest) {

    int itemCount = 0;

    try {
      var documentDate =
          org.threeten.bp.LocalDate.of(
              domainRequest.getDocumentDate().getYear(),
              domainRequest.getDocumentDate().getMonthValue(),
              domainRequest.getDocumentDate().getDayOfMonth());
      var targetDate =
          org.threeten.bp.LocalDate.of(
              domainRequest.getTargetDate().getYear(),
              domainRequest.getTargetDate().getMonthValue(),
              domainRequest.getTargetDate().getDayOfMonth());
      var request =
          new GenerateBillingDocumentsAccountRequest()
              .post(true)
              .documentDate(documentDate)
              .targetDate(targetDate);
      var response =
          this.zuoraV2Client
              .accounts()
              .generateBillingDocuments(domainRequest.getAccountNumber(), request);

      itemCount =
          Optional.ofNullable(response.getInvoices()).map(i -> i.getData().size()).orElse(0);
      itemCount +=
          Optional.ofNullable(response.getCreditMemos()).map(c -> c.getData().size()).orElse(0);
    } catch (ApiException e) {
      e.printStackTrace();
    }

    return itemCount;
  }
}
