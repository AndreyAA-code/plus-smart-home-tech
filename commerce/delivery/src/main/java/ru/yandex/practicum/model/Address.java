package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "address", schema = "delivery")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "address_id")
    private UUID addressId;
    @Column (name = "country")
    private String country;
    @Column (name = "city")
    private String city;
    @Column (name = "street")
    private String street;
    @Column (name = "house")
    private String house;
    @Column (name = "flat")
    private String flat;
}
