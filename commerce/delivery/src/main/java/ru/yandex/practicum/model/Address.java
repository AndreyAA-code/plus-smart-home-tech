package ru.yandex.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "address", schema = "delivery")
public class Address {
    @Id

        private String country;
        private String city;
        private String street;
        private String house;
        private String flat;
}
