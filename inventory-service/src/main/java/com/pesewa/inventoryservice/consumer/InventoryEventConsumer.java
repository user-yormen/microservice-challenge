package com.pesewa.inventoryservice.consumer;

import com.pesewa.inventoryservice.model.Inventory;
import com.pesewa.inventoryservice.model.InventoryEvent;
import com.pesewa.inventoryservice.repository.InventoryEventRepository;
import com.pesewa.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class InventoryEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryEventRepository inventoryEventRepository;

    @Autowired
    public InventoryEventConsumer(InventoryRepository inventoryRepository, InventoryEventRepository inventoryEventRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryEventRepository = inventoryEventRepository;
    }

    @KafkaListener(topics = "inventory-events", groupId = "inventory-group")
    // public void consume(ProductPayload payload) {
    public void consume(Map<String, Object> payload) {
        LOGGER.info("Consumed event: {}", payload);

        ProductPayload productPayload = new ProductPayload();
        productPayload.setId(Long.valueOf(payload.get("id").toString()));
        productPayload.setName(payload.get("name").toString());
        productPayload.setDescription(payload.get("description").toString());
        productPayload.setPrice(Double.valueOf(payload.get("price").toString()));    
        productPayload.setQuantity(Integer.valueOf(payload.get("quantity").toString()));
        // Save event to MongoDB
        InventoryEvent event = InventoryEvent.builder()
                .productId(productPayload.getId())
                .productName(productPayload.getName())
                .quantity(productPayload.getQuantity())
                .timestamp(LocalDateTime.now())
                .build();
        inventoryEventRepository.save(event);

        // Update inventory in PostgreSQL
        inventoryRepository.findById(productPayload.getId()).ifPresentOrElse(
                inventory -> {
                    inventory.setQuantity(productPayload.getQuantity());
                    inventoryRepository.save(inventory);
                },
                () -> {
                    Inventory newInventory = Inventory.builder()
                            .productId(productPayload.getId())
                            .productName(productPayload.getName())
                            .quantity(productPayload.getQuantity())
                            .build();
                    inventoryRepository.save(newInventory);
                }
        );
    }
}
