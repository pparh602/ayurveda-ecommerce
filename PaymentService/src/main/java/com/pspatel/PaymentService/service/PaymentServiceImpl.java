package com.pspatel.PaymentService.service;

import com.pspatel.PaymentService.entity.TransactionDetails;
import com.pspatel.PaymentService.model.PaymentMode;
import com.pspatel.PaymentService.model.PaymentRequest;
import com.pspatel.PaymentService.model.PaymentResponse;
import com.pspatel.PaymentService.repository.TransactionDetailsRepository;
import java.time.Instant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PaymentServiceImpl implements
    PaymentService {

  @Autowired private TransactionDetailsRepository transactionDetailsRepository;

  @Override
  public long doPayment(PaymentRequest paymentRequest) {
    log.info("Recording Payment Request: {}", paymentRequest);
    TransactionDetails transactionDetails = TransactionDetails.builder()
        .paymentDate(Instant.now())
        .paymentMode(paymentRequest.getPaymentMode().name())
        .paymentStatus("SUCCESS")
        .orderId(paymentRequest.getOrderId())
        .referenceNumber(paymentRequest.getReferenceNumber())
        .amount(paymentRequest.getAmount())
        .build();

    transactionDetailsRepository.save(transactionDetails);

    log.info("Transaction Completed with Id: {}", transactionDetails.getId());
    return transactionDetails.getId();
  }

  @Override
  public PaymentResponse getPaymentDetailsByOrderId(long orderId) {
    log.info("Getting Payment details for the Order Id: {}", orderId);
    TransactionDetails transactionDetails = transactionDetailsRepository.findByOrderId(orderId);

    PaymentResponse paymentResponse = PaymentResponse.builder()
        .paymentId(transactionDetails.getId())
        .paymentDate(transactionDetails.getPaymentDate())
        .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
        .orderId(transactionDetails.getOrderId())
        .status(transactionDetails.getPaymentStatus())
        .amount(transactionDetails.getAmount())
        .build();

    return paymentResponse;
  }
}
