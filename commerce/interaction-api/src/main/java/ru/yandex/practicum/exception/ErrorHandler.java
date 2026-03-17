package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ProductNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "Product not found", e.getMessage());
    }

    @ExceptionHandler({NotAuthorizedUserException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotAuthorizedUserException(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "User is not authorized", e.getMessage());
    }

    @ExceptionHandler({NoProductsInShoppingCartException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoProductsInShoppingCartExceptionn(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "Product not found", e.getMessage());
    }

    @ExceptionHandler({SpecifiedProductAlreadyInWarehouseException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleSpecifiedProductAlreadyInWarehouseException(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "Product already in warehouse", e.getMessage());
    }

    @ExceptionHandler({ProductInShoppingCartLowQuantityInWarehouse.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleProductInShoppingCartLowQuantityInWarehouse(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "Product is low in quantity in warehouse", e.getMessage());
    }

    @ExceptionHandler({NoSpecifiedProductInWarehouseException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoSpecifiedProductInWarehouseException(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "No such Product in warehouse", e.getMessage());
    }

    @ExceptionHandler({DeactivateCartException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleDeactivateCartException(RuntimeException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "Cart is deactivated", e.getMessage());
    }

}