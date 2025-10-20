package com.example.zuora_api.config;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.ErrorResponse;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import java.util.Optional;

@Slf4j
public class ZuoraV2ApiRetryPolicy extends SimpleRetryPolicy {

    private final ZuoraV2Client zuoraClient;

    public ZuoraV2ApiRetryPolicy(int maxAttempts, ZuoraV2Client zuoraClient) {
        super(maxAttempts);
        this.zuoraClient = zuoraClient;
    }

    @Override
    public boolean canRetry(RetryContext context) {

        var shouldRetry = true;
        var lastThrowable = context.getLastThrowable();
        if (lastThrowable instanceof ApiException apiException) {
            if (401 == apiException.getCode()) {
                zuoraClient.retryAuth();
            } else {
                shouldRetry = Optional.ofNullable(apiException.getErrorObject())
                        .map(ErrorResponse::getRetryable)
                        .orElse(false);
            }
        }

        if (shouldRetry) {
            log.warn("リトライ可能（リトライ回数：" + context.getRetryCount() + "）");
        } else {
            log.error("リトライ不可能（リトライ回数：" + context.getRetryCount() + "）");
        }
        return super.canRetry(context) && shouldRetry;
    }
}
