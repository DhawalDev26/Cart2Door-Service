package com.shopespher.feignservice;

import com.shopespher.dto.APIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8081/user")
public interface UserFeignClient {

    @GetMapping("/getbyemail")
    public APIResponse getUserByEmail(@RequestParam String email);
}
