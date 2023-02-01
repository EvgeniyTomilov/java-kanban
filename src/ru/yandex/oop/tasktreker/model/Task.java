package ru.yandex.oop.tasktreker.model;


import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

public class Task {
    protected String name;
    protected int id;
    protected String description;
    protected TaskStatus taskStatus;
    protected TaskType taskType;

    public Task (){
    }

    public Task(String name, String description,  TaskType taskType) {
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
        this.taskType = taskType;
    }

    public Task(String name, int id, String description, TaskStatus taskStatus, TaskType taskType) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + taskStatus +
                ", taskType=" + taskType +
                '}';
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public TaskStatus getStatus() {
        return taskStatus;
    }
    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
    public TaskType getTaskType() {
        return taskType;
    }
    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }


}
