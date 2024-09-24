package com.example.zuora_api.subscription;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SubscriptionService {

    private final SubscriptionGateway subscriptionRepository;

    public List<SubscriptionDto> getActiveSubscriptionsByAccount(String accountId) {
        return subscriptionRepository.getActiveSubscriptionsByAccount(accountId);
    }
}
