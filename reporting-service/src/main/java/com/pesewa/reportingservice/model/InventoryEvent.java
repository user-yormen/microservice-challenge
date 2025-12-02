package com.pesewa.reportingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "inventory_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEvent {

    @Id
    private String id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private LocalDateTime timestamp;
    private String eventType;
}
