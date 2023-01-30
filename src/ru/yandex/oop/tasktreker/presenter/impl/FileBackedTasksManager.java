package ru.yandex.oop.tasktreker.presenter.impl;

import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager{

    private static String file = "src/ru/yandex/oop/tasktreker/resource/file.csv";

    public FileBackedTasksManager() {
        super();
    }



    private void save() {
        try(BufferedWriter writer = new BufferedWriter(new ObjectOutputStream(new FileOutputStream(file)))) {
            for (Map.Entry<Integer, Task> pair : getTaskMap().entrySet()) {
                writer.write( toString(pair.getValue()));
            }
            for (Map.Entry<Integer, SubTask> pair : getSubTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()));
            }
            for (Map.Entry<Integer, EpicTask> pair : getEpicTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()));
            }

        }

    }

    public String toString(Task task) {

        if(task instanceof Task) {
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription();
        }
        if(task instanceof SubTask) {
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + ((SubTask) task).getEpicId();
        }
        if (task instanceof EpicTask){
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription();
        }
        return null;
    }

    @Override
    public Collection<? extends Task> getTaskByType(TaskType taskType) {
        return super.getTaskByType(taskType);
    }

    @Override
    public Task getByIdAndTypeTask(int id, TaskType taskType) {
        return super.getByIdAndTypeTask(id, taskType);
    }

    @Override
    public boolean deleteTasksByType(TaskType taskType) {
        return super.deleteTasksByType(taskType);
    }

    @Override
    public boolean deleteByIdAndTypeTask(Integer id, TaskType taskType) {
        return super.deleteByIdAndTypeTask(id, taskType);
    }

    @Override
    public int createTaskAndReturnId(Task task) {
        return super.createTaskAndReturnId(task);
    }

    @Override
    public List<SubTask> getListSubtask(int id) {
        return super.getListSubtask(id);
    }

    @Override
    public void changeStatus(int epicId) {
        super.changeStatus(epicId);
    }

    @Override
    public int getNextId() {
        return super.getNextId();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public Task getAnyTask(int id) {
        return super.getAnyTask(id);
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public SubTask getSubtask(int id) {
        return super.getSubtask(id);
    }

    @Override
    public EpicTask getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public Map<Integer, Task> getTaskMap() {
        return super.getTaskMap();
    }

    @Override
    public Map<Integer, SubTask> getSubTaskMap() {
        return super.getSubTaskMap();
    }

    @Override
    public Map<Integer, EpicTask> getEpicTaskMap() {
        return super.getEpicTaskMap();
    }
}
