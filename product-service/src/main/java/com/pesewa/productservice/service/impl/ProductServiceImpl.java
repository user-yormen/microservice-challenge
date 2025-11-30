package com.pesewa.productservice.service.impl;

import com.pesewa.productservice.model.Product;
import com.pesewa.productservice.repository.ProductRepository;
import com.pesewa.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    // private final KafkaTemplate<String, Product> kafkaTemplate;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, KafkaTemplate<String, Map<String, Object>> kafkaTemplate) {
    // public ProductServiceImpl(ProductRepository productRepository, KafkaTemplate<String, Product> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        kafkaTemplate.send("inventory-events", createPayload(savedProduct));
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
        kafkaTemplate.send("inventory-events", createPayload(updatedProduct));
        return updatedProduct;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        // Note: Sending a delete event might be necessary in a real-world scenario
        // For simplicity, we are not handling delete events in this example
    }

    private Map<String, Object> createPayload(Product product) {
        return Map.of(
            "id", product.getId(),
            "name", product.getName(),
            "description", product.getDescription(),
            "price", product.getPrice(),
            "quantity", product.getQuantity()
        );
    }
}
