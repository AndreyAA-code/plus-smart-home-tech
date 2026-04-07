package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Orders;

import java.util.UUID;

public interface OrdersRepository extends JpaRepository<Orders, UUID> {

}
