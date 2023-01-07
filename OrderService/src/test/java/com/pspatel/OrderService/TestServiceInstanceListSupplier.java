package com.pspatel.OrderService;

import java.util.ArrayList;
import java.util.List;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {

  @Override
  public String getServiceId() {
    return null;
  }

  @Override
  public Flux<List<ServiceInstance>> get() {
    List<ServiceInstance> results = new ArrayList<>();
    results.add(new DefaultServiceInstance(
        "PAYMENT_SERVICE",
        "PAYMENT_SERVICE",
        "localhost",
        8080,
        false));
    results.add(new DefaultServiceInstance(
        "PRODUCT_SERVICE",
        "PRODUCT_SERVICE",
        "localhost",
        8080,
        false));
    return Flux.just(results);
  }
}
