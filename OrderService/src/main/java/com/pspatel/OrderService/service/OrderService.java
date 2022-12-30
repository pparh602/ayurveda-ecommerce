package com.pspatel.OrderService.service;

import com.pspatel.OrderService.model.OrderRequest;
import com.pspatel.OrderService.model.OrderResponse;

public interface OrderService {

  Long placeOrder(OrderRequest orderRequest);

  OrderResponse getOrderDetails(Long orderId);
}
