package ru.yandex.oop.tasktreker.presenter.impl;


import ru.yandex.oop.tasktreker.model.Node;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> historyTaskMap;

    private Node<Task> first;
    private Node<Task> last;

    private int size;

    public InMemoryHistoryManager() {
        this.historyTaskMap = new LinkedHashMap<>();
        this.first = new Node<>(null, null, null);
        this.last = null;
        this.size = 0;
    }

    public void remove(int id){ // это к 5-му тз
        removeNode(historyTaskMap.get(id));
        historyTaskMap.remove(id);
        size--;
    }

    public void removeNode(Node<Task> delete) {
        final Node<Task> next = delete.next;
        final Node<Task> prev = delete.prev;
        if (prev == null) {
            first = next;
        } else  {
            prev.next = next;
            delete.prev = null;
        }
        if (next == null) {
            last = prev;
        } else  {
            next.prev = prev;
            delete.next = null;
        }
        size--;
    }

    public void linkLast(Task task) {
        if (task == null) {
            throw new NullPointerException("Task is null");
        }
        Node<Task> oldLast = last;
        Node<Task> newNode = new Node<>(oldLast, task, null);
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.setNext(newNode);
        }
        historyTaskMap.put(task.getId(), newNode);
        size++;
    }

    @Override
    public void add(Task task) {
        if (historyTaskMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
        size++;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        if (historyTaskMap.isEmpty()) {
            System.out.println("history is empty");
        }
        Node<Task> temp = first;
        while (temp.getNext() != null) {
            list.add(temp.getTask());
            temp = temp.getNext();
        }
        return list;
    }

//    public List<Task> getTasks() {
//        List<Task> list = new ArrayList<>();
//        for (Map.Entry<Integer, Node> pair : historyTaskMap.entrySet()) {
//            list.add((Task) pair.getValue().getTask());
//        }
//        return list;
//    }


    public int getSize() {
        return size;
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

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task +
                    ", prev=" + prev +
                    ", next=" + next +
                    '}';
        }
    }
}
