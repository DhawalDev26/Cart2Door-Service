package com.shopespher.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product {

    private String id;
    private String tenantId;
    private String name;
    private String slug;
    private String description;
    private String brand;
    private String categoryId;
    private String subCategoryId;
    private BigDecimal price;
    private BigDecimal finalPrice;
    private DiscountInfo discount;
    private Integer stock;
    private String sku;
    private List<String> images;
    private Map<String, String> attributes;
    private List<String> tags;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiscountInfo {
        private String type;
        private Double value;
    }
}
