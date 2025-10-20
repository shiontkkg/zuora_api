package com.example.zuora_api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.zuora_api.dto.SubscriptionDto;
import com.example.zuora_api.service.SubscriptionService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/subscriptions")
@CrossOrigin
@AllArgsConstructor
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  /**
   * 指定したアカウントが持つサブスクリプションの一覧を取得する。
   *
   * @param accountKey アカウント番号またはアカウントID
   * @return
   */
  @GetMapping
  public List<SubscriptionDto> listSubscriptionsByAccount(
      @RequestParam("accountKey") String accountKey) {

    List<SubscriptionDto> subscriptions =
        subscriptionService.getActiveSubscriptionsByAccount(accountKey);
    return subscriptions;
  }

  /**
   * 指定したサブスクリプションを取得する。
   *
   * @param subscriptionNumber サブスクリプション番号
   * @return
   */
  @GetMapping("/{subscriptionNumber}")
  public SubscriptionDto getSubscription(
      @PathVariable("subscriptionNumber") String subscriptionNumber) {

    return subscriptionService.getSubscription(subscriptionNumber);
  }
}
