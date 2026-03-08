package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@ToString(exclude = {"scenarioConditions", "scenarioActions"})
public class Sensor {
    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @OneToMany(mappedBy = "sensor")
    private List<ScenarioCondition> scenarioConditions = new ArrayList<>();

    @OneToMany(mappedBy = "sensor")
    private List<ScenarioAction> scenarioActions = new ArrayList<>();
}
