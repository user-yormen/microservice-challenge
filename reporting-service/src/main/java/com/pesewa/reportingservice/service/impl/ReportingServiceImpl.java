package com.pesewa.reportingservice.service.impl;

import com.pesewa.reportingservice.model.Inventory;
import com.pesewa.reportingservice.model.InventoryEvent;
import com.pesewa.reportingservice.repository.InventoryEventRepository;
import com.pesewa.reportingservice.repository.InventoryRepository;
import com.pesewa.reportingservice.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportingServiceImpl implements ReportingService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventRepository inventoryEventRepository;

    @Autowired
    public ReportingServiceImpl(InventoryRepository inventoryRepository, InventoryEventRepository inventoryEventRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryEventRepository = inventoryEventRepository;
    }

    @Override
    public List<Inventory> getCurrentInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Optional<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findById(productId);
    }

    @Override
    public List<InventoryEvent> getInventoryHistory(Long productId) {
        return inventoryEventRepository.findByProductId(productId);
    }
}
