package com.example.zuora_api.config;

import com.zuora.sdk.ZuoraClient;

/**
 * Zuora v1 APIのクライアントと区別するため、v2 APIのクライアントであることを明示化したクラス。
 */
public class ZuoraV2Client extends ZuoraClient {

    public ZuoraV2Client(String clientId, String clientSecret, String zuoraBaseUrl) {
        super(clientId, clientSecret, zuoraBaseUrl);
    }

    public void retryAuth() {
        super.auth();
    }
}
