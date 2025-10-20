package com.example.zuora_api.main;

import java.util.List;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;

public class CreditMemoPartItemSample {

  public static void main(String[] args) throws ApiException {
    var zuoraClient =
        new ZuoraClient(
            "a2581d34-150e-400e-8f94-8e0f7f0925d0",
            "rx=xPiDt9kvxQV6CVLgsU9tpefR6BEn6cVbhIrxz",
            "https://rest.apisandbox.zuora.com");
    zuoraClient.initialize();

    var creditMemoKey = "CM00000027";
    
    var res1 = zuoraClient.creditMemosApi().getCreditMemoItemPartsApi("8ad0875997cf2c1b0197cf519450509d", "CM00000045").execute();
    var res2 = zuoraClient.creditMemosApi().getCreditMemoItemPartsApi("8ad0980c97cf401a0197cf519a121681", "CM00000045").execute();

    System.out.println(res1);
    System.out.println(res2);
  }
}
