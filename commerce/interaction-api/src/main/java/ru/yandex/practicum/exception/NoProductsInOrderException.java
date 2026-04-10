package ru.yandex.practicum.exception;

public class NoProductsInOrderException extends ApiException {
    public NoProductsInOrderException(String message) {
        super("No products in Order",message);
    }
}
