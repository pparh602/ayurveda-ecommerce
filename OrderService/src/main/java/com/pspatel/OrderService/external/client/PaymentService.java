package com.pspatel.OrderService.external.client;

import com.pspatel.OrderService.exception.CustomException;
import com.pspatel.OrderService.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient("PAYMENT-SERVICE/payment")
public interface PaymentService {

  @PostMapping
  public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

  default ResponseEntity<Void> fallback(Exception e){
    throw new CustomException("Payments Service is not available", "UNAVAILABLE",500);
  }

}
