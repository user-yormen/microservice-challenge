package com.pesewa.reportingservice.controller;

import com.pesewa.reportingservice.model.Inventory;
import com.pesewa.reportingservice.model.InventoryEvent;
import com.pesewa.reportingservice.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reporting Service", description = "Endpoints for generating inventory reports")
public class ReportingController {

    private final ReportingService reportingService;

    @Autowired
    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/inventory")
    @Operation(summary = "Get current inventory levels for all products")
    public ResponseEntity<List<Inventory>> getCurrentInventory() {
        List<Inventory> inventoryList = reportingService.getCurrentInventory();
        return new ResponseEntity<>(inventoryList, HttpStatus.OK);
    }

    // @GetMapping("/inventory/{id}")
    // @Operation(summary = "Get current inventory for a specific product where ID is the product ID")
    // public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable("productId") Long productId) {
    //     return reportingService.getInventoryByProductId(productId)
    //             .map(inventory -> new ResponseEntity<>(inventory, HttpStatus.OK))
    //             .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    // }
    @GetMapping("/inventory/{productId}")
    @Operation(summary = "Get current inventory for a specific product where ID is the product ID")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable("productId") Long productId) {
        return reportingService.getInventoryByProductId(productId)
                .map(inventory -> new ResponseEntity<>(inventory, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/history/{productId}")
    @Operation(summary = "Get inventory history for a specific product")
    public ResponseEntity<List<InventoryEvent>> getInventoryHistory(@PathVariable("productId") Long productId) {
        List<InventoryEvent> history = reportingService.getInventoryHistory(productId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}
