package com.example.zuora_api.contract;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class ContractCreateService {

    private final OrderGateway orderGateway;

    public ContractPreviewDto previewContractCreate(ContractPreviewForm form) {
        return orderGateway.createOrderPreview(form);
    }

    public String create(ContractPreviewForm form) {
        return orderGateway.createOrder(form);
    }
}
