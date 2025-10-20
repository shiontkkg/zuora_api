package com.example.zuora_api.config;

import org.openapitools.client.ApiException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import com.example.zuora_api.config.property.ZuoraClientProperties;
import com.zuora.ZuoraClient;

@Configuration
public class ZuoraClientConfig {

  @Bean
  ZuoraClient zuoraClient(ZuoraClientProperties properties) {
    var zuoraClient =
        new ZuoraClient(
            properties.getClientId(), properties.getClientSecret(), properties.getZuoraBaseUrl());
    zuoraClient.setConnectTimeout(properties.getConnectTimeout() * 1000);
    zuoraClient.setWriteTimeout(properties.getWriteTimeout() * 1000);
    zuoraClient.setReadTimeout(properties.getReadTimeout() * 1000);
    
    zuoraClient.initialize();
    
    return zuoraClient;
  }

  @Bean
  ZuoraV2Client zuoraV2Client(ZuoraClientProperties properties) {
    var zuoraClient =
        new ZuoraV2Client(
            properties.getClientId(), properties.getClientSecret(), properties.getZuoraBaseUrl());
    zuoraClient.setConnectTimeout(properties.getConnectTimeout() * 1000);
    zuoraClient.setWriteTimeout(properties.getWriteTimeout() * 1000);
    zuoraClient.setReadTimeout(properties.getReadTimeout() * 1000);

    zuoraClient.initialize();

    return zuoraClient;
  }

  @Bean
  RetryTemplate retryTemplate(ZuoraV2Client zuoraClient) {
    var backoffPolicy = new ExponentialBackOffPolicy();
    backoffPolicy.setInitialInterval(500);
    backoffPolicy.setMaxInterval(4000);
    backoffPolicy.setMultiplier(2);

    return RetryTemplate.builder()
        .customPolicy(new ZuoraV2ApiRetryPolicy(5, zuoraClient))
        .customBackoff(backoffPolicy)
        .retryOn(ApiException.class)
        .build();
  }
}
