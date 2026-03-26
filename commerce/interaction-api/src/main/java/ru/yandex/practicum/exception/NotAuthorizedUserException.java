package ru.yandex.practicum.exception;

public class NotAuthorizedUserException extends ApiException {
    public NotAuthorizedUserException(String message)
        {
        super("Not authorized", message);
        }
}
