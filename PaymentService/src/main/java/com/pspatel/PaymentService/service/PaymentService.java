package com.pspatel.PaymentService.service;

import com.pspatel.PaymentService.model.PaymentRequest;
import com.pspatel.PaymentService.model.PaymentResponse;

public interface PaymentService {

  long doPayment(PaymentRequest paymentRequest);

  PaymentResponse getPaymentDetailsByOrderId(long orderId);
}
