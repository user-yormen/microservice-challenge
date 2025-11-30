package com.pesewa.inventoryservice.consumer;

import lombok.Data;

@Data
public class ProductPayload {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
