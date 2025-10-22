package com.example.zuora_api.api.zuora.builder;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.zuora_api.model.CreateSubscriptionOrderRequest;
import com.zuora.model.BillingOptions;
import com.zuora.model.ChargeOverride;
import com.zuora.model.ChargeOverridePricing;
import com.zuora.model.CreateOrderAction;
import com.zuora.model.CreateOrderCreateSubscription;
import com.zuora.model.CreateOrderRatePlanOverride;
import com.zuora.model.CreateOrderRequest;
import com.zuora.model.CreateOrderSubscription;
import com.zuora.model.InitialTerm;
import com.zuora.model.OrderActionCreateSubscriptionTerms;
import com.zuora.model.OrderActionType;
import com.zuora.model.PriceChangeOption;
import com.zuora.model.ProcessingOptionsWithDelayedCapturePayment;
import com.zuora.model.RecurringFlatFeePricingOverride;
import com.zuora.model.RenewalSetting;
import com.zuora.model.RenewalTerm;
import com.zuora.model.TermPeriodType;
import com.zuora.model.TermType;
import com.zuora.model.TriggerDate;
import com.zuora.model.TriggerDateName;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CreateOrderRequestBuilder {

  public CreateOrderRequest create(CreateSubscriptionOrderRequest domainRequest) {

    // createSubscription > terms
    var initialTerm =
        new InitialTerm()
            .period(1)
            .periodType(TermPeriodType.YEAR)
            .startDate(domainRequest.getTriggerDate())
            .termType(TermType.TERMED);
    var renewalTerm = new RenewalTerm().period(1).periodType(TermPeriodType.YEAR);
    var terms =
        new OrderActionCreateSubscriptionTerms()
            .initialTerm(initialTerm)
            .renewalSetting(RenewalSetting.WITH_SPECIFIC_TERM)
            .renewalTerms(List.of(renewalTerm));

    // createSubscription > subscribeToRatePlans
    var ratePlanOverride =
        new CreateOrderRatePlanOverride()
            .productRatePlanId(domainRequest.getProductRatePlanDto().getId());
    if (domainRequest.getDiscountOption().getDiscountType() >= 2) {
      var pricingOverride =
          new RecurringFlatFeePricingOverride()
               .priceChangeOption(PriceChangeOption.USELATESTPRODUCTCATALOGPRICING)
              .listPrice(BigDecimal.valueOf(domainRequest.getDiscountOption().getPrice()));
      if (domainRequest.getDiscountOption().getDiscountType() == 3) {
        pricingOverride.setPriceChangeOption(PriceChangeOption.NOCHANGE);
      }
      var chargeOverrides =
          List.of(
              new ChargeOverride()
                  .productRatePlanChargeId(domainRequest.getProductRatePlanDto().getChargeId())
                  .pricing(new ChargeOverridePricing().recurringFlatFee(pricingOverride)));
      ratePlanOverride.setChargeOverrides(chargeOverrides);
    }

    // createSubscription
    var createOrderCreateSubscription =
        new CreateOrderCreateSubscription()
            //        .notes("")
            .terms(terms)
            .addSubscribeToRatePlansItem(ratePlanOverride);

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
    var createOrderAction =
        new CreateOrderAction()
            .type(OrderActionType.CREATESUBSCRIPTION)
            .createSubscription(createOrderCreateSubscription)
            .triggerDates(triggerDates);

    // subscriptions
    var subscription = new CreateOrderSubscription().addOrderActionsItem(createOrderAction);

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
            .description(domainRequest.getProductRatePlanDto().getName() + "の新規契約")
            .existingAccountNumber(domainRequest.getAccountNumber())
            .orderDate(domainRequest.getOrderDate())
            .addSubscriptionsItem(subscription)
            .processingOptions(processingOptions);

    return request;
  }
}
