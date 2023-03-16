package ru.yandex.oop.tasktreker.model;

import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EpicTask extends Task {

    private List<SubTask> subTasks;

    public EpicTask(String name, String description) {
        super(name, description, Duration.ofMinutes(10L), LocalDateTime.parse("2007-12-03T10:15:30"));
        this.taskType = TaskType.EPICTASK;
        this.subTasks = new ArrayList<>();
        this.duration = null;
        this.startTime = null;
    }

    public LocalDateTime getEpicStartTime() {
        return subTasks.stream()
                .filter(Objects::nonNull)
                .map(SubTask::getStartTime)
                .min(Comparator.naturalOrder()).get();
    }

    public LocalDateTime getEpicEndTime() {
        return subTasks.stream()
                .filter(Objects::nonNull)
                .map(SubTask::getStartTime)
                .max(Comparator.naturalOrder()).get();
    }

    public Duration getEpicDuration() {
        Duration epicDuration = Duration.ZERO;
        for (SubTask subTask : subTasks) {
            epicDuration.plus(subTask.getDuration());
        }
        return epicDuration;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }
    public void removeSubTaskId(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }
    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }
    public void deleteAllSubtasks() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasks=" + subTasks +
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

