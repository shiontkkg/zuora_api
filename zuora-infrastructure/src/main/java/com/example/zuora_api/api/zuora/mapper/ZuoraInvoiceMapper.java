package com.example.zuora_api.api.zuora.mapper;

import java.util.ArrayList;
import org.springframework.stereotype.Component;
import com.example.zuora_api.dto.InvoiceDto;
import com.example.zuora_api.dto.InvoiceItemDto;
import com.zuora.model.ExpandedInvoice;
import com.zuora.model.InvoiceItemsResponse;
import com.zuora.model.InvoiceResponse;

@Component
public class ZuoraInvoiceMapper {
  
  /**
   * Object Query 用の Mapper
   * 
   * @param invoice
   * @return
   */
  public InvoiceDto toDto(ExpandedInvoice invoice) {
    
    var invoiceItems = new ArrayList<InvoiceItemDto>();
    invoice
        .getInvoiceItems()
        .forEach(
            i -> {
              invoiceItems.add(
                  InvoiceItemDto.builder()
                      .id(i.getId())
                      .chargeName(i.getChargeName())
                      .chargeAmount(i.getChargeAmount().intValue())
                      .taxAmount(i.getTaxAmount().intValue())
                      .balance(i.getBalance().intValue())
                      .serviceStartDate(i.getServiceStartDate())
                      .serviceEndDate(i.getServiceEndDate())
                      .build());
            });

    var invoiceDto =
        InvoiceDto.builder()
            .id(invoice.getId())
            .invoiceNumber(invoice.getInvoiceNumber())
            .invoiceDate(invoice.getInvoiceDate())
            .amount(invoice.getAmount().intValue())
            .amountWithoutTax(invoice.getAmountWithoutTax().intValue())
            .taxAmount(invoice.getTaxAmount().intValue())
            .paymentAmount(invoice.getPaymentAmount().intValue())
            .creditMemoAmount(invoice.getCreditMemoAmount().intValue())
            .balance(invoice.getBalance().intValue())
            .invoiceItems(invoiceItems)
            .build();
    
    return invoiceDto;
  }
  
  /**
   * Invoice API 用の Mapper
   * 
   * @param invoice
   * @param invoiceItems
   * @return
   */
  public InvoiceDto toDto(InvoiceResponse invoice, InvoiceItemsResponse invoiceItems) {
    
    var invoiceItemDtoList =  new ArrayList<InvoiceItemDto>();
    invoiceItems
        .getInvoiceItems()
        .forEach(
            i -> {
              invoiceItemDtoList.add(
                  InvoiceItemDto.builder()
                      .id(i.getId())
                      .chargeName(i.getChargeName())
                      .chargeAmount(i.getChargeAmount().intValue())
                      .taxAmount(i.getTaxAmount().intValue())
                      .balance(i.getBalance().intValue())
                      .serviceStartDate(i.getServiceStartDate())
                      .serviceEndDate(i.getServiceEndDate())
                      .build());
            });

    var invoiceDto =
        InvoiceDto.builder()
            .id(invoice.getId())
            .invoiceNumber(invoice.getInvoiceNumber())
            .invoiceDate(invoice.getInvoiceDate())
            .amount(invoice.getAmount().intValue())
            .amountWithoutTax(invoice.getAmountWithoutTax().intValue())
            .taxAmount(invoice.getTaxAmount().intValue())
            .paymentAmount(invoice.getPaymentAmount().intValue())
            .creditMemoAmount(invoice.getCreditMemoAmount().intValue())
            .balance(invoice.getBalance().intValue())
            .invoiceItems(invoiceItemDtoList)
            .build();
    
    return invoiceDto;
  }
}
