package com.example.zuora_api.model;

import lombok.Getter;

@Getter
public class DiscountOption {
  
  /**
   * 1: 値引きなし
   * 2: 価格を変更する
   */
  private int discountType;
  
  private int price;

  public DiscountOption(int discountType, int price) {
    this.discountType = discountType;
    this.price = price;
  }
}
