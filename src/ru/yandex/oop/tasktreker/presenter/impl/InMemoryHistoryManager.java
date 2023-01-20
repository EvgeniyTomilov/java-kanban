package ru.yandex.oop.tasktreker.presenter.impl;


import ru.yandex.oop.tasktreker.model.Node;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> historyTaskMap = new LinkedHashMap<>();

    private Node<Task> first;
    private Node<Task> last;

    public int size = historyTaskMap.size();


    public void remove(int id){ // это к 5-му тз
        historyTaskMap.remove(id);
    }

    public void removeNode(Node<Task> node) {
        if (node.equals(first)) {
            first = node.getNext();
        }
        if (node.equals(last)) {
            last = node.getPrev();
        }
        int id = node.getTask().getId();
        historyTaskMap.get(id).getPrev().setNext(node.getNext());
        historyTaskMap.get(id).getNext().setPrev(node.getPrev());
        historyTaskMap.remove(node.getTask().getId());
    }

    public void linkLast(Task task) {
        Node node = new Node(last, task, null);
        if (historyTaskMap.isEmpty()) {
            first = node;
        }
        historyTaskMap.get(last.getTask().getId()).setNext(node);
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

    private static class Node<Task> {

        private final Task task;
        private Node<Task> prev;
        private Node<Task> next;

        public Node(Node<Task> prev, Task item, Node<Task> next) {
            this.task = item;
            this.prev = prev;
            this.next = next;
        }

        public Task getTask() {
            return task;
        }

        public Node<Task> getPrev() {
            return prev;
        }

        public void setPrev(Node<Task> prev) {
            this.prev = prev;
        }

        public Node<Task> getNext() {
            return next;
        }

        public void setNext(Node<Task> next) {
            this.next = next;
        }
    }
}