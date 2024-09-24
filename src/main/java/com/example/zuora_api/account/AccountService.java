package com.example.zuora_api.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountDto> getAllAccounts() {
        return accountRepository.getAllAccounts();
    }

    public AccountDto getAccount(String accountId) {
        return accountRepository.getAccount(accountId);
    }
}
