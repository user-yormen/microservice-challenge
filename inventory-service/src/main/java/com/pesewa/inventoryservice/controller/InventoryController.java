package com.pesewa.inventoryservice.controller;

import com.pesewa.inventoryservice.model.Inventory;
import com.pesewa.inventoryservice.repository.InventoryRepository;
import com.pesewa.inventoryservice.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "APIs for managing inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryController(InventoryService inventoryService, 
                               InventoryRepository inventoryRepository) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    @Operation(summary = "Get all inventory items")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventory = inventoryRepository.findAll();
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory by product ID")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable Long productId) {
        Optional<Inventory> inventory = inventoryRepository.findById(productId);
        return inventory.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{productId}/deduct")
    @Operation(summary = "Deduct stock for a product")
    public ResponseEntity<Inventory> deductStock(@PathVariable Long productId, 
                                                 @RequestParam Integer quantity) {
        try {
            Inventory updated = inventoryService.deductStock(productId, quantity);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{productId}/add")
    @Operation(summary = "Add stock for a product")
    public ResponseEntity<Inventory> addStock(@PathVariable Long productId,
                                              @RequestParam Integer quantity) {
        try {
            Inventory updated = inventoryService.addStock(productId, quantity);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
