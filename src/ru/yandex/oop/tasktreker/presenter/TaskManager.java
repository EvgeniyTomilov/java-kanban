package ru.yandex.oop.tasktreker.presenter;

import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TaskManager {

    Map<Integer, Task> getTaskMap();

    Map<Integer, SubTask> getSubTaskMap();

    Map<Integer, EpicTask> getEpicTaskMap();

    HistoryManager getHistoryManager();

    Collection<? extends Task> getTaskByType(TaskType taskType);

    Task getByIdAndTypeTask(int id, TaskType taskType);

    boolean deleteTasksByType(TaskType taskType);

    int createTaskAndReturnId(Task task);

    boolean deleteByIdAndTypeTask(Integer id, TaskType taskType);

    List<SubTask> getListSubtask(int id);

    void changeStatus(int epicId);

    int getNextId();

    Task getTask(int id);

    SubTask getSubtask(int id);

    EpicTask getEpic(int id);

    Task getAnyTask(int id);


}
