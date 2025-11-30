package com.pesewa.inventoryservice;

import com.pesewa.inventoryservice.consumer.InventoryEventConsumer;
import com.pesewa.inventoryservice.consumer.ProductPayload;
import com.pesewa.inventoryservice.model.Inventory;
import com.pesewa.inventoryservice.model.InventoryEvent;
import com.pesewa.inventoryservice.repository.InventoryEventRepository;
import com.pesewa.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InventoryEventConsumerTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryEventRepository inventoryEventRepository;

    @InjectMocks
    private InventoryEventConsumer inventoryEventConsumer;

    @Test
    void whenConsumeEvent_thenInventoryIsUpdatedAndEventIsSaved() {
        ProductPayload payload = new ProductPayload();
        payload.setId(1L);
        payload.setName("Test Product");
        payload.setQuantity(100);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        inventoryEventConsumer.consume(payload);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(inventoryCaptor.capture());
        Inventory savedInventory = inventoryCaptor.getValue();
        assertThat(savedInventory.getProductId()).isEqualTo(1L);
        assertThat(savedInventory.getProductName()).isEqualTo("Test Product");
        assertThat(savedInventory.getQuantity()).isEqualTo(100);

        ArgumentCaptor<InventoryEvent> eventCaptor = ArgumentCaptor.forClass(InventoryEvent.class);
        verify(inventoryEventRepository).save(eventCaptor.capture());
        InventoryEvent savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getProductId()).isEqualTo(1L);
        assertThat(savedEvent.getProductName()).isEqualTo("Test Product");
        assertThat(savedEvent.getQuantity()).isEqualTo(100);
    }
}
