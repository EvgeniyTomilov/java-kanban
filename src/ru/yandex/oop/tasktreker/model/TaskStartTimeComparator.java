package ru.yandex.oop.tasktreker.model;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(final Task t1, final Task t2) {
        return t1.getStartTime().compareTo(t2.getStartTime());
//        if (t1 != null && t2 != null) {
//            if (t1.getStartTime() == null && t2.getStartTime() == null) {
//                return t1.getId() - t2.getId();
//            } else if (t1.getStartTime() == null && t2.getStartTime() != null) {
//                return 1;
//            } else if (t1.getStartTime() != null && t2.getStartTime() == null) {
//                return -1;
//            } else {
//                return t1.getStartTime().isBefore(t2.getStartTime()) == true ? -1 : 1;
//            }
//        }
//        return 0;
    }


}

