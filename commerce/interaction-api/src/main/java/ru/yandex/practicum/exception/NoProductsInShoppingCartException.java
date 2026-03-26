package ru.yandex.practicum.exception;

public class NoProductsInShoppingCartException extends ApiException {
    public NoProductsInShoppingCartException(String message) {
        super("No product in shopping cart", message);
    }
}
