package ru.yandex.oop.tasktreker.model;

public  class Node<Task>{
    Task item;
    Node<Task> next;
    Node<Task> prev;

    Node(Node<Task> prev, Task element, Node<Task> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
