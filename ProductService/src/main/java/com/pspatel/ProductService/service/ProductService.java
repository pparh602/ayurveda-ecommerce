package com.pspatel.ProductService.service;

import com.pspatel.ProductService.model.ProductRequest;
import com.pspatel.ProductService.model.ProductResponse;

public interface ProductService {

  Long addProduct(ProductRequest productRequest);

  ProductResponse getProductById(Long productId);

  void reduceQuatity(Long productId, Long quantity);
}
