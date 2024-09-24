package com.example.zuora_api.config;

import com.zuora.sdk.ZuoraClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZuoraApiConfig {

    @Bean
    ZuoraClient zuoraClient(
            @Value("${zuora.sdk-client.client-id}") String clientId,
            @Value("${zuora.sdk-client.client-secret}") String clientSecret)
    {
        var zuoraClient = new ZuoraClient(
                clientId,
                clientSecret,
                ZuoraClient.ZuoraEnv.SBX
        );
        zuoraClient.initialize();
        return zuoraClient;
    }
}
