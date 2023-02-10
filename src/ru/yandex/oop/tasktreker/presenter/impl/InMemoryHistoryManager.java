package ru.yandex.oop.tasktreker.presenter.impl;


import ru.yandex.oop.tasktreker.model.Node;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> history = new CustomLinkedList<>();
    private final Map<Integer, Node> historyTaskMap = new HashMap<>();


    @Override
    public void add(Task task) {
        if (historyTaskMap.containsKey(task.getId())) {
            remove(task.getId());
            historyTaskMap.put(task.getId(), history.listLast(task));
        } else {
            historyTaskMap.put(task.getId(), history.listLast(task));
        }


    }

    @Override
    public void remove(int id) {
        history.removeNode(historyTaskMap.get(id));
    }

    @Override
    public List<Task> getHistory() {

        return history.getTasks();
    }


}

   class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size = 0;


    public Node<T> listLast(T element) {
        final Node<T> oldTail = tail;
        final Node<T> newNode = new Node<>(tail, element, null);
        tail = newNode;
        if (oldTail != null)
            oldTail.next = newNode;
        else
            head = newNode;
        size++;
        return newNode;
    }

    public List<T> getTasks() {
        List<T> allTasks = new ArrayList<>();
        Node<T> node = tail;
        while (node != null) {
            allTasks.add(node.data);
            node = node.prev;
        }
        return allTasks;
    }

    public void removeNode(Node<T> task) {
        Node<T> prev = task.prev;
        Node<T> next = task.next;


        if (task == head && next != null) {
            next.prev = null;
            head = next;
        } else if (task == tail && prev != null) {
            prev.next = null;
            tail = prev;
        } else if (next != null && prev != null) {
            prev.next = next;
            next.prev = prev;
        } else {
            head = null;
            tail = null;
        }
        size--;
    }
}

