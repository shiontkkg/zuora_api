package com.example.zuora_api.api.zuora;

import java.time.Period;
import java.util.ArrayList;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.RenewSubscriptionOrderApi;
import com.example.zuora_api.api.zuora.builder.CreateRenewSubscriptionOrderRequestBuiler;
import com.example.zuora_api.api.zuora.builder.PreviewRenewSubscriptionOrderRequestBuilder;
import com.example.zuora_api.dto.PreviewOrderDto;
import com.example.zuora_api.model.RenewSubscriptionOrderRequest;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ZuoraRenewSubscriptionOrderApi implements RenewSubscriptionOrderApi {

  private final ZuoraClient zuoraClient;

  private final PreviewRenewSubscriptionOrderRequestBuilder
      previewRenewSubscriptionOrderRequestBuilder;

  private final CreateRenewSubscriptionOrderRequestBuiler
      createRenewSubscriptionOrderRequestBuilder;

  @Override
  public PreviewOrderDto previewRenewSubscription(RenewSubscriptionOrderRequest domainRequest) {

    var request = previewRenewSubscriptionOrderRequestBuilder.create(domainRequest);
    PreviewOrderDto orderPreview = null;

    try {
      var response = zuoraClient.ordersApi().previewOrderApi(request).execute();

      var invoice = response.getPreviewResult().getInvoices().get(0);

      var invoiceItems = new ArrayList<PreviewOrderDto.Item>();
      invoice
          .getInvoiceItems()
          .forEach(
              i -> {
                var period =
                    Period.between(i.getServiceStartDate(), i.getServiceEndDate().plusDays(1));
                var invoiceItem =
                    PreviewOrderDto.Item.builder()
                        .name(i.getChargeName())
                        .quantity(i.getAdditionalInfo().getQuantity().intValue())
                        .startDate(i.getServiceStartDate())
                        .endDate(i.getServiceEndDate())
                        .period(period.getMonths() + period.getYears() * 12)
                        .total(i.getAmountWithoutTax().intValue())
                        .build();
                invoiceItems.add(invoiceItem);
              });

      orderPreview =
          PreviewOrderDto.builder()
              .items(invoiceItems)
              .subtotal(invoice.getAmountWithoutTax().intValue())
              .tax(invoice.getTaxAmount().intValue())
              .total(invoice.getAmount().intValue())
              .build();

    } catch (ApiException e) {
      e.printStackTrace();
    }

    return orderPreview;
  }

  @Override
  public String renewSubscription(RenewSubscriptionOrderRequest domainRequest) {

    var request = createRenewSubscriptionOrderRequestBuilder.create(domainRequest);
    var orderNumber = "";
    try {
      var response = zuoraClient.ordersApi().createOrderApi(request).execute();
      orderNumber = response.getOrderNumber();

    } catch (ApiException e) {
      e.printStackTrace();
    }

    return orderNumber;
  }
}
