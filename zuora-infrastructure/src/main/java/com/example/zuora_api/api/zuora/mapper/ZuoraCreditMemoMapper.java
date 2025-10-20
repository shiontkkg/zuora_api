package com.example.zuora_api.api.zuora.mapper;

import java.util.ArrayList;
import org.springframework.stereotype.Component;
import com.example.zuora_api.dto.CreditMemoDto;
import com.example.zuora_api.dto.CreditMemoItemDto;
import com.zuora.model.ExpandedCreditMemo;

@Component
public class ZuoraCreditMemoMapper {
  
  public CreditMemoDto toDto(ExpandedCreditMemo creditMemo) {
    
    var creditMemoItemDtoList = new ArrayList<CreditMemoItemDto>();

    for (var creditMemoItem : creditMemo.getCreditMemoItems()) {
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
    }

    var creditMemoDto =
        CreditMemoDto.builder()
            .id(creditMemo.getId())
            .memoNumber(creditMemo.getMemoNumber())
            .memoDate(creditMemo.getMemoDate())
            .amount(creditMemo.getTotalAmount().intValue())
            .taxAmount(creditMemo.getTaxAmount().intValue())
            .unappliedAmount(creditMemo.getBalance().intValue())
            .refundAmount(creditMemo.getRefundAmount().intValue())
            .appliedAmount(creditMemo.getAppliedAmount().intValue())
            .creditMemoItems(creditMemoItemDtoList)
            .build();
    
    return creditMemoDto;
  }
}
