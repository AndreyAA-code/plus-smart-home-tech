package ru.yandex.practicum.exception;

public class NoDeliveryFoundException extends ApiException {
    public NoDeliveryFoundException(String message) {
        super("No delivery found",message);
    }
}