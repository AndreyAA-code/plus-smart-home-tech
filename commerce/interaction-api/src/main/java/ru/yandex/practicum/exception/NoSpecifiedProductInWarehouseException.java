package ru.yandex.practicum.exception;

public class NoSpecifiedProductInWarehouseException extends ApiException {
    public NoSpecifiedProductInWarehouseException(String message) {
        super("No specified product in warehouse", message);
    }
}
