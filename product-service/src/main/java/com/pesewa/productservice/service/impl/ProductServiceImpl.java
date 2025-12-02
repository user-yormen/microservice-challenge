package com.pesewa.productservice.service.impl;

import com.pesewa.productservice.event.ProductEvent;
import com.pesewa.productservice.model.Product;
import com.pesewa.productservice.repository.ProductRepository;
import com.pesewa.productservice.service.ProductService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    // private final KafkaTemplate<String, Product> kafkaTemplate;
    // private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private static final String PRODUCT_EVENTS_TOPIC = "product-events";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, 
                              KafkaTemplate<String, ProductEvent> kafkaTemplate) {
    // public ProductServiceImpl(ProductRepository productRepository, KafkaTemplate<String, Map<String, Object>> kafkaTemplate) {
    // public ProductServiceImpl(ProductRepository productRepository, KafkaTemplate<String, Product> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        log.info("About to send the savedProduct : {}", savedProduct);
        
        publishProductEvent(savedProduct, "PRODUCT_CREATED");
        // kafkaTemplate.send("inventory-events", createPayload(toProductEvent(savedProduct, "PRODUCT_CREATED")));
        return savedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());

        Product updatedProduct = productRepository.save(existingProduct);
        // kafkaTemplate.send("inventory-events", createPayload(updatedProduct));
        // kafkaTemplate.send("inventory-events", createPayload(toProductEvent(updatedProduct, "PRODUCT_UPDATED")));
        publishProductEvent(updatedProduct, "PRODUCT_UPDATED");

        return updatedProduct;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            // Get product before deleting to include in event
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
         
            publishProductEvent(product, "PRODUCT_DELETED");
            // kafkaTemplate.send("inventory-events", createPayload(toProductEvent(product, "PRODUCT_DELETED")));

         // Delete from database
            productRepository.deleteById(id);      
        } else {
            throw new RuntimeException("Product not found with id: " + id);
        }

    }

    public Map<String, Object> createPayload(ProductEvent product) {
        return Map.of(
            "id", product.getId(),
            "name", product.getName(),
            "description", product.getDescription(),
            "price", product.getPrice(),
            "quantity", product.getQuantity(),
            "eventType", product.getEventType(),
            "timestamp", product.getTimestamp()
        );
    }

    public ProductEvent toProductEvent(Product product, String eventType) {
        return ProductEvent.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .eventType(eventType)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    // private void publishProductEvent(String publisheString, Map<String, Object> event) {
    //         kafkaTemplate.send(publisheString, event);
    // }
    private void    publishProductEvent(Product product, String eventType) {
        ProductEvent event = ProductEvent.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .eventType(eventType)
                .timestamp(Instant.now().toEpochMilli())
                .build();

            
        log.info("Received Product Event: {}", event);        
        try {
            // kafkaTemplate.send(PRODUCT_EVENTS_TOPIC, event);
            CompletableFuture<SendResult<String, ProductEvent>> future = kafkaTemplate.send(PRODUCT_EVENTS_TOPIC, event);
            SendResult<String, ProductEvent> result = future.get(); // This will block until the send is complete
            log.info("Yormen's event sent successfully: {}", result.getRecordMetadata());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send event", e);
        }

    }
}
