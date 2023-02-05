package ru.yandex.oop.tasktreker.presenter.impl;


import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> historyTaskMap;
    private List<Task> range = new ArrayList<Task>();
    private Node<Task> first;
    private Node<Task> last;
    private int size;

    public InMemoryHistoryManager() {
        this.historyTaskMap = new LinkedHashMap<>();
        this.first = new Node<>(null, null, null);
        this.last = null;
        this.size = 0;
    }

    @Override
    public void remove(int id) {
        if (historyTaskMap.keySet().contains(id)) {
            removeNode(historyTaskMap.remove(id));
        }
    }

    public void removeNode(Node<Task> node) {
        if (historyTaskMap.keySet().contains(node.task.getId())) {
            Node<Task> prevNode = node.prev;
            Node<Task> nextNode = node.next;
            historyTaskMap.remove(node.task.getId());
            if (prevNode != null) {
                prevNode.next = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            }
            size--;
        }
    }

    public void linkLast(Task task) {
        if (task == null) {
            throw new NullPointerException("Task is null");
        }
        final Node<Task> oldTail = last;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        last = newNode;
        if (oldTail == null)
            first = newNode;
        else
            oldTail.prev = newNode;
        size++;
        if (task != null) {
            historyTaskMap.put(task.getId(), newNode);
        }
    }

    public void add(Task task) {
        if (historyTaskMap.keySet().contains(task.getId())) {
            if (historyTaskMap.get(task.getId()) != null) {
                removeNode(historyTaskMap.get(task.getId()));
            }
        }
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        if (historyTaskMap.isEmpty()) {
            System.out.println("history is empty");
        }
        Node<Task> tmpNode = first;
        while(tmpNode.next != null) {
            range.add(tmpNode.task);
            tmpNode = tmpNode.next;
        }

        return (ArrayList<Task>) range;
    }

    @Override
    public int getSize() {
        return size;
    }

    private static class Node<Task> {
        final Task task;
        Node<Task> prev;
        Node<Task> next;

        public Node(Node<Task> prev, Task item, Node<Task> next) {
            this.task = item;
            this.prev = prev;
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
