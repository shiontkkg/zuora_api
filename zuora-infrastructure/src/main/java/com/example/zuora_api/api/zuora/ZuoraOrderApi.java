package com.example.zuora_api.api.zuora;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.example.zuora_api.api.OrderApi;
import com.example.zuora_api.dto.OrderDto;
import com.example.zuora_api.exception.ExternalApiException;
import com.zuora.ApiException;
import com.zuora.ZuoraClient;
import com.zuora.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ZuoraOrderApi implements OrderApi {

  private final ZuoraClient zuoraClient;

  public List<OrderDto> getOrdersBySubscription(String subscriptionNumber) {

    var orders = new ArrayList<OrderDto>();

    try {
      var getOrdersResponse =
          zuoraClient.ordersApi().getOrdersBySubscriptionNumberApi(subscriptionNumber).execute();

      for (var order : getOrdersResponse.getOrders()) {
        orders.add(
            OrderDto.builder()
                .number(order.getOrderNumber())
                .orderDate(order.getOrderDate().toString())
                .description(order.getDescription())
                .status(order.getStatus().getValue())
                .build());
      }
    } catch (ApiException e) {
      log.error(
          """
              オーダーの取得に失敗しました。%s
              """
              .formatted(e.getMessage()));
      throw new ExternalApiException(e.getErrorObject().getReasons().get(0).getMessage(), e.getCode());
    }

    return orders;
  }

  public boolean activate(String orderNumber) {
    try {
      zuoraClient.ordersApi().activateOrderApi(orderNumber).execute();

    } catch (ApiException e) {
      log.error(
          """
              オーダーの有効化に失敗しました。%s
              """
              .formatted(e.getMessage()));
      //      throw new IllegalStateException(e.getMessage());
      throw new ExternalApiException(e.getErrorObject().getReasons().get(0).getMessage(), e.getCode());
    }

    return true;
  }

  public boolean forceDelete(String orderNumber) {

    try {
      var response = zuoraClient.ordersApi().getOrderApi(orderNumber).execute();
      var order = response.getOrder();
      if (OrderStatus.COMPLETED.equals(order.getStatus())) {
        zuoraClient.ordersApi().deleteOrderApi(orderNumber).execute();
      } else if (OrderStatus.DRAFT.equals(order.getStatus())) {
        zuoraClient.ordersApi().deleteOrderApi(orderNumber).execute();
      }

    } catch (ApiException e) {
      log.error(
          """
              オーダーの削除に失敗しました。%s
              """
              .formatted(e.getMessage()));
      //      throw new IllegalStateException(e.getMessage());
      throw new ExternalApiException(e.getErrorObject().getReasons().get(0).getMessage(), e.getCode());
    }

    return true;
  }
}
