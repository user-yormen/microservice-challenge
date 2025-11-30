package com.pesewa.reportingservice;

import com.pesewa.reportingservice.model.Inventory;
import com.pesewa.reportingservice.repository.InventoryRepository;
import com.pesewa.reportingservice.service.impl.ReportingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ReportingServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ReportingServiceImpl reportingService;

    @Test
    void whenGetCurrentInventory_thenReturnsListOfInventory() {
        Inventory inventory = Inventory.builder().productId(1L).productName("Test Product").quantity(100).build();
        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

        List<Inventory> inventoryList = reportingService.getCurrentInventory();

        assertThat(inventoryList).isNotNull();
        assertThat(inventoryList.size()).isEqualTo(1);
        assertThat(inventoryList.get(0).getProductName()).isEqualTo("Test Product");
    }

    @Test
    void whenGetInventoryByProductId_thenReturnsInventory() {
        Inventory inventory = Inventory.builder().productId(1L).productName("Test Product").quantity(100).build();
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        Optional<Inventory> result = reportingService.getInventoryByProductId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getProductName()).isEqualTo("Test Product");
    }
}
