package ru.yandex.oop.tasktreker.model;


import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

import java.time.LocalDateTime;

public class Task {
    protected String name;
    protected int id;
    protected String description;
    protected TaskStatus taskStatus;
    protected TaskType taskType;
    protected Long duration;
    protected LocalDateTime startTime;

    public Task (){
    }

    public Task(String name, String description, Long duration, String startTime) {
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
        this.duration = duration;
        this.startTime = LocalDateTime.parse(startTime);
    }

    public Task(String name, int id, String description, TaskStatus taskStatus, Long duration, String startTime) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
        this.duration = duration;
        this.startTime = LocalDateTime.parse(startTime);
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == 0) {
            throw new RuntimeException("В задаче отсутствует startTime и/или её продолжительность равно 0");
        }
        return startTime.plusMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime);
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
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

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
