package ru.yandex.practicum.exception;

public class NoPaymentFoundException extends ApiException {
    public NoPaymentFoundException(String message) {
        super("Payment doesn't exist",message);
    }
}
