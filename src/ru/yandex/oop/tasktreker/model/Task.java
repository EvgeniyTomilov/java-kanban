package ru.yandex.oop.tasktreker.model;

import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected String name;
    protected int id;
    protected String description;
    protected TaskStatus taskStatus;
    protected TaskType taskType;
    protected  transient Duration duration;
    protected  transient LocalDateTime startTime;

    public Task() {
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, int id, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration.isZero()) {
            return null;
        }
        return startTime.plusMinutes(duration.toMinutes());
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
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

    @Override
    public int compareTo(Task o) {
        if (o == null) {
            return 1;
        }
        if (this.getStartTime() == null && o.getStartTime() == null) {
            return this.getId() - o.getId();
        } else if (this.getStartTime() == null && o.getStartTime() != null) {
            return 1;
        } else if (this.getStartTime() != null && o.getStartTime() == null) {
            return -1;
        } else {
            return this.getStartTime().compareTo(o.getStartTime());
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && taskStatus == task.taskStatus && taskType == task.taskType && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, description, taskStatus, taskType, duration, startTime);
    }
}
