package com.pesewa.inventoryservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pesewa.inventoryservice.consumer.InventoryEventConsumer;
import com.pesewa.inventoryservice.event.ProductPayload;
import com.pesewa.inventoryservice.model.Inventory;
import com.pesewa.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InventoryEventConsumerTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private InventoryEventConsumer inventoryEventConsumer;

    @Mock
     private ObjectMapper objectMapper;

    @Test
    void whenConsumeProductCreatedEvent_thenInventoryIsCreated()  throws Exception {
        ProductPayload payload = new ProductPayload();
        payload.setId(1L);
        payload.setName("Test Product");
        payload.setQuantity(100);
        payload.setEventType("PRODUCT_CREATED");
        // Map<String, Object> payload = Map.of(
        //         "id", 1L,
        //         "name", "Test Product",
        //         "quantity", 100,
        //         "eventType", "PRODUCT_CREATED",
        //         "timestamp", System.currentTimeMillis()
        // );

        // when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        // inventoryEventConsumer.consumeProductEvent(payload.toString());

        // ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        String message = "test message";

         when(objectMapper.readValue(message, ProductPayload.class)).thenReturn(payload);
         when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

         inventoryEventConsumer.consumeProductEvent(message);

         ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);

        verify(inventoryRepository).save(inventoryCaptor.capture());
        Inventory savedInventory = inventoryCaptor.getValue();
        assertThat(savedInventory.getProductId()).isEqualTo(1L);
        assertThat(savedInventory.getProductName()).isEqualTo("Test Product");
        assertThat(savedInventory.getQuantity()).isEqualTo(100);
    }

    @Test
    void whenConsumeProductUpdatedEvent_thenInventoryIsUpdated()  throws Exception {
        ProductPayload payload = new ProductPayload();
        payload.setId(1L);
        payload.setName("Updated Product");
        payload.setQuantity(150);
        payload.setEventType("PRODUCT_UPDATED");

        // Map<String, Object> payload = Map.of(
        //         "id", 1L,
        //         "name", "Test Product",
        //         "quantity", 100,
        //         "eventType", "PRODUCT_CREATED",
        //         "timestamp", System.currentTimeMillis()
        // );
        
        String message = "test message";

         when(objectMapper.readValue(message, ProductPayload.class)).thenReturn(payload);

         Inventory existingInventory = Inventory.builder()
                 .productId(1L)
                 .productName("Old Product")
                 .quantity(100)
                 .build();

         when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingInventory));

         inventoryEventConsumer.consumeProductEvent(message);

        assertThat(existingInventory.getProductName()).isEqualTo("Updated Product");
        assertThat(existingInventory.getQuantity()).isEqualTo(150);
        verify(inventoryRepository).save(existingInventory);
    }
}