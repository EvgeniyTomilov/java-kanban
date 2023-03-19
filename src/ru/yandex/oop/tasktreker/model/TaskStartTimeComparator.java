package ru.yandex.oop.tasktreker.model;

import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(final Task task1, final Task task2) {
        return task1.getStartTime().compareTo(task2.getStartTime());
    }
}