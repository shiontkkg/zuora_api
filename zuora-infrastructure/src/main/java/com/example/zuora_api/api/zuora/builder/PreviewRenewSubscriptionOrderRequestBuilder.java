package com.example.zuora_api.api.zuora.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.zuora_api.model.RenewSubscriptionOrderRequest;
import com.zuora.model.ChangePlanSubType;
import com.zuora.model.ChargeOverride;
import com.zuora.model.ChargeOverridePricing;
import com.zuora.model.CreateOrderChangePlan;
import com.zuora.model.CreateOrderChangePlanRatePlanOverride;
import com.zuora.model.OrderActionType;
import com.zuora.model.PreviewOptions;
import com.zuora.model.PreviewOptions.PreviewTypesEnum;
import com.zuora.model.PreviewOptionsPreviewThruType;
import com.zuora.model.PreviewOrderOrderAction;
import com.zuora.model.PreviewOrderRequest;
import com.zuora.model.PreviewOrderSubscriptions;
import com.zuora.model.RecurringFlatFeePricingOverride;
import com.zuora.model.TriggerDate;
import com.zuora.model.TriggerDateName;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PreviewRenewSubscriptionOrderRequestBuilder {

  public PreviewOrderRequest create(RenewSubscriptionOrderRequest domainRequest) {

    // subscriptions > orderActions
    var orderActions = new ArrayList<PreviewOrderOrderAction>();

    // subscriptions > orderActions > triggerDates
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

    // subscriptions > orderActions > RenewSubscription
    var renewSubscriptionOrderAction =
        new PreviewOrderOrderAction()
            .type(OrderActionType.RENEWSUBSCRIPTION)
            .triggerDates(triggerDates);

    // subscriptions > orderActions > ChangePlan
    if (domainRequest.getDiscountOption().getDiscountType() == 2) {
      var newProductRatePlan =
          new CreateOrderChangePlanRatePlanOverride()
              .productRatePlanId(domainRequest.getProductRatePlanDto().getId());
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
      var changePlan =
          new CreateOrderChangePlan()
              .subType(ChangePlanSubType.CROSSGRADE)
              .productRatePlanId(domainRequest.getProductRatePlanDto().getId())
              .newProductRatePlan(newProductRatePlan);
      var changePlanOrderAction =
          new PreviewOrderOrderAction()
              .type(OrderActionType.CHANGEPLAN)
              .changePlan(changePlan)
              .triggerDates(triggerDates);
      orderActions.add(changePlanOrderAction);
    }

    orderActions.add(renewSubscriptionOrderAction);

    // subscriptions
    var subscription =
        new PreviewOrderSubscriptions()
            .subscriptionNumber(domainRequest.getSubscriptionDto().getSubscriptionNumber())
            .orderActions(orderActions);

    /* previewOptions */
    var previewOptions =
        new PreviewOptions()
            .previewTypes(List.of(PreviewTypesEnum.BILLINGDOCS))
            .previewThruType(PreviewOptionsPreviewThruType.SPECIFICDATE)
            .specificPreviewThruDate(domainRequest.getSubscriptionDto().getSubscriptionEndDate());

    // request
    var request =
        new PreviewOrderRequest()
            .description(domainRequest.getSubscriptionDto().getRatePlanName() + "の更新")
            .existingAccountNumber(domainRequest.getAccountNumber())
            .orderDate(domainRequest.getOrderDate())
            .addSubscriptionsItem(subscription)
            .previewOptions(previewOptions);

    return request;
  }
}
