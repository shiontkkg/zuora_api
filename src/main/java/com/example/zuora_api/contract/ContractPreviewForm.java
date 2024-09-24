package com.example.zuora_api.contract;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ContractPreviewForm {

    private LocalDate orderDate;

    private String productId;

    private String planId;

    private int discountType;

    private int price;

    private int discount;
}
