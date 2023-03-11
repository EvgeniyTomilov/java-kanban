package ru.yandex.oop.tasktreker.model;

import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    public int compare(Task task1, Task task2) {
        return task1.getStartTime().compareTo(task2.getStartTime());
    }
}
