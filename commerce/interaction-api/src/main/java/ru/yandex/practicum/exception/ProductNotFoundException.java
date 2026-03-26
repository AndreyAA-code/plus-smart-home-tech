package ru.yandex.practicum.exception;

public class ProductNotFoundException extends ApiException {
    public ProductNotFoundException(String message) {
        super("Product not found", message);
    }
}
