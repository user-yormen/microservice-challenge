package com.pesewa.productservice;

import com.pesewa.productservice.model.Product;
import com.pesewa.productservice.repository.ProductRepository;
import com.pesewa.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaTemplate<String, Product> kafkaTemplate;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void whenCreateProduct_thenProductIsSavedAndEventIsSent() {
        Product product = Product.builder().name("Test Product").price(10.0).quantity(100).build();
        when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(product);

        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("Test Product");
        verify(productRepository).save(product);
        verify(kafkaTemplate).send("inventory-events", createdProduct);
    }
}
