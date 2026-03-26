package ru.yandex.practicum.exception;

public class ProductInShoppingCartLowQuantityInWarehouse extends ApiException {
    public ProductInShoppingCartLowQuantityInWarehouse(String message) {
        super("Low quantity in warehouse", message);
    }
}
