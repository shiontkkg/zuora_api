package com.example.zuora_api.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionDto {

    private String subscriptionNumber;

    private LocalDate subscriptionStartDate;

    private LocalDate subscriptionEndDate;
    
    private LocalDate lastBookingDate;
    
    private LocalDate termStartDate;
    
    private LocalDate termEndDate;
    
    // ratePlans > productId
    private String productId;
    
    // ratePlans > productName
    private String productName;
    
    // ratePlans > ratePlanId
    private String ratePlanId;
    
    // ratePlans > productRatePlanId
    private String productRatePlanId;
    
    // ratePlans > ratePlanName
    private String ratePlanName;
    
    // ratePlans > ratePlanCharges > price
    private int price;
    
    // ratePlans > ratePlanCharges > priceChangeOption
    private String priceChangeOption;
    
    // プラン履歴
    private List<RatePlanDto> ratePlans;
}
