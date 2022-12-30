package com.pspatel.ProductService.service;

import static org.springframework.beans.BeanUtils.*;

import com.pspatel.ProductService.entity.Product;
import com.pspatel.ProductService.exception.ProductServiceCustomException;
import com.pspatel.ProductService.model.ProductRequest;
import com.pspatel.ProductService.model.ProductResponse;
import com.pspatel.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {

  @Autowired
  ProductRepository productRepository;

  @Override
  public Long addProduct(ProductRequest productRequest) {
    log.info("Adding Product...");
    Product product = Product.builder().productName(productRequest.getName())
        .price(productRequest.getPrice()).quantity(productRequest.getQuantity()).build();

    productRepository.save(product);

    return product.getProductId();
  }

  @Override
  public ProductResponse getProductById(Long productId) {
    log.info("Getting the Product for productId: {}", productId);
    Product product = productRepository.findById(productId)
        .orElseThrow(
            () -> new ProductServiceCustomException("Product with given id " + productId + " not "
                + "found", "PRODUCT_NOT_FOUND"));

    ProductResponse productResponse = new ProductResponse();
    copyProperties(product, productResponse);
    return productResponse;
  }

  @Override
  public void reduceQuatity(Long productId, Long quantity) {
    log.info("Reduce Quantity {} for Id: {}", quantity, productId);
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductServiceCustomException(
            "Product with given id not found",
            "PRODUCT_NOT_FOUND"
        ));

    if(product.getQuantity()< quantity){
      throw new ProductServiceCustomException(
          "Product does not have sufficient Quantity",
          "INSUFFICIENT_QUANTITY"
      );
    }

    product.setQuantity(product.getQuantity()- quantity);
    productRepository.save(product);
    log.info("Product Quantity updated Successfully");
  }
}
