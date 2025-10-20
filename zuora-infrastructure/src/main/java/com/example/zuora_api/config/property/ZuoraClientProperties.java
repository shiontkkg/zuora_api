package com.example.zuora_api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "zuora.sdk-client")
public class ZuoraClientProperties {

  private String clientId;
  private String clientSecret;
  private String zuoraBaseUrl;
  private int connectTimeout;
  private int writeTimeout;
  private int readTimeout;
}
