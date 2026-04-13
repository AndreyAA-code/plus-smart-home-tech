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

    public String getFullAddress() {
        StringBuilder result = new StringBuilder();
        if (country != null) {
            result.append(country).append(", ");
        }
        if (city != null) {
            result.append(city).append(", ");
        }
        if (street != null) {
            result.append(street).append(", ");
        }
        if (house != null) {
            result.append("д. ").append(house);
            if (flat != null) {
                result.append(", кв. ").append(flat);
            }
        } else if (flat != null) {
            result.append("кв. ").append(flat);
        }
        String fullAddress = result.toString();
        if (fullAddress.endsWith(", ")) {
            fullAddress = fullAddress.substring(0, fullAddress.length() - 2);
        }

        return fullAddress.isEmpty() ? "No address exists" : fullAddress;
    }
}
