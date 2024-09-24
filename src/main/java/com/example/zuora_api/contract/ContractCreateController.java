package com.example.zuora_api.contract;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/contract")
@CrossOrigin
@AllArgsConstructor
public class ContractCreateController {

    private final ContractCreateService contractCreateService;

    @PostMapping("/preview")
    public ContractPreviewDto previewContractCreate(@RequestBody ContractPreviewForm form) {
        var accountNumber = "A00000020";  // TODO 固定

        return contractCreateService.previewContractCreate(form);

//        var response = ContractPreviewDto.builder()
//                .items(new ContractPreviewDto.Item[]{
//                        ContractPreviewDto.Item.builder()
//                                .name("サンプルプロダクト1 ベーシックプラン 年額 基本料金")
//                                .quantity(1)
//                                .startDate("2024-10-01")
//                                .endDate("2025-09-30")
//                                .total(15000).build(),
//                        ContractPreviewDto.Item.builder()
//                                .name("値引き")
//                                .quantity(1)
//                                .startDate("2024-10-01")
//                                .endDate("2025-09-30")
//                                .total(-10000).build()
//                })
//                .subtotal(10000)
//                .tax(0)
//                .total(10000).build();
//
//        return response;
    }

    @PostMapping("/create")
    public String create(@RequestBody ContractPreviewForm form) {
        return contractCreateService.create(form);
    }
}
