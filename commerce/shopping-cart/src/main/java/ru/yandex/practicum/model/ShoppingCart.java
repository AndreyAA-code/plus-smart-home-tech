package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cart", schema = "shopping_cart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id")
    private UUID cartId;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private Boolean active = true;
    @ElementCollection
    @CollectionTable(name = "cart_products", schema = "shopping_cart", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products = new HashMap<>();
}
