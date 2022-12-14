package ru.yandex.oop.tasktreker.model;

import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, TaskStatus taskStatus, TaskType taskType, int epicId) {
        super(name, description, taskStatus, taskType);
        this.epicId = epicId;
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
                ", status=" + taskStatus +
                ", taskType=" + taskType +
                '}';
    }

    /*
    Для каждой подзадачи известно, в рамках какого эпика она выполняется.
    Завершение всех подзадач эпика считается завершением эпика.
     */
}
