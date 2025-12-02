package com.pesewa.inventoryservice.service;

import com.pesewa.inventoryservice.event.InventoryEvent;
import com.pesewa.inventoryservice.model.Inventory;
import com.pesewa.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository,
                            KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Inventory deductStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }
        
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
        
        // Publish event
        publishInventoryEvent(inventory, "STOCK_DEDUCTED", quantity);
        
        return inventory;
    }

    @Transactional
    public Inventory addStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
        
        // Publish event
        publishInventoryEvent(inventory, "STOCK_ADDED", quantity);
        
        return inventory;
    }

    private void publishInventoryEvent(Inventory inventory, String eventType, Integer quantityChange) {
        InventoryEvent event = InventoryEvent.builder()
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .quantity(inventory.getQuantity())
                .eventType(eventType)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        
        kafkaTemplate.send("inventory-events", event);
    }
}
