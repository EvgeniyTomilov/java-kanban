package ru.yandex.oop.tasktreker.presenter.impl;

import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;


import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> historyTaskList;

    public InMemoryHistoryManager() {
        this.historyTaskList = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (historyTaskList.size() < 10) {
            historyTaskList.add(task);
        } else {
            historyTaskList.remove(0);
            historyTaskList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyTaskList;
    }
}
