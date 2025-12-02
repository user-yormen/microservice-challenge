package com.pesewa.inventoryservice.event;

import lombok.Data;

@Data
public class ProductPayload {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String eventType;
    private Integer quantity;
    private Long timestamp; 
}

enum EventType {
    CREATED,
    UPDATED,
    DELETED,
    STOCK_UPDATED, 
    STOCK_DEDUCTED, 
    STOCK_ADDED
}
