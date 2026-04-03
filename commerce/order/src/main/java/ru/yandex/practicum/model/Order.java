package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "order", schema = "order")
public class Order {
    @Id
    @Column(name = "order_id")
    private UUID orderId;

}
