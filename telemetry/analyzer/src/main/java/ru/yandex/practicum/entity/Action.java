package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "actions")
@Getter
@Setter
@ToString(exclude = "scenarioActions")
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ActionTypeProto type;

    @Column(name = "value")
    private Integer value;

    @OneToMany(mappedBy = "action")
    private List<ScenarioAction> scenarioActions = new ArrayList<>();
}
