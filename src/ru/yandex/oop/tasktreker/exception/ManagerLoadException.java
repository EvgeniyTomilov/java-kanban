package ru.yandex.oop.tasktreker.exception;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(String message) {
        super(message);
    }

    public ManagerLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
