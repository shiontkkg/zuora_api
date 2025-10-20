package com.example.zuora_api.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreviewOrderDto {

  private int subtotal;

  private int tax;

  private int total;

  private List<Item> items;

  @Data
  @Builder
  public static class Item {

    private int quantity;

    private LocalDate startDate;

    private LocalDate endDate;
    
    private int period;

    private String name;

    private int total;
  }
}
