package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "delivery", schema = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    private UUID deliveryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_address_id")
    private Address fromAddress;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_address_id")
    private Address toAddress;
    @Column(name = "delivery_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState;
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
}
