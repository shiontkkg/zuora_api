package com.example.zuora_api.model;

import lombok.Getter;

@Getter
public class DiscountOption {
  
  /**
   * 1: 変更しない
   * 2: 最初の期間のみ変更する
   * 3: 永続的に変更する
   */
  private int discountType;
  
  private int price;

  public DiscountOption(int discountType, int price) {
    this.discountType = discountType;
    this.price = price;
  }
}
