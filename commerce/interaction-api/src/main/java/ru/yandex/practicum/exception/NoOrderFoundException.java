package ru.yandex.practicum.exception;

public class NoOrderFoundException extends ApiException {
    public NoOrderFoundException(String message) {
        super("No order found", message);
    }
}