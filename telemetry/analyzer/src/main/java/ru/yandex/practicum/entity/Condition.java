package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@ToString(exclude = "scenarioConditions")
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ConditionTypeProto type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false, length = 50)
    private ConditionOperationProto operation;

    @Column(name = "value", nullable = false)
    private Integer value;

    @OneToMany(mappedBy = "condition")
    private List<ScenarioCondition> scenarioConditions = new ArrayList<>();
}
