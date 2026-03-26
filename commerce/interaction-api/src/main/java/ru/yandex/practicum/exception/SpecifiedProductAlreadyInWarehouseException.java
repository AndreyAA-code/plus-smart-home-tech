package ru.yandex.practicum.exception;

public class SpecifiedProductAlreadyInWarehouseException extends ApiException {
    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super("Product already in Warehouse", message);
    }
}
