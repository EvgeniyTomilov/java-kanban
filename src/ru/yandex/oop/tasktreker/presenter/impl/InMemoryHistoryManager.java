package ru.yandex.oop.tasktreker.presenter.impl;


import ru.yandex.oop.tasktreker.model.Node;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node> historyTaskMap = new LinkedHashMap<>();

    private Node<Task> first;
    private Node<Task> last;


    public void remove(int id){ // это к 5-му тз
        historyTaskMap.remove(id);
    }

    public void removeNode(Node node) {
        Task task = (Task) node.getTask();
        historyTaskMap.remove(task.getId());
    }

    public void linkLast(Task task) {
        Node node = new Node(task);
        if (historyTaskMap.isEmpty()) {
            first = node;
        }
        historyTaskMap.put(task.getId(), node);
        last = node;
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        for (Map.Entry<Integer, Node> pair : historyTaskMap.entrySet()) {
            list.add((Task) pair.getValue().getTask());
        }
        return list;
    }

    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        for (Map.Entry<Integer, Node> pair : historyTaskMap.entrySet()) {
            list.add((Task) pair.getValue().getTask());
        }
        return list;
    }

    public class Node<Task> {
        private final Task task;

        public Node(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }
    }

}
