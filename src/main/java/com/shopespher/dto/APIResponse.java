package com.shopespher.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class APIResponse<T> {
    private HttpStatus status;
    private T body;
    private String message;
}
