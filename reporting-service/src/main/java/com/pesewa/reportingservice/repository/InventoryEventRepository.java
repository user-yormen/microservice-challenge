package com.pesewa.reportingservice.repository;

import com.pesewa.reportingservice.model.InventoryEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryEventRepository extends MongoRepository<InventoryEvent, String> {
    List<InventoryEvent> findByProductId(Long productId);
}
