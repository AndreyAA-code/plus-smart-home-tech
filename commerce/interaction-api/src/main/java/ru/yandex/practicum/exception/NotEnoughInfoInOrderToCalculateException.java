package ru.yandex.practicum.exception;

public class NotEnoughInfoInOrderToCalculateException extends ApiException {
    public NotEnoughInfoInOrderToCalculateException(String message) {
        super("Not enough info to calculate",message);
    }
}
