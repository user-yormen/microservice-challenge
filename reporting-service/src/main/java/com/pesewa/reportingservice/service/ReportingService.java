package com.pesewa.reportingservice.service;

import com.pesewa.reportingservice.model.Inventory;
import com.pesewa.reportingservice.model.InventoryEvent;

import java.util.List;
import java.util.Optional;

public interface ReportingService {
    List<Inventory> getCurrentInventory();
    Optional<Inventory> getInventoryByProductId(Long productId);
    List<InventoryEvent> getInventoryHistory(Long productId);
}
