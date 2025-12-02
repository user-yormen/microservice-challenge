package com.pesewa.productservice.event;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductEvent {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String eventType; // PRODUCT_CREATED, PRODUCT_UPDATED, PRODUCT_DELETED
    private Long timestamp;
}