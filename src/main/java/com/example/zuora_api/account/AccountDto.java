package com.example.zuora_api.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

    private String id;

    private String name;

    private String number;
}
