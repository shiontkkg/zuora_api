package com.example.zuora_api.subscription;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@CrossOrigin
@AllArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public List<SubscriptionDto> aaa(@RequestParam("accountId") String accountId) {
        return subscriptionService.getActiveSubscriptionsByAccount(accountId);
    }
}
