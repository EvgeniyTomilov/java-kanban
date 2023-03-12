package ru.yandex.oop.tasktreker.model;

import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

import java.time.LocalDateTime;
import java.util.*;

public class EpicTask extends Task {

    private List<SubTask> subTasks;

    public EpicTask(String name, String description) {
        super(name, description, null, null);
        this.taskType = TaskType.EPICTASK;
        this.subTasks = new ArrayList<>();
        this.duration = getDuration();
        this.startTime = getEpicStartTime().get();
    }

    public Optional<LocalDateTime> getEpicStartTime() {
        return subTasks.stream()
                .filter(Objects::nonNull)
                .map(SubTask::getStartTime)
                .min(Comparator.naturalOrder());
    }

    public Optional<LocalDateTime> getEpicEndTime() {
        return subTasks.stream()
                .filter(Objects::nonNull)
                .map(SubTask::getStartTime)
                .max(Comparator.naturalOrder());
    }

    @Override
    public Long getDuration() {
        return subTasks.stream().mapToLong(SubTask::getDuration).sum();
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

