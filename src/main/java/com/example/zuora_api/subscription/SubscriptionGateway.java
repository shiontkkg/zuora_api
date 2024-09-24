package com.example.zuora_api.subscription;

import com.zuora.sdk.ZuoraClient;
import lombok.AllArgsConstructor;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.Subscription;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class SubscriptionGateway {

    private final ZuoraClient zuoraClient;

    public List<SubscriptionDto> getActiveSubscriptionsByAccount(String accountId) {
        var subscriptions = new ArrayList<SubscriptionDto>();
        try {
            var subscriptionListResponse = zuoraClient.subscriptions().getSubscriptions(
                    null,
                    Arrays.asList("subscription_plans", "account"),
                    Arrays.asList("account_id.EQ:" + accountId, "state.EQ:active"));
            subscriptionListResponse.getData().forEach(s -> subscriptions.add(convert(s)));
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return subscriptions;
    }

    private SubscriptionDto convert(Subscription subscription) {
        var number = subscription.getSubscriptionNumber();
        var productName = subscription.getSubscriptionPlans().getData().get(0).getProductId();
        var startDate = subscription.getStartDate() == null ? "" : subscription.getStartDate().toString();
        var endDate = subscription.getEndDate() == null ? "" : subscription.getEndDate().toString();
        // アカウント情報

        return SubscriptionDto.builder()
                .number(number)
                .productName(productName)
                .startDate(startDate)
                .endDate(endDate).build();
    }
}
