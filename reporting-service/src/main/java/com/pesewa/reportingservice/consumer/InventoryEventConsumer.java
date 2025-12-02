package com.pesewa.reportingservice.consumer;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.pesewa.reportingservice.model.InventoryEvent;
import com.pesewa.reportingservice.repository.InventoryEventRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryEventConsumer {
    
    private final InventoryEventRepository eventRepository;
    
    @Autowired
    public InventoryEventConsumer(InventoryEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @KafkaListener(topics = "inventory-events")
    public void consumeInventoryEvent(Map<String, Object> payload) {
        InventoryEvent event = new InventoryEvent();
        event.setProductId(Long.valueOf(payload.get("productId").toString()));
        event.setProductName(payload.get("productName").toString());
        event.setQuantity(Integer.valueOf(payload.get("quantity").toString()));
        event.setTimestamp(LocalDateTime.now());
        event.setEventType(payload.get("eventType").toString());
        eventRepository.save(event);
        log.info("Saved inventory event: {}", event);
    }
}