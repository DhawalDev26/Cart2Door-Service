package com.shopespher.feignservice;

import com.shopespher.dto.APIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "http://localhost:8082/products")
public interface ProductFeignClient {

    @GetMapping("/getProductById")
    public APIResponse getProductById(@RequestParam String id);
}
