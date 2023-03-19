package ru.yandex.oop.tasktreker.presenter;

import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {

    Map<Integer, Task> getTaskMap();

    Map<Integer, SubTask> getSubTaskMap();

    Map<Integer, EpicTask> getEpicTaskMap();

    HistoryManager getHistoryManager();

    Collection<? extends Task> getTaskByType(TaskType taskType);

    List<Task> getPrioritizedTasks();

    Task getByIdAndTypeTask(int id, TaskType taskType);

    boolean deleteTasksByType(TaskType taskType);

    int createTaskAndReturnId(Task task);

    boolean deleteByIdAndTypeTask(Integer id, TaskType taskType);

    List<SubTask> getListSubtask(int id);

    void changeStatus(int epicId);

    int getNextId();


    Task getAnyTask(int id);

    List<Task> getAllTasks();

//    void isTaskOverlap();
    boolean isTaskOverlap();
    LocalDateTime getTaskEndTime(Task task);
    Duration getEpicDuration(EpicTask epic);

}
