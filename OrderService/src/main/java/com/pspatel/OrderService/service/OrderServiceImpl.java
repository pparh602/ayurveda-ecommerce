package com.pspatel.OrderService.service;

import com.pspatel.OrderService.entity.Order;
import com.pspatel.OrderService.exception.CustomException;
import com.pspatel.OrderService.external.client.PaymentService;
import com.pspatel.OrderService.external.client.ProductService;
import com.pspatel.OrderService.external.request.PaymentRequest;
import com.pspatel.OrderService.external.response.PaymentResponse;
import com.pspatel.OrderService.external.response.ProductResponse;
import com.pspatel.OrderService.model.OrderRequest;
import com.pspatel.OrderService.model.OrderResponse;
import com.pspatel.OrderService.repository.OrderRepository;
import java.time.Instant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
  @Autowired private OrderRepository orderRepository;

  @Autowired private ProductService productService;

  @Autowired private PaymentService paymentService;

  @Autowired private RestTemplate restTemplate;

  @Override
  public Long placeOrder(OrderRequest orderRequest) {
    // Order Entity -> Save the data with Status Order Created
    // Product Service - Block Products (Reduce the Quantity)
    // Payment Service -> Payment -> Success -> Complete, Else
    // Cancelled

    log.info("Placing Order Request: {}", orderRequest);

    productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

    log.info("Creating Order with Status CREATED");
    Order order = Order.builder()
        .amount(orderRequest.getTotalAmount())
        .orderStatus("CREATED")
        .productId(orderRequest.getProductId())
        .orderDate(Instant.now())
        .quantity(orderRequest.getQuantity())
        .build();

    order = orderRepository.save(order);

    log.info("Calling Payment Service to complete the payment");
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .orderId(order.getId())
        .paymentMode(orderRequest.getPaymentMode())
        .amount(orderRequest.getTotalAmount())
        .build();

    String orderStatus = null;
    try{
      paymentService.doPayment(paymentRequest);
      log.info("Payment done Successfully. Changing the Order status to PLACED");
      orderStatus = "PLACED";
    }catch (Exception e){
      log.error("Error occurred in payment. Changing order status to PAYMENT_FAILED");
      orderStatus = "PAYMENT_FAILED";
    }

    order.setOrderStatus(orderStatus);
    orderRepository.save(order);

    log.info("Order Placed successfully with Order Id: {}", order.getId());
    return order.getId();
  }

  @Override
  public OrderResponse getOrderDetails(Long orderId) {
    log.info("Get order details for order Id: {}", orderId);
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new CustomException("Order not found for Order Id: " + orderId,
            "NOT_FOUND",
            404));
    log.info("Invoking Product Service to fetch the product for id: {}", order.getId());

    ProductResponse productResponse
        = restTemplate.getForObject(
            "http://PRODUCT-SERVICE/product/" + order.getProductId(),
        ProductResponse.class
    );

    log.info("Getting Payment information from Payment Service");

    PaymentResponse paymentResponse = restTemplate.getForObject(
        "http://PAYMENT-SERVICE/payment/order/" + orderId,
        PaymentResponse.class
    );

     OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
         .paymentId(paymentResponse.getPaymentId())
         .paymentStatus(paymentResponse.getStatus())
         .paymentDate(paymentResponse.getPaymentDate())
         .paymentMode(paymentResponse.getPaymentMode())
        .build();
    OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
        .productName(productResponse.getProductName())
        .productId(productResponse.getProductId())
        .quantity(productResponse.getQuantity())
        .price(productResponse.getPrice())
        .build();
    OrderResponse orderResponse = OrderResponse.builder()
        .orderId(order.getId())
        .amount(order.getAmount())
        .orderDate(order.getOrderDate())
        .orderStatus(order.getOrderStatus())
        .productDetails(productDetails)
        .paymentDetails(paymentDetails)
        .build();

    return orderResponse;
  }
}
