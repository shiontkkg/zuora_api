package com.example.zuora_api.contract;

import com.example.zuora_api.util.ThreeTenBpConverter;
import com.zuora.sdk.ZuoraClient;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@AllArgsConstructor
public class OrderGateway {

    private final ZuoraClient zuoraClient;

    private final ThreeTenBpConverter threeTenBpConverter;

    public ContractPreviewDto createOrderPreview(ContractPreviewForm form) {
        var accountNumber = "A00000020";
        var orderDate = form.getOrderDate();

        var postSubscriptionOrderRequest = createSubscriptionOrderRequest(form);

        // オーダープレビュー
        ContractPreviewDto contractPreviewDto = null;
        var orderPreviewCreateRequest = new OrderPreviewCreateRequest()
                .accountNumber(accountNumber)
                .orderDate(threeTenBpConverter.convert(orderDate))
                .metrics(List.of(OrderPreviewCreateRequest.MetricsEnum.BILLING_DOCUMENTS))
                .subscriptions(List.of(postSubscriptionOrderRequest));

        try {
            var orderPreviewResponse = zuoraClient.orders().createOrderPreview(orderPreviewCreateRequest);

            for (var billingDocument : orderPreviewResponse.getBillingDocuments()) {
                var dtoItems = new ArrayList<ContractPreviewDto.Item>();
                for (var item : billingDocument.getBillingDocumentItems()) {
                    var dtoItem = ContractPreviewDto.Item.builder()
                            .name(item.getSubscriptionItemName())
                            .quantity(item.getQuantity().intValue())
                            .startDate(item.getServiceStartDate())
                            .endDate(item.getServiceEndDate())
                            .total(item.getTotal().intValue()).build();
                    dtoItems.add(dtoItem);
                }
                contractPreviewDto = ContractPreviewDto.builder()
                        .items(dtoItems.toArray(new ContractPreviewDto.Item[dtoItems.size()]))
                        .subtotal(billingDocument.getSubtotal().intValue())
                        .tax(billingDocument.getTax().intValue())
                        .total(billingDocument.getTotal().intValue()).build();
            }

        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        return contractPreviewDto;
    }

    public String createOrder(ContractPreviewForm form) {
        var accountNumber = "A00000020";
        var orderDate = form.getOrderDate();

        var postSubscriptionOrderRequest = createSubscriptionOrderRequest(form);

        // オーダー
        var orderCreateRequest = new OrderCreateRequest()
                .accountNumber(accountNumber)
                .orderDate(threeTenBpConverter.convert(orderDate))
                .subscriptions(List.of(postSubscriptionOrderRequest));

        String subscriptionNumber = null;

        // オーダー実行
        try {
            var order = zuoraClient.orders().createOrder(orderCreateRequest);
            System.out.println(order);
            subscriptionNumber = order.getSubscriptions().get(0).getSubscriptionNumber();
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return subscriptionNumber;
    }

    private PostSubscriptionOrderRequest createSubscriptionOrderRequest(ContractPreviewForm form) {

        var orderDate = form.getOrderDate();
        var planId = form.getPlanId();
        var discountType = form.getDiscountType();
        var price = form.getPrice();
        var discount = form.getDiscount();

        var subscriptionPlanRequests = new ArrayList<SubscriptionPlanCreateRequest>();

        if (discountType == 3) { // 定額値引きする
            // 料金プラン課金
            var discountPlanId = getDiscountPlanOfPlan(planId);
            var discountPriceId = getDiscountChargeIdOfPlan(discountPlanId);
            var subscriptionItemCreateRequest = new SubscriptionItemCreateRequest()
                    .priceId(discountPriceId)
                    .discountAmount(new BigDecimal(discount));

            // 料金プラン
            var subscriptionPlanRequest = new SubscriptionPlanCreateRequest()
                    .planId(discountPlanId)
                    .prices(List.of(subscriptionItemCreateRequest));

            subscriptionPlanRequests.add(subscriptionPlanRequest);
        }

        if (discountType == 2) { // 価格を変更する
            // 料金プラン課金
            var priceId = getRecurringChargeIdOfPlan(planId);
            var subscriptionItemCreateRequest = new SubscriptionItemCreateRequest()
                    .priceId(priceId)
                    .amount(new BigDecimal(price));

            // 料金プラン
            var subscriptionPlanRequest = new SubscriptionPlanCreateRequest()
                    .planId(planId)
                    .prices(List.of(subscriptionItemCreateRequest));

            subscriptionPlanRequests.add(subscriptionPlanRequest);
        } else {
            // 料金プラン
            var subscriptionPlanRequest = new SubscriptionPlanCreateRequest().planId(planId);

            subscriptionPlanRequests.add(subscriptionPlanRequest);
        }

        // 各種日付
        var contractEffectiveDate = orderDate.withDayOfMonth(1);
        var subscriptionStartDate = orderDate.plusMonths(1).withDayOfMonth(1);

        // 初期契約期間
        var initialTerm = new Term()
                .startDate(threeTenBpConverter.convert(subscriptionStartDate))
                .intervalCount(1)
                .interval(Term.IntervalEnum.YEAR)
                .type(Term.TypeEnum.TERMED);

        // 契約更新期間
        var renewalTerm = new Term()
                .intervalCount(1)
                .interval(Term.IntervalEnum.YEAR)
                .type(Term.TypeEnum.TERMED);

        // トリガー日付
        var startOn = new StartOn()
                .contractEffective(threeTenBpConverter.convert(contractEffectiveDate))
                .serviceActivation(threeTenBpConverter.convert(subscriptionStartDate))
                .customerAcceptance(threeTenBpConverter.convert(subscriptionStartDate));

        // リクエスト生成
        var request = new PostSubscriptionOrderRequest()
                .initialTerm(initialTerm)
                .renewalTerm(renewalTerm)
                .startOn(startOn)
                .subscriptionPlans(subscriptionPlanRequests);

        return request;
    }

    // TODO 適切な実装に直す
    private String getRecurringChargeIdOfPlan(String planId) {
        return switch (planId) {
            case "8ad081dd917ed4750191881a3b3428c5" -> "8ad097b4917efc770191881c5c9d43da";
            case "8ad081dd917ed4750191881a6cbd28c6" -> "8ad081dd917ed4750191881de10e2919";
            case "8ad081dd917ed4750191881aa5eb28d1" -> "8ad081dd917ed4750191881e80062920";
            default -> "";
        };
    }

    // TODO 適切な実装に直す
    private String getDiscountPlanOfPlan(String planId) {
        return switch (planId) {
            case "8ad081dd917ed4750191881a3b3428c5" -> "8ad097b491e54f780191eec8e13f140e";
            case "8ad081dd917ed4750191881a6cbd28c6" -> "8ad097b491e54f780191eec8e13f140e";
            case "8ad081dd917ed4750191881aa5eb28d1" -> "8ad097b491e54f780191eec8e13f140e";
            default -> "";
        };
    }

    // TODO 適切な実装に直す
    private String getDiscountChargeIdOfPlan(String planId) {
        return switch (planId) {
            case "8ad097b491e54f780191eec8e13f140e" -> "8ad097b491e54f780191eed19d8e153c";
            default -> "";
        };
    }
}
