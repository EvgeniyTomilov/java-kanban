package ru.yandex.oop.tasktreker.exception;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }

}
