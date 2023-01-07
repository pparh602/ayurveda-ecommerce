package com.pspatel.OrderService.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pspatel.OrderService.entity.Order;
import com.pspatel.OrderService.exception.CustomException;
import com.pspatel.OrderService.external.client.PaymentService;
import com.pspatel.OrderService.external.client.ProductService;
import com.pspatel.OrderService.external.request.PaymentRequest;
import com.pspatel.OrderService.external.response.PaymentResponse;
import com.pspatel.OrderService.external.response.ProductResponse;
import com.pspatel.OrderService.model.OrderRequest;
import com.pspatel.OrderService.model.OrderResponse;
import com.pspatel.OrderService.model.PaymentMode;
import com.pspatel.OrderService.repository.OrderRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock private ProductService productService;

  @Mock private PaymentService paymentService;

  @Mock private RestTemplate restTemplate;

  @InjectMocks private OrderService orderService = new OrderServiceImpl();

  @DisplayName("Ger Order - Success Scenario")
  @Test
  void test_When_Order_Success(){
    // Mocking
    Order order = getMockOrder();
    when(orderRepository.findById(anyLong()))
        .thenReturn(Optional.of(order));

    when(restTemplate.getForObject(
        "http://PRODUCT-SERVICE/product/" + order.getProductId(),
        ProductResponse.class
    )).thenReturn(getMockProductResponse());

    when(restTemplate.getForObject( "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
        PaymentResponse.class)).thenReturn(getMockPaymentResponse());

    // Actual
    OrderResponse orderResponse = orderService.getOrderDetails(1L);

    // Verification
    verify(orderRepository,times(1)).findById(anyLong());
    verify(restTemplate,times(1)).getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),
        ProductResponse.class);
    verify(restTemplate,times(1)).getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
        PaymentResponse.class);

    // Assert
    assertNotNull(orderResponse);
    assertEquals(order.getId(), orderResponse.getOrderId());
  }

  @DisplayName("Ger Order - Failure Scenario")
  @Test
  void test_When_Get_Order_NOT_FOUND_then_Not_FOUND(){
    when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

    CustomException exception = assertThrows(CustomException.class,
        () -> orderService.getOrderDetails(1L));

    assertEquals("NOT_FOUND",exception.getErrorCode());
    assertEquals(404, exception.getStatus());

    verify(orderRepository,times(1)).findById(anyLong());
  }

  @DisplayName("Place Order - Success Scenario")
  @Test
  void test_When_Place_Order_Success(){
    Order order = getMockOrder();
    OrderRequest orderRequest = getMockOrderRequest();

    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(productService.reduceQuantity(anyLong(), anyLong()))
        .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
    when(paymentService.doPayment(any(PaymentRequest.class)))
        .thenReturn(new ResponseEntity<Long>(1L, HttpStatus.OK));

    Long orderId = orderService.placeOrder(orderRequest);

    verify(orderRepository,times(2)).save(any());
    verify(productService,times(1)).reduceQuantity(anyLong(),anyLong());
    verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));

    assertEquals(order.getId(),orderId);
  }

  @DisplayName("Place Order - Failure Scenario")
  @Test
  void test_When_Place_Order_Payment_Fails_then_Order_Placed(){

    Order order = getMockOrder();
    OrderRequest orderRequest = getMockOrderRequest();

    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(productService.reduceQuantity(anyLong(), anyLong()))
        .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
    when(paymentService.doPayment(any(PaymentRequest.class)))
        .thenThrow(new RuntimeException());

    Long orderId = orderService.placeOrder(orderRequest);


    verify(orderRepository,times(2)).save(any());
    verify(productService,times(1)).reduceQuantity(anyLong(),anyLong());
    verify(paymentService,times(1)).doPayment(any(PaymentRequest.class));

    assertEquals(order.getId(),orderId);

  }

  private PaymentResponse getMockPaymentResponse() {
    return PaymentResponse.builder()
        .paymentId(1)
        .paymentDate(Instant.now())
        .paymentMode(PaymentMode.CASH)
        .orderId(1)
        .amount(200)
        .status("ACCEPTED")
        .build();
  }

  private ProductResponse getMockProductResponse() {
    return ProductResponse.builder()
        .productId(2)
        .productName("iPhone")
        .quantity(200)
        .price(100)
        .build();
  }

  private OrderRequest getMockOrderRequest(){

    return OrderRequest.builder()
        .productId(1)
        .quantity(10)
        .paymentMode(PaymentMode.CASH)
        .totalAmount(100)
        .build();
  }

  private Order getMockOrder() {
    return Order.builder()
        .orderStatus("PLACED")
        .orderDate(Instant.now())
        .id(1)
        .amount(100)
        .productId(2)
        .build();
  }
}