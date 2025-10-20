package com.example.zuora_api.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatePlanDto {
  
  private String subscriptionRatePlanNumber;
  
  private String ratePlanName;
  
  private String lastChangeType;
  
  // ratePlans > ratePlanName > effectiveStartDate
  private LocalDate effectiveStartDate;
  
  //ratePlans > ratePlanName > effectiveEndDate
  private LocalDate effectiveEndDate;
  
  private int period;
  
  //ratePlans > ratePlanCharges > price
  private int price;
  
  // ratePlans > ratePlanCharges > priceChangeOption
  private String priceChangeOption;
}
