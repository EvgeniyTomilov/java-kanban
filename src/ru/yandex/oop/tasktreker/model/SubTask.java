package ru.yandex.oop.tasktreker.model;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, Status status, TaskType taskType, int epicId) {
        super(name, description, status, taskType);
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
        return "Task{" +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                "epicId=" + epicId +
                '}';
    }
    /*
    Для каждой подзадачи известно, в рамках какого эпика она выполняется.
    Завершение всех подзадач эпика считается завершением эпика.
     */
}
