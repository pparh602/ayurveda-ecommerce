package com.pspatel.ProductService.controller;

import com.pspatel.ProductService.model.ProductRequest;
import com.pspatel.ProductService.model.ProductResponse;
import com.pspatel.ProductService.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

  @Autowired
  ProductService productService;

  @PreAuthorize("hasAuthority('Admin')")
  @PostMapping
  public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest) {
    Long productId = productService.addProduct(productRequest);
    return new ResponseEntity<>(productId, HttpStatus.CREATED);
  }

  @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer') || hasAuthority"
      + "('SCOPE_internal')")
  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long productId) {
    ProductResponse productResponse
        = productService.getProductById(productId);
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  @PutMapping("/reduceQuantity/{id}")
  public ResponseEntity<Void> reduceQuantity(
      @PathVariable("id") Long productId,
      @RequestParam Long quantity) {
    productService.reduceQuatity(productId, quantity);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
