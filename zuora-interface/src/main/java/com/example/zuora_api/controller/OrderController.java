package com.example.zuora_api.controller;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.zuora_api.dto.OrderDto;
import com.example.zuora_api.dto.PreviewChangePlanOrderDto;
import com.example.zuora_api.dto.PreviewOrderDto;
import com.example.zuora_api.request.ChangePlanOrderRequest;
import com.example.zuora_api.request.CreateSubscriptionOrderRequest;
import com.example.zuora_api.request.RenewSubscriptionOrderRequest;
import com.example.zuora_api.service.ChangePlanOrderService;
import com.example.zuora_api.service.CreateSubscriptionOrderService;
import com.example.zuora_api.service.OrderService;
import com.example.zuora_api.service.RenewSubscriptionOrderService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/orders")
@CrossOrigin
@AllArgsConstructor
public class OrderController {

  private final OrderService orderService;
  
  private final CreateSubscriptionOrderService createSubscriptionOrderService;
  
  private final ChangePlanOrderService changePlanOrderService;
  
  private final RenewSubscriptionOrderService renewSubscriptionOrderService;

  @GetMapping
  public List<OrderDto> listOrdersBySubscription(
      @RequestParam("subscriptionNumber") String subscriptionNumber) {
    return orderService.getOrdersBySubscription(subscriptionNumber);
  }

  @PostMapping("/preview/create-subscription")
  public PreviewOrderDto previewCreateSubscriptionOrder(@RequestBody CreateSubscriptionOrderRequest request) {
    return createSubscriptionOrderService.previewSubscription(
            request.getAccountNumber(),
            request.getOrderDate(),
            request.getProductRatePlanId(),
            request.getDiscountType(),
            request.getPrice());
  }

  @PostMapping("/create-subscription")
  public String createSubscriptionOrder(@RequestBody CreateSubscriptionOrderRequest request) {
    return createSubscriptionOrderService.createSubscription(
        request.getAccountNumber(),
        request.getOrderDate(),
        request.getProductRatePlanId(),
        request.getDiscountType(),
        request.getPrice());
  }

  @PostMapping("/preview/change-plan")
  public PreviewChangePlanOrderDto previewChangePlanOrder(@RequestBody ChangePlanOrderRequest request) {
    var orderPreviewDto =
        changePlanOrderService.previewChangePlan(
            request.getAccountNumber(),
            request.getSubscriptionNumber(),
            request.getOrderDate(),
            request.getRatePlanId(),
            request.getCurrentProductRatePlanId(),
            request.getNewProductRatePlanId(),
            request.getDiscountType(),
            request.getPrice(),
            request.getDowngradeOption());

    return orderPreviewDto;
  }

  @PostMapping("/change-plan")
  public String changePlanOrder(@RequestBody ChangePlanOrderRequest request) {
    var orderNumber =
        changePlanOrderService.changePlan(
            request.getAccountNumber(),
            request.getSubscriptionNumber(),
            request.getOrderDate(),
            request.getRatePlanId(),
            request.getCurrentProductRatePlanId(),
            request.getNewProductRatePlanId(),
            request.getDiscountType(),
            request.getPrice(),
            request.getDowngradeOption());

    return orderNumber;
  }
  
  @PostMapping("/preview/renew-subscription")
  public PreviewOrderDto previewRenewSubscriptionOrder(@RequestBody RenewSubscriptionOrderRequest request) {

    var orderPreviewDto =
        renewSubscriptionOrderService.previewRenewSubscription(
            request.getAccountNumber(),
            request.getSubscriptionNumber(),
            request.getOrderDate(),
            request.getCurrentProductRatePlanId(),
            request.getDiscountType(),
            request.getPrice());

    return orderPreviewDto;
  }
  
  @PostMapping("/renew-subscription")
  public String renewSubscriptionOrder(@RequestBody RenewSubscriptionOrderRequest request) {

    var orderPreviewDto =
        renewSubscriptionOrderService.renewSubscription(
            request.getAccountNumber(),
            request.getSubscriptionNumber(),
            request.getOrderDate(),
            request.getCurrentProductRatePlanId(),
            request.getDiscountType(),
            request.getPrice());

    return orderPreviewDto;
  }
  
  @PutMapping("/{orderNumber}/activate")
  public String activate(@PathVariable("orderNumber") String orderNumber) {
    
    orderService.activate(orderNumber);
    return "";
  }

  @DeleteMapping("/{orderNumber}")
  public String delete(@PathVariable("orderNumber") String orderNumber) {

    orderService.forceDelete(orderNumber);
    return "";
  }
}
