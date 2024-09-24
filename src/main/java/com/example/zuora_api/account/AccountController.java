package com.example.zuora_api.account;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@CrossOrigin
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountDto> listAccounts() {
        var accounts = accountService.getAllAccounts();
        return accounts;
    }

    @GetMapping("/{accountId}")
    public AccountDto getAccount(@PathVariable String accountId) {
        var account = accountService.getAccount(accountId);
        return account;
    }
}
