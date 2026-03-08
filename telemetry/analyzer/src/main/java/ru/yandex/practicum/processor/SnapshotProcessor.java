package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SnapshotProcessor {
    public void start() {
        log.info("SnapshotProcessor started");

    }
}
