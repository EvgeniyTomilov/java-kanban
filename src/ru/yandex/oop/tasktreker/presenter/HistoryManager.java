package ru.yandex.oop.tasktreker.presenter;

import ru.yandex.oop.tasktreker.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id); // это к 5-му тз
    List<Task> getHistory();
}

