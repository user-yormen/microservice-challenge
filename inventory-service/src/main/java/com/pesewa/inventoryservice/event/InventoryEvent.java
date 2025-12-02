package com.pesewa.inventoryservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEvent {
    private Long productId;
    private String productName;
    private Integer quantity;
    private String eventType;
    private Long timestamp;
}
