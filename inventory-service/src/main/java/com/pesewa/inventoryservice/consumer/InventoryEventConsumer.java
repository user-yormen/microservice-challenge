package com.pesewa.inventoryservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pesewa.inventoryservice.event.InventoryEvent;
import com.pesewa.inventoryservice.event.ProductPayload;
import com.pesewa.inventoryservice.model.Inventory;
import com.pesewa.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class InventoryEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final InventoryRepository inventoryRepository;
     private final KafkaTemplate<String, Object> kafkaTemplate;
     private final ObjectMapper objectMapper;

     @Autowired
     public InventoryEventConsumer(InventoryRepository inventoryRepository, 
                                   KafkaTemplate<String, Object> kafkaTemplate,
                                   ObjectMapper objectMapper) {
         this.inventoryRepository = inventoryRepository;
         this.kafkaTemplate = kafkaTemplate;
         this.objectMapper = objectMapper;
     }

    @KafkaListener(topics = "product-events", groupId = "inventory-service-group")
    @Transactional
    public void consumeProductEvent(String payload) throws JsonMappingException, JsonProcessingException {
        LOGGER.info("Received raw message: {}", payload);
            
            // Manually deserialize JSON string
            ProductPayload productEvent = objectMapper.readValue(payload, ProductPayload.class);
            LOGGER.info("Deserialized Product Event: {}", productEvent);
        
        // Handle based on event type
        switch (productEvent.getEventType()) {
            case "PRODUCT_CREATED":
                updateOrCreateInventory(productEvent);
                break;
            case "PRODUCT_UPDATED":
                updateOrCreateInventory(productEvent);
                break;
            case "PRODUCT_DELETED":
                deleteInventory(productEvent.getId());
                break;
            default:
                LOGGER.warn("Unknown event type: {}", productEvent.getEventType());
        }
    }

    private void updateOrCreateInventory(ProductPayload productEvent) {
        inventoryRepository.findById(productEvent.getId()).ifPresentOrElse(
            existingInventory -> {
                // Update existing inventory
                existingInventory.setProductName(productEvent.getName());
                existingInventory.setQuantity(productEvent.getQuantity());
                inventoryRepository.save(existingInventory);
                LOGGER.info("Updated inventory for product: {}", productEvent.getId());
                
                // Publish inventory event
                publishInventoryEvent(productEvent, "STOCK_UPDATED");
            },
            () -> {
                // Create new inventory
                Inventory newInventory = Inventory.builder()
                        .productId(productEvent.getId())
                        .productName(productEvent.getName())
                        .quantity(productEvent.getQuantity())
                        .build();
                inventoryRepository.save(newInventory);
                LOGGER.info("Created new inventory for product: {}", productEvent.getId());
                
                // Publish inventory event
                publishInventoryEvent(productEvent, "STOCK_INITIALIZED");
            }
        );
    }

    private void deleteInventory(Long productId) {
        if (inventoryRepository.existsById(productId)) {
            inventoryRepository.deleteById(productId);
            LOGGER.info("Deleted inventory for product: {}", productId);
            
            // Publish deletion event
            InventoryEvent deletionEvent = InventoryEvent.builder()
                    .productId(productId)
                    .eventType("STOCK_DELETED")
                    .timestamp(Instant.now().toEpochMilli())
                    .build();
            // kafkaTemplate.send("inventory-events", deletionEvent);
            Map<String, Object> deleteMap = inventoryEventToMap(deletionEvent);
        kafkaTemplate.send("inventory-events", deleteMap);
        } else {
            LOGGER.warn("Attempted to delete non-existent inventory for product: {}", productId);
        }
    }

    private void publishInventoryEvent(ProductPayload productEvent, String eventType) {
        InventoryEvent inventoryEvent = InventoryEvent.builder()
                .productId(productEvent.getId())
                .productName(productEvent.getName())
                .quantity(productEvent.getQuantity())
                .eventType(eventType)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        
        // kafkaTemplate.send("inventory-events", inventoryEvent);
        Map<String, Object> mapPayload = inventoryEventToMap(inventoryEvent);
        kafkaTemplate.send("inventory-events", mapPayload);
        LOGGER.info("Published inventory event: {}", inventoryEvent);
    }

    public Map<String, Object> inventoryEventToMap(InventoryEvent inventoryEvent) {
        return Map.of(
            "productId", inventoryEvent.getProductId(),
            "productName", inventoryEvent.getProductName(),
            "quantity", inventoryEvent.getQuantity(),
            "eventType", inventoryEvent.getEventType(),
            "timestamp", inventoryEvent.getTimestamp()
        );
    }
}