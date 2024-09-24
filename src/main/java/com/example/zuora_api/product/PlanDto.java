package com.example.zuora_api.product;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanDto {

    private String id;

    private String name;

    private int price;
}
