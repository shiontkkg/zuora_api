package com.example.zuora_api.api.zuora.builder;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.zuora_api.model.ChangePlanDowngradeOrderRequest;
import com.example.zuora_api.model.ChangePlanUpgradeOrderRequest;
import com.zuora.model.BillingOptions;
import com.zuora.model.ChangePlanSubType;
import com.zuora.model.ChargeOverride;
import com.zuora.model.ChargeOverridePricing;
import com.zuora.model.CreateOrderAction;
import com.zuora.model.CreateOrderChangePlan;
import com.zuora.model.CreateOrderChangePlanRatePlanOverride;
import com.zuora.model.CreateOrderRequest;
import com.zuora.model.CreateOrderSubscription;
import com.zuora.model.OrderActionType;
import com.zuora.model.OrderStatus;
import com.zuora.model.ProcessingOptionsWithDelayedCapturePayment;
import com.zuora.model.RecurringFlatFeePricingOverride;
import com.zuora.model.TriggerDate;
import com.zuora.model.TriggerDateName;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CreateChangePlanOrderRequestBuilder {

  public CreateOrderRequest createUpgrade(ChangePlanUpgradeOrderRequest domainRequest) {

    // orderActions > changePlan
    var newProductRatePlan =
        new CreateOrderChangePlanRatePlanOverride()
            .productRatePlanId(domainRequest.getProductRatePlanDto().getId());
    if (domainRequest.getDiscountOption().getDiscountType() == 2) {
      var chargeOverrides =
          List.of(
              new ChargeOverride()
                  .productRatePlanChargeId(domainRequest.getProductRatePlanDto().getChargeId())
                  .pricing(
                      new ChargeOverridePricing()
                          .recurringFlatFee(
                              new RecurringFlatFeePricingOverride()
                                  .listPrice(
                                      BigDecimal.valueOf(
                                          domainRequest.getDiscountOption().getPrice())))));
      newProductRatePlan.chargeOverrides(chargeOverrides);
    }
    var changePlan =
        new CreateOrderChangePlan()
            .subType(ChangePlanSubType.UPGRADE)
            //            .productRatePlanId(ratePlanId)
            .ratePlanId(domainRequest.getSubscriptionDto().getRatePlanId())
            .newProductRatePlan(newProductRatePlan);

    // orderActions > triggerDates
    var triggerDates =
        List.of(
            new TriggerDate()
                .name(TriggerDateName.CONTRACTEFFECTIVE)
                .triggerDate(domainRequest.getTriggerDate()),
            new TriggerDate()
                .name(TriggerDateName.SERVICEACTIVATION)
                .triggerDate(domainRequest.getTriggerDate()),
            new TriggerDate()
                .name(TriggerDateName.CUSTOMERACCEPTANCE)
                .triggerDate(domainRequest.getTriggerDate()));

    // orderActions
    var changePlanOrderAction =
        new CreateOrderAction()
            .type(OrderActionType.CHANGEPLAN)
            .changePlan(changePlan)
            .triggerDates(triggerDates);

    // subscriptions
    var subscription =
        new CreateOrderSubscription()
            .subscriptionNumber(domainRequest.getSubscriptionDto().getSubscriptionNumber())
            .orderActions(List.of(changePlanOrderAction));

    // processingOptions
    var processingOptions =
        new ProcessingOptionsWithDelayedCapturePayment()
            .runBilling(true)
            .billingOptions(
                new BillingOptions()
                    .documentDate(domainRequest.getOrderDate())
                    .targetDate(domainRequest.getTriggerDate()));

    // request
    var request =
        new CreateOrderRequest()
            .description(domainRequest.getProductRatePlanDto().getName() + "へのアップグレード")
            .existingAccountNumber(domainRequest.getAccountNumber())
            .orderDate(domainRequest.getOrderDate())
            .subscriptions(List.of(subscription))
            .processingOptions(processingOptions);

    return request;
  }

  public CreateOrderRequest createDowngrade(ChangePlanDowngradeOrderRequest domainRequest) {

    // orderActions > changePlan
    var newProductRatePlan =
        new CreateOrderChangePlanRatePlanOverride()
            .productRatePlanId(domainRequest.getProductRatePlanDto().getId());
    if (domainRequest.getDiscountOption().getDiscountType() == 2) {
      var chargeOverrides =
          List.of(
              new ChargeOverride()
                  .productRatePlanChargeId(domainRequest.getProductRatePlanDto().getChargeId())
                  .pricing(
                      new ChargeOverridePricing()
                          .recurringFlatFee(
                              new RecurringFlatFeePricingOverride()
                                  .listPrice(
                                      BigDecimal.valueOf(
                                          domainRequest.getDiscountOption().getPrice())))));
      newProductRatePlan.chargeOverrides(chargeOverrides);
    }
    var changePlan =
        new CreateOrderChangePlan()
            .subType(ChangePlanSubType.DOWNGRADE)
            .ratePlanId(domainRequest.getSubscriptionDto().getRatePlanId())
            .newProductRatePlan(newProductRatePlan);

    // orderActions > triggerDates
    var triggerDates =
        List.of(
            new TriggerDate()
                .name(TriggerDateName.CONTRACTEFFECTIVE)
                .triggerDate(domainRequest.getTriggerDate()),
            new TriggerDate()
                .name(TriggerDateName.SERVICEACTIVATION)
                .triggerDate(domainRequest.getTriggerDate()),
            new TriggerDate()
                .name(TriggerDateName.CUSTOMERACCEPTANCE)
                .triggerDate(domainRequest.getTriggerDate()));

    // orderActions
    var changePlanOrderAction =
        new CreateOrderAction()
            .type(OrderActionType.CHANGEPLAN)
            .changePlan(changePlan)
            .triggerDates(triggerDates);

    // subscriptions
    var subscription =
        new CreateOrderSubscription()
            .subscriptionNumber(domainRequest.getSubscriptionDto().getSubscriptionNumber())
            .orderActions(List.of(changePlanOrderAction));

    // request
    CreateOrderRequest request = null;
    if (domainRequest.isDowngradeImmediately()) {
      var processingOptions =
          new ProcessingOptionsWithDelayedCapturePayment()
              .runBilling(true)
              .billingOptions(
                  new BillingOptions()
                      .documentDate(domainRequest.getOrderDate())
                      .targetDate(domainRequest.getTriggerDate()));
      if (domainRequest.isApplyCreditToGeneratedInvoice()) {
        processingOptions.setApplyCredit(true);
      }
      request =
          new CreateOrderRequest()
              .description(domainRequest.getProductRatePlanDto().getName() + "への即時ダウングレード")
              .existingAccountNumber(domainRequest.getAccountNumber())
              .orderDate(domainRequest.getOrderDate())
              .addSubscriptionsItem(subscription)
              .processingOptions(processingOptions);
    } else {
      request =
          new CreateOrderRequest()
              .description(domainRequest.getProductRatePlanDto().getName() + "へのダウングレード")
              .existingAccountNumber(domainRequest.getAccountNumber())
              .orderDate(domainRequest.getOrderDate())
              .status(OrderStatus.DRAFT)
              .addSubscriptionsItem(subscription);
    }

    return request;
  }
}
