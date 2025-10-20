package com.example.zuora_api.api.zuora;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.SubscriptionApi;
import com.example.zuora_api.api.zuora.mapper.ZuoraSubscriptionMapper;
import com.example.zuora_api.dto.SubscriptionDto;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;
import com.zuora.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ZuoraSubscriptionApi implements SubscriptionApi {

  private final ZuoraClient zuoraClient;

  private final ZuoraSubscriptionMapper subscriptionMapper;

  public List<SubscriptionDto> getActiveSubscriptionsByAccount(String accountKey) {

    var subscriptions = new ArrayList<SubscriptionDto>();

    try {
      var getSubscriptionsResponse =
          zuoraClient.subscriptionsApi().getSubscriptionsByAccountApi(accountKey).execute();

      for (var getSubscriptionResponse : getSubscriptionsResponse.getSubscriptions()) {
        if (getSubscriptionResponse.getStatus() == SubscriptionStatus.ACTIVE) {
          var subscriptionDto = subscriptionMapper.toDto(getSubscriptionResponse);
          subscriptions.add(subscriptionDto);
        }
      }

    } catch (ApiException e) {
      log.error(
          """
              サブスクリプションの取得に失敗しました。%s
              """
              .formatted(e.getMessage()));
    }

    return subscriptions;
  }

  /**
   * 指定したサブスクリプションを取得する。
   *
   * @param subscriptionNumber サブスクリプション番号
   * @return
   */
  public SubscriptionDto getSubscriptionByNumber(String subscriptionNumber) {

    SubscriptionDto subscription = null;

    try {
      var response =
          zuoraClient.subscriptionsApi().getSubscriptionByKeyApi(subscriptionNumber).execute();
      subscription = subscriptionMapper.toDto(response);

    } catch (ApiException e) {
      log.error(
          """
              サブスクリプションの取得に失敗しました。%s
              """
              .formatted(e.getMessage()));
    }

    return subscription;
  }
}
