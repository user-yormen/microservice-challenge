package com.pesewa.productservice;

import com.pesewa.productservice.event.ProductEvent;
import com.pesewa.productservice.model.Product;
import com.pesewa.productservice.repository.ProductRepository;
import com.pesewa.productservice.service.impl.ProductServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @InjectMocks
    private ProductServiceImpl productService;

    // Helper for returning a completed Kafka "send" future
    private CompletableFuture<SendResult<String, ProductEvent>> completedKafkaFuture() {
        return CompletableFuture.completedFuture(mock(SendResult.class));
    }

    @Test
    void whenCreateProduct_thenProductIsSavedAndEventIsSent() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(10.0)
                .description("Test Description")
                .quantity(100)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(10.0)
                .description("Test Description")
                .quantity(100)
                .build();

        when(productRepository.save(product)).thenReturn(savedProduct);

        // Mock Kafka send
        when(kafkaTemplate.send(anyString(), any(ProductEvent.class)))
                .thenReturn(completedKafkaFuture());

        // Act
        Product result = productService.createProduct(product);

        // Assert DB save
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(productRepository).save(product);

        // Assert Kafka event
        ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(kafkaTemplate).send(eq("product-events"), eventCaptor.capture());

        ProductEvent sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getId()).isEqualTo(1L);
        assertThat(sentEvent.getEventType()).isEqualTo("PRODUCT_CREATED");
    }

    @Test
    void whenUpdateProduct_thenProductIsUpdatedAndEventIsSent() {
        // Arrange
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Old Product")
                .price(10.0)
                .description("Old Description")
                .quantity(50)
                .build();

        Product updateDetails = Product.builder()
                .name("Updated Product")
                .price(15.0)
                .description("Updated Description")
                .quantity(100)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .price(15.0)
                .description("Updated Description")
                .quantity(100)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);

        // Mock Kafka send
        when(kafkaTemplate.send(anyString(), any(ProductEvent.class)))
                .thenReturn(completedKafkaFuture());

        // Act
        Product result = productService.updateProduct(1L, updateDetails);

        // Assert DB update
        assertThat(result.getName()).isEqualTo("Updated Product");
        verify(productRepository).save(existingProduct);

        // Assert Kafka event
        ArgumentCaptor<ProductEvent> eventCaptor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(kafkaTemplate).send(eq("product-events"), eventCaptor.capture());

        ProductEvent sentEvent = eventCaptor.getValue();
        assertThat(sentEvent.getId()).isEqualTo(1L);
        assertThat(sentEvent.getEventType()).isEqualTo("PRODUCT_UPDATED");
    }
}
