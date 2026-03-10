package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    
    @Query("SELECT a FROM Action a WHERE a.scenario = :scenario")
    List<Action> findAllByScenario(@Param("scenario") Scenario scenario);
    
    void deleteByScenario(Scenario scenario);
}
