package com.example.zuora_api.contract;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractPreviewDto {

    private int subtotal;

    private int tax;

    private int total;

    private Item[] items;

    @Data
    @Builder
    static class Item {

        private int quantity;

        private String startDate;

        private String endDate;

        private String name;

        private int total;
    }
}
