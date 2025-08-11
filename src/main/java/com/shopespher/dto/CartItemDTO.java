package com.shopespher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private String productId;
    private Integer quantity;
    private Double price;
    private Double subTotal;
    private LocalDateTime addedAt;
}
