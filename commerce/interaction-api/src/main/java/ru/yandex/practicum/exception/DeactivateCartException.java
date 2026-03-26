package ru.yandex.practicum.exception;

public class DeactivateCartException extends ApiException {
    public DeactivateCartException(String message) {
        super("PCart id deactivated",message);
    }
}
