package ru.yandex.oop.tasktreker.model;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subTasksId;

    public EpicTask(String name, String description, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        subTasksId = new ArrayList<>();
        this.status = Status.NEW;
    } // здесь что-то не то

    public List<Integer> getSubTaskIds() {
        return subTasksId;
    }

    public void removeSubTaskId(Integer subTaskId) {
        subTasksId.remove(subTaskId);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addSubTaskId(int subTaskId) {
        subTasksId.add(subTaskId);
    }

    public void cLearAllSubTasksId() {
        subTasksId.clear();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasksId=" + subTasksId +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", taskType=" + taskType +
                '}';
    }

}

