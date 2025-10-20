package com.example.zuora_api.api.zuora.mapper;

import java.time.Period;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.stereotype.Component;
import com.example.zuora_api.dto.RatePlanDto;
import com.example.zuora_api.dto.SubscriptionDto;
import com.zuora.model.ChargeType;
import com.zuora.model.GetSubscriptionResponse;
import com.zuora.model.SubscriptionRatePlan.LastChangeTypeEnum;

@Component
public class ZuoraSubscriptionMapper {
  
  public SubscriptionDto toDto(GetSubscriptionResponse response) {
    
    var subscriptionDto =
        SubscriptionDto.builder()
            .subscriptionNumber(response.getSubscriptionNumber())
            .subscriptionStartDate(response.getSubscriptionStartDate())
            .subscriptionEndDate(response.getSubscriptionEndDate())
            .lastBookingDate(response.getLastBookingDate())
            .termStartDate(response.getTermStartDate())
            .termEndDate(response.getTermEndDate())
            .build();

    var ratePlans = new ArrayList<RatePlanDto>();

    for (var ratePlan : response.getRatePlans()) {
      // 料金プランに定額課金がちょうど1つ存在することを前提とする。
      var ratePlanCharge =
          ratePlan.getRatePlanCharges().stream()
              .filter(c -> ChargeType.RECURRING.equals(c.getType()))
              .findFirst()
              .get();

      var period =
          Period.between(ratePlanCharge.getEffectiveStartDate(), ratePlanCharge.getEffectiveEndDate());
      var ratePlanDto =
          RatePlanDto.builder()
              .subscriptionRatePlanNumber(ratePlan.getSubscriptionRatePlanNumber())
              .ratePlanName(ratePlan.getRatePlanName())
              .lastChangeType(
                  Optional.ofNullable(ratePlan.getLastChangeType())
                      .map(t -> t.getValue())
                      .orElse(""))
              .effectiveStartDate(ratePlanCharge.getEffectiveStartDate())
              .effectiveEndDate(ratePlanCharge.getEffectiveEndDate())
              .period(period.getYears() * 12 + period.getMonths())
              .price(ratePlanCharge.getPrice().intValue())
              .priceChangeOption(ratePlanCharge.getPriceChangeOption().getValue())
              .build();
      ratePlans.add(ratePlanDto);

      // lastChangeTypeがRemove以外の料金プランがちょうど1つ存在し、それが最新のバージョンであることを前提とする。
      // それをサブスクリプションの現在のプラン情報として設定する。
      if (!LastChangeTypeEnum.REMOVE.equals(ratePlan.getLastChangeType())) {
        subscriptionDto.setProductId(ratePlan.getProductId());
        subscriptionDto.setProductName(ratePlan.getProductName());
        subscriptionDto.setRatePlanId(ratePlan.getId());
        subscriptionDto.setProductRatePlanId(ratePlan.getProductRatePlanId());
        subscriptionDto.setRatePlanName(ratePlan.getRatePlanName());
        subscriptionDto.setPrice(ratePlanCharge.getPrice().intValue());
        subscriptionDto.setPriceChangeOption(ratePlanCharge.getPriceChangeOption().getValue());
      }
    }

    subscriptionDto.setRatePlans(ratePlans);
    
    return subscriptionDto;
  }
}
