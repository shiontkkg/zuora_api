package com.example.zuora_api.account;

import com.zuora.sdk.ZuoraClient;
import lombok.AllArgsConstructor;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.ListAccountResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class AccountRepository {

    private final ZuoraClient zuoraClient;

    public List<AccountDto> getAllAccounts() {
        var accounts = new ArrayList<AccountDto>();

        try {
            var listAccountResponse = zuoraClient.accounts().getAccounts();
            listAccountResponse.getData().forEach(a -> {
                accounts.add(
                        AccountDto.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .number(a.getAccountNumber())
                                .build()
                );
            });
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        return accounts;
    }

    public AccountDto getAccount(String accountId) {
        AccountDto account = null;
        try {
            var zuoraAccount = zuoraClient.accounts().getAccount(accountId);
            account = AccountDto.builder()
                    .id(zuoraAccount.getId())
                    .number(zuoraAccount.getAccountNumber())
                    .name(zuoraAccount.getName()).build();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return account;
    }
}
