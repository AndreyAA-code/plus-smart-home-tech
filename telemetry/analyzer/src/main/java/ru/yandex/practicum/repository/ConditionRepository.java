package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;

import java.util.List;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {
    @Query("SELECT sc.condition FROM ScenarioCondition sc WHERE sc.scenario = :scenario")
    List<Condition> findAllByScenario(@Param("scenario") Scenario scenario);
}
