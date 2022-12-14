package ru.yandex.oop.tasktreker.presenter.util;

import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryHistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;

import java.util.Map;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static void updateTask(int id, Task task, TaskManager manager) {  // этот метод умеет работать со всеми тасками, его надо заменить на утильный
        task.setId(id);
        manager.getHistoryManager().add(task);

        if (task instanceof SubTask) {
            manager.getSubTaskMap().put(id, (SubTask) task);
            int epicIdOfSubtask = manager.getSubTaskMap().get(id).getEpicId();
            manager.changeStatus(epicIdOfSubtask);
        }
        if (task instanceof EpicTask) {
            for (Map.Entry<Integer, SubTask> subTask : manager.getSubTaskMap().entrySet()) {
                int epicIdOfSubtask = subTask.getValue().getEpicId();
                if (epicIdOfSubtask == id) {
                    ((EpicTask) task).addSubTaskId(epicIdOfSubtask);
                }
            }
            manager.getEpicTaskMap().put(id, (EpicTask) task);
            manager.changeStatus(id);
        } else {
            manager.getTaskMap().put(id, task);
        }
    }
}



