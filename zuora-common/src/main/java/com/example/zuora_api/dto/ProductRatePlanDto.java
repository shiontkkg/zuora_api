package com.example.zuora_api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductRatePlanDto {
  
  String id;
  
  String name;
  
  String chargeId;
  
  String chargeName;
  
  int price;
  
  // アップグレード・ダウングレードの判定で使用する
  int grade;
}
