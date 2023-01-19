package ru.yandex.oop.tasktreker.presenter.impl;


import ru.yandex.oop.tasktreker.model.Node;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> historyTaskList;

    public InMemoryHistoryManager() {
        this.historyTaskList = new LinkedList<>();
    }
    public void remove(int id){ // это к 5-му тз

    }



    @Override
    public void add(Task task) {
        if (historyTaskList.size() == 10) {
            historyTaskList.remove(0);
        }
        historyTaskList.add(task);
    }

    @Override
    public List<Task> getHistory() {

        return new ArrayList<>(historyTaskList);
    }

}
