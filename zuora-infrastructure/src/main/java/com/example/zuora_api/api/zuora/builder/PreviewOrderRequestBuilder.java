package com.example.zuora_api.api.zuora.builder;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.zuora_api.model.CreateSubscriptionOrderRequest;
import com.zuora.model.ChargeOverridePricing;
import com.zuora.model.InitialTerm;
import com.zuora.model.OrderActionType;
import com.zuora.model.PreviewOptions;
import com.zuora.model.PreviewOptions.PreviewTypesEnum;
import com.zuora.model.PreviewOptionsPreviewThruType;
import com.zuora.model.PreviewOrderChargeOverride;
import com.zuora.model.PreviewOrderCreateSubscription;
import com.zuora.model.PreviewOrderCreateSubscriptionTerms;
import com.zuora.model.PreviewOrderOrderAction;
import com.zuora.model.PreviewOrderRatePlanOverride;
import com.zuora.model.PreviewOrderRequest;
import com.zuora.model.PreviewOrderSubscriptions;
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
public class PreviewOrderRequestBuilder {

  public PreviewOrderRequest create(CreateSubscriptionOrderRequest domainRequest) {

    /* subscriptions */

    // createSubscription > terms
    var initialTerm =
        new InitialTerm()
            .period(1)
            .periodType(TermPeriodType.YEAR)
            .startDate(domainRequest.getTriggerDate())
            .termType(TermType.TERMED);
    var renewalTerm = new RenewalTerm().period(1).periodType(TermPeriodType.YEAR);
    var terms =
        new PreviewOrderCreateSubscriptionTerms()
            .initialTerm(initialTerm)
            .renewalSetting(RenewalSetting.WITH_SPECIFIC_TERM)
            .renewalTerms(List.of(renewalTerm));

    // createSubscription > subscribeToRatePlans
    var ratePlanOverride = new PreviewOrderRatePlanOverride().productRatePlanId(domainRequest.getProductRatePlanDto().getId());
    if (domainRequest.getDiscountOption().getDiscountType() >= 2) {
      var chargeOverrides =
          List.of(
              new PreviewOrderChargeOverride()
                  .productRatePlanChargeId(domainRequest.getProductRatePlanDto().getChargeId())
                  .pricing(
                      new ChargeOverridePricing()
                          .recurringFlatFee(
                              new RecurringFlatFeePricingOverride()
                                  .listPrice(BigDecimal.valueOf(domainRequest.getDiscountOption().getPrice())))));
      ratePlanOverride.setChargeOverrides(chargeOverrides);
    }

    // createSubscription
    var createOrderCreateSubscription =
        new PreviewOrderCreateSubscription()
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
    var previewOrderAction =
        new PreviewOrderOrderAction()
            .type(OrderActionType.CREATESUBSCRIPTION)
            .createSubscription(createOrderCreateSubscription)
            .triggerDates(triggerDates);

    // subscriptions
    var subscription = new PreviewOrderSubscriptions().addOrderActionsItem(previewOrderAction);

    /* previewOptions */
    var previewOptions =
        new PreviewOptions()
            .previewTypes(List.of(PreviewTypesEnum.BILLINGDOCS))
            .previewThruType(PreviewOptionsPreviewThruType.SPECIFICDATE)
            .specificPreviewThruDate(domainRequest.getOrderDate().plusMonths(1).withDayOfMonth(1));

    // request
    var request =
        new PreviewOrderRequest()
            .description(domainRequest.getProductRatePlanDto().getName() + "の新規契約")
            .existingAccountNumber(domainRequest.getAccountNumber())
            .orderDate(domainRequest.getOrderDate())
            .previewOptions(previewOptions)
            .addSubscriptionsItem(subscription);

    return request;
  }
}
