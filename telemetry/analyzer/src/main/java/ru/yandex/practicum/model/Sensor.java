package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sensor {
    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @OneToMany(mappedBy = "sensor")
    private List<Action> actions = new ArrayList<>();
}
