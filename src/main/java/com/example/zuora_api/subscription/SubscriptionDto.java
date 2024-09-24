package com.example.zuora_api.subscription;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionDto {

    private String number;

    private String productName;

    private String startDate;

    private String endDate;
}
