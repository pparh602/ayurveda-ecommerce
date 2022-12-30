package com.pspatel.OrderService.controller;

import com.pspatel.OrderService.model.OrderRequest;
import com.pspatel.OrderService.model.OrderResponse;
import com.pspatel.OrderService.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

  @Autowired private OrderService orderService;

  @PostMapping("/placeOrder")
  public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest){
    Long orderId = orderService.placeOrder(orderRequest);
    log.info("Order Id: {}", orderId);
    return new ResponseEntity<>(orderId, HttpStatus.OK);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long orderId){
    OrderResponse orderResponse = orderService.getOrderDetails(orderId);
    return new ResponseEntity<>(orderResponse, HttpStatus.OK);
  }


}
