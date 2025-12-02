package com.pesewa.reportingservice;

import com.pesewa.reportingservice.consumer.InventoryEventConsumer;
import com.pesewa.reportingservice.model.InventoryEvent;
import com.pesewa.reportingservice.repository.InventoryEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InventoryEventConsumerTest {

    @Mock
    private InventoryEventRepository inventoryEventRepository;

    @InjectMocks
    private InventoryEventConsumer inventoryEventConsumer;

    @Test
    void whenConsumeInventoryEvent_thenEventIsSaved() {
        // Given
        Map<String, Object> eventMap = Map.of(
                "productId", 1L,
                "productName", "Test Product",
                "quantity", 100,
                "eventType", "STOCK_UPDATED"
        );

        // When
        inventoryEventConsumer.consumeInventoryEvent(eventMap);

        // Then
        ArgumentCaptor<InventoryEvent> eventCaptor = ArgumentCaptor.forClass(InventoryEvent.class);
        verify(inventoryEventRepository).save(eventCaptor.capture());
        
        InventoryEvent savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getProductId()).isEqualTo(1L);
        assertThat(savedEvent.getProductName()).isEqualTo("Test Product");
        assertThat(savedEvent.getQuantity()).isEqualTo(100);
        assertThat(savedEvent.getEventType()).isEqualTo("STOCK_UPDATED");
        assertThat(savedEvent.getTimestamp()).isNotNull();
    }
}
