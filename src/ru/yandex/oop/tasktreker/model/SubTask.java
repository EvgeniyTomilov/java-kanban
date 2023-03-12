package ru.yandex.oop.tasktreker.model;

import ru.yandex.oop.tasktreker.model.enums.TaskType;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int epicId, Long duration, String startTime) {
        super(name, description, null, null);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
        this.startTime = LocalDateTime.parse(startTime);
        this.duration = duration;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
