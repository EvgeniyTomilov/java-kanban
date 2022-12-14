package ru.yandex.oop.tasktreker.model;

import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subTasksId;

    public EpicTask(String name, String description, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        subTasksId = new ArrayList<>();
        this.taskStatus = TaskStatus.NEW;
    } // здесь что-то не то

    public List<Integer> getSubTaskIds() {
        return subTasksId;
    }

    public void removeSubTaskId(Integer subTaskId) {
        subTasksId.remove(subTaskId);
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
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
                ", status=" + taskStatus +
                ", taskType=" + taskType +
                '}';
    }

}

