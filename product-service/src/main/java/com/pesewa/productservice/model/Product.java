package com.pesewa.productservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false)
    private String name;

    private String description;

    @NotNull(message = "Price is mandatory")
    @PositiveOrZero(message = "Price must be positive or zero")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Quantity is mandatory")
    @PositiveOrZero(message = "Quantity must be positive or zero")
    @Column(nullable = false)
    private Integer quantity;
}
