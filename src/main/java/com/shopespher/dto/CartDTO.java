package com.shopespher.dto;

import com.shopespher.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long id;
    private Double totalPrice;
    private Long userId;
    private List<Product> products;
}
