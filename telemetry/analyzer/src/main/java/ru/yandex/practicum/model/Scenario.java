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
@Table(name = "scenarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "hub_id")
    private String hubId;
    
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Action> actions = new ArrayList<>();
}
